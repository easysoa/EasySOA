package org.easysoa.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOADocument;
import org.easysoa.api.EasySOARemoteApiFactory;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class BuildPropertyFileExample {

    /**
     * Lists a list of all services in a proprties file
     */
    public static void main(String[] args) throws Exception {

        // Request service list

        EasySOAApiSession api = EasySOARemoteApiFactory.createRemoteApi("Administrator", "Administrator");
        List<EasySOADocument> documents = api.queryDocuments("SELECT * FROM Service");

        // Fill properties

        Properties properties = new Properties();
        for (EasySOADocument doc : documents) {
            properties.setProperty(doc.getTitle().replace(' ', '_'), doc.getPropertyAsString("serv:url"));
        }
        
        // Store in file

        File propertieFile = new File("target/out.properties");
        FileOutputStream fos = new FileOutputStream(propertieFile);
        properties.store(fos, "EasySOA Services list");
        fos.close();

        // Display file contents

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(propertieFile)));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        reader.close();
        
        /**
         * Example output:
         * ---------------------
         * #EasySOA Services list
         * #Thu Nov 10 11:18:31 CET 2011
         * My_Service=http\://www.mysite.com/myapi/myservice
         * Other_Service_3=http\://www.myservices.com/service3
         * Other_Service_2=http\://www.myservices.com/service2
         * Other_Service_1=http\://www.myservices.com/service1
         * Orders_count=http\://127.0.0.1\:9010/PureAirFlowers
         * ---------------------
         */

    }
    
}