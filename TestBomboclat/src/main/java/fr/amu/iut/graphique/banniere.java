package fr.amu.iut.graphique;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class banniere {
    private GraphicsContext gc;
    private Timeline timer;
    private int gameTime; // en secondes
    private boolean timeUp = false;

    public banniere(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setGameTime(int seconds) {
        this.gameTime = seconds;
        this.timeUp = false;
    }

    public void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            gameTime--;
            if (gameTime <= 0) {
                timeUp = true;
                timer.stop();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void render(int alivePlayers, int totalBombs, int powerUps, int score) {
        // Fond de la bannière
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(0, 0, 912, 60); // 19 * 48 = 912

        // Texte de la bannière
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));
        gc.setTextAlign(TextAlignment.LEFT);

        // Temps restant
        int minutes = gameTime / 60;
        int seconds = gameTime % 60;
        String timeText = String.format("TIME: %02d:%02d", minutes, seconds);
        gc.fillText(timeText, 10, 25);

        // Joueurs vivants
        gc.fillText("PLAYERS: " + alivePlayers, 150, 25);

        // Bombes actives
        gc.fillText("BOMBS: " + totalBombs, 280, 25);

        // Score
        gc.fillText("SCORE: " + score, 400, 25);

        // Message si temps bientôt écoulé
        if (gameTime <= 30 && gameTime > 0) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("Arial", 14));
            gc.fillText("TIME RUNNING OUT!", 600, 25);
        }
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public int getGameTime() {
        return gameTime;
    }
}