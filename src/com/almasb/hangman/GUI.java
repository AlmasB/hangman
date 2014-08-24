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
        setResizable(false);
        setTitle("Java Swing GUI");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(new KeyPress());

        for (int i = LETTERS; i <= MESSAGE; i++) {
            labels[i] = new JLabel();
            labels[i].setFont(new Font("Monospaced", Font.BOLD, 20));
        }

        again.addActionListener(event -> {
            labels[MESSAGE].setText("");
            again.setEnabled(false);
            GUI.this.requestFocus();
            game.newGame();
            pack();
        });
        again.setEnabled(false);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(labels[LETTERS])
                        .addGap(50)
                        .addComponent(labels[LIVES])
                        .addComponent(again))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(labels[TYPED])
                                .addComponent(labels[MESSAGE]))
                );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(labels[LETTERS])
                        .addComponent(labels[LIVES])
                        .addComponent(again))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(labels[TYPED])
                                .addComponent(labels[MESSAGE]))
                );

        game.livesProperty().addListener((observable, oldValue, newValue) -> {
            labels[LIVES].setText(newValue.intValue() + " lives");
            pack();
        });
        game.playableProperty().addListener((obs, old, newValue) -> {
            if (!newValue.booleanValue())
                gameOver(game.guessed() ? "You win" : "You lose");
        });
        game.guessedLettersProperty().addListener((obs, old, newValue) -> {
            labels[LETTERS].setText(newValue);
            pack();
        });

        game.newGame();
        setVisible(true);
        pack();
    }

    private void gameOver(String message) {
        labels[MESSAGE].setText(message);
        labels[TYPED].setText("");
        again.setEnabled(true);
    }

    private class KeyPress implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            char pressed = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase().charAt(0);

            String typedSoFar = labels[TYPED].getText();

            if (game.playableProperty().get() && !typedSoFar.contains(pressed + "")) {
                labels[TYPED].setText(typedSoFar + pressed);
                game.guess(pressed);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {}
        @Override
        public void keyTyped(KeyEvent e) {}
    }
}
