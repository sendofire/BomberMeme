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
            // Créer des pixels par défaut si l'image ne peut pas être chargée
            pixels = new int[SIZE * SIZE];
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = 0xFF000000; // Noir par défaut
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