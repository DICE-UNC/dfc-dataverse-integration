package org.dfc.dvn.dvntoirods;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.utils.CollectionAndPath;
import org.irods.jargon.core.utils.MiscIRODSUtils;

import com.fasterxml.jackson.databind.ObjectMapper;  
   

import databook.listener.Indexer; 
import databook.listener.Scheduler;
import databook.listener.Scheduler.Continuation;
import databook.listener.Scheduler.Job;
import databook.listener.service.IndexingService;
import databook.persistence.rule.rdf.ruleset.DataEntity;
import databook.persistence.rule.rdf.ruleset.DataObject;
import databook.persistence.rule.rdf.ruleset.Message;
import databook.persistence.rule.rdf.ruleset.Messages;

public class DvnToIrodsIndexer implements Indexer {

	IndexingService is;
	Scheduler scheduler;  

	public static Log log = LogFactory.getLog(DvnToIrodsIndexer.class);
	private IRODSFileSystem irodsFileSystem;
	private IRODSAccount dvnIrodsAccount;
	private IRODSAccount dfcIrodsAccount;
	private final String COLL_IN_DFC = "/dfcmain/home/xxx/dataversedemo";

	public void setIndexingService(IndexingService is) {
		this.is = is;
	}

	public void startup() {
		is.regIndexer(this);
		try {
			irodsFileSystem = IRODSFileSystem.instance();
			dvnIrodsAccount = IRODSAccount.instance("dvndfc1.renci.org", 1247,
					"demo", "xxxx", "", "g1", "");
			dfcIrodsAccount = IRODSAccount.instance("iren2.renci.org", 1237, "user", "password", "", "dfcMain", "");
			// FIXME: hard code for now
		} catch (JargonException e) {
			log.error("unable to get IRODSFileSystem");
			throw new JargonRuntimeException("unable to get IRODSFileSystem", e);
		}
	}

	public void shutdown() {
		is.unregIndexer(this);
		irodsFileSystem.closeAndEatExceptions();
	}

	private void handleDataObjectCreate(final DataObject dataObject) {

		if (dataObject == null) {
			throw new IllegalArgumentException("null dataObject");
		}

		String absPath = dataObject.getLabel();
		log.info("absPath:" + absPath);
		Message msg = new Message();
		msg.setOperation("retrieve");
		ArrayList<DataEntity> list = new ArrayList<DataEntity>();
		list.add(dataObject);
		msg.setHasPart(list);
		scheduler.submit(new Job<Reader>(this, msg, new Continuation<Reader>() {

			@Override
			public void call(Reader data) {
				try {

					log.info("have reader for file...in call()");
					log.info("for path:" + dataObject.getLabel());

					// ignore the reader
					Reader is = data;
					is.close();

					log.info("getting input stream from file at path:" + dataObject.getLabel());
					
					InputStream inputStream = new BufferedInputStream(irodsFileSystem.getIRODSFileFactory(dvnIrodsAccount).instanceIRODSFileInputStream(dataObject.getLabel()));
					StringBuilder sb = new StringBuilder();
					sb.append("/");
					sb.append("dfcdemo-");
					sb.append(System.currentTimeMillis());
					
					String collName = sb.toString();
					
					IRODSFile collFile = irodsFileSystem.getIRODSFileFactory(dfcIrodsAccount).instanceIRODSFile(collName);
					collFile.mkdirs();
					
					sb.append("/");
					CollectionAndPath cap = MiscIRODSUtils
							.separateCollectionAndPathFromGivenAbsolutePath(dataObject.getLabel());

					sb.append(cap.getChildName());
					String targetFileAbsPath = sb.toString();
					log.info("getting output stream which will be in a file:" + targetFileAbsPath);
					
					OutputStream outputStream = new BufferedOutputStream(irodsFileSystem.getIRODSFileFactory(dfcIrodsAccount).instanceIRODSFileOutputStream(targetFileAbsPath));
					
					
					log.info("copying...");
					IOUtils.copy(inputStream, outputStream);
					log.info("import op completed");

					irodsFileSystem.closeAndEatExceptions();

				} catch (Exception e) {
					log.error("error", e);
					throw new JargonRuntimeException("exception in indexer", e);
				}
			}
		}, new Continuation<Throwable>() {

			@Override
			public void call(Throwable data) {
				log.error("error", data);
			}
		}));

	}

	@Override
	public void messages(Messages ms) {
		try {
			// System.out.println("messages received " + ms);
			ObjectMapper om = new ObjectMapper();
			// om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

			for (Message m : ms.getMessages()) {
				if (m.getOperation().equals("create")) {
					for (DataEntity o : m.getHasPart()) {
						if (o instanceof DataObject) {
							log.info("Got a create in dvn for:" + o.getLabel());
							log.info("from message:" + ms);
							handleDataObjectCreate((DataObject) o);
						}
					}
				}

			}
		} catch (Exception e) {
			log.error("error", e);
		}
	}

	@Override
	public void setScheduler(Scheduler s) {
		this.scheduler = s;
	}

}
