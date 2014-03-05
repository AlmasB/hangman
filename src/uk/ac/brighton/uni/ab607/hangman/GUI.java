package uk.ac.brighton.uni.ab607.hangman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class GUI extends JFrame {
    /**
     * Serializable
     */
    private static final long serialVersionUID = 137507991201396188L;

    private static final int LM = 10;
    private static final int TM = 10;

    private static final String BTN_NEW = "NEW";
    private JButton again = new JButton(BTN_NEW);
    private ButtonPress onBtnPress = new ButtonPress();

    private CustomKeyPress onKey = new CustomKeyPress();

    private JLabel[] labels = new JLabel[4];    // TODO: provide final int indexes to array

    private Logic game = new Logic();

    public GUI() {
        game.newGame();

        setSize(330, 170);
        setTitle("Hangman 0.5 by Almas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(onKey);

        Container cp = getContentPane();
        cp.setLayout(null);

        Font font = new Font("Monospaced", Font.BOLD, 20);

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            labels[i].setFont(font);
            cp.add(labels[i]);
        }

        labels[0].setText(game.getLetters());
        labels[1].setText(game.getLives() + " lives");

        labels[0].setBounds(LM, TM, 200, 40);
        labels[1].setBounds(LM + 200, TM, 200, 40);
        labels[2].setBounds(LM, TM + 50, 200, 40);
        labels[3].setBounds(LM + 200, TM + 30, 200, 40);

        again.setBounds(LM + 195, TM + 70, 75, 30);
        again.addActionListener(onBtnPress);
        again.setEnabled(false);
        cp.add(again);

        setVisible(true);
    }

    private void gameOver(String message) {
        labels[3].setText(message);
        labels[2].setText("");
        labels[0].setText(game.getWord());
        again.setEnabled(true);
    }

    class CustomKeyPress implements KeyListener {
        @Override
        public void keyPressed(KeyEvent e) {
            char pressed = KeyEvent.getKeyText(e.getKeyCode()).toLowerCase()
                    .charAt(0);

            String typedSoFar = labels[2].getText();

            if (!game.guessed() && game.getLives() > 0
                    && !typedSoFar.contains(pressed + "")) {
                labels[2].setText(typedSoFar + pressed);

                game.guess(pressed);
                labels[0].setText(game.getLetters());
                labels[1].setText(game.getLives() + " lives");

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

    class ButtonPress implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ae) {
            if (ae.getActionCommand() == BTN_NEW) {
                again.setEnabled(false);
                GUI.this.requestFocus();

                game.newGame();
                labels[0].setText(game.getLetters());
                labels[1].setText(game.getLives() + " lives");
                labels[3].setText("");
            }
        }
    }
}
