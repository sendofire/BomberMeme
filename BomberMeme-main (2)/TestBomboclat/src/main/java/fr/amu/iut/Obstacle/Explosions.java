package fr.amu.iut.Obstacle;

public class Explosions {

    int x, y;
    int ownerId;

    public Explosions(int x, int y) {
        this.x = x;
        this.y = y;
        this.ownerId = 0;
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

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
}
