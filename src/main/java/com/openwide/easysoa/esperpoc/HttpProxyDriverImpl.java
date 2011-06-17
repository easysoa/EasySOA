package com.openwide.easysoa.esperpoc;

import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;

import com.openwide.easysoa.monitoring.MonitorService;

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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		logger.debug("Starting a new run !");
		RunManager.getInstance().start(runName);
		return "Run '" + runName + "' started !";
	}

	@Override
	public String startNewRun() {
		// TODO Add code to register recorded services in Nuxeo at the end of the run
		logger.debug("Stopping the current run !");
		RunManager.getInstance().stop();
		MonitorService.getMonitorService().registerDetectedServicesToNuxeo();
		return "Current run stopped !";
	}

}
