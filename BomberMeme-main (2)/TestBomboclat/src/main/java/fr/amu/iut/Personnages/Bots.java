package fr.amu.iut.Personnages;

import fr.amu.iut.Objets.Bombes;
import fr.amu.iut.Obstacle.Explosions;

import java.util.*;

public class Bots extends JoueurMultiplayer {

    private final Random random = new Random();
    private long lastBombTime = 0;
    private long lastMoveTime = 0;
    private final long moveCooldown = 300;

    public Bots(int id, int x, int y, String couleur) {
        super(id, x, y, couleur);
        this.setVies(1);
        this.setNombreBombes(1);
    }

    public void updateAI(int[][] gameBoard, List<Bombes> bombs, List<Explosions> explosions, List<JoueurMultiplayer> allPlayers) {
        if (!isAlive()) return;

        long now = System.currentTimeMillis();
        if (now - lastMoveTime < moveCooldown) return;
        lastMoveTime = now;

        int x = getX();
        int y = getY();

        if (isInDangerFromBombs(x, y, bombs, gameBoard)) {
            flee(x, y, gameBoard, explosions);
            setBombKeyPressed(false);
            return;
        }

        if (isPlayerNearby(x, y, allPlayers)) {
            long myBombs = bombs.stream().filter(b -> b.getOwnerId() == getIdJ()).count();
            if (peutPlacerBombe((int) myBombs) && now - lastBombTime > 3000) {
                setBombKeyPressed(true);
                lastBombTime = now;
                return;
            }
        }

        setBombKeyPressed(false);
        moveTowardPlayer(x, y, gameBoard, allPlayers);
    }

    private boolean isInDangerFromBombs(int x, int y, List<Bombes> bombs, int[][] board) {
        for (Bombes bomb : bombs) {
            int bx = bomb.getX();
            int by = bomb.getY();
            int range = bomb.getRange();

            if (bx == x && Math.abs(by - y) <= range && isLineClear(x, y, bx, by, board)) return true;
            if (by == y && Math.abs(bx - x) <= range && isLineClear(x, y, bx, by, board)) return true;
        }
        return false;
    }

    private boolean isLineClear(int x1, int y1, int x2, int y2, int[][] board) {
        int dx = Integer.compare(x2, x1);
        int dy = Integer.compare(y2, y1);
        int x = x1 + dx;
        int y = y1 + dy;

        while (x != x2 || y != y2) {
            if (board[y][x] == 1) return false;
            x += dx;
            y += dy;
        }
        return true;
    }

    private boolean isPlayerNearby(int x, int y, List<JoueurMultiplayer> players) {
        int[][] dirs = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        for (JoueurMultiplayer player : players) {
            if (player.getIdJ() == this.getIdJ() || !player.isAlive()) continue;

            for (int[] d : dirs) {
                int nx = x + d[0];
                int ny = y + d[1];
                if (nx == player.getX() && ny == player.getY()) return true;
            }
        }
        return false;
    }

    private void flee(int x, int y, int[][] board, List<Explosions> explosions) {
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        for (int[] dir : directions) {
            int nx = x + dir[0], ny = y + dir[1];
            if (nx >= 0 && ny >= 0 && nx < board[0].length && ny < board.length) {
                boolean safe = board[ny][nx] == 0 || board[ny][nx] == 5;
                boolean hasExplosion = explosions.stream().anyMatch(e -> e.getX() == nx && e.getY() == ny);
                if (safe && !hasExplosion) {
                    setX(nx);
                    setY(ny);
                    updateDirection(dir);
                    return;
                }
            }
        }
    }

    private void moveTowardPlayer(int x, int y, int[][] board, List<JoueurMultiplayer> players) {
        JoueurMultiplayer target = players.stream()
                .filter(p -> p.getIdJ() != this.getIdJ() && p.isAlive())
                .findFirst()
                .orElse(null);

        if (target == null) {
            moveToRandomSafeTile(x, y, board);
            return;
        }

        int tx = target.getX();
        int ty = target.getY();

        int dx = Integer.compare(tx, x);
        int dy = Integer.compare(ty, y);

        List<int[]> directions = new ArrayList<>();
        if (Math.abs(tx - x) > Math.abs(ty - y)) {
            directions.add(new int[]{dx, 0});
            directions.add(new int[]{0, dy});
        } else {
            directions.add(new int[]{0, dy});
            directions.add(new int[]{dx, 0});
        }

        // Essayer les directions calculées, puis alternatives si bloqué
        directions.add(new int[]{-dx, 0});
        directions.add(new int[]{0, -dy});

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];

            if (nx >= 0 && ny >= 0 && nx < board[0].length && ny < board.length) {
                if (board[ny][nx] == 0 || board[ny][nx] == 5) {
                    setX(nx);
                    setY(ny);
                    updateDirection(dir);
                    return;
                }
            }
        }

        // Si tout est bloqué, mouvement aléatoire
        moveToRandomSafeTile(x, y, board);
    }

    private void moveToRandomSafeTile(int x, int y, int[][] board) {
        int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};
        List<int[]> valid = new ArrayList<>();

        for (int[] dir : directions) {
            int nx = x + dir[0];
            int ny = y + dir[1];
            if (nx >= 0 && ny >= 0 && nx < board[0].length && ny < board.length) {
                if (board[ny][nx] == 0 || board[ny][nx] == 5) {
                    valid.add(dir);
                }
            }
        }

        if (!valid.isEmpty()) {
            int[] dir = valid.get(random.nextInt(valid.size()));
            setX(x + dir[0]);
            setY(y + dir[1]);
            updateDirection(dir);
        }
    }

    private void updateDirection(int[] dir) {
        if (dir[0] == 0 && dir[1] == -1) setDirection("UP");
        if (dir[0] == 0 && dir[1] == 1) setDirection("DOWN");
        if (dir[0] == -1 && dir[1] == 0) setDirection("LEFT");
        if (dir[0] == 1 && dir[1] == 0) setDirection("RIGHT");
    }
}
