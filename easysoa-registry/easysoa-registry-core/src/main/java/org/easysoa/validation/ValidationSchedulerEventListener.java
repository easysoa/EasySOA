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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.validation;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class ValidationSchedulerEventListener implements EventListener {

    private static Log log = LogFactory.getLog(ValidationSchedulerEventListener.class);
    
	@Override
	public void handleEvent(Event event) throws ClientException {

        try {
            // "eventCategory" stores information under the format "runName-environmentName"
            // (see ValidationSchedulerComponent.registerContribution()
            String[] eventData = ((String) event.getContext().getProperty("eventCategory")).split("-");

            EnvironmentValidationService environmentValidationService = Framework.getService(EnvironmentValidationService.class);
            environmentValidationService.run(eventData[0], eventData[1]);
        } catch (Exception e) {
            log.error("Failed to trigger scheduled validation", e);
        }
        
	}
	
}
