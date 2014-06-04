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

	public String getTitle() {
		return title;
	}

	public String getId() {
		return id;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "DataVerseTarget [title=" + title + ", id=" + id + "]";
	}

}
