package fr.amu.iut.graphique;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.Objects;

public class Sprite {
    private static final int SCALED_FACTOR = 3; // Facteur d'agrandissement
    private static final String TEXTURE_PATH = "/fr/amu/iut/textures/classique/"; // a modifier pour charger de style (style inclus : amongUS ou classique)

    private Image texture;
    private final String imagePath;

    /*
    |--------------------------------------------------------------------------
    | Board sprites
    |--------------------------------------------------------------------------
     */
    public static final Sprite sol = new Sprite("sol.png");
    public static final Sprite brick = new Sprite("crate.png");
    public static final Sprite mur = new Sprite("mur.png");

    /*
    |--------------------------------------------------------------------------
    | Bomber Sprites - Rouge
    |--------------------------------------------------------------------------
     */
    public static final Sprite player_down_rouge = new Sprite("red-down.png");
    public static final Sprite player_up_rouge = new Sprite("red-up.png");
    public static final Sprite palyer_left_rouge = new Sprite("red-left.png");
    public static final Sprite palyer_right_rouge = new Sprite("red-right.png");

    /*
    |--------------------------------------------------------------------------
    | Bomber Sprites - Bleu
    |--------------------------------------------------------------------------
     */
    public static final Sprite player_down_bleu = new Sprite("blue-down.png");
    public static final Sprite player_up_bleu = new Sprite("blue-up.png");
    public static final Sprite palyer_left_bleu = new Sprite("blue-left.png");
    public static final Sprite palyer_right_bleu = new Sprite("blue-right.png");

    /*
    |--------------------------------------------------------------------------
    | Bomber Sprites - Vert
    |--------------------------------------------------------------------------
     */
    public static final Sprite player_down_vert = new Sprite("green-down.png");
    public static final Sprite player_up_vert = new Sprite("green-up.png");
    public static final Sprite palyer_left_vert = new Sprite("green-left.png");
    public static final Sprite palyer_right_vert = new Sprite("green-right.png");

    /*
    |--------------------------------------------------------------------------
    | Bomber Sprites - Jaune
    |--------------------------------------------------------------------------
     */
    public static final Sprite player_down_jaune = new Sprite("yellow-down.png");
    public static final Sprite player_up_jaune = new Sprite("yellow-up.png");
    public static final Sprite palyer_left_jaune = new Sprite("yellow-left.png");
    public static final Sprite palyer_right_jaune = new Sprite("yellow-right.png");

    /*
    |--------------------------------------------------------------------------
    | Bomb Sprites (utilise les mêmes images en rotation ou par défaut)
    |--------------------------------------------------------------------------
     */
    public static final Sprite bomb_1 = new Sprite("bomb-1.png"); // Utilise crate en attendant
    public static final Sprite bomb_2 = new Sprite("bomb-2.png");
    public static final Sprite bomb_3 = new Sprite("bomb-3.png");

    /*
    |--------------------------------------------------------------------------
    | FlameSegment Sprites - Centre
    |--------------------------------------------------------------------------
     */
    public static final Sprite Center_flameSegment_1 = new Sprite("explosion-center-1.png");
    public static final Sprite Center_flameSegment_2 = new Sprite("explosion-center-2.png");
    public static final Sprite Center_flameSegment_3 = new Sprite("explosion-center-3.png");
    public static final Sprite Center_flameSegment_4 = new Sprite("explosion-center-4.png");

    /*
    |--------------------------------------------------------------------------
    | FlameSegment Sprites - Fins
    |--------------------------------------------------------------------------
     */
    public static final Sprite top_flameSegment_1 = new Sprite("explosion-top-1.png");
    public static final Sprite top_flameSegment_2 = new Sprite("explosion-top-2.png");
    public static final Sprite top_flameSegment_3 = new Sprite("explosion-top-3.png");
    public static final Sprite top_flameSegment_4 = new Sprite("explosion-top-4.png");

    public static final Sprite bottom_flameSegment_1 = new Sprite("explosion-bottom-1.png");
    public static final Sprite bottom_flameSegment_2 = new Sprite("explosion-bottom-2.png");
    public static final Sprite bottom_flameSegment_3 = new Sprite("explosion-bottom-3.png");
    public static final Sprite bottom_flameSegment_4 = new Sprite("explosion-bottom-4.png");

