/**
 * EasySOA Registry
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

package org.easysoa.validation;

import org.nuxeo.ecm.core.api.DocumentModel;

public class CorrelationMatch implements Comparable<CorrelationMatch> {

    private DocumentModel documentModel;
    
    private double correlationRate;
    
    private String stringValue;
    
    public CorrelationMatch(DocumentModel documentModel, double correlationRate) {
        this.documentModel = documentModel;
        this.correlationRate = correlationRate;
        this.stringValue = String.valueOf(Math.floor(correlationRate * 1000)) + '-' + documentModel.getId();
    }
    
    public DocumentModel getDocumentModel() {
        return documentModel;
    }

    /**
     * @return 0 < n < 1
     */
    public double getCorrelationRate() {
        return correlationRate;
    }

    /**
     * @return Ex: 33.3
     */
    public double getCorrelationRateAsPercentage() {
        return Math.floor(correlationRate * 1000) / 10;
    }
    
    /**
     * @return Ex: "33.3%"
     */
    public String getCorrelationRateAsPercentageString() {
        return String.valueOf(getCorrelationRateAsPercentage()) + "%";
    }

    /**
     * Allows to sort highest correlation rates first
     */
    public int compareTo(CorrelationMatch otherMatch) {
        // Note: We can't only compare correlation rates, since if compareTo() returns 0,
        // any TreeSet that is supposed to hold the objects will not add the new one.
        return toString().compareTo(otherMatch.toString());
    }
    
    public String toString() {
        return stringValue;
    }
}
