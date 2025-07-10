package game.gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;

public class StartWindow extends Application {

    private static final double SLOW_MOTION_RATE = 0.3; // Adjust slow-motion speed
    private static final int FADE_DURATION_MILLIS = 500;

    private AudioClip buttonHoverSound;
    private AudioClip buttonClickSound;
    private MediaPlayer backgroundMusicPlayer;
    private MediaPlayer backgroundVideoPlayer;
    private MediaView videoView;
    private Font pixelFont;

    @Override
    public void start(Stage primaryStage) {
        loadAssets();
        setupBackgroundVideo(primaryStage);
        createMenu(primaryStage);
        playBackgroundMusic();
    }

    private void loadAssets() {
         pixelFont = Font.loadFont(StartWindow.class.getResourceAsStream("/American Captain.ttf"), 24);
        try {
            buttonHoverSound = new AudioClip(StartWindow.class.getResource("/hover.wav").toURI().toString());
            buttonClickSound = new AudioClip(StartWindow.class.getResource("/click.wav").toURI().toString());
        } catch (Exception e) {
            e.printStackTrace(); // Handle loading errors properly in a production setting
        }
    }

    private void setupBackgroundVideo(Stage primaryStage) {
        Media video = new Media(new File("D:\\GIU_2485_62_17261_2024-05-01T11_21_0611\\Q2V2_Wednesday4th - updated\\Q2V2MuseumBaseUpdated\\AttackonTitan\\src\\main\\resources\\AT9.mp4").toURI().toString());
        backgroundVideoPlayer = new MediaPlayer(video);
        videoView = new MediaView(backgroundVideoPlayer);
        videoView.fitWidthProperty().bind(primaryStage.widthProperty());
        videoView.fitHeightProperty().bind(primaryStage.heightProperty());

        backgroundVideoPlayer.setRate(SLOW_MOTION_RATE);
        backgroundVideoPlayer.setOnEndOfMedia(this::reverseVideoPlayback); // Use method reference
        backgroundVideoPlayer.play();
    }

    private void createMenu(Stage primaryStage) {
        // Logo Label
        Label logoLabel = new Label("Attack on Titan: Utopia");
        logoLabel.setFont(Font.font(pixelFont.getFamily(), 72));
        logoLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow( gaussian , rgba(0,0,0,0.7) , 10,0,0,5 );");
        logoLabel.setAlignment(Pos.CENTER);

        // Menu Buttons
        Button easyModeButton = createMenuButton("Easy Mode", pixelFont);
        Button hardModeButton = createMenuButton("Hard Mode", pixelFont);

        // Button Actions (add your game start logic)
        easyModeButton.setOnAction(event -> {
            buttonClickSound.play();
            startGame(primaryStage, true); // true for Easy Mode
        });

        hardModeButton.setOnAction(event -> {
            buttonClickSound.play();
            startGame(primaryStage, false); // false for Hard Mode
        });

        // Menu Layout (with semi-transparent background)
        VBox menuBox = new VBox(30, logoLabel, easyModeButton, hardModeButton);
        menuBox.setAlignment(Pos.CENTER);

        Region backgroundPanel = new Region();
        backgroundPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");

        StackPane root = new StackPane(videoView, backgroundPanel, menuBox);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 1280, 720); // Initial size (will be fullscreen)
        primaryStage.setTitle("Attack on Titan: Utopia");
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        // Initial Fade In for the video
        FadeTransition initialFadeIn = new FadeTransition(Duration.millis(FADE_DURATION_MILLIS), videoView);
        initialFadeIn.setFromValue(0.0);
        initialFadeIn.setToValue(1.0);
        initialFadeIn.play();
    }

    private void playBackgroundMusic() {
        Media backgroundMusic = new Media(new File("D:\\GIU_2485_62_17261_2024-05-01T11_21_0611\\Q2V2_Wednesday4th - updated\\Q2V2MuseumBaseUpdated\\AttackonTitan\\src\\main\\resources\\Undertale.mp3").toURI().toString());
        backgroundMusicPlayer = new MediaPlayer(backgroundMusic);
        backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusicPlayer.play();
    }

    private Button createMenuButton(String text, Font font) {
        Button button = new Button(text);
        button.setFont(font);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 15 30;");
        button.setEffect(new DropShadow(5, Color.BLACK));

        // Hover Effects (Scale and Shadow)
        button.setOnMouseEntered(event -> {
            buttonHoverSound.play();

            ScaleTransition scale = new ScaleTransition(Duration.millis(200), button);
            scale.setToX(1.1);
            scale.setToY(1.1);

            DropShadow shadow = new DropShadow(10, Color.BLACK); // Enhanced shadow
            button.setEffect(shadow);

            ParallelTransition parallel = new ParallelTransition(scale, new PauseTransition(Duration.millis(100)));
            parallel.play();
        });

        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 15 30;");
            button.setEffect(new DropShadow(5, Color.BLACK)); // Reset to default shadow
        });

        return button;
    }

    // Background Video Reverse Logic with Fade
    private void reverseVideoPlayback() {
        double currentRate = backgroundVideoPlayer.getRate();
        backgroundVideoPlayer.setRate(currentRate * -1);

        Duration seekTarget = (currentRate > 0) ? backgroundVideoPlayer.getTotalDuration() : Duration.ZERO;

        // Fade Out then Fade In
        FadeTransition fadeOut = new FadeTransition(Duration.millis(FADE_DURATION_MILLIS), videoView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(FADE_DURATION_MILLIS), videoView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeOut.setOnFinished(e -> {
            backgroundVideoPlayer.seek(seekTarget);
            fadeIn.play();
        });

        fadeOut.play();
    }

    // Game Start Logic (replace with your actual game initialization)
    private void startGame(Stage primaryStage, boolean easyMode) {
        primaryStage.close(); // Close the StartWindow
        // ... Create and show your GameWindow ...
    }

    public static void main(String[] args) {
        launch(args);
    }
}