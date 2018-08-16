package Test;

import distroDB.Node;

public class TestN4 {
	public static void main(String args[]) {
		Node node3 = new Node(4444);
		node3.start();
		node3.ConnectDB("root", "Yy994948350", "Market_db04", "Market_list", "jdbc:mysql://localhost:3306/");
	}
}
