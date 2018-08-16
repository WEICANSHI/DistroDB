package distroDB;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author canshi wei
 * Web administrator. Only Administrator has the power to
 * add node to the network
 */
public class Admin {
	private Map<String, Header> nodes; // nodes in the network
	private Requester requester; // send renew message to the node in network
	private AdminReceiver receiver;	// the receiver for Admin
	
	/**
	 * Constructor: for initialize administrator
	 */
	public Admin(){
		nodes = new HashMap<>();
		receiver = new AdminReceiver(9999);
		new Thread(receiver).start();
	}
	
	/**
	 * Add a new node to the network, and assign an identifier to the node
	 * @param identifier: assign an identifier to the newly added node
	 * @param dns: the dns where the node located
	 * @param port: the port number of the node socket
	 */
	public void addNode(String identifier, String dns, int port) {
		/*
		 * Inform all the nodes in the network that a new 
		 * node is going to be added to the network
		 */
		
		// Initialize an iterator and iterate through all nodes
		Iterator<Entry<String, Header>> itr = nodes.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, Header> nodeEntry = itr.next();
			String nodeDns = nodeEntry.getValue().getDns();	// get the node dns
			int nodePort = nodeEntry.getValue().getPort();	// get the node port
			requester = new Requester(nodeDns, nodePort);	// initialize a requester
			// send message through requester
			requester.sendMessage("ADDNODE " + identifier + " " + dns + " " + port);
		}
		
		/*
		 * Tell the new node the information in the network
		 */
		// Initialize an iterator and iterate through all nodes
		requester = new Requester(dns, port);	// initialize a requester
		requester.sendMessage("ASSIGN " + identifier); // assign the node an identifier
		itr = nodes.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<String, Header> nodeEntry = itr.next();
			String nodeDns = nodeEntry.getValue().getDns();	// get the node dns
			int nodePort = nodeEntry.getValue().getPort();	// get the node port
			//requester = new Requester(dns, port);	// initialize a requester
			// send message through requester
			requester.sendMessage("ADDNODE " + nodeEntry.getKey() + " " + nodeDns + " " + nodePort);
		}
		
		Header header = new Header(dns, port);
		nodes.put(identifier, header);
	}
	
	/**
	 * Start a connection with a potential honest node and send instruction to the node
	 * @param command: the instruction sent to the node
	 */
	public void executeCommand(String command) {
		// Initialize an iterator and iterate through all nodes
		Iterator<Entry<String, Header>> itr = nodes.entrySet().iterator();
		// send to the first node
		Entry<String, Header> entry = itr.next();
		requester = new Requester(entry.getValue().getDns(), entry.getValue().getPort());
		requester.sendMessage("WAKEUP " + command);
	}
}
