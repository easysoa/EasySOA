/**
 * EasySOA Proxy
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

package org.easysoa.scaffolding.wsdlhelper.soapui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.easysoa.scaffolding.wsdlhelper.WsdlServiceHelper;
import org.eclipse.jetty.util.log.Log;
import org.json.JSONException;
import org.osoa.sca.annotations.Property;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.WsdlInterfaceFactory;
import com.eviware.soapui.impl.wsdl.WsdlInterface;
import com.eviware.soapui.impl.wsdl.WsdlOperation;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.WsdlRequest;
import com.eviware.soapui.impl.wsdl.WsdlSubmit;
import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.submit.transports.http.WsdlResponse;
import com.eviware.soapui.model.iface.Request.SubmitException;
import com.eviware.soapui.settings.ProxySettings;
import com.eviware.soapui.support.SoapUIException;

/**
 * WSDL Service helper based on Soap UI
 * @author jguillemotte
 *
 */
public class SoapUIServiceHelper implements WsdlServiceHelper {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(SoapUIServiceHelper.class.getClass());
	
	public static final String NOAUTH_PROXY_USERNAME = "anonymous";
	public static final String NOAUTH_PROXY_PASSWORD = "nopass";
	
	@Property
	private String httpProxyHost = null;
	@Property
	private int httpProxyPort = 80;
	@Property
	private String httpProxyUsername = NOAUTH_PROXY_USERNAME;
	@Property
	private String httpProxyPassword = NOAUTH_PROXY_PASSWORD;
	
	/**
	 * Create a temporary SOAPUI project, get the XML request, map the request with the parameters,
	 * Send the request and get the response.
	 * 
	 * NB. when you change the config, it has to be restarted
	 * 
	 * About soapui http client architecture :
	 * all transports are configured in RequestTransportRegistry http://www.java2s.com/Open-Source/Java-Document/Web-Services/soapui-1.7.5/com/eviware/soapui/impl/wsdl/submit/RequestTransportRegistry.java.htm
	 * by default only HttpClientRequestTransport http://www.java2s.com/Open-Source/Java-Document/Web-Services/soapui-1.7.5/com/eviware/soapui/impl/wsdl/submit/transports/http/HttpClientRequestTransport.java.htm
	 * whose configuration includes request filters, by default a lot ex. security, proxy (HttpProxyRequestFilter) http://www.java2s.com/Open-Source/Java-Document/Web-Services/soapui-1.7.5/com/eviware/soapui/impl/wsdl/submit/filters/HttpProxyRequestFilter.java.htm
	 * in proxy filter, ProxyUtils applies settings conf gotten from wsdlRequest http://www.java2s.com/Open-Source/Java-Document/Web-Services/soapui-1.7.5/com/eviware/soapui/impl/wsdl/support/http/ProxyUtils.java.htm
	 * 
	 * @param wsldOperation The operation to call
	 * @param paramList The list of parameters to map in the request
	 * @return the service response as XML string, or null if a problem occurs
	 * @throws SoapUIException
	 * @throws XmlException
	 * @throws IOException
	 * @throws SubmitException
	 * @throws JSONException 
	 */
	public String callService(String wsdlUrl, String binding, String wsldOperation, HashMap<String, List<String>> paramList) throws SoapUIException, XmlException, IOException, SubmitException, JSONException { 
		
		// TODO BETTER call in init
		setProxySettings();
		
		// create new project
		WsdlProject project = new WsdlProject();
		// import wsdl
		WsdlInterface iface = null;
		WsdlInterface[] ifaceArray = WsdlInterfaceFactory.importWsdl(project, wsdlUrl, true);
		// Get the corresponding binding
		for(WsdlInterface i : ifaceArray){
			if(i.getName().equals(binding)){
				iface = i;
			}
		}
		// If iface still null, binding not found => error
		if(iface == null){
			throw new IllegalArgumentException("Binding '" + binding + "' not found in the specified WSDL");
		}
		// get desired operation
		WsdlOperation operation = (WsdlOperation) iface.getOperationByName(wsldOperation);
		
		// create a new empty request for that operation
		WsdlRequest request = operation.addNewRequest("myRequest");
		// generate the xml request schema and map the parameter values		
		request.setRequestContent(mapInputParams(operation.createRequest(true), paramList));
		// submit the request
		WsdlSubmit<?> wsdlSubmit = (WsdlSubmit<?>) request.submit(new WsdlSubmitContext(request), false);
		// Send the request and wait for the response
		WsdlResponse wsdlResponse = (WsdlResponse) wsdlSubmit.getResponse();
		Log.info("Submit status : " + wsdlSubmit.getStatus());
		if(wsdlResponse != null){
			logger.debug("Soap Response" + wsdlResponse.getContentAsString());
			return wsdlResponse.getContentAsString();
		} else {
			logger.debug("Soap Response is null");
			return null;
		}
	}
	
