package org.easysoa.proxy.core.api.event;

import java.util.List;
import java.util.Map;
import org.easysoa.message.InMessage;
import org.easysoa.proxy.core.api.exchangehandler.MessageHandler;

/**
 * @author fntangke
 */
public interface IEventMessageHandler extends MessageHandler {

    /**
     * WARNING readonly
     * @param void
     * @return the services to call
     */
    public Map<List<Condition>, List<String>> getListenedServiceUrlToServicesToLaunchUrlMap();

    /**
     * Update the conf ; WARNING synchronized method
     *
     * @param listenedServiceUrlToServicesToLaunchUrlMap services to call
     * @return void
     */
    public void setListenedServiceUrlToServicesToLaunchUrlMap(
            Map<List<Condition>, List<String>> listenedServiceUrlToServicesToLaunchUrlMap);

    /**
     * @param inMessage
     * @return is The inMessage's url is reconized by the proxy
     */
    public boolean isApplicable(InMessage inMessage);
    
    /**
     * return the configuration which map with the current ListenedServiceUrlToServicesToLaunchUrlMap
     * @return
     */
    public Configuration getConfiguration();
}
