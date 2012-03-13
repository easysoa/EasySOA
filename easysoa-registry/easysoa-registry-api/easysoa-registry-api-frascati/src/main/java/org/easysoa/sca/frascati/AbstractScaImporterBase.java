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

package org.easysoa.sca.frascati;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.registry.frascati.RestBindingInfoProvider;
import org.easysoa.registry.frascati.WSBindingInfoProvider;
import org.easysoa.sca.BindingInfoProvider;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.ScaVisitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.Composite;
import org.ow2.frascati.util.FrascatiException;

/**
 * ScaImporter using FraSCAti SCA parser
 * 
 * About FraSCAti parsing archi : in AssemblyFactoryManager (conf'd in
 * "composite-manager"), by ScaCompositeParser/AbstractDelegateScaParser and
 * others in between and mainly in org.ow2.frascati.parser.core.ScaParser, also
 * in the appropriate Resolver, ex. JavaResolver, JavaInterfaceResolver
 * 
 * 
 * TODO get parsing errors from FraSCAti ?! OK wrap as delegate
 * ProcessingContextImpl.error/warning() (that extends ParsingContextImpl) TODO
 * and feed errors back to UI
 * 
 * DONE get FraSCAti not to aborts parsing on unknown classes (impl or itf, not
 * in the classpath) ? (though WSDLs are OK) FraSCAti archi : done in the
 * appropriate Resolver, ex. JavaResolver, JavaInterfaceResolver those could be
 * put back to WARNING, but other failures still possible (CompositeInterface,
 * ConstrainingType, ImplementationComposite, Reference, Promote, Autowire,
 * Callback, Authentication...) 1. easiest is to only support full SCA (zip /
 * jar with classes and all SCAs) for that, using
 * AssemblyFactoryManager.processContribution() requires sca-contribution.xml so
 * can't work (save if generated) OK so rather using custom unzip & recursive
 * readComposite 2. intercept unfound java class errors by wrapping as delegate
 * ProcessingContextImpl.loadClass() OK works, further : possibly resolve within
 * easysoa registry though still no support of dependency between zips
 * 
 * TODO LATER to further support incomplete composites : have to know what level
 * of incomplete, till then the other import impl can help
 * 
 * 
 * remaining TODOs :
 * 
 * TODO implement service & reference discovery in SCA visitor : OK for now only WS Service case, done in visitBinding() 
 * TODO also references & REST service
 * 
 * TODO unify with original SCAImport : either remove original, or factorize business logic & unify fct 
 * TODO hook in UI and whole (release) build 
 * TODO possibly extract & separate easysoa-frascati-nuxeo (original) 
 * TODO refactor project (easysoa-registry(-core)-frascati ?) & package (org.easysoa.registry.frascati)
 * 
 * @author mdutoo
 * 
 */

/**
 * Base SCA Importer class (Nuxeo free) to register services
 * @author jguillemotte
 *
 */
public abstract class AbstractScaImporterBase implements IScaImporter
{
    public static final String SCA_URI = "http://www.osoa.org/xmlns/sca/1.0";
    public static final String FRASCATI_URI = "http://frascati.ow2.org/xmlns/sca/1.1";
    public static final String WSDLINSTANCE_URI = "http://www.w3.org/2004/08/wsdl-instance";
    
    // public static final QName SCA_COMPONENT_QNAME = new QName(SCA_URI, "component");
    // public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
    // public static final QName SCA_REFERENCE_QNAME = new QName(SCA_URI, "reference");
    
    // Logger
    private static Log log = LogFactory.getLog(AbstractScaImporterBase.class);
    
    // Import either a composite file or a composite object
    protected File compositeFile;
    protected Composite scaComposite;
    private String serviceStackType;
    private String serviceStackUrl;
    private Stack<String> archiNameStack = new Stack<String>();
    private Stack<EObject> bindingStack = new Stack<EObject>();
    protected BindingVisitorFactory bindingVisitorFactory;
    /**
     * List of binding info providers
     */
    protected List<BindingInfoProvider> bindingInfoProviders = null;
    private String appliImplURL;

    /**
     * Constructor
     * 
     * @param compositeFile
     *            Composite file to import, can be null if this is used as a runtime
     *            importer
     * @throws FrascatiException
     */
    public AbstractScaImporterBase(
            BindingVisitorFactory bindingVisitorFactory,
            File compositeFile)
    {
        this.bindingVisitorFactory = bindingVisitorFactory;
        this.compositeFile = compositeFile;
    }

