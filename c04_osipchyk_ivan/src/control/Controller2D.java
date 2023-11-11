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

    }

    private void update() {
//        panel.clear();
        //TODO

    }

    private void hardClear() {
        panel.clear();
    }

}
