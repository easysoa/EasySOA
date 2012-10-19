
package org.talend.schemas.esb.locator._2011._11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for lookupRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="lookupRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="matcherData" type="{http://talend.org/schemas/esb/locator/2011/11}MatcherDataType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lookupRequestType", propOrder = {
    "serviceName",
    "matcherData"
})
public class LookupRequestType {

    @XmlElement(required = true)
    protected QName serviceName;
    protected MatcherDataType matcherData;

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link QName }
     *     
     */
    public QName getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link QName }
     *     
     */
    public void setServiceName(QName value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the matcherData property.
     * 
     * @return
     *     possible object is
     *     {@link MatcherDataType }
     *     
     */
    public MatcherDataType getMatcherData() {
        return matcherData;
    }

    /**
     * Sets the value of the matcherData property.
     * 
     * @param value
     *     allowed object is
     *     {@link MatcherDataType }
     *     
     */
    public void setMatcherData(MatcherDataType value) {
        this.matcherData = value;
    }

}
