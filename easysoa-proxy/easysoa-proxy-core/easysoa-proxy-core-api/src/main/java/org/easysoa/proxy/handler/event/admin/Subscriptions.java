package org.easysoa.proxy.handler.event.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author fntangke 
 */

@XmlRootElement
public class Subscriptions {
    
    private List<Subscription> subscriptions;

    public Subscriptions(){
        this.subscriptions = new ArrayList<Subscription>();
    }

    public Subscriptions(Subscription subscription){
        this.subscriptions = new ArrayList<Subscription>();
        this.subscriptions.add(subscription);
    }
    
    public Subscriptions(List<Subscription> lsubscription){
    	this.subscriptions = lsubscription;
    }
    
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    /**
     * 
     * @param subscriptions 
     */
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    
    /**
     * 
     * @param behavior
     * @return Set the behavior when a listened web service is called
     */
    public Map<List<CompiledCondition>, List<String>> updateBehaviors(){
        
        Map<List<CompiledCondition>, List<String>> behavior = new HashMap<List<CompiledCondition>, List<String>>();
        List<CompiledCondition> listCompiledCondition = new ArrayList<CompiledCondition>();
        for(Subscription subscription: this.subscriptions){
            CompiledCondition compiledCondition = new CompiledCondition(subscription.getConditions().getRegexConditions());
            listCompiledCondition.add(compiledCondition);
            behavior.put(listCompiledCondition, subscription.getLaunchedServiceUrl());
        }
        return behavior;
    }
}
