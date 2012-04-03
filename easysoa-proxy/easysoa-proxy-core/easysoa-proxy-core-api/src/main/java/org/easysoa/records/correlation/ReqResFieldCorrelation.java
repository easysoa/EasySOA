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

package org.easysoa.records.correlation;

/**
 * 
 * @author jguillemotte
 *
 */
public class ReqResFieldCorrelation {

    // ?? Why there is a level here and another in CorrelationLevel ???
    private int level;
    // Input field
    private CandidateField inField;
    // Output field
    private CandidateField outField;
    // 
    private String info;

    /**
     * Constructor
     * @param level
     * @param inField
     * @param outField
     * @param info
     */
    public ReqResFieldCorrelation(int level, CandidateField inField, CandidateField outField, String info) {
        this.level = level;
        this.inField = inField;
        this.outField = outField;
        this.info = info;
    }

    /**
     * 
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * 
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 
     * @return
     */
    public CandidateField getInField() {
        return inField;
    }

    /**
     * 
     * @param inField
     */
    public void setInField(CandidateField inField) {
        this.inField = inField;
    }

    /**
     * 
     * @return
     */
    public CandidateField getOutField() {
        return outField;
    }

    /**
     * 
     * @param outField
     */
    public void setOutField(CandidateField outField) {
        this.outField = outField;
    }

    /**
     * 
     * @return
     */
    public String getInfo() {
        return info;
    }

    /**
     * 
     * @param info
     */
    public void setInfo(String info) {
        this.info = info;
    }

}
