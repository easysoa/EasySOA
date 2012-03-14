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
package org.easysoa.frascati.api.observer;

import org.osoa.sca.annotations.Service;

/**
 * A FraSCAti {@link org.ow2.frascati.assembly.factory.api.Processor}
 * observer definition
 */
@Service
public interface ProcessorObserverItf<T> 
{
    /**
     * A <T> processor observer is informed about the begining of a 
     * checking process for the <T> object passed on as a parameter
     * 
     * @param t
     *          the <T> object for which a checking has been asked for 
     */
    void checkDo(T t);
    
    /**
     * A <T> processor observer is informed about the end of a 
     * checking process for the <T> object passed on as a parameter
     * 
     * @param t
     *          the <T> object for which a checking has been asked for 
     */
    void checkDone(T t);
}
