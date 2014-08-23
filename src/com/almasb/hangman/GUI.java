package com.almasb.hangman;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

@SuppressWarnings("serial")
public class GUI extends JFrame {
    private JButton again = new JButton("NEW");

    /**
     * Indices to labels on the screen, i.e
     * labels[LETTERS] is the label that shows letters
     */
    private static final int LETTERS = 0,
            LIVES = 1,
            TYPED = 2,
            MESSAGE = 3;

    private JLabel[] labels = new JLabel[4];

    private Logic game = new Logic();

    public GUI() {
        game.newGame();

        setSize(330, 170);
        setTitle("Hangman 0.7 by Almas");
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(new KeyPress());

        for (int i = LETTERS; i <= MESSAGE; i++) {
            labels[i] = new JLabel();
            labels[i].setFont(new Font("Monospaced", Font.BOLD, 20));
            add(labels[i]);
        }

        labels[LETTERS].setText(game.getLetters());
        labels[LIVES].setText(game.getLives() + " lives");

        labels[LETTERS].setBounds(10, 10, 200, 40);
        labels[LIVES].setBounds(210, 10, 200, 40);
        labels[TYPED].setBounds(10, 60, 200, 40);
        labels[MESSAGE].setBounds(210, 40, 200, 40);

        again.setBounds(205, 80, 75, 30);
        again.addActionListener(event -> {
            again.setEnabled(false);
            GUI.this.requestFocus();

            game.newGame();
            labels[LETTERS].setText(game.getLetters());
            labels[LIVES].setText(game.getLives() + " lives");
            labels[MESSAGE].setText("");
        });
        again.setEnabled(false);
        add(again);

        setVisible(true);
    }

    private void gameOver(String message) {
        labels[MESSAGE].setText(message);
        labels[TYPED].setText("");
        labels[LETTERS].setText(game.getWord());
        again.setEnabled(true);
    }

    class KeyPress implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            char pressed = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase()
                    .charAt(0);

            String typedSoFar = labels[TYPED].getText();

            if (!game.guessed() && game.getLives() > 0
                    && !typedSoFar.contains(pressed + "")) {
                labels[TYPED].setText(typedSoFar + pressed);

                game.guess(pressed);
                labels[LETTERS].setText(game.getLetters());
                labels[LIVES].setText(game.getLives() + " lives");

                if (game.guessed()) {
                    gameOver("You won");
                }

                if (game.getLives() == 0) {
                    gameOver("You lost");
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyTyped(KeyEvent e) {}
    }
}
