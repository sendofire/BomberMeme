package fr.amu.iut;

import fr.amu.iut.Personnages.JoueurMultiplayer;
import fr.amu.iut.Objets.Bombes;
import fr.amu.iut.Objets.PowerUps;
import fr.amu.iut.Obstacle.Explosions;
import fr.amu.iut.graphique.Sprite;
import fr.amu.iut.graphique.Banniere;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Jeu implements Initializable {

    @FXML
    private AnchorPane gamePane;

    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;
    private Banniere gameBanner;

    // Constantes du jeu
    private static final int GRID_WIDTH = 15;
    private static final int GRID_HEIGHT = 13;
    private static final int TILE_SIZE = 48;
    private static final int CANVAS_WIDTH = GRID_WIDTH * TILE_SIZE;
    private static final int CANVAS_HEIGHT = GRID_HEIGHT * TILE_SIZE + 60; // +60 pour la bannière
    private static final int BANNER_HEIGHT = 60;

    // État du jeu
    private int[][] gameBoard;
    private List<JoueurMultiplayer> players;
    private List<Bombes> bombs;
    private List<Explosions> explosions;
    private List<PowerUps> powerUps;
    private Set<KeyCode> pressedKeys;
    private Random random;
    private GameState gameState;
    private int gameScore;

    // Types de cases
    private static final int EMPTY = 0;
    private static final int WALL = 1;
    private static final int BRICK = 2;
    private static final int BOMB = 3;
    private static final int EXPLOSION = 4;
    private static final int POWERUP = 5;

    // États du jeu
    private enum GameState {
        PLAYING, GAME_OVER, PLAYER_WON
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeCanvas();
        initializeGame();
        setupKeyHandlers();
        startGameLoop();

    }

    private void initializeCanvas() {
        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        gc = canvas.getGraphicsContext2D();
        gameBanner = new Banniere(gc);
        gamePane.getChildren().add(canvas);

        // Centrer le canvas
        canvas.setLayoutX((800 - CANVAS_WIDTH) / 2);
        canvas.setLayoutY((600 - CANVAS_HEIGHT) / 2);
    }

    private void initializeGame() {
        // Initialiser les collections
        players = new ArrayList<>();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        powerUps = new ArrayList<>();
        pressedKeys = ConcurrentHashMap.newKeySet();
        random = new Random();
        gameState = GameState.PLAYING;
        gameScore = 0;

        // Créer le plateau de jeu
        createGameBoard();

        // Créer les joueurs
        createPlayers();

        // Démarrer le timer
        gameBanner.setGameTime(180); // 3 minutes
        gameBanner.startTimer();
    }

    private void createGameBoard() {
        gameBoard = new int[GRID_HEIGHT][GRID_WIDTH];

        // Remplir avec des murs indestructibles sur les bords et en damier
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (x == 0 || y == 0 || x == GRID_WIDTH - 1 || y == GRID_HEIGHT - 1) {
                    gameBoard[y][x] = WALL;
                } else if (x % 2 == 0 && y % 2 == 0) {
                    gameBoard[y][x] = WALL;
                } else {
                    // Zones de départ des joueurs (coins)
                    if ((x <= 2 && y <= 2) || (x >= GRID_WIDTH - 3 && y <= 2) ||
                            (x <= 2 && y >= GRID_HEIGHT - 3) || (x >= GRID_WIDTH - 3 && y >= GRID_HEIGHT - 3)) {
                        gameBoard[y][x] = EMPTY;
                    } else {
                        // Placer des briques destructibles aléatoirement
                        if (random.nextDouble() < 0.7) {
                            gameBoard[y][x] = BRICK;
                        } else {
                            gameBoard[y][x] = EMPTY;
                        }
                    }
                }
            }
        }
    }

    private void createPlayers() {
        // Joueur 1 (Rouge) - coin supérieur gauche
        JoueurMultiplayer player1 = new JoueurMultiplayer(1, 1, 1, "Rouge");
        player1.setControls("Z", "S", "Q", "D", "SPACE");
        players.add(player1);

        // Joueur 2 (Bleu) - coin supérieur droit
        JoueurMultiplayer player2 = new JoueurMultiplayer(2, GRID_WIDTH - 2, 1, "Bleu");
        player2.setControls("UP", "DOWN", "LEFT", "RIGHT", "ENTER");
        players.add(player2);

        // Joueur 3 (Vert) - coin inférieur gauche
        JoueurMultiplayer player3 = new JoueurMultiplayer(3, 1, GRID_HEIGHT - 2, "Vert");
        player3.setControls("T", "G", "F", "H", "R");
        players.add(player3);

        // Joueur 4 (Jaune) - coin inférieur droit
        JoueurMultiplayer player4 = new JoueurMultiplayer(4, GRID_WIDTH - 2, GRID_HEIGHT - 2, "Jaune");
        player4.setControls("I", "K", "J", "L", "U");
        players.add(player4);
    }

    private void setupKeyHandlers() {
        gamePane.setOnKeyPressed(this::handleKeyPressed);
        gamePane.setOnKeyReleased(this::handleKeyReleased);
        gamePane.setFocusTraversable(true);
        gamePane.requestFocus();
    }

    private void handleKeyPressed(KeyEvent event) {
        pressedKeys.add(event.getCode());

        // Gestion de la touche Escape pour retourner au menu
        if (event.getCode() == KeyCode.ESCAPE) {
            stopGame();
            // Ici vous pourriez ajouter la logique pour retourner au menu
        }
    }

    private void handleKeyReleased(KeyEvent event) {
        pressedKeys.remove(event.getCode());
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private long lastPlayerUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_000_000) { // 60 FPS
                    if (now - lastPlayerUpdate >= 100_000_000) { // Mouvement joueur plus lent
                        updatePlayers();
                        lastPlayerUpdate = now;
                    }
                    update();
                    render();
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void update() {
        if (gameState != GameState.PLAYING) return;

        updateBombs();
        updateExplosions();
        updatePowerUps();
        checkCollisions();
        checkGameEnd();
    }

    private void updatePlayers() {
        if (gameState != GameState.PLAYING) return;

        for (JoueurMultiplayer player : players) {
            if (!player.isAlive()) continue;

            updatePlayerMovement(player);
            updatePlayerBomb(player);
        }
    }

    private void updatePlayerMovement(JoueurMultiplayer player) {
        int newX = player.getX();
        int newY = player.getY();
        String newDirection = player.getDirection();

        // Vérifier les touches de mouvement en utilisant KeyCode
        if (isKeyPressed(player.getUpKey())) {
            newY--;
            newDirection = "UP";
        }
        if (isKeyPressed(player.getDownKey())) {
            newY++;
            newDirection = "DOWN";
        }
        if (isKeyPressed(player.getLeftKey())) {
            newX--;
            newDirection = "LEFT";
        }
        if (isKeyPressed(player.getRightKey())) {
            newX++;
            newDirection = "RIGHT";
        }

        // Vérifier si le mouvement est valide
        if (canMoveTo(newX, newY)) {
            player.setX(newX);
            player.setY(newY);
            player.setDirection(newDirection);

            // Vérifier si le joueur ramasse un powerup
            checkPowerUpCollection(player);
        }
    }

    private void updatePlayerBomb(JoueurMultiplayer player) {
        if (isKeyPressed(player.getBombKey()) && !player.isBombKeyPressed()) {
            player.setBombKeyPressed(true);
            placeBomb(player);
        } else if (!isKeyPressed(player.getBombKey())) {
            player.setBombKeyPressed(false);
        }
    }

    private boolean isKeyPressed(String keyString) {
        try {
            KeyCode keyCode = KeyCode.valueOf(keyString);
            return pressedKeys.contains(keyCode);
        } catch (IllegalArgumentException e) {
            // Pour les touches qui ne correspondent pas directement à KeyCode
            switch (keyString) {
                case "Z": return pressedKeys.contains(KeyCode.Z);
                case "S": return pressedKeys.contains(KeyCode.S);
                case "Q": return pressedKeys.contains(KeyCode.Q);
                case "D": return pressedKeys.contains(KeyCode.D);
                case "T": return pressedKeys.contains(KeyCode.T);
                case "G": return pressedKeys.contains(KeyCode.G);
                case "F": return pressedKeys.contains(KeyCode.F);
                case "H": return pressedKeys.contains(KeyCode.H);
                case "R": return pressedKeys.contains(KeyCode.R);
                case "I": return pressedKeys.contains(KeyCode.I);
                case "K": return pressedKeys.contains(KeyCode.K);
                case "J": return pressedKeys.contains(KeyCode.J);
                case "L": return pressedKeys.contains(KeyCode.L);
                case "U": return pressedKeys.contains(KeyCode.U);
                default: return false;
            }
        }
    }

    private boolean canMoveTo(int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) {
            return false;
        }
        // Les joueurs peuvent marcher sur les power-ups et les cases vides
        return gameBoard[y][x] == EMPTY || gameBoard[y][x] == POWERUP;
    }

    private void placeBomb(JoueurMultiplayer player) {
        int x = player.getX();
        int y = player.getY();

        // Vérifier si le joueur peut placer une bombe
        if (!player.peutPlacerBombe(countPlayerBombs(player))) {
            return;
        }

        // Vérifier qu'il n'y a pas déjà une bombe à cette position
        for (Bombes bomb : bombs) {
            if (bomb.getX() == x && bomb.getY() == y) {
                return;
            }
        }

        Bombes newBomb = new Bombes(x, y, player.getPorteBombe(), 3000);
        newBomb.setOwnerId(player.getIdJ());
        bombs.add(newBomb);
        gameBoard[y][x] = BOMB;

        // Programmer l'explosion de la bombe
        Timeline bombTimer = new Timeline(new KeyFrame(Duration.seconds(3), e -> explodeBomb(newBomb)));
        bombTimer.play();
    }

    private int countPlayerBombs(JoueurMultiplayer player) {
        int count = 0;
        for (Bombes bomb : bombs) {
            if (bomb.getOwnerId() == player.getIdJ()) {
                count++;
            }
        }
        return count;
    }

    private void explodeBomb(Bombes bomb) {
        bombs.remove(bomb);
        if (bomb.getY() >= 0 && bomb.getY() < GRID_HEIGHT &&
                bomb.getX() >= 0 && bomb.getX() < GRID_WIDTH) {
            gameBoard[bomb.getY()][bomb.getX()] = EMPTY;
        }

        // Créer l'explosion au centre
        createExplosion(bomb.getX(), bomb.getY());

        // Créer les flammes dans les 4 directions
        int range = bomb.getRange();
        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};

        for (int dir = 0; dir < 4; dir++) {
            for (int i = 1; i <= range; i++) {
                int x = bomb.getX() + dx[dir] * i;
                int y = bomb.getY() + dy[dir] * i;

                if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) break;
                if (gameBoard[y][x] == WALL) break;

                createExplosion(x, y);

                if (gameBoard[y][x] == BRICK) {
                    gameBoard[y][x] = EMPTY;
                    // Chance de créer un powerup (50% de chance)
                    if (random.nextDouble() < 0.5) {
                        createPowerUp(x, y);
                    }
                    break;
                }
            }
        }
    }

    private void createExplosion(int x, int y) {
        if (x < 0 || y < 0 || x >= GRID_WIDTH || y >= GRID_HEIGHT) return;

        Explosions explosion = new Explosions(x, y);
        explosions.add(explosion);

        // Détruire un power-up s'il y en a un à cette position
        powerUps.removeIf(powerUp -> powerUp.getX() == x && powerUp.getY() == y);

        if (gameBoard[y][x] != WALL) {
            gameBoard[y][x] = EXPLOSION;
        }

        // Programmer la fin de l'explosion
        Timeline explosionEnd = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            explosions.remove(explosion);
            if (gameBoard[y][x] == EXPLOSION) {
                gameBoard[y][x] = EMPTY;
            }
        }));
        explosionEnd.play();
    }

    private void createPowerUp(int x, int y) {
        PowerUps.PowerupType[] types = PowerUps.PowerupType.values();
        PowerUps.PowerupType randomType = types[random.nextInt(types.length)];

        PowerUps powerUp = new PowerUps(x, y, randomType);
        powerUps.add(powerUp);
        gameBoard[y][x] = POWERUP;

        System.out.println("Power-up créé à (" + x + ", " + y + ") de type: " + randomType);
    }

    private void checkPowerUpCollection(JoueurMultiplayer player) {
        Iterator<PowerUps> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUps powerUp = iterator.next();
            if (powerUp.getX() == player.getX() && powerUp.getY() == player.getY() && !powerUp.isCollected()) {
                powerUp.collect();
                applyPowerUp(player, powerUp);
                iterator.remove();
                gameBoard[powerUp.getY()][powerUp.getX()] = EMPTY;
                gameScore += powerUp.getPoints();

                System.out.println("Joueur " + player.getCouleur() + " a ramassé un power-up de type: " + powerUp.getType());
            }
        }
    }

    private void applyPowerUp(JoueurMultiplayer player, PowerUps powerUp) {
        switch (powerUp.getType()) {
            case BOMB_RANGE:
                player.ameliorerPortee();
                System.out.println("Portée des bombes améliorée pour " + player.getCouleur() + " : " + player.getPorteBombe());
                break;
            case BOMB_COUNT:
                player.ameliorerNombreBombes();
                System.out.println("Nombre de bombes amélioré pour " + player.getCouleur() + " : " + player.getNombreBombes());
                break;
            case SPEED_UP:
                player.ameliorerVitesse();
                System.out.println("Vitesse améliorée pour " + player.getCouleur() + " : " + player.getVitesse());
                break;
            case EXTRA_LIFE:
                player.gagnerVie();
                System.out.println("Vie supplémentaire pour " + player.getCouleur() + " : " + player.getVies());
                break;
            case INVINCIBILITY:
                player.setInvincible(true, 5000); // 5 secondes
                System.out.println("Invincibilité temporaire pour " + player.getCouleur());
                break;
        }
    }

    private void updateBombs() {
        // Les bombes sont gérées par leurs timers individuels
    }

    private void updateExplosions() {
        // Les explosions sont gérées par leurs timers individuels
    }

    private void updatePowerUps() {
        Iterator<PowerUps> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUps powerUp = iterator.next();
            if (powerUp.isExpired()) {
                iterator.remove();
                if (gameBoard[powerUp.getY()][powerUp.getX()] == POWERUP) {
                    gameBoard[powerUp.getY()][powerUp.getX()] = EMPTY;
                }
                System.out.println("Power-up expiré à (" + powerUp.getX() + ", " + powerUp.getY() + ")");
            }
        }
    }

    private void checkCollisions() {
        // Vérifier collision avec les explosions
        for (JoueurMultiplayer player : players) {
            if (!player.isAlive() || player.isInvincible()) continue;

            for (Explosions explosion : explosions) {
                if (player.getX() == explosion.getX() && player.getY() == explosion.getY()) {
                    player.perdreVie();
                    if (!player.isAlive()) {
                        System.out.println("Player " + player.getCouleur() + " eliminated!");
                    }
                }
            }
        }
    }

    private void checkGameEnd() {
        // Vérifier si le temps est écoulé
        if (gameBanner.isTimeUp()) {
            gameState = GameState.GAME_OVER;
            return;
        }

        // Compter les joueurs vivants
        long alivePlayers = players.stream().filter(JoueurMultiplayer::isAlive).count();

        if (alivePlayers <= 1) {
            gameState = GameState.PLAYER_WON;
        }
    }

    private void render() {
        // Nettoyer l'écran
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Dessiner la bannière
        renderBanner();

        // Décaler le rendu du jeu sous la bannière
        gc.save();
        gc.translate(0, BANNER_HEIGHT);

        // Dessiner le plateau avec sprites
        renderBoard();

        // Dessiner les powerups avec sprites (AVANT les bombes et joueurs)
        renderPowerUps();

        // Dessiner les bombes avec sprites
        renderBombs();

        // Dessiner les explosions avec sprites
        renderExplosions();

        // Dessiner les joueurs avec sprites (EN DERNIER pour qu'ils soient au-dessus)
        renderPlayers();

        gc.restore();

        // Dessiner l'interface de fin de jeu
        renderGameOverScreen();
    }

    private void renderBanner() {
        int alivePlayers = (int) players.stream().filter(JoueurMultiplayer::isAlive).count();
        int totalBombs = bombs.size();
        int totalPowerUps = powerUps.size();

        gameBanner.render(alivePlayers, totalBombs, totalPowerUps, gameScore);
    }

    private void renderBoard() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                int tileX = x * TILE_SIZE;
                int tileY = y * TILE_SIZE;

                // Toujours dessiner le sol en premier
                gc.drawImage(Sprite.sol.getTexture(), tileX, tileY);

                // Puis dessiner les éléments par-dessus selon le type
                switch (gameBoard[y][x]) {
                    case WALL:
                        gc.drawImage(Sprite.mur.getTexture(), tileX, tileY);
                        break;
                    case BRICK:
                        gc.drawImage(Sprite.brick.getTexture(), tileX, tileY);
                        break;
                    // Les cases EMPTY, POWERUP, BOMB et EXPLOSION sont gérées ailleurs
                }
            }
        }
    }

    private void renderPowerUps() {
        for (PowerUps powerUp : powerUps) {
            if (!powerUp.isCollected()) {
                int x = powerUp.getX() * TILE_SIZE;
                int y = powerUp.getY() * TILE_SIZE;

                // Le sol est déjà dessiné dans renderBoard()

                // Dessiner le sprite du power-up selon son type
                Sprite powerUpSprite = getPowerUpSprite(powerUp.getType());
                if (powerUpSprite != null) {
                    gc.drawImage(powerUpSprite.getTexture(), x, y);
                }

                // Effet de brillance/pulsation
                double opacity = 0.2 + 0.2 * Math.sin(System.currentTimeMillis() / 300.0);
                gc.setGlobalAlpha(opacity);
                gc.setFill(Color.WHITE);
                gc.fillRect(x + 5, y + 5, TILE_SIZE - 10, TILE_SIZE - 10);
                gc.setGlobalAlpha(1.0);
            }
        }
    }

    private Sprite getPowerUpSprite(PowerUps.PowerupType type) {
        switch (type) {
            case BOMB_RANGE:
                return Sprite.fire_Up;
            case BOMB_COUNT:
                return Sprite.bomb_Up;
            case SPEED_UP:
                return Sprite.speed_Up;
            default:
                return Sprite.bomb_Up; // fallback
        }
    }

    private void renderBombs() {
        for (Bombes bomb : bombs) {
            int x = bomb.getX() * TILE_SIZE;
            int y = bomb.getY() * TILE_SIZE;

            // Le sol est déjà dessiné dans renderBoard()

            // Animation de la bombe selon le temps restant
            long timeLeft = bomb.getTimeRemaining();
            Sprite bombSprite;

            if (timeLeft > 2000) {
                bombSprite = Sprite.bomb_1;
            } else if (timeLeft > 1000) {
                bombSprite = Sprite.bomb_2;
            } else {
                bombSprite = Sprite.bomb_3;
            }

            // Effet de clignotement quand la bombe va exploser
            if (timeLeft <= 1000 && System.currentTimeMillis() % 200 < 100) {
                gc.setGlobalAlpha(0.5);
            }

            gc.drawImage(bombSprite.getTexture(), x, y);
            gc.setGlobalAlpha(1.0);

        }
    }

    private void renderExplosions() {
        for (Explosions explosion : explosions) {
            int x = explosion.getX() * TILE_SIZE;
            int y = explosion.getY() * TILE_SIZE;

            // Le sol est déjà dessiné dans renderBoard()

            // Déterminer le type d'explosion et utiliser les sprites appropriés
            Sprite explosionSprite = determineExplosionSprite(explosion);

            // Effet de scintillement
            double opacity = 0.7 + 0.3 * Math.sin(System.currentTimeMillis() / 50.0);
            gc.setGlobalAlpha(opacity);

            gc.drawImage(explosionSprite.getTexture(), x, y);

            gc.setGlobalAlpha(1.0);
        }
    }

    private Sprite determineExplosionSprite(Explosions explosion) {
        int x = explosion.getX();
        int y = explosion.getY();

        // Vérifier les explosions adjacentes pour déterminer le type
        boolean hasLeft = explosions.stream().anyMatch(exp -> exp.getX() == x - 1 && exp.getY() == y);
        boolean hasRight = explosions.stream().anyMatch(exp -> exp.getX() == x + 1 && exp.getY() == y);
        boolean hasUp = explosions.stream().anyMatch(exp -> exp.getX() == x && exp.getY() == y - 1);
        boolean hasDown = explosions.stream().anyMatch(exp -> exp.getX() == x && exp.getY() == y + 1);

        // Animation des flammes (cycle entre 4 sprites)
        long time = System.currentTimeMillis();
        int animFrame = (int) ((time / 100) % 4) + 1; // 1-4

        // Déterminer le type d'explosion et retourner le sprite approprié
        if (!hasLeft && !hasRight && !hasUp && !hasDown) {
            // Centre d'explosion (pas d'adjacents)
            switch (animFrame) {
                case 1: return Sprite.Center_flameSegment_1;
                case 2: return Sprite.Center_flameSegment_2;
                case 3: return Sprite.Center_flameSegment_3;
                case 4: return Sprite.Center_flameSegment_4;
            }
        } else if ((hasLeft || hasRight) && !hasUp && !hasDown) {
            // Explosion horizontale
            if (!hasLeft) { // Fin gauche
                switch (animFrame) {
                    case 1: return Sprite.left_flameSegment_1;
                    case 2: return Sprite.left_flameSegment_2;
                    case 3: return Sprite.left_flameSegment_3;
                    case 4: return Sprite.left_flameSegment_4;
                }
            } else if (!hasRight) { // Fin droite
                switch (animFrame) {
                    case 1: return Sprite.right_flameSegment_1;
                    case 2: return Sprite.right_flameSegment_2;
                    case 3: return Sprite.right_flameSegment_3;
                    case 4: return Sprite.right_flameSegment_4;
                }
            } else { // Milieu horizontal
                switch (animFrame) {
                    case 1: return Sprite.center_left_flamSegment_1;
                    case 2: return Sprite.center_left_flamSegment_2;
                    case 3: return Sprite.center_left_flamSegment_3;
                    case 4: return Sprite.center_left_flamSegment_4;
                }
            }
        } else if ((hasUp || hasDown) && !hasLeft && !hasRight) {
            // Explosion verticale
            if (!hasUp) { // Fin haut
                switch (animFrame) {
                    case 1: return Sprite.top_flameSegment_1;
                    case 2: return Sprite.top_flameSegment_2;
                    case 3: return Sprite.top_flameSegment_3;
                    case 4: return Sprite.top_flameSegment_4;
                }
            } else if (!hasDown) { // Fin bas
                switch (animFrame) {
                    case 1: return Sprite.bottom_flameSegment_1;
                    case 2: return Sprite.bottom_flameSegment_2;
                    case 3: return Sprite.bottom_flameSegment_3;
                    case 4: return Sprite.bottom_flameSegment_4;
                }
            } else { // Milieu vertical
                switch (animFrame) {
                    case 1: return Sprite.center_top_flamSegment_1;
                    case 2: return Sprite.center_top_flamSegment_2;
                    case 3: return Sprite.center_top_flamSegment_3;
                    case 4: return Sprite.center_top_flamSegment_4;
                }
            }
        } else {
            // Explosion en croix (centre avec connexions dans plusieurs directions)
            switch (animFrame) {
                case 1: return Sprite.Center_flameSegment_1;
                case 2: return Sprite.Center_flameSegment_2;
                case 3: return Sprite.Center_flameSegment_3;
                case 4: return Sprite.Center_flameSegment_4;
            }
        }

        // Par défaut, retourner explosion au centre
        return Sprite.Center_flameSegment_1;
    }

    private void renderPlayers() {
        for (JoueurMultiplayer player : players) {
            if (!player.isAlive()) continue;

            int x = player.getX() * TILE_SIZE;
            int y = player.getY() * TILE_SIZE;

            // Effet de clignotement si invincible
            if (player.isInvincible() && System.currentTimeMillis() % 200 < 100) {
                continue;
            }

            // Choisir le sprite selon la couleur et la direction
            Sprite sprite = getPlayerSprite(player);
            if (sprite != null) {
                gc.drawImage(sprite.getTexture(), x, y);
            }

            // Afficher les informations du joueur si en mode debug
            if (isDebugMode()) {
                renderPlayerInfo(player, x, y);
            }
        }
    }

    private boolean isDebugMode() {
        // Activer le mode debug en appuyant sur F3
        return pressedKeys.contains(KeyCode.F3);
    }

    private void renderPlayerInfo(JoueurMultiplayer player, int x, int y) {
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 8));
        gc.setTextAlign(TextAlignment.LEFT);

        // Afficher les stats du joueur au-dessus de lui
        String info = String.format("V:%d B:%d P:%d",
                player.getVies(), player.getNombreBombes(), player.getPorteBombe());
        gc.fillText(info, x, y - 5);
    }

    private Sprite getPlayerSprite(JoueurMultiplayer player) {
        String couleur = player.getCouleur();
        String direction = player.getDirection();

        switch (couleur) {
            case "Rouge":
                return switch (direction) {
                    case "UP" -> Sprite.player_up_rouge;
                    case "DOWN" -> Sprite.player_down_rouge;
                    case "LEFT" -> Sprite.palyer_left_rouge;
                    case "RIGHT" -> Sprite.palyer_right_rouge;
                    default -> Sprite.player_down_rouge;
                };
            case "Bleu":
                switch (direction) {
                    case "UP": return Sprite.player_up_bleu;
                    case "DOWN": return Sprite.player_down_bleu;
                    case "LEFT": return Sprite.palyer_left_bleu;
                    case "RIGHT": return Sprite.palyer_right_bleu;
                    default: return Sprite.player_down_bleu;
                }
            case "Vert":
                switch (direction) {
                    case "UP": return Sprite.player_up_vert;
                    case "DOWN": return Sprite.player_down_vert;
                    case "LEFT": return Sprite.palyer_left_vert;
                    case "RIGHT": return Sprite.palyer_right_vert;
                    default: return Sprite.player_down_vert;
                }
            case "Jaune":
                switch (direction) {
                    case "UP": return Sprite.player_up_jaune;
                    case "DOWN": return Sprite.player_down_jaune;
                    case "LEFT": return Sprite.palyer_left_jaune;
                    case "RIGHT": return Sprite.palyer_right_jaune;
                    default: return Sprite.player_down_jaune;
                }
            default:
                return Sprite.player_down_rouge; // Défaut
        }
    }

    private void renderGameOverScreen() {
        if (gameState == GameState.GAME_OVER || gameState == GameState.PLAYER_WON) {
            // Overlay semi-transparent
            gc.setFill(Color.BLACK);
            gc.setGlobalAlpha(0.8);
            gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            gc.setGlobalAlpha(1.0);

            // Titre principal
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 42));
            gc.setTextAlign(TextAlignment.CENTER);

            String titleText;
            if (gameState == GameState.GAME_OVER) {
                titleText = "TEMPS ÉCOULÉ!";
            } else {
                Optional<JoueurMultiplayer> winner = players.stream()
                        .filter(JoueurMultiplayer::isAlive)
                        .findFirst();

                if (winner.isPresent()) {
                    titleText = "JOUEUR " + winner.get().getCouleur().toUpperCase() + " GAGNE!";
                    // Couleur spéciale pour le gagnant
                    switch (winner.get().getCouleur()) {
                        case "Rouge": gc.setFill(Color.RED); break;
                        case "Bleu": gc.setFill(Color.BLUE); break;
                        case "Vert": gc.setFill(Color.LIME); break;
                        case "Jaune": gc.setFill(Color.YELLOW); break;
                    }
                } else {
                    titleText = "ÉGALITÉ!";
                }
            }

            gc.fillText(titleText, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 - 40);

            // Score final
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 20));
            gc.fillText("Score final: " + String.format("%,d", gameScore),
                    CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2);

            // Statistiques des joueurs
            gc.setFont(new Font("Arial", 14));
            int yOffset = 40;
            for (JoueurMultiplayer player : players) {
                String playerStats = String.format("%s - Vies: %d | Bombes: %d | Portée: %d",
                        player.getCouleur(), player.getVies(), player.getNombreBombes(), player.getPorteBombe());

                // Couleur du joueur
                switch (player.getCouleur()) {
                    case "Rouge": gc.setFill(Color.RED); break;
                    case "Bleu": gc.setFill(Color.CYAN); break;
                    case "Vert": gc.setFill(Color.LIME); break;
                    case "Jaune": gc.setFill(Color.YELLOW); break;
                    default: gc.setFill(Color.WHITE);
                }

                gc.fillText(playerStats, CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + yOffset);
                yOffset += 20;
            }

            // Instructions
            gc.setFill(Color.LIGHTGRAY);
            gc.setFont(new Font("Arial", 16));
            gc.fillText("Appuyez sur ESC pour retourner au menu",
                    CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + yOffset + 20);

            // Mode debug info
            if (isDebugMode()) {
                gc.setFill(Color.ORANGE);
                gc.setFont(new Font("Arial", 12));
                gc.fillText("Mode Debug activé (F3)", CANVAS_WIDTH / 2, CANVAS_HEIGHT - 30);
            }
        }
    }

    /**
     * Méthode utilitaire pour obtenir des informations sur l'état du jeu
     */
    public String getGameStatus() {
        long alivePlayers = players.stream().filter(JoueurMultiplayer::isAlive).count();
        return String.format("Jeu en cours - Joueurs vivants: %d, Bombes: %d, Power-ups: %d, Temps: %d",
                alivePlayers, bombs.size(), powerUps.size(), gameBanner.getGameTime());
    }

    /**
     * Méthode pour forcer la fin du jeu (pour tests)
     */
    public void forceGameEnd() {
        gameState = GameState.GAME_OVER;
    }

    /**
     * Méthode pour redémarrer le jeu
     */
    public void restartGame() {
        stopGame();
        initializeGame();
        startGameLoop();
    }

    /**
     * Arrêt propre du jeu avec nettoyage des ressources
     */
    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
            gameLoop = null;
        }
        if (gameBanner != null) {
            gameBanner.stopTimer();
        }

        // Nettoyage des collections
        if (bombs != null) bombs.clear();
        if (explosions != null) explosions.clear();
        if (powerUps != null) powerUps.clear();
        if (pressedKeys != null) pressedKeys.clear();

        System.out.println("Jeu arrêté proprement.");
    }

    /**
     * Getter pour l'état du jeu (utile pour les tests)
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Getter pour la liste des joueurs (utile pour les tests)
     */
    public List<JoueurMultiplayer> getPlayers() {
        return new ArrayList<>(players); // Copie défensive
    }

    /**
     * Getter pour le score (utile pour les tests)
     */
    public int getGameScore() {
        return gameScore;
    }

    /**
     * Méthode pour obtenir le nombre de power-ups sur le terrain
     */
    public int getPowerUpCount() {
        return powerUps.size();
    }

    /**
     * Méthode pour obtenir le nombre de bombes actives
     */
    public int getActiveBombCount() {
        return bombs.size();
    }

    /**
     * Méthode pour obtenir le temps restant
     */
    public int getRemainingTime() {
        return gameBanner.getGameTime();
    }

    /**
     * Méthode pour vérifier si le jeu est en mode debug
     */
    public boolean getDebugMode() {
        return isDebugMode();
    }

    /**
     * Méthode pour obtenir des statistiques complètes du jeu
     */
    public Map<String, Object> getGameStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("gameState", gameState);
        stats.put("score", gameScore);
        stats.put("remainingTime", gameBanner.getGameTime());
        stats.put("alivePlayers", players.stream().filter(JoueurMultiplayer::isAlive).count());
        stats.put("activeBombs", bombs.size());
        stats.put("activePowerUps", powerUps.size());
        stats.put("activeExplosions", explosions.size());
        stats.put("debugMode", isDebugMode());

        // Statistiques par joueur
        Map<String, Map<String, Integer>> playerStats = new HashMap<>();
        for (JoueurMultiplayer player : players) {
            Map<String, Integer> playerInfo = new HashMap<>();
            playerInfo.put("lives", player.getVies());
            playerInfo.put("bombCount", player.getNombreBombes());
            playerInfo.put("bombRange", player.getPorteBombe());
            playerInfo.put("speed", player.getVitesse());
            playerInfo.put("isAlive", player.isAlive() ? 1 : 0);
            playerInfo.put("isInvincible", player.isInvincible() ? 1 : 0);
            playerStats.put(player.getCouleur(), playerInfo);
        }
        stats.put("players", playerStats);

        return stats;
    }
}