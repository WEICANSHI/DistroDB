package distroDB;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 
 * @author canshi wei
 * Requester aim at sending message to a particular port and socket
 */
public class Requester {
	private String dns; 			// the dns of the client provided
	private int port;				// port number of the intended socket
	
	/**
	 * Constructor: Initialize a requester
	 * @param dns: the dns 
	 * @param port: the port number
	 */
	public Requester(String dns, int port) {
		this.port = port;
		this.dns = dns;
	}
	
	/**
	 * Send message to the dns and port number specify
	 * @param message: the message needs to be sent
	 */
	public void sendMessage(String message) {
		try {
			// create socket and accept connection
			Socket socket = new Socket(dns, port);
			// input stream, output stream define
			InputStream inStream = socket.getInputStream();
			OutputStream outStream = socket.getOutputStream();
			
			// initialize scanner, out put printer
			Scanner in = new Scanner(inStream, "UTF-8");
			//input = new Scanner(System.in);
			PrintWriter out = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
			out.println(message);	// send the message out
			
			// close the connection and writer
			socket.close();
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
