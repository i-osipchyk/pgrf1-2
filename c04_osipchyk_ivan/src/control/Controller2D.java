package control;

import fill.ScanLine;
import model.Point;
import model.Polygon;
import model.Rectangle;
import rasterize.*;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final Panel panel;
    public PolygonRasterizer polygonRasterizer;
    public RectangleRasterizer rectangleRasterizer;
    public LineRasterizer lineRasterizer;
    public EllipseRasterizer ellipseRasterizer;
    public Polygon polygon;
    public Rectangle rectangle;
    private boolean wasDragged = false;
    Point startingPoint;
    Point movingPoint;
    private int mode = 1;

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
                if (mode == 1) {

                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (polygon.getPoints().isEmpty()) {
                            polygon.addPoint(new Point(e.getX(), e.getY()));
                            movingPoint = new Point(e.getX(), e.getY());
                        }
                    }

                    if (e.getButton() == MouseEvent.BUTTON3) {
                        ScanLine scanLine = new ScanLine(lineRasterizer, polygonRasterizer, polygon);
                        scanLine.fill();
                    }
                }

                if (mode == 2) {
                    cleanRectangle();
                    if (rectangle.getPoints().isEmpty()) {
                        startingPoint = new Point(e.getX(), e.getY());
                        movingPoint = new Point(e.getX(), e.getY());
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

                if (mode == 1) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        if (wasDragged || polygon.getPoints().size() != 1) {
                            polygon.addPoint(new Point(x, y));
                        }
                        movingPoint = null;
                        update();
                    }
                }

                if (mode == 2) {
                    rectangle.addAllPoints(startingPoint, new Point(x, y));
                    if (wasDragged)
                        update();
                    movingPoint = null;
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
                if (mode == 1) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        movingPoint = new Point(x, y);
                        update();
                    }
                }

                if (mode == 2) {
                    movingPoint = new Point(x, y);
                    update();
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
                    mode = 1;
                }

                if(e.getKeyChar() == 'r' || e.getKeyChar() == 'R') {
                    mode = 2;
                }
            }
        });
    }

    private void update() {
        panel.clear();
        if (mode == 1)
            polygonRasterizer.rasterize(polygon, movingPoint);
        if (mode == 2) {
            rectangleRasterizer.rasterize(rectangle, startingPoint, movingPoint);
            if (rectangle.getPoints().size() == 4) {
                ellipseRasterizer.setEquationValues(rectangle);
                ellipseRasterizer.rasterize();
            }
        }
    }

    private void hardClear() {
        panel.clear();
    }

    private void cleanAll() {
        polygon = new Polygon();
        rectangle = new Rectangle();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
        rectangleRasterizer = new RectangleRasterizer(lineRasterizer);
        ellipseRasterizer = new EllipseRasterizer(lineRasterizer);
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
