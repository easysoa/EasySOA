package org.easysoa.discovery.rest.client;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.easysoa.discovery.rest.model.SOANode;

public class DiscoveryRequest {
    
    private List<SOANode> notifications = new LinkedList<SOANode>();
    
    public DiscoveryRequest() {
        // TODO Auto-generated constructor stub
    }

    public void addDiscoveryNotifications(Collection<SOANode> soaNode) {
        notifications.addAll(soaNode);
    }
    
    public void addDiscoveryNotification(SOANode soaNode) {
        notifications.add(soaNode);
    }
    
    public void send() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        for (SOANode notification : notifications) {
            System.out.println(writer.writeValueAsString(notification.toJSON()));
        }
    }

}
