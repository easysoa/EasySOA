package org.easysoa.proxy.handler.event.admin;

import java.util.ArrayList;
import java.util.List;
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
	
    public Subscriptions(List<Subscription> lsubscription){
    	this.subscriptions = lsubscription;
    }
    
	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}
    
}
