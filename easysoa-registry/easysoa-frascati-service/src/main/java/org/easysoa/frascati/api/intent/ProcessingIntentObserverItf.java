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
package org.easysoa.frascati.api.intent;

import javax.xml.namespace.QName;

import org.objectweb.fractal.api.Component;
import org.osoa.sca.annotations.Service;

/**
 * A FraSCAti {@link org.ow2.frascati.assembly.factory.api.CompositeManager}
 * observer definition
 */
@Service
public interface ProcessingIntentObserverItf
{
    /**
     * A FraSCAti {@link org.ow2.frascati.assembly.factory.api.CompositeManager}
     * observer is informed about the begining of a processing process for the 
     * SCA {@link org.eclipse.stp.sca.Composite} which {@link QName} is passed 
     * on as a parameter
     * 
     * @param name
     *          the {@link QName} of the SCA 
     *          {@link org.eclipse.stp.sca.Composite} for which a processing has
     *          been asked for 
     */
    void startProcessing(QName name);
    
    /**
     * A FraSCAti {@link org.ow2.frascati.assembly.factory.api.CompositeManager}
     * observer is informed about the end of a processing process for the SCA 
     * {@link org.eclipse.stp.sca.Composite} which {@link QName} is passed on as
     * a parameter
     * 
     * @param name
     *          the {@link QName} of the SCA {@link org.eclipse.stp.sca.Composite} 
     *          for which a processing has been asked for 
     */
    void stopProcessing(QName name);
    
    /**
     * A FraSCAti CompositeManager observer is informed about the creation of
     * a Fractal {@link Component} during the processing process
     * 
     * @param component
     *          the Fractal {@link Component} created during the processing 
     *          process
     */
    void componentAdded(Component component);
    
    /**
     * A FraSCAti CompositeManager observer is informed about the demand of a 
     * suppression of a Fractal {@link Component}
     * 
     * @param componentName
     *          the name of the Fractal {@link Component} removed
     */
    void componentRemoved(String componentName);
}
