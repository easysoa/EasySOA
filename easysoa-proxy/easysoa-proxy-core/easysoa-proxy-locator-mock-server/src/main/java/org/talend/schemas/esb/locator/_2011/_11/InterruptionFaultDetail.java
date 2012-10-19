
package org.talend.schemas.esb.locator._2011._11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="InterruptionDetail" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "interruptionDetail"
})
@XmlRootElement(name = "InterruptionFaultDetail")
public class InterruptionFaultDetail {

    @XmlElement(name = "InterruptionDetail", required = true)
    protected String interruptionDetail;

    /**
     * Gets the value of the interruptionDetail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterruptionDetail() {
        return interruptionDetail;
    }

    /**
     * Sets the value of the interruptionDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterruptionDetail(String value) {
        this.interruptionDetail = value;
    }

}
