package com.almasb.hangman.jfxgui;

import java.util.ArrayList;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import com.almasb.hangman.Logic;

public class FXGUI extends Application {

    private Logic game = new Logic();

    private String typedSoFar = "";

    private ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
    private ArrayList<Text> screenLetters = new ArrayList<Text>();

    public Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(900, 150);
        root.setMinSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);
        root.setMaxSize(Pane.USE_PREF_SIZE, Pane.USE_PREF_SIZE);

        game.newGame();

        HBox hBox = new HBox(5);
        hBox.setLayoutY(40);

        String letters = game.getLetters();
        String word = game.getWord();
        for (int i = 0; i < letters.length(); i++) {
            Rectangle rect = new Rectangle(20, 20, 50, 75);
            rect.setArcHeight(20);
            rect.setArcWidth(20);
            rect.setFill(Color.BLUE);
            rects.add(rect);

            Text text = new Text(word.charAt(i) + "");
            text.setFont(new Font("Monospaced", 40));
            text.setVisible(false);
            screenLetters.add(text);

            hBox.getChildren().add(rect);

            text.setLayoutX(15 + i*55);
            text.setLayoutY(85);

            root.getChildren().add(text);
        }

        root.getChildren().add(hBox);
        return root;
    }

    @Override
    public void stop() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getText().isEmpty())
                return;

            char pressed = event.getText().toLowerCase().charAt(0);

            if (!game.guessed() && game.getLives() > 0 && !typedSoFar.contains(pressed + "")) {
                typedSoFar += pressed;
                System.out.println(typedSoFar);

                game.guess(pressed);
                String guessed = game.getLetters();

                for (int i = 0; i < rects.size(); i++) {
                    final int index = i;
                    Rectangle rect = rects.get(i);
                    if (rect.isVisible() && guessed.charAt(i) != '.') {
                        RotateTransition trans = new RotateTransition(Duration.seconds(0.5), rect);
                        trans.setAxis(new Point3D(0, 1, 0));
                        trans.setFromAngle(0);
                        trans.setToAngle(170);
                        trans.setOnFinished(e -> {
                            rect.setVisible(false);
                            screenLetters.get(index).setVisible(true);
                        });
                        trans.play();
                    }
                }

                System.out.println(game.getLives() + " lives");

                if (game.guessed()) {
                    gameOver("You won");
                }

                if (game.getLives() == 0) {
                    gameOver("You lost");
                }
            }
        });

        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void gameOver(String message) {
        System.out.println(message);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
