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

import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.intent.cxf.AbstractHandlerIntent;
import org.talend.esb.sam._2011._03.common.CustomInfoType;
import org.talend.esb.sam._2011._03.common.CustomInfoType.Item;
import org.talend.esb.sam._2011._03.common.EventEnumType;
import org.talend.esb.sam._2011._03.common.EventType;
import org.talend.esb.sam._2011._03.common.MessageInfoType;
import org.talend.esb.sam._2011._03.common.OriginatorType;
import org.talend.esb.sam.agent.eventproducer.MessageToEventMapper;
import org.talend.esb.sam.common.event.Event;
import org.talend.esb.sam.monitoringservice.v1.MonitoringService;

/**
 * 
 * @author jguillemotte
 *
 */
public class CXFMonitoringIntent extends AbstractHandlerIntent implements Handler<MessageContext> {

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger(CXFMonitoringIntent.class.getName());    
    
    // SAM WS service reference
    @Reference
    MonitoringService samMonitoringService;

    @Override
    public boolean handleMessage(MessageContext context) {

        // Event list
        List<EventType> events = new ArrayList<EventType>();

        // To remove when finished
        Iterator<String> keys = context.keySet().iterator();
        logger.debug("CXF context keys =>");
        while(keys.hasNext()){
            logger.debug(keys.next());
        }

        try {
            WrappedMessageContext wmc = (WrappedMessageContext) context;
            Message message = wmc.getWrappedMessage();
            //message.setContent(CachedOutputStream.class, new CachedOutputStream(message.getContent(PipedInputStream.class)));
            MessageToEventMapper mapper = new MessageToEventMapper();
            Event event = mapper.mapToEvent(message);
                
            // Add events to list
            events.add(mapEventToEventType(event));

            // Send events
            String samResponse = samMonitoringService.putEvents(events);
            logger.info("SAM server response");
        } catch (Exception ex) {
            logger.error("An error occurs when sending the event in SAM Server", ex);
            ex.printStackTrace();
        }        
        
        return true;
    }

    /**
     * Mapper to transform an Event to an EventType
     * @param event
     * @return
     */
    private EventType mapEventToEventType(Event event) {
        EventType messageEvent = new EventType();
        // Content
        try {
            MimeType mimeType = new MimeType(event.getContent());
            messageEvent.setContent(new DataHandler(event.getContent(), mimeType.toString()));
        } catch (MimeTypeParseException ex) {
            logger.error("An error occurs when setting the content. Message content is maybe null or empty", ex);
            ex.printStackTrace();
        }            
        // Content cut
        messageEvent.setContentCut(event.isContentCut());
        // CustomInfo
        CustomInfoType customInfoType = new CustomInfoType();
        List<Item> customInfoItemList = customInfoType.getItem();
        for(String customInfoKey : event.getCustomInfo().keySet()){
            Item item = new Item();
            item.setKey(customInfoKey);
            item.setValue(event.getCustomInfo().get(customInfoKey));
            customInfoItemList.add(item);
        }
        messageEvent.setCustomInfo(customInfoType);
        // Event type
        messageEvent.setEventType(EventEnumType.fromValue(event.getEventType().toString()));
        // Message info
        MessageInfoType messageInfoType = new MessageInfoType();
        messageInfoType.setFlowId(event.getMessageInfo().getFlowId());
        messageInfoType.setMessageId(event.getMessageInfo().getMessageId());
        // Got a problem with the 2 following properties : marshalling error on SAM server because the tag string segment "{http" is considered as a namespace prefix
        //messageInfoType.setOperationName(event.getMessageInfo().getOperationName());
        //messageInfoType.setPorttype(new QName(event.getMessageInfo().getPortType()));
        messageInfoType.setTransport(event.getMessageInfo().getTransportType());
        messageEvent.setMessageInfo(messageInfoType);
        // Originator
        OriginatorType originatorType = new OriginatorType();
        originatorType.setCustomId(event.getOriginator().getCustomId());
        originatorType.setHostname(event.getOriginator().getHostname());
        originatorType.setIp(event.getOriginator().getIp());
        originatorType.setPrincipal(event.getOriginator().getPrincipal());
        originatorType.setProcessId(event.getOriginator().getProcessId());
        messageEvent.setOriginator(originatorType);
        // timestamp
        GregorianCalendar gCalendar = new GregorianCalendar();
        gCalendar.setTime(event.getTimestamp());
        try {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            messageEvent.setTimestamp(factory.newXMLGregorianCalendar(gCalendar));
        }
        catch(Exception ex){
            logger.error("An error occurs when setting the timestamp", ex);
            ex.printStackTrace();
        }
        //
        return messageEvent; 
    }

    @Override
    public boolean handleFault(MessageContext context) {
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