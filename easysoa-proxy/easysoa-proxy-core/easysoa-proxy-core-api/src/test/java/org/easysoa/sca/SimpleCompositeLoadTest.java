/**
 * EasySOA Proxy
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

package org.easysoa.sca;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * Class to test the load of simple composite files (with no include tag) in FraSCAti
 * 
 * @author jguillemotte
 *
 */
public class SimpleCompositeLoadTest {

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(SimpleCompositeLoadTest.class);    

    /** The FraSCAti platform */
    protected static FraSCAti frascati;

    protected static ArrayList<Component> componentList;    
    
    @BeforeClass
    public static void setUp() throws FrascatiException {
        //logger.info("FraSCATI Starting");
        componentList =  new ArrayList<Component>();
        frascati = FraSCAti.newFraSCAti();
    }
    
    @Test
    public void loadLogEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("logEngine", new ProcessingContextImpl()));
    }
    
    @Test
    public void loadEsperEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("esperEngine", new ProcessingContextImpl()));
    }
    
    @Test
    public void loadStoreManager() throws FrascatiException {
        componentList.add(frascati.processComposite("storeManager", new ProcessingContextImpl()));
    }

    @Test
    public void loadExchangeNumberGeneratorComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("exchangeNumberGenerator", new ProcessingContextImpl()));
    }
    
    @Test
    public void loadCorrelatioEngineComposite() throws FrascatiException {
        componentList.add(frascati.processComposite("correlationEngine", new ProcessingContextImpl()));
    }
    
    @AfterClass
    public static void endTest() throws FrascatiException{
        for(Component component : componentList){
            frascati.close(component);            
        }
    }    
}
