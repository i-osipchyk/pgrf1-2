package control;

import clip.PolygonClipper;
import fill.ScanLine;
import fill.SeedFill;
import fill.SeedFillBorder;
import model.Point;
import model.Polygon;
import model.Rectangle;
import rasterize.*;
import view.Panel;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller2D implements Controller {

    private final Panel panel;

    // models
    public Polygon polygon;
    public Rectangle rectangle;
    public Polygon clippingPolygon;
    public PolygonClipper clipper;
    private Polygon currentPolygon;
    Point startingPoint;
    Point movingPoint;
    private ArrayList<Polygon> polygons = new ArrayList<>();

    // rasterizers
    public LineRasterizer lineRasterizer;
    public PolygonRasterizer polygonRasterizer;
    public RectangleRasterizer rectangleRasterizer;
    public EllipseRasterizer ellipseRasterizer;
    public PolygonRasterizer clippingPolygonRasterizer;
    private LineRasterizerGraphics rasterizer;

    // modes
    private int rasterizationMode = 1;
    private int fillingMode = 0;
    private int clippingMode = 0;

    // dynamic variables
    private boolean wasDragged = false;
    private int polygonToRasterize = -1;
    private StringBuilder numberInput = new StringBuilder();
    int nearestPointIndex = -1;

    // colors
    private final ColorGenerator colorGenerator = new ColorGenerator();
    public Color finalLineColor = Color.GREEN;
    private final Color movingLineColor = Color.RED;
    private final Color clippingFinalLineColor = Color.BLUE;
    private final Color clippingMovingLineColor = Color.CYAN;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners();
        lineRasterizer = new LineRasterizerGraphics(this.panel.getRaster());
        cleanAll();
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
     }

    @Override
    public void initListeners() {
        this.panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // add points to draw polygon dynamically
                startingPoint = new Point(e.getX(), e.getY());
                movingPoint = new Point(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                wasDragged = false;

                // if user draws polygon
                if (rasterizationMode == 1) {
                    // if user draws ordinary polygon
                    if (clippingMode == 0) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (polygon.getPoints().isEmpty()) {
                                // add first point to polygon and initialize moving point
                                polygon.addPoint(new Point(e.getX(), e.getY()));
                                movingPoint = new Point(e.getX(), e.getY());
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3 && fillingMode == 0) {
                            // find nearest point
                            int nearest = polygon.findNearestPointIndex(new Point(e.getX(), e.getY()), 25);
                            if(nearest >= 0) {
                                // if exists, reset it to clicked point
                                nearestPointIndex = nearest;
                                polygon.setPoint(nearest, new Point(e.getX(), e.getY()));
                            } else {
                                nearestPointIndex = -1;
                                System.out.println("Could not find any near point");
                            }
                        }
                    }
                    // if user draws clipping polygon, repeats the same steps for it
                    else {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (clippingPolygon.getPoints().isEmpty()) {
                                clippingPolygon.addPoint(new Point(e.getX(), e.getY()));
                            }
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            int nearest = clippingPolygon.findNearestPointIndex(new Point(e.getX(), e.getY()), 30);
                            if(nearest >= 0) {
                                nearestPointIndex = nearest;
                                clippingPolygon.setPoint(nearest, new Point(e.getX(), e.getY()));
                            } else {
                                nearestPointIndex = -1;
                            }
                        }
                    }

                    // fill the polygon if the filling mode is chosen
                    if (fillingMode == 1) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            ScanLine scanLine = new ScanLine(lineRasterizer, polygonRasterizer, polygon);
                            scanLine.fill();
                        }
                    } else if (fillingMode == 2) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            SeedFill seedFill = new SeedFill(panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()), e.getX(), e.getY(), panel.getWidth(), panel.getHeight());
                            seedFill.fill();
                        }
                    } else if (fillingMode == 3) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            SeedFillBorder seedFillBorder = new SeedFillBorder(panel.getRaster(), polygon.color, e.getX(), e.getY(), panel.getWidth(), panel.getHeight());
                            seedFillBorder.fill();
                        }
                    }
                    // reset filling mode
                    fillingMode = 0;
                }
                // if user draws rectangle
                else if (rasterizationMode == 2) {
                    cleanAll();
                    // add first point to rectangle and initialize moving point
                    if (rectangle.getPoints().isEmpty()) {
                        startingPoint = new Point(e.getX(), e.getY());
                        movingPoint = new Point(e.getX(), e.getY());
                    }
                }
                // if user draws a list of polygons
                else if (rasterizationMode == 3) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        // add first point to current polygon and initialize moving point
                        if (currentPolygon.getPoints().isEmpty()) {
                            currentPolygon.addPoint(new Point(e.getX(), e.getY()));
                            movingPoint = new Point(e.getX(), e.getY());
                        }
                    }
                    // if filling mode is chosen
                    if (fillingMode == 1) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            // rasterize chosen polygon if number is valid
                            if (polygonToRasterize > 0 && polygonToRasterize <= polygons.size()) {
                                ScanLine scanLine = new ScanLine(lineRasterizer, polygonRasterizer, polygons.get(polygonToRasterize-1));
                                scanLine.fill();
                            } else {
                                System.out.println("There is no such polygon drawn");
                            }
                            // reset variables
                            polygonToRasterize = -1;
                            numberInput.setLength(0);
                            fillingMode = 0;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // adjust coordinates to be within canvas boundaries
                x = Math.max(0, Math.min(x, panel.getWidth()));
                y = Math.max(0, Math.min(y, panel.getHeight()));

                // if user draws polygon
                if (rasterizationMode == 1) {
                    // if normal mode is chosen
                    if (clippingMode == 0) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            // add point if it was not added yet
                            if (wasDragged || polygon.getPoints().size() != 1) {
                                polygon.addPoint(new Point(x, y));
                            }
                            // reset moving point and draw
                            movingPoint = null;
                            update();
                        }
                    }
                    // if clipping mode is chosen
                    else if (clippingMode == 1) {
                        // do the same but for clipping polygon
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (wasDragged || clippingPolygon.getPoints().size() != 1) {
                                clippingPolygon.addPoint(new Point(x, y));
                            }
                            movingPoint = null;
                            update();
                        }
                    }
                }
                // if user draws rectangle
                else if (rasterizationMode == 2) {
                    // add all points to rectangle
                    rectangle.addAllPoints(startingPoint, new Point(x, y));
                    // draw if mouse was dragged
                    if (wasDragged)
                        update();
                    // reset moving point
                    movingPoint = null;
                }
                // if user draws a list of polygons
                else if (rasterizationMode == 3) {
                    // do the same but for current polygon in the list
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (wasDragged || currentPolygon.getPoints().size() != 1) {
                            currentPolygon.addPoint(new Point(x, y));
                        }
                        movingPoint = null;
                        update();
                    }
                }
            }
        });

        this.panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // set dragged mouse
                wasDragged = true;
                int x = e.getX();
                int y = e.getY();

                // adjust coordinates to be within canvas boundaries
                x = Math.max(0, Math.min(x, panel.getWidth()));
                y = Math.max(0, Math.min(y, panel.getHeight()));

                // if polygon is chosen
                if (rasterizationMode == 1) {
                    // draw
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        movingPoint = new Point(x, y);
                        update();
                    }
                    // if clipping mode is off
                    if (clippingMode == 0) {
                        // move the point of polygon if any found
                        if (e.getButton() == MouseEvent.BUTTON3 && nearestPointIndex >= 0) {
                            movingPoint = null;
                            polygon.setPoint(nearestPointIndex, new Point(e.getX(), e.getY()));
                            update();
                        }
                    }
                    // if clipping mode is on
                    else {
                        // move the point of clipping polygon if any found
                        if (e.getButton() == MouseEvent.BUTTON3 && nearestPointIndex >= 0) {
                            movingPoint = null;
                            clippingPolygon.setPoint(nearestPointIndex, new Point(e.getX(), e.getY()));
                            update();
                        }
                    }
                }
                // if rectangle is chosen
                else if (rasterizationMode == 2) {
                    // draw red moving lines
                    movingPoint = new Point(x, y);
                    update();
                }
                // if polygon list is chosen
                else if (rasterizationMode == 3) {
                    // draw current polygon
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        movingPoint = new Point(x, y);
                        update();
                    }
                }
            }
        });

        this.panel.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                // clean all when c is pressed
                if(e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
                    cleanAll();
                }

                // clean all if polygon is chosen and clipping mode is on. reset modes
                if(e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {
                    if(clippingMode > 0)
                        cleanAll();
                    rasterizationMode = 1;
                    clippingMode = 0;
                }

                // choose rectangle and ellipse rasterization
                if(e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    rasterizationMode = 2;
                    clippingMode = 0;
                }

                // choose scan line filling mode
                if(e.getKeyChar() == 'b' || e.getKeyChar() == 'B') {
                    fillingMode = 1;
                    clippingMode = 0;
                }

                // choose background seed fill mode
                if(e.getKeyChar() == 'n' || e.getKeyChar() == 'N') {
                    fillingMode = 2;
                    clippingMode = 0;
                }

                // choose border line seed fill mode
                if(e.getKeyChar() == 'm' || e.getKeyChar() == 'M') {
                    fillingMode = 3;
                    clippingMode = 0;
                }

                // turn on clipping mode
                if(e.getKeyChar() == 'o' || e.getKeyChar() == 'O') {
                    clippingMode = 1;
                }

                // turn on reverse clipping mode
                if(e.getKeyChar() == 'i' || e.getKeyChar() == 'I') {
                    clippingMode = 2;
                }

                // clip the polygon
                if(e.getKeyChar() == 'e' || e.getKeyChar() == 'E') {
                    clipPolygon(polygon, clippingPolygon);
                }

                // explicitly reset filling mode
                if(e.getKeyChar() == 'v' || e.getKeyChar() == 'V') {
                    fillingMode = 0;
                }

                // choose polygon list rasterization
                if(e.getKeyChar() == 'l' || e.getKeyChar() == 'L') {
                    rasterizationMode = 3;
                    clippingMode = 0;
                }

                // add new polygon to the list
                if(e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
                    Color color = colorGenerator.generate();
                    currentPolygon = new Polygon(color);
                    polygons.add(currentPolygon);
                }

                // get number of polygon to rasterize in the list
                if (Character.isDigit(e.getKeyChar())) {
                    numberInput.append(e.getKeyChar());
                    try {
                        polygonToRasterize = Integer.parseInt(numberInput.toString());
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid input: " + numberInput);
                    }
                }

                if (!Character.isDigit(e.getKeyChar())) {
                    numberInput.setLength(0);
                }
            }
        });
    }

    // function to update canvas
    private void update() {
        panel.clear();
        if (clippingMode == 0) {
            if (rasterizationMode == 1)
                polygonRasterizer.rasterize(polygon, movingPoint, finalLineColor);
            if (rasterizationMode == 2) {
                rectangleRasterizer.rasterize(rectangle, startingPoint, movingPoint);
                if (rectangle.getPoints().size() == 4) {
                    ellipseRasterizer.setEquationValues(rectangle);
                    ellipseRasterizer.rasterize();
                }
            }
            if (rasterizationMode == 3 && !polygons.isEmpty()) {
                for (Polygon polygon: polygons) {
                    if (polygons.indexOf(polygon) == polygons.size() - 1)
                        polygonRasterizer.rasterize(polygon, movingPoint, polygon.color);
                    else
                        polygonRasterizer.rasterize(polygon, null, polygon.color);
                }
            }
        } else {
            if (rasterizationMode == 1)
                polygonRasterizer.rasterize(polygon, null, finalLineColor);
            clippingPolygonRasterizer.rasterize(clippingPolygon, movingPoint, clippingFinalLineColor);
        }
    }

    // clean all structures on the canvas
    private void cleanAll() {
        polygon = new Polygon(finalLineColor);
        polygonRasterizer = new PolygonRasterizer(lineRasterizer, movingLineColor);

        rectangle = new Rectangle(Color.RED);
        rectangleRasterizer = new RectangleRasterizer(lineRasterizer);
        ellipseRasterizer = new EllipseRasterizer(lineRasterizer);

        clippingPolygon = new Polygon(clippingFinalLineColor);
        clippingPolygonRasterizer = new PolygonRasterizer(lineRasterizer, clippingMovingLineColor);

        Color color = colorGenerator.generate();
        currentPolygon = new Polygon(color);
        polygons = new ArrayList<>();

        movingPoint = null;
        panel.clear();
    }

    // clip polygon function depending on the clipping mode chosen
    private void clipPolygon(Polygon polygonToClip, Polygon clippingPolygon) {
        clipper = new PolygonClipper();
        if (clippingMode == 1)
            polygon = clipper.clip(polygonToClip, clippingPolygon);
        else if (clippingMode == 2)
            polygon = clipper.clipReverse(polygonToClip, clippingPolygon);
        update();
    }
}
