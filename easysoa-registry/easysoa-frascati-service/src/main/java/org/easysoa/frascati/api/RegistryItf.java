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
package org.easysoa.frascati.api;

import java.util.List;

import org.easysoa.frascati.FraSCAtiServiceException;
import org.eclipse.stp.sca.Composite;
import org.objectweb.fractal.api.Component;
import org.osoa.sca.annotations.Service;

@Service
public interface RegistryItf 
{
    /**
     * Return the registered SCA Composite which name is passed on as a parameter
     * 
     * @aram compositeName
     *          the name of the SCA Composite
     * @return
     *          the SCA Composite if it has been found, Null otherwise
     */
    Composite getComposite(String compositeName);
    
    /**
     * Return the registered fractal Component which name is passed on as a
     *  parameter
     * 
     * @aram componentName
     *          the name of the fractal Component
     * @return
     *          the fractal Component if it has been found, Null otherwise
     */
    Component getComponent(String componentName);

    <T> T getService(String componentName, 
            String serviceName, Class<T> serviceClass) 
                    throws FraSCAtiServiceException;

    /**
     * Return the list of the last processed fractal Component. The list is 
     * empty after each call to this method
     * 
     * @return
     *          the list of the last processed fractal Component
     */
    List<String> getProcessedComponentList();

}