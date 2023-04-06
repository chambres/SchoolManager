import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.sql.*;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class SectionView extends JPanel {
    Connection con;

    JTextField CourseNameField = new JTextField();
    JTextField TeacherField = new JTextField();
    JTextField idField = new JTextField();

    JButton submit;
    JButton clear;
    JButton saveChanges;
    JButton deleteContact;
    int selectedStudent;

   


    static ArrayList<ContactButton> contactList = new ArrayList<ContactButton>();

    ContactButton current = null;

    JScrollPane scrollPane;


    int maxPanelWidth = 450; // set the maximum width of the panel

    private JPanel buttonPanel = new JPanel(new GridLayout(0, 1)); // 1 column grid

    public SectionView() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/p2?characterEncoding=utf8","root","password");
        } catch (Exception e) {
            System.out.println(e);
        }

        int rs = performUpdate(
                "create table sections (ID int auto_increment primary key," + "\n" +
                        "course_id int NOT null," + "\n" +
                        "teacher_id int NOT null" + "\n" +
                        ");"
        );


        System.out.println(rs);
        
        File f = new File("contacts.txt");

        Start();

        if (f.exists() && !f.isDirectory()) {
            try {
                List<String> lines = Files.readAllLines(Paths.get("contacts.txt"), java.nio.charset.StandardCharsets.UTF_8);
                for (String line : lines) {
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

    int getNextIncrement() {
        ResultSet a = performQuery("SELECT COALESCE(MAX(id), 0) + 1 FROM sections;");
        int maxID = 0;
        try {
            if (a.next()) {
                maxID = a.getInt(1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return maxID;
    }


    ArrayList<String> choices = new ArrayList<>();
    ArrayList<String> teachers = new ArrayList<>();
    ArrayList<String> students = new ArrayList<>();

    JComboBox<Item> teacherSelection;
    JComboBox<Item> courseSelection;
    JComboBox<Item> studentSelection;
    JPanel rightPanel = new JPanel(null);

    int offset = 30;
    int labelHeight = 20; // set the height of the labels

    void Start() {
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
        for (JLabel label : labels) { // loop through the labels
            label.setBounds(labelX, labelY, 100, labelHeight); // hardcode the position and size of the label
            labelY += labelHeight + labelGap; // update the Y position for the next label
            rightPanel.add(label);
        }

        labelY = 30;
        idField.setEditable(false);
        idField.setText(Integer.toString(getNextIncrement()));


        ResultSet courses = performQuery("Select * from courses");
        courseSelection = new JComboBox<Item>();
        courseSelection.setBounds(labelX + 100, labelY, 220, labelHeight);
        try {
            while (courses.next()) {
                Item courseItem = new Item(Integer.parseInt(courses.getString("ID")), courses.getString("CourseName") + " " + courses.getString("Type"));
                courseSelection.addItem(courseItem);

                choices.add(courses.getString("CourseName") + " " + courses.getString("Type"));

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        rightPanel.add(courseSelection);
        CourseNameField.setBounds(labelX + 100, labelY, 220, labelHeight);
        rightPanel.add(CourseNameField);


        labelY += labelHeight + labelGap;

        ResultSet teacher = performQuery("Select * from teachers");
        teacherSelection = new JComboBox<Item>();
        teacherSelection.setBounds(labelX + 100, labelY, 220, labelHeight);
        try {
            while (teacher.next()) {
                teachers.add(teacher.getString("FirstName") + " " + teacher.getString("LastName"));
                teacherSelection.addItem(new Item(Integer.parseInt(teacher.getString("ID")), teacher.getString("FirstName") + " " + teacher.getString("LastName")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
//        teacherSelection.setModel(new DefaultComboBoxModel<Item>(teachers));
        rightPanel.add(teacherSelection);

        JTextField[] fields = {TeacherField, idField}; // store the text fields in an array
        for (JTextField field : fields) { // loop through the text fields
            field.setBounds(labelX + 100, labelY, 220, labelHeight); // hardcode the position and size of the text field
            labelY += labelHeight + labelGap; // update the Y position for the next text field
            rightPanel.add(field);
        }

        int yOff = 20;
        saveChanges = new JButton("Save");
        saveChanges.setBounds(180 - 60 + 60 - offset, 200 - 50 + yOff, 100, 20);
        rightPanel.add(saveChanges);
        saveChanges.addActionListener(saveButtonListener());
        //turn off
        saveChanges.setEnabled(false);

        deleteContact = new JButton("Delete");
        deleteContact.setBounds(180 - 60 + 60 - offset, 200 - 20 + yOff, 100, 20);
        rightPanel.add(deleteContact);
        deleteContact.addActionListener(deleteButtonListener());
        //turn off
        deleteContact.setEnabled(false);

        submit = new JButton("Submit");
        submit.setBounds(180 - 60 + 60 - offset, 200 + 20 + yOff, 100, 20);
        rightPanel.add(submit);
        submit.addActionListener(submitButtonListener());

        clear = new JButton("Clear");
        clear.setBounds(180 - 60 + 60 - offset, 200 + 20 + 70 + yOff, 100, 20);
        clear.addActionListener(clearListener());
        rightPanel.add(clear);

        JButton test = new JButton("Test");
        test.setBounds(180 - 60 + 60 - offset, 200 + 20 + 70 + 70, 100, 20);
        test.addActionListener(testListener());
        rightPanel.add(test);


        ResultSet student = performQuery("Select * from students");
        students.clear();
        studentSelection = new JComboBox<Item>();
        studentSelection.setBounds(180 - 60 + 60 - offset - 60, 200 + 20 + 70 + 70 + 80, 120, labelHeight);
        studentSelection.setName("studentSelectionComboBox");
        try {
            while (student.next()) {
                students.add(student.getString("FirstName") + " " + student.getString("LastName"));
                studentSelection.addItem(new Item(Integer.parseInt(student.getString("ID")), student.getString("FirstName") + " " + student.getString("LastName")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }
//        studentSelection.setModel(new DefaultComboBoxModel<String>(students.toArray(new String[students.size()])));
        rightPanel.add(studentSelection);

        JButton test2 = new JButton("Add Student");
        test2.setBounds(180 - 60 + 60 - offset + 60, 200 + 20 + 70 + 70 + 80, 100, labelHeight);
        test2.addActionListener(addAStudentListener());
        rightPanel.add(test2);

        JButton test3 = new JButton("Remove Student");
        test3.setBounds(180 - 60 + 60 - offset + 150, 200 + 20 + 70 + 70 + 80, 100, labelHeight);
        test3.addActionListener(removeAStudentListener());
        rightPanel.add(test3);

//==================================================================================================


        // create the table model with one column named "Students"
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Students");

        // add the student names to the table model
        for (String s : selectedStudents) {
            model.addRow(new Object[]{s});
        }

        // create the table with the table model
        table = new JTable(model);
        table.setName("studentSelectionTable");
        table.setBounds(180 - 60 + 60 - offset - offset * 3, 200 + 20 + 70 + 70 + 80 + 30, 320, 100);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) { // Make sure selection is not still changing
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) { // Make sure a row is actually selected
                        // Do something with the selected row, e.g. get the values from the table model
                        String firstName = table.getValueAt(selectedRow, 0).toString();
                        selectedStudent = selectedStudents.indexOf(firstName);
                        System.out.println("Selected row: " + firstName + " ");
//                    selectedStudent = firstName.split(" ")[2];
//                    selectedStudents.remove(index);
                    }
                }
            }
        });
        rightPanel.add(table);
//==================================================================================================


        centerPanel.add(rightPanel, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);
    }


    ArrayList<String> selectedStudents = new ArrayList<String>();
    ArrayList<String> studentsOfASection = new ArrayList<String>();
    JTable table;

    void resetStudentDropdown() {
        //iterate through all components of right panel
        for (Component c : rightPanel.getComponents()){
            if(c.getName() == "studentSelectionComboBox"){
                rightPanel.remove(c);
                rightPanel.repaint();
                rightPanel.revalidate();
            }
        }

        ResultSet student = performQuery("Select * from students");
        students.clear();
        studentSelection = new JComboBox<Item>();
        studentSelection.setBounds(180 - 60 + 60 - offset - 60, 200 + 20 + 70 + 70 + 80, 120, labelHeight);
        studentSelection.setName("studentSelectionComboBox");
        try {
            while (student.next()) {
                students.add(student.getString("FirstName") + " " + student.getString("LastName"));
                Item itemStudent = new Item(Integer.parseInt(student.getString("ID")), student.getString("LastName") + " " + student.getString("FirstName"));
                studentSelection.addItem(itemStudent);
            }
        } catch (Exception e) {
            System.out.println(e);
        }



        rightPanel.add(studentSelection);
    }

    ActionListener addAStudentListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Add Student");
                String student = studentSelection.getSelectedItem().toString() + " " + ((Item) studentSelection.getSelectedItem()).getId();
                selectedStudents.add(student);

                // create the table model with one column named "Students"
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Students");

                OrderByFourthLetter.orderByFourthLetter(selectedStudents);
                // add the student names to the table model
                for (String s : selectedStudents) {
                    model.addRow(new Object[]{s});
                }
                DefaultTableModel model1 = (DefaultTableModel) table.getModel();
                model1.setColumnIdentifiers(new String[]{"StudentNames"});


                model1.setRowCount(0);
                model1 = model;
                table.setAutoCreateRowSorter(true);
                table.setModel(model1);
                table.setName("studentSelectionTable");
            }
        };
    }

    ActionListener removeAStudentListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Remove Student" + table.getSelectedRow());
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int[] rows = table.getSelectedRows();
                for (int i = 0; i < rows.length; i++) {
                    System.out.println(model.getValueAt(i, 0));
                    model.removeRow(rows[i] - i);
                    selectedStudents.remove(selectedStudent);
                }

            }
        };
    }

    ActionListener testListener() {

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

    ActionListener clearListener() {
        ActionListener a = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CourseNameField.setText("");
                TeacherField.setText("");
            }
        };
        return a;
    }


    ResultSet performQuery(String query) {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            return con.createStatement().executeQuery(query);
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    int performUpdate(String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return con.createStatement().executeUpdate(query);
        } catch (Exception e) {
            System.out.println(e);
        }

        return -1;
    }


    void addButton() {
        Item courseItem = ((Item) courseSelection.getSelectedItem());
        int course = courseItem.getId(); // CourseNameField.getText();
        Item teacherItem = ((Item) teacherSelection.getSelectedItem());
        int teacher = teacherItem.getId(); //teacherSelection TeacherField.getText();

        try {
            performUpdate(String.format("insert into sections(course_id, teacher_id, ID)\nvalues (%d, %d, %d);", course, teacher, Integer.parseInt(idField.getText())));
            //browse through all students and modify student section in student table.
            //If section is not there for that student add. If already there don't add
            Iterator it = selectedStudents.iterator();

            // Holds true till there is single element
            // remaining in the list
            for (String stud : selectedStudents) {
                String currentStudent = stud.split(" ")[2];
                ResultSet b = performQuery("select * from students where id=" + currentStudent);
                b.next();
                System.out.println("Section" + b.getString("Section"));
                System.out.println("IDField" + idField.getText());
                String section = "";
                if (b.getString(4) == null || b.getString(4) == "")
                    section = idField.getText() + ":";
                else
                    section = b.getString("Section") + idField.getText() + ":";
                int updated = performUpdate("update students SET section='" + section + "' where id=" + currentStudent);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        ContactButton button = new ContactButton(courseItem.getDescription(), teacherItem.getDescription(), idField.getText().toString());

        
        button.addActionListener(b);
        contactList.add(button);
        reloadButtons();
    }

    void reloadButtons() {
        buttonPanel.removeAll(); // remove all existing buttons from the panel

        for (ContactButton button : contactList) {
            button.setAlignmentX(Component.LEFT_ALIGNMENT); // set the alignment of the button to center
            button.setHorizontalAlignment(SwingConstants.LEFT); // left align the text on the button
            button.setMaximumSize(new Dimension(maxPanelWidth, button.getPreferredSize().height)); // set the maximum size of the button to the maximum width of the panel and the preferred height of the button
            button.addActionListener(b);
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.add(button);
            buttonPanel.add(row);
            scrollPane.revalidate();
        }
        // tell the panel to update its layout
        setVisible(true);
    }

    ActionListener b = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Button selection clicked");
            ContactButton tmp = (ContactButton) e.getSource();
            current = tmp;
            courseSelection.setSelectedItem(tmp.fname);
            idField.setText(tmp.id);
            teacherSelection.setSelectedItem(tmp.lname);

            idField.setText(String.valueOf(tmp.id));
            saveChanges.setEnabled(true);
            deleteContact.setEnabled(true);
            submit.setEnabled(false);
            clear.setEnabled(false);
            //Find the list of students for that section and add it to model
            ResultSet b = performQuery("SELECT * FROM students");
            String section;
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("Students");
            selectedStudents.clear();
            try{
                while(b.next()){
                    section = b.getString("section");
                    if((section!=null)&&(section!="")) {
                        if (Arrays.asList(section.split(":")).contains(idField.getText())) {
                            selectedStudents.add(b.getString("LastName") + " " + b.getString("FirstName") + " " + b.getString("ID"));
                            model.addRow(new Object[]{b.getString("LastName") + " " + b.getString("FirstName") + " " + b.getString("ID")});
                        }
                    }

                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            table.setModel(model);
            table.setAutoCreateRowSorter(true);
            
        }
    };


    ActionListener saveButtonListener() {
        JTextField fname = CourseNameField;
        JTextField lname = TeacherField;


        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Item courseItem = (Item) courseSelection.getSelectedItem();
                String CourseName = courseItem.getDescription();
                Item teacherItem = (Item) teacherSelection.getSelectedItem();
                String Type = teacherItem.getDescription();

                if (CourseName == null || CourseName.equals("") || Type == null || Type.equals("")) {
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please choose course and teacher");
                    return;
                }
                System.out.println(contactList.toString());
                int indexInArrayList = contactList.indexOf(current);
                if (indexInArrayList == -1) {
                    System.out.println(CourseName + " " + Type + " not found");
                }
                ; //not found (shouldn't happen


                performUpdate(String.format("update sections SET course_id=%s, teacher_id=%s where id=%s", courseItem.getId(), teacherItem.getId(), idField.getText()));

                ContactButton tmp = new ContactButton(CourseName, Type, idField.getText().trim().toString());
                
                tmp.addActionListener(b);
                contactList.remove(current);
                contactList.add(tmp);
                reloadButtons();

                
                try {
                    //browse through all students and modify student section in student table.
                    //If section is not there for that student add. If already there don't add
        
                    // Holds true till there is single element
                    // remaining in the list
                    for (String stud : selectedStudents) {
                        String currentStudent = stud.split(" ")[2];
                        ResultSet b = performQuery("select * from students where id=" + currentStudent);
                        b.next();
                        System.out.println("Section" + b.getString("Section"));
                        System.out.println("IDField" + idField.getText());
                        String section = "";

                        if((b.getString("Section") + ":").contains(idField.getText() + ":")){
                            continue;
                        }
                        if (b.getString(4) == null || b.getString(4) == "")
                            section = idField.getText() + ":";
                        else
                            section = b.getString("Section") + idField.getText() + ":";
                        int updated = performUpdate("update students SET section='" + section + "' where id=" + currentStudent);
                    }
                } catch (Exception eee) {
                    System.out.println(eee);
                }
        
                
                //turn off
                saveChanges.setEnabled(false);
                deleteContact.setEnabled(false);
                submit.setEnabled(true);
                clear.setEnabled(true);
    
                current = null;

                selectedStudents.clear();
                DefaultTableModel modell = new DefaultTableModel();
                modell.addColumn("Students");
                // add the student names to the table model
                for (String s : selectedStudents) {
                    modell.addRow(new Object[]{s});
                }
                table.setModel(modell);
            }
        };
    }

    ActionListener submitButtonListener() {
        JTextField fname = CourseNameField;
        JTextField lname = TeacherField;

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Item courseItem = ((Item) courseSelection.getSelectedItem());
                String CourseName = String.valueOf(courseItem.getId()); // CourseNameField.getText();
                Item teacherItem = ((Item) teacherSelection.getSelectedItem());
                String Teacher = String.valueOf(teacherItem.getId()); //teacherSelection TeacherField.getText();

                if (CourseName == null || CourseName.equals("") || Teacher == null || Teacher.equals("")  || selectedStudents.size() == 0) {
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please choose teacher and course, and make sure students are added.");
                    return;
                }

                addButton();
                idField.setText(Integer.toString(getNextIncrement()));

                selectedStudents.clear();
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Students");
                // add the student names to the table model
                for (String s : selectedStudents) {
                    model.addRow(new Object[]{s});
                }
                table.setModel(model);
    
            }
        };


    }

    ActionListener deleteButtonListener() {
        JTextField fname = CourseNameField;
        JTextField lname = TeacherField;

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int indexInArrayList = contactList.indexOf(current);
                if (indexInArrayList == -1) {
                    System.out.println("not found");
                }
                ; //not found (shouldn't happen

                //clear fields
                fname.setText("");
                lname.setText("");

                contactList.remove(indexInArrayList);
                buttonPanel.remove(current);
                reloadButtons();

                // update the students in those section to no longer have deleted section
                ResultSet students = performQuery("select * from students");
                try {
                    String section;
                    while (students.next()) {
                        //Find the list of sections for student
                        section = students.getString("Section");
                        if ((section != null) && (section != "")) {
                            String[] sectionsStudent = section.split(":");
                            String sectionList = "";
                            for (String st : sectionsStudent) {
                                if (!st.equals(idField.getText())) {
                                    if (sectionList.equals(""))
                                        sectionList = st;
                                    else
                                        sectionList = sectionList + ":" + st;
                                }
                            }
                            int updated = performUpdate("update students SET section='" + sectionList + "' where id=" + students.getString("ID"));

                        }
                    }
                } catch (SQLException ex) {
                    System.out.println(ex);
                }
                System.out.println("delete from sections where id=" + idField.getText());
                int updated = performUpdate(String.format("delete from sections where id=%s", idField.getText() ));
                //akshi
//                for(ContactButton button:contactList)
//                    button.getText().split(,)
                DefaultTableModel model1 = (DefaultTableModel) table.getModel();
                model1.setRowCount(0);

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

    static ArrayList<ContactButton> geList() {
        return contactList;
    }

    static void end() {
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

    void updateTable() {
        ResultSet courses = performQuery("Select * from courses");

        choices.clear();
        courseSelection.removeAllItems();
        try {
            while (courses.next()) {
                choices.add(courses.getString("CourseName"));
                courseSelection.addItem(new Item(Integer.parseInt(courses.getString("ID")), courses.getString("CourseName") + " " + courses.getString("Type")));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        ResultSet teacher = performQuery("Select * from teachers");
        teachers.clear();
        teacherSelection.removeAllItems();
        try {
            while (teacher.next()) {
                teachers.add(teacher.getString("FirstName") + " " + teacher.getString("LastName"));
                teacherSelection.addItem(new Item(Integer.parseInt(teacher.getString("ID")), teacher.getString("FirstName") + " " + teacher.getString("LastName")));

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        ResultSet student = performQuery("Select * from students");
        students.clear();
        studentSelection.removeAllItems();
        try {
            while (student.next()) {
                Item itemStudent = new Item(Integer.parseInt(student.getString("ID")), student.getString("LastName") + " " + student.getString("FirstName"));
                studentSelection.addItem(itemStudent);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        contactList.clear();
        ResultSet sections = performQuery("Select * from sections");
        try {

            while (sections.next()) {
                ResultSet course = performQuery(String.format("Select * from courses where ID=%s", String.valueOf(sections.getInt("course_id"))));
                String courseName;
                if (!course.next()) {
                    System.out.println("no data");
                    courseName = "";
                } else {
                    courseName = course.getString("CourseName") + " " + course.getString("Type");
                }

                String teacherName;
                teacher = performQuery(String.format("Select * from teachers where ID=%s", String.valueOf(sections.getInt("teacher_id"))));
                if (!teacher.next()) {
                    System.out.println("no data");
                    teacherName = "";
                } else {
                    teacherName = teacher.getString("FirstName") + " " + teacher.getString("LastName");
                }

                idField.setText(sections.getString("ID"));
                ContactButton button = new ContactButton(courseName, teacherName, String.valueOf(sections.getInt("id")));
                button.addActionListener(b);
                contactList.add(button);
                reloadButtons();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}

//In this version of the AddNewProject2 class, I changed the layout manager of the centerPanel to a BorderLayout, and then added the scrollPane to the BorderLayout.WEST position of the centerPanel. This will cause the scrollPane and its contents to be locked to the left side of the centerPanel.




