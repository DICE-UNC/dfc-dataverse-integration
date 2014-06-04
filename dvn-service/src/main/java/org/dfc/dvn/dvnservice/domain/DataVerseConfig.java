/**
 * 
 */
package org.dfc.dvn.dvnservice.domain;

/**
 * Configuration of a dataverse installation, including host/port info
 * 
 * @author Mike Conway - DICE
 * 
 */
public class DataVerseConfig {

	private String host = "";
	private String port = "";
	private boolean ssl = true;
	private String requestRoot = "";
	private String verb = "";
	private String alias = "";

	/**
	 * 
	 */
	public DataVerseConfig() {
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	public String getRequestRoot() {
		return requestRoot;
	}

	public void setRequestRoot(String requestRoot) {
		this.requestRoot = requestRoot;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return "DataVerseConfig [host=" + host + ", port=" + port + ", ssl="
				+ ssl + ", requestRoot=" + requestRoot + ", verb=" + verb
				+ ", alias=" + alias + "]";
	}

	/**
	 * Given the values in this object, return a formatted URL as a string
	 * 
	 * @return
	 */
	public String urlFromValues() {
		StringBuilder sb = new StringBuilder();

		if (ssl) {
			sb.append("https://");
		} else {
			sb.append("http://");
		}

		sb.append(host);
		if (!port.isEmpty()) {
			sb.append(":");
			sb.append(port);
		}

		sb.append("/");
		sb.append(requestRoot);
		sb.append("/");
		sb.append(verb);
		sb.append("/");
		sb.append("alias");
		return sb.toString();
	}
}
