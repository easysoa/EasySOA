
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
         <a href="${Root.path}/tag${tag.path}"><@displayDocShort tag/></a><
		</#macro>


		<#macro displayDocShort doc>
         ${doc['title']} - ${doc['path']}
		</#macro>
		<#macro displayDocsShort docs>
         <#list docs as doc><@displayDocShort doc/> ; </#list>
		</#macro>

		<#macro displayProps1 props propName>
         <#if propName = 'parent'>${props['title']} - ${props['path']}
			<#elseif propName = 'children'><#list props as child>${child['title']} - ${child['path']}</#list>
			<#elseif propName = 'proxies'><#list props as proxy>${proxy['title']} - ${proxy['path']}</#list>
			<#else><@displayProps props propName/></#if>
		</#macro>
		<#macro displayProps props propName>
         <#if !props?has_content>££NON
			<#elseif props?is_string || props?is_number || props?is_boolean>${props}
			<#elseif props?is_date>${props?string("yyyy-MM-dd HH:mm:ss zzzz")}
			<#elseif props?is_hash><#list props?keys as itemName>${itemName} : <#if props[itemName]?has_content><@displayProps1 props[itemName] itemName/></#if> ; </#list>
			<#elseif props?is_sequence>%%<#list props as item><@displayProps1 item propName/> ; </#list>
			<#else>Error : type not supported</#if>
		</#macro>
		<#macro displayDoc doc>
		<ul>
		<#list doc?keys as propName>
			<li>${propName} : <#if doc[propName]?has_content><@displayProps1 doc[propName] propName/><#else>$$</#if></li>
		</#list>
		</ul>
      <#list doc['facets'] as facet>
			<b>${facet}:</b>
			<ul>
			<#-- list facet?keys as propName>
				<li>${propName} : <#if doc[propName]?has_content><@displayProps1 doc[propName] propName/><#else>$$</#if></li>
			</#list -->
			</ul>
		</#list>
		</#macro>
		<#macro displayDocs docs>
         <#list docs as doc>
				<h4><@displayDocShort doc/></h4>
				<@displayDoc doc/>
			</#list>
		</#macro>