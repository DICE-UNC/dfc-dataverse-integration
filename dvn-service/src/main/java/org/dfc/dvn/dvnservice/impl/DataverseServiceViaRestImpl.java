/**
 * 
 */
package org.dfc.dvn.dvnservice.impl;

import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.DataverseServiceException;
import org.dfc.dvn.dvnservice.domain.DataVerseTarget;

/**
 * REST based implementation of basic Dataverse operations via the exposed
 * Dataverse REST API
 * 
 * @author Mike Conway - DICE
 * 
 */
public class DataverseServiceViaRestImpl implements DataverseService {

	/**
	 * 
	 */
	public DataverseServiceViaRestImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.dfc.dvn.dvnservice.DataverseService#importStudyToDvn(org.dfc.dvn.
	 * dvnservice.domain.DataVerseTarget, java.lang.String)
	 */
	@Override
	public void importStudyToDvn(DataVerseTarget dataVerseTarget,
			String irodsCollectionAbsolutePath)
			throws DataverseServiceException {

		// step 1 blah

		// step 2 blah

	}

}
