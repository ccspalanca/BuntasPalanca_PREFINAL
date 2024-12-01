// Made by Gabriel Nicholas O. Buntas - Carl Chester S. Palanca

package com.mycompany.memorytraininggame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MemoryTrainingGame extends JFrame {
    private JButton[] cards;
    private ArrayList<Integer> sequence;
    private ArrayList<Integer> playerInput;
    private int round;
    private boolean isGameRunning;
    private boolean isSequenceShowing;
    private JDialog gameOverDialog;

    public MemoryTrainingGame() {
        round = 1;
        sequence = new ArrayList<>();
        playerInput = new ArrayList<>();
        isGameRunning = false;
        isSequenceShowing = false;

        setTitle("Memory Training Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));

        cards = new JButton[9];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new JButton();
            cards[i].setBackground(Color.LIGHT_GRAY);
            cards[i].setFocusable(false);
            cards[i].addActionListener(new CardClickListener(i));
            add(cards[i]);
        }

        setLocationRelativeTo(null);
        setVisible(true);
        showCreditsAndWelcome();
    }

    private void showCreditsAndWelcome() {
        String credits = """
                Welcome to the Memory Training Game!

                Developers:
                1. Gabriel Nicholas O. Buntas
                   Personal Mobile: 09676288552
                   Role: Code and Presentation

                2. Carl Chester S. Palanca
                   Personal Mobile: 09924863166
                   Role: Code and Debug
                """;
        JOptionPane.showMessageDialog(this, credits, "Game Credits", JOptionPane.INFORMATION_MESSAGE);

        showWelcomePrompts();
    }

    private void showWelcomePrompts() {
        JOptionPane.showMessageDialog(this, "Welcome to the Memory Training Game!", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        int startGame = JOptionPane.showConfirmDialog(this, "Press Start to begin the game.", "Start Game", JOptionPane.DEFAULT_OPTION);

        if (startGame == JOptionPane.OK_OPTION) {
            isGameRunning = true;
            startRound();
        }
    }

    private void startRound() {
        if (!isGameRunning) {
            return;
        }

        playerInput.clear();
        sequence.add((int) (Math.random() * cards.length));

        isSequenceShowing = true;
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                for (int index : sequence) {
                    cards[index].setBackground(Color.BLUE);
                    Thread.sleep(500);
                    cards[index].setBackground(Color.LIGHT_GRAY);
                    Thread.sleep(200);
                }
                isSequenceShowing = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkPlayerInput() {
        if (playerInput.equals(sequence)) {
            JOptionPane.showMessageDialog(this, "Round " + round + " completed!");
            round++;
            startRound();
        } else {
            new Thread(() -> {
                try {
                    for (JButton card : cards) {
                        card.setBackground(Color.RED);
                    }
                    Thread.sleep(1000);
                    for (JButton card : cards) {
                        card.setBackground(Color.LIGHT_GRAY);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            showGameOverPrompt();
        }
    }

    private void showGameOverPrompt() {
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> restartGame());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel message = new JLabel("Game Over! You reached round " + round);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(message);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(restartButton);

        gameOverDialog = new JDialog(this, "Game Over", true);
        gameOverDialog.setLayout(new BorderLayout());
        gameOverDialog.add(panel, BorderLayout.CENTER);
        gameOverDialog.pack();
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setVisible(true);
    }

    private void restartGame() {
        gameOverDialog.dispose();
        round = 1;
        sequence.clear();
        playerInput.clear();
        isGameRunning = true;
        isSequenceShowing = false;

        for (JButton card : cards) {
            card.setBackground(Color.LIGHT_GRAY);
        }

        new Thread(() -> {
            try {
                Thread.sleep(500);
                startRound();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private class CardClickListener implements ActionListener {
        private final int cardIndex;

        public CardClickListener(int cardIndex) {
            this.cardIndex = cardIndex;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (isSequenceShowing) {
                return;
            }

            playerInput.add(cardIndex);

            for (int i = 0; i < playerInput.size(); i++) {
                if (!playerInput.get(i).equals(sequence.get(i))) {
                    checkPlayerInput();
                    return;
                }
            }

            if (playerInput.size() == sequence.size()) {
                checkPlayerInput();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryTrainingGame().setVisible(true));
    }
}
