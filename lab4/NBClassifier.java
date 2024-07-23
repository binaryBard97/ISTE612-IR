import java.io.*;
import java.nio.file.Files;
import java.util.*;

@SuppressWarnings("unchecked")
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
        preprocess(trainDataFolder);

    }

    /**
     * @param trainDataFolder
     * @throws IOException
     */
    public void preprocess(String trainDataFolder) throws IOException {
        File folder = new File(trainDataFolder);
        List<String> docs = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        // Read positive and negative documents
        File posFolder = new File(folder, "pos");
        File negFolder = new File(folder, "neg");

        for (File file : posFolder.listFiles((dir, name) -> name.endsWith(".txt"))) {
            docs.add(new String(Files.readAllBytes(file.toPath())));
            labels.add(0); // label 0 for positive reviews
        }

        for (File file : negFolder.listFiles((dir, name) -> name.endsWith(".txt"))) {
            docs.add(new String(Files.readAllBytes(file.toPath())));
            labels.add(1); // label 1 for negative reviews
        }

        trainingDocs = docs.toArray(new String[0]);
        trainingLabels = labels.stream().mapToInt(i -> i).toArray();
        numClasses = 2; // positive and negative
        classCounts = new int[numClasses];
        classStrings = new String[numClasses];
        classTokenCounts = new int[numClasses];
        condProb = new HashMap[numClasses];
        vocabulary = new HashSet<String>();

        for (int i = 0; i < numClasses; i++) {
            classStrings[i] = "";
            condProb[i] = new HashMap<String, Double>();
        }

        for (int i = 0; i < trainingLabels.length; i++) {
            classCounts[trainingLabels[i]]++;
            classStrings[trainingLabels[i]] += (trainingDocs[i] + " "); // add the document content to the class string
        }

        for (int i = 0; i < numClasses; i++) {
            String[] tokens = classStrings[i].split("\\s+");
            classTokenCounts[i] = tokens.length;

            // collecting the token counts
            for (String token : tokens) {
                vocabulary.add(token);

                if (condProb[i].containsKey(token)) {
                    double count = condProb[i].get(token);
                    condProb[i].put(token, count + 1);
                } else {
                    condProb[i].put(token, 1.0);
                }
            }
        }

        // computing the class conditional probability using Laplace smoothing
        for (int i = 0; i < numClasses; i++) {
            Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
            int vSize = vocabulary.size();

            while (iterator.hasNext()) {
                Map.Entry<String, Double> entry = iterator.next();
                String token = entry.getKey();
                Double count = entry.getValue();
                count = (count + 1) / (classTokenCounts[i] + vSize);
                condProb[i].put(token, count);
            }
        }
    }

    public int classify(String testDoc) {
        int label = 0;
        int vSize = vocabulary.size();
        double[] score = new double[numClasses]; // class likelihood for each class

        for (int i = 0; i < score.length; i++) {
            score[i] = Math.log(classCounts[i] * 1.0 / trainingDocs.length); // prior probability of class
        }

        String[] tokens = testDoc.split("\\s+");

        for (int i = 0; i < numClasses; i++) {
            for (String token : tokens) {
                if (condProb[i].containsKey(token)) {
                    score[i] += Math.log(condProb[i].get(token)); // term's class conditional probability
                } else {
                    score[i] += Math.log(1.0 / (classTokenCounts[i] + vSize)); // previously unknown term, compute its
                                                                               // Laplace smoothed class conditional
                                                                               // probability
                }
            }
        }

        double maxScore = score[0];

        // find the largest class likelihood and save its label to return as the class
        // value
        for (int i = 0; i < score.length; i++) {
            if (score[i] > maxScore) {
                label = i;
                maxScore = score[i];
            }
        }

        return label;
    }

    public double classifyAll(String testDataFolder) throws IOException {
        File folder = new File(testDataFolder);
        List<String> docs = new ArrayList<>();
        List<Integer> trueLabels = new ArrayList<>();

        // Read positive and negative documents
        File posFolder = new File(folder, "pos");
        File negFolder = new File(folder, "neg");

        for (File file : posFolder.listFiles()) {
            docs.add(new String(Files.readAllBytes(file.toPath())));
            trueLabels.add(0); // label 0 for positive reviews
        }

        for (File file : negFolder.listFiles()) {
            docs.add(new String(Files.readAllBytes(file.toPath())));
            trueLabels.add(1); // label 1 for negative reviews
        }

        int correct = 0;

        for (int i = 0; i < docs.size(); i++) {
            String doc = docs.get(i);
            int trueLabel = trueLabels.get(i);
            int predictedLabel = classify(doc);
            if (trueLabel == predictedLabel) {
                correct++;
            }
        }
        int total = docs.size();
        System.out.println("Correctly classified: " + correct + " out of " + total);

        return correct * 1.0 / total;
        // return 1.0;
    }

    public static void main(String[] args) throws IOException {

        String trainDataFolder = "Lab4_Data/train";
        String testDataFolder = "Lab4_Data/test";

        NBClassifier nb = new NBClassifier(trainDataFolder);

        double accuracy = nb.classifyAll(testDataFolder);
        System.out.println("Classification accuracy = " + accuracy);

    }

}