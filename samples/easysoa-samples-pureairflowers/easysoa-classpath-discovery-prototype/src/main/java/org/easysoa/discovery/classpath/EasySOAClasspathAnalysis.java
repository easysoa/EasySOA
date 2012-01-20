/**
 * EasySOA Samples - PureAirFlowers
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

package org.easysoa.discovery.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOARemoteApiFactory;
import org.easysoa.doctypes.AppliImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasySOAClasspathAnalysis  {

    private final static Logger logger = LoggerFactory.getLogger(EasySOAClasspathAnalysis.class);

    private final static String PROPERTIES_FILENAME = "discovery.properties";
    private final static String PROP_LOGONLY = "discovery.logOnly";
    private final static String PROP_JARMATCHERS = "discovery.jarMatchers";
    private final static String PROP_USERNAME = "discovery.username";
    private final static String PROP_PASSWORD = "discovery.password";
    
    private static boolean init = false, initFailed = false;
    
    private static List<Pattern> matchers = new LinkedList<Pattern>();
    private static boolean logOnly;
    private static String username, password;
    private static String title = null;
    
    public static void setAppliImplTitle(String title) {
        EasySOAClasspathAnalysis.title = title;
    }
    
    public static synchronized void discover(String appliImplUrl) {

        if (initFailed) {
            return;
        }
        
        if (!init) {
            try {
                init();
            }
            catch (Exception e) {
                logger.error("Failed to initialize classpath discovery", e);
                initFailed = true;
                return;
            }
        }
        
        try {
            
            // Log in to Nuxeo
            EasySOAApiSession easySOA = EasySOARemoteApiFactory.createRemoteApi(username, password);
            
            // Fetch the classpath JARs list
            String[] classPath = System.getProperty("java.class.path").split(File.pathSeparator);
            
            // Extract the JAR names
            List<String> jarNames = new LinkedList<String>();
            for (String jar : classPath) {
                if (jar.endsWith(".jar")) {
                    String name = jar.substring(jar.lastIndexOf(File.separator) + 1, jar.length() - 4);
                    for (Pattern matcher : matchers) {
                        if (matcher.matcher(name).matches()) {
                            if (logOnly) {
                                logger.info("Found: " + name);
                            }
                            jarNames.add(name);
                            break;
                        }
                    }
                }
            }
            
            if (!logOnly) {
                // Build a notification for Nuxeo
                String data = "";
                for (String jarName : jarNames) {
                    data += jarName + '\n';
                }
                
                // Send the notification
                Map<String, String> params = new HashMap<String, String>();
                params.put(AppliImpl.PROP_URL, appliImplUrl);
                params.put(AppliImpl.PROP_DEPLOYABLES, data);
                if (title != null) {
                    params.put(AppliImpl.PROP_TITLE, title);
                }
                easySOA.notifyAppliImpl(params);
            }
            else {
                logger.info("Not sending a discovery notification, as defined in the settings");
            }
            
        } catch (IOException e) {
            logger.error("Failed to connect to the EasySOA registry, will not discover the classpath: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to discover the classpath", e);
        }
        
        System.exit(0); // XXX
    }
    

    
    private static void init() throws FileNotFoundException, IOException {

        Properties props = new Properties();
        
        // Load properties from configuration file
        String propsPath = null;
        if (new File(PROPERTIES_FILENAME).exists()) {
            propsPath = PROPERTIES_FILENAME;
        }
        else if (new File("target/classes/" + PROPERTIES_FILENAME).exists()) {
            propsPath = "target/classes/" + PROPERTIES_FILENAME;
        }
        if (propsPath != null) {
            props.load(new FileInputStream(propsPath));
        }
        else {
            logger.warn("No " + PROPERTIES_FILENAME + " found, using default config");
        }

        // Set discovery settings
        username = props.getProperty(PROP_USERNAME, "Administrator");
        password = props.getProperty(PROP_PASSWORD, "Administrator");
        logOnly = Boolean.parseBoolean(props.getProperty(PROP_LOGONLY, "false"));
        String matchersProp = props.getProperty(PROP_JARMATCHERS, ".*");
        String matchersStrings[] = matchersProp.split("\\|");
        for (String matcherString : matchersStrings) {
            try {
                matchers.add(Pattern.compile(matcherString.trim()));
            }
            catch (PatternSyntaxException e) {
                logger.warn("Invalid syntax for pattern '" + matcherString + "' : " + e.getMessage());
            }
        }
            
        init = true;
        
    }
    
}
