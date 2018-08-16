package distroDB;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


/**
 * 
 * @author canshi wei
 * Logger: the memmory of a node, used to save the command, 
 * message receive, and the network graph. Besides, communicate
 * with the Receiver and notify node if changed needs to be made
 *
 */
public class Logger {
	private String command;		// command give by admin
	private String prev_dbHash;	// database hash before execute the command
	private String commit_dbHash; // database hash after execute the command
	private String result;		// result needs to sent to the admin
	private Node reference;		// Communicate with node in software level
	private Map<String, Header> peers;	// the view of the network
	private Map<Verification, Set<String>> prepare_logger;
	private Map<String, Set<String>> commit_logger; // hash, set<id>
	
	/**
	 * Constructor: construct the logger for a node
	 * and start communication within software
	 * @param reference: refer the node is working for
	 */
	public Logger(Node reference) {
		this.peers = new HashMap<>();	// the map of the network	
		this.prepare_logger = new HashMap<>();
		this.commit_logger = new HashMap<>();
		this.reference = reference;	
	}
	
	/**
	 * The node should be added to the network with a 
	 * given identifier, where is unique
	 * @param identifier
	 */
	public void joinNetwork(String identifier) {
		// check if the identifier is already in the network
		if(peers.containsKey(identifier))
			throw new RuntimeException("Identifier already Exist");
		else {
			reference.assignId(identifier);
		}
	}
	
	/**
	 * Add a peer to the network, record the dns and port number of the peer
	 */
	public void addPeer(String identifier, String dns, int port) {
		// check if the identifier is already in the network
		if(peers.containsKey(identifier) || identifier.equals(reference.getId()))
			throw new RuntimeException("Identifier already Exist");
		else {
			// create a new header, and add the node information to the map
			Header header = new Header(dns, port);
			this.peers.put(identifier, header);
			System.out.println(identifier + " add to the network ");
		}
	}
	
	public void wakeUp(String command) {
		reference.wakeUp(command);
	}
	
	/**
	 * Get the pre_prepare message from the receiver
	 * @param command: command needs to be execute in database
	 */
	public void pre_prepare(String command) {
		this.command = command; // buffer the command in logger
		this.prev_dbHash = reference.getHashroot(); // buffer the current state of database
		Verification verification = new Verification(prev_dbHash, command);
		// if no such verification exist before, initialize a new hash set
		if(!prepare_logger.containsKey(verification)) 
			prepare_logger.put(verification, new HashSet<>());
		// put the data in to the logger
		prepare_logger.get(verification).add(reference.getId());
		reference.pre_prepare(command); // notify the stage change to node
	}
	
	/**
	 * Compare if the arriving command and current stage of database match.
	 * if more than half of nodes agree, and their stage and command match, turn into prepare stage
	 * if agree but not match, turn into recovery
	 * if not agree, system needs to be refine
	 * @param identifier: identifier of the sending node
	 * @param dbHash: Merkle tree hash of the sending database
	 * @param command: Command from the administrator
	 */
	public void prepare(String identifier, String dbHash, String command) {
		
		System.out.println("Node : " + reference.getId() + " recieve command " + command + 
				" with hash " + dbHash + " from " + identifier);
		
		// if the identifier is not in the network, directly reject the verification
		if(!peers.containsKey(identifier)) return; 
		// else, the identifier is a recogonized peer, add to the prepare logger
		Verification verification = new Verification(dbHash, command);
		// if no such verification exist before, initialize a new hash set
		if(!prepare_logger.containsKey(verification)) 
			prepare_logger.put(verification, new HashSet<>());
		// put the data in to the logger
		prepare_logger.get(verification).add(identifier);
		
		// check if node ready to get into prepare stage
		Iterator<Entry<Verification, Set<String>>> itr = prepare_logger.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<Verification, Set<String>> entry = itr.next();
			// if number of nodes in the set greater than half, reach agreement
			if(entry.getValue().size() > peers.size()/2) {
				// check if the command and stage match
				if(entry.getKey().command.equals(this.command) && entry.getKey().dbHash.equals(this.prev_dbHash)) {
					reference.prepare(command);
					break;
				}else {
					//recovery stage
					System.out.println("needs to be recovery");
				}
			}
		}
		
		
	}
	
	
	/**
	 * Commit the result to the admin
	 * @param identifier
	 * @param dbHash
	 */
	public void commit(String identifier, String dbHash) {
		
		// if the identifier is not in the network, directly reject the verification
		if(!peers.containsKey(identifier)) return; 
		
		// if no such verification exist before, initialize a new hash set
		if(!commit_logger.containsKey(dbHash)) 
			commit_logger.put(dbHash, new HashSet<>());
		// put the data in to the logger
		commit_logger.get(dbHash).add(identifier);
				
				
		// check if node ready to get into commit stage
		Iterator<Entry<String, Set<String>>> itr = commit_logger.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, Set<String>> entry = itr.next();
			// if number of nodes in the set greater than half, reach agreement
			if(entry.getValue().size() + 1 > peers.size()/2) {
				// check if the command and stage match
				if(entry.getKey().equals(this.commit_dbHash)) {
					reference.commit(result);
				}else {
					//recovery stage
					System.out.println("needs to be recovery");
				}
			}
		}
	}
	
	/**
	 * Get the iterator of the peers map
	 * @return the iterator of the peers map
	 */
	public Iterator<Entry<String, Header> > getPeers(){
		return peers.entrySet().iterator();
	}
	
	/**
	 * Setter: set the result needs to be sent
	 * @param result: String to reply to the admin
	 */
	public void setResult(String result) {
		this.result = result;
	}
	
	/**
	 * Setter: set the command needs to execute
	 * @param command: Command needs to execute
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * Setter: set the database hash
	 * @param dbHash: hash of the database
	 */
	public void setdbHash(String dbHash) {
		this.prev_dbHash = dbHash;
	}
	
	/**
	 * Setter: set the committed database hash
	 * @param dbHash: hash of the database
	 */
	public void setCommitHash(String dbHash) {
		this.commit_dbHash = dbHash;
	}
	
	/**
	 * 
	 * @author canshi wei
	 * A tuple store the hash of the database and the command received
	 * by the peers. 
	 */
	private class Verification {
		public String dbHash;	// hash of the database
		public String command;	// command needs to sent
		
		/**
		 * Constructor: construct a verification tuple
		 * @param dbHash: hash of the data base
		 * @param command: command needs to be sent
		 */
		public Verification(String dbHash, String command) {
			this.dbHash = dbHash;
			this.command = command;
		}
		
		/**
		 * Hash code of the verification
		 */
		@Override
		public int hashCode(){
		    return (dbHash + command).hashCode();
		}
		
		/**
		 * Equal comparator of the verification
		 */
		@Override
		public boolean equals(Object o){
		    if(o instanceof Verification) {
		    	if(((Verification) o).dbHash.equals(this.dbHash)
		    			&& ((Verification) o).command.equals(this.command)) {
		    		return true;
		    	}
		    }
		    
		    return false;
		}
	}
}
