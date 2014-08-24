package com.almasb.hangman.jfxgui;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.almasb.hangman.Logic;

public class FXGUI extends Application {

    private static final Font DEFAULT = new Font("Courier", 30);

    private Logic game = new Logic();

    private Text typedSoFar = new Text("");
    private Text letters = new Text("");
    private Text lives = new Text("");
    private Text message = new Text("");

    private Button again = new Button("NEW");

    public Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(500, 120);
        root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        typedSoFar.setFont(DEFAULT);
        letters.setFont(DEFAULT);
        lives.setFont(DEFAULT);
        message.setFont(DEFAULT);
        again.setFont(DEFAULT);

        again.setAlignment(Pos.BASELINE_CENTER);
        again.setDisable(true);
        again.setOnAction(event -> {
            again.setDisable(true);
            message.setText("");
            game.newGame();
        });

        game.livesProperty().addListener((observable, oldValue, newValue) -> {
            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), lives);
            ft.setFromValue(1.0);
            ft.setToValue(0.2);
            ft.setOnFinished(event -> {
                lives.setOpacity(1.0);
                lives.setText(newValue.intValue() + " lives");
            });
            ft.play();
        });
        game.playableProperty().addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                gameOver(game.guessed() ? "You win" : "You lose");
        });
        game.guessedLettersProperty().addListener((obs, old, newValue) -> {

            letters.setText(newValue);
        });

        game.newGame();

        VBox vBox = new VBox(10);
        HBox hBox = new HBox(10);
        HBox hBox2 = new HBox(50);

        hBox.getChildren().addAll(letters, lives, again);
        hBox2.getChildren().addAll(typedSoFar, message);

        vBox.getChildren().addAll(hBox, hBox2);

        root.getChildren().add(vBox);
        return root;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getText().isEmpty())
                return;

            char pressed = event.getText().toLowerCase().charAt(0);

            if (game.playableProperty().get() && !typedSoFar.getText().contains(pressed + "")) {
                typedSoFar.setText(typedSoFar.getText() + pressed);
                game.guess(pressed);
            }
        });

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
        primaryStage.setResizable(false);
        primaryStage.setTitle("JavaFX GUI");
        primaryStage.setScene(scene);
        primaryStage.show();

        FillTransition fillTransition = new FillTransition(Duration.seconds(2), typedSoFar, Color.BROWN, Color.DODGERBLUE);
        fillTransition.setCycleCount(Timeline.INDEFINITE);
        fillTransition.setAutoReverse(true);
        fillTransition.play();
    }

    private void gameOver(String msg) {
        message.setText(msg);
        typedSoFar.setText("");
        RotateTransition rt = new RotateTransition(Duration.seconds(2), again);
        rt.setAxis(new Point3D(0, 1, 0));
        rt.setFromAngle(0);
        rt.setToAngle(360);
        rt.setOnFinished(event -> {
            again.setDisable(false);
        });
        rt.play();
    }
}
