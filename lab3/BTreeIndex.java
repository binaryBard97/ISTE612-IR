import java.io.*;
import java.util.*;

public class BTreeIndex {
    Node root;

    // Node class as defined previously
    public static class Node {
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

    // Constructor to build the binary tree index
    public BTreeIndex(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                BufferedReader reader = new BufferedReader(new FileReader(files[i]));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] terms = line.split("\\s+");
                    for (String term : terms) {
                        add(root, new Node(term, i));
                    }
                }
                reader.close();
            }
        }
    }

    public void add(Node node, Node iNode) {
        if (root == null) {
            root = iNode;
        } else {
            if (node.term.compareTo(iNode.term) > 0) {
                if (node.left == null) {
                    node.left = iNode;
                } else {
                    add(node.left, iNode);
                }
            } else if (node.term.compareTo(iNode.term) < 0) {
                if (node.right == null) {
                    node.right = iNode;
                } else {
                    add(node.right, iNode);
                }
            } else {
                node.docIDs.add(iNode.docIDs.get(0));
            }
        }
    }

    public Node search(Node n, String key) {
        if (n == null || n.term.equals(key)) {
            return n;
        }
        if (n.term.compareTo(key) > 0) {
            return search(n.left, key);
        } else {
            return search(n.right, key);
        }
    }

    public List<Integer> conjunctiveSearch(String[] terms) {
        List<Integer> results = new ArrayList<>();
        if (terms.length == 0)
            return results;

        Node first = search(root, terms[0]);
        if (first == null)
            return results;

        results.addAll(first.docIDs);
        for (int i = 1; i < terms.length; i++) {
            Node termNode = search(root, terms[i]);
            if (termNode == null)
                return new ArrayList<>();
            results.retainAll(termNode.docIDs);
        }
        return results;
    }

    public void visualizeTree(Node node, int level, Map<Integer, List<String>> levelMap) {
        if (node == null)
            return;
        levelMap.putIfAbsent(level, new ArrayList<>());
        levelMap.get(level).add(node.term);
        visualizeTree(node.left, level + 1, levelMap);
        visualizeTree(node.right, level + 1, levelMap);
    }

    public void visualizeTree() {
        Map<Integer, List<String>> levelMap = new HashMap<>();
        visualizeTree(root, 0, levelMap);
        try {
            PrintWriter writer = new PrintWriter("tree.txt", "UTF-8");
            for (Map.Entry<Integer, List<String>> entry : levelMap.entrySet()) {
                writer.println("Level " + entry.getKey() + ": " + String.join(", ", entry.getValue()));
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Change the path to the folder containing your documents
            String folderPath = "my_Lab1_Data";
            BTreeIndex bTreeIndex = new BTreeIndex(folderPath);

            // Single term queries
            System.out.println("Single Term Queries:");
            System.out.println("sales: " + bTreeIndex.search(bTreeIndex.root,
                    "sales").docIDs);
            System.out.println("july: " + bTreeIndex.search(bTreeIndex.root,
                    "july").docIDs);
            // System.out.println("plot: " + bTreeIndex.search(bTreeIndex.root,
            // "plot").docIDs);
            // System.out.println("watch: " + bTreeIndex.search(bTreeIndex.root,
            // "watch").docIDs);

            // Conjunctive queries
            System.out.println("Conjunctive Queries:");
            System.out
                    .println("new AND rise: " + bTreeIndex.conjunctiveSearch(new String[] {
                            "new", "rise" }));
            System.out.println("increase AND sales AND july: "
                    + bTreeIndex.conjunctiveSearch(new String[] { "increase", "sales", "july"
                    }));
            // System.out
            // .println("warner AND animated: "
            // + bTreeIndex.conjunctiveSearch(new String[] { "warner", "animated" }));
            // System.out.println("steal AND clout AND attempt: "
            // + bTreeIndex.conjunctiveSearch(new String[] { "steal", "clout", "attempt"
            // }));

            // Visualize tree
            bTreeIndex.visualizeTree();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
