package control;

import fill.ScanLine;
import fill.SeedFill;
import fill.SeedFillBorder;
import model.Point;
import model.Polygon;
import model.Rectangle;
import rasterize.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final Panel panel;
    public LineRasterizer lineRasterizer;
    public Polygon polygon;
    public PolygonRasterizer polygonRasterizer;
    public Rectangle rectangle;
    public RectangleRasterizer rectangleRasterizer;
    public EllipseRasterizer ellipseRasterizer;
    public Polygon cuttingPolygon;
    public PolygonRasterizer cuttingPolygonRasterizer;
    private boolean wasDragged = false;
    Point startingPoint;
    Point movingPoint;
    private int rasterizationMode = 1;
    private int fillingMode = 1;
    private int cuttingMode = 0;
    private Color finalLineColor = Color.GREEN;
    private Color movingLineColor = Color.RED;
    private Color cuttingFinalLineColor = Color.BLUE;
    private Color cuttingMovingLineColor = Color.CYAN;

    private int x,y;
    private LineRasterizerGraphics rasterizer;

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
                startingPoint = new Point(e.getX(), e.getY());
                movingPoint = new Point(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                wasDragged = false;
                if (cuttingMode == 0) {
                    if (rasterizationMode == 1) {

                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (polygon.getPoints().isEmpty()) {
                                polygon.addPoint(new Point(e.getX(), e.getY()));
                                movingPoint = new Point(e.getX(), e.getY());
                            }
                        }

                        if (e.getButton() == MouseEvent.BUTTON3) {
                            if (fillingMode == 1) {
                                ScanLine scanLine = new ScanLine(lineRasterizer, polygonRasterizer, polygon);
                                scanLine.fill();
                            }

                            if (fillingMode == 2) {
                                SeedFill seedFill = new SeedFill(panel.getRaster(), panel.getRaster().getPixel(e.getX(), e.getY()), e.getX(), e.getY(), panel.getWidth(), panel.getHeight());
                                seedFill.fill();
                            }

                            if (fillingMode == 3) {
                                SeedFillBorder seedFillBorder = new SeedFillBorder(panel.getRaster(), finalLineColor, e.getX(), e.getY(), panel.getWidth(), panel.getHeight());
                                seedFillBorder.fill();
                            }
                        }
                    }

                    if (rasterizationMode == 2) {
                        cleanAll();
                        if (rectangle.getPoints().isEmpty()) {
                            startingPoint = new Point(e.getX(), e.getY());
                            movingPoint = new Point(e.getX(), e.getY());
                        }
                    }
                } else {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (cuttingPolygon.getPoints().isEmpty()) {
                            cuttingPolygon.addPoint(new Point(e.getX(), e.getY()));
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Adjust coordinates to be within canvas boundaries
                x = Math.max(0, Math.min(x, panel.getWidth()));
                y = Math.max(0, Math.min(y, panel.getHeight()));

                if (cuttingMode == 0) {
                    if (rasterizationMode == 1) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (wasDragged || polygon.getPoints().size() != 1) {
                                polygon.addPoint(new Point(x, y));
                            }
                            movingPoint = null;
                            update();
                        }
                    }

                    if (rasterizationMode == 2) {
                        rectangle.addAllPoints(startingPoint, new Point(x, y));
                        if (wasDragged)
                            update();
                        movingPoint = null;
                    }
                } else {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (wasDragged || cuttingPolygon.getPoints().size() != 1) {
                            cuttingPolygon.addPoint(new Point(x, y));
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
                wasDragged = true;
                int x = e.getX();
                int y = e.getY();

                // Adjust coordinates to be within canvas boundaries
                x = Math.max(0, Math.min(x, panel.getWidth()));
                y = Math.max(0, Math.min(y, panel.getHeight()));
                if (cuttingMode == 0) {
                    if (rasterizationMode == 1) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            movingPoint = new Point(x, y);
                            update();
                        }
                    }

                    if (rasterizationMode == 2) {
                        movingPoint = new Point(x, y);
                        update();
                    }
                } else {
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
                if(e.getKeyChar() == 'c' || e.getKeyChar() == 'C') {
                    cleanAll();
                }

                if(e.getKeyChar() == 'p' || e.getKeyChar() == 'P') {
                    rasterizationMode = 1;
                    cuttingMode = 0;
                }

                if(e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    rasterizationMode = 2;
                    cuttingMode = 0;
                }

                if(e.getKeyChar() == '1') {
                    fillingMode = 1;
                    cuttingMode = 0;
                }

                if(e.getKeyChar() == '2') {
                    fillingMode = 2;
                    cuttingMode = 0;
                }

                if(e.getKeyChar() == '3') {
                    fillingMode = 3;
                    cuttingMode = 0;
                }

                if(e.getKeyChar() == 'o' || e.getKeyChar() == 'O') {
                    cuttingMode = 1;
                }
            }
        });
    }

    private void update() {
        panel.clear();
        if (cuttingMode == 0) {
            if (rasterizationMode == 1)
                polygonRasterizer.rasterize(polygon, movingPoint);
            if (rasterizationMode == 2) {
                rectangleRasterizer.rasterize(rectangle, startingPoint, movingPoint);
                if (rectangle.getPoints().size() == 4) {
                    ellipseRasterizer.setEquationValues(rectangle);
                    ellipseRasterizer.rasterize();
                }
            }
        } else {
            if (rasterizationMode == 1)
                polygonRasterizer.rasterize(polygon, null);
            cuttingPolygonRasterizer.rasterize(cuttingPolygon, movingPoint);
        }
    }

    private void hardClear() {
        panel.clear();
    }

    private void cleanAll() {
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer, finalLineColor, movingLineColor);

        rectangle = new Rectangle();
        rectangleRasterizer = new RectangleRasterizer(lineRasterizer);
        ellipseRasterizer = new EllipseRasterizer(lineRasterizer);

        cuttingPolygon = new Polygon();
        cuttingPolygonRasterizer = new PolygonRasterizer(lineRasterizer, cuttingFinalLineColor, cuttingMovingLineColor);

        movingPoint = null;
        panel.clear();
    }

    private void cleanRectangle() {
        rectangle = new Rectangle();
        rectangleRasterizer = new RectangleRasterizer(lineRasterizer);
        ellipseRasterizer = new EllipseRasterizer(lineRasterizer);
        movingPoint = null;
    }
}
