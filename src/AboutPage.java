import javax.swing.*;


public class AboutPage extends JPanel {
        public AboutPage() {

            JEditorPane editorPane = new JEditorPane();
            editorPane.setContentType("text/plain");
            editorPane.setText("School Manager Application - Tompkins HighSchool");

            JFrame frame = new JFrame("About Page");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setContentPane(editorPane);
            frame.setVisible(true);
            frame.setSize(300, 300);


        }


}
