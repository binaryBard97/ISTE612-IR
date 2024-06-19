import java.util.*;

public class PositionalIndex {
   String[] myDocs;
   ArrayList<String> termList; // dictionary
   ArrayList<ArrayList<DocId>> docLists;
   
   public PositionalIndex(String[] docs) {
      myDocs = docs;
      termList = new ArrayList<String>();
      docLists = new ArrayList<ArrayList<DocId>>(); // postings list
      ArrayList<DocId> docList; // postings for a single term
      
      for(int i = 0; i < myDocs.length; i++) {
         String[] tokens = myDocs[i].split(" ");
         String token;
         
         for(int j = 0; j < tokens.length; j++) {
            token = tokens[j];
            
            if(!termList.contains(token)) { // is this term in the dictionary?
               termList.add(token);
               docList = new ArrayList<DocId>();
               DocId doid = new DocId(i, j); // document ID and position passed in
               docList.add(doid); // add to postings for this term
               docLists.add(docList); // add row to postings list
            }
            else { // term is in dictionary, need to make updates
               int index = termList.indexOf(token);
               docList = docLists.get(index);
               int k = 0; 
               boolean match = false; // did we already see this document?
               // search the postings for a document id
               // if match, insert a new position for this document
               for(DocId doid : docList) {
                  if(doid.docId == i) { // we've seen term in this document before
                     doid.insertPosition(j); // add a position to the position list
                     docList.set(k, doid); // update position list
                     match = true;
                     break;
                  }
                  k++;
               }
               
               // if no match, add new document Id to the list, along with position
               if(!match) {
                  DocId doid = new DocId(i, j);
                  docList.add(doid);
               }
            }
         }
      }
   }
   
   public String toString() {
      String matrixString = new String();
      ArrayList<DocId> docList;
      
      for(int i = 0; i < termList.size(); i++) {
         matrixString += String.format("%-15s", termList.get(i));
         docList = docLists.get(i);
         
         for(int j = 0; j < docList.size(); j++) {
            matrixString += docList.get(j) + "\t"; // DocId has a toString method
         }
         
         matrixString += "\n";
      }
      
      return matrixString;
   }
   
   public ArrayList<Integer> intersect(String q1, String q2) {
      ArrayList<Integer> mergedList = new ArrayList<Integer>();
      ArrayList<DocId> l1 = docLists.get(termList.indexOf(q1)); // first term's doc list
      ArrayList<DocId> l2 = docLists.get(termList.indexOf(q2)); // second term's doc list
      int id1 = 0, id2 = 0; // doc list pointers
      
      while(id1 < l1.size() && id2 < l2.size()) {
         // if both terms appear in the same document
         if(l1.get(id1).docId == l2.get(id2).docId) {
            // get the position information for both terms
            ArrayList<Integer> pp1 = l1.get(id1).positionList;
            ArrayList<Integer> pp2 = l2.get(id2).positionList;
            int pid1 = 0, pid2 = 0; // position list pointers
            
            // determine if the two terms have an adjacency in the current document
            // if it does, stop comparing the position lists and add the document ID
            // to the mergedList
            
            id1++;
            id2++;
         }
         else if(l1.get(id1).docId < l2.get(id2).docId)
            id1++;
         else
            id2++;
      }
      
      return mergedList;
   }
   
   public static void main(String[] args) {
      String[] docs = {"new home sales top forecasts",
         			     "home sales rise in july",
         			     "increase in home sales in july", // in appears twice here
         			     "july new home sales rise" };
                       
      PositionalIndex pi = new PositionalIndex(docs);
      System.out.println(pi);
      
      ArrayList<Integer> result = pi.intersect("new", "home");
      
      if(result.size() != 0) {
         for(Integer i : result)
            System.out.println("Document " + i.intValue() + ": " + docs[i.intValue()]);
      }
      else
         System.out.println("No adjacency found!");
   }
}

class DocId {
   int docId;
   ArrayList<Integer> positionList;
   
   public DocId(int did, int position) {
      docId = did;
      positionList = new ArrayList<Integer>();
      positionList.add(new Integer(position));
   }
   
   public void insertPosition(int position) {
      positionList.add(new Integer(position));
   }
   
   public String toString() {
      String docIdString = "" + docId + ":<";
      for(Integer pos : positionList)
         docIdString += pos + ",";
      
      // remove extraneous final comma
      docIdString = docIdString.substring(0, docIdString.length() - 1) + ">";
      return docIdString;
   }
}
