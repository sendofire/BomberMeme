package fr.amu.iut.graphique;

import fr.amu.iut.Personnages.JoueurMultiplayer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.List;

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

    /**
     * Méthode render mise à jour pour inclure les informations des joueurs
     */
    public void render(int alivePlayers, int totalBombs, int powerUps, int score, List<JoueurMultiplayer> players) {
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

        // Affichage détaillé des joueurs avec leurs vies
        renderPlayersWithLives(players);

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

    /**
     * Méthode render de compatibilité (ancienne version)
     */
    public void render(int alivePlayers, int totalBombs, int powerUps, int score) {
        render(alivePlayers, totalBombs, powerUps, score, null);
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

        // Séparateurs verticaux ajustés pour la nouvelle disposition
        gc.setFill(Color.rgb(60, 60, 60));
        int[] separators = {180, 460, 600, 750}; // Ajusté pour faire de la place aux vies des joueurs
        for (int x : separators) {
            gc.fillRect(x, 5, 1, 50);
        }
    }

    private void renderTimeDisplay() {
        int minutes = gameTime / 60;
        int seconds = gameTime % 60;
        String timeText = String.format("⏰ %02d:%02d", minutes, seconds);

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

        // Afficher le pourcentage de temps restant sous le temps principal
        double timePercentage = (double) gameTime / initialTime * 100;
        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        gc.fillText(String.format("%.0f%%", timePercentage), 10, 38);
    }

    private void renderPlayersWithLives(List<JoueurMultiplayer> players) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("JOUEURS:", 190, 20);

        if (players != null && !players.isEmpty()) {
            int startX = 190;
            int startY = 30;
            int playerWidth = 65; // Largeur allouée par joueur

            for (int i = 0; i < Math.min(players.size(), 4); i++) {
                JoueurMultiplayer player = players.get(i);
                int playerX = startX + i * playerWidth;

                // Couleur du joueur
                Color playerColor = getPlayerColor(player.getCouleur());

                // Dessiner l'icône du joueur
                drawPlayerIcon(playerX, startY, playerColor, player.isAlive(), player.isInvincible());

                // Afficher les vies du joueur
                renderPlayerLives(playerX, startY + 15, player, playerColor);

                // Nom du joueur (première lettre)
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 8));
                gc.fillText(player.getCouleur().substring(0, 1), playerX + 15, startY + 5);
            }
        } else {
            // Affichage par défaut si pas de liste de joueurs
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            gc.fillText("En attente...", 250, 35);
        }
    }

    private void drawPlayerIcon(int x, int y, Color color, boolean isAlive, boolean isInvincible) {
        if (!isAlive) {
            // Joueur mort - icône grisée avec croix
            gc.setFill(Color.DARKGRAY);
            gc.fillOval(x, y, 12, 12);
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeLine(x + 3, y + 3, x + 9, y + 9);
            gc.strokeLine(x + 9, y + 3, x + 3, y + 9);
        } else {
            // Joueur vivant
            if (isInvincible) {
                // Effet de clignotement pour l'invincibilité
                if (System.currentTimeMillis() % 300 < 150) {
                    gc.setFill(Color.WHITE);
                } else {
                    gc.setFill(color);
                }

                // Aura dorée autour du joueur invincible
                gc.setStroke(Color.GOLD);
                gc.setLineWidth(2);
                gc.strokeOval(x - 2, y - 2, 16, 16);
            } else {
                gc.setFill(color);
            }

            gc.fillOval(x, y, 12, 12);

            // Contour noir
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            gc.strokeOval(x, y, 12, 12);

            // Point brillant pour l'effet 3D
            gc.setFill(Color.WHITE);
            gc.fillOval(x + 2, y + 2, 3, 3);
        }
    }

    private void renderPlayerLives(int x, int y, JoueurMultiplayer player, Color playerColor) {
        int lives = player.getVies();

        // Dessiner les cœurs de vie
        for (int i = 0; i < Math.max(lives, 3); i++) { // Montrer toujours 3 emplacements
            int heartX = x + i * 8;
            int heartY = y;

            if (i < lives) {
                // Cœur plein
                gc.setFill(Color.RED);
                drawHeart(heartX, heartY, 6, true);
            } else {
                // Cœur vide (contour seulement)
                gc.setFill(Color.DARKGRAY);
                drawHeart(heartX, heartY, 6, false);
            }
        }

        // Afficher le nombre de vies si > 3
        if (lives > 3) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 8));
            gc.fillText("+" + (lives - 3), x + 25, y + 5);
        }

        // Indicateur de statut spécial
        if (player.isInvincible()) {
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 7));
            gc.fillText("INV", x + 35, y + 5);
        }
    }

    private void drawHeart(int x, int y, int size, boolean filled) {
        // Dessiner un cœur simple avec des cercles et un triangle
        if (filled) {
            gc.setFill(Color.RED);
        } else {
            gc.setStroke(Color.DARKGRAY);
            gc.setLineWidth(1);
        }

        if (filled) {
            // Deux cercles pour le haut du cœur
            gc.fillOval(x, y, size/2, size/2);
            gc.fillOval(x + size/2 - 1, y, size/2, size/2);

            // Triangle pour le bas du cœur
            double[] xPoints = {x + size/4.0, x + 3*size/4.0, x + size/2.0};
            double[] yPoints = {y + size/3.0, y + size/3.0, y + size - 1};
            gc.fillPolygon(xPoints, yPoints, 3);
        } else {
            // Contour seulement
            gc.strokeOval(x, y, size/2, size/2);
            gc.strokeOval(x + size/2 - 1, y, size/2, size/2);

            double[] xPoints = {x + size/4.0, x + 3*size/4.0, x + size/2.0};
            double[] yPoints = {y + size/3.0, y + size/3.0, y + size - 1};
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }

    private Color getPlayerColor(String couleur) {
        switch (couleur) {
            case "Rouge": return Color.RED;
            case "Bleu": return Color.BLUE;
            case "Vert": return Color.LIME;
            case "Jaune": return Color.YELLOW;
            default: return Color.WHITE;
        }
    }

    private void renderBombsDisplay(int totalBombs) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("💣 " + totalBombs, 470, 25);

        // Indicateurs visuels pour les bombes (simplifié pour faire de la place)
        if (totalBombs > 0) {
            int bombIconX = 510;

            // Dessiner jusqu'à 5 bombes individuelles
            for (int i = 0; i < Math.min(totalBombs, 5); i++) {
                // Animation de pulsation pour les bombes
                double pulseScale = 1.0 + 0.2 * Math.sin(System.currentTimeMillis() / 200.0 + i);
                double size = 6 * pulseScale;

                gc.setFill(Color.DARKRED);
                gc.fillOval(bombIconX + i * 10 - size/2 + 3, 18 - size/2 + 3, size, size);

                // Mèche
                gc.setFill(Color.ORANGE);
                gc.fillRect(bombIconX + i * 10 + 1, 16, 2, 4);
            }

            // Afficher "+X" si plus de 5 bombes
            if (totalBombs > 5) {
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
                gc.fillText("+" + (totalBombs - 5), bombIconX + 50, 22);
            }
        }
    }

    private void renderPowerUpsDisplay(int powerUps) {
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.fillText("⭐ " + powerUps, 610, 25);

        // Indicateur visuel pour les power-ups
        if (powerUps > 0) {
            // Effet de brillance
            double sparkle = 0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 300.0);
            gc.setFill(Color.rgb(255, 255, 0, sparkle));

            // Étoile brillante
            int starX = 650;
            int starY = 18;
            drawStar(starX, starY, 6, 3, 5);

            // Cercles de power-up
            gc.setFill(Color.GOLD);
            for (int i = 0; i < Math.min(powerUps, 3); i++) {
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
        gc.fillText("💰 " + formattedScore, 760, 25);

        // Effet de brillance pour les scores élevés
        if (score > 1000) {
            gc.setFill(Color.GOLD);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.fillText("★", 850, 20);
        }
        if (score > 5000) {
            gc.fillText("★★", 860, 20);
        }
        if (score > 10000) {
            gc.fillText("★★★", 870, 20);
        }
    }

    private void renderAlertMessages() {
        gc.setTextAlign(TextAlignment.RIGHT);

        if (gameTime <= 5 && gameTime > 0) {
            // Clignotement rapide pour les 5 dernières secondes
            if (System.currentTimeMillis() % 200 < 100) {
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                gc.fillText("⚠️ CRITIQUE!", 900, 25);

                // Effet de pulsation
                gc.setStroke(Color.RED);
                gc.setLineWidth(2);
                double pulseSize = 5 + 3 * Math.sin(System.currentTimeMillis() / 100.0);
                gc.strokeOval(800 - pulseSize, 15 - pulseSize, 100 + 2*pulseSize, 20 + 2*pulseSize);
            }
        } else if (gameTime <= 10 && gameTime > 5) {
            // Clignotement pour les 10 dernières secondes
            if (System.currentTimeMillis() % 500 < 250) {
                gc.setFill(Color.RED);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                gc.fillText("⚠️ DANGER!", 900, 25);
            }
        } else if (gameTime <= 30 && gameTime > 10) {
            gc.setFill(Color.ORANGE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            gc.fillText("⏰ Dépêchez-vous!", 900, 25);
        }

        gc.setTextAlign(TextAlignment.LEFT);
    }

    private void drawTimeProgressBar() {
        int barWidth = 120;
        int barHeight = 6;
        int barX = 10;
        int barY = 45;

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
    }

    private void renderAdditionalInfo() {
        // Contrôles d'aide
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 8));
        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("F3: Debug | ESC: Menu", 190, 55);

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

    // Getters
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