package game.gui;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Attack on Titan: Utopia");

        // 1. Background
        Image backgroundImage = new Image("D:\\batman-sign-black-background-dc-superheroes-amoled-5k-8k-10k-10000x6250-4409.jpg"); // Replace with your background path
        BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true);
        BackgroundImage backgroundImg = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        Background background = new Background(backgroundImg);

        // 2. Logo
        Label gameTitle = new Label("Attack on Titan: Utopia");
        gameTitle.setFont(Font.font("Arial", FontWeight.BOLD, 72)); // Choose your font
        gameTitle.setTextFill(Color.valueOf("#E5B24E")); // Gold-ish color, adjust to match your theme

        // Glow effect for the logo
        DropShadow glow = new DropShadow();
        glow.setColor(Color.valueOf("#E5B24E"));
        glow.setRadius(20);
        glow.setSpread(0.5);
        gameTitle.setEffect(glow);

        // 3. Team Credit
        Label teamCredit = new Label("A Game By: [Ahmed/TTA]");
        teamCredit.setFont(Font.font("Arial", 24));
        teamCredit.setTextFill(Color.WHITE);

        // 4. (Optional) Titan Silhouette
        ImageView titanSilhouette = new ImageView(new Image("D:\\TITAN-ATTACK.png"));
        titanSilhouette.setFitWidth(200); // Adjust size as needed
        titanSilhouette.setPreserveRatio(true);
        titanSilhouette.setOpacity(0.8); // Make it semi-transparent

        // Layout (using VBox)
        VBox root = new VBox(20, gameTitle, teamCredit, titanSilhouette);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setBackground(background);

        // Scene and Stage
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();

        // Optional: Fade-out transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), root); // 3 seconds
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> primaryStage.close()); // Close the splash screen when fade-out is done
        fadeOut.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
