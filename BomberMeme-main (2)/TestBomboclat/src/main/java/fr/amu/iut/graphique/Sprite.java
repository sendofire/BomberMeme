package fr.amu.iut.graphique;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import static fr.amu.iut.graphique.SpriteSheet.spriteSheet;

public class Sprite {
    private static final int DEFAULT_SIZE = 16;
    private static final int SCALED_FACTOR = 3; //times the default size
    private static final int SCALED_SIZE = DEFAULT_SIZE * SCALED_FACTOR;
    private static final int TRANSPARENT = 0xffff00ff;
    private final int[] pixels;
    private final int X;
    private final int Y;

    /*
	|--------------------------------------------------------------------------
	| Board sprites
	|--------------------------------------------------------------------------
	 */
    public static final Sprite sol = new Sprite( 5, 0);
    public static final Sprite brick = new Sprite( 7, 0);
    public static final Sprite brick2 = new Sprite( 7, 1);
    public static final Sprite mur = new Sprite( 6, 0);
    public static final Sprite porte = new Sprite(4 , 1);

    /*
    |--------------------------------------------------------------------------
    | Bomber Sprites
    |--------------------------------------------------------------------------
     */

    public static final Sprite player_down_rouge = new Sprite(0, 0);
    public static final Sprite player_up_rouge = new Sprite(1, 0);
    public static final Sprite palyer_left_rouge = new Sprite(2, 0);
    public static final Sprite palyer_right_rouge = new Sprite(3, 0);

    public static final Sprite player_down_bleu = new Sprite(0, 1);
    public static final Sprite player_up_bleu = new Sprite(1, 1);
    public static final Sprite palyer_left_bleu = new Sprite(2, 1);
    public static final Sprite palyer_right_bleu = new Sprite(3, 1);

    public static final Sprite player_down_vert = new Sprite(0, 2);
    public static final Sprite player_up_vert = new Sprite(1, 2);
    public static final Sprite palyer_left_vert = new Sprite(2, 2);
    public static final Sprite palyer_right_vert = new Sprite(3, 2);

    public static final Sprite player_down_jaune = new Sprite(0, 3);
    public static final Sprite player_up_jaune = new Sprite(1, 3);
    public static final Sprite palyer_left_jaune = new Sprite(2, 3);
    public static final Sprite palyer_right_jaune = new Sprite(3, 3);

    /*
    |--------------------------------------------------------------------------
    | Bomb Sprites
    |--------------------------------------------------------------------------
     */



    /*
    |--------------------------------------------------------------------------
    | FlameSegment Sprites
    |--------------------------------------------------------------------------
     */



    /*
    |--------------------------------------------------------------------------
    | Powerups
    |--------------------------------------------------------------------------
     */



    private Sprite(int x, int y) {
        X = x * DEFAULT_SIZE;
        Y = y * DEFAULT_SIZE;
        pixels = new int[DEFAULT_SIZE * DEFAULT_SIZE];
        load();
    }

    public static int getScaledSize() {
        return SCALED_SIZE;
    }

    private void load() {
        for (int y = 0; y < DEFAULT_SIZE; ++y) {
            System.arraycopy(spriteSheet.getPixels(), X + (y + Y) * spriteSheet.getSize(), pixels, y * 16, DEFAULT_SIZE);
        }
    }

    /**
     * Return an upscaled texture.
     */
    public Image getTexture() {
        WritableImage image = new WritableImage(DEFAULT_SIZE, DEFAULT_SIZE);
        PixelWriter pixelWriter = image.getPixelWriter();

        for (int x = 0; x < DEFAULT_SIZE; ++x) {
            for (int y = 0; y < DEFAULT_SIZE; ++y) {
                if (pixels[x + y * DEFAULT_SIZE] == TRANSPARENT) {
                    pixelWriter.setArgb(x, y, 0);
                } else {
                    pixelWriter.setArgb(x, y, pixels[x + y * DEFAULT_SIZE]);
                }
            }
        }

        return upscale(image);
    }

    /**
     * Upscale image.
     * @param inputImage input image
     * @return An upscaled image
     */
    private Image upscale(WritableImage inputImage) {
        final int width = (int) inputImage.getWidth();
        final int height = (int) inputImage.getHeight();

        WritableImage outputImage = new WritableImage(width * SCALED_FACTOR, height * SCALED_FACTOR);
        PixelReader pixelReader = inputImage.getPixelReader();
        PixelWriter pixelWriter = outputImage.getPixelWriter();

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int argb = pixelReader.getArgb(x, y);
                for (int scaledY = 0; scaledY < SCALED_FACTOR; ++scaledY) {
                    for (int scaledX = 0; scaledX < SCALED_FACTOR; ++scaledX) {
                        pixelWriter.setArgb(x * SCALED_FACTOR + scaledX, y * SCALED_FACTOR + scaledY, argb);
                    }
                }
            }
        }

        return outputImage;
    }
}
