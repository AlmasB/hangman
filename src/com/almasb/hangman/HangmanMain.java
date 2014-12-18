package com.almasb.hangman;

import java.util.HashMap;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HangmanMain extends Application {

    private static final Font DEFAULT_FONT = new Font("Courier", 36);

    private Logic game = new Logic();

    private ObservableList<Node> letters;

    private Text lettersUsed = new Text("");
    private Text lives = new Text("");
    private Text message = new Text("");

    private Button again = new Button("NEW");

    private HashMap<Character, Text> map = new HashMap<Character, Text>();

    public Parent createContent() {
        HBox lettersBox = new HBox();
        lettersBox.setAlignment(Pos.CENTER);
        letters = lettersBox.getChildren();

        lettersUsed.setFont(DEFAULT_FONT);

        again.disableProperty().bind(game.playableProperty());
        lives.textProperty().bind(game.livesProperty().asString().concat(" Lives"));

        again.setOnAction(event -> {
            startGame();
        });

        game.playableProperty().addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                stopGame(game.guessed() ? "You win" : "You lose");
        });

        // layout
        HBox row1 = new HBox();
        HBox row3 = new HBox();
        row1.setAlignment(Pos.CENTER);
        row3.setAlignment(Pos.CENTER);
        for (int i = 0 ; i < 20; i++) {
            row1.getChildren().add(new Letter(' '));
            row3.getChildren().add(new Letter(' '));
        }

        HBox letBox = new HBox(5);
        letBox.setAlignment(Pos.CENTER);
        for (char c = 'A'; c <= 'Z'; c++) {
            Text t = new Text(String.valueOf(c));
            t.setFont(DEFAULT_FONT);
            map.put(c, t);
            letBox.getChildren().add(t);
        }

        VBox vBox = new VBox(10);
        vBox.setPrefSize(600, 300);
        vBox.getChildren().addAll(row1, lettersBox, row3, letBox,
                new HBox(10, lives, again));
        return vBox;
    }

    private void stopGame(String msg) {
        message.setText(msg);
        lettersUsed.setText("");

        for (Node n : letters) {
            Letter letter = (Letter) n;
            letter.show();
        }
    }

    private void startGame() {
        for (Text t : map.values()) {
            t.setStrikethrough(false);
            t.setFill(Color.BLACK);
        }
        message.setText("");
        game.newGame();
        letters.clear();
        for (char c : game.wordProperty().get().toCharArray()) {
            letters.add(new Letter(c));
        }
    }

    private static class Letter extends StackPane {
        private Rectangle bg = new Rectangle(40, 60);
        private Text text;

        public Letter(char letter) {
            bg.setFill(letter == ' ' ? Color.DARKSEAGREEN : Color.WHITE);
            bg.setStroke(Color.BLUE);

            text = new Text(String.valueOf(letter).toUpperCase());
            text.setFont(DEFAULT_FONT);
            text.setVisible(false);

            setAlignment(Pos.CENTER);
            getChildren().addAll(bg, text);
        }

        public void show() {
            RotateTransition rt = new RotateTransition(Duration.seconds(1), bg);
            rt.setAxis(Rotate.Y_AXIS);
            rt.setToAngle(180);
            rt.setOnFinished(event -> text.setVisible(true));
            rt.play();
        }

        public boolean isEqualTo(char other) {
            return text.getText().equals(String.valueOf(other).toUpperCase());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent());
        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.getText().isEmpty())
                return;

            char pressed = event.getText().toUpperCase().charAt(0);
            if (pressed < 'A' || pressed > 'Z')
                return;

            if (game.playableProperty().get() && !lettersUsed.getText().contains(String.valueOf(pressed))) {
                map.get(pressed).setFill(Color.BLUE);
                map.get(pressed).setStrikethrough(true);

                lettersUsed.setText(lettersUsed.getText() + pressed);
                game.guess(pressed);

                for (Node n : letters) {
                    Letter letter = (Letter) n;
                    if (letter.isEqualTo(pressed)) {
                        letter.show();
                    }
                }
            }
        });

        primaryStage.setResizable(false);
        primaryStage.setTitle("Hangman");
        primaryStage.setScene(scene);
        primaryStage.show();
        startGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
