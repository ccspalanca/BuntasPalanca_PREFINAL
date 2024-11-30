package com.mycompany.memorytraininggame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MemoryTrainingGame extends JFrame {
    private JButton[] cards; // Array to hold card buttons
    private ArrayList<Integer> sequence; // Stores the sequence of lit cards
    private ArrayList<Integer> playerInput; // Stores the player's input
    private int round; // Tracks the current round
    private boolean isGameRunning; // Tracks if the game has started
    private boolean isSequenceShowing; // Prevent pressing tiles while sequence is showing
    private JDialog gameOverDialog; // To keep track of the Game Over dialog

    public MemoryTrainingGame() {
        // Initialize game state
        round = 1;
        sequence = new ArrayList<>();
        playerInput = new ArrayList<>();
        isGameRunning = false;
        isSequenceShowing = false; // Initially, the sequence is not being shown

        // Setup JFrame
        setTitle("Memory Training Game");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3)); // 3x3 grid of cards

        // Create cards (but don't start the rounds yet)
        cards = new JButton[9];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new JButton();
            cards[i].setBackground(Color.LIGHT_GRAY);
            cards[i].setFocusable(false);
            cards[i].addActionListener(new CardClickListener(i));
            add(cards[i]);
        }

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Show the game window first, then show prompts
        setVisible(true);
        showWelcomePrompts();
    }

    private void showWelcomePrompts() {
        // First prompt: Welcome message
        JOptionPane.showMessageDialog(this, "Welcome to the Memory Training Game!", "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Second prompt: Press Start to begin the game
        int startGame = JOptionPane.showConfirmDialog(this, "Press Start to begin the game.", "Start Game", JOptionPane.DEFAULT_OPTION);

        if (startGame == JOptionPane.OK_OPTION) {
            isGameRunning = true;
            startRound(); // Start the first round when the game is ready
        }
    }

    private void startRound() {
        if (!isGameRunning) {
            return;
        }

        playerInput.clear(); // Clear player's input for the new round
        sequence.add((int) (Math.random() * cards.length)); // Add a random card to the sequence

        // Show the sequence with a delay before it starts
        isSequenceShowing = true; // Sequence is now showing
        new Thread(() -> {
            try {
                // Wait for a few seconds before showing the sequence
                Thread.sleep(2000); // 2-second delay before starting the sequence

                for (int index : sequence) {
                    cards[index].setBackground(Color.BLUE); // Light up the card in blue
                    Thread.sleep(500); // Pause for visibility
                    cards[index].setBackground(Color.LIGHT_GRAY); // Reset color
                    Thread.sleep(200); // Short pause before the next light-up
                }

                // Sequence finished showing, allow player to press tiles
                isSequenceShowing = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void checkPlayerInput() {
        if (playerInput.equals(sequence)) {
            // Correct sequence
            JOptionPane.showMessageDialog(this, "Round " + round + " completed!");
            round++;
            startRound(); // Proceed to the next round
        } else {
            // Incorrect sequence
            new Thread(() -> {
                try {
                    for (JButton card : cards) {
                        card.setBackground(Color.RED); // Light all cards red for failure
                    }
                    Thread.sleep(1000); // Keep the red light for a moment
                    for (JButton card : cards) {
                        card.setBackground(Color.LIGHT_GRAY); // Reset all cards
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
            // Show "Restart" button on Game Over
            showGameOverPrompt();
        }
    }

    private void showGameOverPrompt() {
        // Create a "Restart" button
        JButton restartButton = new JButton("Restart");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame(); // Restart the game when clicked
            }
        });

        // Create a panel for message and restart button
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Set vertical layout

        // Create and add the "Game Over" message
        JLabel message = new JLabel("Game Over! You reached round " + round);
        message.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the message
        panel.add(message);

        // Add some space between the message and the button
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add the "Restart" button
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align the button
        panel.add(restartButton);

        // Create a custom modal dialog
        gameOverDialog = new JDialog(this, "Game Over", true);
        gameOverDialog.setLayout(new BorderLayout());
        gameOverDialog.add(panel, BorderLayout.CENTER);
        gameOverDialog.pack();
        gameOverDialog.setLocationRelativeTo(this);
        gameOverDialog.setVisible(true);
    }

    private void restartGame() {
        // Close the "Game Over" dialog immediately
        gameOverDialog.dispose();

        // Reset all game variables
        round = 1;
        sequence.clear();
        playerInput.clear();
        isGameRunning = true;
        isSequenceShowing = false;

        // Reset all card colors
        for (JButton card : cards) {
            card.setBackground(Color.LIGHT_GRAY);
        }

        // Start a new round after a 2-second delay
        new Thread(() -> {
            try {
                // Wait for a brief period (2-3 seconds) before starting the new round
                Thread.sleep(500); // 2-second delay before the round starts
                startRound(); // Start the round after the delay
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
            // If sequence is still showing, don't let the player press the tile
            if (isSequenceShowing) {
                return;
            }

            playerInput.add(cardIndex);

            // Check if player's input matches the sequence so far
            for (int i = 0; i < playerInput.size(); i++) {
                if (!playerInput.get(i).equals(sequence.get(i))) {
                    checkPlayerInput(); // Input is incorrect
                    return;
                }
            }

            // If player's input matches the sequence, but it's incomplete
            if (playerInput.size() == sequence.size()) {
                checkPlayerInput();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MemoryTrainingGame().setVisible(true));
    }
}
