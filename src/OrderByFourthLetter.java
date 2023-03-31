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
    }

    public static void orderByFourthLetter(ArrayList<String> words) {
        Collections.sort(words, (a, b) -> a.charAt(3) - b.charAt(3));
    }
}