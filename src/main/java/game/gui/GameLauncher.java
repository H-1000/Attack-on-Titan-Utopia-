package game.gui;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class GameLauncher extends Application {
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showCreditSequence();
    }

    private void showCreditSequence() {
        Stage splashStage = new Stage();
        splashStage.setTitle("Attack on Titan: Utopia");
        splashStage.setFullScreen(true);

        Image backgroundImage = new Image(GameLauncher.class.getResourceAsStream("/AT5.jpg"));
        ImageView backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(primaryStage.getWidth());
        backgroundImageView.setFitHeight(primaryStage.getHeight());

        StackPane splashRoot = new StackPane(backgroundImageView);
        Scene splashScene = new Scene(splashRoot);
        splashStage.setScene(splashScene);
        splashStage.show();

        // 1. Load the Pixel Font (default size for other labels)
        Font pixelFont = Font.loadFont(GameLauncher.class.getResourceAsStream("/American Captain.ttf"), 36);

        // Credit Labels:
        Label logoLabel = new Label(" Attack on Titan: Utopia "); // Replace with your logo text
        logoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        logoLabel.setAlignment(Pos.CENTER);
        logoLabel.setFont(Font.font(pixelFont.getFamily(), 120)); // BIG logo font size

        Label devName = new Label("Developed by: Ahmed & Hamid");
        devName.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        devName.setAlignment(Pos.CENTER);
        devName.setFont(pixelFont);

        Label musicName = new Label("Music: [Your Music Track Name]"); // Replace with track name
        musicName.setStyle("-fx-text-fill: white;");
        musicName.setAlignment(Pos.CENTER);
        musicName.setFont(pixelFont);

        Label teamName = new Label("Team: [Your Team Name]"); // Replace with team name
        teamName.setStyle("-fx-text-fill: white;");
        teamName.setAlignment(Pos.CENTER);
        teamName.setFont(pixelFont);

        // Fade Transitions (adjust timing if needed)
        FadeTransition fadeInLogo = createFadeTransition(logoLabel, 0.0, 1.0, 2);
        FadeTransition fadeOutLogo = createFadeTransition(logoLabel, 1.0, 0.0, 2);
        FadeTransition fadeInDev = createFadeTransition(devName, 0.0, 1.0, 2);
        FadeTransition fadeOutDev = createFadeTransition(devName, 1.0, 0.0, 2);
        FadeTransition fadeInMusic = createFadeTransition(musicName, 0.0, 1.0, 2);
        FadeTransition fadeOutMusic = createFadeTransition(musicName, 1.0, 0.0, 2);
        FadeTransition fadeInTeam = createFadeTransition(teamName, 0.0, 1.0, 2);
        FadeTransition fadeOutTeam = createFadeTransition(teamName, 1.0, 0.0, 2);

        // Sequential Transition for Credits (logo first)
        SequentialTransition creditSequence = new SequentialTransition(
                fadeInLogo, fadeOutLogo,
                fadeInDev, fadeOutDev,
                fadeInMusic, fadeOutMusic,
                fadeInTeam, fadeOutTeam
        );

        creditSequence.setOnFinished(event -> {
            splashRoot.getChildren().removeAll(logoLabel, devName, musicName, teamName); // Remove text
            playCutscene(splashStage, splashRoot);
        });
        creditSequence.play();

        // Add all labels to the scene:
        splashRoot.getChildren().addAll(logoLabel, devName, musicName, teamName);
    }

    private FadeTransition createFadeTransition(javafx.scene.Node node, double fromValue, double toValue, int durationSeconds) {
        FadeTransition fade = new FadeTransition(Duration.seconds(durationSeconds), node);
        fade.setFromValue(fromValue);
        fade.setToValue(toValue);
        return fade;
    }

    private void playCutscene(Stage splashStage, StackPane splashRoot) {
        Media video = new Media(new File("D:\\GIU_2485_62_17261_2024-05-01T11_21_0611\\Q2V2_Wednesday4th - updated\\Q2V2MuseumBaseUpdated\\AttackonTitan\\src\\main\\resources\\AT6.mp4").toURI().toString()); // Correct path!
        MediaPlayer player = new MediaPlayer(video);
        MediaView mediaView = new MediaView(player);
        splashRoot.getChildren().add(mediaView);

        FadeTransition fadeIn = createFadeTransition(mediaView, 0.0, 1.0, 2);
        fadeIn.setOnFinished(event -> player.play());
        fadeIn.play();

        player.setOnEndOfMedia(() -> {
            FadeTransition fadeOut = createFadeTransition(splashRoot, 1.0, 0.0, 2);
            fadeOut.setOnFinished(event -> {
                splashStage.hide();
                showStartWindow();
            });
            fadeOut.play();
        });
    }

    private void showStartWindow() {
        StartWindow startWindow = new StartWindow();
        startWindow.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}