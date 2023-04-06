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
            String prog = "C:\\Program Files\\MySQL\\MySQL Server 5.7\\bin\\mysql\\";
            String user = "-uroot";
            String pass = "-ppassword";

            ProcessBuilder builder = new ProcessBuilder(prog, user, pass);
            Process runtimeProcess = builder.start();
            int result = runtimeProcess.waitFor();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void orderByFourthLetter(ArrayList<String> words) {
        Collections.sort(words, (a, b) -> a.charAt(0) - b.charAt(0));
    }
}