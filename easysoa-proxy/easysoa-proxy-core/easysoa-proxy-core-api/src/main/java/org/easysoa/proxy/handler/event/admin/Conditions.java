/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.handler.event.admin;

/**
 *
 * @author fntangke
 */
public class Conditions {
    
    private String jxPathConditions;
    private String regexConditions;

    public Conditions() {
    }
    
    public Conditions(String urlConditions) {
        //this.jxPathConditions = jxPathConditions;
        this.regexConditions = urlConditions.concat(".*");
    }

    /**
     * @return the jxPathConditions
     */
    public String getJxPathConditions() {
        return jxPathConditions;
    }

    /**
     * @param jxPathConditions the jxPathConditions to set
     */
    public void setJxPathConditions(String jxPathConditions) {
        this.jxPathConditions = jxPathConditions;
    }

    /**
     * @return the regexConditions
     */
    public String getRegexConditions() {
        return regexConditions;
    }

    /**
     * @param regexConditions the regexConditions to set
     */
    public void setRegexConditions(String regexConditions) {
        this.regexConditions = regexConditions;
    }
    
}
