package game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.weapons.Weapon;

import java.util.HashMap;

public class LanePane extends VBox {
    private Lane lane;
    private GameWindow gameWindow;
    private HashMap<Titan, Node> titanDisplays;
    private HashMap<Weapon, Node> weaponDisplays; // Map to store weapon displays

    private ImageView wallImageView;
    private Rectangle wallHealthBar;
    private GridPane weaponDeploymentGrid;
    private Pane titanPathPane;

    // Constants for UI elements (adjust as needed)
    private final int cellSize;
    private final int pathWidth;

    public LanePane(Lane lane, GameWindow gameWindow, int cellSize, int pathWidth) {
        this.lane = lane;
        this.gameWindow = gameWindow;
        this.cellSize = cellSize;
        this.pathWidth = pathWidth;
        titanDisplays = new HashMap<>();
        weaponDisplays = new HashMap<>();

        // Initialize UI elements
        wallImageView = new ImageView(new Image(getClass().getResourceAsStream("/Lane.png")));
        wallImageView.setFitWidth(pathWidth);
        wallImageView.setFitHeight(cellSize);

        wallHealthBar = new Rectangle(pathWidth, 10, Color.GREEN);

        weaponDeploymentGrid = new GridPane();
        weaponDeploymentGrid.setHgap(1);
        weaponDeploymentGrid.setVgap(1);
        weaponDeploymentGrid.setPadding(new Insets(5));

        titanPathPane = new Pane();
        titanPathPane.setPrefWidth(pathWidth);
        titanPathPane.setPrefHeight(400); // Adjust height as needed
        titanPathPane.setStyle("-fx-background-color: transparent;");

        this.getChildren().addAll(titanPathPane, wallHealthBar, wallImageView, weaponDeploymentGrid);
        this.setAlignment(Pos.BOTTOM_CENTER);
        this.setPadding(new Insets(10));
        this.setSpacing(5);

        initializeWeaponDeploymentGrid();
    }

    // Initialize the grid for weapon deployment
    private void initializeWeaponDeploymentGrid() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < pathWidth / cellSize; col++) {
                Pane cell = new Pane();
                cell.setPrefSize(cellSize, cellSize);
                cell.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");

                // Add weapon deployment logic to each cell
                final int cellRow = row;
                final int cellCol = col;
                cell.setOnMouseClicked(event -> {
                    if (gameWindow.selectedLane == this.lane) {
                        deployWeapon(cellRow, cellCol);
                    }
                });

                weaponDeploymentGrid.add(cell, col, row);
            }
        }
    }

    // Handle weapon deployment logic
    private void deployWeapon(int row, int col) {
        // ... (Get the selected weapon from your GameWindow or elsewhere) ...

        // ... (Deploy the selected weapon in the game logic (battle)) ...
        // (For example, you might use battle.purchaseWeapon() if you've already
        // selected a weapon and this click is to finalize deployment).

        updateWeaponDisplays(); // Update the UI to reflect the deployed weapon
    }

    private void createTitanDisplay(Titan titan) {
        // Replace with your titan image loading and setup
        Rectangle titanView = new Rectangle(20, 30, Color.RED); // Placeholder
        titanDisplays.put(titan, titanView);
        titanPathPane.getChildren().add(titanView);
        positionTitanDisplay(titan);
    }

    public void updateLaneDisplay() {
        updateWallHealthBar();
        updateTitanDisplays();
        updateWeaponDisplays();
    }

    private void updateWallHealthBar() {
        double healthPercentage = (double) lane.getLaneWall().getCurrentHealth() / lane.getLaneWall().getBaseHealth();
        wallHealthBar.setWidth(pathWidth * healthPercentage);

        if (healthPercentage > 0.6) {
            wallHealthBar.setFill(Color.GREEN);
        } else if (healthPercentage > 0.3) {
            wallHealthBar.setFill(Color.YELLOW);
        } else {
            wallHealthBar.setFill(Color.RED);
        }
    }

    private void updateTitanDisplays() {
        for (Titan titan : lane.getTitans()) {
            if (!titanDisplays.containsKey(titan)) {
                createTitanDisplay(titan);
            } else {
                positionTitanDisplay(titan);

                // Handle titan death (remove from display)
                if (titan.isDefeated()) {
                    Node titanDisplay = titanDisplays.get(titan);
                    titanPathPane.getChildren().remove(titanDisplay);
                    titanDisplays.remove(titan);
                }
            }
        }
    }

    private void positionTitanDisplay(Titan titan) {
        double yPosition = (titanPathPane.getPrefHeight() - cellSize) - (titan.getDistance() / (double) gameWindow.battle.getTitanSpawnDistance()) * (titanPathPane.getPrefHeight() - cellSize);
        Node titanDisplay = titanDisplays.get(titan);
        titanDisplay.relocate((pathWidth - titanDisplay.getBoundsInLocal().getWidth()) / 2, yPosition);
    }

    private void updateWeaponDisplays() {
        weaponDeploymentGrid.getChildren().clear(); // Clear existing weapon displays

        for (Weapon weapon : lane.getWeapons()) {
            createWeaponDisplay(weapon);
        }
    }

    // Create a display for a weapon
    private void createWeaponDisplay(Weapon weapon) {
        // Replace with your weapon image loading and setup
        Rectangle weaponView = new Rectangle(cellSize, cellSize, Color.BLUE); // Placeholder
        weaponDisplays.put(weapon, weaponView);
        int gridCol = getWeaponGridCol(weapon); // Get the weapon's column from the game logic
        int gridRow = getWeaponGridRow(weapon); // Get the weapon's row from the game logic
        weaponDeploymentGrid.add(weaponView, gridCol, gridRow);
    }

    // Get the weapon's grid column based on game logic
    private int getWeaponGridCol(Weapon weapon) {
        // ... (Implement logic based on your game mechanics) ...
        return 0; // Placeholder
    }

    // Get the weapon's grid row based on game logic
    private int getWeaponGridRow(Weapon weapon) {
        // ... (Implement logic based on your game mechanics) ...
        return 0; // Placeholder
    }
}