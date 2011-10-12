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
 * Contact : easysoa-dev@groups.google.com
 */

package pureairflowers.clients;

import javax.jws.WebService;

@WebService(endpointInterface = "pureairflowers.clients.PureAirFlowersClients", serviceName = "PureAirFlowers")
public class PureAirFlowersClientsImpl implements PureAirFlowersClients {

	public int getOrdersNumber(String clientName) {
        System.out.println("getOrdersNumber called");
        return Math.abs(clientName.hashCode() % 50);
    }

	/*public String repeatAfterMe(String text, int iterations) {
        System.out.println("repeatAfterMe called");		
		StringBuffer st = new StringBuffer();
		for(int i = 0; i<iterations; i++){
			if(st.length() > 0){
				st.append(" ; ");
			}
			st.append(text);
		}
		return st.toString();
	}*/
}
