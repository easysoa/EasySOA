/**
 * EasySOA Samples - Smart Travel
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package net.server;

import javax.xml.ws.Endpoint;

import com.microsofttranslator.api.v1.soap_svc.LanguageServiceSoapSpyWrapper;
import de.daenet.webservices.currencyserver.CurrencyServerWebServiceSoapSpyWrapper;
import net.webservicex.GlobalWeatherWebServiceSoapSpyWrapper;

public class Server {

	public final static String ADDRESS_BASE = "http://localhost:9020/";
	
	protected GlobalWeatherWebServiceSoapSpyWrapper meteoImplementor;
	protected CurrencyServerWebServiceSoapSpyWrapper currencyImplementor;
	protected LanguageServiceSoapSpyWrapper translateImplementor;
	//protected CurrencyServerWebServiceSoap currencyImplementor;
	
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
        //meteoImplementor = new GlobalWeatherSoapImpl1();
        meteoImplementor = new GlobalWeatherWebServiceSoapSpyWrapper();
        Endpoint.publish(addressBase + "WeatherService", meteoImplementor);
        
        // Currency Backup
        //currencyImplementor = new CurrencyServerWebServiceSoapImpl1();
        currencyImplementor = new CurrencyServerWebServiceSoapSpyWrapper();
        Endpoint.publish(addressBase + "CurrencyServerWebService", currencyImplementor);

        /**
         * Cannot use here a dynamic proxy. There is a problem with JAXWS annotations.
         * The annotations are not used and the service is not deployed at the right place so the client service cannot use it.
         */
        //CurrencyServerWebServiceSoapSpyWrapper currencyWrapper = new CurrencyServerWebServiceSoapSpyWrapper();
        //currencyImplementor = (CurrencyServerWebServiceSoap) CurrencyServerWebServiceSoapSpyDynWrapper.newInstance(currencyWrapper);
        //Endpoint.publish(addressBase + "CurrencyServerWebService", currencyWrapper);
        //Endpoint.publish(addressBase + "CurrencyServerWebService", (CurrencyServerWebServiceSoapSpyWrapper)currencyImplementor);
        
        // Translator backup
        //translateImplementor = new LanguageServiceImpl();
        translateImplementor = new LanguageServiceSoapSpyWrapper();
        Endpoint.publish(addressBase + "SoapService", translateImplementor);
    }

    public GlobalWeatherWebServiceSoapSpyWrapper getMeteoImplementor(){
    	return this.meteoImplementor;
    }
    
    public CurrencyServerWebServiceSoapSpyWrapper getCurrencyImplementor(){
    //public CurrencyServerWebServiceSoap getCurrencyImplementor(){
    	return this.currencyImplementor;
    }
    
    public LanguageServiceSoapSpyWrapper getTranslateImplementor(){
    	return this.translateImplementor;
    }
    
    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String addressBase = ADDRESS_BASE;
        if(args != null && args.length != 0 && args[0] != null && !"".equals(args[0])){
            addressBase = args[0];
        }
    	new Server(addressBase);
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
