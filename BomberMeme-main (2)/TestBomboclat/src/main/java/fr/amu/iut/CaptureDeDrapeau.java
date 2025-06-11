package fr.amu.iut;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class CaptureDeDrapeau {

        private final int TILE_SIZE = 40;
        private final int WIDTH = 15;
        private final int HEIGHT = 10;

        private int playerX = 1;
        private int playerY = 1;
        private int redBaseX = 1, redBaseY = 1;
        private int blueBaseX = WIDTH - 2, blueBaseY = HEIGHT - 2;
        private int flagX = blueBaseX, flagY = blueBaseY;

        private boolean hasFlag = false;
        private boolean[][] walls = new boolean[WIDTH][HEIGHT];

        private Canvas canvas;
        private Text scoreText;
        private int redScore = 0;

        public void start(Pane root) {
            canvas = new Canvas(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);
            scoreText = new Text("Red: 0");
            root.getChildren().addAll(canvas, scoreText);
            draw();

            root.setFocusTraversable(true);
            root.setOnKeyPressed(e -> {
                int newX = playerX;
                int newY = playerY;

                if (e.getCode() == KeyCode.UP) newY--;
                else if (e.getCode() == KeyCode.DOWN) newY++;
                else if (e.getCode() == KeyCode.LEFT) newX--;
                else if (e.getCode() == KeyCode.RIGHT) newX++;

                if (isInBounds(newX, newY) && !walls[newX][newY]) {
                    playerX = newX;
                    playerY = newY;
                    checkFlagPickup();
                    checkFlagCapture();
                    draw();
                }
            });
        }

        private void draw() {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Background
            gc.setFill(Color.LIGHTGRAY);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Bases
            gc.setFill(Color.RED);
            gc.fillRect(redBaseX * TILE_SIZE, redBaseY * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            gc.setFill(Color.BLUE);
            gc.fillRect(blueBaseX * TILE_SIZE, blueBaseY * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            // Flag
            if (!hasFlag) {
                gc.setFill(Color.GOLD);
                gc.fillOval(flagX * TILE_SIZE + 10, flagY * TILE_SIZE + 10, 20, 20);
            }

            // Player
            gc.setFill(hasFlag ? Color.ORANGE : Color.GREEN);
            gc.fillOval(playerX * TILE_SIZE + 5, playerY * TILE_SIZE + 5, 30, 30);
        }

        private void checkFlagPickup() {
            if (playerX == flagX && playerY == flagY && !hasFlag) {
                hasFlag = true;
            }
        }

        private void checkFlagCapture() {
            if (hasFlag && playerX == redBaseX && playerY == redBaseY) {
                redScore++;
                hasFlag = false;
                flagX = blueBaseX;
                flagY = blueBaseY;
                scoreText.setText("Red: " + redScore);

                if (redScore >= 3) {
                    scoreText.setText("🏆 Red wins!");
                }
            }
        }

        private boolean isInBounds(int x, int y) {
            return x >= 0 && y >= 0 && x < WIDTH && y < HEIGHT;
        }
    }