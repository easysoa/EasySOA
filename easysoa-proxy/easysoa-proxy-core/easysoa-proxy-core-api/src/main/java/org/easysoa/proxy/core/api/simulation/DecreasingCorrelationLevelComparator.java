/**
 * EasySOA Proxy Core
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

package org.easysoa.proxy.core.api.simulation;

import java.util.Comparator;

import org.easysoa.proxy.core.api.template.AbstractTemplateField;

/**
 * Comparator to sort template field in decreasing order using correlation level
 * 
 * @author jguillemotte
 *
 */
public class DecreasingCorrelationLevelComparator implements Comparator<AbstractTemplateField> {

    @Override
    public int compare(AbstractTemplateField o1, AbstractTemplateField o2) {
        if(o1.getCorrelationLevel() < o2.getCorrelationLevel()){
            return 1;
        } else if(o1.getCorrelationLevel() > o2.getCorrelationLevel()) {
            return -1;
        } else {
            return 0;
        }
    }

}
