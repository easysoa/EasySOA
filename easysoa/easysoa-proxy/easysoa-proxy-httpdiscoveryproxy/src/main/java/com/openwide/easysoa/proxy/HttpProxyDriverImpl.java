package com.openwide.easysoa.proxy;

import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;
import com.openwide.easysoa.monitoring.MonitoringService;

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
	MonitoringService monitoringService;
	
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
		help.append("</UL></P></BODY></HTML>");
		return help.toString();
	}

	@Override
	public String startNewRun(String runName) {
		logger.debug("Starting a new run !");
		try{
			monitoringService.getRunManager().start(runName);
		} catch(Exception ex){
			logger.error("Unable to start a new run", ex);
			return ex.getMessage();			
		}
		return "Run '" + runName + "' started !";
	}

	@Override
	public String stopCurrentRun() {
		logger.debug("Stopping the current run !");
		monitoringService.getRunManager().stop();
		monitoringService.registerDetectedServicesToNuxeo();
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
			//DiscoveryMonitoringService.getMonitorService(MonitoringMode.valueOf(mode.toUpperCase()));
			// Best method is to stop the http Proxy with FraSCAti, and then restart with the good composite file
		} catch(Exception ex){
			logger.error("Unable to set monitoring mode", ex);
			return ex.getMessage();			
		}		
		return "Monitoring mode set";
	}

}
