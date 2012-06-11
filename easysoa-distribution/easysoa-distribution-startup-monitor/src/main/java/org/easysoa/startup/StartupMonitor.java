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
    
    public static void main(String[] args) throws IOException {
        
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
                   
                    // Launch easysoa-web when Nuxeo is ready
                   /* if (port == EasySOAConstants.NUXEO_PORT) {
                        launchEasySOAWeb();
                    }*/
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
            BrowserHelper.openBrowser(EASYSOA_URL);
        } catch (Exception e) {
            print("\nPlease browse to '" + EASYSOA_URL +"' to get started with the demo.");
        }
        
    }
    
    /*private static void launchEasySOAWeb() throws IOException {
        String command = "./web/start-web.sh";
        if ("Windows".equals(System.getProperty("os.name"))) {
            command = windowsify(command);
        }
        ProcessBuilder pb = new ProcessBuilder(command);
        
        if (System.getProperty("user.dir").endsWith("easysoa-distribution-startup-monitor")) {
            pb.directory(new File(System.getProperty("user.dir").replace("easysoa-distribution-startup-monitor", "easysoa")));
        }
        
        Process start;
        try {
            start = pb.start();
            Thread.sleep(1000);
            BufferedReader bufferedReader = new java.io.BufferedReader(new java.io.InputStreamReader(start.getInputStream()));
            while (bufferedReader.ready()) {
                print(bufferedReader.readLine());
            }
        } catch (Exception e) {
            throw new IOException("Failed to start easysoa-web (command: " + command + ")", e);
        }
    }

    private static String windowsify(String command) {
        String result = command.replace(".sh", ".bat");
        result = result.replaceFirst("\\.\\/", "");
        return result;
    }*/

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


}
