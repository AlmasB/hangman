package com.almasb.hangman;

import java.util.Arrays;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Logic {
    private static final int MAX_LIVES = 7;

    private String word = "";
    private char[] letters;
    private SimpleStringProperty guessedLetters = new SimpleStringProperty();
    private SimpleIntegerProperty lives = new SimpleIntegerProperty();
    private SimpleBooleanProperty playable = new SimpleBooleanProperty();

    private WordReader wr = new WordReader();

    public Logic() {
        playable.addListener((obs, oldValue, newValue) -> {
            if (!newValue.booleanValue())
                guessedLetters.set(word);
        });
        lives.addListener((obs, oldValue, newValue) -> {
            if (newValue.intValue() == 0)
                playable.set(false);
        });
        guessedLetters.addListener((obs, old, newValue) -> {
            if (!newValue.contains("."))
                playable.set(false);
        });
    }

    public void newGame() {
        lives.set(MAX_LIVES);
        word = wr.getRandomWord();
        letters = new char[word.length()];
        Arrays.fill(letters, '.');
        guessedLetters.set(new String(letters));
        playable.set(true);
    }

    public void guess(char c) {
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                found = true;
                letters[i] = c;
                guessedLetters.set(new String(letters));
            }
        }

        if (!found)
            lives.set(lives.get() - 1);
    }

    public boolean guessed() {
        for (char c : letters)
            if (c == '.')
                return false;

        return true;
    }

    public SimpleIntegerProperty livesProperty() {
        return lives;
    }

    public SimpleBooleanProperty playableProperty() {
        return playable;
    }

    public SimpleStringProperty guessedLettersProperty() {
        return guessedLetters;
    }
}
