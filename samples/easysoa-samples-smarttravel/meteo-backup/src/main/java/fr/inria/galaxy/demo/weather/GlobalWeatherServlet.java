package fr.inria.galaxy.demo.weather;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;

import net.webservicex.GlobalWeatherSoapImpl;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;

public class GlobalWeatherServlet extends CXFServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2337463742074276141L;

	@Override
	public void loadBus(ServletConfig servletConfig) throws ServletException {
		super.loadBus(servletConfig);
		Bus bus = getBus();
		BusFactory.setDefaultBus(bus);
		       
		Endpoint.publish("/GlobalWeather", new GlobalWeatherSoapImpl());
	}
}
