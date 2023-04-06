import java.util.ArrayList;
import java.util.Collections;

public class OrderByFourthLetter {
    public static void main(String[] args) {
        ArrayList<String> words = new ArrayList<String>();
        words.add("apple");
        words.add("banana");
        words.add("carrot");
        words.add("dog");
        words.add("elephant");
        words.add("frog");
        orderByFourthLetter(words);
        System.out.println(words);
        try {
            Process p = Runtime.getRuntime().exec("C:\\Users\\K1331528\\Desktop\\sdfmahdsf\\SchoolManager\\src\\a.bat");
            //"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql" --defaults-extra-file=C:\Users\K1331528\Desktop\my.cnf -u root p2 < "C:\Users\K1331528\Desktop\sdfmahdsf\SchoolManager\filename.sql"

        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void orderByFourthLetter(ArrayList<String> words) {
        Collections.sort(words, (a, b) -> a.charAt(0) - b.charAt(0));
    }
}