package org.dfc.dvn.irodstodvn;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dfc.dvn.dvnservice.DataverseService;
import org.dfc.dvn.dvnservice.domain.DataVerseConfig;
import org.dfc.dvn.dvnservice.impl.DataverseServiceViaRestImpl;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.JargonRuntimeException;
import org.irods.jargon.core.pub.IRODSFileSystem;

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

public class IrodsToDvnIndexer implements Indexer {

	IndexingService is;
	Scheduler scheduler;

	public static Log log = LogFactory.getLog(IrodsToDvnIndexer.class);
	private IRODSFileSystem irodsFileSystem;
	private IRODSAccount irodsAccount;

	public void setIndexingService(IndexingService is) {
		this.is = is;
	}

	public void startup() {
		is.regIndexer(this);
		try {
			irodsFileSystem = IRODSFileSystem.instance();
			irodsAccount = IRODSAccount.instance("dvndfc1.renci.org", 1247,
					"demo", "putpasswordhere", "", "g1", "");
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

					DataVerseConfig dataVerseConfig = new DataVerseConfig();
					dataVerseConfig.setHost("host");
					dataVerseConfig.setPort("post");
					dataVerseConfig
							.setRequestRoot("/dvn/api/data-deposit/v1/swordv2/edit-media/study/");
					dataVerseConfig.setSsl(true);
					dataVerseConfig.setStudyId("");
					dataVerseConfig.setVerb("hdl:TEST/ODUM-IRODS_10010");

					DataverseService dataverseService = new DataverseServiceViaRestImpl(
							dataVerseConfig);

					InputStream inputStream = new BufferedInputStream(
							irodsFileSystem.getIRODSFileFactory(irodsAccount)
									.instanceIRODSFileInputStream(
											dataObject.getLabel()));

					dataverseService.importStudyToDvn(dataObject.getLabel(),
							inputStream);

					inputStream.close();
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