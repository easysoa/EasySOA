/**
 *
 */
package org.easysoa.proxy.management;

import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.configuration.HttpProxyConfigurationService;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.management.HttpProxyManagementService;
import org.easysoa.proxy.strategy.EasySOAGeneratedAppIdFactoryStrategy;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Proxy management service implementation, that is only able to manage a singleton HTTPProxy instance
 *
 * Sample body request content :
 *
 * {
 *   "proxyConfiguration" : {
 *     "parameters" : [
 *       {
 *         "key":"user",
 *         "value":"Admin"
 *       },
 *       {
 *         "key":"projectId",
 *         "value":"123456"
 *       }
 *     ]
 *   }
 * }
 *
 * @author jguillemotte
 *
 */
@Scope("Composite")
public class HttpProxyManagementServiceImpl implements HttpProxyManagementService, HttpProxyConfigurationService {

    // TODO : Use a singleton : the default proxy instance started in FraSCAti

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(HttpProxyManagementServiceImpl.class.getName());

    @Reference
    EasySOAGeneratedAppIdFactoryStrategy embeddedEasySOAGeneratedAppIdFactoryStrategy;

    @Reference
    HttpProxyConfigurationService defaultHttpProxyInstanceConfigurationService;
    // NB. FraSCAti Studio HttpProxyManagementServiceImpl should have something like :
    /*@Reference
    List<HttpProxyConfigurationService> appInstanceConfigurationServices; // EasySOAGeneratedAppConfigurationService*/

    /**
     * Empty constructor for debug
     */
    public HttpProxyManagementServiceImpl(){
        logger.debug("Passing in ProxyInfoServiceImpl constructor !");
    }

