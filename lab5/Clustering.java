package lab5;

import java.util.*;

public class Clustering {

	private int numClusters;
	private List<Doc> documents;
	private Map<String, Integer> termIndex;
	private double[][] tfMatrix;
	private List<List<Integer>> clusters;

	// Constructor for attribute initialization
	public Clustering(int numC) {
		this.numClusters = numC;
		this.documents = new ArrayList<>();
		this.termIndex = new HashMap<>();
		this.clusters = new ArrayList<>();
		for (int i = 0; i < numC; i++) {
			this.clusters.add(new ArrayList<>());
		}
	}

	// Load the documents to build the vector representations
	public void preprocess(String[] docs) {
		int docId = 0;
		for (String doc : docs) {
			String[] terms = doc.split(" ");
			for (String term : terms) {
				if (!termIndex.containsKey(term)) {
					termIndex.put(term, termIndex.size());
				}
			}
			documents.add(new Doc(docId, terms));
			docId++;
		}
		tfMatrix = new double[documents.size()][termIndex.size()];

		for (Doc doc : documents) {
			double[] tfVector = new double[termIndex.size()];
			for (String term : doc.terms) {
				tfVector[termIndex.get(term)]++;
			}
			tfMatrix[doc.docId] = tfVector;
		}
	}

	// Cluster the documents using K-means
	public void cluster() {
		int[] initialCentroids = { 0, 9 }; // first and ninth documents as initial centroids
		double[][] centroids = new double[numClusters][termIndex.size()];

		for (int i = 0; i < numClusters; i++) {
			centroids[i] = Arrays.copyOf(tfMatrix[initialCentroids[i]], termIndex.size());
		}

		boolean changed;
		do {
			changed = false;
			for (List<Integer> cluster : clusters) {
				cluster.clear();
			}

			// Assign documents to the nearest centroid
			for (int i = 0; i < tfMatrix.length; i++) {
				double minDistance = Double.MAX_VALUE;
				int closestCluster = -1;
				for (int j = 0; j < numClusters; j++) {
					double distance = euclideanDistance(tfMatrix[i], centroids[j]);
					if (distance < minDistance) {
						minDistance = distance;
						closestCluster = j;
					}
				}
				clusters.get(closestCluster).add(i);
			}

			// Recompute the centroids
			for (int i = 0; i < numClusters; i++) {
				double[] newCentroid = new double[termIndex.size()];
				for (int docId : clusters.get(i)) {
					for (int j = 0; j < termIndex.size(); j++) {
						newCentroid[j] += tfMatrix[docId][j];
					}
				}
				for (int j = 0; j < termIndex.size(); j++) {
					newCentroid[j] /= clusters.get(i).size();
				}

				if (!Arrays.equals(newCentroid, centroids[i])) {
					changed = true;
					centroids[i] = newCentroid;
				}
			}
		} while (changed);

		// Output cluster assignments
		for (int i = 0; i < numClusters; i++) {
			System.out.println("Cluster: " + i);
			for (int docId : clusters.get(i)) {
				System.out.print(docId + " ");
			}
			System.out.println();
		}
	}

	// Compute the Euclidean distance between two vectors
	private double euclideanDistance(double[] vec1, double[] vec2) {
		double sum = 0;
		for (int i = 0; i < vec1.length; i++) {
			sum += Math.pow(vec1[i] - vec2[i], 2);
		}
		return Math.sqrt(sum);
	}

	public static void main(String[] args) {
		String[] docs = { "hot chocolate cocoa beans", // doc 0
				"cocoa ghana africa", // doc 1
				"beans harvest ghana", // doc 2
				"cocoa butter", // doc 3
				"butter truffles", // doc 4
				"sweet chocolate can", // doc 5
				"brazil sweet sugar can", // doc 6
				"sugar can brazil", // doc 7
				"sweet cake icing", // doc 8
				"cake black forest" // doc 9
		};
		Clustering c = new Clustering(2);

		c.preprocess(docs);

		c.cluster();
		/*
		 * Expected result:
		 * Cluster: 0
		 * 0 1 2 3 4
		 * Cluster: 1
		 * 5 6 7 8 9
		 */
	}

	// Document class for the vector representation of a document
	class Doc {
		int docId;
		String[] terms;

		public Doc(int docId, String[] terms) {
			this.docId = docId;
			this.terms = terms;
		}

		public String toString() {
			return docId + ": " + Arrays.toString(terms);
		}
	}
}
