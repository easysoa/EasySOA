package com.openwide.easysoa.esperpoc;

import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

import com.openwide.easysoa.esperpoc.run.RunManagerImpl;
import com.openwide.easysoa.monitoring.MonitorService;
import com.openwide.easysoa.monitoring.MonitorService.MonitoringMode;

/**
 * HttpProxyDriver implementation
 * @author jguillemotte
 *
 */
public class HttpProxyDriverImpl implements HttpProxyDriver {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(HttpProxyDriverImpl.class.getName());
	
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
			RunManagerImpl.getInstance().start(runName);
		} catch(Exception ex){
			logger.error("Unable to start a new run", ex);
			return ex.getMessage();			
		}
		return "Run '" + runName + "' started !";
	}

	@Override
	public String stopCurrentRun() {
		logger.debug("Stopping the current run !");
		RunManagerImpl.getInstance().stop();
		MonitorService.getMonitorService().registerDetectedServicesToNuxeo();
		return "Current run stopped !";
	}

	@Override
	public String setMonitoringMode(String mode) {
		logger.debug("Set monitoring mode !");
		try {
			if(mode == null || "".equals(mode)){
				throw new IllegalArgumentException("Monitoring mode must not be null or empty");
			}
			MonitorService.getMonitorService(MonitoringMode.valueOf(mode.toUpperCase()));
		} catch(Exception ex){
			logger.error("Unable to set monitoring mode", ex);
			return ex.getMessage();			
		}		
		return "Monitoring mode set";
	}

}
