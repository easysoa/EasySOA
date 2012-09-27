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

package org.easysoa.sca.intents;

import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.intent.cxf.AbstractHandlerIntent;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;

/**
 * 
 * @author jguillemotte
 *
 */
public class CXFMonitoringIntent extends AbstractHandlerIntent implements Handler<MessageContext> {

    @Reference
    MonitoringService samMonitoringService;
    
    //@Override
    /*protected void configure(HTTPClientPolicy httpClientPolicy) {
        
        // Event list
        List<EventType> events = new ArrayList<EventType>();

        // Create a new event for in message and fill it with CXF message data
        EventType inMessageEvent = new EventType();
        //inMessageEvent.setEventType(EventEnumType.REQ_IN);

        // Create a new event for out message and fill it with CXF message data        
        EventType outMessageEvent = new EventType();
        
        // Add events to list
        events.add(inMessageEvent);
        events.add(outMessageEvent);
        */
        /*try {
            // Send events
            samMonitoringService.putEvents(events);
        } catch (PutEventsFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
    /*}*/

    @Override
    public boolean handleMessage(MessageContext context) {
        return true;
    }

    @Override
    public boolean handleFault(MessageContext context) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void close(MessageContext context) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected Handler<MessageContext> getHandler() {
        return this;
    }

}