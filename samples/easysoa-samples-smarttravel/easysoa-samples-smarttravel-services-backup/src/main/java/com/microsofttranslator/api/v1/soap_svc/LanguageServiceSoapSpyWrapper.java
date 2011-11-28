package com.microsofttranslator.api.v1.soap_svc;

import static org.mockito.Mockito.spy;

import java.util.logging.Logger;

import net.server.Delegated;

import com.microsofttranslator.api.v1.soap.ArrayOfstring;

@javax.jws.WebService(
        serviceName = "SoapService",
        portName = "BasicHttpBinding_LanguageService",
        targetNamespace = "http://api.microsofttranslator.com/v1/soap.svc",
        //wsdlLocation = "file:microsoftTranslatorWebService.test.wsdl",
        endpointInterface = "com.microsofttranslator.api.v1.soap_svc.LanguageService")
public class LanguageServiceSoapSpyWrapper implements LanguageService , Delegated<LanguageService>{

	private static final Logger LOG = Logger.getLogger(LanguageServiceSoapSpyWrapper.class.getName());
    
    private LanguageService impl;
    private LanguageService spyDelegate;

	public LanguageServiceSoapSpyWrapper() {
    	this.impl = new LanguageServiceImpl();
    	this.spyDelegate = spy(this.impl);
    }

	public String detect(String appId, String text) {
		return spyDelegate.detect(appId, text);
	}

	public String translate(String appId, String text, String from, String to) {
		return spyDelegate.translate(appId, text, from, to);
	}

	public ArrayOfstring getLanguageNames(String appId, String locale) {
		return spyDelegate.getLanguageNames(appId, locale);
	}

	public ArrayOfstring getLanguages(String appId) {
		return spyDelegate.getLanguages(appId);
	}

	@Override
	public LanguageService getDelegate() {
		return spyDelegate;
	}
	
}
