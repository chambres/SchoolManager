import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.sql.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class SectionView extends JPanel {
    Connection con;


    
    


    JTextField CourseNameField = new JTextField();
    JTextField TypeField = new JTextField();
    JTextField idField = new JTextField();

    JButton submit;
    JButton clear;
    JButton saveChanges;
    JButton deleteContact;

    ActionListener b = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Button clicked");
            ContactButton tmp = (ContactButton) e.getSource();
            CourseNameField.setText(tmp.fname);
            TypeField.setText(tmp.lname);
            
            saveChanges.setEnabled(true);
            deleteContact.setEnabled(true);
            submit.setEnabled(false);
            clear.setEnabled(false);

            current = tmp;
        }
    };

    static ArrayList<ContactButton> contactList = new ArrayList<ContactButton>();

    ContactButton current = null;

    JScrollPane scrollPane;


    int maxPanelWidth = 450; // set the maximum width of the panel

    private JPanel buttonPanel = new JPanel(new GridLayout(0, 1)); // 1 column grid
    public SectionView() {

        

        try{
        Class.forName("com.mysql.jdbc.Driver");
        con= DriverManager.getConnection("jdbc:mysql://localhost:3306/p2","root","password");       
        }
        catch(Exception e){ System.out.println(e);}

        int rs = performUpdate("create table courses (ID int auto_increment primary key,\nCourseName varchar(500) NOT null,\nType varchar(500) NOT null\n);");
        

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
        ResultSet a = performQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM courses;");
        int maxID = 0;
        try{
        if(a.next()) {
            maxID = a.getInt(1);
        }
        }
        catch(Exception e){ System.out.println(e);}
        return maxID;
    }


    ArrayList<String> choices = new ArrayList<>();
    ArrayList<String> teachers = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();

    JComboBox<String> teacherSelection;
    JComboBox<String> courseSelection;
    JComboBox<String> studentSelection;


    JPanel rightPanel = new JPanel(null);

    int offset = 30;
    int labelHeight = 20; // set the height of the labels

    void Start(){
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonPanel, BorderLayout.PAGE_START);
        scrollPane = new JScrollPane(panel);
        scrollPane.getViewport().setPreferredSize(new Dimension(450, 500));

        
        int gap = 2;
        setBorder(BorderFactory.createEmptyBorder(gap, gap, gap, gap));
        setLayout(new BorderLayout(gap, gap));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setPreferredSize(new Dimension(850, 500));
        centerPanel.setMaximumSize(new Dimension(450, 500));
        centerPanel.add(scrollPane, BorderLayout.WEST);

         
        rightPanel.setPreferredSize(new Dimension(400, 500));
        rightPanel.setMaximumSize(new Dimension(400, 500));
        rightPanel.setBackground(Color.white);

        JLabel CourseNameLabel = new JLabel("Course Name:");
        JLabel TypeLabel = new JLabel("Teacher:");
        JLabel idLabel = new JLabel("ID:");



        int labelX = 40; // set the X position of the labels
        int labelY = 30; // set the Y position of the first label

        int labelGap = 10; // set the vertical gap between the labels

        JLabel[] labels = {CourseNameLabel, TypeLabel, idLabel}; // store the labels in an array
        for(JLabel label : labels) { // loop through the labels
            label.setBounds(labelX, labelY, 100, labelHeight); // hardcode the position and size of the label
            labelY += labelHeight + labelGap; // update the Y position for the next label
            rightPanel.add(label);
        }

        labelY = 30;
        idField.setEditable(false);
        idField.setText(Integer.toString(getNextIncrement()));


        ResultSet courses = performQuery("Select * from courses");
        try{
        while(courses.next()) {
            choices.add(courses.getString("CourseName"));
        }
        }
        catch(Exception e){ System.out.println(e);}

        


        courseSelection = new JComboBox<String>(teachers.toArray(new String[teachers.size()]));
        courseSelection.setModel(new DefaultComboBoxModel<String>(choices.toArray(new String[choices.size()])));
        courseSelection.setBounds(labelX+100, labelY, 220, labelHeight);
        rightPanel.add(courseSelection);

        labelY += labelHeight + labelGap;

        ResultSet teacher = performQuery("Select * from teachers");
        try{
        while(teacher.next()) {
            teachers.add(courses.getString("FirstName") + " " + courses.getString("LastName"));
        }
        }
        catch(Exception e){ System.out.println(e);}

        teacherSelection = new JComboBox<String>(teachers.toArray(new String[teachers.size()]));
        teacherSelection.setModel(new DefaultComboBoxModel<String>(teachers.toArray(new String[teachers.size()])));
        teacherSelection.setBounds(labelX+100, labelY, 220, labelHeight);
        rightPanel.add(teacherSelection);

        JTextField[] fields = { TypeField, idField}; // store the text fields in an array
        for(JTextField field : fields) { // loop through the text fields
            field.setBounds(labelX+100, labelY, 220, labelHeight); // hardcode the position and size of the text field
            labelY += labelHeight + labelGap; // update the Y position for the next text field
            rightPanel.add(field);
        }

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
        test.setBounds(180-60+60-offset, 200+20+70+70, 100, 20);
        test.addActionListener(testListener());
        rightPanel.add(test);


        ResultSet student = performQuery("Select * from students");
        students.clear();
        try{
        while(student.next()) {
            students.add(student.getString("FirstName") + " " + student.getString("LastName"));
        }
        }
        catch(Exception e){ System.out.println(e);}

        studentSelection = new JComboBox<String>(students.toArray(new String[students.size()]));
        studentSelection.setModel(new DefaultComboBoxModel<String>(students.toArray(new String[students.size()])));
        studentSelection.setBounds(180-60+60-offset-60, 200+20+70+70+80, 120, labelHeight);
        studentSelection.setName("studentSelection");
        rightPanel.add(studentSelection);

        JButton test2 = new JButton("Add Student");
        test2.setBounds(180-60+60-offset+60, 200+20+70+70+80, 120, labelHeight);
        test2.addActionListener(addAStudentListener());
        rightPanel.add(test2);
        
