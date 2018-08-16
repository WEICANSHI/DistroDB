package DatabaseDemo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Database {
	public static final String dbName = "Market_db04";
	public static final String dbTable = "Market_list";
	public static final String sysurl = "jdbc:mysql://localhost:3306/";
	public static String filename = "./data/Export.csv";
	public static String user = "";
	public static String password = "";
	public static Connection con;
	
	public static void Connect() {
		String url = sysurl + dbName + "?user=" + user + "&password=" + password;
		try {
			con = DriverManager.getConnection(url);
			System.out.println("Connection suscessfully");
		} catch (SQLException e) {
			if(e.getMessage().indexOf("Unknown database") != -1) {
				System.out.println("Initializing database, this might take a minute");
				Database.createDb();
				Database.Connect();
				Database.createTable();
				System.out.println("Initializing done!");
				return;
			}else {
				// print the error info
				for(Throwable t: e) {
					t.printStackTrace();
				}
				throw new RuntimeException("connection fail");
			}
		}
	}
	
	
	public static void createDb(){
		String url = sysurl + "?user=" + user + "&password=" + password;
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			String sql = "CREATE DATABASE " + Database.dbName;
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createTable() {
		try {
			DataParser.readData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createHeader() {
		String sql = "CREATE TABLE " + Database.dbTable + "( ";
		sql += DataParser.indices.get(0) + " INT UNSIGNED AUTO_INCREMENT, ";
		for(int i = 1; i < DataParser.indices.size(); i++) {
			sql += DataParser.indices.get(i) + " VARCHAR(200), ";
		}
		sql += "PRIMARY KEY (" + DataParser.indices.get(0) + ") )";
		
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void insert(List<String> data) {
		try {
			PreparedStatement ptmt = con.prepareStatement(DataParser.INSERT);
			ptmt.setInt(1, Integer.parseInt(data.get(0)));
			for(int i = 1; i < data.size(); i++) {
				ptmt.setString(i + 1, data.get(i));
			}
			ptmt.execute();
		} catch (SQLException e) {
//			for(int i = 1; i < data.size(); i++) {
//				System.out.println(data.get(i));
//			}
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Formate the data get by query into string
	 * @param rs: result from query
	 * @param rsmd: metadata result from query
	 * @return String of the formated data
	 * @throws SQLException
	 */
	private static String formatResult(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
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
	
	public static void operation(String command) {
		if(command.indexOf("INSERT") != -1) {
			try {
				Statement stmt = con.createStatement();
				stmt.executeUpdate(command);
				int quote = command.indexOf("(");
				int endfirstval = command.indexOf(",");
				if(endfirstval == -1) {
					endfirstval = command.indexOf(")");
				}
				String title = command.substring(quote + 1, endfirstval).trim();
				
				command = command.substring(command.indexOf(")") + 1).trim();
				int nextquote = command.indexOf("(");
				int nextendfirstval = command.indexOf(",");
				if(nextendfirstval == -1) {
					nextendfirstval = command.indexOf(")");
				}
				String value = command.substring(nextquote + 1, nextendfirstval).trim();
				
				String query = "SELECT * FROM " + dbTable + " WHERE " + title + "=" + value;
				ResultSet rs = stmt.executeQuery(query);
				if(rs.next()) {
					ResultSetMetaData rsmd = rs.getMetaData();	// get metadata
					String data = formatResult(rs, rsmd);	// using metadata format the result into string
					//root = root.add(data, root);
					System.out.println(data);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
		}else if(command.indexOf("SELECT") != -1) {
			try {
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(command);
				while(rs.next()) {
					ResultSetMetaData rsmd = rs.getMetaData();	// get metadata
					String data = formatResult(rs, rsmd);	// using metadata format the result into string
					System.out.println(data);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		Database.Connect();
		Database.operation("INSERT INTO Market_list (FMID, MarketName, Website) VALUES (110, \"GOOD TO EAT\", \"www.goodeat.com\")");
	}
}
