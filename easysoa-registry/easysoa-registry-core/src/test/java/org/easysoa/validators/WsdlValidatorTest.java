package org.easysoa.validators;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.easysoa.validation.validators.WSDLValidator;
import org.junit.Test;
import org.ow2.easywsdl.wsdl.api.WSDLException;

/**
 * 
 * Tests the WSDL validator (here outside of any Nuxeo context).
 * 
 * @author mkalam-alami
 *
 */
public class WsdlValidatorTest {
    
    private static final File REFERENCE_WSDL = getFile("master");
    
    private static final String MUST_PASS = "This WSDL must pass";
    private static final String MUST_NOT_PASS = "This WSDL must not pass";
    
    private WSDLValidator validator = new WSDLValidator();
    
    @Test
    public void testWsdlValidator() throws Exception {
        Assert.assertTrue(MUST_PASS, isSameOrFiner("master")); // The exact same WSDL
        Assert.assertTrue(MUST_PASS, isSameOrFiner("same")); // The same WSDL on a different URL
        Assert.assertTrue(MUST_PASS, isSameOrFiner("finer")); // The same WSDL with a new, optional parameter
        Assert.assertFalse(MUST_NOT_PASS, isSameOrFiner("bad1")); // WSDL with a different interface
        Assert.assertFalse(MUST_NOT_PASS, isSameOrFiner("bad2")); // WSDL with a different operation
        Assert.assertFalse(MUST_NOT_PASS, isSameOrFiner("bad3")); // WSDL with a matching operation but different parameters
    }

    private boolean isSameOrFiner(String fileSuffix) throws WSDLException, MalformedURLException, IOException, URISyntaxException {
        return validator.isWsdlCompatibleWith(getFile(fileSuffix), REFERENCE_WSDL);
    }
    
    private static File getFile(String suffix) {
        return new File("src/test/resources/org/easysoa/validators/GalaxyTrip-" + suffix + ".wsdl");
    }

}
