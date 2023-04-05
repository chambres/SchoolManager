import javax.swing.*;
import java.awt.*;



public class AboutPage extends JPanel {
    private JFrame frame = new JFrame("Test");
    private JPanel panel = new JPanel();
    private JLabel label = new JLabel("School Manager v1.0.0 -- Tompkins High School");

    public AboutPage() {
        panel.setLayout(new GridBagLayout());
        panel.add(label);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }



}
