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

package org.easysoa.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.easysoa.rest.AutomationHelper;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

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

        AutomationHelper automationHelper = new AutomationHelper(
                "http://localhost:8080/nuxeo/site/automation","Administrator", "Administrator");
        Documents documents = automationHelper.query("SELECT * FROM Service");

        // Fill properties

        Properties properties = new Properties();
        for (Document doc : documents) {
            properties.setProperty(doc.getTitle().replace(' ', '_'), doc.getString("serv:url"));
        }
        
        // Store in file

        File propertieFile = new File("out.properties");
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

    }
    
}