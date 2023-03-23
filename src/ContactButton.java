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


    
}