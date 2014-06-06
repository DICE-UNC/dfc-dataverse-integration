/**
 * 
 */
package org.dfc.dvn.dvnservice;

import java.io.InputStream;

import org.dfc.dvn.dvnservice.domain.DataVerseTarget;

/**
 * Interface for basic services to manage metadata and studies in DataVerse
 * 
 * @author Mike Conway - DICE
 * 
 */
public interface DataverseService {

	/**
	 * 
	 * @param dataVerseTarget
	 *            {@link DataVerseTarget} representing the study and alias that
	 *            is the target of this deposition
	 * @param irodsFileAbsolutePath
	 * @param fileInput
	 *            <code>String</code> with the absolute path of the irods file
	 *            being transferred, as reflected in the input stream where the
	 *            collection resideds
	 * @throws DataverseServiceException
	 */
	void importStudyToDvn(DataVerseTarget dataVerseTarget,
			String irodsFileAbsolutePath, InputStream fileInput)
			throws DataverseServiceException;

}
