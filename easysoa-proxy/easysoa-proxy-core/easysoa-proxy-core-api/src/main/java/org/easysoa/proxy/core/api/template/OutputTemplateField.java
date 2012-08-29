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
package org.easysoa.proxy.core.api.template;

/**
 * @author jguillemotte
 *
 */
public class OutputTemplateField extends AbstractTemplateField {
    
    /**
     * 
     */
    public OutputTemplateField(){
        super();
    }

    @Override
    public int getPathParamPosition() {
        // Unused method in case of output field
        return 0;
    }

    @Override
    public void setPathParamPosition(int pathParamPosition) {
        // Unused method in case of output field        
    }
    
}
