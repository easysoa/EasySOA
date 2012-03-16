/**
 * 
 */
package org.easysoa.sca.frascati;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.objectweb.fractal.api.Component;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.assembly.factory.processor.ProcessingContextImpl;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 *
 */
public class RemoteFraSCAtiServiceProvider implements FraSCAtiServiceProviderItf {

    private static Log log = LogFactory.getLog(RemoteFraSCAtiServiceProvider.class);
    
    private FraSCAtiServiceItf frascatiService;
    
    @Override
    public FraSCAtiServiceItf getFraSCAtiService() {
        if(frascatiService == null){
            FraSCAti frascati;
            try {
                frascati = FraSCAti.newFraSCAti();
                Component frascatiServiceComponent = frascati.processComposite("org/easysoa/EasySOA.composite", new ProcessingContextImpl());
                this.frascatiService = (FraSCAtiServiceItf) frascati.getService(frascatiServiceComponent, "frascati-service", FraSCAtiServiceItf.class);
            } catch (FrascatiException ex) {
                log.error("Unable to get RemoteFraSCAtiService", ex);
                //ex.printStackTrace();
                frascatiService = null;
            }
        }
        return frascatiService;
    }

}
