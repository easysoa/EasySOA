/**
 * EasySOA Samples - Smart Travel
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */


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
 *         &lt;element name="provider" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="srcCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dstCurrency" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "provider",
    "srcCurrency",
    "dstCurrency"
})
@XmlRootElement(name = "getCurrencyValue")
public class GetCurrencyValue {

    protected String provider;
    protected String srcCurrency;
    protected String dstCurrency;

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvider(String value) {
        this.provider = value;
    }

    /**
     * Gets the value of the srcCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcCurrency() {
        return srcCurrency;
    }

    /**
     * Sets the value of the srcCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcCurrency(String value) {
        this.srcCurrency = value;
    }

    /**
     * Gets the value of the dstCurrency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDstCurrency() {
        return dstCurrency;
    }

    /**
     * Sets the value of the dstCurrency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDstCurrency(String value) {
        this.dstCurrency = value;
    }

}
