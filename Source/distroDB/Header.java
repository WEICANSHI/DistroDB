package distroDB;

/**
 * @author canshi wei
 * Use to record the dns and port number of a certain node
 */
public class Header {
	private int port;	// socket port number use be certain node
	private String dns;	// dns of certain node
	
	/**
	 * Constructor: for creating the Header Object
	 * @param dns: dns of certain node
	 * @param port: port number of certain node
	 */
	public Header(String dns, int port) {
		this.dns = dns;
		this.port = port;
	}
	
	/**
	 * Get the port number contained in the header
	 * @return port number
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Get the dns contained in the header
	 * @return dns
	 */
	public String getDns() {
		return dns;
	}
}
