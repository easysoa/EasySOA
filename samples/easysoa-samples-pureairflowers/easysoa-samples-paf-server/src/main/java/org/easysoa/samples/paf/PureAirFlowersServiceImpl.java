/**
 * EasySOA Samples - PureAirFlowers
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.samples.paf;

import javax.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author jguillemotte
 * 
 */
@WebService(endpointInterface = "org.easysoa.samples.paf.PureAirFlowersService", serviceName = "PureAirFlowers")
public class PureAirFlowersServiceImpl implements PureAirFlowersService {

    private final static Logger logger = LoggerFactory.getLogger(PureAirFlowersServer.class);

    public int getOrdersNumber(String clientName) {
        logger.info("getOrdersNumber called");
        return Math.abs(clientName.hashCode() % 50);
    }

}
