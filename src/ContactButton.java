import javax.swing.*;
public class ContactButton extends JButton{
    
    String fname;
    String lname;
    String id;
    String sectionstaught;


    //create constructor
    public ContactButton(String fname, String lname){
        super(lname +", " + fname);
        this.fname = fname;
        this.lname = lname;
        this.id = id;
        this.sectionstaught = sectionstaught;
    }

    //create constructor
    //Added this to get ID when clicked - when teacher/section is deleted from other view
    public ContactButton(String fname, String lname, String id){
        super(lname +", " + fname + ", " + id);
        this.fname = fname;
        this.lname = lname;
        this.id = id;
        this.sectionstaught = sectionstaught;
    }

}