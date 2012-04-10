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

public class NxFraSCAtiApp implements EasySOAApp
{

    private final static String STARTING_METHOD = "NxFraSCAti";

    private String appPath;

    // private AppDescriptor appDescriptor;

    private FraSCAtiRegistryServiceBase fraSCAtiService; // TODO make it
                                                         // independent from
                                                         // nuxeo by
                                                         // reimplementing it
                                                         // also directly on top
                                                         // of FraSCAti

    /*
     * public NxFraSCAtiApp (AppDescriptor appDescriptor) { this.appDescriptor =
     * appDescriptor; }
     */

    public NxFraSCAtiApp(String appPath)
    {

        this.appPath = appPath;
    }

    //@Override
    public void start()
    {
        NxFraSCAtiRegistryService nxFraSCAtiRegistryService = Framework
                .getLocalService(NxFraSCAtiRegistryService.class);
        try
        {
            nxFraSCAtiRegistryService.getFraSCAti().processComposite(appPath);
        } catch (FraSCAtiServiceException e)
        {
            e.printStackTrace();
        }
    }

    //@Override
    public void stop()
    {

        // TODO stop compos
    }

    public String getAppPath()
    {

        return appPath;
    }

    public FraSCAtiRegistryServiceBase getFraSCAtiService()
    {

        return fraSCAtiService;
    }

    public String getAppId()
    {

        return STARTING_METHOD + ":" + appPath;
    }

}
