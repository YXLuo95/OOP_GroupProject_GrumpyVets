
package GUI;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Minimal standalone Main Menu for a Chess app (Swing).
 *
 * Requirements addressed:
 * - A single window with three buttons: Single Player, Multiplayer, Saved Game
 * - Single Player button opens a new window (Singleplayer.java)
 * - Multiplayer and Saved Game buttons show a placeholder dialog
 * - Clean, centered layout; ESC quits; Enter triggers focused button
 *
 * Compile: javac MainMenuApp.java
 * Run:     java MainMenuApp
 */
public class MainMenuApp {
    public static void main(String[] args) {
        // Use system look & feel if possible
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(MainMenuFrame::new);
    }
}

class MainMenuFrame extends JFrame {
    private final JButton btnSingle = new JButton("Single Player");
    private final JButton btnMulti  = new JButton("Multiplayer");
    private final JButton btnSaved  = new JButton("Saved Game");

    MainMenuFrame() {
        super("Chess – Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        center.setBackground(new Color(34, 40, 49));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        center.add(title("Chess"), pos(gbc, row++));
        center.add(subtitle("Main Menu"), pos(gbc, row++));
        styleBlack(btnSingle); styleBlack(btnMulti); styleBlack(btnSaved);
        btnSingle.addActionListener(e -> openSinglePlayer());
        btnMulti.addActionListener(e -> openMultiplayer());
        btnSaved.addActionListener(e -> openSavedGame());
        center.add(btnSingle, pos(gbc, row++));
        center.add(btnMulti,  pos(gbc, row++));
        center.add(btnSaved,  pos(gbc, row++));

        add(center, BorderLayout.CENTER);

        // Default button (Enter activates Single Player)
        getRootPane().setDefaultButton(btnSingle);

        // ESC to quit
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        center.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "quit");
        center.getActionMap().put("quit", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { dispose(); }
        });

        setSize(520, 380);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openSinglePlayer() {
        // Hide main menu
        setVisible(false);
        
        // Open single player game window
        SwingUtilities.invokeLater(() -> {
            Singleplayer singlePlayerWindow = new Singleplayer();
            singlePlayerWindow.setVisible(true);
            
            // When single player window closes, show main menu again
            singlePlayerWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    setVisible(true);
                }
            });
        });
    }

    private void openMultiplayer() {
        // Hide main menu and open multiplayer window
        setVisible(false);
        
        SwingUtilities.invokeLater(() -> {
            try {
                MultiplayerFrame multiplayerFrame = new MultiplayerFrame();
                multiplayerFrame.setVisible(true);
                
                // When multiplayer window closes, show main menu again
                multiplayerFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        setVisible(true);
                    }
                });
            } catch (Exception ex) {
                // If MultiplayerFrame fails to load, show error and return to menu
                JOptionPane.showMessageDialog(this, 
                    "Failed to open multiplayer window: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                setVisible(true);
            }
        });
    }

    private void openSavedGame() {
        // Import GameSave for this method
        try {
            Class<?> gameSaveClass = Class.forName("logic.GameSave");
            java.lang.reflect.Method listSaveFiles = gameSaveClass.getMethod("listSaveFiles");
            String[] saveFiles = (String[]) listSaveFiles.invoke(null);
            
            if (saveFiles.length == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No saved games found!", 
                    "Load Game", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String selectedFile = (String) JOptionPane.showInputDialog(this,
                "Select a saved game to load:",
                "Load Saved Game",
                JOptionPane.PLAIN_MESSAGE,
                null,
                saveFiles,
                saveFiles[0]);
            
            if (selectedFile != null) {
                // Hide main menu and open game with loaded state
                setVisible(false);
                
                SwingUtilities.invokeLater(() -> {
                    Singleplayer singlePlayerWindow = new Singleplayer();
                    
                    // Load the saved game into the window
                    try {
                        java.lang.reflect.Method loadGame = gameSaveClass.getMethod("loadGame", String.class);
                        Object gameSave = loadGame.invoke(null, selectedFile);
                        
                        if (gameSave != null) {
                            // Apply the save to the game session
                            java.lang.reflect.Method applyToGameSession = gameSave.getClass().getMethod("applyToGameSession", 
                                Class.forName("logic.GameSession"));
                            java.lang.reflect.Field gameSessionField = singlePlayerWindow.getClass().getDeclaredField("gameSession");
                            gameSessionField.setAccessible(true);
                            Object gameSession = gameSessionField.get(singlePlayerWindow);
                            applyToGameSession.invoke(gameSave, gameSession);
                            
                            // Update the display
                            java.lang.reflect.Method updateStatus = singlePlayerWindow.getClass().getDeclaredMethod("updateStatus");
                            updateStatus.setAccessible(true);
                            updateStatus.invoke(singlePlayerWindow);
                            
                            singlePlayerWindow.repaint();
                            
                            JOptionPane.showMessageDialog(singlePlayerWindow, 
                                "Game loaded successfully!", 
                                "Load Complete", 
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(singlePlayerWindow, 
                                "Failed to load game!", 
                                "Load Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(singlePlayerWindow, 
                            "Error loading game: " + ex.getMessage(), 
                            "Load Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                    singlePlayerWindow.setVisible(true);
                    
                    // When single player window closes, show main menu again
                    singlePlayerWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            setVisible(true);
                        }
                    });
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error accessing save system: " + e.getMessage(), 
                "System Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel title(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(new Color(238, 238, 238));
        l.setFont(l.getFont().deriveFont(Font.BOLD, 36f));
        return l;
    }
    private JLabel subtitle(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(new Color(210, 210, 210));
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 16f));
        return l;
    }
    private JLabel note(String t) {
        JLabel l = new JLabel(t, SwingConstants.CENTER);
        l.setForeground(new Color(170, 170, 170));
        l.setFont(l.getFont().deriveFont(Font.ITALIC, 12f));
        return l;
    }
    private GridBagConstraints pos(GridBagConstraints gbc, int row) {
        GridBagConstraints c = (GridBagConstraints) gbc.clone();
        c.gridy = row; return c;
    }

    private void styleBlack(JButton b) {
    b.setBackground(Color.BLACK);     // Black background
    b.setForeground(Color.WHITE);     // White text (better readability on black)
    b.setOpaque(true);        
    b.setContentAreaFilled(true);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
    b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
}
}
