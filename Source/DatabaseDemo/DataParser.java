package DatabaseDemo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
	public static List<String> indices = new ArrayList<>();
	public static String INSERT = "INSERT INTO " + Database.dbTable + " (";
	
	public static void readData() throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(Database.filename));
		String line = reader.readLine(); // read the first line of index
		int indices_limit = 2;
		while(line.indexOf(",") != -1 && indices_limit >= 0) {
			int split = line.indexOf(",");
			String index = line.substring(0, split).trim();
			indices.add(index);
			if(indices_limit != 0)
				INSERT += index + ",";
			else
				INSERT += index;
			line = line.substring(split + 1).trim();
			indices_limit --;
		}
		//indices.add(line.trim());
		//INSERT += line.trim() + ") values(";
		INSERT += ") values(";
		for(int i = 0; i < indices.size() - 1; i++) {
			INSERT += "?,";
		}
		INSERT += "?)";
		System.out.println("HERE: " + INSERT);
		Database.createHeader();
		
		int counter = 0;
		while((line = reader.readLine()) != null && counter < 10){
			List<String> dataCollection = new ArrayList<>();
			indices_limit = 2;
			while(line.indexOf(",") != -1 && indices_limit >= 0) {
				int startQuote = line.indexOf("\"");
				int split = line.indexOf(",");
				String data; // data predeclare
				// there is a quote before commom
				if(startQuote != -1 && startQuote < split) {
					// check the next quote
					int endQuote = line.indexOf("\"", startQuote + 1);
					while(line.charAt(endQuote + 1) != ',') {
						endQuote = line.indexOf("\"", endQuote + 1);
					}
					if(endQuote == -1) {
						System.out.println("ERROR!!!");
						System.out.println(dataCollection.get(0));
					}
					data = line.substring(startQuote + 1, endQuote).trim();
					line = line.substring(endQuote + 2).trim();
				}else {
					data = line.substring(0, split).trim();
					line = line.substring(split + 1).trim();
				}
				dataCollection.add(data);
				System.out.println(data);
				indices_limit --;
			}
			//dataCollection.add(line.trim());
			Database.insert(dataCollection);
			counter ++;
		}
		reader.close();
	}
}
