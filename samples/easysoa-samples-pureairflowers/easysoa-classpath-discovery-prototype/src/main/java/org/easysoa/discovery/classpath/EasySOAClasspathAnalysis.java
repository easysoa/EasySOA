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

public class EasySOAClasspathAnalysis implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(EasySOAClasspathAnalysis.class);

    private final static String PROPERTIES_FILENAME = "discovery.properties";

    private final static String PROP_NUXEOURL = "discovery.nuxeoUrl";
    private final static String PROP_USERNAME = "discovery.nuxeoUsername";
    private final static String PROP_PASSWORD = "discovery.nuxeoPassword";
    private final static String PROP_APPLIIMPLURL = "discovery.appliImplUrl";
    private final static String PROP_APPLIIMPLTITLE = "discovery.appliImplTitle";
    private final static String PROP_ENVIRONMENT = "discovery.environment";
    private final static String PROP_JARMATCHERS = "discovery.jarMatchers";
    private final static String PROP_LOGONLY = "discovery.logOnly";
   
    private static Object mutex = new Object();
    
    private boolean initFailed = false;
    private List<Pattern> matchers = new LinkedList<Pattern>();
    private boolean logOnly;
    private String nuxeoUrl = null;
    private String username = null, password = null, environment = null;
    private String appliImplUrl = null, appliImplTitle = null;
    
    public EasySOAClasspathAnalysis() {
        
        try {
            
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
            appliImplUrl = props.getProperty(PROP_APPLIIMPLURL);
            appliImplTitle = props.getProperty(PROP_APPLIIMPLTITLE);
            environment = props.getProperty(PROP_ENVIRONMENT, "Master");
            username = props.getProperty(PROP_USERNAME, "Administrator");
            password = props.getProperty(PROP_PASSWORD, "Administrator");
            nuxeoUrl = props.getProperty(PROP_NUXEOURL, "http://localhost:8080/nuxeo");
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
            
        }
        catch (Exception e) {
            logger.error("Failed to initialize classpath discovery", e);
            initFailed = true;
        }
    }

    public void setAppliImplUrl(String url) {
        this.appliImplUrl = url;
    }
    
    public void setAppliImplTitle(String title) {
        this.appliImplTitle = title;
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public synchronized void discover() {
        new Thread(this).start();
    }
    
    /**
     * Use discover() instead.
     */
    @Override
    public void run() {

        if (initFailed) {
            return;
        }
        
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

        boolean tryLater = false;
        
        do {
        
            synchronized (mutex) {
                
                try {
                    
                    // Log in to Nuxeo
                    EasySOAApiSession easySOA = EasySOARemoteApiFactory.createRemoteApi(nuxeoUrl + "/site", username, password);
                    
                    if (!logOnly) {
                        // Build a notification for Nuxeo
                        String data = "";
                        for (String jarName : jarNames) {
                            String deployableName = jarName.replaceAll("-[0-9.]+(-SNAPSHOT)?$", "");
                            String deployableVersion = jarName.replaceFirst(deployableName, "").substring(1);
                            data += deployableName + '|' + deployableName + '|' + deployableVersion + '\n';
                        }
                        
                        // Send the notification
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(AppliImpl.PROP_URL, appliImplUrl);
                        params.put(AppliImpl.PROP_DEPLOYABLES, data);
                        if (appliImplTitle != null) {
                            params.put(AppliImpl.PROP_TITLE, appliImplTitle);
                        }
                        if (environment != null) {
                            params.put(AppliImpl.PROP_ENVIRONMENT, environment);
                        }
                        easySOA.notifyAppliImpl(params);
                    }
                    else {
                        logger.info("Not sending a discovery notification, as defined in the settings");
                    }
                    
                    // stopping tries after successful notification
                    tryLater = false;
                    
                } catch (IOException e) {
                    logger.error("Failed to connect to the EasySOA registry: " + e.getMessage());
                    tryLater = true;
                } catch (Exception e) {
                    logger.error("Failed to discover the classpath", e);
                }
                
            }
        
            if (tryLater) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // Do nothing
                }
            }
        
        } while (tryLater);
        
    }
    
}
