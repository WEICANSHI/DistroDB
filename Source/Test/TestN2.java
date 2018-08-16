package Test;

import distroDB.Node;

public class TestN2 {
	public static void main(String args[]) {
		Node node2 = new Node(2222);
		node2.start();
		node2.ConnectDB("username", "password", "database name", "table name", "url to database");
	}
}
