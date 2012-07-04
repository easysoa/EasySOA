package org.easysoa.samples.axxx.dcv;

import javax.inject.Inject;

import org.easysoa.samples.axxx.dps.TdrService;

public class ClientServiceImpl {

    @Inject TdrService tdrService;
    
    public void updateClient(Client client) {}
    public void updateContact(ClientContact clientContact) {}
    public void updateInfo(ClientInfo clientInfo) {}
    public Client getClient() { return null; }

}
