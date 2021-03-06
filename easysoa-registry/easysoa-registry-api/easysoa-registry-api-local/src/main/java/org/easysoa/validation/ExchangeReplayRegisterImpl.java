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
import org.easysoa.services.ServiceValidationService;
import org.nuxeo.runtime.api.Framework;

/**
 * Implementation of ExchangeReplayRegister interface
 * 
 * @author jguillemotte
 *
 */
public class ExchangeReplayRegisterImpl implements ExchangeReplayRegister {

    private static final Log log = LogFactory.getLog(ExchangeReplayRegisterImpl.class);    
    
    /**
     * Register an <code>ExchangeReplayController</code> in the Nuxeo <code>ServiceValidationService</code>
     */
    @Override
    public void registerExchangeReplayController(ExchangeReplayController exchangeReplayController) throws Exception {
        // Get the Nuxeo service corresponding to ServiceValidationService
        ServiceValidationService validationService = Framework.getService(ServiceValidationService.class);
        
        // Registering the exchange replay controller
        log.info("registering exchange replay controller");
        validationService.setExchangeReplayController(exchangeReplayController);
    }

}
