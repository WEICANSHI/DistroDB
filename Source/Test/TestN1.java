package Test;

import distroDB.Node;

public class TestN1 {
	public static void main(String args[]) {
		Node node1 = new Node(1111);
		node1.start();
		node1.ConnectDB("root", "Yy994948350", "Market_db01", "Market_list", "jdbc:mysql://localhost:3306/");
	}
}
