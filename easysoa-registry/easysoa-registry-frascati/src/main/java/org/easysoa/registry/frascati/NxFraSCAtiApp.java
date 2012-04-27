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

package org.easysoa.registry.frascati;

//import org.easysoa.app.AppDescriptor;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * What to do with this class ?
 * Do we have to update this class to work with the new FraSCAti Nuxeo embedded architecture ?
 * 
 * Think it is better to have a independent EasysoaApp class that can be used in remote or in embedded frascati
 *
 */
public class NxFraSCAtiApp implements EasySOAApp {

    // Starting method : in this case corresponding to Nuxeo FraSCAti method
    private final static String STARTING_METHOD = "NxFraSCAti";

    // Application composite path
    private String appPath;

    // private AppDescriptor appDescriptor;

    //private FraSCAtiRegistryServiceBase fraSCAtiService; // TODO make it
                                                         // independent from
                                                         // nuxeo by
                                                         // reimplementing it
                                                         // also directly on top
                                                         // of FraSCAti

    /*
     * public NxFraSCAtiApp (AppDescriptor appDescriptor) { this.appDescriptor =
     * appDescriptor; }
     */

    /**
     * Constructor
     * @param appPath The application composite path
     */
    public NxFraSCAtiApp(String appPath) {
        this.appPath = appPath;
    }

    /**
     * Start the composite
     */
    // TODO : do not call the Nuxeo framework in this method
    // 
    public void start() {
        NxFraSCAtiRegistryService nxFraSCAtiRegistryService = Framework.getLocalService(NxFraSCAtiRegistryService.class);
        try {
            nxFraSCAtiRegistryService.getFraSCAti().processComposite(appPath);
        } catch (FraSCAtiServiceException e) {
            e.printStackTrace();
        }
    }

    // @Override
    public void stop() {
        // TODO stop compos
    }

    /**
     * 
     * @return
     */
    public String getAppPath() {
        return appPath;
    }

    /**
     * 
     * @return
     */
    /*public FraSCAtiRegistryServiceBase getFraSCAtiService() {
        //return fraSCAtiService;
        return null;
    }*/

    /**
     * Returns the application ID
     * @return The application ID
     */
    public String getAppId() {
        return STARTING_METHOD + ":" + appPath;
    }

}
