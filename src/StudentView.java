import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.sql.*;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;



public class StudentView extends JPanel {
    Connection con;






    JTextField firstNameField = new JTextField();
    JTextField lastNameField = new JTextField();
    JTextField idField = new JTextField();

    JButton submit;
    JButton clear;
    JButton saveChanges;
    JButton deleteContact;
    JTable tableCourses;

    String[][] coursesForStudent = {};
    String[] columnNamesForCourses = { "SectionID", "Course" };
    DefaultTableModel modelCourses;

    ActionListener b = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Button clicked");
            ContactButton tmp = (ContactButton) e.getSource();
            firstNameField.setText(tmp.fname);
            lastNameField.setText(tmp.lname);

            saveChanges.setEnabled(true);
            deleteContact.setEnabled(true);
            submit.setEnabled(false);
            clear.setEnabled(false);

            current = tmp;

            System.out.println("Select Student");

            // create the table model with one column named "SectionID" and CourseName
            DefaultTableModel modelCourses = new DefaultTableModel();
            modelCourses.addColumn("SectionID");
            modelCourses.addColumn("CourseName");

            ResultSet a = performQuery("SELECT section FROM sections where students.ID=" + Integer.parseInt((idField.getText())));
            try {
                a.next();
                String[] sections = a.getString("1").split(":");
                for(int i=0; i<sections.length;i++) {
                    String query = "SELECT sections.ID as SectionID, courses.CourseName as CourseName FROM sections, courses where sections.ID=" + sections[i] + " and courses.ID=sections.course_id";
                    ResultSet courses = performQuery(query);
                    courses.next();
                    modelCourses.addRow(new Object[] {courses.getString("SectionID"),courses.getString("CourseName") });
                }

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            // add the student names to the table model

            DefaultTableModel model1 = (DefaultTableModel) tableCourses.getModel();

            model1.setColumnIdentifiers(new String[]{"SectionID", "CourseName"});
            model1.setRowCount(0);
            model1 = modelCourses;
            tableCourses.setBounds(180-60+60-30-130 ,200+20+70+70+40, 360, tableCourses.getRowCount()*17);
            tableCourses.setModel(model1);
        }

    };

    static ArrayList<ContactButton> contactList = new ArrayList<ContactButton>();

    ContactButton current = null;

    JScrollPane scrollPane;


    int maxPanelWidth = 450; // set the maximum width of the panel

    private JPanel buttonPanel = new JPanel(new GridLayout(0, 1)); // 1 column grid
    public StudentView() {



        try{
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/p2","root","password");
        }
        catch(Exception e){ System.out.println(e);}

        int rs = performUpdate("create table students (ID int auto_increment primary key,\nFirstName varchar(500) NOT null,\nLastName varchar(500) NOT null,\nSection varchar(500)\n);");




        System.out.println(rs);


        File f = new File("contacts.txt");

        Start();

        if(f.exists() && !f.isDirectory()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get("contacts.txt"), java.nio.charset.StandardCharsets.UTF_8);
                for(String line: lines) {
                    String[] parts = line.split(",");
                    ContactButton button = new ContactButton(parts[0], parts[1]);
                    button.addActionListener(b);
                    contactList.add(button);

                    System.out.println(line);

                }
            } catch (IOException ex) {
                System.out.format("I/O error: %s%n", ex);
            }
        }

        reloadButtons();

    }

    int getNextIncrement(){
        ResultSet a = performQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM students;");
        int maxID = 0;
        try{
            if(a.next()) {
                maxID = a.getInt(1);
            }
        }
        catch(Exception e){ System.out.println(e);}
        return maxID;
    }

    JPanel rightPanel;
    void Start(){





        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonPanel, BorderLayout.PAGE_START);
        scrollPane = new JScrollPane(panel);
        scrollPane.getViewport().setPreferredSize(new Dimension(450, 500));


        JPanel topPanel = new JPanel();

        int gap = 2;
        setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
        setLayout(new BorderLayout(gap, gap));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(850, 500));
        centerPanel.setMaximumSize(new Dimension(450, 500));
        centerPanel.add(scrollPane, BorderLayout.WEST);

        rightPanel = new JPanel(null);
        rightPanel.setPreferredSize(new Dimension(400, 500));
        rightPanel.setMaximumSize(new Dimension(400, 500));
        rightPanel.setBackground(Color.green);

        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        JLabel idLabel = new JLabel("ID:");



        int labelX = 40; // set the X position of the labels
        int labelY = 30; // set the Y position of the first label
        int labelHeight = 20; // set the height of the labels
        int labelGap = 10; // set the vertical gap between the labels

        JLabel[] labels = {firstNameLabel, lastNameLabel, idLabel}; // store the labels in an array
        for(JLabel label : labels) { // loop through the labels
            label.setBounds(labelX, labelY, 100, labelHeight); // hardcode the position and size of the label
            labelY += labelHeight + labelGap; // update the Y position for the next label
            rightPanel.add(label);
        }

        labelY = 30;
        idField.setEditable(false);
        idField.setText(Integer.toString(getNextIncrement()));
        JTextField[] fields = {firstNameField, lastNameField, idField}; // store the text fields in an array
        for(JTextField field : fields) { // loop through the text fields
            field.setBounds(labelX+100, labelY, 220, labelHeight); // hardcode the position and size of the text field
            labelY += labelHeight + labelGap; // update the Y position for the next text field
            rightPanel.add(field);
        }
        int offset = 30;
        int yOff = 20;
        saveChanges = new JButton("Save");
        saveChanges.setBounds(180-60+60-offset, 200-50 + yOff, 100, 20);
        rightPanel.add(saveChanges);
        saveChanges.addActionListener(saveButtonListener());
        //turn off
        saveChanges.setEnabled(false);

        deleteContact = new JButton("Delete");
        deleteContact.setBounds(180-60+60-offset, 200-20 + yOff, 100, 20);
        rightPanel.add(deleteContact);
        deleteContact.addActionListener(deleteButtonListener());
        //turn off
        deleteContact.setEnabled(false);

        submit = new JButton("Submit");
        submit.setBounds(180-60+60-offset, 200+20 + yOff, 100, 20);
        rightPanel.add(submit);
        submit.addActionListener(submitButtonListener());

        clear = new JButton("Clear");
        clear.setBounds(180-60+60-offset, 200+20+70 + yOff, 100, 20);
        clear.addActionListener(clearListener());
        rightPanel.add(clear);

        JButton test = new JButton("Test");
        test.setBounds(180-60+60-offset, 200+20+70+70+00, 100, 20);
        test.addActionListener(testListener());
        rightPanel.add(test);

        // create the table with the table model
        modelCourses = new DefaultTableModel();
        modelCourses.addColumn("SectionID");
        modelCourses.addColumn("CourseName");
        tableCourses = new JTable(modelCourses);
        tableCourses.setBackground(Color.red);
        tableCourses.setBounds(180-60+60-offset-130 ,200+20+70+70+40, 360, tableCourses.getRowCount()*17);
        rightPanel.add(tableCourses);
        centerPanel.add(rightPanel, BorderLayout.EAST);


        add(topPanel, BorderLayout.PAGE_START);
        add(centerPanel, BorderLayout.CENTER);
    }



    ActionListener testListener(){

        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Test");
                for (int i = 1; i <= 21; i++) { // add some sample buttons
                    JButton button = new JButton("Button " + i);
                    button.setAlignmentX(Component.CENTER_ALIGNMENT); // set the alignment of the button to center
                    button.setHorizontalAlignment(SwingConstants.LEFT); // left align the text on the button
                    button.setMaximumSize(new Dimension(maxPanelWidth, button.getPreferredSize().height)); // set the maximum size of the button to the maximum width of the panel and the preferred height of the button
                    buttonPanel.add(Box.createRigidArea(new Dimension(0, 5))); // add a 5-pixel vertical gap between buttons
                    ContactButton tmp = new ContactButton("Last Name " + i, "John " + i);
                    tmp.addActionListener(b);
                    contactList.add(tmp);

                    //buttonPanel.add(button);
                    reloadButtons();

                    buttonPanel.revalidate(); // tell the panel to update its layout

                    scrollPane.setViewportView(buttonPanel);
                    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                    scrollPane.revalidate();
                    setVisible(true);
                }
            }
        };
    }

    ActionListener clearListener(){
        ActionListener a = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                firstNameField.setText("");
                lastNameField.setText("");
            }
        };
        return a;
    }


    ResultSet performQuery(String query){

        try{ Class.forName("com.mysql.jdbc.Driver"); return con.createStatement().executeQuery(query);}

        catch(Exception e){System.out.println(e);}

        return null;
    }

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }
        return new DefaultTableModel(data, columnNames);
    }

    int performUpdate(String query){
        try{ Class.forName("com.mysql.jdbc.Driver"); return con.createStatement().executeUpdate(query);}

        catch(Exception e){System.out.println(e);}

        return -1;
    }



    void addButton(){
        String fname = firstNameField.getText();
        String lname = lastNameField.getText();


        try{

            performUpdate(String.format("insert into students(FirstName, LastName)\nvalues ('%s', '%s');", fname, lname));


            ResultSet b = performQuery("select * from students");
            while(b.next()){
                System.out.println(b.getString("ID") + " " + b.getString("FirstName") + " " + b.getString("LastName"));
            }
        }
        catch(Exception e){ System.out.println(e);}

        ContactButton button = new ContactButton(fname, lname);

        ActionListener b = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button clicked");
                ContactButton tmp = (ContactButton) e.getSource();
                firstNameField.setText(tmp.fname);
                lastNameField.setText(tmp.lname);

                saveChanges.setEnabled(true);
                deleteContact.setEnabled(true);
                submit.setEnabled(false);
                clear.setEnabled(false);

                ResultSet b = performQuery("SELECT id FROM students WHERE FirstName = + '" + fname + "' AND LastName = '" + lname + "';");
                try{
                    while(b.next()){
                        idField.setText(b.getString("ID"));
                    }
                }
                catch(Exception e1){System.out.println(e1);}

                current = tmp;

                reloadButtons();
                System.out.println("Select Student");
                // create the table model with one column named "SectionId" and CourseName
                modelCourses = new DefaultTableModel();
                modelCourses.setColumnIdentifiers(new String[]{"SectionId", "CourseName"});
                if(idField.getText()!=null && idField.getText()!="") {
                    System.out.println("SELECT section FROM students where students.ID=" + Integer.parseInt((idField.getText())));
                    ResultSet a = performQuery("SELECT section FROM students where students.ID=" + Integer.parseInt((idField.getText())));

                    try {
                        if (a != null && a.next() ) {

                            if (a.getString(1) != null) {
                                String[] sections = a.getString(1).split(":");
                                System.out.println(Arrays.toString(sections));
                                if(sections.length > 0) {
                                    for (int i = 0; i < sections.length; i++) {
                                        String query = "SELECT sections.ID as sectionID, courses.CourseName FROM sections, courses where sections.ID=" + sections[i] + " and courses.ID=sections.course_id";
                                        System.out.println(query);
                                        ResultSet courses = performQuery(query);
                                        courses.next();
                                        modelCourses.addRow(new Object[]{courses.getString("sectionID"), courses.getString("CourseName")});
                                    }
                                }
                            }
                            tableCourses.setBackground(Color.red);
                            tableCourses.setAutoCreateRowSorter(true);
                            tableCourses.setModel(modelCourses);
                            tableCourses.setBounds(180-60+60-30-130 ,200+20+70+70+40, 360, tableCourses.getRowCount()*17);
                            tableCourses.setName("tableCourses");
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        };
        button.addActionListener(b);
        contactList.add(button);
        reloadButtons();
    }

    void reloadButtons() {

        //SortContactButtons.sortContactButtonsByLastName(contactList);
        //contactList =


        buttonPanel.removeAll(); // remove all existing buttons from the panel

        for (ContactButton button : contactList) {
            button.setAlignmentX(Component.LEFT_ALIGNMENT); // set the alignment of the button to center
            button.setHorizontalAlignment(SwingConstants.LEFT); // left align the text on the button
            button.setMaximumSize(new Dimension(maxPanelWidth, button.getPreferredSize().height)); // set the maximum size of the button to the maximum width of the panel and the preferred height of the button
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.add(button);
            // row.add(label);
            // row.add(txtFld);
            buttonPanel.add(row);
            scrollPane.revalidate();
        }
        // tell the panel to update its layout
        setVisible(true);
    }



    ActionListener saveButtonListener(){
        JTextField fname = firstNameField;
        JTextField lname = lastNameField;


        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String firstname = fname.getText();
                String lastname = lname.getText();
                if(firstname == null || firstname.equals("") || lastname == null || lastname.equals("")){
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please enter a first and last name");
                    return;
                }

                int indexInArrayList = contactList.indexOf(current);
                if(indexInArrayList == -1){
                    System.out.println(firstname + " " + lastname + " not found");
                }; //not found (shouldn't happen

                //clear fields
                fname.setText("");
                lname.setText("");

                int updated = performUpdate("update students SET firstname='" + firstname + "', lastname ='" + lastname + "' where id=" + Integer.parseInt(idField.getText()));

                ContactButton tmp = new ContactButton(firstname, lastname);
                ActionListener b = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Button clicked line 476");
                        ContactButton tmp = (ContactButton) e.getSource();
                        firstNameField.setText(tmp.fname);
                        lastNameField.setText(tmp.lname);

                        saveChanges.setEnabled(true);
                        deleteContact.setEnabled(true);
                        submit.setEnabled(false);
                        clear.setEnabled(false);

                        current = tmp;
                        modelCourses = new DefaultTableModel();
                        modelCourses.setColumnIdentifiers(new String[]{"SectionId", "CourseName"});
                        System.out.println("id: " + idField.getText());
                        System.out.println("SELECT section FROM sections where students.ID=" + Integer.parseInt((idField.getText())));
                        ResultSet a = performQuery("SELECT section FROM sections where students.ID=" + Integer.parseInt((idField.getText())));
                        try {
                            a.next();
                            String[] sections = a.getString("1").split(":");
                            for (int i = 0; i < sections.length; i++) {
                                String query = "SELECT sections.ID as SectionID, courses.CourseName as CourseName FROM sections, courses where sections.ID=" + sections[i] + " and courses.ID=sections.course_id";
                                ResultSet courses = performQuery(query);
                                courses.next();
                                modelCourses.addRow(new Object[]{courses.getString("SectionID"), courses.getString("CourseName")});
                            }
                        } catch (SQLException ex) {
                            System.out.println(ex.getMessage());
                        }

                        tableCourses.setBackground(Color.red);
                        tableCourses.setAutoCreateRowSorter(true);
                        tableCourses.setModel(modelCourses);
                        tableCourses.setBounds(180-60+60-30-130 ,200+20+70+70+40, 360, tableCourses.getRowCount()*17);
//
//                        model1.setColumnIdentifiers(new String[]{"SectionID", "CourseName"});
//
//                        model1.setRowCount(0);
//                        model1 = modelCourses;
//                        tableCourses.setAutoCreateRowSorter(true);
//                        tableCourses.setModel(model1);
                    }
                };
                 tmp.addActionListener(b);
                contactList.set(indexInArrayList, tmp);
                reloadButtons();

                //turn off
                saveChanges.setEnabled(false);
                deleteContact.setEnabled(false);
                submit.setEnabled(true);
                clear.setEnabled(true);
                idField.setText(Integer.toString(getNextIncrement()));


                for (Component c : rightPanel.getComponents()){
                    if(c.getName() == "tableCourses"){
                        rightPanel.remove(c);
                        rightPanel.repaint();
                        rightPanel.revalidate();
                    }
                }

                    current = null;
            }
        };
    }

    ActionListener submitButtonListener(){
        JTextField fname = firstNameField;
        JTextField lname = lastNameField;

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String firstname = fname.getText();
                String lastname = lname.getText();
                if(firstname == null || firstname.equals("") || lastname == null || lastname.equals("")){
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please enter a first and last name");
                    return;
                }


                try {
                    addButton();
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                idField.setText(Integer.toString(getNextIncrement()));
                //clear fields
                fname.setText("");
                lname.setText("");

            }
        };
    }

    ActionListener deleteButtonListener(){
        JTextField fname = firstNameField;
        JTextField lname = lastNameField;

    
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int indexInArrayList = contactList.indexOf(current);
                if(indexInArrayList == -1){
                    System.out.println("not found");
                }; //not found (shouldn't happen
    
                //clear fields
                fname.setText("");
                lname.setText("");
    
                contactList.remove(indexInArrayList);
                buttonPanel.remove(current);
                reloadButtons();

                //akshi - delete from database students
                //search for teacher in sections table and set teacher = -1
                int rs = performUpdate(String.format("delete from students where id = %s", idField.getText() ));
//                rs = performUpdate(String.format("delete from sections where student_id=%s", idField.getText() ));

                //turn off
                saveChanges.setEnabled(false);
                deleteContact.setEnabled(false);
                submit.setEnabled(true);
                clear.setEnabled(true);
                idField.setText("");
                fname.setText("");
                lname.setText("");

                current = null;
                buttonPanel.revalidate();
                buttonPanel.repaint();
                
            }
            };
       }

       static ArrayList<ContactButton> geList(){
           return contactList;
       }

       static void end(){
        // FileWriter writer;
        // try {
        //     writer = new FileWriter("contacts.txt");
        //     for(ContactButton str: geList()) {
        //         writer.write(str.toString() + System.lineSeparator());
        //     }
        //     writer.close();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // } 
            
        // System.out.println("end");
        
        System.exit(1);
       }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("GUI");

            f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

                WindowListener listener = new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent we) {
                        
                            end();
                            f.setVisible(false);
                            f.dispose();
                        
                    }
                };
                f.addWindowListener(listener);
            
            StudentView project2 = new StudentView();
            f.add(project2);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
//In this version of the AddNewProject2 class, I changed the layout manager of the centerPanel to a BorderLayout, and then added the scrollPane to the BorderLayout.WEST position of the centerPanel. This will cause the scrollPane and its contents to be locked to the left side of the centerPanel.




