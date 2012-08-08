package org.easysoa.proxy.handler.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.exchangehandler.HandlerManagerImpl;
import org.easysoa.proxy.handler.event.admin.CompiledCondition;
import org.easysoa.proxy.handler.event.admin.IEventMessageHandler;
import org.easysoa.proxy.handler.event.admin.RegexCondition;
import org.easysoa.proxy.test.HttpUtils;
import org.easysoa.test.util.AbstractProxyTestStarter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ow2.frascati.util.FrascatiException;

/**
 *  Prerequisite:
 *  Pure Air Flowers, Smart Travel launched.
 *  Then  will: send a paf soap request
 *  This for allow the HandlerManager to handle the paf request and call the fake paf (Glue).
 *  If the order is more than 10, it will get some travel places to the Smart Travel.
 * @author fntangke
 *
 */
public class EventPafSampleGlueStarter extends AbstractProxyTestStarter{

    /**
     * Logger
     */
    private Logger logger = Logger.getLogger(HandlerManagerImpl.class.getName());    
    /**
     * Initialize one time the remote systems for the test
     * FraSCAti and HTTP discovery Proxy ...
     * @throws Exception 
     */
    @Before
    public void setUp() throws Exception {
        logger.info("Launching FraSCAti and REST mock");
        // Start fraSCAti
        startFraSCAti();
        // Start HTTP Proxy
        startHttpDiscoveryProxy("eventPafSample.composite"); //startGalaxyDemo
    }


    /**
     * Stop FraSCAti components and cleans jett (?)
     * @throws FrascatiException
     */
    @After
    public void tearDown() throws Exception{
        logger.info("Stopping FraSCAti...");
        stopFraSCAti();
        cleanJetty(9000); // GalaxyTrip
        cleanJetty(9080); // CreateSummary
    }
    
    
    
    
    /**
     * For test PAF, The Glue...
     * @throws ClientProtocolException
     * @throws IOException
     * @throws FrascatiException 
     * @throws FraSCAtiServiceException 
     */
    
    @Test
    public void testTheScenario() throws ClientProtocolException, IOException, FrascatiException{        
        
        String urlToListen = "http://localhost:9010/PureAirFlowers";        

        Map<List<CompiledCondition>, List<String>> listenedServiceUrlToServicesToLaunchUrlMap = new HashMap<List<CompiledCondition>, List<String>>() ;
        ArrayList<String> value = new ArrayList<String>();
        value.add("http://localhost:8090/glue");
        
        List<CompiledCondition> listCompiledCondition = new ArrayList<CompiledCondition>();
        listCompiledCondition.add(new RegexCondition(urlToListen));
	listenedServiceUrlToServicesToLaunchUrlMap.put(listCompiledCondition, value);
        
        listenedServiceUrlToServicesToLaunchUrlMap.put(listCompiledCondition, value);
        frascati.getService(componentList.get(0), "IEventMessageHandler", IEventMessageHandler.class)
            .setListenedServiceUrlToServicesToLaunchUrlMap(listenedServiceUrlToServicesToLaunchUrlMap);
        
        //String verif = frascati.getService("servicesToLaunchMock", "IEventMessageHandler", IEventMessageHandler.class).getListenedServiceUrlToServicesToLaunchUrlMap().toString();

        HttpUtils httputils = new HttpUtils();
        String res = httputils.doPostSoap("http://localhost:9010/PureAirFlowers?wsdl",
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body>" +
                "<paf:addOrder xmlns:paf=\"http://paf.samples.easysoa.org/\"><orderNb>3</orderNb><ClientName>me</ClientName></paf:addOrder>" +
                "</soap:Body></soap:Envelope>");
        System.out.println(res);
        System.in.read();
    }

    
    /**
     * Clean Frascati 
     * @throws Exception
     */
    @After
    public void cleanUp() throws Exception{
        logger.info("Stopping FraSCAti...");
        stopFraSCAti();
        // Clean Jetty for twitter mock
        cleanJetty(EasySOAConstants.TWITTER_MOCK_PORT);
        // Clean Jetty for meteo mock
        cleanJetty(EasySOAConstants.METEO_MOCK_PORT);
        // Clean Jetty for Nuxeo mock
        cleanJetty(EasySOAConstants.NUXEO_TEST_PORT);
        // Clean Easysoa proxy
        cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT);
        cleanJetty(EasySOAConstants.HTTP_DISCOVERY_PROXY_DRIVER_PORT);
        cleanJetty(EasySOAConstants.EXCHANGE_RECORD_REPLAY_SERVICE_PORT);
    }
}