    // TODO reset idem
    public void update(ProxyConfiguration configuration) throws Exception {
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            appInstanceConfService.update(configuration);
        } else {
            throw new Exception("No app with id" + configuration.getId());
        }
    }

    public void reset(ProxyConfiguration configuration) throws Exception {
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            appInstanceConfService.reset(configuration);
        } else {
            throw new Exception("No app with id" + configuration.getId());
        }
    }

    public ProxyConfiguration get(ProxyConfiguration configuration) throws Exception {
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            return appInstanceConfService.get(configuration);
        }
        return null;
    }

    // local only, not remote !
    public HttpProxyConfigurationService getConfigurationService(ProxyConfiguration configuration) throws Exception {
        String appID = configuration.getId();
        HttpProxyConfigurationService appInstanceConfService = defaultHttpProxyInstanceConfigurationService;
        if (appInstanceConfService != null) {
            if (appID.equals(appInstanceConfService.get(configuration).getId())) {
                // there is one
                return appInstanceConfService;
            }
        }
        return null;
        // NB. FraSCAti Studio Instanciating registry impl should do something like :
        /*///appInstance = appInstances.get(appId);
        HttpProxyConfigurationService appInstanceConfService = null;
        for (HttpProxyConfigurationService curAppInstanceConfigurationService : appInstanceConfigurationServices) {
            if (appID.equals(curAppInstanceConfigurationService.get().getId())) {
                appInstanceConfService = curAppInstanceConfigurationService
                break;
            }
        }
        return appInstanceConfService;*/
    }

    /**
     * Returns the proxy with the provided configuration
     * @param configuration The proxy configuration
     * @return <code>ProxyInfo</code> containing informations about instanced proxy
     */
    public ProxyConfiguration getHttpProxy(ProxyConfiguration configuration) throws Exception {

        // Generate the App ID form the configuration
        //EmbeddedEasySOAGeneratedAppIdFactoryStrategy idFactory = new EmbeddedEasySOAGeneratedAppIdFactoryStrategy(); // TODO inject & PerUser
        String appID = embeddedEasySOAGeneratedAppIdFactoryStrategy.getId(configuration.getParameter(ProxyConfiguration.USER_PARAM_NAME),
                configuration.getParameter(ProxyConfiguration.PROJECTID_PARAM_NAME),
                configuration.getParameter(ProxyConfiguration.COMPONENTID_PARAM_NAME));

        // Set proxy informations
        configuration.setName("EasySOA embedded proxy");
        // Generate ID
        configuration.setId(appID);

        // Complete the configuration
        configuration.addParameter(ProxyConfiguration.PROXY_PORT_PARAM_NAME, String.valueOf(EasySOAConstants.HTTP_DISCOVERY_PROXY_PORT));
        configuration.addParameter(ProxyConfiguration.PROXY_PATH_PARAM_NAME, "/");
        configuration.addParameter(ProxyConfiguration.PROXY_HOST_PARAM_NAME, "localhost");

        // TODO : Call required for Frascati studio app's, not for embedded proxy
        // getAvailablePort();

        // Call the generated app registry to check
        // TODO : Must be a singleton => FraSCAti component ?
        //InstanciatingEasySOAGeneratedAppsRegistry appRegistry = new InstanciatingEasySOAGeneratedAppsRegistry();
        //appRegistry.get(appID, template);

        // Create the proxy using the proxy configuration
        // How to determine what creation strategy to use ?
        // Set in the proxy configuration ?
        // TODO : load the ProxyCreationStrategy on demand (determined by parameters contained in the configuration)

        //ProxyCreationStrategy strategy = new EmbeddedProxyCreationStrategy();
        ////ProxyInfo proxyInfo = proxyCreationStrategy.createProxy(configuration); // TODO remove


        // Embedded registry impl :
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            // it already exists

            ///if (!appInstanceConfService.get().getParameters().isEmpty()) {
            ///    // but maybe it's already been reset ?
            ///    throw new Exception("Incompatible, should reset first !");
            ///}

            appInstanceConfService.update(configuration);
        } else {
            if (defaultHttpProxyInstanceConfigurationService != null) {
                throw new Exception("Instanciation is not supported by "
                        + this.getClass().getName() + ", should reset first !");
            } else {
                throw new Exception("default instance not ready !");
            }
        }
        // NB. FraSCAti Studio Instanciating registry impl should do something like :
        /*///appInstance = appInstances.get(appId);
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            // it already exists

            // but maybe it's already been reset ?
            ///if (!appInstanceConfService.get().getParameters().isEmpty()) {
            ///    // no
            ///    throw new Exception("Incompatible, should reset first !");
            ///}
            // else reusing pooled instance

            // updating conf of the right instance :
            appInstanceConfService.update(configuration);
        } else {
            genAppService.instanciate(httpProxyTemplate, configuration); // as params
            // lookup the instanciated app's conf service (if not returned by the live above through a FraSCAti.getService(ConfigurationService.Class))) :
            HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
            // NB. since instanciated, appInstanceConfService should not be null
            appInstanceConfService.update(configuration); // if not yet done above (?)
        }*/

        return configuration;

        // Configure the handler
        // TODO : Reactivate
        //configurationService.update(configuration);

        ////return proxyInfo;

        //* (Embedded/PerUser/AndComponent)EasySOAGeneratedAppIdFactoryStrategy.getId(user, projectId, componentIds [, other SoaNode ids ex. service, impl TODO components also ?]) returns easysoaGeneratedAppId
        //* getAvailablePort(conditions ex. > 20000) TODO Q pmerle implement by storing ports in FStudio db or by introspection (AT WORST or mere json) ??
        //* (Single/Instanciating)EasySOAGeneratedApp(s)Registry.get(id, Template (httpProxy), templateParams which are easysoaGeneratedApp param PLUS port etc.)
        //* finally, calls HttpProxyConfigurationService.update((proxyid,) HttpProxyConfiguration) which dispatches to Handlers

    }

    /**
     * Get info about the proxy corresponding to the given proxy ID
     * @param proxyID Proxy ID
     * @return
     */
    public EasySOAGeneratedAppConfiguration get(String proxyID){
        // TODO : complete this method
        return null;
    }

}
