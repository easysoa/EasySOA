package pureairflowers.clients;

import javax.jws.WebService;

@WebService(endpointInterface = "pureairflowers.clients.PureAirFlowersClients", serviceName = "PureAirFlowers")
public class PureAirFlowersClientsImpl implements PureAirFlowersClients {

	public int getOrdersNumber(String clientId) {
        System.out.println("getOrdersNumber called");
        return 10;
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
