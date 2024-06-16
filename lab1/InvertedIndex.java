import java.util.*;

public class InvertedIndex {
    String[] myDocs; // document collection
    ArrayList<String> termList; // dictionary
    ArrayList<ArrayList<Integer>> docLists; // used for each term's postings

    public InvertedIndex() {
        termList = new ArrayList<>();
        docLists = new ArrayList<>();
    }

    public void addDocument(String[] tokens, int docId) {
        for (String token : tokens) {
            if (!termList.contains(token)) {
                termList.add(token);
                ArrayList<Integer> docList = new ArrayList<>();
                docList.add(docId);
                docLists.add(docList);
            } else {
                int index = termList.indexOf(token);
                ArrayList<Integer> docList = docLists.get(index);
                if (!docList.contains(docId)) {
                    docList.add(docId);
                }
            }
        }
    }

    public ArrayList<Integer> search(String query) {
        String stemmedQuery = stem(query); // Stem the query term
        int index = termList.indexOf(stemmedQuery);
        if (index < 0)
            return null;
        return docLists.get(index);
    }

    public ArrayList<Integer> searchAnd(String[] query) {
        ArrayList<String> sortedQuery = sortQueryByPostingsListSize(query);
        ArrayList<Integer> result = search(sortedQuery.get(0));
        for (int i = 1; i < sortedQuery.size(); i++) {
            ArrayList<Integer> termResult = search(sortedQuery.get(i));
            if (termResult == null) {
                return null;
            }
            result = intersect(result, termResult);
        }
        return result;
    }

    public ArrayList<Integer> searchOr(String query1, String query2) {
        ArrayList<Integer> result1 = search(query1);
        ArrayList<Integer> result2 = search(query2);
        if (result1 == null)
            return result2;
        if (result2 == null)
            return result1;
        Set<Integer> resultSet = new HashSet<>(result1);
        resultSet.addAll(result2);
        return new ArrayList<>(resultSet);
    }

    public ArrayList<Integer> searchAndMultiple(String[] query) {
        ArrayList<String> sortedQuery = sortQueryByPostingsListSize(query);
        // System.out.println("sortedQuery" + sortedQuery);
        System.out.println("-------------------------------------------------");
        System.out.println("Task 2.4 Query term processing order:");
        System.out.println();
        for (int i = 0; i < sortedQuery.size(); i++) {
            System.out.println((i + 1) + ". " + sortedQuery.get(i));
        }

        ArrayList<Integer> result = search(sortedQuery.get(0));
        for (int i = 1; i < sortedQuery.size(); i++) {
            ArrayList<Integer> termResult = search(sortedQuery.get(i));
            if (termResult == null) {
                return null;
            }
            result = intersect(result, termResult);
        }
        return result;
    }

    private ArrayList<String> sortQueryByPostingsListSize(String[] query) {
        ArrayList<String> queryList = new ArrayList<>(Arrays.asList(query));

        // System.out.println("Before sorting by postings list size:");
        // for (String term : queryList) {
        // int index = termList.indexOf(term);
        // if (index == -1) {
        // // System.out.println("Term: " + term + " (not found in index)");
        // } else {
        // ArrayList<Integer> list = docLists.get(index);
        // // System.out.println("Term: " + term + " (postings list size: " +
        // list.size() + ")");
        // }
        // }

        queryList.sort((term1, term2) -> {
            int index1 = termList.indexOf(term1);
            int index2 = termList.indexOf(term2);
            if (index1 == -1)
                return 1; // If term1 is not in the index, place it later
            if (index2 == -1)
                return -1; // If term2 is not in the index, place it later
            ArrayList<Integer> list1 = docLists.get(index1);
            ArrayList<Integer> list2 = docLists.get(index2);
            return Integer.compare(list1.size(), list2.size());
        });

        // System.out.println("After sorting by postings list size:");
        // for (String term : queryList) {
        // int index = termList.indexOf(term);
        // if (index == -1) {
        // System.out.println("Term: " + term + " (not found in index)");
        // } else {
        // ArrayList<Integer> list = docLists.get(index);
        // System.out.println("Term: " + term + " (postings list size: " + list.size() +
        // ")");
        // }
        // }

        return queryList;
    }

    private ArrayList<Integer> intersect(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        ArrayList<Integer> intersectedList = new ArrayList<>();
        int idx1 = 0, idx2 = 0;

        while (idx1 < list1.size() && idx2 < list2.size()) {
            int docId1 = list1.get(idx1);
            int docId2 = list2.get(idx2);
            if (docId1 == docId2) {
                intersectedList.add(docId1);
                idx1++;
                idx2++;
            } else if (docId1 < docId2) {
                idx1++;
            } else {
                idx2++;
            }
        }

        return intersectedList;
    }

    public ArrayList<Integer> search(String query1, String query2) {
        ArrayList<Integer> result1 = search(query1);
        ArrayList<Integer> result2 = search(query2);
        if (result1 == null || result2 == null) {
            return null; // no documents contain either keyword
        }
        return merge(result1, result2);
    }

    private ArrayList<Integer> merge(ArrayList<Integer> l1, ArrayList<Integer> l2) {
        ArrayList<Integer> mergedList = new ArrayList<>();
        int id1 = 0, id2 = 0; // positions in the respective lists

        while (id1 < l1.size() && id2 < l2.size()) {
            if (l1.get(id1).intValue() == l2.get(id2).intValue()) { // found a match
                mergedList.add(l1.get(id1));
                id1++;
                id2++;
            } else if (l1.get(id1) < l2.get(id2)) { // l1 docId is smaller, advance l1 pointer
                id1++;
            } else { // l2 docId is smaller, advance l2 pointer
                id2++;
            }
        }

        return mergedList;
    }

    public String toString() {
        StringBuilder matrixString = new StringBuilder();
        ArrayList<Integer> docList;

        for (int i = 0; i < termList.size(); i++) {
            matrixString.append(String.format("%-15s", termList.get(i)));
            docList = docLists.get(i);

            for (int j = 0; j < docList.size(); j++) {
                matrixString.append(docList.get(j)).append("\t");
            }

            matrixString.append("\n");
        }

        return matrixString.toString();
    }

    public String stem(String token) {
        Stemmer stemmer = new Stemmer();
        stemmer.add(token.toCharArray(), token.length());
        stemmer.stem();
        return stemmer.toString();
    }

}
