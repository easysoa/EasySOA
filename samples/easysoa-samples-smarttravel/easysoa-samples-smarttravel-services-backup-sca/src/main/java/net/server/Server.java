package net.server;

import javax.xml.ws.Endpoint;

import com.microsofttranslator.api.v1.soap_svc.LanguageService;
import com.microsofttranslator.api.v1.soap_svc.LanguageServiceImpl;
import de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoapImpl1;

import net.webservicex.GlobalWeatherSoapImpl1;

public class Server {

	public final static String ADDRESS_BASE = "http://localhost:9020/";
	
	/**
	 * 
	 * @param addressBase
	 * @throws Exception
	 */
    protected Server(String addressBase) throws Exception {
        System.out.println("Starting Server");
        if(addressBase == null || "".equals(addressBase)){
        	addressBase = ADDRESS_BASE;
        }

        // Meteo backup
        GlobalWeatherSoapImpl1 meteoImplementor = new GlobalWeatherSoapImpl1();
        //Endpoint.publish(address, implementor);
        Endpoint.publish(addressBase + "WeatherService", meteoImplementor);
        
        // Currency Backup
        CurrencyServerWebServiceSoapImpl1 currencyImplementor = new CurrencyServerWebServiceSoapImpl1();
        Endpoint.publish(addressBase + "CurrencyServerWebService", currencyImplementor);
        
        // Translator backup
        LanguageService translateImplementor = new LanguageServiceImpl();
        //Endpoint.publish(address, translateImplementor);
        Endpoint.publish(addressBase + "SoapService", translateImplementor);
    }

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
    	new Server(args[0]);
    	if(args == null || args[0] == null || "".equals(args[0])){
    		throw new IllegalArgumentException("The deployment address must be specified in arg0 !");
    	}
        System.out.println("Server ready...");
        System.out.println("To stop the server, push the 'Q' key !");
        while(System.in.read()!='q' && System.in.read()!='Q'){
        	Thread.sleep(100);
        	// Continue
        }
        System.out.println("Server exiting");
        System.exit(0);        
    }

}
