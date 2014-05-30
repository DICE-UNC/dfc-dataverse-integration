/**
 * 
 */
package org.dfc.dvn.dvnservice.domain;

/**
 * Represents a DataVerse target and study
 * 
 * @author Mike Conway - DICE
 * 
 */
public class DataVerseTarget {

	private String title = "";
	private String id = "";
	private String dvnAlias = "";

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}

	public String getDvnAlias() {
		return dvnAlias;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDvnAlias(String dvnAlias) {
		this.dvnAlias = dvnAlias;
	}

}
