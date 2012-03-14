package org.easysoa.startup;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
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
    
    private static final String EASYSOA_URL = "http://localhost:8083/easysoa";
    private static final int STARTUP_TIMEOUT = 90000;
    private static final String[] BROWSERS = { "firefox", "google-chrome", "opera",
       "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" };
    
    public static void main(String[] args) {
        
        // Port list
        Map<Integer, String> portsToCheck = new ConcurrentHashMap<Integer, String>();
        portsToCheck.put(EasySOAConstants.WEB_PORT, "EasySOA home");
        portsToCheck.put(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT, "HTTP Monitoring proxy");
        portsToCheck.put(EasySOAConstants.HTML_FORM_GENERATOR_PORT, "UI Scaffolding proxy");
        portsToCheck.put(EasySOAConstants.PAF_SERVICES_PORT, "Pure Air Flowers demo");
        portsToCheck.put(EasySOAConstants.TRIP_SERVICES_PORT, "Smart Travel demo");
        portsToCheck.put(EasySOAConstants.TRIP_BACKUP_SERVICES_PORT, "Smart Travel demo (services backup)");
        portsToCheck.put(EasySOAConstants.NUXEO_PORT, "Service registry");
       
        // Start-up list
        print("* Servers to be started");
        for (Entry<Integer, String> entry : portsToCheck.entrySet()) {
            print(entry.getValue() + " (port " + entry.getKey() + ")");
        }
        
        // Check loop
        long initialTime = System.currentTimeMillis();
        int up = 0, total = portsToCheck.size();
        print("\n* Checking servers...");
        while (!portsToCheck.isEmpty() && System.currentTimeMillis() - initialTime < STARTUP_TIMEOUT) {
            for (Integer port : portsToCheck.keySet()) {
                if (isAvailable("http://localhost:" + port, 500, port == EasySOAConstants.NUXEO_PORT)) {
                    String label = portsToCheck.remove(port);
                    up++;
                    print(label + " is up... (" + up + "/" + total + ")");
                }
                if (System.currentTimeMillis() - initialTime > STARTUP_TIMEOUT) {
                    break;
                }
                trySleep(300);
            }
        }

        // Feedback
        if (portsToCheck.isEmpty()) {
            print("Everything is ready!");
        }
        else {
            print("Everything's not ready yet, but let's get started anyway.");
            trySleep(1500);
        }
        
        // Open browser
        try {
            openBrowser();
        } catch (Exception e) {
            print("\nPlease browse to '" + EASYSOA_URL +"' to get started with the demo.");
        }
        
    }

    private static void trySleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            // Nothing
        }
    }
    
    /**
     * 
     * @param url
     * @param timeout in ms
     * @return
     */
    public static boolean isAvailable(String url, int timeout, boolean expectRequestSuccess) {
        
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            connection.connect();
            InputStream is = connection.getInputStream();
            is.close();
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            return !expectRequestSuccess;
        }
        
        return true;
        
    }
    
    public static void print(String string) {
        System.out.println(string);
    }

    /**
     * Opens the specified web page in the user's default browser
     * (Desktop.browse() doesn't work correctly with Ubuntu 11.10)
     * @throws Exception 
     */
    public static void openBrowser() throws Exception {

        // Source: http://www.centerkey.com/java/browser/
        
        String url = EASYSOA_URL;

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            Class.forName("com.apple.eio.FileManager")
                    .getDeclaredMethod("openURL", new Class[] { String.class })
                    .invoke(null, new Object[] { url });
        } else if (osName.startsWith("Windows"))
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        else { // assume Unix or Linux
            String browser = null;
            for (String b : BROWSERS) {
                if (browser == null &&  Runtime.getRuntime().exec(
                        new String[] { "which", b }).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[] { browser = b, url });
                }
            }
            if (browser == null) {
                throw new Exception();
            }
        }
    }

}
