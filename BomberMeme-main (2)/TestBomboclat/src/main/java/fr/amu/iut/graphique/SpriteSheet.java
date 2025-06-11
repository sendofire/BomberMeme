package fr.amu.iut.graphique;

/**
 * Classe SpriteSheet simplifiée pour maintenir la compatibilité.
 * Maintenant, les sprites utilisent directement les images PNG individuelles.
 */
public class SpriteSheet {

    // Instance statique maintenue pour compatibilité
    public static SpriteSheet spriteSheet = new SpriteSheet();

    // Constantes maintenues pour compatibilité
    private static final int SIZE = 256;
    private int[] pixels;

    public SpriteSheet() {
        // Initialisation minimale - les pixels ne sont plus utilisés
        pixels = new int[SIZE * SIZE];
        System.out.println("SpriteSheet initialisée (mode compatibilité - images PNG individuelles utilisées)");
    }

    /**
     * Retourne les pixels (maintenu pour compatibilité, mais non utilisé)
     */
    public int[] getPixels() {
        return pixels;
    }

    /**
     * Retourne la taille (maintenu pour compatibilité)
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Méthode utilitaire pour vérifier le statut du système de sprites
     */
    public static void printStatus() {
        System.out.println("=== Statut du système de sprites ===");
        System.out.println("Mode: Images PNG individuelles");
        System.out.println("Répertoire: /fr/amu/iut/textures/");
        System.out.println("Facteur d'agrandissement: 3x");
        System.out.println("Taille finale des sprites: 48x48 pixels");
        System.out.println("=====================================");

        // Lister les images disponibles
        Sprite.listAvailableImages();
    }
}