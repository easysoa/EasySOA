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

import org.eclipse.stp.sca.Composite;
import org.osoa.sca.annotations.Service;

/**
 * A FraSCAti {@link org.ow2.frascati.parser.api.Parser} observer definition
 */
@Service
public interface ParserIntentObserverItf
{
    /**
     * A FraSCAti {@link org.ow2.frascati.parser.api.Parser} observer is 
     * informed that an SCA {@link Composite} has been parsed. 
     * 
     * @param composite
     *          the SCA {@link Composite} object parsed by FraSCAti 
     */
    void compositeParsed(Composite composite);
}
