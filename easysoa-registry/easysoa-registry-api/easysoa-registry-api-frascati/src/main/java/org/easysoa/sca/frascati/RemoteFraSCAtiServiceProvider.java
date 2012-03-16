/**
 * 
 */
package org.easysoa.sca.frascati;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.NameController;
import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

/**
 * @author jguillemotte
 * 
 */
public class RemoteFraSCAtiServiceProvider implements
        FraSCAtiServiceProviderItf
{

    private static Log log = LogFactory
            .getLog(RemoteFraSCAtiServiceProvider.class);

    private FraSCAtiServiceItf frascatiService;

    @Override
    public FraSCAtiServiceItf getFraSCAtiService() {
        if(frascatiService == null){
            FraSCAti frascati;
            try {
                frascati = FraSCAti.newFraSCAti();
                
                Component root = frascati.getCompositeManager(
                        ).getTopLevelDomainComposite();
                
                Component af = getComponent(root,
                        "org.ow2.frascati.FraSCAti/assembly-factory");
                
                this.frascatiService = (FraSCAtiServiceItf) 
                   frascati.getService(af,"easysoa-frascati-service",FraSCAtiServiceItf.class);
                
            } catch (FrascatiException ex) {
                log.error("Unable to get RemoteFraSCAtiService", ex);
                //ex.printStackTrace();
                frascatiService = null;
            }
        }
        return frascatiService;
    }

    private Component getComponent(Component currentComponent,
            String componentPath)
    {
        String[] componentPathElements = componentPath.split("/");
        String lookFor = componentPathElements[0];
        String next = null;

        if (componentPathElements.length > 1)
        {
            int n = 1;
            StringBuilder nextSB = new StringBuilder();
            for (; n < componentPathElements.length; n++)
            {
                nextSB.append(componentPathElements[n]);
                if (n < componentPathElements.length - 1)
                {
                    nextSB.append("/");
                }
            }
            next = nextSB.toString();
        }
        try
        {
            ContentController contentController;
            contentController = (ContentController) currentComponent
                    .getFcInterface("content-controller");

            NameController nameController;

            Component[] subComponents = contentController.getFcSubComponents();

            if (subComponents == null)
            {
                return null;
            }
            for (Component component : subComponents)
            {
                nameController = (NameController) component
                        .getFcInterface("name-controller");

                String name = nameController.getFcName();
                if (lookFor.equals(name))
                {
                    if (next == null || next.length() == 0)
                    {
                        return component;
                    } else
                    {
                        return getComponent(component, next);
                    }
                }
            }
        } catch (NoSuchInterfaceException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
