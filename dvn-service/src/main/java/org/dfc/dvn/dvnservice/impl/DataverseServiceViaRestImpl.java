/**
 * 
 */
package org.dfc.dvn.dvnservice.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.DataverseServiceException;
import org.dfc.dvn.dvnservice.domain.DataVerseConfig;
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

	@Override
	public void importStudyToDvn(String irodsFileAbsolutePath,
			InputStream fileInput) throws DataverseServiceException {

		log.info("importStudyToDvn()");

		if (irodsFileAbsolutePath == null || irodsFileAbsolutePath.isEmpty()) {
			throw new IllegalArgumentException(
					"null or empty irodsFileAbsolutePath");
		}

		if (fileInput == null) {
			throw new IllegalArgumentException("null or empty fileInput");
		}

		log.info("irodsFileAbsolutePath:{}", irodsFileAbsolutePath);
		BufferedInputStream bis = new BufferedInputStream(fileInput);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(dataVerseConfig.urlFromValues());

			// see
			// http://hc.apache.org/httpcomponents-client-4.3.x/httpmime/apidocs/
			InputStreamBody bin = new InputStreamBody(bis,
					ContentType.APPLICATION_OCTET_STREAM);

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
		} catch (ClientProtocolException e) {
			log.error("client protocol exception", e);
			throw new DataverseServiceException("exception in http transfer", e);
		} catch (IOException e) {
			log.error("io exception", e);
			throw new DataverseServiceException("ioexception in http transfer",
					e);
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.error("error closing http client logged and ignored", e);
			}
		}

	}
}
