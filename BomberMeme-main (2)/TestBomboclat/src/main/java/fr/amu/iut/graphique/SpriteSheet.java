package fr.amu.iut.graphique;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class SpriteSheet {
    private static final String SPRITE_SHEET_PATH = "/textures/AmongUs-assets.png";
    private static final int SIZE = 256; // Taille de la spritesheet
    private int[] pixels;

    public static SpriteSheet spriteSheet = new SpriteSheet();

    public SpriteSheet() {
        load();
    }

    private void load() {
        try {
            Image image = new Image(getClass().getResourceAsStream(SPRITE_SHEET_PATH));
            int width = (int) image.getWidth();
            int height = (int) image.getHeight();
            pixels = new int[width * height];

            PixelReader pixelReader = image.getPixelReader();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    pixels[x + y * width] = pixelReader.getArgb(x, y);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la spritesheet: " + e.getMessage());
            // Créer des pixels par défaut avec des couleurs différentes pour chaque sprite
            pixels = new int[SIZE * SIZE];
            createDefaultSprites();
        }
    }

    private void createDefaultSprites() {
        // Créer des sprites colorés par défaut
        for (int spriteY = 0; spriteY < SIZE / 16; spriteY++) {
            for (int spriteX = 0; spriteX < SIZE / 16; spriteX++) {
                int color = generateSpriteColor(spriteX, spriteY);
                fillSpriteArea(spriteX * 16, spriteY * 16, 16, 16, color);
            }
        }
    }

    private int generateSpriteColor(int spriteX, int spriteY) {
        // Générer des couleurs spécifiques pour différents types de sprites
        if (spriteX == 5 && spriteY == 0) return 0xFF808080; // Sol - gris
        if (spriteX == 7 && spriteY == 0) return 0xFF8B4513; // Brique - marron
        if (spriteX == 6 && spriteY == 0) return 0xFF2F2F2F; // Mur - gris foncé
        if (spriteX == 4 && spriteY == 1) return 0xFF00FF00; // Porte - vert

        // Joueurs colorés
        if (spriteY == 0) return 0xFFFF0000; // Rouge
        if (spriteY == 1) return 0xFF0000FF; // Bleu
        if (spriteY == 2) return 0xFF00FF00; // Vert
        if (spriteY == 3) return 0xFFFFFF00; // Jaune

        // Couleur par défaut
        return 0xFF000000 | ((spriteX * 50) << 16) | ((spriteY * 50) << 8) | ((spriteX + spriteY) * 30);
    }

    private void fillSpriteArea(int startX, int startY, int width, int height, int color) {
        for (int y = startY; y < startY + height && y < SIZE; y++) {
            for (int x = startX; x < startX + width && x < SIZE; x++) {
                if (x >= 0 && y >= 0) {
                    pixels[x + y * SIZE] = color;
                }
            }
        }
    }

    public int[] getPixels() {
        return pixels;
    }

    public int getSize() {
        return SIZE;
    }
}