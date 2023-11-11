package model;

public class Edge {
    private int x1, x2, y1, y2;

    public Edge(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public boolean isHorizontal() {
        return y1 == y2;
    }

    public void orientate() {
        if (y2 < y1) {
            int temp = x1;
            x1 = x2;
            x2= temp;

            temp = y1;
            y1 = y2;
            y2= temp;
        }
    }

    public int getX1() {
        return this.x1;
    }

    public int getY1() {
        return this.y1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }
}
