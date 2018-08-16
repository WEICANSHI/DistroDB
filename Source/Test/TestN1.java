package Test;

import distroDB.Node;

public class TestN1 {
	public static void main(String args[]) {
		Node node1 = new Node(1111);
		node1.start();
		node1.ConnectDB("username", "password", "database name", "table name", "url to database");
	}
}
