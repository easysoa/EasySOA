package org.easysoa.rest.servicefinder.finders;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.EasySOAConstants;
import org.easysoa.rest.servicefinder.FoundService;
import org.easysoa.rest.servicefinder.ServiceFinder;

/**
 * 
 * Scraper based on the knowledge of various services stacks,
 * to retrieve web-services by trying various URL patterns.
 * 
 * XXX: Currently highly mocked, works with the demo only.
 * 
 * @author mkalam-alami
 *
 */
public class ContextServiceFinder extends DefaultAbstractFinder implements ServiceFinder {
    
    @Override
    public List<FoundService> findFromURL(URL url) throws Exception {
        
        List<FoundService> foundServices = new LinkedList<FoundService>();
        
        // XXX: Hard-coded matching of the PAF demo services
        if (url.getPath().contains("crm")) {
            foundServices.add(new FoundService(
                    "Orders service",
                    "http://localhost:"+EasySOAConstants.PAF_SERVICES_PORT+"/PureAirFlowers?wsdl",
                    guessApplicationName(url)));
        }
        
        return foundServices;
    }

}
