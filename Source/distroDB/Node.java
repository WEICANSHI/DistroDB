package distroDB;


import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 
 * @author canshi wei
 * Stage of the node
 */
enum State{
	SLEEP, PRE_PREPARE, PREPARE, COMMIT;
}

/**
 * @author canshi
 * Node contain logger, receiver and database manager for execution
 * Running as a commander send message and manipulate the behavior of
 * certain objects
 */
public class Node {
	private State state;	// current state
	private String identifier;	// the node identifier, sign by the admin
	
	private Logger logger;	// logger for record
	private Receiver receiver;	// receiver get message sent by other peers or admin
	private DBManager dbmanager;	// database manager
	
	/**
	 * Constructor: construct a node
	 * @param port: port number
	 */
	public Node(int port) {
		this.logger = new Logger(this);
		this.receiver = new Receiver(port, logger);
	}
	
	/**
	 * Start the service of the node
	 */
	public void start() {
		new Thread(receiver).start();
	}
	
	
	/**
	 * Get stage change. Recived the message sent by admin
	 * @param message
	 */
	public void wakeUp(String message) {
		System.out.println(identifier + " get wakeup " + message);
		logger.setCommand(message);
		logger.setdbHash(this.getHashroot());
		this.notification("PRE-PREPARE:" + message);
		this.pre_prepare(message);
	}
	
	/**
	 * Preprepare stage of the node. 
	 * @param command: message sent
	 */
	public void pre_prepare(String command) {
		System.out.println(identifier + " get pre-prepare ");
		this.state = State.PRE_PREPARE;
		this.notification("ACK:" + identifier + "," + dbmanager.getHashroot() + "," + command);
	}
	
	/**
	 * Prepare stage of the node
	 * @param command: the command needs to execute
	 */
	public void prepare(String command) {
		if(state != State.PRE_PREPARE) return;
		state = State.PREPARE;
		System.out.println(identifier + " agree with the command and ready for execute");
		dbmanager.operation(command); // do the operation
		logger.setCommitHash(dbmanager.getHashroot()); // set the hash to the commited root hash
		this.notification("COMMIT:" + identifier + "," + dbmanager.getHashroot());
	}
	
	/**
	 * Commit stage, nodes ready to give reply to the admin
	 * @param result
	 */
	public void commit(String result) {
		if(state != State.PREPARE) return;
		//state = state.COMMIT;
		Requester requester = new Requester("localhost", 9999);
		requester.sendMessage(this.identifier + " successfully commit:\n" + result);
	}
	
	/**
	 * Start connection to the data base
	 * @param user: user name
	 * @param password: password
	 * @param dbName: database name
	 * @param dbTable: data table
	 * @param sysurl: connection url to the database
	 */
	public void ConnectDB(String user, String password, String dbName, String dbTable, String sysurl) {
		dbmanager = new DBManager(user, password, dbName, dbTable, sysurl, logger);
	}
	
	/**
	 * Assign Id to the node
	 * @param identifier
	 */
	public void assignId(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Get id of the node
	 * @return id of the node
	 */
	public String getId() {
		return identifier;
	}
	
	/**
	 * Get the hash of the database
	 * @return hash of the database
	 */
	public String getHashroot() {
		return dbmanager.getHashroot();
	}
	
	/**
	 * Notify the peers the message got through requester
	 * @param message: String needs to be sent to peers
	 */
	public void notification(String message) {
		Iterator<Entry<String, Header> > itr = logger.getPeers();
		while(itr.hasNext()) {
			Entry<String, Header> node = itr.next();
			Requester requester = new Requester(node.getValue().getDns(), node.getValue().getPort());
			requester.sendMessage(message);
		}
	}
	
}
