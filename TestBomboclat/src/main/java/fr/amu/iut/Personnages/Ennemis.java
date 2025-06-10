package fr.amu.iut.Personnages;

import java.util.Random;

public class Ennemis {
    private int idE;
    private int x, y;
    private int vitesse;
    private String direction;
    private int type; // 0 = lent, 1 = rapide, 2 = intelligent
    private Random random;
    private long lastMoveTime;
    private int moveDelay; // délai entre les mouvements en ms

    public Ennemis(int idE, int x, int y) {
        this.idE = idE;
        this.x = x;
        this.y = y;
        this.vitesse = 1;
        this.direction = "DOWN";
        this.type = 0;
        this.random = new Random();
        this.lastMoveTime = System.currentTimeMillis();
        this.moveDelay = 500; // bouge toutes les 500ms par défaut
    }

    public Ennemis(int idE, int x, int y, int type) {
        this(idE, x, y);
        this.type = type;

        switch (type) {
            case 0: // Lent
                this.vitesse = 1;
                this.moveDelay = 800;
                break;
            case 1: // Rapide
                this.vitesse = 2;
                this.moveDelay = 300;
                break;
            case 2: // Intelligent
                this.vitesse = 1;
                this.moveDelay = 400;
                break;
        }
    }

    public boolean shouldMove() {
        return System.currentTimeMillis() - lastMoveTime >= moveDelay;
    }

    public void updateMoveTime() {
        lastMoveTime = System.currentTimeMillis();
    }

    public String getRandomDirection() {
        String[] directions = {"UP", "DOWN", "LEFT", "RIGHT"};
        return directions[random.nextInt(directions.length)];
    }

    public String getDirectionTowards(int targetX, int targetY) {
        int dx = targetX - x;
        int dy = targetY - y;

        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? "RIGHT" : "LEFT";
        } else {
            return dy > 0 ? "DOWN" : "UP";
        }
    }

    // Getters et setters
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

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMoveDelay() {
        return moveDelay;
    }

    public void setMoveDelay(int moveDelay) {
        this.moveDelay = moveDelay;
    }
}