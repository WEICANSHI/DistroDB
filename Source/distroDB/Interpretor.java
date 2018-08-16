package distroDB;


/**
 * @author canshi wei
 * Interpretor: interprate the input command for DBManager, and inform DBManager 
 * which execution should be called
 *
 */
public class Interpretor {
	private DBManager dbmanager;
	
	/**
	 * Constructor: constructor for the interpretor
	 * Communicate with dbmanager in software level
	 * @param dbmanager
	 */
	public Interpretor(DBManager dbmanager) {
		this.dbmanager = dbmanager;
	}
	
	/**
	 * Parse the command and call dbmanager to execute corrsponding
	 * command
	 * @param command: command needs to be interprated
	 */
	public void interprate(String command) {
		if(command.indexOf("INSERT") != -1) {
			String buffer = command;
			int quote = command.indexOf("(");
			int endfirstval = command.indexOf(",");
			// check if there the comma is quoted
			int check = command.indexOf("\"");
			if(check == -1) {
				check = command.indexOf("\'");
			}
			int endcheck = command.indexOf("\"", check + 1);
			if(endcheck == -1) {
				endcheck = command.indexOf("\'", check + 1);
			}
			// if the comma is quoted
			if(check != -1 && endfirstval != -1 && endfirstval > check) {
				command = command.substring(check + 1);
				check = command.indexOf("\"");
				if(check == -1) {
					check = command.indexOf("\"");
				}
				endfirstval = check;
				assert(command.substring(endfirstval, endfirstval + 1).equals("\"") ||
						command.substring(endfirstval, endfirstval + 1).equals("\'") );	
			}
				
			if(endfirstval == -1) {
				endfirstval = command.indexOf(")");
			}
				
			String title = command.substring(quote + 1, endfirstval).trim();
			command = command.substring(command.indexOf(")") + 1).trim();
			int nextquote = command.indexOf("(");
			int nextendfirstval = command.indexOf(",");
			if(nextendfirstval == -1) {
				nextendfirstval = command.indexOf(")");
			}
			String value = command.substring(nextquote + 1, nextendfirstval).trim();
			dbmanager.insert(buffer, title, value);
			
			}else if(command.indexOf("DELETE") != -1) {
				int where = command.indexOf("WHERE");
				String constrain = command.substring(where + "WHERE".length()).trim();
				int split = constrain.indexOf("=");
				//String idname = constrain.substring(0, split);
				String id = constrain.substring(split + 1);
				dbmanager.delete(command, id);
			}
			else if(command.indexOf("SELECT") != -1) {
				dbmanager.Query(command);
			}
	}
}
