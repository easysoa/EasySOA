package org.easysoa.samples.axxx.dps;

import javax.xml.ws.WebServiceProvider;

import org.easysoa.samples.axxx.dcv.Client;
import org.easysoa.samples.axxx.dcv.ClientContact;
import org.easysoa.samples.axxx.dcv.ClientInfo;

@WebServiceProvider
public class ClientServiceProvider {
    
    public void updateClient(Client client) {}
    public void updateContact(ClientContact clientContact) {}
    public void updateInfo(ClientInfo clientInfo) {}
    public Client getClient() { return null; }

}
