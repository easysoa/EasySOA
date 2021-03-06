/**
 * EasySOA Proxy
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

package org.easysoa.wsdl.twitter_test_run_wsdl;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 * This class was generated by Apache CXF 2.4.1
 * 2012-01-25T10:02:41.899+01:00
 * Generated source version: 2.4.1
 * 
 */
@WebService(targetNamespace = "http://www.easysoa.org/wsdl/Twitter_test_run.wsdl", name = "Twitter_test_run_PortType")
@XmlSeeAlso({org.easysoa.twitter_test_run.ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface TwitterTestRunPortType {

    @WebResult(name = "resTemplateRecord_2Response", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd", partName = "body")
    @WebMethod(operationName = "resTemplateRecord_2", action = "resTemplateRecord_2")
    public org.easysoa.twitter_test_run.ResTemplateRecord2Response resTemplateRecord2(
        @WebParam(partName = "body", name = "resTemplateRecord_2Request", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd")
        org.easysoa.twitter_test_run.ResTemplateRecord2Request body
    );

    @WebResult(name = "reqTemplateRecord_2Response", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd", partName = "body")
    @WebMethod(operationName = "reqTemplateRecord_2", action = "reqTemplateRecord_2")
    public org.easysoa.twitter_test_run.ReqTemplateRecord2Response reqTemplateRecord2(
        @WebParam(partName = "body", name = "reqTemplateRecord_2Request", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd")
        org.easysoa.twitter_test_run.ReqTemplateRecord2Request body
    );

    @WebResult(name = "reqTemplateRecord_1Response", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd", partName = "body")
    @WebMethod(operationName = "reqTemplateRecord_1", action = "reqTemplateRecord_1")
    public org.easysoa.twitter_test_run.ReqTemplateRecord1Response reqTemplateRecord1(
        @WebParam(partName = "body", name = "reqTemplateRecord_1Request", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd")
        org.easysoa.twitter_test_run.ReqTemplateRecord1Request body
    );

    @WebResult(name = "resTemplateRecord_1Response", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd", partName = "body")
    @WebMethod(operationName = "resTemplateRecord_1", action = "resTemplateRecord_1")
    public org.easysoa.twitter_test_run.ResTemplateRecord1Response resTemplateRecord1(
        @WebParam(partName = "body", name = "resTemplateRecord_1Request", targetNamespace = "http://easysoa.org/Twitter_test_run.xsd")
        org.easysoa.twitter_test_run.ResTemplateRecord1Request body
    );
}
