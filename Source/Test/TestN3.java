package Test;

import distroDB.Node;

public class TestN3 {
	public static void main(String args[]) {
		Node node3 = new Node(3333);
		node3.start();
		node3.ConnectDB("username", "password", "database name", "table name", "url to database");
	}
}

