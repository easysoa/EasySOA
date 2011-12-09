package org.easysoa.validation;

import org.nuxeo.ecm.core.api.DocumentModel;

public class CorrelationMatch implements Comparable<CorrelationMatch> {

    private DocumentModel documentModel;
    
    private double correlationRate;
    
    public CorrelationMatch(DocumentModel documentModel, double correlationRate) {
        this.documentModel = documentModel;
        this.correlationRate = correlationRate;
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
        return String.valueOf(correlationRate) + "%";
    }

    /**
     * Allows to sort highest correlation rates first
     */
    public int compareTo(CorrelationMatch otherMatch) {
        return (int) Math.ceil((otherMatch.getCorrelationRate() - correlationRate)*1000);
    }
}
