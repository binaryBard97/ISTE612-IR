import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String path = "my_Lab1_Data";
        PositionalIndex pi = new PositionalIndex(path);

        System.out.println(pi);

        ArrayList<Integer> result = pi.intersect("plot", "two");

        if (result.size() != 0) {
            for (Integer i : result)
                System.out.println("Document " + i + ": " + pi.myDocs[i]);
        } else
            System.out.println("No adjacency found!");
    }
}
