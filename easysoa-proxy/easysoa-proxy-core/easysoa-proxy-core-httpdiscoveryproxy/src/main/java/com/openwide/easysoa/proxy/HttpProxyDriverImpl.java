package com.openwide.easysoa.proxy;

import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.run.RunManager;

/**
 * HttpProxyDriver implementation
 * @author jguillemotte
 *
 */
@Scope("composite")
public class HttpProxyDriverImpl implements HttpProxyDriver {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(HttpProxyDriverImpl.class.getName());
	
	/**
	 * 	Reference on monitoring service 
	 */
	@Reference
	RunManager runManager;
	//MonitoringService monitoringService;
	
	static {
		ProxyConfigurator.configure();
	}	
	
	@Override
	public String returnUseInformations(UriInfo ui) {
		logger.debug("Returning help informations");
		StringBuffer help = new StringBuffer();
		help.append("<HTML><HEAD><TITLE>");
		help.append("HTTP Proxy Driver commands");
		help.append("</TITLE></HEAD><BODY>");
		help.append("<P><H1>HTTP Proxy Driver</H1></P>");
		help.append("<P><H2>How to use :</H2></P>");
		help.append("<P><UL>");
		help.append("<LI>To start a new proxy run : /startNewRun/{runName}</LI>");
		help.append("<LI>To stop the current proxy run : /stopCurrentRun</LI>");
		help.append("<LI>To get the run list : /getOrderedRunNames</LI>");
		help.append("<LI>To re-run a run : /reRun/{runName}</LI>");
		help.append("</UL></P></BODY></HTML>");
		return help.toString();
	}

	@Override
	public String startNewRun(String runName) {
		logger.debug("Starting a new run !");
		try{
			runManager.start(runName);
		} catch(Exception ex){
			logger.error("Unable to start a new run", ex);
			return ex.getMessage();
		}
		return "Run '" + runName + "' started !";
	}

	@Override
	public String stopCurrentRun() {
		logger.debug("Stopping the current run !");
		runManager.stop();
		runManager.getMonitoringService().registerDetectedServicesToNuxeo();
		return "Current run stopped !";
	}

	@Override
	@Deprecated
	// TODO Modify this method to change the monitoring mode
	public String setMonitoringMode(String mode) {
		logger.debug("Set monitoring mode !");
		try {
			if(mode == null || "".equals(mode)){
				throw new IllegalArgumentException("Monitoring mode must not be null or empty");
			}
			// Best method is to stop the http Proxy with FraSCAti, and then restart with the good composite file
		} catch(Exception ex){
			logger.error("Unable to set monitoring mode", ex);
			return ex.getMessage();
		}
		return "Monitoring mode set";
	}

	@Override
	public String getOrderedRunNames() {
		StringBuffer result = new StringBuffer();
		int i = 1;
		for(String runName: runManager.getOrderedRunNames()){
			result.append("Run number " + i + ": ");
			result.append(runName);
			result.append("; ");
			i++;
		}
		return result.toString();
	}

	@Override
	public String reRun(String runName) {
		try{
			runManager.reRun(runName);
		}
		catch(Exception ex){
			logger.error("Unable to re-run the run '" + runName + "'", ex);
			return ex.getMessage();	
		}
		return "Re-run done";
	}

	@Override
	public String deleteRun(String runName) {
		try {
			runManager.deleteRun(runName);
		}
		catch(Exception ex){
			logger.error("Unable to delete the run '" + runName + "'", ex);
			return ex.getMessage();			
		}
		return "Run deleted ";		
	}

}
