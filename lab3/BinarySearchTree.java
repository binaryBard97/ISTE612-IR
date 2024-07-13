import java.util.Arrays;


class Node {
   Node left;
   Node right;
   int data;
   
   public Node(int value) {
      data = value;
   }
}

public class BinarySearchTree {
   Node root; // root node of the entire tree
   
   public BinarySearchTree(int[] keys) {
     // sort keys in ascending order
      Arrays.sort(keys);
      int start = 0;
      int end = keys.length - 1;
      int mid = (start + end) / 2;
      root = new Node(keys[mid]);
      
      // left side of array passed to left subtree
      insert(root, keys, start, mid - 1);
      // right side of array passed to right subtree
      insert(root, keys, mid + 1, end);
   }
   
   public void insert(Node node, int[] keys, int start, int end) {
      if(start <= end) {
         int mid = (start + end) / 2;
         if(keys[mid] < node.data) { // left subtree
            node.left = new Node(keys[mid]);
            insert(node.left, keys, start, mid - 1);
            insert(node.left, keys, mid + 1, end);
         }
         else { // right subtree
            node.right = new Node(keys[mid]);
            insert(node.right, keys, start, mid - 1);
            insert(node.right, keys, mid + 1, end);
         }
      }
   }
   
   public void inorderTraversal(Node node) {
      // print the contents of the tree in increasing order
      if(node != null) {
         inorderTraversal(node.left);
         System.out.println("Traversed " + node.data); // print node's key value
         inorderTraversal(node.right);
      }
   }
   
   public Node search(Node node, int key) {
      if(node == null)
         // hitting an empty node means search has failed
         return null;
      if(node.data == key)
         // found a match, return the Node's data
         return node;
      else if(node.data > key) 
         // need to search the left subtree since key is less than node value
         return search(node.left, key);
      else
         // key value is larger than current node, search right subtree
         return search(node.right, key); 
   }
   
   public static void main(String args[]) {
      int[] key_values = {16, 70, 11, 23, 15, 25, 106};
      BinarySearchTree bst = new BinarySearchTree(key_values);
      
      System.out.println("Inorder tree traversal");
      bst.inorderTraversal(bst.root);
      
      Node node = bst.search(bst.root, 23);
      
      if(node != null)
         System.out.println("Found " + node.data);
      else
         System.out.println("No match");
   }
}
