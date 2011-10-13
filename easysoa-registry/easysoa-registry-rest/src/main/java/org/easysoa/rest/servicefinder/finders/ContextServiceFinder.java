/**
 * EasySOA Registry
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
 * Contact : easysoa-dev@groups.google.com
 */

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
