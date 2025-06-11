package fr.amu.iut.graphique;

/**
 * Classe utilitaire pour tester le chargement des images
 * Ajoutez cette méthode dans votre classe principale pour diagnostiquer les problèmes
 */
public class ImageLoader {

    /**
     * Teste le chargement de toutes les images et affiche un rapport détaillé
     */
    public static void testAllImages() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎮 TEST DE CHARGEMENT DES IMAGES BOMBERMAN 🎮");
        System.out.println("=".repeat(60));

        // Test des images de terrain
        System.out.println("\n📍 TERRAIN:");
        testSprite("Sol", Sprite.sol);
        testSprite("Mur", Sprite.mur);
        testSprite("Brique", Sprite.brick);

        // Test des joueurs rouges
        System.out.println("\n🔴 JOUEUR ROUGE:");
        testSprite("Rouge Bas", Sprite.player_down_rouge);
        testSprite("Rouge Haut", Sprite.player_up_rouge);
        testSprite("Rouge Gauche", Sprite.palyer_left_rouge);
        testSprite("Rouge Droite", Sprite.palyer_right_rouge);

        // Test des joueurs bleus
        System.out.println("\n🔵 JOUEUR BLEU:");
        testSprite("Bleu Bas", Sprite.player_down_bleu);
        testSprite("Bleu Haut", Sprite.player_up_bleu);
        testSprite("Bleu Gauche", Sprite.palyer_left_bleu);
        testSprite("Bleu Droite", Sprite.palyer_right_bleu);

        // Test des joueurs verts
        System.out.println("\n🟢 JOUEUR VERT:");
        testSprite("Vert Bas", Sprite.player_down_vert);
        testSprite("Vert Haut", Sprite.player_up_vert);
        testSprite("Vert Gauche", Sprite.palyer_left_vert);
        testSprite("Vert Droite", Sprite.palyer_right_vert);

        // Test des joueurs jaunes
        System.out.println("\n🟡 JOUEUR JAUNE:");
        testSprite("Jaune Bas", Sprite.player_down_jaune);
        testSprite("Jaune Haut", Sprite.player_up_jaune);
        testSprite("Jaune Gauche", Sprite.palyer_left_jaune);
        testSprite("Jaune Droite", Sprite.palyer_right_jaune);

        // Test des bombes
        System.out.println("\n💣 BOMBES:");
        testSprite("Bombe 1", Sprite.bomb_1);
        testSprite("Bombe 2", Sprite.bomb_2);
        testSprite("Bombe 3", Sprite.bomb_3);

        // Test des explosions centrales
        System.out.println("\n💥 EXPLOSIONS CENTRALES:");
        testSprite("Centre 1", Sprite.Center_flameSegment_1);
        testSprite("Centre 2", Sprite.Center_flameSegment_2);
        testSprite("Centre 3", Sprite.Center_flameSegment_3);
        testSprite("Centre 4", Sprite.Center_flameSegment_4);

        // Test des power-ups
        System.out.println("\n⭐ POWER-UPS:");
        testSprite("Bomb Up", Sprite.bomb_Up);
        testSprite("Fire Up", Sprite.fire_Up);
        testSprite("Speed Up", Sprite.speed_Up);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ Test terminé ! Vérifiez les messages ci-dessus.");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * Teste un sprite individuel
     */
    private static void testSprite(String name, Sprite sprite) {
        try {
            if (sprite != null && sprite.getTexture() != null) {
                double width = sprite.getTexture().getWidth();
                double height = sprite.getTexture().getHeight();
                System.out.printf("  ✅ %-15s : %dx%d pixels%n", name, (int)width, (int)height);
            } else {
                System.out.printf("  ❌ %-15s : NULL%n", name);
            }
        } catch (Exception e) {
            System.out.printf("  ❌ %-15s : ERREUR - %s%n", name, e.getMessage());
        }
    }

    /**
     * Affiche des conseils de dépannage
     */
    public static void printTroubleshootingTips() {
        System.out.println("\n🔧 CONSEILS DE DÉPANNAGE:");
        System.out.println("━".repeat(50));
        System.out.println("1️⃣  Vérifiez que le dossier 'resources/fr/amu/iut/textures/' existe");
        System.out.println("2️⃣  Vérifiez que toutes les images PNG sont présentes");
        System.out.println("3️⃣  Vérifiez les noms des fichiers (sensible à la casse)");
        System.out.println("4️⃣  Si une image manque, une texture colorée sera générée automatiquement");
        System.out.println("5️⃣  Utilisez ImageLoaderTest.testAllImages() pour diagnostiquer");
        System.out.println("━".repeat(50));
    }
}