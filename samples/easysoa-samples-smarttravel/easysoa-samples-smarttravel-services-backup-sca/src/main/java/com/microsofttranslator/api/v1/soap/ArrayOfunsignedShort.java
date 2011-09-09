
package com.microsofttranslator.api.v1.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfunsignedShort complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfunsignedShort">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="unsignedShort" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfunsignedShort", propOrder = {
    "unsignedShort"
})
public class ArrayOfunsignedShort {

    @XmlElement(type = Integer.class)
    @XmlSchemaType(name = "unsignedShort")
    protected List<Integer> unsignedShort;

    /**
     * Gets the value of the unsignedShort property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unsignedShort property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnsignedShort().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getUnsignedShort() {
        if (unsignedShort == null) {
            unsignedShort = new ArrayList<Integer>();
        }
        return this.unsignedShort;
    }

}
