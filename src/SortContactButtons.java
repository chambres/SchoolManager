import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SortContactButtons {
    public static void sortContactButtonsByLastName(List<ContactButton> contactButtons) {
        Comparator<ContactButton> lastNameComparator = new Comparator<ContactButton>() {
            public int compare(ContactButton btn1, ContactButton btn2) {
                return btn1.lname.compareTo(btn2.lname);
            }
        };
        Collections.sort(contactButtons, lastNameComparator);
    }
}
