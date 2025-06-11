package fr.amu.iut.Personnages;

import fr.amu.iut.Objets.Bombes;
import fr.amu.iut.Obstacle.Explosions;

import java.util.List;
import java.util.Random;

public class Bots extends JoueurMultiplayer {

    private final Random random = new Random();

    public Bots(int id, int x, int y, String couleur) {
        super(id, x, y, couleur);
        this.setVies(1);
    }

    public void updateAI(int[][] gameBoard, List<Bombes> bombs, List<Explosions> explosions) {
        if (!isAlive()) return;

        int currentX = getX();
        int currentY = getY();

        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        int[] dir = directions[random.nextInt(directions.length)];

        int nx = currentX + dir[0];
        int ny = currentY + dir[1];

        // Se déplacer si la case est vide ou contient un powerup
        if (nx >= 0 && ny >= 0 && nx < gameBoard[0].length && ny < gameBoard.length) {
            if (gameBoard[ny][nx] == 0 || gameBoard[ny][nx] == 5) {
                setX(nx);
                setY(ny);
                if (dir[0] == 0 && dir[1] == -1) setDirection("UP");
                if (dir[0] == 0 && dir[1] == 1) setDirection("DOWN");
                if (dir[0] == -1 && dir[1] == 0) setDirection("LEFT");
                if (dir[0] == 1 && dir[1] == 0) setDirection("RIGHT");
            }
        }

        // Vérifie s’il peut poser une bombe et le fait directement
        if (random.nextDouble() < 0.1 && peutPlacerBombe((int) bombs.stream()
                .filter(b -> b.getOwnerId() == getIdJ()).count())) {
            setBombKeyPressed(true);
        } else {
            setBombKeyPressed(false);
        }
    }

}
