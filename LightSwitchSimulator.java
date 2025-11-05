import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/** Exercise 2: Light Switch Simulator */
public class LightSwitchSimulator {

    private boolean isOn = false;            // light starts OFF

    // create label and button
    private final JLabel stateLabel = new JLabel("Light is OFF", SwingConstants.CENTER);
    private final JButton switchButton = new JButton("Turn ON");

    private void createAndShowUI() {
        JFrame frame = new JFrame("Light Switch Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make the label easy to see
        stateLabel.setFont(stateLabel.getFont().deriveFont(Font.BOLD, 22f));

        // Wire the button to toggle the state
        switchButton.addActionListener(this::toggleLight);

        // Simple layout: label on top, button at bottom
        frame.setLayout(new BorderLayout(12, 12));
        frame.add(stateLabel, BorderLayout.CENTER);
        frame.add(switchButton, BorderLayout.SOUTH);

        frame.setSize(320, 180);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void toggleLight(ActionEvent e) {
        isOn = !isOn;
        stateLabel.setText(isOn ? "Light is ON" : "Light is OFF");
        switchButton.setText(isOn ? "Turn OFF" : "Turn ON");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LightSwitchSimulator().createAndShowUI());
    }
}
