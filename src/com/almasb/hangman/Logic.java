package com.almasb.hangman;

import java.util.Arrays;

public class Logic {
    private static final int MAX_LIVES = 7;

    private String word = "";
    private char[] letters;
    private int lives = 0;

    private WordReader wr = new WordReader();

    public void newGame() {
        lives = MAX_LIVES;

        word = wr.getRandomWord();
        letters = new char[word.length()];
        Arrays.fill(letters, '.');
    }

    public void guess(char c) {
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == c) {
                found = true;
                letters[i] = c;
            }
        }

        if (!found)
            --lives;
    }

    public boolean guessed() {
        for (char c : letters)
            if (c == '.')
                return false;

        return true;
    }

    public String getLetters() {
        return new String(letters);
    }

    public int getLives() {
        return lives;
    }

    public String getWord() {
        return word;
    }
}
