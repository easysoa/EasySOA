package org.easysoa.validation.validators;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.easysoa.validation.ServiceValidator;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

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
        if (referenceEnvWsdlBlob != null) {
            Blob wsdlBlob = (Blob) service.getProperty("file", "content");
            File referenceEnvWsdlFile = File.createTempFile("easysoa", "wsdl");
            File wsdlFile = File.createTempFile("easysoa", "wsdl");
            referenceEnvWsdlBlob.transferTo(referenceEnvWsdlFile);
            wsdlBlob.transferTo(wsdlFile);
            if (wsdlBlob != null && !isFinerWsdlThan(wsdlFile, referenceEnvWsdlFile)) {
                errors.add("Service has different WSDL");
            }
        }
        else {
            errors.add("Cannot access WSDLs to compare them");
        }
        
        return errors;
    }

    private boolean isFinerWsdlThan(File wsdlFile, File referenceEnvWsdlFile) {
        return true;
        /*try {
            FileInputStream wsdlFin = new FileInputStream(wsdlFile);
            FileInputStream referenceEnvWsdlFin = new FileInputStream(referenceEnvWsdlFile);
            byte[] referenceEnvBuf = new byte[1024];
            byte[] buf = new byte[1024];
            int referenceEnvNbRead, nbRead;
            while ((referenceEnvNbRead = referenceEnvWsdlFin.read(referenceEnvBuf)) > 0) {
                nbRead = wsdlFin.read(buf);
                if (nbRead != referenceEnvNbRead) {
    
                }
            }
        } catch (IOException ioex) {
            return false;
        }
        return true;*/
    }
}
