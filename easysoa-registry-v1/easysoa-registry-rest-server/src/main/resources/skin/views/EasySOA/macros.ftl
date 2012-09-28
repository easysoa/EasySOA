
		<#macro displayServiceShort service>
         <a href="${Root.path}/path${service.path}"><@displayDocShort service/></a>
		</#macro>
		<#macro displayServicesShort services>
			<ul>
			<#list services as service>
				<li><@displayServiceShort service/></li>
			</#list>
			</ul>
		</#macro>
		<#macro displayTagShort tag>
         <a href="${Root.path}/tag${tag.path}">${tag['title']} (<#if tag.children?has_content>${tag['children']?size}<#else>0</#if>) - ${tag['path']}</a>
		</#macro>


		<#macro displayDocShort doc>
         ${doc['title']} - ${doc['path']}
		</#macro>
		<#macro displayDocsShort docs>
         <#list docs as doc><@displayDocShort doc/> ; </#list>
		</#macro>

<#assign documentPropNames=["lifeCyclePolicy", "versionLabel", "facets", "children", "type", "sourceId", "id", "author", "title", "repository", "created", "name", "path", "schemas", "parent", "lifeCycleState", "allowedStateTransitions", "isLocked", "modified", "content", "ref", "versions", "isFolder", "sessionId", "session", "proxies"]/>
<#assign shortDocumentPropNames=["title", "type", "path", "author", "versionLabel"]/>
<#assign mostDocumentPropNames=["title", "type", "name", "path", "author", "created", "versionLabel", "isLocked", "modified",  "id"]/>
<#assign deliverablePropNames=["soav:version", "del:nature", "del:application", "del:location", "del:dependencies"]/>
<#assign serviceImplementationPropNames=["impl:technology", "impl:operations", "impl:documentation", "impl:ismock", "impl:tests"]/>
<#assign serviceImplementationPropHashNames=["operationName", "operationParameters", "operationDocumentation"]/>
<#assign deployedDeliverablePropNames=["soan:name"]/>
<#assign endpointPropNames=["env:environment", "endp:url"]/>
<#assign intelligentSystemTreeRootPropNames=["istr:classifier", "istr:parameters"]/>
<#assign soaNodePropNames=["soan:name"]/>
<#assign allPropHashNames=serviceImplementationPropHashNames/>

		<#macro displayProps1 props propName>
         <#if propName = 'parent'>${props['title']} - ${props['path']}
			<#elseif propName = 'children'><#list props as child>${child['title']} - ${child['path']}</#list>
			<#elseif propName = 'proxies'><#list props as proxy>${proxy['title']} - ${proxy['path']}</#list>
			<#else><@displayProps props propName/></#if>
		</#macro>
		<#macro displayProps props propName>
         <#if !props?has_content>
			<#elseif props?is_string || props?is_number || props?is_boolean>${props}
			<#elseif props?is_date>${props?string("yyyy-MM-dd HH:mm:ss zzzz")}
			<#elseif props?is_hash><@displayPropsHash props/>
			<#elseif props?is_sequence>%%<#list props as item><@displayProps1 item propName/> ; </#list>
			<#else>Error : type not supported</#if>
		</#macro>
		<#macro displayPropsHash props propHashNames=allPropHashNames>
         <#list propHashNames as itemName><#if props[itemName]?has_content && !props[itemName]?is_method>${itemName} : <@displayProps1 props[itemName] itemName/></#if> ; </#list>
		</#macro>
		<#macro displayDoc doc propNames=documentPropNames>
		<ul>
		<#list propNames as propName>
			<li>${propName} : <#if doc[propName]?has_content><@displayProps1 doc[propName] propName/><#else>$$</#if></li>
		</#list>
		</ul>
      <#-- list doc['facets'] as facet>
			<b>${facet}:</b>
			<ul>
			<#list facet?keys as propName>WARNING CAN'T WORK
				<li>${propName} : <#if doc[propName]?has_content><@displayProps1 doc[propName] propName/><#else>$$</#if></li>
			</#list>
			</ul>
		</#list -->
		</#macro>
		<#macro displayDocs docs propNames=documentPropNames>
         <#list docs as doc>
				<h4><@displayDocShort doc/></h4>
				<@displayDoc doc propNames/>
			</#list>
		</#macro>