package org.easysoa.samples.axxx.dcv;

import javax.jws.WebService;


@WebService
public interface ClientService {
    
    public void updateClient(Client client);
    public void updateContact(ClientContact clientContact);
    public void updateInfo(ClientInfo clientInfo);
    public Client getClient();

}
