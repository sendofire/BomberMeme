package fr.amu.iut.Personnages;

public class JoueurMultiplayer extends Joueurs {
    private String couleur;
    private String upKey, downKey, leftKey, rightKey, bombKey;
    private boolean invincible;
    private long invincibilityStartTime;
    private long invincibilityDuration;
    private boolean bombKeyPressed; // Pour éviter les placements répétés

    public JoueurMultiplayer(int idJ, int x, int y, String couleur) {
        super(idJ, x, y);
        this.couleur = couleur;
        this.invincible = false;
        this.invincibilityStartTime = 0;
        this.invincibilityDuration = 0;
        this.bombKeyPressed = false;
    }

    public void setControls(String up, String down, String left, String right, String bomb) {
        this.upKey = up;
        this.downKey = down;
        this.leftKey = left;
        this.rightKey = right;
        this.bombKey = bomb;
    }

    public void setInvincible(boolean invincible, long duration) {
        this.invincible = invincible;
        if (invincible) {
            this.invincibilityStartTime = System.currentTimeMillis();
            this.invincibilityDuration = duration;
        }
    }

    public boolean isInvincible() {
        if (invincible) {
            long elapsed = System.currentTimeMillis() - invincibilityStartTime;
            if (elapsed >= invincibilityDuration) {
                invincible = false;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void perdreVie() {
        if (!isInvincible()) {
            super.perdreVie();
            // Invincibilité temporaire après avoir perdu une vie
            if (isAlive()) {
                setInvincible(true, 2000); // 2 secondes d'invincibilité
            }
        }
    }

    // Getters pour les contrôles
    public String getUpKey() { return upKey; }
    public String getDownKey() { return downKey; }
    public String getLeftKey() { return leftKey; }
    public String getRightKey() { return rightKey; }
    public String getBombKey() { return bombKey; }

    // Getter et setter pour la couleur
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }

    // Getter et setter pour bombKeyPressed
    public boolean isBombKeyPressed() { return bombKeyPressed; }
    public void setBombKeyPressed(boolean bombKeyPressed) { this.bombKeyPressed = bombKeyPressed; }

    // Méthodes utilitaires
    public String getPlayerInfo() {
        return String.format("Joueur %s - Vies: %d, Bombes: %d, Portée: %d, Vitesse: %d",
                couleur, getVies(), getNombreBombes(), getPorteBombe(), getVitesse());
    }

    public boolean canPlaceBomb(int currentBombs) {
        return peutPlacerBombe(currentBombs) && !bombKeyPressed;
    }
}