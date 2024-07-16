import java.io.*;
import java.util.*;

public class BTreeIndex {
    Node root;

    public static class Node {
        String term;
        List<String> docNames;
        Node left, right;

        public Node(String term, String docName) {
            this.term = term;
            this.docNames = new ArrayList<>();
            this.docNames.add(docName);
            this.left = null;
            this.right = null;
        }
    }

    public BTreeIndex(String folderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] terms = line.split("\\s+");
                    for (String term : terms) {
                        root = add(root, new Node(term.toLowerCase(), file.getName()));
                    }
                }
                reader.close();
            }
        }
    }

    public Node add(Node node, Node iNode) {
        if (node == null) {
            return iNode;
        }
        if (node.term.compareTo(iNode.term) > 0) {
            node.left = add(node.left, iNode);
        } else if (node.term.compareTo(iNode.term) < 0) {
            node.right = add(node.right, iNode);
        } else {
            if (!node.docNames.contains(iNode.docNames.get(0))) {
                node.docNames.add(iNode.docNames.get(0));
            }
        }
        return node;
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

    public List<String> conjunctiveSearch(String[] terms) {
        List<String> results = new ArrayList<>();
        if (terms.length == 0)
            return results;

        Node first = search(root, terms[0].toLowerCase());
        if (first == null)
            return results;

        results.addAll(first.docNames);
        for (int i = 1; i < terms.length; i++) {
            Node termNode = search(root, terms[i].toLowerCase());
            if (termNode == null)
                return new ArrayList<>();
            results.retainAll(termNode.docNames);
        }
        return results;
    }

    public void visualizeTree() {
        if (root == null)
            return;

        Map<Integer, List<Node>> levelMap = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        Queue<Integer> levels = new LinkedList<>();

        queue.add(root);
        levels.add(0);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();
            int level = levels.poll();

            levelMap.putIfAbsent(level, new ArrayList<>());
            levelMap.get(level).add(currentNode);

            if (currentNode.left != null) {
                queue.add(currentNode.left);
                levels.add(level + 1);
            }
            if (currentNode.right != null) {
                queue.add(currentNode.right);
                levels.add(level + 1);
            }
        }

        try {
            PrintWriter writer = new PrintWriter("tree.txt", "UTF-8");
            for (int i = 0; i <= 3; i++) {
                if (levelMap.containsKey(i)) {
                    writer.println("Level " + i + ": " + formatLevel(levelMap.get(i)));
                } else {
                    writer.println("Level " + i + ": ");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatLevel(List<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            sb.append(node.term).append(" (").append(String.join(", ", node.docNames)).append(") ");
            if (node.left != null) {
                sb.append("[L: ").append(node.left.term).append("] ");
            }
            if (node.right != null) {
                sb.append("[R: ").append(node.right.term).append("] ");
            }
            sb.append("| ");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            String folderPath = "Lab1_data";
            BTreeIndex bTreeIndex = new BTreeIndex(folderPath);

            // Single term queries
            System.out.println("Single Term Queries:");
            Node plotNode = bTreeIndex.search(bTreeIndex.root, "plot");
            System.out.println("plot: " + (plotNode != null ? plotNode.docNames : "Not Found"));
            Node watchNode = bTreeIndex.search(bTreeIndex.root, "watch");
            System.out.println("watch: " + (watchNode != null ? watchNode.docNames : "Not Found"));

            // two term queries
            System.out.println("Conjunctive Queries:");
            System.out
                    .println("kick AND ship: "
                            + bTreeIndex.conjunctiveSearch(new String[] { "kick", "ship" }));
            System.out
                    .println("pretty AND decent: "
                            + bTreeIndex.conjunctiveSearch(new String[] { "pretty", "decent" }));

            // three term queries
            System.out.println("robots AND empty AND pink: "
                    + bTreeIndex.conjunctiveSearch(new String[] { "robots", "empty", "pink" }));
            System.out.println("steal AND clout AND attempt: "
                    + bTreeIndex.conjunctiveSearch(new String[] { "steal", "clout", "attempt" }));

            bTreeIndex.visualizeTree();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
