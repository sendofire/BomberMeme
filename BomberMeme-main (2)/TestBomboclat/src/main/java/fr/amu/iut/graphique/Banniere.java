package fr.amu.iut.graphique;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Banniere {
    private GraphicsContext gc;
    private Timeline timer;
    private int gameTime; // en secondes
    private boolean timeUp = false;
    private int initialTime = 180; // Pour calculer le pourcentage

    public Banniere(GraphicsContext gc) {
        this.gc = gc;
    }

    public void setGameTime(int seconds) {
        this.gameTime = seconds;
        this.initialTime = seconds;
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
        // Nettoyer la zone de la bannière
        gc.clearRect(0, 0, 912, 60);

        // Fond de la bannière avec dégradé
        createGradientBackground();

        // Bordures décoratives
        createBorders();

        // Texte de la bannière
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setTextAlign(TextAlignment.LEFT);

        // Temps restant avec couleur dynamique et effets
        renderTimeDisplay();

        // Joueurs vivants avec icônes colorées
        renderPlayersDisplay(alivePlayers);

        // Bombes actives avec indicateurs visuels
        renderBombsDisplay(totalBombs);

        // Power-ups disponibles
        renderPowerUpsDisplay(powerUps);

        // Score avec formatage
        renderScoreDisplay(score);

        // Messages d'alerte dynamiques
        renderAlertMessages();

        // Barre de progression du temps
        drawTimeProgressBar();

        // Informations supplémentaires
        renderAdditionalInfo();
    }

    private void createGradientBackground() {
        // Fond principal avec effet de dégradé simulé
        gc.setFill(Color.rgb(45, 45, 45)); // Gris foncé
        gc.fillRect(0, 0, 912, 60);

        // Effet de brillance en haut
        gc.setFill(Color.rgb(60, 60, 60));
        gc.fillRect(0, 0, 912, 20);

        // Ombre en bas
        gc.setFill(Color.rgb(30, 30, 30));
        gc.fillRect(0, 45, 912, 15);
    }

    private void createBorders() {
        // Bordure supérieure brillante
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 912, 2);

        // Bordure inférieure sombre
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 58, 912, 2);

        // Séparateurs verticaux
        gc.setFill(Color.rgb(60, 60, 60));
        int[] separators = {140, 320, 460, 600};
        for (int x : separators) {
            gc.fillRect(x, 5, 1, 50);
        }
    }

    private void renderTimeDisplay() {
        int minutes = gameTime / 60;
        int seconds = gameTime % 60;
        String timeText = String.format("TIME: %02d:%02d", minutes, seconds);

        // Couleur dynamique selon le temps restant
        if (gameTime <= 10) {
            // Clignotement rouge pour les 10 dernières secondes
            if (System.currentTimeMillis() % 400 < 200) {
                gc.setFill(Color.RED);
            } else {
                gc.setFill(Color.DARKRED);
            }
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        } else if (gameTime <= 30) {
            gc.setFill(Color.ORANGE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        } else if (gameTime <= 60) {
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        } else {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        }

        gc.fillText(timeText, 10, 25);
    }

    private void renderPlayersDisplay(int alivePlayers) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("PLAYERS: " + alivePlayers, 150, 25);

        // Dessiner des icônes de joueurs colorées
        int playerIconX = 250;
        String[] playerColors = {"Rouge", "Bleu", "Vert", "Jaune"};
        Color[] colors = {Color.RED, Color.BLUE, Color.LIME, Color.YELLOW};

        for (int i = 0; i < Math.min(alivePlayers, 4); i++) {
            // Cercle principal
            gc.setFill(colors[i]);
            gc.fillOval(playerIconX + i * 18, 12, 12, 12);

            // Contour noir
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(playerIconX + i * 18, 12, 12, 12);

            // Point brillant pour l'effet 3D
            gc.setFill(Color.WHITE);
            gc.fillOval(playerIconX + i * 18 + 2, 14, 3, 3);
        }

        // Afficher le nombre total si plus de 4 joueurs
        if (alivePlayers > 4) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
            gc.fillText("+" + (alivePlayers - 4), playerIconX + 72, 20);
        }
    }

    private void renderBombsDisplay(int totalBombs) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("BOMBS: " + totalBombs, 330, 25);

        // Indicateurs visuels pour les bombes
        if (totalBombs > 0) {
            int bombIconX = 390;

            // Dessiner jusqu'à 8 bombes individuelles
            for (int i = 0; i < Math.min(totalBombs, 8); i++) {
                // Animation de pulsation pour les bombes
                double pulseScale = 1.0 + 0.2 * Math.sin(System.currentTimeMillis() / 200.0 + i);
                double size = 6 * pulseScale;

                gc.setFill(Color.DARKRED);
                gc.fillOval(bombIconX + i * 10 - size/2 + 3, 18 - size/2 + 3, size, size);

                // Mèche
                gc.setFill(Color.ORANGE);
                gc.fillRect(bombIconX + i * 10 + 1, 16, 2, 4);
            }

            // Afficher "+X" si plus de 8 bombes
            if (totalBombs > 8) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                gc.fillText("+" + (totalBombs - 8), bombIconX + 80, 22);
            }
        }
    }

    private void renderPowerUpsDisplay(int powerUps) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("POWER-UPS: " + powerUps, 470, 25);

        // Indicateur visuel pour les power-ups
        if (powerUps > 0) {
            // Effet de brillance
            double sparkle = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0);
            gc.setFill(Color.rgb(255, 255, 0, sparkle));

            // Étoile brillante
            int starX = 580;
            int starY = 18;
            drawStar(starX, starY, 6, 3, 5);

            // Cercles de power-up
            gc.setFill(Color.GOLD);
            for (int i = 0; i < Math.min(powerUps, 5); i++) {
                gc.fillOval(starX + 15 + i * 8, 16, 6, 6);
                gc.setStroke(Color.ORANGE);
                gc.setLineWidth(1);
                gc.strokeOval(starX + 15 + i * 8, 16, 6, 6);
            }
        }
    }

    private void renderScoreDisplay(int score) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        // Formatage du score avec séparateurs
        String formattedScore = String.format("%,d", score);
        gc.fillText("SCORE: " + formattedScore, 620, 25);

        // Effet de brillance pour les scores élevés
        if (score > 1000) {
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.fillText("★", 750, 20);
        }
        if (score > 5000) {
            gc.fillText("★★", 760, 20);
        }
        if (score > 10000) {
            gc.fillText("★★★", 770, 20);
        }
    }

    private void renderAlertMessages() {
        gc.setTextAlign(TextAlignment.RIGHT);

        if (gameTime <= 5 && gameTime > 0) {
            // Clignotement rapide pour les 5 dernières secondes
            if (System.currentTimeMillis() % 200 < 100) {
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                gc.fillText("CRITICAL!", 900, 25);

                // Effet de pulsation
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                double pulseSize = 5 + 3 * Math.sin(System.currentTimeMillis() / 100.0);
                gc.strokeOval(850 - pulseSize, 15 - pulseSize, 60 + 2*pulseSize, 20 + 2*pulseSize);
            }
        } else if (gameTime <= 10 && gameTime > 5) {
            // Clignotement pour les 10 dernières secondes
            if (System.currentTimeMillis() % 500 < 250) {
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                gc.fillText("DANGER!", 900, 25);
            }
        } else if (gameTime <= 30 && gameTime > 10) {
            gc.setFill(Color.ORANGE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gc.fillText("TIME RUNNING OUT!", 900, 25);
        } else if (gameTime <= 60 && gameTime > 30) {
            gc.setFill(Color.YELLOW);
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
            gc.fillText("Hurry up!", 900, 25);
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void drawTimeProgressBar() {
        int barWidth = 120;
        int barHeight = 6;
        int barX = 10;
        int barY = 35;

        // Calculer le pourcentage de temps restant
        double timePercentage = Math.max(0, (double) gameTime / initialTime);

        // Fond de la barre avec bordure
        gc.setFill(Color.BLACK);
        gc.fillRect(barX - 1, barY - 1, barWidth + 2, barHeight + 2);
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(barX, barY, barWidth, barHeight);

        // Barre de progression avec couleur dynamique
        Color barColor;
        if (timePercentage > 0.6) {
            barColor = Color.LIME;
        } else if (timePercentage > 0.3) {
            barColor = Color.YELLOW;
        } else if (timePercentage > 0.15) {
            barColor = Color.ORANGE;
        } else {
            barColor = Color.RED;
        }

        // Effet de dégradé simulé
        gc.setFill(barColor);
        double progressWidth = barWidth * timePercentage;
        gc.fillRect(barX, barY, progressWidth, barHeight);

        // Effet de brillance sur la barre
        gc.setFill(Color.WHITE);
        gc.setGlobalAlpha(0.3);
        gc.fillRect(barX, barY, progressWidth, barHeight / 2);
        gc.setGlobalAlpha(1.0);

        // Bordure de la barre
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barWidth, barHeight);

        // Marqueurs de temps (quarts)
        gc.setStroke(Color.LIGHTGRAY);
        for (int i = 1; i < 4; i++) {
            int markX = barX + (barWidth * i / 4);
            gc.strokeLine(markX, barY, markX, barY + barHeight);
        }
    }

    private void renderAdditionalInfo() {
        // Mini-légende des couleurs de temps
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("Time:", 10, 55);

        // Indicateurs de couleur
        gc.setFill(Color.LIME);
        gc.fillRect(35, 50, 8, 3);
        gc.setFill(Color.YELLOW);
        gc.fillRect(45, 50, 8, 3);
        gc.setFill(Color.ORANGE);
        gc.fillRect(55, 50, 8, 3);
        gc.setFill(Color.RED);
        gc.fillRect(65, 50, 8, 3);

        // Version du jeu
        gc.setFill(Color.DARKGRAY);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("BOMBOCLAT v1.0", 900, 55);
        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void drawStar(double centerX, double centerY, double outerRadius, double innerRadius, int numPoints) {
        double[] xPoints = new double[numPoints * 2];
        double[] yPoints = new double[numPoints * 2];

        for (int i = 0; i < numPoints * 2; i++) {
            double angle = i * Math.PI / numPoints;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = centerX + radius * Math.cos(angle - Math.PI / 2);
            yPoints[i] = centerY + radius * Math.sin(angle - Math.PI / 2);
        }

        gc.fillPolygon(xPoints, yPoints, numPoints * 2);
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public int getGameTime() {
        return gameTime;
    }

    public double getTimePercentage() {
        return Math.max(0, (double) gameTime / initialTime);
    }

    public boolean isLowTime() {
        return gameTime <= 30;
    }

    public boolean isCriticalTime() {
        return gameTime <= 10;
    }
}