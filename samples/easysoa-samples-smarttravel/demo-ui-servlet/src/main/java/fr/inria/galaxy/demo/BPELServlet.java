package fr.inria.galaxy.demo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import fr.inria.galaxy.j1.scenario1.Trip;
import fr.inria.galaxy.j1.scenario1.TripPortType;
import fr.inria.galaxy.orchestration.examples.trip.generated.OrchestrationSTrip;
import fr.inria.galaxy.orchestration.examples.trip.generated.OrchestrationSTripPortType;

public class BPELServlet extends HttpServlet {

	private static final long serialVersionUID = -3738652774577953854L;

	private static final String PARAM_CITY = "city";
	private static final String PARAM_FREE_SENTENCE = "freeSentence";
	private static final String PARAM_EXCHANGE_THRESHOLD = "exancheRateOK";
	private static final String PARAM_ENGINE = "engine";
	private static final String SEA = "SEA";
	private static final String ART = "ART";
	private static final String MOUNTAIN = "MOUNTAIN";

	private URL URI_ACTIVE_BPEL;
	private URL URI_FRASCATI;

	private TripPortType frascatiService;
	private OrchestrationSTripPortType activeBpelService;
	private HTTPClientPolicy httpClientPolicy;

	@Override
	public void init() throws ServletException {
		super.init();

		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/config.properties"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		String activeBpelWsdl = properties.getProperty("activebpel.wsdl");
		String frascatiWsdl = properties.getProperty("frascati.wsdl");
		System.out.println("activeBpelWsdl:" + activeBpelWsdl);
		System.out.println("frascatiWsdl:" + frascatiWsdl);
		URI_ACTIVE_BPEL = null;
		URI_FRASCATI = null;
		try {
			URI_ACTIVE_BPEL = new URL(activeBpelWsdl);
			URI_FRASCATI = new URL(frascatiWsdl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(600000);
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setAutoRedirect(true);

		System.out.println("Init DONE");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		onRequest(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		onRequest(req, resp);
	}

	private void onRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String engine = req.getParameter(PARAM_ENGINE);
		String freeSentence = req.getParameter(PARAM_FREE_SENTENCE);
		String city = req.getParameter(PARAM_CITY);
		String activity;
		double exchangeThreshold = Double.parseDouble(req.getParameter(PARAM_EXCHANGE_THRESHOLD));

		String response = "error";
		if (engine.equalsIgnoreCase("bpel")) {
			// ActiveBPEL
			OrchestrationSTrip ss2 = new OrchestrationSTrip(URI_ACTIVE_BPEL);
			activeBpelService = ss2.getOrchestrationSTripHttpSoap11Endpoint();
			
			Client client = ClientProxy.getClient(activeBpelService);
			HTTPConduit http = (HTTPConduit) client.getConduit();
			http.setClient(httpClientPolicy);
			
			response = activeBpelService.process(city, freeSentence, exchangeThreshold);
		} else {
			// get activity
			if (city.compareTo("Grenoble") == 0)
				activity = MOUNTAIN;
			else if (city.compareTo("Nice") == 0)
				activity = SEA;
			else
				activity = ART;
			
			Trip ss = new Trip(URI_FRASCATI);
			frascatiService = ss.getTripPort();
			
			Client client = ClientProxy.getClient(frascatiService);
			HTTPConduit http = (HTTPConduit) client.getConduit();
			http.setClient(httpClientPolicy);

			response = frascatiService.process(activity, freeSentence, exchangeThreshold);
		}

		req.setAttribute("response", response);
		RequestDispatcher rd = req.getRequestDispatcher("jsp/result.jsp");
		rd.forward(req, resp);
	}
}
