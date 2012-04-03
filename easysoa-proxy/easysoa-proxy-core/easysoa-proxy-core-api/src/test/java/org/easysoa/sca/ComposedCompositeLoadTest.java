/**
 * 
 */
package org.easysoa.sca;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * CLass to test composed composite files in FraSCAti (with include tags)
 * 
 * @author jguillemotte
 *
 */
public class ComposedCompositeLoadTest {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(ComposedCompositeLoadTest.class);    

    /** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;    
    
    @Before
    public void setUp() throws FrascatiException {
        //logger.info("FraSCATI Starting");
        componentList =  new ArrayList<Component>();
        frascati = FraSCAti.newFraSCAti();
    }
    
    @Test
    public void loadMonitoringServiceComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("monitoringService", new ProcessingContextImpl()));
    }
    
    @Test
    public void loadTemplateEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("templateEngine", new ProcessingContextImpl()));
    }

    @Test
    public void loadAssertionEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("assertionEngine", new ProcessingContextImpl()));
    }    
    
    @Test
    public void loadReplayEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("replayEngine", new ProcessingContextImpl()));
    }    

    @Test
    public void loadRunManagerComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("runManager", new ProcessingContextImpl()));
    }    
    
    @Test
    public void loadSimulationEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("simulationEngine", new ProcessingContextImpl()));
    }
    
    @After
    public void endTest() throws FrascatiException{
        for(Component component : componentList){
            frascati.close(component);            
        }
    }

}
