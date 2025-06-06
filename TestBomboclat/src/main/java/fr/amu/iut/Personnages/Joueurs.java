package fr.amu.iut.Personnages;

public class Joueurs {

    int idJ;
    int x, y;

    public Joueurs(int idJ, int x, int y) {
        this.idJ = idJ;
        this.x = x;
        this.y = y;
    }

    public int getIdJ() {
        return idJ;
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
