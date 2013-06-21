//v.3.0 build 110707

/*
Copyright DHTMLX LTD. http://www.dhtmlx.com
You allowed to use this component or parts of it under GPL terms
To use it on other terms or get Professional edition of the component please contact us at sales@dhtmlx.com
*/
function dhtmlXGridFromTable(a,d){typeof a!="object"&&(a=document.getElementById(a));a.className="";var g=document.createElement("DIV");g.setAttribute("width",a.getAttribute("gridWidth")||(a.offsetWidth?a.offsetWidth+"px":0)||(window.getComputedStyle?window.getComputedStyle(a,null).width:a.currentStyle?a.currentStyle.width:0));g.setAttribute("height",a.getAttribute("gridHeight")||(a.offsetHeight?a.offsetHeight+"px":0)||(window.getComputedStyle?window.getComputedStyle(a,null).height:a.currentStyle?
a.currentStyle.height:0));var b=a,e=a.getAttribute("dragAndDrop");b.parentNode.insertBefore(g,b);var i=b.getAttribute("name")||"name_"+(new Date).valueOf(),c=new dhtmlXGridObject(g);window[i]=c;var j=b.getAttribute("onbeforeinit"),r=b.getAttribute("oninit");j&&eval(j);c.setImagePath(c.imgURL||b.getAttribute("imgpath")||"");d&&d(c);for(var h=b.rows[0],k="",l="",m="",n="",o="",f=0;f<h.cells.length;f++){k+=(k?",":"")+h.cells[f].innerHTML;var p=h.cells[f].getAttribute("width")||h.cells[f].offsetWidth||
(window.getComputedStyle?window.getComputedStyle(h.cells[f],null).width:h.cells[f].currentStyle?h.cells[f].currentStyle.width:0);l+=(l?",":"")+(p=="*"?p:parseInt(p));m+=(m?",":"")+(h.cells[f].getAttribute("align")||"left");n+=(n?",":"")+(h.cells[f].getAttribute("type")||"ed");o+=(o?",":"")+(h.cells[f].getAttribute("sort")||"str");var q=h.cells[f].getAttribute("format");if(q)h.cells[f].getAttribute("type").toLowerCase().indexOf("calendar")!=-1?c._dtmask=q:c.setNumberFormat(q,f)}c.setHeader(k);c.setInitWidths(l);
c.setColAlign(m);c.setColTypes(n);c.setColSorting(o);a.getAttribute("gridHeight")=="auto"&&c.enableAutoHeigth(!0);a.getAttribute("multiline")&&c.enableMultiline(!0);var s=b.getAttribute("lightnavigation");s&&c.enableLightMouseNavigation(s);var t=b.getAttribute("evenrow"),u=b.getAttribute("unevenrow");(t||u)&&c.enableAlterCss(t,u);e&&c.enableDragAndDrop(!0);c.init();a.getAttribute("split")&&c.splitAt(a.getAttribute("split"));c._process_inner_html(b,1);r&&eval(r);a.parentNode&&a.parentNode.removeChild&&
a.parentNode.removeChild(a);return c}dhtmlXGridObject.prototype._process_html=function(a){if(a.tagName&&a.tagName=="TABLE")return this._process_inner_html(a,0);var d=document.createElement("DIV");d.innerHTML=a.xmlDoc.responseText;var g=d.getElementsByTagName("TABLE")[0];this._process_inner_html(g,0)};
dhtmlXGridObject.prototype._process_inner_html=function(a,d){for(var g=a.rows.length,b=d;b<g;b++){var e=a.rows[b].getAttribute("id")||b;this.rowsBuffer.push({idd:e,data:a.rows[b],_parser:this._process_html_row,_locator:this._get_html_data})}this.render_dataset();this.setSizes()};
dhtmlXGridObject.prototype._process_html_row=function(a,d){var g=d.getElementsByTagName("TD"),b=[];a._attrs=this._xml_attrs(d);for(var e=0;e<g.length;e++){var i=g[e],c=i.getAttribute("type");if(a.childNodes[e]){if(c)a.childNodes[e]._cellType=c;a.childNodes[e]._attrs=this._xml_attrs(g[e])}i.firstChild?b.push(i.innerHTML):b.push("");if(i.colSpan>1){a.childNodes[e]._attrs.colspan=i.colSpan;for(var j=1;j<i.colSpan;j++)b.push("")}}for(;e<a.childNodes.length;e++)a.childNodes[e]._attrs={};this._fillRow(a,
this._c_order?this._swapColumns(b):b);return a};dhtmlXGridObject.prototype._get_html_data=function(a,d){for(a=a.firstChild;;){if(!a)return"";a.tagName=="TD"&&d--;if(d<0)break;a=a.nextSibling}return a.firstChild?a.firstChild.data:""};dhtmlxEvent(window,"load",function(){for(var a=document.getElementsByTagName("table"),d=0;d<a.length;d++)a[d].className=="dhtmlxGrid"&&dhtmlXGridFromTable(a[d])});

//v.3.0 build 110707

/*
Copyright DHTMLX LTD. http://www.dhtmlx.com
You allowed to use this component or parts of it under GPL terms
To use it on other terms or get Professional edition of the component please contact us at sales@dhtmlx.com
*/