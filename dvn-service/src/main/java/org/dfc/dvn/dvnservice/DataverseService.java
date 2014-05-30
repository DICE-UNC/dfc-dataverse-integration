/**
 * 
 */
package org.dfc.dvn.dvnservice;

import org.dfc.dvn.dvnservice.domain.DataVerseTarget;

/**
 * Interface for basic services to manage metadata and studies in DataVerse
 * 
 * @author Mike Conway - DICE
 * 
 */
public interface DataverseService {

	/**
	 * Import a study into DataVerse TODO: should this be a collection turned
	 * into a zip? A zip deposited in iRODS?
	 * 
	 * @param dataVerseTarget
	 *            {@link DataVerseTarget} representing the study and alias that
	 *            is the target of this deposition
	 * @param irodsCollectionAbsolutePath
	 *            <code>String</code> with the absolute path on the target iRODS
	 *            where the collection resideds TODO: implies some sort of
	 *            context that contains the iRODS info?
	 * @throws DataverseServiceException
	 */
	void importStudyToDvn(final DataVerseTarget dataVerseTarget,
			final String irodsCollectionAbsolutePath)
			throws DataverseServiceException;

}
