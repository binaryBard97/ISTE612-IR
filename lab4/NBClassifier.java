import java.util.*;

public class NBClassifier {
   String[] trainingDocs;
   int[] trainingLabels;
   int numClasses;
   int[] classCounts; // number of docs per class
   String[] classStrings; // concatenated string for all terms in a class
   int[] classTokenCounts; // total number of terms per class (includes duplicate terms)
   HashMap<String, Double>[] condProb; // one hash map for each class
   HashSet<String> vocabulary; // entire vocabuary
	
   public NBClassifier(String[] docs, int[] labels, int numC)
   {
      trainingDocs = docs;
      trainingLabels = labels;
      numClasses = numC;
      classCounts = new int[numClasses];
      classStrings = new String[numClasses];
      classTokenCounts = new int[numClasses];
      condProb = new HashMap[numClasses];
      vocabulary = new HashSet<String>();
      
      for(int i = 0; i < numClasses; i++) {
         classStrings[i] = "";
         condProb[i] = new HashMap<String, Double>();
      }
      
      for(int i = 0; i < trainingLabels.length; i++) {
         classCounts[trainingLabels[i]]++;
         classStrings[trainingLabels[i]] += (trainingDocs[i] + " "); // add the document content to the class string
      }
      
      for(int i = 0; i < numClasses; i++) {
         String[] tokens = classStrings[i].split(" ");
         classTokenCounts[i] = tokens.length;
      	
         // collecting the token counts
         for(String token : tokens) {
            vocabulary.add(token);
            
            if(condProb[i].containsKey(token)) {
               double count = condProb[i].get(token);
               condProb[i].put(token, count + 1);
            }
            else
               condProb[i].put(token, 1.0);
         }
      }
      
   	// computing the class conditional probability using Laplace smoothing
      for(int i = 0; i < numClasses; i++) {
         Iterator<Map.Entry<String, Double>> iterator = condProb[i].entrySet().iterator();
         int vSize = vocabulary.size();
         
         while(iterator.hasNext())
         {
            Map.Entry<String, Double> entry = iterator.next();
            String token = entry.getKey();
            Double count = entry.getValue();
            count = (count + 1) / (classTokenCounts[i] + vSize);
            condProb[i].put(token, count);
         }
         
         System.out.println("Class " + i + " class conditional probablities\n" + condProb[i]);
      }
   }
	
   public int classify(String testDoc) {
      int label = 0;
      int vSize = vocabulary.size();
      double[] score = new double[numClasses]; // class likelihood for each class
      
      for(int i = 0; i < score.length; i++) {
         // use log to avoid precision problems
         score[i] = Math.log(classCounts[i] * 1.0 / trainingDocs.length); // prior probability of class
      }
      
      String[] tokens = testDoc.split(" ");
      
      for(int i = 0; i < numClasses; i++) {
      
         for(String token: tokens) {
            // use log/addition to avoid precision problems
            if(condProb[i].containsKey(token))
               score[i] += Math.log(condProb[i].get(token)); // term's class conditional probability
            else
               score[i] += Math.log(1.0 / (classTokenCounts[i] + vSize)); // previously unknown term, compute its Laplace smoothed class conditional probability            
         }
      }
      
      double maxScore = score[0];
      
      // find the largest class likelihood and save its label to return as the class value
      for(int i = 0; i < score.length; i++) {
         System.out.println("Class " + i + " likelihood = " + score[i]);
         if(score[i] > maxScore) {
            label = i;
            maxScore = score[i];
         }
      }
   	
      return label;
   }
	
   public static void main(String[] args){
      String[] trainingDocs = {"Chinese Beijing Chinese",
         				          "Chinese Chinese Shanghai",
         				          "Chinese Macao",
         				          "Tokyo Japan Chinese"};
      int[] trainingLabels = {0, 0, 0, 1};
      int numClass = 2;
      NBClassifier nb = new NBClassifier(trainingDocs, trainingLabels, numClass);
      
      String testDoc = "Chinese Chinese Chinese Tokyo Japan";
      System.out.println("Class label = " + nb.classify(testDoc));
   }
}