	private void setProxySettings() {
		if (this.httpProxyHost != null) {
			SoapUI.getSettings().setString(ProxySettings.HOST, this.httpProxyHost); // required
			SoapUI.getSettings().setLong(ProxySettings.PORT, this.httpProxyPort); // required
			SoapUI.getSettings().setString(ProxySettings.ENABLE_PROXY, "true"); // required (String and not boolean as annotated)
			SoapUI.getSettings().setString(ProxySettings.USERNAME, this.httpProxyUsername); // required even if no auth but any value OK
			SoapUI.getSettings().setString(ProxySettings.PASSWORD, this.httpProxyPassword); // required even if no auth but any value OK
		} else {
			// TODO try hot reconf :
			SoapUI.getSettings().clearSetting(ProxySettings.HOST);
			SoapUI.getSettings().clearSetting(ProxySettings.PORT);
			SoapUI.getSettings().clearSetting(ProxySettings.ENABLE_PROXY);
			SoapUI.getSettings().clearSetting(ProxySettings.USERNAME);
			SoapUI.getSettings().clearSetting(ProxySettings.PASSWORD);
			//SoapUI.getSettings().reloadSettings(); // required ??
		}
		
		// A few other but useless things tried :
		/*iface.getSettings().setString(ProxySettings.HOST, "localhost");
		iface.getSettings().setLong(ProxySettings.PORT, 8082);
		iface.getSettings().setString(ProxySettings.ENABLE_PROXY, "true");*/
		//request.getConfig().addNewSettings().set(iface.getSettings().);
		//SettingsConfig sc = SettingsConfig.Factory.parse(iface.getSettings().toString());
		//request.setConfig(WsdlRequestConfig.Factory.parse(iface.getConfig()));
		//iface.getSettings().setConfig(request.getConfig().addNewSettings());
		/*SettingConfig hostSetting = request.getConfig().getSettings().addNewSetting();
		hostSetting.setId("ProxySettings@host");
		hostSetting.setStringValue("localhost");
		SettingConfig portSetting = request.getConfig().getSettings().addNewSetting();
		portSetting.setId("ProxySettings@port");
		portSetting.setStringValue("8082");
		SettingConfig enableProxySetting = request.getConfig().getSettings().addNewSetting();
		enableProxySetting.setId("ProxySettings@enableProxy");
		enableProxySetting.setStringValue("true");
		SettingConfig usernameSetting = request.getConfig().getSettings().addNewSetting();
		usernameSetting.setId("ProxySettings@username");
		usernameSetting.setStringValue("anonymous");
		SettingConfig passwordSetting = request.getConfig().getSettings().addNewSetting();
		passwordSetting.setId("ProxySettings@password");
		passwordSetting.setStringValue("nopass");*/
		///HostConfiguration hostConfiguration = new HostConfiguration();
		///wsdlSubmitContext.setProperty(BaseHttpRequestTransport.HOST_CONFIGURATION, hostConfiguration);
	}

	/**
	 * Map REST input params to the XML SOAP request
	 * @param xmlRequest The XML SOAP request
	 * @param params The params to map
	 * @return A mapped XML request for SOAP
	 */
	private String mapInputParams(String xmlRequest, HashMap<String, List<String>> params){
		// For each param key, replace the '?' in the xml request by the param value 
		logger.debug("XML request before mapping : " + xmlRequest);
		for(String paramKey : params.keySet()){
			//TODO make it works with multiple values and not only with the first param
			xmlRequest = xmlRequest.replace(paramKey + ">?", paramKey + ">" + params.get(paramKey).get(0));
		}
		logger.debug("XML request after mapping : " + xmlRequest);
		return xmlRequest;
	}
	
	
	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	public String getHttpProxyUsername() {
		return httpProxyUsername;
	}

	public void setHttpProxyUsername(String httpProxyUsername) {
		if (httpProxyUsername != null && httpProxyUsername.length() != 0) {
			this.httpProxyUsername = httpProxyUsername;
		} else {
			this.httpProxyUsername = NOAUTH_PROXY_USERNAME;
		}
	}

	public String getHttpProxyPassword() {
		return httpProxyPassword;
	}

	public void setHttpProxyPassword(String httpProxyPassword) {
		if (httpProxyPassword != null && httpProxyPassword.length() != 0) {
			this.httpProxyPassword = httpProxyPassword;
		} else {
			this.httpProxyPassword = NOAUTH_PROXY_PASSWORD;
		}
	}

}