    public static final Sprite left_flameSegment_1 = new Sprite("explosion-right-top-1.png");
    public static final Sprite left_flameSegment_2 = new Sprite("explosion-right-top-2.png");
    public static final Sprite left_flameSegment_3 = new Sprite("explosion-right-top-3.png");
    public static final Sprite left_flameSegment_4 = new Sprite("explosion-right-top-4.png");

    public static final Sprite right_flameSegment_1 = new Sprite("explosion-last-1.png");
    public static final Sprite right_flameSegment_2 = new Sprite("explosion-last-2.png");
    public static final Sprite right_flameSegment_3 = new Sprite("explosion-last-3.png");
    public static final Sprite right_flameSegment_4 = new Sprite("explosion-last-4.png");

    /*
    |--------------------------------------------------------------------------
    | FlameSegment Sprites - Milieux
    |--------------------------------------------------------------------------
     */
    public static final Sprite center_top_flamSegment_1 = new Sprite("explosion-center-top-1.png");
    public static final Sprite center_top_flamSegment_2 = new Sprite("explosion-center-top-2.png");
    public static final Sprite center_top_flamSegment_3 = new Sprite("explosion-center-top-3.png");
    public static final Sprite center_top_flamSegment_4 = new Sprite("explosion-center-top-4.png");

    public static final Sprite center_bottom_flamSegment_1 = new Sprite("explosion-bottom-center-1.png");
    public static final Sprite center_bottom_flamSegment_2 = new Sprite("explosion-bottom-center-2.png");
    public static final Sprite center_bottom_flamSegment_3 = new Sprite("explosion-bottom-center-3.png");
    public static final Sprite center_bottom_flamSegment_4 = new Sprite("explosion-bottom-center-4.png");

    public static final Sprite center_left_flamSegment_1 = new Sprite("explosion-right-center-1.png");
    public static final Sprite center_left_flamSegment_2 = new Sprite("explosion-right-center-2.png");
    public static final Sprite center_left_flamSegment_3 = new Sprite("explosion-right-center-3.png");
    public static final Sprite center_left_flamSegment_4 = new Sprite("explosion-right-center-4.png");

    public static final Sprite center_right_flamSegment_1 = new Sprite("explosion-middle-1.png");
    public static final Sprite center_right_flamSegment_2 = new Sprite("explosion-middle-2.png");
    public static final Sprite center_right_flamSegment_3 = new Sprite("explosion-middle-3.png");
    public static final Sprite center_right_flamSegment_4 = new Sprite("explosion-middle-4.png");

    /*
    |--------------------------------------------------------------------------
    | Powerups
    |--------------------------------------------------------------------------
     */
    public static final Sprite bomb_Up = new Sprite("powerUP-bomb.png");
    public static final Sprite fire_Up = new Sprite("powerUP-explosion.png");
    public static final Sprite speed_Up = new Sprite("powerUP-speed.png");

    /**
     * Constructeur privé pour créer un sprite à partir d'un fichier image
     */
    private Sprite(String imageName) {
        this.imagePath = TEXTURE_PATH + imageName;
        loadTexture();
    }

