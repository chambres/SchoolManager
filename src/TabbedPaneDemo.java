
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JFileChooser;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class TabbedPaneDemo extends JPanel {

    static TeacherView teacherView = new TeacherView();
    static StudentView studentView = new StudentView();
    static CourseView courseView = new CourseView();
    static SectionView sectionView = new SectionView();
    static Connection con;
    static int selectedtab;
    static JTabbedPane tabbedPane = new JTabbedPane();
    
    public TabbedPaneDemo() {
        super(new GridLayout(1, 1));

        //Added to import data
        try {
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/p2","root","password");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        ImageIcon icon = createImageIcon("images/middle.gif");
        
        JComponent panel1 = makeTextPanel("Teachers");
        tabbedPane.addTab("Teachers", icon, panel1,
                "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        
        JComponent panel2 = makeTextPanel("Students");
        tabbedPane.addTab("Students", icon, panel2,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        JComponent panel3 = makeTextPanel("Courses");
        tabbedPane.addTab("Courses", icon, panel3,
                "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        
        JComponent panel4 = makeTextPanel("Sections");
        panel4.setPreferredSize(new Dimension(410, 50));
        tabbedPane.addTab("Sections", icon, panel4,
                "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int index = tabbedPane.getSelectedIndex();
                //Added to find current tab - akshi
                selectedtab = index;
                System.out.println("Tab " + (index+1) + " opened.");
                if(selectedtab==3) {
                    System.out.println("Opening Section Tab " + (index+1) + " opened.");
                    sectionView.updateTable();
                    sectionView.resetStudentDropdown();
                }
                // Perform your desired function here
            }
        });
        
        //Add the tabbed pane to this panel.
        add(tabbedPane);
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        
    }
    
    protected static JComponent makeTextPanel(String text) {
        JPanel panel;
        //panel.setHorizontalAlignment(JLabel.CENTER);
        switch(text){
            case "Teachers":
                panel = teacherView;
                return panel;
            case "Students":
                panel = studentView;
                return panel;
            case "Courses":
                panel = courseView;
                return panel;
            case "Sections":
                panel = sectionView;
                return panel;
            default:
                panel = new JPanel(false);
                return panel;
        }

        
 
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TabbedPaneDemo.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from
     * the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TabbedPaneDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenu view = new JMenu("View");
        JMenu help = new JMenu("Help");

        JMenuItem exportItem = new JMenuItem(new AbstractAction("Export Data") {
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame();

                System.out.println(System.getProperty("user.dir") + "\\files" + selectedtab);
                switch(selectedtab){
                    case 0:
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\teachers.csv");
                        WriteTeachersDataIntoFile(System.getProperty("user.dir") + "\\files" + "\\teachers.csv");
                        break;
                    case 1:
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\students.csv");
                        WriteStudentsDataIntoFile(System.getProperty("user.dir") + "\\files" + "\\students.csv");
                        break;
                    case 2:
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\courses.csv");
                        WriteCoursesDataIntoFile(System.getProperty("user.dir") + "\\files" + "\\courses.csv");
                        break;
                    case 3:
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\sections.csv");
                        WriteSectionsDataIntoFile(System.getProperty("user.dir") + "\\files" + "\\sections.csv");
                        break;
                }
            }
        });

        JMenuItem importItem = new JMenuItem(new AbstractAction("Import Data") {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "\\files"));
                fileChooser.showOpenDialog(frame);
                File selFile = fileChooser.getSelectedFile();
                String fullFilename = selFile.toString();
                String fileName = fullFilename.substring(fullFilename.lastIndexOf("\\") + 1);
                try(BufferedReader in = new BufferedReader(new FileReader(fileChooser.getSelectedFile().getAbsolutePath()))) {
                    String str;
                    while ((str = in.readLine()) != null) {
                        System.out.println(str);
                    }
                }
                catch (Exception ex) {
                    System.out.println("File Read Error");
                }
                System.out.println("select file to export" + selFile);


                System.out.println("Filename" + fileName);
                switch(fileName){
                    case "teachers.csv":
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\teachers.csv");
                        PopulateTeachersForm(System.getProperty("user.dir") + "\\files" + "\\teachers.csv");
                        break;
                    case "students.csv":
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\students.csv");
                        PopulateStudentsForm(System.getProperty("user.dir") + "\\files" + "\\students.csv");
                        break;
                    case "courses.csv":
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\courses.csv");
                        PopulateCoursesForm(System.getProperty("user.dir") + "\\files" + "\\courses.csv");
                        break;
                    case "sections.csv":
                        System.out.println(System.getProperty("user.dir") + "\\files" + "\\sections.csv");
                        PopulateSectionsForm(System.getProperty("user.dir") + "\\files" + "\\sections.csv");
                        break;
                }
            }


        });


        JMenuItem purgeItem =  new JMenuItem(new AbstractAction("Purge Item") {
            public void actionPerformed(ActionEvent e) {
                //akshi - delete all records
                ResultSet result;
                result = performQuery("Truncate table students");
                result = performQuery("Truncate table teachers");
                result = performQuery("Truncate table courses");
                result = performQuery("Truncate table sections");


            }
        });

        JMenuItem exitItem =  new JMenuItem(new AbstractAction("Exit Item") {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        JMenuItem teacherViewItem = new JMenuItem(new AbstractAction("Teacher View") {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(0);
            }
        });

        JMenuItem studentViewItem = new JMenuItem(new AbstractAction("Student View") {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(1);
            }
        });

        JMenuItem courseViewItem = new JMenuItem(new AbstractAction("Course View") {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(2);
            }
        });

        JMenuItem sectionViewItem = new JMenuItem(new AbstractAction("Section View") {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setSelectedIndex(3);
            }
        });

        JMenuItem helpViewItem = new JMenuItem(new AbstractAction("Help") {
            public void actionPerformed(ActionEvent e) {

                AboutPage page = new AboutPage();
                page.setLocation(100,100);
                page.setSize(400, 300);
                page.setVisible(true);

            }
        });

        menu.add(exportItem);
        menu.add(importItem);
        menu.add(purgeItem);
        menu.add(exitItem);
        menuBar.add(menu);

        view.add(teacherViewItem);
        view.add(studentViewItem);
        view.add(courseViewItem);
        view.add(sectionViewItem);
        menuBar.add(view);

        help.add(helpViewItem);
        menuBar.add(help);

        frame.setJMenuBar(menuBar);

        //Add content to the window.
        frame.add(new TabbedPaneDemo(), BorderLayout.CENTER);
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }

    static ResultSet performQuery(String query){

        try{ Class.forName("com.mysql.jdbc.Driver"); return con.createStatement().executeQuery(query);}

        catch(Exception e){System.out.println(e);}

        return null;
    }

    public static void PopulateTeachersForm(String filename)  {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            performUpdate("truncate table teachers");
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                //read text file and updating database
//                performUpdate(String.format("insert into teachers(ID, FirstName, LastName)\nvalues (%d,'%s', '%s');", Integer.parseInt(values[0]), values[1], values[2]));

                //update form
                teacherView.firstNameField.setText(values[1].toString());
                teacherView.lastNameField.setText(values[2].toString());
                teacherView.idField.setText(values[0].toString());

                teacherView.submit.doClick();


            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void PopulateStudentsForm(String filename)  {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            performUpdate("truncate table students");
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                //read text file and updating database
//                performUpdate(String.format("insert into students(ID, FirstName, LastName)\nvalues (%d,'%s', '%s');", Integer.parseInt(values[0]), values[1], values[2]));

                //update form
                studentView.firstNameField.setText(values[1].toString());
                studentView.lastNameField.setText(values[2].toString());
                studentView.idField.setText(values[0].toString());

                studentView.submit.doClick();


            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void PopulateCoursesForm(String filename)  {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            performUpdate("truncate table courses");
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                //read text file and updating database
//                performUpdate(String.format("insert into courses(ID, CourseName, Type)\nvalues (%d,'%s', '%s');", Integer.parseInt(values[0]), values[1], values[2]));

                //update form
                courseView.CourseNameField.setText(values[1].toString());
                courseView.TypeField.setText(values[2].toString());
                courseView.idField.setText(values[0].toString());

                courseView.submit.doClick();


            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void PopulateSectionsForm(String filename)  {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            performUpdate("truncate table sections");
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                //read text file and updating database
//                performUpdate(String.format("insert into sections(ID, course_id, teacher_id)\nvalues (%d,'%s', '%s');", Integer.parseInt(values[0]), values[1], values[2]));

                //update form
                sectionView.courseSelection.setSelectedItem(Integer.parseInt(values[1]));
                sectionView.teacherSelection.setSelectedItem(Integer.parseInt(values[2]));
                sectionView.idField.setText(values[0].toString());

                sectionView.submit.doClick();


            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static int  performUpdate(String query){
        try{ Class.forName("com.mysql.jdbc.Driver"); return con.createStatement().executeUpdate(query);}

        catch(Exception e){System.out.println(e);}

        return -1;
    }

    public static void WriteTeachersDataIntoFile(String filename)  {
        List<String> resultSetArray=new ArrayList<>();
        String username ="";     // Enter DB Username
        String password = "";    // Enter DB password
        String url = "";         // Enter DB URL

        
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/p2","root","password");
            fetchDataFromDatabase("select * from teachers", con, filename);

        } catch (Exception e) {
            System.out.println("Sql exception " + e.getMessage());
        }

    }

    public static void WriteStudentsDataIntoFile(String filename)  {
        List<String> resultSetArray=new ArrayList<>();
        String username ="";     // Enter DB Username
        String password = "";    // Enter DB password
        String url = "";         // Enter DB URL
        
        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/p2","root","password");

            fetchDataFromDatabase("select * from students", con, filename);

        } catch (Exception e) {
            System.out.println("Sql exception " + e.getMessage());
        }

    }

    public static void WriteCoursesDataIntoFile(String filename)  {
        List<String> resultSetArray=new ArrayList<>();
        String username ="";     // Enter DB Username
        String password = "";    // Enter DB password
        String url = "";         // Enter DB URL


        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/p2","root","password");

            fetchDataFromDatabase("select * from courses", con, filename);

        } catch (Exception e) {
            System.out.println("Sql exception " + e.getMessage());
        }

    }

    public static void WriteSectionsDataIntoFile(String filename)  {
        List<String> resultSetArray=new ArrayList<>();
        String username ="";     // Enter DB Username
        String password = "";    // Enter DB password
        String url = "";         // Enter DB URL


        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/p2","root","password");

            fetchDataFromDatabase("select * from sections", con, filename);

        } catch (Exception e) {
            System.out.println("Sql exception " + e.getMessage());
        }

    }

    private static void fetchDataFromDatabase(String selectQuery, java.sql.Connection con, String filename) throws  Exception{
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);
            int numCols = rs.getMetaData().getColumnCount();
            List<String> resultSetArray=new ArrayList<>();

            while(rs.next()) {
                StringBuilder sb = new StringBuilder();

                for (int i = 1; i <= numCols; i++) {
                    if (i != numCols) {
                        sb.append(String.format(String.valueOf(rs.getString(i))) + ",");
                    }
                    else
                        sb.append(String.format(String.valueOf(rs.getString(i))));
//                    resultSetArray.add(sb.toString());
                }

                resultSetArray.add(sb.toString());

            }
            printToCsv(resultSetArray, filename);

        } catch (SQLException e) {
            System.out.println("Sql exception " + e.getMessage());
        }

    }

    public static void printToCsv(List<String> resultArray, String filename) throws Exception{

        File csvOutputFile = new File(filename);
        FileWriter fileWriter = new FileWriter(csvOutputFile, false);

        for(int i=0;i<resultArray.size();i++){
            if(i!= resultArray.size()-1)
             fileWriter.write(resultArray.get(i) + "\n");
            else
                fileWriter.write(resultArray.get(i));
        }
        fileWriter.close();

    }
    
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		createAndShowGUI();
            }
        });
    }
}