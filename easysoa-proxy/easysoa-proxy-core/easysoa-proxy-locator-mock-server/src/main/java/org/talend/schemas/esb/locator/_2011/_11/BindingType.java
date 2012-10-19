
package org.talend.schemas.esb.locator._2011._11;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BindingType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BindingType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="SOAP11"/>
 *     &lt;enumeration value="SOAP12"/>
 *     &lt;enumeration value="JAXRS"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BindingType")
@XmlEnum
public enum BindingType {

    @XmlEnumValue("SOAP11")
    SOAP_11("SOAP11"),
    @XmlEnumValue("SOAP12")
    SOAP_12("SOAP12"),
    JAXRS("JAXRS"),
    OTHER("OTHER");
    private final String value;

    BindingType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BindingType fromValue(String v) {
        for (BindingType c: BindingType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
