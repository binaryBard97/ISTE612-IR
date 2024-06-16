import java.util.*;

public class Shreya_Patel_IncidenceMatrix {
   String[] myDocs; // document collection
   ArrayList<String> termList; // list of all terms in the document collection
   ArrayList<int[]> docLists; // the binary representation of terms

   /**
    * Construct an incidence matrix
    * 
    * @param docs List of input strings (or documents)
    */
   public Shreya_Patel_IncidenceMatrix(String[] docs) {
      myDocs = docs; // store the document collection here
      termList = new ArrayList<String>(); // empty list of terms
      docLists = new ArrayList<int[]>();

      // separate each document into individual terms and store them in termList
      for (int i = 0; i < myDocs.length; i++) {
         String[] tokens = myDocs[i].split(" "); // assume terms are space separated

         for (String token : tokens) {
            // have we seen this token before? If not, add it to our term list
            if (!termList.contains(token)) // this is a new term
            {
               termList.add(token);
               int[] docList = new int[myDocs.length]; // blank document list has length of number of docs
               docList[i] = 1; // set this entry to 1 since it appears in the current document
               docLists.add(docList); // add term's row to the entire document list
            } else // already seen this term; update document list for that term
            {
               int index = termList.indexOf(token); // find the correct row for this term
               int[] docList = docLists.remove(index); // grab the row and remove it
               docList[i] = 1; // set this entry to 1
               docLists.add(index, docList); // add the row back to the document list where it previously was
            }
         }
      }
   }

   public String toString() {
      String matrixString = new String();

      // print out each term and corresponding row in incidence matrix
      for (int i = 0; i < termList.size(); i++) {
         matrixString += String.format("%-15s", termList.get(i));
         int[] docList = docLists.get(i);

         // print out incidence matrix
         for (int j = 0; j < docList.length; j++) {
            matrixString += docList[j] + "\t"; // separate entries by tab
         }

         matrixString += "\n"; // end the line
      }

      return matrixString;
   }

   // single keyword query that returns all documents with that keyword
   public ArrayList<Integer> search(String query) {
      ArrayList<Integer> resultList = new ArrayList<Integer>();
      int queryIndex = termList.indexOf(query);

      if (queryIndex != -1) { // check if the term exists
         int[] docList = docLists.get(queryIndex);
         for (int i = 0; i < docList.length; i++) {
            if (docList[i] == 1) {
               resultList.add(i); // add document ID to the result list
            }
         }
      }

      return resultList;
   }

   // multi-keyword query that returns all documents containing all query keywords
   public ArrayList<Integer> search(String[] query) {
      ArrayList<Integer> resultList = new ArrayList<Integer>();
      HashSet<Integer> allDocs = new HashSet<Integer>();

      // iterate through each query term and find documents containing that term
      for (String term : query) {
         int queryIndex = termList.indexOf(term);
         if (queryIndex != -1) {
            int[] docList = docLists.get(queryIndex);
            for (int i = 0; i < docList.length; i++) {
               if (docList[i] == 1) {
                  allDocs.add(i); // add document ID to a set for all terms
               }
            }
         }
      }

      // iterate through all documents and check if they are in all query term lists
      // (intersection)
      for (int docID : allDocs) {
         boolean allPresent = true;
         for (String term : query) {
            int queryIndex = termList.indexOf(term);
            if (docLists.get(queryIndex)[docID] == 0) {
               allPresent = false; // if a term is missing in a document, skip it
               break;
            }
         }
         if (allPresent) {
            resultList.add(docID); // add document ID to the result if it has all terms
         }
      }

      return resultList;
   }

   // public static void main(String[] args) {
   // String[] docs = { "new home sales top forecasts",
   // "home sales rise in july",
   // "increase in home sales in july",
   // "july new home sales rise" }; // document collection

   // IncidenceMatrix matrix = new IncidenceMatrix(docs);
   // System.out.println(matrix);

   // String singleQuery = new String("new");
   // ArrayList<Integer> singleQueryResult = matrix.search(singleQuery);

   // String[] multiQuery = { "sales", "july", "increase" };
   // ArrayList<Integer> multiQueryResult = matrix.search(multiQuery);
   // }
   // }

   public static void main(String[] args) {
      String[] docs = { "new home sales top forecasts",
            "home sales rise in july",
            "increase in home sales in july",
            "july new home sales rise" }; // document collection

      Shreya_Patel_IncidenceMatrix matrix = new Shreya_Patel_IncidenceMatrix(docs);
      System.out.println(matrix);

      // Single keyword query
      String singleQuery = "new";
      ArrayList<Integer> singleQueryResult = matrix.search(singleQuery);
      System.out.println("Documents containing \"" + singleQuery + "\": " + singleQueryResult);

      // Multi-keyword query (all terms must be present)
      String[] multiQuery = { "sales", "july", "increase" };
      ArrayList<Integer> multiQueryResult = matrix.search(multiQuery);
      System.out
            .println("Documents containing all terms: \"" + String.join(",", multiQuery) + "\": " + multiQueryResult);
   }
}
