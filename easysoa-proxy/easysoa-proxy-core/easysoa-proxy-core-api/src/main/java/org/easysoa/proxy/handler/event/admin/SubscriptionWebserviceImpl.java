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
        
        Subscriptions subscriptions = new Subscriptions();

        Subscription subsc = new Subscription();

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

        subsc.setLaunchedservices(launchlist);
        subsc.setListenedservices(listelist);

        ArrayList<Subscription> listesubscription = new ArrayList<Subscription>();

        listesubscription.add(subsc);
        subscriptions.setSubscriptions(listesubscription);
        return subscriptions;
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
        HashMap<String, List<String>> newListenedServiceUrlToServicesToLaunchUrlMap = new HashMap<String, List<String>>();
      
        for (Subscription subscription : subscriptions.getSubscriptions()) {		
                for(ListenedService servicelistened: subscription.getListenedservices()){
                        List<String> launchedServicesUrlsCopy = new ArrayList<String>(subscription.getLaunchedServicesUrls());
                        newListenedServiceUrlToServicesToLaunchUrlMap.put(servicelistened.getUrl(), launchedServicesUrlsCopy);
                }		
        }
        
        // single update because we're calling a synchronized method here :
        this.eventMessageHandler.setListenedServiceUrlToServicesToLaunchUrlMap(newListenedServiceUrlToServicesToLaunchUrlMap);
        
    //    return simulResult();
        return this.getSubscriptions();
    }

    /**
     * To return the Id subscription 
     * 
     */
    
    @GET	
    @Path("/subscriptions/{subscriptionId}")
    @Produces({"application/xml", "application/json"})
    public Subscription updateSubscription(Subscription subscription, @PathParam("subscriptionId") Integer subscriptionId) {
            // LATER
            return this.subscriptions.getSubscriptions().get(subscriptionId);
            
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
