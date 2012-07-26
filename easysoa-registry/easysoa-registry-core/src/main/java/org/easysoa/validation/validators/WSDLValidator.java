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

package org.easysoa.validation.validators;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.validation.ServiceValidator;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.ow2.easywsdl.schema.api.ComplexType;
import org.ow2.easywsdl.schema.api.Element;
import org.ow2.easywsdl.schema.api.Schema;
import org.ow2.easywsdl.schema.api.Type;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.InterfaceType;
import org.ow2.easywsdl.wsdl.api.Operation;
import org.ow2.easywsdl.wsdl.api.Part;
import org.ow2.easywsdl.wsdl.api.WSDLException;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * 
 * Service validator that compares the WSDLs
 * XXX: Mocked, currently always returns true
 * 
 * @author mkalam-alami
 *
 */
public class WSDLValidator extends ServiceValidator {

    @Override
    public List<String> validateService(CoreSession session, DocumentModel service, DocumentModel referenceService) throws ClientException, IOException {
        List<String> errors = new LinkedList<String>();
        
        // Check WSDL
        // Note: delta-based validation would be : ex. if wsdl changed, check it for
        // now only full validation.
        Blob referenceEnvWsdlBlob = (Blob) referenceService.getProperty("file", "content");
        Blob wsdlBlob = (Blob) service.getProperty("file", "content");
        if (referenceEnvWsdlBlob != null && wsdlBlob != null) {
            try {
                File referenceEnvWsdlFile = File.createTempFile("easysoa", "wsdl");
                referenceEnvWsdlBlob.transferTo(referenceEnvWsdlFile);
                File wsdlFile = File.createTempFile("easysoa", "wsdl");
                wsdlBlob.transferTo(wsdlFile);
                if (!isWsdlCompatibleWith(wsdlFile, referenceEnvWsdlFile)) {
                    errors.add("Service has different WSDL");
                }
            } catch (WSDLException e) {
                errors.add("Invalid WSDL: " + e.getMessage());
            } catch (URISyntaxException e) {
                errors.add(e.getMessage());
            }
        }
        else {
            errors.add("Cannot access WSDLs to compare them");
        }
        
        return errors;
    }

    /**
     * Checks the compatibility between two WSDL 
     * @param wsdlFile
     * @param referenceEnvWsdlFile
     * @return If the WSDL is compatible with the referenced one
     */
    public boolean isWsdlCompatibleWith(File wsdlFile, File referenceEnvWsdlFile) throws WSDLException, MalformedURLException, IOException, URISyntaxException  {

        /**
         * Parses both WSDLs in parallel, following the reference WSDL structure.
         * XXX: Parsing is rough, incomplete and could deserve a more structured approach
         * (for instance node visitors in a similar way to the SCA parsing)
         */
        
        // Load WSDLs
        WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
        Description wsdl = reader.read(wsdlFile.toURI().toURL());
        Description referenceWsdl = reader.read(referenceEnvWsdlFile.toURI().toURL());
        
        // Match interfaces
        for (InterfaceType referenceInterfaceType : referenceWsdl.getInterfaces()) {
            InterfaceType interfaceType = wsdl.getInterface(referenceInterfaceType.getQName());
            if (interfaceType != null) {
                
                // Match operations
                for (Operation referenceOperation : referenceInterfaceType.getOperations()) {
                    Operation operation = interfaceType.getOperation(referenceOperation.getQName());
                    
                    // Match parameters types
                    if (operation != null) {
                        // Input
                        for (Part referencePart : referenceOperation.getInput().getParts()) {
                            Part part = operation.getInput().getPart(referencePart.getPartQName().getLocalPart());
                            if (part != null) {
                                if (!part.getElement().equals(referencePart.getElement())) {
                                    return false;
                                }
                            }
                            else {
                                return false;
                            }
                        }
                        // Output
                        for (Part referencePart : referenceOperation.getOutput().getParts()) {
                            Part part = operation.getOutput().getPart(referencePart.getPartQName().getLocalPart());
                            if (part != null) {
                                if (!part.getElement().equals(referencePart.getElement())) {
                                    return false;
                                }
                            }
                            else {
                                return false;
                            }
                        }
                    }
                    else {
                        return false;
                    }
                }
            }
            else {
                return false;
            }
        }

        // Match message types
        List<Schema> referenceSchemas = referenceWsdl.getTypes().getSchemas(),
                     schemas = wsdl.getTypes().getSchemas();
        if (!referenceSchemas.isEmpty()) {
            Schema referenceSchema = referenceSchemas.get(0);
            if (!schemas.isEmpty()) {
                Schema schema = schemas.get(0);
                // Match elements
                for (Element referenceElement : referenceSchema.getElements()) {
                    Element element = schema.getElement(referenceElement.getQName());
                    if (element != null && element.getType() != null && element.getType().getQName() != null
                            && referenceElement != null && referenceElement.getType() != null
                            && !element.getType().getQName().equals(referenceElement.getType().getQName())) {
                        return false;
                    }
                }
                // Match types
                for (Type referenceType : referenceSchema.getTypes()) {
                    Type type = schema.getType(referenceType.getQName());
                    if (type != null) {
                        if (referenceType instanceof ComplexType) {
                            if (type instanceof ComplexType) {
                                for (Element referenceElement : ((ComplexType) referenceType).getSequence().getElements()) {
                                    boolean found = false;
                                    for (Element element : ((ComplexType) type).getSequence().getElements()) {
                                        if (element.equals(referenceElement)) {
                                            found = true;
                                        }
                                    }
                                    if (!found) {
                                        return false;
                                    }
                                }
                            }
                            else {
                                return false;
                            }
                        }
                        else if (!type.equals(referenceType)) {
                            return false;
                        }
                    }
                    else {
                       return false;
                    }
                }
            }
            else {
                return false;
            }
        }
        
        return true;
    }
}
