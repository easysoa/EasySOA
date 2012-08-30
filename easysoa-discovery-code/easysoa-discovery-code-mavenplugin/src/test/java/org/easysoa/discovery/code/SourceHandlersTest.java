package org.easysoa.discovery.code;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.easysoa.discovery.code.handler.JaxRSSourcesHandler;
import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.discovery.code.handler.SourcesHandler;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.junit.BeforeClass;
import org.junit.Test;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * 
 * @author mkalam-alami
 *
 */
public class SourceHandlersTest {

    private static Logger logger = Logger.getLogger(SourceHandlersTest.class);
    
    private static final Map<String, SourcesHandler> availableHandlers = new HashMap<String, SourcesHandler>();
    
    @BeforeClass
    public static void setUp() {
        availableHandlers.put("JAX-WS", new JaxWSSourcesHandler());
        availableHandlers.put("JAX-RS", new JaxRSSourcesHandler());
    }
    
    @Test
    public void testSourceHandlers() throws Exception {
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File("../easysoa-discovery-code-sample"));
        JavaSource[] sources = builder.getSources();
        
        MavenDeliverableInformation mavenDeliverable = new MavenDeliverableInformation("TestDeliverable");
        List<SoaNodeInformation> soaNodeResults = runHandlers(sources, mavenDeliverable, new SystemStreamLog());
        
        Assert.assertTrue("SoaNodes must have been found during discovery", soaNodeResults.size() > 0);

        logger.info("Found SoaNodes");
        logger.info("--------------");
        for (SoaNodeInformation soaNode : soaNodeResults) {
            logger.info("   > ID = " + soaNode.getSoaNodeId());
            logger.info("     Properties =  " + soaNode.getProperties());
            logger.info("     Parents =  " + soaNode.getParentDocuments());
        }
    }
    
    private List<SoaNodeInformation> runHandlers(JavaSource[] sources, 
            MavenDeliverableInformation mavenDeliverable, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        for (SourcesHandler handler : availableHandlers.values()) {
            discoveredNodes.addAll(handler.handleSources(sources, mavenDeliverable, log));
        }
        return discoveredNodes;
    }

}