    /**
     * Charge la texture depuis le fichier PNG
     */
    private void loadTexture() {
        try {
            Image originalImage = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)));

            if (originalImage.isError()) {
                throw new RuntimeException("Erreur lors du chargement de l'image: " + imagePath);
            }

            // Agrandir l'image
            this.texture = upscale(originalImage);
            System.out.println("Image chargée avec succès: " + imagePath);

        } catch (Exception e) {
            System.err.println("Impossible de charger l'image: " + imagePath);
            System.err.println("Erreur: " + e.getMessage());

            // Créer une texture de fallback colorée
            this.texture = createFallbackTexture();
        }
    }

    /**
     * Crée une texture de fallback en cas d'échec de chargement
     */
    private Image createFallbackTexture() {
        int size = 48; // Taille finale après agrandissement
        WritableImage fallback = new WritableImage(size, size);
        PixelWriter pixelWriter = fallback.getPixelWriter();

        // Couleur basée sur le nom de l'image
        int color = generateFallbackColor();

        // Créer un carré coloré simple
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                // Bordure noire
                if (x == 0 || y == 0 || x == size - 1 || y == size - 1) {
                    pixelWriter.setArgb(x, y, 0xFF000000);
                } else {
                    pixelWriter.setArgb(x, y, color);
                }
            }
        }

        System.out.println("Texture de fallback créée pour: " + imagePath);
        return fallback;
    }

    /**
     * Génère une couleur de fallback basée sur le nom de l'image
     */
    private int generateFallbackColor() {
        String name = imagePath.toLowerCase();

        // Couleurs spécifiques selon le type
        if (name.contains("red")) return 0xFFFF0000;
        if (name.contains("blue")) return 0xFF0000FF;
        if (name.contains("green")) return 0xFF00FF00;
        if (name.contains("yellow")) return 0xFFFFFF00;
        if (name.contains("explosion")) return 0xFFFF4500;
        if (name.contains("powerup")) return 0xFFFFD700;
        if (name.contains("crate")) return 0xFF8B4513;
        if (name.contains("mur")) return 0xFF808080;
        if (name.contains("sol")) return 0xFF90EE90;

        // Couleur par défaut
        return 0xFF888888;
    }

    /**
     * Agrandit l'image d'origine
     */
    private Image upscale(Image inputImage) {
        final int width = (int) inputImage.getWidth();
        final int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(
                width * SCALED_FACTOR,
                height * SCALED_FACTOR
        );

        PixelReader pixelReader = inputImage.getPixelReader();
        PixelWriter pixelWriter = outputImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int argb = pixelReader.getArgb(x, y);

                // Dupliquer le pixel selon le facteur d'agrandissement
                for (int scaledY = 0; scaledY < SCALED_FACTOR; scaledY++) {
                    for (int scaledX = 0; scaledX < SCALED_FACTOR; scaledX++) {
                        pixelWriter.setArgb(
                                x * SCALED_FACTOR + scaledX,
                                y * SCALED_FACTOR + scaledY,
                                argb
                        );
                    }
                }
            }
        }

        return outputImage;
    }

    /**
     * Retourne la texture agrandie et prête à l'usage
     */
    public Image getTexture() {
        return texture;
    }

    /**
     * Retourne la taille agrandie (utile pour le positionnement)
     */
    public static int getScaledSize() {
        return 16 * SCALED_FACTOR; // 48 pixels
    }

    /**
     * Méthode utilitaire pour vérifier si une image existe
     */
    public static boolean imageExists(String imageName) {
        try {
            return Sprite.class.getResourceAsStream(TEXTURE_PATH + imageName) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Méthode pour lister toutes les images disponibles (debug)
     */
    public static void listAvailableImages() {
        String[] imageNames = {
                "sol.png", "mur.png", "crate.png",
                "amongUs-red-down.png", "amongUs-red-up.png", "amongUs-red-left.png", "amongUs-red-right.png",
                "amongUs-blue-down.png", "amongUs-blue-up.png", "amongUs-blue-left.png", "amongUs-blue-right.png",
                "amongUs-green-down.png", "amongUs-green-up.png", "amongUs-green-left.png", "amongUs-green-right.png",
                "amongUs-yellow-down.png", "amongUs-yellow-up.png", "amongUs-yellow-left.png", "amongUs-yellow-right.png",
                "explosion-center-1.png", "explosion-center-2.png",
                "explosion-center-3.png", "explosion-center-4.png",
                "explosion-middle-1.png", "explosion-middle-2.png",
                "explosion-middle-3.png", "explosion-middle-4.png",
                "explosion-last-1.png", "explosion-last-2.png",
                "explosion-last-3.png", "explosion-last-4.png",
                "powerUP-bomb.png", "powerUP-explosion.png", "powerUP-speed.png"
        };

        System.out.println("=== Vérification des images disponibles ===");
        for (String imageName : imageNames) {
            boolean exists = imageExists(imageName);
            System.out.println(imageName + ": " + (exists ? "✓" : "✗"));
        }
        System.out.println("============================================");
    }
}