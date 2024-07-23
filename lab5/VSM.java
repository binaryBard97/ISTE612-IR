package lab5;

import java.util.*;

public class VSM {
   String[] myDocs;
   ArrayList<String> termList;
   ArrayList<ArrayList<Doc>> docLists;
   double[] docLengthVec; // length vector for each document (used for cosine similarity computation)

   public VSM(String[] docs) {
      myDocs = docs;
      termList = new ArrayList<String>();
      docLists = new ArrayList<ArrayList<Doc>>();
      ArrayList<Doc> docList;

      // parse the documents to construct the vector space model
      for (int i = 0; i < myDocs.length; i++) {
         String[] tokens = myDocs[i].split(" ");
         String token;

         for (int j = 0; j < tokens.length; j++) {
            token = tokens[j];

            if (!termList.contains(token)) {
               // new term
               termList.add(token);
               docList = new ArrayList<Doc>();
               Doc doc = new Doc(i, 1); // initial raw frequency is 1
               docList.add(doc); // add term to the postings list for this document
               docLists.add(docList);
            } else {
               // already seen this term
               int index = termList.indexOf(token);
               docList = docLists.get(index);
               boolean match = false; // is this a new document or not?
               // search the postings for this document id, if match, update term frequency
               for (Doc doc : docList) {
                  if (doc.docId == i) { // we've seen this term in this document before
                     doc.tw++;
                     match = true;
                     break;
                  }
               }
               // if no match, we need to add another Doc object with a frequency of 1
               if (!match) {
                  Doc doc = new Doc(i, 1);
                  docList.add(doc);
               }
            }
         }
      } // end of parsing

      // compute the term tf-idf weights and the length vector for each document
      int N = myDocs.length;
      docLengthVec = new double[N];

      for (int i = 0; i < termList.size(); i++) {
         docList = docLists.get(i);
         int df = docList.size(); // how many documents contain this term
         Doc doc;

         for (int j = 0; j < docList.size(); j++) {
            doc = docList.get(j);
            double tfidf = (1 + Math.log(doc.tw)) * Math.log(N / (df * 1.0));
            tfidf = Math.round(tfidf * 100.0) / 100.0;
            docLengthVec[doc.docId] += Math.pow(tfidf, 2);
            doc.tw = tfidf;
            docList.set(j, doc);
         }
      }

      // compute the magnitude of the length vector to use in cosine similarity
      for (int i = 0; i < N; i++) {
         docLengthVec[i] = Math.sqrt(docLengthVec[i]);
      }
   }

   public void rankSearch(String[] query) {
      // key is an integer that points to a document's score for this query
      HashMap<Integer, Double> docs = new HashMap<Integer, Double>();
      ArrayList<Doc> docList;

      // find all documents with any matching query keywords
      for (String term : query) {
         int index = termList.indexOf(term);

         if (index < 0)
            continue; // ignore this keyword

         docList = docLists.get(index);
         double w_t = Math.log((myDocs.length * 1.0) / docList.size()); // N / df of keyword
         Doc doc;

         for (int j = 0; j < docList.size(); j++) {
            doc = docList.get(j);
            double score = w_t * doc.tw; // (need to normalize to perform cosine similarity)

            if (!docs.containsKey(doc.docId)) { // is this doc in hash map yet?
               docs.put(doc.docId, score); // add to hash map
            } else {
               score += docs.get(doc.docId); // add to previous score due to multiple keyword matches
               docs.put(doc.docId, score);
            }
         }
      }
      System.out.println(docs); // output document scores
   }

   public String toString() {
      String matrixString = new String();
      ArrayList<Doc> docList;

      for (int i = 0; i < termList.size(); i++) {
         matrixString += String.format("%-15s", termList.get(i));
         docList = docLists.get(i);

         for (int j = 0; j < docList.size(); j++)
            matrixString += docList.get(j) + "\t";

         matrixString += "\n";
      }

      return matrixString;
   }

   public static void main(String[] args) {
      String[] docs = { "new home sales top forecasts", // top and forecasts are rare terms
            "home sales rise in july",
            "increase in home sales in july", // in has a higher weight since it appears twice
            "july new home sales rise" };

      VSM vsm = new VSM(docs);
      System.out.println(vsm);

      // test cases
      // 1: in
      // 2: in, top
      // 3: in, july
      // 4: nothing, no, new, home
      // String[] query = {"in"};
      // String[] query = {"in", "top"};
      // String[] query = {"in", "july"};
      String[] query1 = { "in" };
      String[] query2 = { "in", "top" };
      String[] query3 = { "in", "july" };
      String[] query4 = { "nothing", "no", "new", "home" };
      vsm.rankSearch(query1);
      vsm.rankSearch(query2);
      vsm.rankSearch(query3);
      vsm.rankSearch(query4);
   }

   static class Doc {
      int docId;
      double tw; // term's weight in this document

      public Doc(int did, double weight) {
         docId = did;
         tw = weight;
      }

      public String toString() {
         String docIdString = docId + ": " + tw;
         return docIdString;
      }
   }
}
