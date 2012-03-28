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
