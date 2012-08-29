package org.easysoa.proxy.core.api.event;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author fntangke 
 */

@XmlRootElement
public class ListenedServices {
    
	private List<ListenedService> listenedServices;
	
	public ListenedServices(){
		this.listenedServices = new ArrayList<ListenedService>();
	}
	
    public ListenedServices(List<ListenedService> lservices){
    	this.listenedServices = lservices;
    }

	public List<ListenedService> getListenedServices() {
		return listenedServices;
	}

	public void setListenedServices(List<ListenedService> listenedServices) {
		this.listenedServices = listenedServices;
	}

}
