package org.easysoa.discovery.rest.client;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.POJONode;
import org.easysoa.discovery.rest.model.SoaNode;

public class DiscoveryRequest {
    
    private List<SoaNode> notifications = new LinkedList<SoaNode>();
    
    private final OutputStream outputStream;
    
    public DiscoveryRequest(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void addDiscoveryNotifications(Collection<SoaNode> soaNode) {
        notifications.addAll(soaNode);
    }
    
    public void addDiscoveryNotification(SoaNode soaNode) {
        notifications.add(soaNode);
    }
    
    public void addDiscoveryNotification(SoaNode... soaNodes) {
        notifications.addAll(Arrays.asList(soaNodes));
    }
    
    public void send() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode notificationsArray = mapper.createArrayNode();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        for (SoaNode notification : notifications) {
            POJONode pojoNode = mapper.getNodeFactory().POJONode(notification);
            notificationsArray.add(pojoNode);
        }
        writer.writeValue(outputStream, notificationsArray);
    }

}
