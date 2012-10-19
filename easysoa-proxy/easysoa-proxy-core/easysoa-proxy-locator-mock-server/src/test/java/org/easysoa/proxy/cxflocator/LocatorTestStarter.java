package org.easysoa.proxy.cxflocator;
/**
 * EasySOA Samples - Smart Travel
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

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.schemas.esb.locator._2011._11.BindingType;
import org.talend.schemas.esb.locator._2011._11.SLPropertiesType;
import org.talend.schemas.esb.locator._2011._11.TransportType;
import org.talend.services.esb.locator.v1.InterruptedExceptionFault;
import org.talend.services.esb.locator.v1.LocatorService;
import org.talend.services.esb.locator.v1.ServiceLocatorFault;

/**
 * 
 * @author jguillemotte
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:cxf.xml" }) // easysoa-proxy-cxflocator-test-context.xml
public class LocatorTestStarter {
    
    @Autowired
    @Qualifier("org.easysoa.proxy.cxflocator.discoveryProxyLocatorServiceTestClient")
    private LocatorService proxyLocator;

    //private LocatorService talendLocator;

	public void setProxyLocator(LocatorService proxyLocator) {
        this.proxyLocator = proxyLocator;
    }

    /**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(LocatorTestStarter.class.getClass());	
	
	/**
	 * 
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void setUp() throws InterruptedException {
		// Start locator mock test
		//startLocatorMockComposite();
	}

    @Test
	public void testProxyLocator() throws ServiceLocatorFault, InterruptedExceptionFault {
	    TransportType transport = null;
        BindingType binding = null;
        SLPropertiesType props = null;
        QName a = QName.valueOf("{http://www.axxx.com/dps/apv}TdrWebService");
        this.proxyLocator.registerEndpoint(a, "http://localhost:8080/cxf/TdrWebService",
	            binding, transport, props);
	}
	
	/**
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	//@Test
	public final void testWaitUntilRead() throws Exception{
		logger.info("Locator mock started, wait for user action to stop !");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("Locator mock test stopped !");
	}	
}
