package fr.amu.iut.Obstacle;

import java.util.ArrayList;

public class Murs {

    ArrayList<Integer> idM = new ArrayList<Integer>();
    int x, y;

    public Murs(int x, int y, Boolean cassable) {
        this.x = x;
        this.y = y;
        idM.add(x, y);
    }

    public ArrayList<Integer> getIdM() {
        return idM;
    }

    public void setIdM(ArrayList<Integer> idM) {
        this.idM = idM;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
