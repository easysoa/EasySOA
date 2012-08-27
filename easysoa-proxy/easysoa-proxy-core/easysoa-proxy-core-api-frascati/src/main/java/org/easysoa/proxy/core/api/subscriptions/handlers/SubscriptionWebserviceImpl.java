/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.subscriptions.handlers;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.*;

import org.easysoa.proxy.core.api.exchangehandler.MessageHandler;
import org.easysoa.proxy.core.api.handler.event.admin.CompiledCondition;
import org.easysoa.proxy.core.api.handler.event.admin.IEventMessageHandler;
import org.easysoa.proxy.core.api.handler.event.admin.LaunchedService;
import org.easysoa.proxy.core.api.handler.event.admin.ListenedService;
import org.easysoa.proxy.core.api.handler.event.admin.Subscription;
import org.easysoa.proxy.core.api.handler.event.admin.Subscriptions;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * To Implement all the web services registered in the serviceToLaunch.composite
 * They will be launched automatically by frascati
 *
 * @author fntangke
 */
@Scope("Composite")
public class SubscriptionWebserviceImpl implements ISubscriptionWebService {

    private Subscriptions subscriptions;
    //private IEMessageHandler eventMessageHandler;
    @Reference
    private MessageHandler eventMessageHandler;

    public SubscriptionWebserviceImpl() {
        this.subscriptions = new Subscriptions();
    }

    /**
     * Used to return a valid json of xml result For our needs.
     */
    public Subscriptions simulResult() {

        List<Subscription> listsub = new ArrayList<Subscription>();


        LaunchedService laweb = new LaunchedService();
        laweb.setUrl("http://Yahoo.fr");


        LaunchedService laweb1 = new LaunchedService();
        laweb1.setUrl("http://Yahorgeo.fr");
        /*      
       
        
         Conditions conds1 = new Conditions("http://amazon.fr");
         Subscription subsc1 = new Subscription();
         subsc1.setConditions(conds1);
         List<LaunchedService> list1 = new ArrayList<LaunchedService>();
         list1.add(laweb);
         subsc1.setLaunchedservices(list1);
         listsub.add(subsc1);
        
         Conditions conds2 = new Conditions("http://google.fr");
         Subscription subsc2 = new Subscription();
         subsc1.setConditions(conds2);
         List<LaunchedService> list2 = new ArrayList<LaunchedService>();
         list2.add(laweb1);
         subsc2.setLaunchedservices(list2);
         listsub.add(subsc2);
        
         this.subscriptions.setSubscriptions(listsub);
        
         ArrayList<LaunchedService> launchlist = new ArrayList<LaunchedService>();
         launchlist.add(laweb);
         launchlist.add(laweb1);

         ArrayList<ListenedService> listelist = new ArrayList<ListenedService>();

         listelist.add(lisw);

         subsc.setLaunchedservices(launchlist);
         Conditions conditions = new Conditions("http://amazon.fr");
         subsc.setConditions(conditions);

         ArrayList<Subscription> listesubscription = new ArrayList<Subscription>();

         listesubscription.add(subsc);
         subscriptions.setSubscriptions(listesubscription);*/
        return subscriptions;
    }

    /**
     * To set subscriptions and set the
     *
     * @return Example POST JSON/HTTP request content:
     * {'subscriptions':{'subscriptions':{'launchedservices':[{'id':159,'url':'http://Yahoo.fr'},{'id':15,'url':'http://Yahorgeo.fr'}],'listenedservices':{'id':17,'url':'http://amazon.fr'}}}}
     */
    @Override
    public Subscriptions udpateSubscriptions(Subscriptions subscriptions) {
        //we get this uid service in nuxeo
        this.setSubscriptions(subscriptions);

        // update listenedServiceUrlToServicesToLaunchUrlMap :
        HashMap<List<CompiledCondition>, List<String>> newListenedServiceUrlToServicesToLaunchUrlMap = new HashMap<List<CompiledCondition>, List<String>>();

        for (Subscription subscription : subscriptions.getSubscriptions()) {
            // TODO mettre à jour le contenu en changeant pour mettre les conditions à la place des listenedservices
  /*              for(ListenedService servicelistened: subscription.getListenedservices()){    
             List<String> launchedServicesUrlsCopy = new ArrayList<String>(subscription.getLaunchedServicesUrls());
             CompiledCondition compiledCondition = new CompiledCondition(servicelistened.getUrl());
                        
             List<CompiledCondition> compiledConditionList = new ArrayList<CompiledCondition>();
             compiledConditionList.add(compiledCondition);
                        
             newListenedServiceUrlToServicesToLaunchUrlMap.put(compiledConditionList, launchedServicesUrlsCopy);
             }		
             */
        }

        // single update because we're calling a synchronized method here :
        //TODO Absolument changer  et rajouter la methode setListenedServiceUrlToServicesToLaunchUrlMap(newListenedServiceUrlToServicesToLaunchUrlMap); a l iterface dmessage handler et la retirer à emessagehandler
        this.eventMessageHandler.setListenedServiceUrlToServicesToLaunchUrlMap(newListenedServiceUrlToServicesToLaunchUrlMap);

        //    return simulResult();
        return this.getSubscriptions();
    }

    /**
     * To return the Id subscription
     *
     */
    public Subscription updateSubscription(Subscription subscription, @PathParam("subscriptionId") Integer subscriptionId) {
        return this.subscriptions.getSubscriptions().get(subscriptionId);

    }

    /**
     *
     * @return the subscription list of he runtime side
     */
    @Override
    public Subscriptions getSubscriptions() {
        this.simulResult();
        return this.subscriptions;
    }

    /**
     * @param subscriptions the subscriptions to set
     */
    public void setSubscriptions(Subscriptions subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String getNumber() {
        return "text representation";
    }
}
