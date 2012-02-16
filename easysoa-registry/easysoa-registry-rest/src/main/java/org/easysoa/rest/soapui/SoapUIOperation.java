package org.easysoa.rest.soapui;

public class SoapUIOperation {

    String name = "";
    
    String outputName = "";
    
    String inputName = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        if (outputName != null) {
            this.outputName = outputName;
        }
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        if (inputName != null) {
            this.inputName = inputName;
        }
    }
    
}
