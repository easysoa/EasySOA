package org.easysoa.registry.indicators.rest;

public class IndicatorValue {
    
    private int count;
    private int percentage;

    /**
     * 
     * @param count The indicator value, or -1 if not relevant
     * @param percentage The indicator value in percents, or -1 if not relevant
     */
    public IndicatorValue(int count, int percentage) {
        this.count = count;
        this.percentage = percentage;
    }
    
    /**
     * 
     * @return The indicator value, or -1 if not relevant
     */
    public int getCount() {
        return count;
    }
    
    /**
     * 
     * @return The indicator value in percents, or -1 if not relevant
     */
    public int getPercentage() {
        return percentage;
    }
    
    @Override
    public String toString() {
        String countString = (count != -1) ? Integer.toString(count) : "N.A.";
        String percentageString = (percentage != -1) ? Integer.toString(percentage) + "%" : "N.A.";
        return "[" + countString + " / " + percentageString + "]";
    }

}
