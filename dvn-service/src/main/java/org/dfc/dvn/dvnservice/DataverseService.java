/**
 * 
 */
package org.dfc.dvn.dvnservice;

import java.io.InputStream;

/**
 * Interface for basic services to manage metadata and studies in DataVerse
 * 
 * @author Mike Conway - DICE
 * 
 */
public interface DataverseService {

	/**
	 * @param irodsFileAbsolutePath
	 * @param fileInput
	 *            <code>String</code> with the absolute path of the irods file
	 *            being transferred, as reflected in the input stream where the
	 *            collection resideds
	 * @throws DataverseServiceException
	 */
	void importStudyToDvn(String irodsFileAbsolutePath, InputStream fileInput)
			throws DataverseServiceException;

}
