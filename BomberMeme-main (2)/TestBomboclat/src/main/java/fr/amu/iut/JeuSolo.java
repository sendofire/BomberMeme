package fr.amu.iut;

import fr.amu.iut.Personnages.Bots;
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

public class JeuSolo implements Initializable {

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
                    }
                }
            }
        }
    }

    private void createPlayers() {
        // Joueur 1 (Rouge) - coin supérieur gauche
        JoueurMultiplayer player1 = new JoueurMultiplayer(1, 1, 1, "Rouge");
        player1.setControls("Z", "S", "Q", "D", "A");
        player1.setNombreBombes(3);
        players.add(player1);

        // Bot 1 (Bleu) - coin supérieur droit
        Bots bot1 = new Bots(2, GRID_WIDTH - 2, 1, "Bleu");
        players.add(bot1);

        // Bot 3 (Vert) - coin inférieur gauche
        Bots player3 = new Bots(3, 1, GRID_HEIGHT - 2, "Vert");
        players.add(player3);

        // Bot 4 (Jaune) - coin inférieur droit
        Bots bot4 = new Bots(4, GRID_WIDTH - 2, GRID_HEIGHT - 2, "Jaune");
        players.add(bot4);
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

            if (player instanceof Bots) {
                ((Bots) player).updateAI(gameBoard, bombs, explosions, players);
                if (player.isBombKeyPressed()) {
                    placeBomb(player);
                }
            } else {
                updatePlayerMovement(player);
                updatePlayerBomb(player);
            }
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
                case "A": return pressedKeys.contains(KeyCode.A);
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
                    // Chance de créer un powerup
                    if (random.nextDouble() < 0.3) {
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
            }
        }
    }

    private void applyPowerUp(JoueurMultiplayer player, PowerUps powerUp) {
        switch (powerUp.getType()) {
            case BOMB_RANGE:
                player.ameliorerPortee();
                break;
            case BOMB_COUNT:
                player.ameliorerNombreBombes();
                break;
            case SPEED_UP:
                player.ameliorerVitesse();
                break;
            case EXTRA_LIFE:
                player.gagnerVie();
                break;
            case INVINCIBILITY:
                player.setInvincible(true, 5000); // 5 secondes
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

        // Dessiner le plateau
        renderBoard();

        // Dessiner les powerups
        renderPowerUps();

        // Dessiner les bombes
        renderBombs();

        // Dessiner les explosions
        renderExplosions();

        // Dessiner les joueurs
        renderPlayers();

        gc.restore();

        // Dessiner l'interface de fin de jeu
        renderGameOverScreen();
    }

    private void renderBanner() {
        int alivePlayers = (int) players.stream().filter(JoueurMultiplayer::isAlive).count();
        int totalBombs = bombs.size();

        gameBanner.render(alivePlayers, totalBombs, 0, gameScore);
    }

    private void renderBoard() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                int tileX = x * TILE_SIZE;
                int tileY = y * TILE_SIZE;

                switch (gameBoard[y][x]) {
                    case EMPTY:
                    case POWERUP:
                        gc.drawImage(Sprite.sol.getTexture(), tileX, tileY);
                        break;
                    case WALL:
                        gc.drawImage(Sprite.mur.getTexture(), tileX, tileY);
                        break;
                    case BRICK:
                        gc.drawImage(Sprite.brick.getTexture(), tileX, tileY);
                        break;
                }
            }
        }
    }

    private void renderPowerUps() {
        for (PowerUps powerUp : powerUps) {
            if (!powerUp.isCollected()) {
                int x = powerUp.getX() * TILE_SIZE + TILE_SIZE / 4;
                int y = powerUp.getY() * TILE_SIZE + TILE_SIZE / 4;

                // Couleur selon le type de powerup
                switch (powerUp.getType()) {
                    case BOMB_RANGE:
                        gc.setFill(Color.ORANGE);
                        break;
                    case BOMB_COUNT:
                        gc.setFill(Color.YELLOW);
                        break;
                    case SPEED_UP:
                        gc.setFill(Color.GREEN);
                        break;
                    case EXTRA_LIFE:
                        gc.setFill(Color.PINK);
                        break;
                    case INVINCIBILITY:
                        gc.setFill(Color.PURPLE);
                        break;
                }
                gc.fillOval(x, y, TILE_SIZE / 2, TILE_SIZE / 2);
            }
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
        }
    }

    private Sprite getPlayerSprite(JoueurMultiplayer player) {
        String couleur = player.getCouleur();
        String direction = player.getDirection();

        switch (couleur) {
            case "Rouge":
                switch (direction) {
                    case "UP": return Sprite.player_up_rouge;
                    case "DOWN": return Sprite.player_down_rouge;
                    case "LEFT": return Sprite.palyer_left_rouge;
                    case "RIGHT": return Sprite.palyer_right_rouge;
                }
                break;
            case "Bleu":
                switch (direction) {
                    case "UP": return Sprite.player_up_bleu;
                    case "DOWN": return Sprite.player_down_bleu;
                    case "LEFT": return Sprite.palyer_left_bleu;
                    case "RIGHT": return Sprite.palyer_right_bleu;
                }
                break;
            case "Vert":
                switch (direction) {
                    case "UP": return Sprite.player_up_vert;
                    case "DOWN": return Sprite.player_down_vert;
                    case "LEFT": return Sprite.palyer_left_vert;
                    case "RIGHT": return Sprite.palyer_right_vert;
                }
                break;
            case "Jaune":
                switch (direction) {
                    case "UP": return Sprite.player_up_jaune;
                    case "DOWN": return Sprite.player_down_jaune;
                    case "LEFT": return Sprite.palyer_left_jaune;
                    case "RIGHT": return Sprite.palyer_right_jaune;
                }
                break;
        }
        return Sprite.player_down_rouge; // Défaut
    }

    private void renderGameOverScreen() {
        if (gameState == GameState.GAME_OVER || gameState == GameState.PLAYER_WON) {
            gc.setFill(Color.BLACK);
            gc.setGlobalAlpha(0.7);
            gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
            gc.setGlobalAlpha(1.0);

            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 36));
            gc.setTextAlign(TextAlignment.CENTER);

            if (gameState == GameState.GAME_OVER) {
                gc.fillText("TEMPS ÉCOULÉ!", CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 - 50);
            } else {
                Optional<JoueurMultiplayer> winner = players.stream()
                        .filter(JoueurMultiplayer::isAlive)
                        .findFirst();

                if (winner.isPresent()) {
                    gc.fillText("JOUEUR " + winner.get().getCouleur().toUpperCase() + " GAGNE!",
                            CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 - 50);
                } else {
                    gc.fillText("ÉGALITÉ!", CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 - 50);
                }
            }

            gc.setFont(new Font("Arial", 18));
            gc.fillText("Appuyez sur ESC pour retourner au menu", CANVAS_WIDTH / 2, CANVAS_HEIGHT / 2 + 20);
        }
    }

    public void stopGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        if (gameBanner != null) {
            gameBanner.stopTimer();
        }
    }
}