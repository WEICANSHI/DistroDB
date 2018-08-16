package distroDB;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author canshi wei
 * Merkle tree used to quick verification, error detection and recovery
 * Which continuosly compute the hash between to nodes, ultimatly come
 * into a hash root. For verification, simple compare if the hash root
 * are the same.
 */
public class MerkleTree {
	private String sha2HexValue;	// hash root
	private MerkleTree parent;		// parent node
	private MerkleTree leftChild;	// left child 
	private MerkleTree rightChild;	// right child
	private int leftHeight;			// left height
	private int rightHeight;		// right height
	public static Map<String, MerkleTree> Query = new HashMap<>(); // id find Merkle tree
	
	/**
	 * Constructor: construct a tree with given data
	 * @param data: data to store in tree
	 */
	public MerkleTree(String data) {
		this.sha2HexValue = getSHA2HexValue(data);
	}
	
	/**
	 * Constructor: construct a tree given hash root and left right child
	 * @param sha2HexValue: hash root
	 * @param left: left child
	 * @param right: right child
	 */
	public MerkleTree(String sha2HexValue, MerkleTree left, MerkleTree right){
		this.sha2HexValue = sha2HexValue;
		this.leftChild = left;
		this.rightChild = right;
		this.leftHeight = Math.min(left.leftHeight, left.rightHeight) + 1;
		this.rightHeight = Math.min(right.leftHeight, right.rightHeight) + 1;
	}
	
	/**
	 * Add the data to the tree
	 * @param data: data needs to be added
	 * @param id: id of the data in data base
	 * @param root: current root of the tree
	 * @return Merkle tree root
	 */
	public MerkleTree add(String data, String id,  MerkleTree root) {
		MerkleTree tracker = root;
		while(tracker.leftHeight != 0 || tracker.rightHeight != 0){
			if(tracker.rightHeight < tracker.leftHeight) {
				tracker = tracker.rightChild;
			}else {
				tracker = tracker.leftChild;
			}
			//System.out.println(tracker.sha2HexValue);
		}
		
		// onece the tracker's height equal 0, find the potential leaf
		
		// create a new node
		String sha2HexValue = getSHA2HexValue(data);
		MerkleTree leaf = new MerkleTree(sha2HexValue);
		MerkleTree.Query.put(id, leaf);
		// compute the merge hash value and create a merge node
		String Mergesha2HexValue = getSHA2HexValue(tracker.sha2HexValue + leaf.sha2HexValue);
		MerkleTree merge = new MerkleTree(Mergesha2HexValue, tracker, leaf);
		// check if the tracker is right or left and change the link state
		if(tracker.parent != null && tracker.parent.leftChild.equals(tracker)) {
			// tracker is the left child
			tracker.parent.leftChild = merge;
			merge.parent = tracker.parent;
		}else {
			// if tracker has no parent, which means the is the seconde node added
			if(tracker.parent == null) {
				root = merge;
			}else {
				// tracker is the right child
				tracker.parent.rightChild = merge;
				merge.parent = tracker.parent;
			}
		}
		
		tracker.parent = merge;
		leaf.parent = merge;
		
		// recomput the hash
		this.backTrace(merge, root);
		return root;
	}
	
	/**
	 * Delete a certain node from the tree given the id
	 * @param id: id of the data
	 * @param root: current root
	 * @return root after execution done
	 */
	public MerkleTree delete(String id, MerkleTree root) {
		MerkleTree leaf = Query.get(id);
		// if the parent of leaf is the root
		if(leaf.parent.equals(root)) {
			// root become the other part of the tree
			root = this.getAnotherChild(leaf);
		}else {
			MerkleTree peer = this.getAnotherChild(leaf);
			peer.parent = leaf.parent.parent;
			// check if the leaf parent is right or left
			if(leaf.parent.equals(leaf.parent.parent.leftChild)) {
				// if leaf parent is the left child
				leaf.parent.parent.leftChild = peer; // modify the child to be the peer
			}else {
				leaf.parent.parent.rightChild = peer; // modify the child to be the peer
			}
			this.backTrace(peer, root);
		}
		
		return root;
	}
	
	/**
	 * Get another child given one of a child
	 * @param child: a child of the tree
	 * @return another child with the same parent with the given child
	 */
	private MerkleTree getAnotherChild(MerkleTree child) {
		if(child.parent != null) {
			// check the child is right or left
			// child is the left child
			if(child.parent.leftChild.equals(child)) {
				return child.parent.rightChild;
			}else {
				return child.parent.leftChild;
			}
		}
		
		return null;
	}
	
	/**
	 * From a certain leaf, back trace and recompute the parent hash and height
	 * @param leaf: start leaf
	 * @param root: the root of the current tree
	 */
	private void backTrace(MerkleTree leaf, MerkleTree root) {
		MerkleTree tracker = leaf;
		while(!tracker.equals(root)) {
			this.renewState(tracker.parent.leftChild, tracker.parent.rightChild);
			tracker = tracker.parent;
		}
	}
	
	/**
	 * Renew the state. helper function of back trace, recompute the hash
	 * and height of the parent
	 * @param left: left child
	 * @param right: right child
	 */
	private void renewState(MerkleTree left, MerkleTree right) {
		MerkleTree parent = left.parent;
		assert(parent.equals(right.parent));
		// recompute the new hash
		String sha2HexValue = getSHA2HexValue(left.sha2HexValue + right.sha2HexValue);
		parent.sha2HexValue = sha2HexValue;
		// renew the new min value
		parent.leftHeight = Math.min(left.leftHeight, left.rightHeight) + 1;
		parent.rightHeight = Math.min(right.leftHeight, right.rightHeight) + 1;
	}
	
	/**
	   * Return hex string
	   * @param str
	   * @return
	   */
	  public static String getSHA2HexValue(String str) {
	        byte[] cipher_byte;
	        try{
	            MessageDigest md = MessageDigest.getInstance("SHA-256");
	            md.update(str.getBytes());
	            cipher_byte = md.digest();
	            
	            StringBuilder sb = new StringBuilder(2 * cipher_byte.length);
	            for(byte b: cipher_byte) {
	              sb.append(String.format("%02x", b&0xff) );
	            }
	            return sb.toString();
	        } catch (Exception e) {
	                e.printStackTrace();
	        }
	        
	        return "";
	  }
	  
	  /**
	   * Print out the tree using breadth first search
	   * @param root
	   */
	  public void print(MerkleTree root) {
		 List<MerkleTree> child = new ArrayList<>();
		 child.add(root);
		 
		 while(child.size() != 0) {
			 List<MerkleTree> childBuffer = new ArrayList<>();
			 for(int i = 0; i < child.size(); i++) {
				 System.out.print(child.get(i).sha2HexValue.substring(0, 5) + "," + child.get(i).leftHeight + "," + child.get(i).rightHeight + "=====");
				 if(child.get(i).leftChild != null)
					 childBuffer.add(child.get(i).leftChild);
				 if(child.get(i).rightChild != null)
					 childBuffer.add(child.get(i).rightChild);
			 }
			 System.out.println();
			 child = childBuffer;
		 }
		 
	  }
	  
	  /**
	   * Get the hash value of the current node
	   * @return the hash value
	   */
	  public String getSha2HexValue() {
		  return this.sha2HexValue;
	  }
}

