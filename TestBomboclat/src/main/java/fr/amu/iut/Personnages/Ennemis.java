package fr.amu.iut.Personnages;

public class Ennemis {

    int idE;
    int x, y;

    public Ennemis(int idE, int x, int y) {
        this.idE = idE;
        this.x = x;
        this.y = y;
    }

    public int getIdE() {
        return idE;
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
