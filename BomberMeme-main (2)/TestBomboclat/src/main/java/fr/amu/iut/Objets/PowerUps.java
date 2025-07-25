package fr.amu.iut.Objets;

public class PowerUps {
    private int x, y;
    private PowerupType type;
    private boolean collected;
    private long spawnTime;

    public enum PowerupType {
        BOMB_RANGE,     // Augmente la portée des bombes
        BOMB_COUNT,     // Augmente le nombre de bombes
        SPEED_UP,       // Augmente la vitesse
        EXTRA_LIFE,     // Vie supplémentaire
        INVINCIBILITY   // Invincibilité temporaire
    }

    public PowerUps(int x, int y, PowerupType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.collected = false;
        this.spawnTime = System.currentTimeMillis();
    }

    public void collect() {
        this.collected = true;
    }

    public boolean isExpired() {
        // Les powerups disparaissent après 30 secondes s'ils ne sont pas collectés
        return System.currentTimeMillis() - spawnTime > 30000;
    }

    public int getPoints() {
        switch (type) {
            case BOMB_RANGE: return 10;
            case BOMB_COUNT: return 15;
            case SPEED_UP: return 20;
            case EXTRA_LIFE: return 50;
            case INVINCIBILITY: return 30;
            default: return 5;
        }
    }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public PowerupType getType() { return type; }
    public boolean isCollected() { return collected; }
    public long getSpawnTime() { return spawnTime; }

    // Setters
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setType(PowerupType type) { this.type = type; }
}
