package fr.amu.iut.Objets;

public class Bombes {
    private int x, y;
    private int range;
    private long timeCreated;
    private int explosionTime; // en millisecondes

    public Bombes(int x, int y) {
        this.x = x;
        this.y = y;
        this.range = 2; // portée par défaut
        this.timeCreated = System.currentTimeMillis();
        this.explosionTime = 3000; // 3 secondes par défaut
    }

    public Bombes(int x, int y, int range, int explosionTime) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.timeCreated = System.currentTimeMillis();
        this.explosionTime = explosionTime;
    }

    public boolean shouldExplode() {
        return System.currentTimeMillis() - timeCreated >= explosionTime;
    }

    public long getTimeRemaining() {
        long elapsed = System.currentTimeMillis() - timeCreated;
        return Math.max(0, explosionTime - elapsed);
    }

    // Getters et setters
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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public int getExplosionTime() {
        return explosionTime;
    }

    public void setExplosionTime(int explosionTime) {
        this.explosionTime = explosionTime;
    }
}