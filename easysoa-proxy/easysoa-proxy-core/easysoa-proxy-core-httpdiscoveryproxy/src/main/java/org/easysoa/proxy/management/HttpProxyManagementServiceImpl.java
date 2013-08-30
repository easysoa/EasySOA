/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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
package org.easysoa.proxy.management;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.core.api.configuration.ConfigurationParameter;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.core.api.configuration.HttpProxyConfigurationService;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.management.HttpProxyManagementService;
import org.easysoa.proxy.core.api.management.ManagementServiceResult;
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

    /**
     * Logger
     */
    static Logger logger = Logger.getLogger(HttpProxyManagementServiceImpl.class.getName());

    @Reference
    EasySOAGeneratedAppIdFactoryStrategy embeddedEasySOAGeneratedAppIdFactoryStrategy;

    // singleton : the default proxy instance started in FraSCAti
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

    //@Override
    public void update(ProxyConfiguration configuration) throws Exception {
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            appInstanceConfService.update(configuration);
        } else {
            throw new Exception("No app with id" + configuration.getId());
        }
    }

    //@Override
    public String reset(ProxyConfiguration configuration) throws Exception {
        logger.debug("reset..." + configuration.getId());
        String result = "";
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            result = appInstanceConfService.reset(configuration);
        } else {
            logger.debug("reset error, no " + configuration.getId());
            throw new Exception("No app with id" + configuration.getId());
        }
        return result;
    }

    //@Override
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
    //@Override
    public ManagementServiceResult getHttpProxy(ProxyConfiguration configuration) throws Exception {

        ManagementServiceResult result;

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

        // Embedded registry impl :
        HttpProxyConfigurationService appInstanceConfService = getConfigurationService(configuration);
        if (appInstanceConfService != null) {
            // an appInstance with the same id already exists
        	ProxyConfiguration existingProxyConfiguration = appInstanceConfService.get(configuration);
            ArrayList<String> incompatibleParametersNames = new ArrayList<String>();
            boolean areConfigurationIncompatible = false;
        	for (ConfigurationParameter existingParameter : existingProxyConfiguration.getParameters()) {
        		String newValue = configuration.getParameter(existingParameter.getKey());
        		if (newValue == null && existingParameter.getValue() != null
        				 || newValue != null && !newValue.equals(existingParameter.getValue())) {
        			areConfigurationIncompatible = true;
                    incompatibleParametersNames.add(existingParameter.getKey());
        		}
        	}
            if (areConfigurationIncompatible) {
                // but maybe it's already been reset ?
                //throw new Exception("Incompatible, should reset first !");
                logger.debug("Incompatible, should reset first !");
                result = new ManagementServiceResult(ManagementServiceResult.RESULT_KO, existingProxyConfiguration);
                result.setMessage(buildIncompatibleMessage(existingProxyConfiguration, configuration, incompatibleParametersNames));
                return result;
            }
            appInstanceConfService.update(configuration);
        } else {
            if (defaultHttpProxyInstanceConfigurationService != null) {
                //throw new Exception("Instanciation is not supported by "
                //        + this.getClass().getName() + ", should reset first !");
                logger.debug("Instanciation is not supported by "
                        + this.getClass().getName() + ", should reset first !");
                result = new ManagementServiceResult(ManagementServiceResult.RESULT_KO, null);
                result.setMessage("Instanciation is not supported by "
                        + this.getClass().getName() + ", should reset first !");
                return result;
            } else {
                //throw new Exception("default instance not ready !");
                logger.error("default instance not ready !");
                result = new ManagementServiceResult(ManagementServiceResult.RESULT_KO, null);
                result.setMessage("default instance not ready !");
                return result;
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

        result = new ManagementServiceResult(ManagementServiceResult.RESULT_OK, configuration);
        return result;

        // Configure the handler
        // TODO : Reactivate
        //configurationService.update(configuration);

        //* (Embedded/PerUser/AndComponent)EasySOAGeneratedAppIdFactoryStrategy.getId(user, projectId, componentIds [, other SoaNode ids ex. service, impl TODO components also ?]) returns easysoaGeneratedAppId
        //* getAvailablePort(conditions ex. > 20000) TODO Q pmerle implement by storing ports in FStudio db or by introspection (AT WORST or mere json) ??
        //* (Single/Instanciating)EasySOAGeneratedApp(s)Registry.get(id, Template (httpProxy), templateParams which are easysoaGeneratedApp param PLUS port etc.)
        //* finally, calls HttpProxyConfigurationService.update((proxyid,) HttpProxyConfiguration) which dispatches to Handlers
    }

    /**
     * Build an incompatible error message
     * @param existingProxyConfiguration The existing proxy configuration
     * @param configuration The new proxy configuration
     * @param incompatibleParameterNames A list containing the name of the incompatibles parameters
     * @return The error message
     */
    private String buildIncompatibleMessage(ProxyConfiguration existingProxyConfiguration, ProxyConfiguration configuration, List<String> incompatibleParameterNames){
        StringBuilder message = new StringBuilder();
        message.append("<p><b>Incompatible, should reset first !</b></p><p>Details : <br/>");
        for (String incompatibleParameter : incompatibleParameterNames) {
        		String existingValue = existingProxyConfiguration.getParameter(incompatibleParameter);
                String newValue = configuration.getParameter(incompatibleParameter);
                message.append("<br/>");
                message.append("&nbsp;&nbsp;- The parameter '");
                message.append(incompatibleParameter);
                message.append("' new value '");
                message.append(newValue);
                message.append("' is incompatible with the existing value '");
                message.append(existingValue);
                message.append("'");
        }
        message.append("</p>");
        //"Incompatible, should reset first !"); // TODO buildIncompatibleMessage(existingProxyConfiguration, configuration, incompatibleParameterNames) by recopying "for"
        return message.toString();
    }

    /**
     * Get info about the proxy corresponding to the given proxy ID
     * @param proxyID Proxy ID
     * @return The configuration corresponding to the proxyID
     */
    public ProxyConfiguration get(String proxyID) throws Exception {
        HttpProxyConfigurationService appInstanceConfService = defaultHttpProxyInstanceConfigurationService;
        // Get the default instance
        return appInstanceConfService.get(null);
    }

    public List<ProxyConfiguration> listInstances() throws Exception {
        List<ProxyConfiguration> instances = new ArrayList<ProxyConfiguration>();
        // Default embedded proxy instance
        instances.add(defaultHttpProxyInstanceConfigurationService.get(null));
        // TODO : add fstudio InstanceConfiguration service when done
        /*for(ProxyConfiguration conf : ){
            instances.add();
        }*/

        return instances;
    }

}
