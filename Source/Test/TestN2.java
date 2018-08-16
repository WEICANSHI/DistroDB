package Test;

import distroDB.Node;

public class TestN2 {
	public static void main(String args[]) {
		Node node2 = new Node(2222);
		node2.start();
		node2.ConnectDB("root", "Yy994948350", "Market_db02", "Market_list", "jdbc:mysql://localhost:3306/");
	}
}
