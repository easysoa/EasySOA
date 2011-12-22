package org.easysoa.records.correlation;

public class ReqResFieldCorrelation {

    private int level;
    private CandidateField inField;
    private CandidateField outField;
    private String info;

    public ReqResFieldCorrelation(int level, CandidateField inField,
            CandidateField outField, String info) {
        this.level = level;
        this.inField = inField;
        this.outField = outField;
        this.info = info;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public CandidateField getInField() {
        return inField;
    }

    public void setInField(CandidateField inField) {
        this.inField = inField;
    }

    public CandidateField getOutField() {
        return outField;
    }

    public void setOutField(CandidateField outField) {
        this.outField = outField;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
