package lab5;

import java.util.*;

/**
 * Document clustering
 *
 */
public class Clustering {

	// Declare attributes here

	/**
	 * Constructor for attribute initialization
	 * 
	 * @param numC number of clusters
	 */
	public Clustering(int numC) {
		// TO BE COMPLETED
	}

	/**
	 * Load the documents to build the vector representations
	 * 
	 * @param docs
	 */
	public void preprocess(String[] docs) {
		// TO BE COMPLETED
	}

	/**
	 * Cluster the documents
	 * For kmeans clustering, use the first and the ninth documents as the initial
	 * centroids
	 */
	public void cluster() {
		// TO BE COMPLETED

	}

	public static void main(String[] args) {
		String[] docs = { "hot chocolate cocoa beans", // doc 0
				"cocoa ghana africa", // doc 1
				"beans harvest ghana", // doc 2
				"cocoa butter", // doc 3
				"butter truffles", // doc 4
				"sweet chocolate can", // doc 5
				"brazil sweet sugar can", // doc 6
				"suger can brazil", // doc 7
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
}

/**
 * 
 * Document class for the vector representation of a document
 */
class Doc {
	// TO BE COMPLETED
}