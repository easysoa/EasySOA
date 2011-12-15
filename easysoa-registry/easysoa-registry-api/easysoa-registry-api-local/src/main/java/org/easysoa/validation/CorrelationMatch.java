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
