package fr.inria.galaxy.demo;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;


public class CustomCXFServlet extends CXFServlet {

	private static final long serialVersionUID = 9191591338441395695L;

	@Override
	    public void loadBus(ServletConfig servletConfig) throws ServletException {
	        super.loadBus(servletConfig);        
	        Bus bus = getBus();
	        BusFactory.setDefaultBus(bus); 
	        Endpoint.publish("/SummaryService", new CreateSummary());          
	    }

}
