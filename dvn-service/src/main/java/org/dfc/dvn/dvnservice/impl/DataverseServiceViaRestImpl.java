/**
 * 
 */
package org.dfc.dvn.dvnservice.impl;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.DataverseServiceException;
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
			HttpPost httppost = new HttpPost("http://localhost:8080"
					+ "/servlets-examples/servlet/RequestInfoExample");

			FileBody bin = new FileBody(new File(args[0]));
			StringBody comment = new StringBody("A binary file of some kind",
					ContentType.TEXT_PLAIN);

			HttpEntity reqEntity = MultipartEntityBuilder.create()
					.addPart("bin", bin).addPart("comment", comment).build();

			httppost.setEntity(reqEntity);
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					System.out.println("Response content length: "
							+ resEntity.getContentLength());
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
