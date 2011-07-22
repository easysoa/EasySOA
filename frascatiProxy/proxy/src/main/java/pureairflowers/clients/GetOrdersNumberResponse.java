
package pureairflowers.clients;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getOrdersNumberResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getOrdersNumberResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ordersNumber" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getOrdersNumberResponse", propOrder = {
    "ordersNumber"
})
public class GetOrdersNumberResponse {

    protected int ordersNumber;

    /**
     * Gets the value of the ordersNumber property.
     * 
     */
    public int getOrdersNumber() {
        return ordersNumber;
    }

    /**
     * Sets the value of the ordersNumber property.
     * 
     */
    public void setOrdersNumber(int value) {
        this.ordersNumber = value;
    }

}
