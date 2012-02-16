<?xml version="1.0" encoding="UTF-8"?>
<con:soapui-project name="Test" resourceRoot="" soapui-version="4.0.1" xmlns:con="http://eviware.com/soapui/config">
  <con:settings />

  <#list wsdls as wsdl>
    <con:interface xsi:type="con:WsdlInterface" wsaVersion="NONE" name="${wsdl.getName()}"
      type="wsdl" bindingName="${wsdl.getBindingName()}" soapVersion="1_1" anonymous="optional"
      definition="${wsdl.getUrl()}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <con:settings />
      <con:definitionCache type="TEXT" rootPart="${wsdl.getUrl()}">
        <con:part>
          <con:url>${wsdl.getUrl()}</con:url>
          <con:content><![CDATA[
            ${wsdl.getContents()}
            ]]></con:content>
          <con:type>http://schemas.xmlsoap.org/wsdl/</con:type>
        </con:part>
      </con:definitionCache>
      <con:endpoints>
        <con:endpoint>${wsdl.getEndpointUrl()}</con:endpoint>
      </con:endpoints>
      <#list wsdl.getOperations() as operation>
        <con:operation isOneWay="false" action="" name="${operation.getName()}" bindingOperationName="${operation.getName()}" 
          type="Request-Response" outputName="${operation.getOutputName()}" inputName="${operation.getInputName()}"
          receivesAttachments="false" sendsAttachments="false" anonymous="optional">
          <con:settings />
        </con:operation>
      </#list>
    </con:interface>
  </#list>

  <con:properties />
  <con:wssContainer />
</con:soapui-project>