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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private final static File[] PROPERTIES_LOCATIONS = new File[] {
        new File(PROPERTIES_FILENAME),
        new File("target/classes/" + PROPERTIES_FILENAME)
    };

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
        // Extract default property file from the JAR
        if (!new File(PROPERTIES_FILENAME).exists()) {
            extractDiscoveryPropertyFile("/" + PROPERTIES_FILENAME, PROPERTIES_FILENAME);
        }
        
        try {
            
            // Load properties from configuration file, otherwise from jar resource
            Properties props = new Properties();
            File propsFile = null;
            for (File propsFileCandidate : PROPERTIES_LOCATIONS) {
                logger.error(propsFileCandidate.getAbsolutePath());
                if (propsFileCandidate.exists()) {
                    propsFile = propsFileCandidate;
                    break;
                }
            }
            if (propsFile != null) {
                InputStream propsIs = new FileInputStream(propsFile);
                try {
                    props.load(propsIs);
                }
                finally {
                    propsIs.close();
                }
            }
            else {
                logger.warn("No " + PROPERTIES_FILENAME + " found, using default config");
            }
            
            // Set discovery settings
            
            appliImplUrl = props.getProperty(PROP_APPLIIMPLURL, "http://unknown");
            appliImplTitle = props.getProperty(PROP_APPLIIMPLTITLE, "Unknown application");
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

    /**
     * Extract discovery property file
     */
    private void extractDiscoveryPropertyFile(String fromResource, String toPath) {
        InputStream propertiesFileStream = null;
        FileOutputStream fos = null;
        try {
            propertiesFileStream = this.getClass().getResourceAsStream(fromResource);
            if (propertiesFileStream != null) {
                fos = new FileOutputStream(toPath);
                int buffer;
                while ((buffer = propertiesFileStream.read()) != -1) {
                    fos.write(buffer);
                }
            }
        } catch (Exception e) {
            logger.error("Init error", e);
        }
        finally {
            try {
                if (propertiesFileStream != null) {
                    propertiesFileStream.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (IOException e) {
                logger.error("Init error", e);
            }
        }
    }
    
}
