package com.almasb.hangman;

import java.util.HashMap;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

    private static final int APP_W = 600;
    private static final int APP_H = 300;
    private static final Font DEFAULT_FONT = new Font("Courier", 36);

    private static final int MAX_LIVES = 7;
    private static final int POINTS_PER_LETTER = 100;
    private static final float BONUS_MODIFIER = 0.2f;

    /**
     * The word to guess
     */
    private SimpleStringProperty word = new SimpleStringProperty();

    /**
     * How many letters left to guess
     */
    private SimpleIntegerProperty lettersToGuess = new SimpleIntegerProperty();

    /**
     * How many lives left
     */
    private SimpleIntegerProperty lives = new SimpleIntegerProperty();

    /**
     * Current score
     */
    private SimpleIntegerProperty score = new SimpleIntegerProperty();

    /**
     * How many points next correct letter is worth
     */
    private float scoreModifier = 1.0f;

    /**
     * Is game playable
     */
    private SimpleBooleanProperty playable = new SimpleBooleanProperty();

    /**
     * List for letters of the word {@link #word}
     * It is backed up by the HBox children list,
     * so changes to this list directly affect the GUI
     */
    private ObservableList<Node> letters;

    /**
     * K - characters [A..Z] and '-'
     * V - javafx.scene.Text representation of K
     */
    private HashMap<Character, Text> alphabet = new HashMap<Character, Text>();

    private WordReader wordReader = new WordReader();

    public Parent createContent() {
        HBox rowLetters = new HBox();
        rowLetters.setAlignment(Pos.CENTER);
        letters = rowLetters.getChildren();

        playable.bind(lives.greaterThan(0).and(lettersToGuess.greaterThan(0)));
        playable.addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                stopGame();
        });

        Button btnAgain = new Button("NEW GAME");
        btnAgain.disableProperty().bind(playable);
        btnAgain.setOnAction(event -> startGame());

        // layout
        HBox row1 = new HBox();
        HBox row3 = new HBox();
        row1.setAlignment(Pos.CENTER);
        row3.setAlignment(Pos.CENTER);
        for (int i = 0 ; i < 20; i++) {
            row1.getChildren().add(new Letter(' '));
            row3.getChildren().add(new Letter(' '));
        }

        HBox rowAlphabet = new HBox(5);
        rowAlphabet.setAlignment(Pos.CENTER);
        for (char c = 'A'; c <= 'Z'; c++) {
            Text t = new Text(String.valueOf(c));
            t.setFont(DEFAULT_FONT);
            alphabet.put(c, t);
            rowAlphabet.getChildren().add(t);
        }

        Text hyphen = new Text("-");
        hyphen.setFont(DEFAULT_FONT);
        alphabet.put('-', hyphen);
        rowAlphabet.getChildren().add(hyphen);

        Text textLives = new Text();
        textLives.textProperty().bind(lives.asString().concat(" Lives"));

        Text textScore = new Text();
        textScore.textProperty().bind(score.asString().concat(" Points"));

        VBox vBox = new VBox(10);
        vBox.setPrefSize(APP_W, APP_H);

        // vertical layout
        vBox.getChildren().addAll(
                row1,
                rowLetters,
                row3,
                rowAlphabet,
                new HBox(10, textLives, btnAgain, textScore));
        return vBox;
    }

    private void stopGame() {
        for (Node n : letters) {
            Letter letter = (Letter) n;
            letter.show();
        }
    }

    private void startGame() {
        for (Text t : alphabet.values()) {
            t.setStrikethrough(false);
            t.setFill(Color.BLACK);
        }

        lives.set(MAX_LIVES);
        word.set(wordReader.getRandomWord().toUpperCase());
        lettersToGuess.set(word.length().get());

        letters.clear();
        for (char c : word.get().toCharArray()) {
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
            if ((pressed < 'A' || pressed > 'Z') && pressed != '-')
                return;

            if (playable.get()) {
                Text t = alphabet.get(pressed);
                if (t.isStrikethrough())
                    return;

                // mark the letter 'used'
                t.setFill(Color.BLUE);
                t.setStrikethrough(true);

                boolean found = false;

                for (Node n : letters) {
                    Letter letter = (Letter) n;
                    if (letter.isEqualTo(pressed)) {
                        found = true;
                        score.set(score.get() + (int)(scoreModifier * POINTS_PER_LETTER));
                        lettersToGuess.set(lettersToGuess.get() - 1);
                        letter.show();
                    }
                }

                if (!found) {
                    lives.set(lives.get() - 1);
                    scoreModifier = 1.0f;
                }
                else {
                    scoreModifier += BONUS_MODIFIER;
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
