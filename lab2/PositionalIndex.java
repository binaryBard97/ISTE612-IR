import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class PositionalIndex {
   String[] myDocs;
   ArrayList<String> termList; // dictionary
   ArrayList<ArrayList<DocId>> docLists;

   public PositionalIndex(String folderName) {

      File folder = new File(folderName);
      File[] listOfFiles = folder.listFiles();

      if (listOfFiles != null) {
         myDocs = new String[listOfFiles.length];

         for (int i = 0; i < listOfFiles.length; i++) {
            myDocs[i] = listOfFiles[i].getName();
         }

         Arrays.sort(myDocs);

         termList = new ArrayList<String>();
         docLists = new ArrayList<ArrayList<DocId>>(); // postings list
         ArrayList<DocId> docList; // postings for a single term

         for (int i = 0; i < myDocs.length; i++) { // iterate through all documents
            String[] tokens = parse(folderName + "/" + myDocs[i]); // create tokens
            String token;

            for (int j = 0; j < tokens.length; j++) { // looking through tokens - where are we in doc?
               token = tokens[j];
               // System.out.println(token);
               // System.out.println("TermList after tokenization");
               // System.out.println("termList: " + termList);

               if (!termList.contains(token)) { // is this term in the dictionary? NEW term
                  // System.out.println("If this term is not in the dict i.e NEW term");
                  termList.add(token);
                  // System.out.println("Then added to termList" + termList);
                  docList = new ArrayList<DocId>();
                  // System.out.println(
                  // "The Doc ID i.e i that we are looking at >> " + i + " the index of the term
                  // in that doc ID >> "
                  // + j);
                  DocId doid = new DocId(i, j); // document ID and position passed in
                  docList.add(doid); // add to postings for this term
                  // System.out.println("doid: " + doid);
                  // System.out.println("docList: " + docList);
                  docLists.add(docList); // add row to postings list
                  // System.out.println("docLists: " + docLists);
               } else { // term is in dictionary, need to make updates
                  // System.out.println("If the term is already in the termList");
                  int index = termList.indexOf(token);
                  // System.out.println("then it is at index: " + index);
                  docList = docLists.get(index);
                  // System.out.println("so it is at index/ the name of that docList : " +
                  // docList);
                  int k = 0; // which doc we are referring to
                  boolean match = false; // did we already see this document?
                  // search the postings for a document id
                  // if match, insert a new position for this document
                  for (DocId doid : docList) {
                     // System.out.println("doid is " + doid);
                     // System.out.println("i is >>>>> " + i);
                     // System.out.println("j is >>>>> " + j);
                     // System.out.println("k is >>>>> " + k);
                     if (doid.docId == i) { // we've seen term in this document before
                        // System.out.println("BEFORE docList " + docList);
                        doid.insertPosition(j); // add a position to the position list
                        docList.set(k, doid); // update position list
                        // System.out.println("updated docList " + docList);
                        match = true;
                        break;
                     }
                     k++;
                  }

                  // if no match, add new document Id to the list, along with position
                  if (!match) {
                     DocId doid = new DocId(i, j);
                     docList.add(doid);
                  }
               }
            }
         }
      } else {
         System.out.println("The folder is empty or does not exist.");
      }
   }

   public String toString() {
      String matrixString = new String();
      ArrayList<DocId> docList;

      for (int i = 0; i < termList.size(); i++) {
         matrixString += String.format("%-15s", termList.get(i));
         docList = docLists.get(i);

         for (int j = 0; j < docList.size(); j++) {
            matrixString += docList.get(j) + "\t"; // DocId has a toString method
         }

         matrixString += "\n";
      }

      return matrixString;
   }

   public ArrayList<Integer> intersect(String q1, String q2) {
      ArrayList<Integer> mergedList = new ArrayList<>();
      ArrayList<DocId> l1 = docLists.get(termList.indexOf(q1));
      ArrayList<DocId> l2 = docLists.get(termList.indexOf(q2));
      int id1 = 0, id2 = 0;

      while (id1 < l1.size() && id2 < l2.size()) {
         if (l1.get(id1).docId == l2.get(id2).docId) {
            ArrayList<Integer> pp1 = l1.get(id1).positionList;
            ArrayList<Integer> pp2 = l2.get(id2).positionList;
            int pid1 = 0, pid2 = 0;

            while (pid1 < pp1.size() && pid2 < pp2.size()) {
               if (Math.abs(pp1.get(pid1) - pp2.get(pid2)) == 1) {
                  mergedList.add(l1.get(id1).docId);
                  break;
               }
               if (pp1.get(pid1) < pp2.get(pid2))
                  pid1++;
               else
                  pid2++;
            }
            id1++;
            id2++;
         } else if (l1.get(id1).docId < l2.get(id2).docId) {
            id1++;
         } else {
            id2++;
         }
      }
      return mergedList;
   }

   public String[] parse(String fileName) {
      StringBuilder allLines = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
         String line;
         while ((line = reader.readLine()) != null) {
            allLines.append(line.toLowerCase());
         }
      } catch (FileNotFoundException e) {
         System.out.println("File not found: " + fileName);
      } catch (IOException ioe) {
         ioe.printStackTrace();
      }
      return allLines.toString().split("[ .,?!:;$%&+*/]+");
   }

   // Implement a phraseQuery method that takes in a phrase query wit multiple
   // terms and return a list of DocId objects
   public String phraseQuery(String phrase) {
      String[] terms = phrase.split(" ");
      if (terms.length == 0)
         return "";

      ArrayList<DocId> result = docLists.get(termList.indexOf(terms[0]));

      for (int i = 1; i < terms.length; i++) {
         ArrayList<Integer> intersectResult = intersect(terms[i - 1], terms[i]);
         ArrayList<DocId> newResult = new ArrayList<>();

         for (DocId docId : result) {
            if (intersectResult.contains(docId.docId)) {
               newResult.add(docId);
            }
         }

         result = newResult;
      }

      // Build the output string
      StringBuilder output = new StringBuilder();

      for (String term : terms) {
         if (termList.contains(term)) {
            int index = termList.indexOf(term);
            output.append(String.format("%-15s", term));
            for (DocId docId : docLists.get(index)) {
               output.append(docId).append("\t");
            }
            output.append("\n");
         }
      }

      return output.toString();
   }

}

class DocId {
   int docId;
   ArrayList<Integer> positionList;

   @SuppressWarnings("removal")
   public DocId(int did, int position) {
      docId = did;
      positionList = new ArrayList<Integer>();
      positionList.add(new Integer(position));
   }

   @SuppressWarnings("removal")
   public void insertPosition(int position) {
      positionList.add(new Integer(position));
   }

   public String toString() {
      String docIdString = "" + docId + ":<";
      for (Integer pos : positionList)
         docIdString += pos + ",";

      // remove extraneous final comma
      docIdString = docIdString.substring(0, docIdString.length() - 1) + ">";
      return docIdString;
   }
}
