
package org.easysoa.proxy.ssl.test.helloworld;

import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.util.RequestForwarder;
import org.easysoa.proxy.core.ssl.HttpClientSSLConfigurator;

public class HttpClient {

    public HttpClient() {
        System.out.println("Client created.");
    }
  
    public final String run() {
        String response="";
        
        System.out.println("Call the HelloService...");
      
        RequestForwarder requestForwarder = new RequestForwarder();
        HttpClientSSLConfigurator sslConfigurator = new HttpClientSSLConfigurator("src/test/resources/certificates/client.jks", "password", "src/test/resources/certificates/truststore.jks", "password");
        requestForwarder.setHttpClientSSLConfigurator(sslConfigurator);
        InMessage inMessage = new InMessage();
        inMessage.setProtocol("https");
        inMessage.setServer("localhost");
        inMessage.setPort(9090);
        inMessage.setPath("/HelloService/test");
        try {
            OutMessage outMessage = requestForwarder.send(inMessage);
            System.out.println("Response status : " + outMessage.getStatus());
            response = outMessage.getMessageContent().getRawContent();
            System.out.println("Response raw content : " + response);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }
}
