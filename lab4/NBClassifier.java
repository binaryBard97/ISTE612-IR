import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NBClassifier {
    String[] trainingDocs;
    int[] trainingLabels;
    int numClasses;
    int[] classCounts; // number of docs per class
    String[] classStrings; // concatenated string for all terms in a class
    int[] classTokenCounts; // total number of terms per class (includes duplicate terms) or T_ct
    HashMap<String, Double>[] condProb; // one hash map for each class
    HashSet<String> vocabulary; // entire vocabuary

    public NBClassifier(String trainDataFolder) throws IOException {

    }

    public int classify(String testDoc) {
        int label = 0;
        return label;
    }

    public double classifyAll(String testDataFolder) throws IOException {
        return 1.0;
    }

    public static void main(String[] args) throws IOException {
        // open test and train folders
        // call classfier for training

        // call classfier for testing on test data

    }

}