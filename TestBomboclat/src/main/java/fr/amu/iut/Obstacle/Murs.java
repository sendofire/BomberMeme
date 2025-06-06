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
}
