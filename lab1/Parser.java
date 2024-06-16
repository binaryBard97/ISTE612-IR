import java.io.*;
import java.util.*;

public class Parser {
    String[] myDocs;
    ArrayList<String> stopList;
    InvertedIndex invertedIndex;

    public Parser(String folderName) {
        stopList = new ArrayList<>();
        invertedIndex = new InvertedIndex();
        loadStopWords("stopwords.txt"); // Load stop words from file

        File folder = new File(folderName);
        File[] listOfFiles = folder.listFiles();

        myDocs = new String[listOfFiles.length]; // store file names

        System.out.println("Unsorted document list");
        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(listOfFiles[i].getName());
            myDocs[i] = listOfFiles[i].getName();
        }

        System.out.println("Sorted document list");
        Arrays.sort(myDocs);
        for (int i = 0; i < myDocs.length; i++) {
            System.out.println(myDocs[i]);
            String[] tokens = parse(folderName + "/" + myDocs[i]);
            addDocumentToIndex(tokens, i);
        }
    }

    private void addDocumentToIndex(String[] tokens, int docId) {
        List<String> processedTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!isStopword(token)) {
                String stemmedToken = stem(token);
                processedTokens.add(stemmedToken);
            } // else {
              // System.out.println("Stopword was and perhaps removed: " + token);
              // }
        }
        invertedIndex.addDocument(processedTokens.toArray(new String[0]), docId);
    }

    private void loadStopWords(String stopWordsFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stopList.add(line.trim().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Stopwords file not found: " + stopWordsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public boolean isStopword(String key) {
        return stopList.contains(key);
    }

    public String stem(String token) {
        Stemmer stemmer = new Stemmer();
        stemmer.add(token.toCharArray(), token.length());
        stemmer.stem();
        // System.out.println("from Parser >> stemmed: " + stemmer.toString());
        return stemmer.toString();
    }

    public InvertedIndex getInvertedIndex() {
        return invertedIndex;
    }

}
