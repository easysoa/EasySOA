/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.exchangehandler;

import java.util.HashMap;
import java.util.List;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.handler.event.admin.CompiledCondition;

/**
 *
 * @author fntangke
 */
public interface MessageHandler {

    /**
     * Handle an exchange
     *
     * @param messagein incoming message
     * @param messageout outgoing message
     * @throws Exception
     */
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception;
    
    /**
     * 
     * @param newListenedServiceUrlToServicesToLaunchUrlMap 
     */

    public void setListenedServiceUrlToServicesToLaunchUrlMap(HashMap<List<CompiledCondition>, List<String>> newListenedServiceUrlToServicesToLaunchUrlMap);
    
    /**
     * 
     * @param inMessage
     * @return is The inMessage's url is reconized by the proxy
     */
    public boolean isApplicable(InMessage inMessage);
}
