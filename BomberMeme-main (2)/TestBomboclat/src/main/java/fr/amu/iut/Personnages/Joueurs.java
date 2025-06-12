package fr.amu.iut.Personnages;

import fr.amu.iut.graphique.Sprite;

import java.util.ArrayList;
import java.util.List;

public class Joueurs {
    private int idJ;
    private int x, y;
    private int vies;
    private int vitesse;
    private int nombreBombes;
    private int porteBombe;
    private String direction;
    private boolean alive;
    private int score;

    public Joueurs(int idJ, int x, int y) {
        this.idJ = idJ;
        this.x = x;
        this.y = y;
        this.vies = 3;
        this.vitesse = 1;
        this.nombreBombes = 1;
        this.porteBombe = 1;
        this.direction = "DOWN";
        this.alive = true;
        this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    //Initialize up animation sprites
    //List<Sprite> up = new ArrayList<>();
    //up.add(Sprite.player_up_rouge);


    public void perdreVie() {
        vies--;
        if (vies <= 0) {
            alive = false;
        }
    }

    public void gagnerVie() {
        vies++;
    }

    public void ameliorerPortee() {
        porteBombe++;
    }

    public void ameliorerNombreBombes() {
        nombreBombes++;
    }

    public void ameliorerVitesse() {
        if (vitesse < 3) {
            vitesse++;
        }
    }

    public boolean peutPlacerBombe(int nombreBombesActuelles) {
        return nombreBombesActuelles < nombreBombes;
    }

    // Getters et setters
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

    public int getVies() {
        return vies;
    }

    public void setVies(int vies) {
        this.vies = vies;
    }

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }

    public int getNombreBombes() {
        return nombreBombes;
    }

    public void setNombreBombes(int nombreBombes) {
        this.nombreBombes = nombreBombes;
    }

    public int getPorteBombe() {
        return porteBombe;
    }

    public void setPorteBombe(int porteBombe) {
        this.porteBombe = porteBombe;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}