/**
 * EasySOA Samples - AXXX
 * Copyright 2011-2012 Open Wide
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

package com.axxx.dps.apv.ws;

import java.io.IOException;

import javax.xml.soap.SOAPException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.axxx.dps.apv.ws.PrecomptePartenaire;
import com.axxx.dps.apv.ws.PrecomptePartenaireWebService;

/**
 * 
 * 
 * @author mdutoo
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:axxx-dps-apv-test-context.xml" })
public class PrecomptePartenaireWebServiceTest {

    /**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(PrecomptePartenaireWebServiceTestStarter.class.getClass());
    
    @Autowired
    @Qualifier("com.axxx.dps.apv.ws.PrecomptePartenaireWebServiceTestClient")
    private PrecomptePartenaireWebService precomptePartenaireWebService;

	public void setProxyLocator(PrecomptePartenaireWebService precomptePartenaireWebService) {
        this.precomptePartenaireWebService = precomptePartenaireWebService;
    }	
	
	/**
	 * 
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void setUp() throws InterruptedException {
		
	}

    @Test
	public void testCreerPrecompte() {
    	PrecomptePartenaire precomptePartenaire = new PrecomptePartenaire();
    	precomptePartenaire.setIdentifiantClientPivotal("0x000E0006A00900C0");
    	precomptePartenaire.setNomStructure("ANECD");
    	precomptePartenaire.setTypeStructure(TypeStructure.ASSOCIATION_NAT);
    	precomptePartenaire.setAdresse("Carl-Metz-Str. 3");
    	precomptePartenaire.setCp("76185");
    	precomptePartenaire.setVille("Karlsruhe");
    	precomptePartenaire.setApeNaf("512E");
    	precomptePartenaire.setSirenSiret("");
		this.precomptePartenaireWebService.creerPrecompte(precomptePartenaire);
	}
	
	/**
	 * Wait for an user action to stop the test 
	 * @throws ClientException
	 * @throws SOAPException
	 * @throws IOException
	 */
	//@Test
	public final void testWaitUntilRead() throws Exception{
		logger.info("PrecomptePartenaireWebServiceImpl test started, wait for user action to stop");
		// Just push a key in the console window to stop the test
		System.in.read();
		logger.info("PrecomptePartenaireWebServiceImpl test stopped.");
	}	
}
