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



public class CourseView extends JPanel {
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
            if(tmp.lname.equalsIgnoreCase("Academic")) {
                Academic.setSelected(true);
                KAP.setSelected(false);
                AP.setSelected(false);
            }
            else if (tmp.lname.equalsIgnoreCase("KAP")) {
                KAP.setSelected(true);
                Academic.setSelected(false);
                AP.setSelected(false);
            }
            else {
                AP.setSelected(true);
                KAP.setSelected(false);
                Academic.setSelected(false);
            }

            ResultSet b = performQuery("SELECT id FROM courses WHERE CourseName = + '" + tmp.fname + "' AND Type = '" + RadioButtonStringToInt(tmp.lname)  + "';");
            try{
                while(b.next()){
                    idField.setText(b.getString("ID"));
                }
            }
            catch(Exception e1){System.out.println(e1);}

            
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
    public CourseView() {

        

        try{
        Class.forName("com.mysql.jdbc.Driver");
            con= DriverManager.getConnection("jdbc:mysql://localhost:3306/p2?characterEncoding=utf8","root","password");
        }
        catch(Exception e){ System.out.println(e);}

        int rs = performUpdate(
        "create table courses (ID int auto_increment primary key," + "\n" +
        "CourseName varchar(500) NOT null," + "\n" + 
        "Type int NOT null" + "\n" +
        ");"
        );



        System.out.println(rs);
    
        
        File f = new File("contacts.txt");

        Start();

        if(f.exists() && !f.isDirectory()) { 
            try {
                List<String> lines = Files.readAllLines(Paths.get("contacts.txt"), java.nio.charset.StandardCharsets.UTF_8);
                for(String line: lines) {
                    String[] parts = line.split(",");
                    String courseName="";
                    if(parts[0].equalsIgnoreCase("Academic")) {
                        Academic.setSelected(true);
                        KAP.setSelected(false);
                        AP.setSelected(false);
                    }
                    else if (parts[0].equalsIgnoreCase("KAP")) {
                        KAP.setSelected(true);
                        Academic.setSelected(false);
                        AP.setSelected(false);
                    }
                    else {
                        AP.setSelected(true);
                        KAP.setSelected(false);
                        Academic.setSelected(false);
                    }
                    ContactButton button = new ContactButton(courseName,getStringFromRadioButtons() );
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


    //radio button for type
    JRadioButton Academic =new JRadioButton("Academic");
    JRadioButton AP =new JRadioButton("AP");
    JRadioButton KAP =new JRadioButton("KAP");

    JRadioButton[] types = {Academic, KAP, AP}; // store the labels in an array
    
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

        JPanel rightPanel = new JPanel(null);        
        rightPanel.setPreferredSize(new Dimension(400, 500));
        rightPanel.setMaximumSize(new Dimension(400, 500));
        rightPanel.setBackground(Color.pink);

        JLabel CourseNameLabel = new JLabel("Course Name:");
        JLabel TypeLabel = new JLabel("Type:");
        JLabel idLabel = new JLabel("ID:");



        int labelX = 40; // set the X position of the labels
        int labelY = 30; // set the Y position of the first label
        int labelHeight = 20; // set the height of the labels
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
       

        CourseNameField.setBounds(labelX+100, labelY, 220, labelHeight); // hardcode the position and size of the text field
        labelY += labelHeight + labelGap; // update the Y position for the next text field
        rightPanel.add(CourseNameField);

        

        //radio button for type

        Academic.setBounds(labelX+100, labelY, 100, labelHeight);
        rightPanel.add(Academic);

        AP.setBounds(labelX+100+96, labelY,50, labelHeight);
        rightPanel.add(AP);

        KAP.setBounds(labelX+100+96+50, labelY, 100, labelHeight);
        rightPanel.add(KAP);
        labelY += labelHeight + labelGap; // update the Y position for the next text field

        idField.setBounds(labelX+100, labelY, 220, labelHeight); // hardcode the position and size of the text field
        labelY += labelHeight + labelGap; // update the Y position for the next text field
        rightPanel.add(idField);

        
        for(JRadioButton typess : types) { // loop through the labels
            typess.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(JRadioButton typea : types) {

                        JRadioButton tmp = (JRadioButton)e.getSource();
                        if(typea != tmp)
                            typea.setSelected(false);
                        System.out.println(typea.getText() + " " + tmp.getText());

                        
                    }
                }
            });
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
                CourseNameField.setText("");
                for(JRadioButton button : types ){
                    button.setSelected(false);
                }
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

    String getStringFromRadioButtons(){
        for(JRadioButton a : types){
            if(a.isSelected()){
                return a.getText();
            }
        }
        return "";
    }

    public void addButton(){
        String fname = CourseNameField.getText();
        String lname =getStringFromRadioButtons();

        // String.valueOf(TypeToInt(getStringFromRadioButtons()));


        try{
            
            performUpdate(String.format(
                "insert into courses(CourseName, Type)" +
                " values ('%s', %s);" , fname, TypeToInt(lname)));
            
            ResultSet b = performQuery("select * from courses");
            while(b.next()){
                System.out.println(b.getString("ID") + " " + b.getString("CourseName") + " " + b.getInt("Type"));
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
                if(tmp.lname.equalsIgnoreCase("Academic")) {
                    Academic.setSelected(true);
                    KAP.setSelected(false);
                    AP.setSelected(false);
                }
                else if (tmp.lname.equalsIgnoreCase("KAP")) {
                    KAP.setSelected(true);
                    Academic.setSelected(false);
                    AP.setSelected(false);
                }
                else {
                    AP.setSelected(true);
                    KAP.setSelected(false);
                    Academic.setSelected(false);
                }
//                TypeField.setText(tmp.lname);
                
                saveChanges.setEnabled(true);
                deleteContact.setEnabled(true);
                submit.setEnabled(false);
                clear.setEnabled(false);

                ResultSet b = performQuery("SELECT id FROM courses WHERE CourseName = + '" + tmp.fname + "' AND Type = '" + RadioButtonStringToInt(lname) + "';");
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

    public void addButton(String fname, String lname){
        try{

            performUpdate(String.format(
                    "insert into courses(CourseName, Type)" +
                            " values ('%s', %s);" , fname, TypeToInt(lname)));

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
                if(tmp.lname.equalsIgnoreCase("Academic")) {
                    Academic.setSelected(true);
                    KAP.setSelected(false);
                    AP.setSelected(false);
                }
                else if (tmp.lname.equalsIgnoreCase("KAP")) {
                    KAP.setSelected(true);
                    Academic.setSelected(false);
                    AP.setSelected(false);
                }
                else {
                    AP.setSelected(true);
                    KAP.setSelected(false);
                    Academic.setSelected(false);
                }

                saveChanges.setEnabled(true);
                deleteContact.setEnabled(true);
                submit.setEnabled(false);
                clear.setEnabled(false);

                ResultSet b = performQuery("SELECT id FROM courses WHERE CourseName = + '" + tmp.fname + "' AND Type = " + RadioButtonStringToInt(lname)  + ";");
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

        buttonPanel.removeAll(); // remove all existing buttons from the panel
        
        for (ContactButton button : contactList) {
            button.setAlignmentX(Component.LEFT_ALIGNMENT); // set the alignment of the button to center
            button.setHorizontalAlignment(SwingConstants.LEFT); // left align the text on the button
            button.setMaximumSize(new Dimension(maxPanelWidth, button.getPreferredSize().height)); // set the maximum size of the button to the maximum width of the panel and the preferred height of the button
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.add(button);

            buttonPanel.add(row);
            scrollPane.revalidate();
        }
        // tell the panel to update its layout
        setVisible(true);
    }

    int TypeToInt(String type){
        switch(type){
            case "Academic":
                return 0;
            case "KAP":
                return 1;
            case "AP":
                return 2;
            default:
                return 0;
        }
    }
    
    
    
    ActionListener saveButtonListener(){
        JTextField fname = CourseNameField;
        JTextField lname = TypeField;

    
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
    
                String CourseName = fname.getText();
                String Type = String.valueOf(TypeToInt(getStringFromRadioButtons()));
                if(CourseName == null || CourseName.equals("") || Type == null || Type.equals("")){
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please enter a course name and type");
                    return;
                }
                int updated = performUpdate("update courses SET CourseName='" + CourseName + "', Type=" + Type +  " where id=" + idField.getText());
    
                int indexInArrayList = contactList.indexOf(current);
                if(indexInArrayList == -1){
                    System.out.println(CourseName + " " + Type + " not found");
                }; //not found (shouldn't happen
    
                //clear fields
                fname.setText("");
                lname.setText("");
                idField.setText(Integer.toString(getNextIncrement()));

    
                ContactButton tmp = new ContactButton(CourseName,types[Integer.parseInt(Type)].getText() );
                ActionListener b = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Button clicked");
                        ContactButton tmp = (ContactButton) e.getSource();
                        CourseNameField.setText(tmp.fname);
                        if(tmp.lname.equalsIgnoreCase("Academic")) {
                            Academic.setSelected(true);
                            KAP.setSelected(false);
                            AP.setSelected(false);
                        }
                        else if (tmp.lname.equalsIgnoreCase("KAP")) {
                            KAP.setSelected(true);
                            Academic.setSelected(false);
                            AP.setSelected(false);
                        }
                        else {
                            AP.setSelected(true);
                            KAP.setSelected(false);
                            Academic.setSelected(false);
                        }
                        ResultSet b = performQuery("SELECT id FROM courses WHERE CourseName = + '" + tmp.fname + "' AND Type = '" + RadioButtonStringToInt(tmp.lname)  + "';");
                        try{
                            while(b.next()){
                                idField.setText(b.getString("ID"));
                            }
                        }
                        catch(Exception e1){System.out.println(e1);}
                        
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

       int RadioButtonToInt(JRadioButton a){
            switch(a.getText()){
                case "Academic":
                    return 0;
                case "KAP":
                    return 1;
                case "AP":
                    return 2;
                default:
                    return 0;
            }
       }

    int RadioButtonStringToInt(String a){
        switch(a){
            case "Academic":
                return 0;
            case "KAP":
                return 1;
            case "AP":
                return 2;
            default:
                return 0;
        }
    }
    
       ActionListener submitButtonListener(){
        JTextField fname = CourseNameField;
        JTextField lname = TypeField;

        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String CourseName = fname.getText();
                String type ="";
                for(JRadioButton button : types){
                    if(button.isSelected()){
                        type = String.valueOf(RadioButtonToInt(button));
                    }
                }
                if(AP.isSelected() == false && KAP.isSelected() == false && Academic.isSelected() == false) {
                    Academic.setSelected(true);
                    AP.setSelected(false);
                    KAP.setSelected(false);
                }
                if(CourseName == null || CourseName.equals("")){
                    //show dialog box
                    JOptionPane.showMessageDialog(null, "Please enter a course Name");
                    return;
                }

    
                addButton();
                idField.setText(Integer.toString(getNextIncrement()));
                //clear fields
                fname.setText("");
                lname.setText("");

                for(JRadioButton button : types ){
                    button.setSelected(false);
                }
                Academic.setSelected(true);
    
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


                //delete from courses with ID selected
                System.out.println("delete from courses where id =" + idField.getText() );
                int updated = performUpdate(String.format("delete from courses where id = %s", idField.getText() )); //delete course with course id

                //select all corresponding sections of the course
                ResultSet sections = performQuery("Select ID from sections where course_id="+idField.getText());
                ArrayList<String> sectionsRemoved = new ArrayList<>();
                while (true) {
                    try {
                        if (!sections.next()) break;
                        sectionsRemoved.add(String.valueOf(sections.getInt("ID")));
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                // update the students in those section to no longer have those sections.
                ResultSet students = performQuery("select * from students");
                try {
                    String section;
                    while (students.next()) {
                         //Find the list of sections for student
                        section = students.getString("Section");
                        if((section == null)||(section==""))
                            break;
                        String[] sectionsStudent = section.split(":");

                        //Remove section if section is in sectionsRemoved
                        String sectionList="";
                        for(int j=0;j<sectionsStudent.length;j++){
                            if(sectionsRemoved.indexOf(sectionsStudent[j]) == -1) {
                                if (sectionList.equals(""))
                                    sectionList = sectionsStudent[j];
                                else
                                    sectionList = sectionList + ":" + sectionsStudent[j];
                            }
                        }

                        updated = performUpdate("update students SET section='" + sectionList + "' where id=" + students.getString("ID"));

                    }
                } catch (SQLException ex) {
                   System.out.println(ex);
                }

                //delete courses from sections
                updated = performUpdate(String.format("delete from sections where course_id=%s", idField.getText() ));
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
            
            CourseView project2 = new CourseView();
            f.add(project2);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
//In this version of the AddNewProject2 class, I changed the layout manager of the centerPanel to a BorderLayout, and then added the scrollPane to the BorderLayout.WEST position of the centerPanel. This will cause the scrollPane and its contents to be locked to the left side of the centerPanel.




