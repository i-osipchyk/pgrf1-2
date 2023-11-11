package control;

import fill.ScanLine;
import model.Point;
import model.Polygon;
import rasterize.*;
import view.Panel;

import javax.swing.*;
import java.awt.event.*;

public class Controller2D implements Controller {

    private final Panel panel;
    public PolygonRasterizer polygonRasterizer;
    public LineRasterizer lineRasterizer;
    public EllipseRasterizer ellipseRasterizer;
    public Polygon polygon;
    private boolean wasDragged = false;
    Point movingPoint;

    private int x,y;
    private LineRasterizerGraphics rasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners();
        lineRasterizer = new LineRasterizerGraphics(this.panel.getRaster());
        polygon = new Polygon();
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
     }

    @Override
    public void initListeners() {
        this.panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                movingPoint = new Point(e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                wasDragged = false;

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

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (wasDragged || polygon.getPoints().size() != 1)
                        polygon.addPoint(new Point(e.getX(), e.getY()));
                    movingPoint = null;
                    update();
                }
            }
        });

        this.panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    wasDragged = true;
                    movingPoint = new Point(e.getX(), e.getY());
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
            }
        });
    }

    private void update() {
        panel.clear();
        polygonRasterizer.rasterize(polygon, movingPoint);
    }

    private void hardClear() {
        panel.clear();
    }

    private void cleanAll() {
        // TODO: clear all elements
    }

}
