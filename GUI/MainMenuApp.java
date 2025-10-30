
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Minimal standalone Main Menu for a Chess app (Swing).
 *
 * Requirements addressed:
 * - A single window with three buttons: Single Player, Multiplayer, Saved Game
 * - No real navigation yet (shows placeholder dialogs)
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
        btnSingle.addActionListener(e -> placeholder("Single Player"));
        btnMulti.addActionListener(e -> placeholder("Multiplayer"));
        btnSaved.addActionListener(e -> placeholder("Saved Game"));
        center.add(btnSingle, pos(gbc, row++));
        center.add(btnMulti,  pos(gbc, row++));
        center.add(btnSaved,  pos(gbc, row++));
        center.add(note("(Navigation not implemented yet)"), pos(gbc, row++));

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

    private void placeholder(String where) {
        JOptionPane.showMessageDialog(this,
                "TODO: navigate to " + where,
                "Not Implemented",
                JOptionPane.INFORMATION_MESSAGE);
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
    b.setBackground(Color.BLACK);     // 黑色背景
    b.setForeground(Color.WHITE);     // 白色文字（黑底更易读）
    b.setOpaque(true);        
    b.setContentAreaFilled(true);
    b.setBorderPainted(false);
    b.setFocusPainted(false);
    b.setFont(b.getFont().deriveFont(Font.BOLD, 16f));
    b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
}
}
