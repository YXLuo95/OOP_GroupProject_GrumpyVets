import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class CounterApp {
    //initialize count and label
    private int count = 0;
    private final JLabel countLabel = new JLabel("0", SwingConstants.CENTER);

    //create and show UI
    private void createAndShowUI() {
        
        JFrame frame = new JFrame("Counter Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD, 36f));

        JButton incBtn = new JButton("+");
        JButton decBtn = new JButton("-");
        //wire buttons to increment and decrement methods
        incBtn.addActionListener(this::increment);
        decBtn.addActionListener(this::decrement);

        //layout buttons in a panel
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        buttons.add(decBtn);
        buttons.add(incBtn);

        frame.setLayout(new BorderLayout(10, 10));
        frame.add(countLabel, BorderLayout.CENTER);
        frame.add(buttons, BorderLayout.SOUTH);

        frame.setSize(300, 180);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void increment(ActionEvent e) {
        count++;
        countLabel.setText(Integer.toString(count));
    }

    private void decrement(ActionEvent e) {
        count--;
        countLabel.setText(Integer.toString(count));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CounterApp().createAndShowUI());
    }
}
