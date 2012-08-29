/**
 * EasySOA Proxy core
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

package org.easysoa.proxy.core.api.records.correlation;

import java.util.ArrayList;

/**
 * 
 * @author jguillemotte
 *
 */
public class CorrelationLevel {

    // Correlation level
    private int level;
    // Correlation list
    private ArrayList<ReqResFieldCorrelation> correlations = new ArrayList<ReqResFieldCorrelation>();

    /**
     * Constructor
     * @param level
     */
    public CorrelationLevel(int level) {
        this.level = level;
    }

    /**
     * Add a correlation to the list
     * @param reqResFieldCorrelation
     */
    public void addCorrelation(ReqResFieldCorrelation reqResFieldCorrelation) {
        this.correlations.add(reqResFieldCorrelation);
    }
    
    /**
     * Level getter
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * Level setter
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Returns the correlation list
     * @return
     */
    public ArrayList<ReqResFieldCorrelation> getCorrelations() {
        return correlations;
    }


}
