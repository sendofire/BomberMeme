package fr.amu.iut.Obstacle;

public class Murs {
    private int x, y;
    private boolean cassable;
    private boolean detruit;

    public Murs(int x, int y, boolean cassable) {
        this.x = x;
        this.y = y;
        this.cassable = cassable;
        this.detruit = false;
    }

    public void detruire() {
        if (cassable) {
            detruit = true;
        }
    }

    public boolean peutEtreDetruit() {
        return cassable && !detruit;
    }

    // Getters et setters
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isCassable() { return cassable; }
    public boolean isDetruit() { return detruit; }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setCassable(boolean cassable) { this.cassable = cassable; }
    public void setDetruit(boolean detruit) { this.detruit = detruit; }
}
