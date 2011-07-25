
package de.daenet.webservices.currencyserver;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="getDollarValueResult" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getDollarValueResult"
})
@XmlRootElement(name = "getDollarValueResponse")
public class GetDollarValueResponse {

    protected double getDollarValueResult;

    /**
     * Gets the value of the getDollarValueResult property.
     * 
     */
    public double getGetDollarValueResult() {
        return getDollarValueResult;
    }

    /**
     * Sets the value of the getDollarValueResult property.
     * 
     */
    public void setGetDollarValueResult(double value) {
        this.getDollarValueResult = value;
    }

}
