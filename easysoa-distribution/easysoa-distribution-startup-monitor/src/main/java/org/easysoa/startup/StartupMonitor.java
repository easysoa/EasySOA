package org.easysoa.startup;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.easysoa.EasySOAConstants;

/**
 * 
 * @author mkalam-alami
 *
 */
public class StartupMonitor {
    
    private static final String EASYSOA_URL = "http://localhost:8083";
    
    private static final int STARTUP_TIMEOUT = 60000;
    
    public static void main(String[] args) {
        
        // Port list
        Map<Integer, String> portsToCheck = new ConcurrentHashMap<Integer, String>();
        portsToCheck.put(EasySOAConstants.WEB_PORT, "EasySOA home");
        portsToCheck.put(EasySOAConstants.NUXEO_PORT, "Service registry");
        portsToCheck.put(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT, "HTTP Monitoring proxy");
        portsToCheck.put(EasySOAConstants.HTML_FORM_GENERATOR_PORT, "UI Scaffolding proxy");
        portsToCheck.put(EasySOAConstants.PAF_SERVICES_PORT, "Pure Air Flowers demo");
        portsToCheck.put(EasySOAConstants.TRIP_SERVICES_PORT, "Smart Travel demo");
        portsToCheck.put(EasySOAConstants.TRIP_BACKUP_SERVICES_PORT, "Smart Travel demo (services backup)");
       
        // Start-up list
        print("* Servers to be started");
        for (Entry<Integer, String> entry : portsToCheck.entrySet()) {
            print(entry.getValue() + " (port " + entry.getKey() + ")");
        }
        
        // Check loop
        long initialTime = System.currentTimeMillis();
        int timeout = 200;
        int up = 0, total = portsToCheck.size();
        print("\n* Checking servers...");
        while (!portsToCheck.isEmpty() && System.currentTimeMillis() - initialTime < STARTUP_TIMEOUT) {
            for (Integer port : portsToCheck.keySet()) {
                if (isAvailable("http://localhost:" + port, timeout)) {
                    String label = portsToCheck.remove(port);
                    up++;
                    print(label + " is up... (" + up + "/" + total + ")");
                }
                if (System.currentTimeMillis() - initialTime > STARTUP_TIMEOUT) {
                    break;
                }
            }
            timeout += 500;
        }

        // Feedback
        if (portsToCheck.isEmpty()) {
            print("Everything is ready!");
        }
        else {
            print("...Something must be wrong, but I'll make you browse to the EasySOA home anyway.");
        }
        
        // Open browser
        try {
            URI easysoaUri = new URI(EASYSOA_URL);
            Desktop.getDesktop().browse(easysoaUri);
        } catch (Exception e) {
            print("\nPlease browse to '" + EASYSOA_URL +"' to get started with the demo.");
        }
        
    }

    /**
     * 
     * @param url
     * @param timeout in ms
     * @return
     */
    public static boolean isAvailable(String url, int timeout) {
        
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
        } catch (IOException e) {
            return false;
        }
        
        return true;
        
    }
    
    public static void print(String string) {
        System.out.println(string);
    }
    
}
