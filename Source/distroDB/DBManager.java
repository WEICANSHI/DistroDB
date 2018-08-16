package distroDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author canshi wei
 * DBManager start a connection with MYSQL data base
 */
public class DBManager {
	private MerkleTree root;	// the hash of root
	private String dbName;		// name of handling database
	private String dbTable;		// name of handling table
	private String sysurl;		// url connection to the data base
	private String user;		// user name of the data base
	private String password;	// password
	private Interpretor interpretor;	// Interperat the message
	private Connection con;		// connection object to the database
	private Logger logger;		// storage for node. Record the stage data
	
	/**
	 * Constructor: Initialize the connection to the data base
	 * @param user: the user name
	 * @param password: user password
	 * @param dbName: database name
	 * @param dbTable: database table
	 * @param sysurl: connection url to the database
	 */
	public DBManager(String user, String password, String dbName, String dbTable, String sysurl, Logger logger) {
		this.user = user;
		this.password = password;
		this.dbName = dbName;
		this.dbTable = dbTable;
		this.sysurl = sysurl;
		this.logger = logger;
		this.Connect();				// make connection to the database
		this.merkleTreeCreator();	// create the merkle tree
		this.interpretor = new Interpretor(this);
	}
	
	/**
	 * Connect the data base
	 */
	public void Connect() {
		// formate the connection post url
		String url = sysurl + dbName + "?user=" + user + "&password=" + password;
		try {
			con = DriverManager.getConnection(url);	// get url connection
			System.out.println("Connection suscessfully");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Query the database
	 * @param command: the command for manipulate the database
	 */
	public void Query(String command) {
		try {
			// start the connection
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			String ret = "";
			// read the query result
			while(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();	// get metadata
				String data = this.formatResult(rs, rsmd);	// using metadata format the result into string
				ret += data + "\n";
			}
			// log the result to the logger
			logger.setResult(ret);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Inser a row to the table
	 * @param command: command manipulate the database
	 * @param id: the id of the inserted row
	 * @param value: primary key for the inserted row
	 */
	public void insert(String command, String id, String value) {
		try{
			// start connection to the database
			Statement stmt = con.createStatement();
			System.out.println(command);
			stmt.executeUpdate(command);
			String query = "SELECT * FROM " + dbTable + " WHERE " + id + "=" + value;
			System.out.println(query);
			// get the query result
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();	// get metadata
				String data = this.formatResult(rs, rsmd);	// using metadata format the result into string
				root = root.add(data, id, root); // add the newly transaction to the merkle tree
				logger.setResult("Successfully insert data: " + data);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Delete a certain row from the data base
	 * @param command
	 * @param id
	 */
	public void delete(String command, String id) {
		try{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(command);
			root = root.delete(id, root); // delete the data from the merkle tree
			logger.setResult("Successfully delete. Current Hashroot: " + root.getSha2HexValue());
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void operation(String command) {
		interpretor.interprate(command);
	}
	
	/**
	 * Create the merkle tree of the database
	 */
	public void merkleTreeCreator() {
		String query = "SELECT * FROM " + dbTable;	// Query the database
		try {
			Statement stmt = this.con.createStatement();
			ResultSet rs = stmt.executeQuery(query);	// make query to the database and get the result
			// loop through the result
			while(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();	// get metadata
				String data = this.formatResult(rs, rsmd);	// using metadata format the result into string
				String id = rs.getString(1);	// get the id from data
				System.out.println("creating id: " + id);
				// if the merkle tree haven't be initialized
				if(root == null) 
					root = new MerkleTree(data);	// initialize the tree
				else 
					root = root.add(data, id,  root);	// add the data to the tree
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Merkle tree created success");
	}
	
	/**
	 * Formate the data get by query into string
	 * @param rs: result from query
	 * @param rsmd: metadata result from query
	 * @return String of the formated data
	 * @throws SQLException
	 */
	private String formatResult(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		int columnsNumber = rsmd.getColumnCount();	// number of column
		StringBuffer result = new StringBuffer("");	// record the result
		// loop through each column and load in to string buffer
		for(int i = 1; i < columnsNumber; i++) {
			result.append(rsmd.getColumnName(i) + ":");
			result.append(rs.getString(i) + "&");
		}
		// add the last column to the buffer
		result.append(rsmd.getColumnName(columnsNumber) + ":");
		result.append(rs.getString(columnsNumber));
		
		// return the buffer as String 
		return result.toString();
	}
	

	
	/**
	 * Get the hash of the database
	 * @return hash of the database
	 */
	public String getHashroot() {
		return root.getSha2HexValue();
	}
	
	
}
