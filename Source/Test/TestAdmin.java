package Test;

import java.util.Scanner;

import distroDB.Admin;

public class TestAdmin {
	public static void main(String args[]) {
		Admin admin = new Admin();
		admin.addNode("node1", "localhost", 1111);
		admin.addNode("node2", "localhost", 2222);
		admin.addNode("node3", "localhost", 3333);
		admin.addNode("node4", "localhost", 4444);
		
		try {
			Thread.sleep(2000);
		}catch(InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Deployment Ready");
		
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		while(true) {
			String command = input.nextLine().trim();
			admin.executeCommand(command);
		}
	}
}

//SELECT * FROM market_list
//INSERT INTO market_list (FMID, MarketName, Website) VALUES (110, "GOOD TO EAT", "www.goodeat.com")
//""