package com.openwide.easysoa.monitoring;

import java.util.ArrayDeque;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Scope;

/**
 * Monitoring service for Validated mode
 * @author jguillemotte
 *
 */
@Scope("composite")
public class ValidatedMonitoringService extends AbstractMonitoringService {

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(ValidatedMonitoringService.class.getName());	
	
	public ValidatedMonitoringService(){
		// init & fill it from Nuxeo
		logger.debug("Mode = VALIDATED !!");
		unknownMessagesList = new ArrayDeque<Message>();
		monitoringModel = new MonitoringModel();
		monitoringModel.fetchFromNuxeo();
		logger.debug("Validated mode : Printing monitoring model keyset");
		for (String url : monitoringModel.getSoaModelUrlToTypeMap().keySet()) {
			logger.debug("url = " + url + ", value = " + monitoringModel.getSoaModelUrlToTypeMap().get(url));
		}
		Iterator<String> urlIter = monitoringModel.getSoaModelUrlToTypeMap().keySet().iterator();
		String url;
		while(urlIter.hasNext()){
			url = urlIter.next();
			logger.debug("url = " + url + ", value = " + monitoringModel.getSoaModelUrlToTypeMap().get(url));
		}
		urlTree = null;
	}
	
	/**
	 * Return the monitoring mode
	 * @return <code>MonitoringMode</code>
	 */
	public MonitoringMode getMode(){
		return MonitoringMode.VALIDATED;
	}	

	@Override
	public void registerUnknownMessagesToNuxeo() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerDetectedServicesToNuxeo() {
		// TODO Auto-generated method stub

	}

}
