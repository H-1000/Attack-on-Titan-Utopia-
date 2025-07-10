package game.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import game.engine.Battle;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.weapons.Weapon;
import game.engine.weapons.WeaponRegistry;
import game.engine.weapons.factory.WeaponFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;

public class GameWindow extends Application {

    // UI Elements
    private BorderPane root;
    private HBox topPane;
    private Label scoreLabel;
    private Label turnLabel;
    private Label phaseLabel;
    private Label resourcesLabel;
    private VBox weaponShopPane;
    private GridPane lanesPane;
    private HBox bottomPane;

    // Game Logic
    Battle battle;
    private WeaponFactory weaponFactory;
    Lane selectedLane;
    private HashMap<Lane, LanePane> lanePanes; // Store LanePanes for updates

    // Audio
    private AudioClip weaponPurchaseSound;
    private AudioClip titanAttackSound;
    private AudioClip gameOverSound;

    // Constants for UI elements
    private final int cellSize = 30;
    private final int pathWidth = 180; // Adjust as needed

    // Constructor to handle difficulty
    public GameWindow(boolean easyMode) {
        try {
            int initialLanes = easyMode ? 3 : 5;
            int resourcesPerLane = easyMode ? 250 : 125;
            battle = new Battle(0, 0, 400, initialLanes, resourcesPerLane); // Titan spawn distance is 400
            weaponFactory = battle.getWeaponFactory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        loadSounds();
        createUI(primaryStage);
        initGame();
    }

    public GameWindow(){
        this(true);
    }

    private void loadSounds() {
        // Load sounds
        weaponPurchaseSound = new AudioClip(new File("D:\\GIU_2485_62_17261_2024-05-01T11_21_0611\\Q2V2_Wednesday4th - updated\\Q2V2MuseumBaseUpdated\\AttackonTitan\\src\\main\\resources\\click.wav").toURI().toString());
        titanAttackSound = new AudioClip(new File("D:\\GIU_2485_62_17261_2024-05-01T11_21_0611\\Q2V2_Wednesday4th - updated\\Q2V2MuseumBaseUpdated\\AttackonTitan\\src\\main\\resources\\click.wav").toURI().toString());
        gameOverSound = new AudioClip(new File("D:\\GIU_2485_62_17261_2024-05-01T11_21_0611\\Q2V2_Wednesday4th - updated\\Q2V2MuseumBaseUpdated\\AttackonTitan\\src\\main\\resources\\click.wav").toURI().toString());
    }

    private void createUI(Stage primaryStage) {
        // Root Layout
        root = new BorderPane();
        root.setStyle("-fx-background-color: #222; -fx-padding: 20;");

        // Top Pane
        topPane = new HBox(20);
        topPane.setPadding(new Insets(10));
        topPane.setAlignment(Pos.CENTER);
        scoreLabel = createLabel("Score: 0", 20);
        turnLabel = createLabel("Turn: 1", 20);
        phaseLabel = createLabel("Phase: EARLY", 20);
        resourcesLabel = createLabel("Resources: 0", 20);
        topPane.getChildren().addAll(scoreLabel, turnLabel, phaseLabel, resourcesLabel);
        root.setTop(topPane);

        // Weapon Shop Pane
        weaponShopPane = new VBox(10);
        weaponShopPane.setPadding(new Insets(10));
        weaponShopPane.setAlignment(Pos.TOP_CENTER);
        weaponShopPane.setPrefWidth(250);
        Label shopTitle = createLabel("Weapon Shop", 28);
        shopTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: orange;");
        weaponShopPane.getChildren().add(shopTitle);
        root.setLeft(weaponShopPane);

        // Lanes Pane
        lanesPane = new GridPane();
        lanesPane.setAlignment(Pos.CENTER);
        lanesPane.setHgap(20);
        lanesPane.setVgap(20);
        root.setCenter(lanesPane);

        // Bottom Pane
        bottomPane = new HBox(20);
        bottomPane.setPadding(new Insets(10));
        bottomPane.setAlignment(Pos.CENTER);
        Button passTurnButton = new Button("Pass Turn");
        passTurnButton.setOnAction(event -> passTurn());
        bottomPane.getChildren().add(passTurnButton);
        root.setBottom(bottomPane);

        // Scene and Stage
        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("Attack on Titan: Utopia");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Label createLabel(String text, int fontSize) {
        Label label = new Label(text);
        label.setTextFill(Color.WHITE);
        label.setFont(Font.loadFont(getClass().getResourceAsStream("/American Captain.ttf"), fontSize));
        return label;
    }

    private void initGame() {
        initializeWeaponShop();
        initializeLanes();
        battle.refillApproachingTitans();
        updateGameInfo();
    }

    private void initializeWeaponShop() {
        int col = 0;
        int row = 0;
        for (WeaponRegistry weaponRegistry : weaponFactory.getWeaponShop().values()) {
            Pane weaponDisplay = new Pane();
            weaponDisplay.setStyle("-fx-background-color: darkgray; -fx-padding: 10;");
            weaponDisplay.setPrefSize(100, 100);

            Label nameLabel = createLabel(weaponRegistry.getName(), 14);
            nameLabel.setWrapText(true); // Allow text to wrap
            nameLabel.setMaxWidth(80); // Limit the width of the label

            Label priceLabel = createLabel("Price: " + weaponRegistry.getPrice(), 12);
            Label damageLabel = createLabel("Damage: " + weaponRegistry.getDamage(), 12);

            Button buyButton = new Button("Buy");
            buyButton.setFont(Font.loadFont(getClass().getResourceAsStream("/American Captain.ttf"), 12));
            buyButton.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 5 10;");
            buyButton.setEffect(new DropShadow(2, Color.BLACK));
            buyButton.setOnAction(event -> handleWeaponPurchase(weaponRegistry));

            // Position labels and button within the weapon display
            nameLabel.relocate(10, 10);
            priceLabel.relocate(10, 30);
            damageLabel.relocate(10, 50);
            buyButton.relocate(10, 70);

            weaponDisplay.getChildren().addAll(nameLabel, priceLabel, damageLabel, buyButton);
            lanesPane.add(weaponDisplay, col, row);

            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }
    }

    private void handleWeaponPurchase(WeaponRegistry weaponRegistry) {
        if (selectedLane != null) {
            try {
                battle.purchaseWeapon(weaponRegistry.getCode(), selectedLane);
                weaponPurchaseSound.play();
                resourcesLabel.setText("Resources: " + battle.getResourcesGathered());
                updateLaneDisplays(); // Update the lane to show the purchased weapon
            } catch (InsufficientResourcesException e) {
                // Display an error message to the user (not enough resources)
                System.out.println("Not enough resources to buy this weapon!");
            } catch (InvalidLaneException e) {
                // Display an error message (invalid lane selected)
                System.out.println("Invalid lane selected!");
            }
        } else {
            // Display an error message (no lane selected)
            System.out.println("Please select a lane first!");
        }
    }

    private void initializeLanes() {
        lanePanes = new HashMap<>(); // Initialize the map
        PriorityQueue<Lane> lanes = battle.getLanes();
        int laneCount = battle.getOriginalLanes().size();
        int col = 0;
        int row = 0;

        for (int i = 0; i < laneCount; i++) {
            Lane lane = battle.getOriginalLanes().get(i);
            LanePane lanePane = new LanePane(lane, this, cellSize, pathWidth);
            lanesPane.add(lanePane, col, row);
            lanePanes.put(lane, lanePane); // Add the LanePane to the map

            // Handle lane selection
            lanePane.setOnMouseClicked(event -> {
                selectedLane = lane;
                // Add visual indication of selected lane (e.g., highlight the lanePane)
                for (Lane l : lanePanes.keySet()) {
                    // Reset the background color of all lanes
                    lanePanes.get(l).setStyle("-fx-background-color: transparent;");
                }
                // Highlight the selected lane
                lanePane.setStyle("-fx-background-color: rgba(255, 255, 0, 0.5);"); // Semi-transparent yellow
            });

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }
    }

    private void updateGameInfo() {
        scoreLabel.setText("Score: " + battle.getScore());
        turnLabel.setText("Turn: " + battle.getNumberOfTurns());
        phaseLabel.setText("Phase: " + battle.getBattlePhase().toString());
        resourcesLabel.setText("Resources: " + battle.getResourcesGathered());
    }

    private void updateLaneDisplays() {
        for (LanePane lanePane : lanePanes.values()) {
            lanePane.updateLaneDisplay();
        }
    }

    private void passTurn() {
        if (!battle.isGameOver()) {
            battle.passTurn();
            updateGameInfo();
            updateLaneDisplays();

            // Play titan attack sounds if they reach the wall
            for (Lane lane : battle.getOriginalLanes()) {
                for (Titan titan : lane.getTitans()) {
                    if (titan.getDistance() == 0) {
                        titanAttackSound.play();
                        break; // Only play the sound once per lane if a titan has reached
                    }
                }
            }

            if (battle.isGameOver()) {
                gameOverSound.play();
                showGameOverScreen();
            }
        }
    }

    // Game Over Screen
    private void showGameOverScreen() {
        // 2. Create a Game Over Overlay
        StackPane gameOverOverlay = new StackPane();
        gameOverOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);"); // Semi-transparent black
        gameOverOverlay.setPrefSize(root.getWidth(), root.getHeight());

        // 3. Game Over Text
        Label gameOverLabel = new Label("Game Over!");
        gameOverLabel.setFont(Font.font("American Captain", 60));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setEffect(new DropShadow(10, Color.BLACK));

        // 4. Final Score
        Label scoreLabel = new Label("Final Score: " + battle.getScore());
        scoreLabel.setFont(Font.font("American Captain", 36));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setEffect(new DropShadow(5, Color.BLACK));


        // 7. Overlay Layout
        VBox gameOverBox = new VBox(20, gameOverLabel, scoreLabel);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverOverlay.getChildren().add(gameOverBox);

        // 8. Add Overlay to the Scene
        root.getChildren().add(gameOverOverlay);
    }

    // Restart Game Logic
    private void restartGame(Stage primaryStage) {
        primaryStage.close(); // Close the current game window
        try {
            // Assuming your StartWindow also handles game initialization
            new StartWindow().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return to Main Menu Logic
    private void returnToMainMenu(Stage primaryStage) {
        primaryStage.close(); // Close the current game window
        try {
            new StartWindow().start(new Stage()); // Show the main menu
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}