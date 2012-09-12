package org.easysoa.samples.axxx.dps;

import javax.xml.ws.WebServiceClient;

import org.easysoa.samples.axxx.dcv.Client;
import org.easysoa.samples.axxx.dcv.ClientContact;
import org.easysoa.samples.axxx.dcv.ClientInfo;

@WebServiceClient
public class ClientServiceClient {
    
    public void updateClient(Client client) {}
    public void updateContact(ClientContact clientContact) {}
    public void updateInfo(ClientInfo clientInfo) {}
    public Client getClient() { return null; }

}
