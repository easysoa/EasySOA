package org.easysoa.proxy.handler.event.admin;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author fntangke
 * 
 */

public interface IEventMessageHandler {
    
    /**
     * @param void
     * @return the services to call
     */
    public Map<String, List<String>> getListenedServiceUrlToServicesToLaunchUrlMap();	
  
      
    /**
     * @param listenedServiceUrlToServicesToLaunchUrlMap  services to call
     * @return void
     */
    
    public void setListenedServiceUrlToServicesToLaunchUrlMap(
			Map<String, List<String>> listenedServiceUrlToServicesToLaunchUrlMap);
}
