import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String path = "Lab1_Data";
        PositionalIndex pi = new PositionalIndex(path);

        // System.out.println(pi);
        // testPhraseQuery(pi, "new home sales");
        testPhraseQuery(pi, "cute outfits");
        testPhraseQuery(pi, "dragon's comedy shtick");
        testPhraseQuery(pi, "for most of its");
        testPhraseQuery(pi, "but the mouse has no reason");
    }

    public static void testPhraseQuery(PositionalIndex pi, String phrase) {
        System.out.println();
        System.out.println("Phrase Query: \"" + phrase + "\"");
        ArrayList<ResultEntry> result = pi.phraseQuery(phrase);
        for (ResultEntry entry : result) {
            System.out.println(entry);
        }
    }
}
