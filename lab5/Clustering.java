package lab5;

import java.util.*;

public class Clustering {

	private int numClusters;
	private List<Doc> documents;
	private Map<String, Integer> termIndex;
	private double[][] tfMatrix;
	private List<List<Integer>> clusters;
	private double epsilon = 0.0001; // Smoothing factor for EM - textbook page
	private double[][] centroids;
	private double[] clusterPriors;
	private double[][] termProbabilities;

	// Constructor for attribute initialization
	public Clustering(int numC) {
		this.numClusters = numC;
		this.documents = new ArrayList<>();
		this.termIndex = new HashMap<>();
		this.clusters = new ArrayList<>();
		this.centroids = new double[numC][];
		this.clusterPriors = new double[numC];
		this.termProbabilities = new double[numC][];
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

	// Cluster the documents using K-means and initialize EM
	public void cluster() {
		int[] initialCentroids = { 0, 9 }; // First and ninth documents as initial centroids
		for (int i = 0; i < numClusters; i++) {
			centroids[i] = Arrays.copyOf(tfMatrix[initialCentroids[i]], termIndex.size());
			termProbabilities[i] = new double[termIndex.size()];
		}

		boolean changed;
		int iteration = 0;
		double prevLogLikelihood = Double.NEGATIVE_INFINITY;
		double logLikelihood;

		do {
			changed = false;
			for (List<Integer> cluster : clusters) {
				cluster.clear();
			}

			// Expectation Step: Compute soft assignments
			double[][] responsibilities = new double[tfMatrix.length][numClusters];
			for (int i = 0; i < tfMatrix.length; i++) {
				double totalProbability = 0;
				for (int j = 0; j < numClusters; j++) {
					responsibilities[i][j] = computeProbability(tfMatrix[i], centroids[j]);
					totalProbability += responsibilities[i][j];
				}
				for (int j = 0; j < numClusters; j++) {
					responsibilities[i][j] /= totalProbability;
				}
			}

			// Maximization Step: Update centroids and cluster priors
			double[] clusterCounts = new double[numClusters];
			for (int j = 0; j < numClusters; j++) {
				Arrays.fill(termProbabilities[j], 0);
			}

			for (int i = 0; i < tfMatrix.length; i++) {
				for (int j = 0; j < numClusters; j++) {
					clusterCounts[j] += responsibilities[i][j];
					for (int k = 0; k < termIndex.size(); k++) {
						termProbabilities[j][k] += responsibilities[i][j] * tfMatrix[i][k];
					}
				}
			}

			for (int j = 0; j < numClusters; j++) {
				if (clusterCounts[j] > 0) {
					for (int k = 0; k < termIndex.size(); k++) {
						centroids[j][k] = (termProbabilities[j][k] + epsilon) / (clusterCounts[j] + epsilon);
					}
					clusterPriors[j] = clusterCounts[j] / tfMatrix.length;
				}
			}

			// Assign documents to clusters based on responsibilities
			for (int i = 0; i < tfMatrix.length; i++) {
				int bestCluster = 0;
				double maxResponsibility = 0;
				for (int j = 0; j < numClusters; j++) {
					if (responsibilities[i][j] > maxResponsibility) {
						maxResponsibility = responsibilities[i][j];
						bestCluster = j;
					}
				}
				clusters.get(bestCluster).add(i);
			}

			// Compute log-likelihood
			logLikelihood = 0;
			for (int i = 0; i < tfMatrix.length; i++) {
				double docLikelihood = 0;
				for (int j = 0; j < numClusters; j++) {
					docLikelihood += clusterPriors[j] * computeProbability(tfMatrix[i], centroids[j]);
				}
				logLikelihood += Math.log(docLikelihood + epsilon); // epsilon for numerical stability
			}

			// System.out.println("Iteration " + iteration + ": Log-Likelihood = " +
			// logLikelihood);
			// for (int i = 0; i < numClusters; i++) {
			// System.out.println("Cluster " + i + ": " + clusters.get(i));
			// }

			changed = (Math.abs(logLikelihood - prevLogLikelihood) > epsilon);
			prevLogLikelihood = logLikelihood;
			iteration++;
		} while (changed && iteration < 25); // Limit iterations to prevent infinite loop

		// Output cluster assignments
		for (int i = 0; i < numClusters; i++) {
			System.out.println("Cluster: " + i);
			for (int docId : clusters.get(i)) {
				System.out.print(docId + " ");
			}
			System.out.println();
		}
	}

	// Compute the probability of a document given a centroid
	private double computeProbability(double[] docVector, double[] centroidVector) {
		double dotProduct = 0;
		double normDoc = 0;
		double normCentroid = 0;
		for (int i = 0; i < docVector.length; i++) {
			dotProduct += docVector[i] * centroidVector[i];
			normDoc += docVector[i] * docVector[i];
			normCentroid += centroidVector[i] * centroidVector[i];
		}
		return Math.exp(dotProduct / (Math.sqrt(normDoc) * Math.sqrt(normCentroid)));
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
		String[] docs = {
				"hot chocolate cocoa beans", // doc 0
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
