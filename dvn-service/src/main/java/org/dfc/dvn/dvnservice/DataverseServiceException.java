/**
 * 
 */
package org.dfc.dvn.dvnservice;

/**
 * General exception in DataVerse Operations, consider richer subclasses
 * 
 * @author Mike Conway - DICE
 * 
 */
public class DataverseServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public DataverseServiceException() {
	}

	/**
	 * @param arg0
	 */
	public DataverseServiceException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public DataverseServiceException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DataverseServiceException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public DataverseServiceException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
