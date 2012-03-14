/**
 * EasySOA - FraSCAti
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
package org.easysoa.frascati.importer;

import java.lang.reflect.Method;

import org.easysoa.frascati.api.ScaImporterIntermediaryItf;
import org.easysoa.frascati.api.ScaImporterRecipientItf;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Binding;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.util.AbstractLoggeable;

@Scope("COMPOSITE") 
@Service(ScaImporterIntermediaryItf.class) 
public class RuntimeSCAImporter
        extends AbstractLoggeable implements ScaImporterIntermediaryItf
{
    protected Object parentAppliImpl;
    protected EObject context;
    protected Method delegate;
    protected Method SERVICE_BINDING_VISITOR_METHOD = null;
    protected Method REFERENCE_BINDING_VISITOR_METHOD = null;
    private ScaImporterRecipientItf recipient;

    /**
     * Activate the visit of a Binding at runtime by the registered 
     * ScaImporterRecipientItf. If no importer has been registered do nothing
     */
    public void importSCA() throws Exception
    {
        if (recipient == null)
        {
            log.info("Enable to execute a runtime visit : "
                    + "No ScaImporterRecipientItf object registered");
            return;
        }
        try
        {
            this.delegate.invoke(recipient);
        } catch (NullPointerException e)
        {
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RuntimeSCAImporterItf#pushArchi(java.lang.String ,
     *      org.eclipse.emf.ecore.EObject)
     */
    public void pushArchi(String archiName, EObject eObject)
    {
        if (recipient == null)
        {
            log.config("No ScaImporterRecipientItf object registered");
            return;
        }
        this.recipient.getArchiNameStack().push(archiName);
        this.recipient.getBindingStack().push(eObject);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RuntimeSCAImporterItf#popArchi(java.lang.String)
     */
    public void popArchi(String archiName)
    {
        if (recipient == null)
        {
            log.config("No ScaImporterRecipientItf object registered");
            return;
        }
        this.recipient.getArchiNameStack().pop();
        this.recipient.getBindingStack().pop();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.RuntimeSCAImporterItf#pushBinding(org.eclipse
     *      .stp.sca.Binding)
     */
    public void pushBinding(Binding binding)
    {
        if (recipient == null)
        {
            log.config("No ScaImporterRecipientItf object registered");
            return;
        }
        recipient.getBindingStack().push(binding);
        try
        {
            importSCA();
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            delegate = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see
     * org.easysoa.frascati.api.ScaImporterIntermediaryItf#setScaImporterRecipient(org
     * .easysoa.frascati.api.ScaImporterRecipientItf)
     */
    public void setScaImporterRecipient(ScaImporterRecipientItf recipient)
    {
        if (recipient == null)
        {
            REFERENCE_BINDING_VISITOR_METHOD = null;
            SERVICE_BINDING_VISITOR_METHOD = null;
            this.recipient = null;
            this.delegate = null;
            return;
        }
        this.recipient = recipient;
        try
        {
            REFERENCE_BINDING_VISITOR_METHOD =
                    ScaImporterRecipientItf.class
                            .getDeclaredMethod("runtimeReferenceBindingVisit");
            SERVICE_BINDING_VISITOR_METHOD =
                    ScaImporterRecipientItf.class
                            .getDeclaredMethod("runtimeServiceBindingVisit");
        } catch (SecurityException e)
        {
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.ScaImporterIntermediaryItf#defineServiceDelegate()
     */
    public void defineServiceDelegate()
    {
        this.delegate = SERVICE_BINDING_VISITOR_METHOD;
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.ScaImporterIntermediaryItf#defineReferenceDelegate()
     */
    public void defineReferenceDelegate()
    {
        this.delegate = REFERENCE_BINDING_VISITOR_METHOD;
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.easysoa.frascati.api.ScaImporterIntermediaryItf#clearDelegate()
     */
    public void clearDelegate()
    {
        this.delegate = null;
        
    }
}
