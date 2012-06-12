/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.handler.event.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * To Implement all the web services registered in the serviceToLaunch.composite
 * They will be launched automatically by frascati
 * @author fntangke
 */
@Scope("Composite")
public class SubscriptionWebserviceImpl implements ISubscriptionWebService {

    private Subscriptions subscriptions;
    @Reference
    private IEventMessageHandler eventMessageHandler;
    
    public SubscriptionWebserviceImpl(){
        this.subscriptions = new Subscriptions();
    }
    
    /**
     * Used to return a valid json of xml result
     * For our needs.
     */
    public Subscriptions simulResult(){
        
        Subscriptions sub = new Subscriptions();

        Subscription su = new Subscription();

        ListenedService lisw = new ListenedService();
        lisw.setId(17);
        lisw.setUrl("http://amazon.fr");

        LaunchedService laweb = new LaunchedService();
        laweb.setUrl("http://Yahoo.fr");
        laweb.setId(159);		

        LaunchedService laweb1 = new LaunchedService();
        laweb1.setUrl("http://Yahorgeo.fr");
        laweb1.setId(15);

        ArrayList<LaunchedService> launchlist = new ArrayList<LaunchedService>();
        launchlist.add(laweb);
        launchlist.add(laweb1);

        ArrayList<ListenedService> listelist  = new ArrayList<ListenedService>();

        listelist.add(lisw);

        su.setLaunchedservices(launchlist);
        su.setListenedservices(listelist);

        ArrayList<Subscription> subb = new ArrayList<Subscription>();

        subb.add(su);
        sub.setSubscriptions(subb);
        return sub;
    }
	
    /**
     * To set subscriptions and set the 
     *
     * @return 
     * Example POST JSON/HTTP request content: {'subscriptions':{'subscriptions':{'launchedservices':[{'id':159,'url':'http://Yahoo.fr'},{'id':15,'url':'http://Yahorgeo.fr'}],'listenedservices':{'id':17,'url':'http://amazon.fr'}}}}
     */ 
    
    @POST
    @Path("/subscriptions")
    @Produces({"application/xml", "application/json"})
    @Override
    public Subscriptions udpateSubscriptions(Subscriptions subscriptions) {
        //we get this uid service in nuxeo
        this.setSubscriptions(subscriptions);
        // update listenedServiceUrlToServicesToLaunchUrlMap :
        //this.eventMessageHandler.getListenedServiceUrlToServicesToLaunchUrlMap().clear();
      
        for (Subscription subscription : subscriptions.getSubscriptions()) {		
                for(ListenedService servicelistened: subscription.getListenedservices()){
                        List<String> launchedServicesUrlsCopy = new ArrayList<String>(subscription.getLaunchedServicesUrls());
                        this.eventMessageHandler.getListenedServiceUrlToServicesToLaunchUrlMap().put(servicelistened.getUrl(), launchedServicesUrlsCopy);
                }		
        }
        this.eventMessageHandler.setListenedServiceUrlToServicesToLaunchUrlMap(new HashMap<String, List<String>>());    
        return simulResult();
    }

    /**
     * TODO doesn't work yet
     * 
     */
    
    @POST	
    @Path("/subscriptions/{subscriptionId}")
    @Produces({"application/xml", "application/json"})
    public boolean updateSubscription(Subscription subscription, @PathParam("subscriptionId") String subscriptionId) {
            // LATER
            return true;
    }

    /**
     *  To return a json or xml of the subscriptions
     */
    @GET    
    @Path("/subscriptions")   
    @Produces({"application/xml", "application/json"})
    public Subscriptions getSubscriptions() {
        return this.subscriptions;
    	//return simulResult();
    }
    
    /**
     * @param subscriptions the subscriptions to set
     */
    public void setSubscriptions(Subscriptions subscriptions) {
        this.subscriptions = subscriptions;
    }
    
}
