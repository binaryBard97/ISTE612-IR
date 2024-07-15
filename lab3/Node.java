import java.util.ArrayList;
import java.util.List;

public class Node {
    String term;
    List<Integer> docIDs;
    Node left, right;

    public Node(String term, int docID) {
        this.term = term;
        this.docIDs = new ArrayList<>();
        this.docIDs.add(docID);
        this.left = null;
        this.right = null;
    }
}