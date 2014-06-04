/**
 * 
 */
package org.dfc.dvn.dvnservice.impl;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.DataverseServiceException;
import org.dfc.dvn.dvnservice.domain.DataVerseConfig;
import org.dfc.dvn.dvnservice.domain.DataVerseTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST based implementation of basic Dataverse operations via the exposed
 * Dataverse REST API
 * 
 * @author Mike Conway - DICE
 * 
 */
public class DataverseServiceViaRestImpl implements DataverseService {

	public static final Logger log = LoggerFactory
			.getLogger(DataverseServiceViaRestImpl.class);

	private final DataVerseConfig dataVerseConfig;

	/**
	 * 
	 */
	public DataverseServiceViaRestImpl(final DataVerseConfig dataVerseConfig) {
		if (dataVerseConfig == null) {
			throw new IllegalArgumentException("null dataVerseConfig");
		}

		this.dataVerseConfig = dataVerseConfig;
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

		log.info("importStudyToDvn()");

		if (dataVerseTarget == null) {
			throw new IllegalArgumentException("null dataVerseTarget");
		}

		if (irodsCollectionAbsolutePath == null
				|| irodsCollectionAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException(
					"null or empty irodsCollectionAbsolutePath");
		}

		log.info("dataVerseTarget:{}", dataVerseTarget);
		log.info("irodsCollectionAbsolutePath:{}", irodsCollectionAbsolutePath);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(dataVerseConfig.urlFromValues());
			InputStream is = 
			// see
			// http://hc.apache.org/httpcomponents-client-4.3.x/httpmime/apidocs/
			InputStreamBody bin = new InputStreamBody(new File(args[0]));

			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("bin", bin).build();

			httppost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {

				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					log.info("Response content length:{}",
							resEntity.getContentLength());
				}
				EntityUtils.consume(resEntity);
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}

		// step 2 blah

	}
}
