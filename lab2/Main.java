public class Main {
    public static void main(String[] args) {
        String path = "my_Lab1_Data";
        PositionalIndex pi = new PositionalIndex(path);

        System.out.println(pi);

        // testPhraseQuery(pi, "the quick brown");
        testPhraseQuery(pi, "new home sales");
        // testPhraseQuery(pi, "quick brown fox jumps");
        // testPhraseQuery(pi, "over the lazy brown dog");
    }

    public static void testPhraseQuery(PositionalIndex pi, String phrase) {
        System.out.println("Phrase Query: \"" + phrase + "\"");
        String result = pi.phraseQuery(phrase);
        System.out.println(result);
    }
}
