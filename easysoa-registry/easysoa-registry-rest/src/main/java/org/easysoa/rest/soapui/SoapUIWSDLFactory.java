package org.easysoa.rest.soapui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

public class SoapUIWSDLFactory {

    public SoapUIWSDLFactory() {}
    
    public SoapUIWSDL create(File file) throws Exception {
        return create(file, null);
    }

    public SoapUIWSDL create(File file, String wsdlUrl) throws Exception {
        
        SoapUIWSDL newWsdl = new SoapUIWSDL();
        
        // Parse WSDL
        parseWSDL(file, newWsdl);
        
        // Store URL
        if (wsdlUrl != null) {
            newWsdl.setUrl(wsdlUrl);
        }
        
        // Extract WSDL contents
        // "<?xml version="1.0" encoding="UTF-8"?>" must be stripped out
        newWsdl.setContents(getFileContents(file).replaceFirst("\\<\\?[^?]*\\?\\>", ""));
        
        return newWsdl;
    }
    
    private void parseWSDL(File file, SoapUIWSDL targetWSDL) throws Exception {
        
        // Init EasyWSDL
        WSDLReader wsdlReader = WSDLFactory.newInstance().newWSDLReader();
        Description description = wsdlReader.read(file.toURI().toURL());
        
        // Extract global information
        try {
            targetWSDL.setName(description.getServices().get(0).getQName().getLocalPart());
            targetWSDL.setBindingName(description.getBindings().get(0).getQName().toString());
            targetWSDL.setEndpointUrl(description.getServices().get(0).getEndpoints().get(0).getAddress());
            targetWSDL.setUrl(targetWSDL.getEndpointUrl() + "?wsdl"); // Default WSDL url
        }
        catch (Exception e) {
            throw new Exception("WSDL has not enough information to build SoapUI config", e);
        }
        
        // Extract operations information
        try {
            List<Operation> operations = description.getServices().get(0).getInterface().getOperations();
            for (Operation operation : operations) {
                SoapUIOperation newConfOperation = new SoapUIOperation();
                newConfOperation.setName(operation.getQName().getLocalPart());
                newConfOperation.setInputName(operation.getInput().getName());
                newConfOperation.setOutputName(operation.getOutput().getName());
                targetWSDL.addOperation(newConfOperation);
            }
        }
        catch (Exception e) {
            // Failed to extract operations
        }
    }
    
    public static String getFileContents(File file) throws IOException {
        String contents = "";
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while (reader.ready()) {
            contents += reader.readLine() + '\n';
        }
        return contents;
    }
    
}
