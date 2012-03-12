package org.easysoa.records.correlation;

import java.util.ArrayList;

public class CorrelationLevel {

    private int level;
    private ArrayList<ReqResFieldCorrelation> correlations = new ArrayList<ReqResFieldCorrelation>();

    public CorrelationLevel(int level) {
        this.level = level;
    }

    public void addCorrelation(ReqResFieldCorrelation reqResFieldCorrelation) {
        this.correlations.add(reqResFieldCorrelation);
    }
    
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ArrayList<ReqResFieldCorrelation> getCorrelations() {
        return correlations;
    }


}
