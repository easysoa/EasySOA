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
package org.easysoa.frascati;

import java.lang.reflect.Method;

import javax.xml.namespace.QName;

import org.eclipse.stp.sca.Binding;
import org.eclipse.stp.sca.BindingType;
import org.eclipse.stp.sca.ComponentReference;
import org.eclipse.stp.sca.ComponentService;
import org.eclipse.stp.sca.Composite;
import org.oasisopen.sca.ServiceReference;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.julia.ComponentInterface;
import org.osoa.sca.annotations.Reference;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.assembly.factory.api.Processor;
import org.ow2.frascati.parser.api.Parser;
import org.ow2.frascati.parser.api.ParsingContext;
import org.ow2.frascati.starter.api.AbstractInitializable;
import org.ow2.frascati.tinfi.TinfiComponentInterceptor;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.util.reference.ServiceReferenceUtil;

public class WeaverInitializer extends AbstractInitializable
{

    // --------------------------------------------------------------------------
    // Internal state.
    // --------------------------------------------------------------------------
   
    //list of service references and intent handlers to weave together
    //those object will allow to notice each event relative to the processing or 
    //parsing processes
    @Reference(name = "composite-parser")
    private Parser<Composite> parser;
   
    @Reference(name = "parser-intent")
    private IntentHandler parserIntent;

    @Reference(name = "composite-processor")
    private Processor<org.eclipse.stp.sca.Composite> processorComposite;

    @Reference(name = "processor-composite-intent")
    private IntentHandler processorCompositeIntent;

    @Reference(name = "component-processor")
    private Processor<org.eclipse.stp.sca.Component> processorComponent;

    @Reference(name = "processor-component-intent")
    private IntentHandler processorComponentIntent;

    @Reference(name = "component-reference-processor")
    private Processor<ComponentReference> processorComponentReference;

    @Reference(name = "processor-component-reference-intent")
    private IntentHandler processorComponentReferenceIntent;

    @Reference(name = "component-service-processor")
    private Processor<ComponentService> processorComponentService;

    @Reference(name = "processor-component-service-intent")
    private IntentHandler processorComponentServiceIntent;

    @Reference(name = "reference-processor")
    private Processor<org.eclipse.stp.sca.Reference> processorReference;

    @Reference(name = "processor-reference-intent")
    private IntentHandler processorReferenceIntent;

    @Reference(name = "service-processor")
    private Processor<org.eclipse.stp.sca.Service> processorService;

    @Reference(name = "processor-service-intent")
    private IntentHandler processorServiceIntent;

    @Reference(name = "binding-processor")
    private Processor<Binding> processorBinding;

    @Reference(name = "processor-binding-intent")
    private IntentHandler processorBindingIntent;

    @Reference(name = "sca-binding-processor")
    private Processor<BindingType> scaProcessorBinding;

    @Reference(name = "processor-sca-binding-intent")
    private IntentHandler processorScaBindingIntent;

    // -------------------------------------------------------------------------
    // Internal methods.
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Public methods.
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     * 
     * @see
     * org.ow2.frascati.starter.api.InitializableItf#initialize
     * ()
     */
    public void doInitialize()
    {
        TinfiComponentInterceptor<?> tci = null;
        Component component = null;
        try
        {
            //list of service references to weave the intents with
            Object[] serviceReferences = new Object[] { 
                    parser, processorComposite,
                    processorComponent, processorComponentReference,
                    processorComponentService, processorReference,
                    processorService, processorBinding, scaProcessorBinding };

            //list of targeted interfaces name for each service references 
            //respectively
            String[] interfaces = new String[] { 
                    "composite-parser",
                    "composite-processor", "component-processor",
                    "component-reference-processor",
                    "component-service-processor",
                    "composite-reference-processor",
                    "composite-service-processor", "binding-processor",
                    "binding-processor" };
            
            //list of intent handlers to weave with each service references
            //respectively
            IntentHandler[] handlers = new IntentHandler[] { 
                    parserIntent, processorCompositeIntent,
                    processorComponentIntent,
                    processorComponentReferenceIntent,
                    processorComponentServiceIntent, processorReferenceIntent,
                    processorServiceIntent, processorBindingIntent,
                    processorScaBindingIntent };

            //ckeck method for Processors
            Method check = Processor.class.getDeclaredMethod("check",
                    new Class<?>[] { Object.class, ProcessingContext.class });
            //parse method for the Parser
            Method parse = Parser.class.getDeclaredMethod("parse",
                    new Class<?>[] { QName.class, ParsingContext.class });
            //list of methods associated to intent handlers respectively
            Method[] methods = new Method[] {
                    parse, check, check, check, check, check, check, check, check };

            int n = 0;
            for (; n < serviceReferences.length; n++)
            {
                component = ServiceReferenceUtil.getRootComponent((ServiceReference<?>) serviceReferences[n]);
                tci = (TinfiComponentInterceptor<?>) ((ComponentInterface) component
                        .getFcInterface(interfaces[n])).getFcItfImpl();
                tci.addIntentHandler(handlers[n], methods[n]);
            }
        } catch (NoSuchInterfaceException e)
        {
            e.printStackTrace();
        } catch (SecurityException e)
        {
            e.printStackTrace();
        } catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }
}
