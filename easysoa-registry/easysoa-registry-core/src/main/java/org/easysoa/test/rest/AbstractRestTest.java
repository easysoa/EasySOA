/**
 * EasySOA Registry
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

package org.easysoa.test.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.rest.RestNotificationFactory;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Class to be extended for the creation of REST APIs test cases.
 * @author mkalam-alami
 *
 */
public abstract class AbstractRestTest {

    protected static boolean useEmbeddedNuxeo;

    private final static String AUTH_USERNAME = "Administrator"; // XXX: Hard-coded
    private final static String AUTH_PASSWORD = "Administrator"; // XXX: Hard-coded
    
    private static Log log = LogFactory.getLog(AbstractRestTest.class);
    private static AutomationHelper automation = null;
    private static RestNotificationFactory notificationFactory = null;
    private static Object notificationFactorySync = new Object();
    
    protected static void setUp(CoreSession session, String targetedNuxeoPropsPath) throws Exception {
        
        synchronized (notificationFactorySync) {
        
        // Run only once
        if (notificationFactory != null) {
            return;
        }
        
        // Load properties
        /**
         * Nuxeo props file exemple:
            "useExistingNuxeo = true
            existingNuxeoHost = localhost
            existingNuxeoPort = 8080
            embeddedNuxeoPort = 9980" 
         */
        FileInputStream isProps = new FileInputStream(targetedNuxeoPropsPath);
        Properties props = new Properties();
        try {
            props.load(isProps);
        }
        finally {
            isProps.close();
        }
        
        // Read properties
        String externalNuxeoHost = props.getProperty("externalNuxeoHost");
        String externalNuxeoPort = props.getProperty("externalNuxeoPort");
        String embeddedNuxeoPort = props.getProperty("embeddedNuxeoPort");
        
        String useEmbeddedNuxeoString = props.getProperty("useEmbeddedNuxeo");
        if (useEmbeddedNuxeoString != null && useEmbeddedNuxeoString.equals("false")) {
            log.info("Running test on external Nuxeo");
            useEmbeddedNuxeo = false;
        }
        else {
            log.info("Running test on embedded Nuxeo");
            useEmbeddedNuxeo = true;
        }
        
        // Create testing objects
        String nuxeoUrl = null;
        if (useEmbeddedNuxeo) {
            nuxeoUrl = "http://localhost:"+embeddedNuxeoPort;
        }
        else {
            if (externalNuxeoHost == null || externalNuxeoPort == null) {
                log.warn("Invalid Nuxeo location, using default: "+
                        RestNotificationFactory.NUXEO_URL_LOCALHOST);
                nuxeoUrl = RestNotificationFactory.NUXEO_URL_LOCALHOST;
            }
            else {
                nuxeoUrl = "http://"+externalNuxeoHost+":"+externalNuxeoPort+"/nuxeo/site";
            }
            // Could also be deployed with the existing Nuxeo,
            // if the automation bundles were correctly deployed & configured.
            automation = new AutomationHelper(nuxeoUrl+"/automation",
                    AUTH_USERNAME, AUTH_PASSWORD);
        }
        notificationFactory = new RestNotificationFactory(nuxeoUrl);

        }
    }
    
    protected static AutomationHelper getAutomationHelper() {
        return automation;
    }
    
    protected static RestNotificationFactory getRestNotificationFactory() {
        return notificationFactory;
    }

    protected void assertDocumentExists(CoreSession session, String doctype, String url) throws Exception {
        if (useEmbeddedNuxeo && session != null) {
            DocumentService docService = Framework.getService(DocumentService.class);
            DocumentModel model = null;
            // TODO DocumentService refactoring
            if (AppliImpl.DOCTYPE.equals(doctype)) {
                model = docService.findAppliImpl(session, url);
            }
            else if (ServiceAPI.DOCTYPE.equals(doctype)) {
                model = docService.findServiceApi(session, url);
            }
            else if (Service.DOCTYPE.equals(doctype)) {
                model = docService.findService(session, url);
            }
            else if (ServiceReference.DOCTYPE.equals(doctype)) {
                model = docService.findServiceReference(session, url);
            }
            assertNotNull(model);
        }
        else if (automation != null) {
            assertFalse(automation.findDocumentByUrl(doctype, url).isEmpty());
        }
        else {
            fail("Cannot access repository");
        }
    }
}
