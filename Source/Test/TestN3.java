package Test;

import distroDB.Node;

public class TestN3 {
	public static void main(String args[]) {
		Node node3 = new Node(3333);
		node3.start();
		node3.ConnectDB("root", "Yy994948350", "Market_db03", "Market_list", "jdbc:mysql://localhost:3306/");
	}
}

