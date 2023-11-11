package control;

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

    private int x,y;
    private LineRasterizerGraphics rasterizer;

    public Controller2D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners();
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
     }

    @Override
    public void initListeners() {
        this.panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO: initialize currentPoint
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO: set dragging to false
                // TODO: add point and initialize current point
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO: draw the polygon and exit interactive mode
            }
        });

        this.panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // TODO: add interactive mode while dragging
                update();
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
        // TODO: rasterize polygon

    }

    private void hardClear() {
        panel.clear();
    }

    private void cleanAll() {
        // TODO: clear all elements
    }

}
