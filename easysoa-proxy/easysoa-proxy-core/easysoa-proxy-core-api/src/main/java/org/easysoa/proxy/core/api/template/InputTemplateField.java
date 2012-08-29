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
public class InputTemplateField extends AbstractTemplateField {

    private int pathParamPosition;
    // Indicate if the field have to be processed by the assertion engine
    
    public InputTemplateField(){
        super();
        pathParamPosition = 0;
    }
    
    /**
     * 
     * @return
     */
    public int getPathParamPosition() {
        return pathParamPosition;
    }

    /**
     * Number to define the parameter position in url path (eg : for http://localhost:8088/1/users/show/FR3Aquitaine.xml, the param user correspond to number 4 (FR3Aquitaine.xml)), the first '/' represent the root of the path. 
     * @param pathParamPosition
     */
    public void setPathParamPosition(int pathParamPosition) {
        this.pathParamPosition = pathParamPosition;
    }

}