//==================================================================================================



    // create the table model with one column named "Students"
    DefaultTableModel model = new DefaultTableModel();
    model.addColumn("Students");

    // add the student names to the table model
    for (String s : selectedStudents) {
        model.addRow(new Object[] {s});
    }

    // create the table with the table model
    table = new JTable(model);
    table.setBounds(180-60+60-offset-offset*3, 200+20+70+70+80+30, 320, 100);

    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Make sure selection is not still changing
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) { // Make sure a row is actually selected
                    // Do something with the selected row, e.g. get the values from the table model
                    String firstName = table.getValueAt(selectedRow, 0).toString();
                    int index = selectedStudents.indexOf(firstName);
                    System.out.println("Selected row: " + firstName + " ");
                    selectedStudents.remove(index);  
                }
            }
        }
    });

    System.out.println(table.getModel());
    
    rightPanel.add(table);


    

//==================================================================================================


        centerPanel.add(rightPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
    }

    ArrayList<String> selectedStudents = new ArrayList<String>();

    JTable table;

    void resetStudentDropdown(){
        ResultSet student = performQuery("Select * from students");
        students.clear();
        try{
        while(student.next()) {
            students.add(student.getString("FirstName") + " " + student.getString("LastName"));
        }
        }
        catch(Exception e){ System.out.println(e);}
        Component[] componentList = rightPanel.getComponents();
        for(int i = 0; i < componentList.length; i++){
            //get name 
            String name = componentList[i].getName();
            if(name == "studentSelection"){
                rightPanel.remove(componentList[i]);
            }
        }

        studentSelection = new JComboBox<String>(students.toArray(new String[students.size()]));
        studentSelection.setModel(new DefaultComboBoxModel<String>(students.toArray(new String[students.size()])));
        studentSelection.setBounds(180-60+60-offset-60, 200+20+70+70+80, 120, labelHeight);
        studentSelection.setName("studentSelection");
        rightPanel.add(studentSelection);
    }

    ActionListener addAStudentListener(){
        return new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("Add Student");
                String student = studentSelection.getSelectedItem().toString();
                selectedStudents.add(student);

                // create the table model with one column named "Students"
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Students");

                // add the student names to the table model
                for (String s : selectedStudents) {
                    model.addRow(new Object[] {s});
                }
                DefaultTableModel model1 = (DefaultTableModel) table.getModel();
                model1.setRowCount(0);
                model1 = model;
                table.setModel(model1);
            }
        };
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
                CourseNameField.setText("");
                TypeField.setText("");
            }
        };
        return a;
    }


    ResultSet performQuery(String query){
        
        try{ Class.forName("com.mysql.jdbc.Driver"); return con.createStatement().executeQuery(query);}
        
        catch(Exception e){System.out.println(e);}

        return null;
    }

    int performUpdate(String query){
        try{ Class.forName("com.mysql.jdbc.Driver"); return con.createStatement().executeUpdate(query);}
        
        catch(Exception e){System.out.println(e);}

        return -1;
    }



    void addButton(){
        String fname = CourseNameField.getText();
        String lname = TypeField.getText();


        try{
            
            performUpdate(String.format("insert into courses(CourseName, Type)\nvalues ('%s', '%s');", fname, lname));
            
            
            ResultSet b = performQuery("select * from courses");
            while(b.next()){
                System.out.println(b.getString("ID") + " " + b.getString("CourseName") + " " + b.getString("Type"));
            }
        }
        catch(Exception e){ System.out.println(e);}

        ContactButton button = new ContactButton(fname, lname);

        ActionListener b = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Button clicked");
                ContactButton tmp = (ContactButton) e.getSource();
                CourseNameField.setText(tmp.fname);
                TypeField.setText(tmp.lname);
                
                saveChanges.setEnabled(true);
                deleteContact.setEnabled(true);
                submit.setEnabled(false);
                clear.setEnabled(false);

                ResultSet b = performQuery("SELECT id FROM courses WHERE CourseName = + '" + fname + "' AND Type = '" + lname + "';");
                try{
                    while(b.next()){
                        idField.setText(b.getString("ID"));
                    }
                }
                catch(Exception e1){System.out.println(e1);}

                current = tmp;
            }
        };
        button.addActionListener(b);
        contactList.add(button);
        reloadButtons();
    }

    void reloadButtons() {

        //SortContactButtons.sortContactButtonsByType(contactList);
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
        JTextField fname = CourseNameField;
        JTextField lname = TypeField;

    
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
    
                
                
    
    
                String CourseName = fname.getText();
                String Type = lname.getText();
                if(CourseName == null || CourseName.equals("") || Type == null || Type.equals("")){
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please enter a first and last name");
                    return;
                }
    
                int indexInArrayList = contactList.indexOf(current);
                if(indexInArrayList == -1){
                    System.out.println(CourseName + " " + Type + " not found");
                }; //not found (shouldn't happen
    
                //clear fields
                fname.setText("");
                lname.setText("");
                idField.setText(Integer.toString(getNextIncrement()));
    
                ContactButton tmp = new ContactButton(CourseName, Type);
                ActionListener b = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Button clicked");
                        ContactButton tmp = (ContactButton) e.getSource();
                        CourseNameField.setText(tmp.fname);
                        TypeField.setText(tmp.lname);
                        
                        saveChanges.setEnabled(true);
                        deleteContact.setEnabled(true);
                        submit.setEnabled(false);
                        clear.setEnabled(false);
        
                        current = tmp;
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
    
                current = null;
            }
            };
       }
    
       ActionListener submitButtonListener(){
        JTextField fname = CourseNameField;
        JTextField lname = TypeField;

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String CourseName = fname.getText();
                String Type = lname.getText();
                if(CourseName == null || CourseName.equals("") || Type == null || Type.equals("")){
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please enter a first and last name");
                    return;
                }

    
                addButton();
                idField.setText(Integer.toString(getNextIncrement()));
                //clear fields
                fname.setText("");
                lname.setText("");
    
            }
            };
       }
    
       ActionListener deleteButtonListener(){
        JTextField fname = CourseNameField;
        JTextField lname = TypeField;

    
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
    
                //turn off
                saveChanges.setEnabled(false);
                deleteContact.setEnabled(false);
                submit.setEnabled(true);
                clear.setEnabled(true);
    
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
            
            SectionView project2 = new SectionView();
            f.add(project2);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

    void updateTable(){
        ResultSet courses = performQuery("Select * from courses");

        choices.clear();
        try{
        while(courses.next()) {
            choices.add(courses.getString("CourseName"));
        }
        }
        catch(Exception e){ System.out.println(e);}

        ResultSet teacher = performQuery("Select * from teachers");
        teachers.clear();
        try{
        while(teacher.next()) {
            teachers.add(teacher.getString("FirstName") + " " + teacher.getString("LastName"));
        }
        }
        catch(Exception e){ System.out.println(e);}

        courseSelection.removeAllItems();

        // Add the updated items from the ArrayList to the combo box's model
        for (String option : choices) {
            courseSelection.addItem(option);
        }

        teacherSelection.removeAllItems();
        System.out.println(teacherSelection.getItemCount());

        // Add the updated items from the ArrayList to the combo box's model
        for (String option : teachers) {
            teacherSelection.addItem(option);
        }
        
        
    }
}
//In this version of the AddNewProject2 class, I changed the layout manager of the centerPanel to a BorderLayout, and then added the scrollPane to the BorderLayout.WEST position of the centerPanel. This will cause the scrollPane and its contents to be locked to the left side of the centerPanel.




