package fr.inria.galaxy.demo.translate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;

import com.microsofttranslator.api.v1.soap.SoapService;

public class TranslateServerServlet extends CXFServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2338569342074276141L;

	@Override
	public void loadBus(ServletConfig servletConfig) throws ServletException {
		super.loadBus(servletConfig);
		Bus bus = getBus();
		BusFactory.setDefaultBus(bus);
		       
		Endpoint.publish("/SoapService", new SoapService());
	}
}
