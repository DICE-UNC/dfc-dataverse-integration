package org.dfc.dvn.irodstodvn;


import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.fasterxml.jackson.databind.*;
import databook.listener.*;
import databook.listener.Scheduler.Continuation;
import databook.listener.Scheduler.Job;
import databook.listener.service.IndexingService;
import databook.persistence.rule.rdf.ruleset.*;


public class IrodsToDvnIndexer  implements Indexer {

	IndexingService is;
	Scheduler scheduler;
	
	public static Log log = LogFactory.getLog(IrodsToDvnIndexer.class);

	public void setIndexingService(IndexingService is) {
		this.is = is;
	}

    private String getOSVersion() {	
		String[] cmd = {
			"lsb_release", 
			"-id"
		};

		String ret = "";
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader bri = new BufferedReader(new InputStreamReader(
				    p.getInputStream()));

			String line = "";
			while ((line = bri.readLine()) != null) {
				ret += line;
			}
		} catch (IOException e) {

			log.error("error", e);
		}

		return ret;

	}

	public void startup() {
		is.regIndexer(this);

	}

	public void shutdown() {
		is.unregIndexer(this);
	}

	

	private void fulltext(DataObject o, final String id) {

		if(o.getLabel().endsWith(".txt")) {
			System.out.println("full text");
			Message msg= new Message();
			msg.setOperation("retrieve");
			ArrayList<DataEntity> list = new ArrayList<DataEntity>();
			list.add(o);
			msg.setHasPart(list);
			scheduler.submit(new Job<Reader>(this, msg, new Continuation<Reader>() {

				@Override
				public void call(Reader data) {
					try{
					Reader is = data;
					
					is.close();
					
					}catch(Exception e) {
						log.error("error", e);
					}
				}
			}, new Continuation<Throwable>() {

				@Override
				public void call(Throwable data) {
					log.error("error", data);
				}
			}));
		}

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