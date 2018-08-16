package Test;

import distroDB.Node;

public class TestN4 {
	public static void main(String args[]) {
		Node node4 = new Node(4444);
		node4.start();
		node4.ConnectDB("username", "password", "database name", "table name", "url to database");
	}
}