    /**
     * @param bindingVisitorFactory
     * @param scaComposite
     *            Composite object to import, can be null if this is used as a runtime
     *            importer
     * @throws FrascatiException
     */
    public AbstractScaImporterBase(
            BindingVisitorFactory bindingVisitorFactory,
            Composite scaComposite)
    {
        this.bindingVisitorFactory = bindingVisitorFactory;
        this.scaComposite = scaComposite;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.sca.IScaImporter#getBindingVisitorFactory()
     */
    public BindingVisitorFactory getBindingVisitorFactory()
    {
        return bindingVisitorFactory;
    }
    
    /**
     * Fill the bindingInfoProviders list if it has not been before and 
     * return  it
     * 
     * @return
     *         the bindingInfoProviders list
     */
    public List<BindingInfoProvider> createBindingInfoProviders()
    {
        if (bindingInfoProviders == null)
        {
            bindingInfoProviders = new ArrayList<BindingInfoProvider>();
            bindingInfoProviders.add(new WSBindingInfoProvider(this));
            bindingInfoProviders.add(new RestBindingInfoProvider(this));
        }
        return bindingInfoProviders;
    }

    /**
     * @param scaQname
     * @param scaVisitor
     *            Binding visitor to register in Nuxeo
     * @param bindingInfoProviders
     *            Bindings info provider list
     */
    public void acceptBindingVisitors(Binding binding, ScaVisitor scaVisitor,
            List<BindingInfoProvider> bindingInfoProviders)
    {
        for (BindingInfoProvider bindingInfoProvider : bindingInfoProviders)
        {
            if (bindingInfoProvider.isOkFor(binding))
            {
                try
                {
                    scaVisitor.visit(bindingInfoProvider);
                } catch (Exception ex)
                {
                    String errorMessage =
                            "Error when visiting binding "
                                    + scaVisitor.getDescription()
                                    + " at archi path " + toCurrentArchiPath();
                    /*
                     * if(compositeFile != null){ errorMessage = errorMessage +
                     * " in SCA composite file " + compositeFile.getFilename(); }
                     */
                    log.error(errorMessage, ex);
                }
            }
        }
    }

    /**
         * 
         */
    public void setServiceStackType(String serviceStackType)
    {
        this.serviceStackType = serviceStackType;
    }

    /**
     * @param serviceStackUrl
     */
    public void setServiceStackUrl(String serviceStackUrl)
    {
        this.serviceStackUrl = serviceStackUrl;
    }

    @Override public String getCurrentArchiName()
    {
        return getArchiNameStack().peek();
    }

    /**
     * @return
     */
    public EObject getCurrentBinding()
    {
        return getBindingStack().peek();
    }

    @Override 
    public String toCurrentArchiPath()
    {
        StringBuffer sbuf = new StringBuffer();
        for (String archiName : getArchiNameStack())
        {
            sbuf.append("/");
            sbuf.append(archiName);
        }
        return sbuf.toString();
    }

    @Override 
    public File getCompositeFile()
    {
        return compositeFile;
    }

    @Override
    public String getServiceStackType()
    {
        return serviceStackType;
    }

    @Override 
    public String getServiceStackUrl()
    {
        return serviceStackUrl;
    }

    /**
     * @return
     */
    public Stack<String> getArchiNameStack()
    {
        return archiNameStack;
    }

    public Stack<EObject> getBindingStack()
    {
        return bindingStack;
    }

    /**
     *  (non-Javadoc)
     * @see org.easysoa.sca.IScaImporter#setAppliImplURL(java.lang.String)
     */
    public void setAppliImplURL(String appliImplURL)
    {
        this.appliImplURL = appliImplURL;
    }

    /**
     *  (non-Javadoc)
     * @see org.easysoa.sca.IScaImporter#getAppliImplURL()
     */
    public String getAppliImplURL()
    {
        return this.appliImplURL;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.sca.IScaImporter#createServiceBindingVisitor()
     */
    public ScaVisitor createServiceBindingVisitor()
    {
        // Visitor using Notification API
        return bindingVisitorFactory.createServiceBindingVisitor(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.sca.IScaImporter#createReferenceBindingVisitor()
     */
    public ScaVisitor createReferenceBindingVisitor()
    {
        // Visitor using Notification API
        return bindingVisitorFactory.createReferenceBindingVisitor(this);
    }
    
}