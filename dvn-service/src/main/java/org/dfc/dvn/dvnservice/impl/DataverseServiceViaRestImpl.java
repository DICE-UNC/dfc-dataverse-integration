/**
 * 
 */
package org.dfc.dvn.dvnservice.impl;

import static org.apache.http.HttpHeaders.USER_AGENT;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.DataverseServiceException;
import org.dfc.dvn.dvnservice.domain.DataVerseConfig;
import org.irods.jargon.core.utils.CollectionAndPath;
import org.irods.jargon.core.utils.MiscIRODSUtils;
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
		CollectionAndPath cap = MiscIRODSUtils
				.separateCollectionAndPathFromGivenAbsolutePath(irodsFileAbsolutePath);

		CloseableHttpClient httpclient = null; // HttpClients.createDefault();

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(dataVerseConfig.getHost(),
				443),
				new UsernamePasswordCredentials(dataVerseConfig.getUserName(),
						dataVerseConfig.getPassword()));

		httpclient = getCloseableHttpClient(credsProvider);

		try {
			HttpPost httppost = new HttpPost(dataVerseConfig.urlFromValues());

			log.info("url is:{}", dataVerseConfig.urlFromValues());

			// see
			// http://hc.apache.org/httpcomponents-client-4.3.x/httpmime/apidocs/
			InputStreamBody bin = new InputStreamBody(bis,
					ContentType.create("application/zip"), cap.getChildName());

			MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
			reqEntity.addPart("file",
					new InputStreamBody(bis, cap.getChildName()));

			// BufferedHttpEntity bufReqEntity = new
			// BufferedHttpEntity(reqEntity);

			httppost.setEntity(reqEntity.build());
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

	static CloseableHttpClient getCloseableHttpClient(
			CredentialsProvider credsProvider) {
		CloseableHttpClient httpclient = null;
		try {

			SSLContext sslcontext = SSLContexts.custom()
					.loadTrustMaterial(null, new TrustStrategy() {
						@Override
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();

			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslcontext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
					.setUserAgent(USER_AGENT)
					.setDefaultCredentialsProvider(credsProvider).build();

			return httpclient;

		} catch (KeyStoreException ex) {
			log.error("KeyStoreException", ex);

		} catch (NoSuchAlgorithmException ex) {
			log.error("NoSuchAlgorithmException", ex);

		} catch (KeyManagementException ex) {
			log.error("KeyManagementException", ex);

		}
		return httpclient;
	}

}
