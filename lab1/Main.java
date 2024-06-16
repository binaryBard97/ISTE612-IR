import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String path = "Lab1_Data";
        Parser parser = new Parser(path);

        InvertedIndex invertedIndex = parser.getInvertedIndex();
        // System.out.println("Inverted Index:");
        // System.out.println(invertedIndex);

        // Task 2.1
        singleKeywordSearch(parser, invertedIndex, "plot");
        singleKeywordSearch(parser, invertedIndex, "watch");

        // Task 2.2
        andKeywordSearch(parser, invertedIndex, "warner", "animated");
        andKeywordSearch(parser, invertedIndex, "halloween", "baldwin");

        // Task 2.3
        orKeywordSearch(parser, invertedIndex, "quest", "hello");
        orKeywordSearch(parser, invertedIndex, "showmanship", "clich");

        // Task 2.4
        multipleKeywordSearch(parser, invertedIndex, new String[] { "steal", "clout",
                "attempt" });
        multipleKeywordSearch(parser, invertedIndex, new String[] { "proliferation", "ditzy",
                "house" });
    }

    private static void singleKeywordSearch(Parser parser, InvertedIndex invertedIndex, String query) {
        ArrayList<Integer> result = invertedIndex.search(query);

        if (result != null) {
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("Task 2.1 Single keyword search results for '" + query + "':");
            System.out.println();
            System.out.println();
            for (Integer docId : result) {
                // System.out.println("Document " + docId);
                // System.out.println("result " + result);
                System.out.println(parser.myDocs[docId]);
            }
        } else {
            System.out.println("No match found for single keyword search: '" + query + "'");
        }
    }

    private static void andKeywordSearch(Parser parser, InvertedIndex invertedIndex, String query1, String query2) {
        ArrayList<Integer> result = invertedIndex.searchAnd(new String[] { query1, query2 });

        if (result != null) {
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("Task 2.2 AND keyword search results for '" + query1 + " AND " + query2 + "':");
            System.out.println();
            for (Integer docId : result) {
                System.out.println(parser.myDocs[docId]);
                // System.out.println("Document " + docId);
            }
        } else {
            System.out.println("No match found for AND keyword search: '" + query1 + " AND " + query2 + "'");
        }
    }

    private static void orKeywordSearch(Parser parser, InvertedIndex invertedIndex, String query1, String query2) {
        ArrayList<Integer> result = invertedIndex.searchOr(query1, query2);

        if (result != null) {
            System.out.println();
            System.out.println("-------------------------------------------------");
            System.out.println("Task 2.3 OR keyword search results for '" + query1 + " OR " + query2 + "':");
            System.out.println();
            for (Integer docId : result) {
                System.out.println(parser.myDocs[docId]);
                // System.out.println("Document " + docId);
            }
        } else {
            System.out.println("No match found for OR keyword search: '" + query1 + " OR " + query2 + "'");
        }
    }

    private static void multipleKeywordSearch(Parser parser, InvertedIndex invertedIndex, String[] queries) {
        ArrayList<Integer> result = invertedIndex.searchAndMultiple(queries);

        if (result != null) {
            System.out.println();
            System.out.println("AND keyword search results for multiple terms:");
            System.out.println();
            for (String query : queries) {
                System.out.print(query + " ");
            }
            System.out.println();

            for (Integer docId : result) {
                System.out.println(parser.myDocs[docId]);
                // System.out.println("Document " + docId);
            }
        } else {
            System.out.println("No match found for AND keyword search with multiple terms:");
            for (String query : queries) {
                System.out.print(query + " ");
            }
            System.out.println();
        }
    }
}
