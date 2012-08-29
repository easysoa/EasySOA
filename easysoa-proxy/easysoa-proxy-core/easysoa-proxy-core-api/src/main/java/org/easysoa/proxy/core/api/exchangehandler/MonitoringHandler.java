/**
 * EasySOA Proxy
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
package org.easysoa.proxy.core.api.exchangehandler;

import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.run.RunManager;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;


/**
 * Implementation of the message handler for the serviceToLaunch.composite
 * @author fntangke
 *
 */

@Scope("composite")
public class MonitoringHandler implements MessageHandler {

    @Reference
    RunManager runManager;
    
    @Override
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        // Builds a new Exchange record with data contained in request and response
        ExchangeRecord record = new ExchangeRecord();
        record.setInMessage(inMessage);
        record.setOutMessage(outMessage);
        // Send it to the monitoring service
        runManager.getMonitoringService().listen(record);
        //runManager.getMonitoringService().registerDetectedServicesToNuxeo();
    }

}
