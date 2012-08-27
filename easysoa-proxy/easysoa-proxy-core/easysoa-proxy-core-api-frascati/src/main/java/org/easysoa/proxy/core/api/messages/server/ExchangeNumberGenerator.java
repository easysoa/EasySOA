/**
 * EasySOA Proxy
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

/**
 * 
 */
package org.easysoa.proxy.core.api.messages.server;

import org.easysoa.proxy.core.api.messages.server.NumberGenerator;
import org.osoa.sca.annotations.Scope;

/**
 * Centralized message number generator server
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class ExchangeNumberGenerator implements NumberGenerator {
    
    private long currentMessageNumber;
    
    /**
     * Constructor
     */
    public ExchangeNumberGenerator(){
        this.currentMessageNumber = 0;
        // TODO : add a persistence to store and retrieve the current message number
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.messages.server.NumberGenerator#getNextNumber()
     */
    @Override
    public synchronized long getNextNumber(){
        this.currentMessageNumber++;
        return this.currentMessageNumber;
    }
     
}
