/**
 * EasySOA - Nuxeo FraSCAti
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
package org.nuxeo.frascati.factory;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nuxeo.common.Environment;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.frascati.NuxeoFraSCAti;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.bridge.Application;
import org.nuxeo.runtime.bridge.ApplicationDescriptor;
import org.nuxeo.runtime.bridge.ApplicationFactory;

/**
 * The FraSCAtiFactory is used by the Nuxeo runtime bridge to build an isolated
 * FraSCAti instance
 */
public class FraSCAtiFactory implements ApplicationFactory
{

    // Logger
    private static Logger log = Logger.getLogger(FraSCAtiFactory.class
            .getCanonicalName());

    private static final String FRASCATI_OUTPUT_DIRECTORY_PROPERTY = "org.ow2.frascati.output.directory";

    /*
     * (non-Javadoc)
     * 
     * @see org.nuxeo.runtime.bridge.ApplicationFactory
     * #createApplication(org.nuxeo.runtime.bridge.ApplicationDescriptor)
     */
    @Override
    public Application createApplication(ApplicationDescriptor desc)
            throws Exception
    {
        char sep = File.separatorChar;
        URLClassLoader cl = (URLClassLoader) Thread.currentThread()
                .getContextClassLoader();

        log.log(Level.INFO, "ContextClassLoader found : " + cl);
        String home = Environment.getDefault().getHome().getAbsolutePath();
        log.log(Level.INFO, "Frascati home dir : " + home);

        String outputDir = new StringBuilder(home).append(sep)
                .append("tmp").toString();
        System.setProperty(FRASCATI_OUTPUT_DIRECTORY_PROPERTY, outputDir);

        log.log(Level.INFO, "Define FraSCAti default output dir : "
                + outputDir);
        String propertyBootFilePath = new StringBuilder(home).append(sep)
                .append("config").append(sep)
                .append("frascati_boot.properties").toString();
        log.log(Level.INFO, "Read frascati_boot.properties file at "
                + propertyBootFilePath);
        try
        {
            Properties props = new Properties();
            props.loadFromXML(new FileInputStream(new File(
                    propertyBootFilePath)));

            Enumeration<Object> keys = props.keys();
            while (keys.hasMoreElements())
            {
                String key = (String) keys.nextElement();
                String value = props.getProperty(key);
                System.setProperty(key, value);
            }
        } catch (Exception e)
        {
            log.log(Level.INFO, "no boot properties found");
        }
        URL[] urls = cl.getURLs();
        if (urls == null || urls.length == 0)
        {
            log.log(Level.WARNING,
                    "No classpath entry found for IsolatedClassLoader");
        } else if (log.getLevel() == Level.CONFIG)
        {
            for (URL url : urls)
            {
                log.log(Level.INFO,
                        "Added classpath entry :" + url.toExternalForm());
            }
        }
        if (desc != null)
        {
            log.log(Level.INFO,
                    "ApplicationDescriptor found - required isolated status : "
                            + desc.isIsolated());
        } else
        {
            log.log(Level.WARNING, "No ApplicationDescriptor found");
        }
        
        NuxeoFraSCAti service = new NuxeoFraSCAti();
        
        // Notify Nuxeo that NuxeoFraSCAti has been launched
        EventProducer eventProducer = Framework.getService(EventProducer.class);
        eventProducer.fireEvent(new NuxeoFraSCAtiStartedEvent(service.getFraSCAtiService()));
        
        return service;
    }
}
