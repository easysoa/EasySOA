//v.3.0 build 110707

/*
Copyright DHTMLX LTD. http://www.dhtmlx.com
You allowed to use this component or parts of it under GPL terms
To use it on other terms or get Professional edition of the component please contact us at sales@dhtmlx.com
*/
function jsonPointer(a,b){this.d=a;this.dp=b}
jsonPointer.prototype={text:function(){var a=function(a){for(var e=[],c=0;c<a.length;c++)e.push("{"+b(a[c])+"}");return e.join(",")},b=function(d){var e=[],c;for(c in d)typeof d[c]=="object"?c.length?e.push('"'+c+'":['+a(d[c])+"]"):e.push('"'+c+'":{'+b(d[c])+"}"):e.push('"'+c+'":"'+d[c]+'"');return e.join(",")};return"{"+b(this.d)+"}"},get:function(a){return this.d[a]},exists:function(){return!!this.d},content:function(){return this.d.content},each:function(a,b,d){var e=this.d[a],c=new jsonPointer;
if(e)for(var f=0;f<e.length;f++)c.d=e[f],b.apply(d,[c,f])},get_all:function(){return this.d},sub:function(a){return new jsonPointer(this.d[a],this.d)},sub_exists:function(a){return!!this.d[a]},each_x:function(a,b,d,e,c){var f=this.d[a],g=new jsonPointer(0,this.d);if(f)for(c=c||0;c<f.length;c++)if(f[c][b]&&(g.d=f[c],d.apply(e,[g,c])==-1))break},up:function(){return new jsonPointer(this.dp,this.d)},set:function(a,b){this.d[a]=b},clone:function(){return new jsonPointer(this.d,this.dp)},through:function(a,
b,d,e,c){var f=this.d[a];if(f.length)for(var g=0;g<f.length;g++){if(f[g][b]!=null&&f[g][b]!=""&&(!d||f[g][b]==d)){var h=new jsonPointer(f[g],this.d);e.apply(c,[h,g])}var i=this.d;this.d=f[g];this.sub_exists(a)&&this.through(a,b,d,e,c);this.d=i}}};
dhtmlXTreeObject.prototype.loadJSArrayFile=function(a,b){this.parsCount||this.callEvent("onXLS",[this,this._ld_id]);this._ld_id=null;this.xmlstate=1;var d=this;this.XMLLoader=new dtmlXMLLoaderObject(function(a,c,b,g,h){eval("var z="+h.xmlDoc.responseText);d.loadJSArray(z)},this,!0,this.no_cashe);if(b)this.XMLLoader.waitCall=b;this.XMLLoader.loadXML(a)};
dhtmlXTreeObject.prototype.loadCSV=function(a,b){this.parsCount||this.callEvent("onXLS",[this,this._ld_id]);this._ld_id=null;this.xmlstate=1;var d=this;this.XMLLoader=new dtmlXMLLoaderObject(function(a,c,b,g,h){d.loadCSVString(h.xmlDoc.responseText)},this,!0,this.no_cashe);if(b)this.XMLLoader.waitCall=b;this.XMLLoader.loadXML(a)};
dhtmlXTreeObject.prototype.loadJSArray=function(a,b){for(var d=[],e=0;e<a.length;e++)d[a[e][1]]||(d[a[e][1]]=[]),d[a[e][1]].push({id:a[e][0],text:a[e][2]});var c={id:this.rootId},f=function(a,c){if(d[a.id]){a.item=d[a.id];for(var b=0;b<a.item.length;b++)c(a.item[b],c)}};f(c,f);this.loadJSONObject(c,b)};
dhtmlXTreeObject.prototype.loadCSVString=function(a,b){for(var d=[],e=a.split("\n"),c=0;c<e.length;c++){var f=e[c].split(",");d[f[1]]||(d[f[1]]=[]);d[f[1]].push({id:f[0],text:f[2]})}var g={id:this.rootId},h=function(a,c){if(d[a.id]){a.item=d[a.id];for(var b=0;b<a.item.length;b++)c(a.item[b],c)}};h(g,h);this.loadJSONObject(g,b)};
dhtmlXTreeObject.prototype.loadJSONObject=function(a,b){this.parsCount||this.callEvent("onXLS",[this,null]);this.xmlstate=1;var d=new jsonPointer(a);this._parse(d);this._p=d;b&&b()};
dhtmlXTreeObject.prototype.loadJSON=function(a,b){this.parsCount||this.callEvent("onXLS",[this,this._ld_id]);this._ld_id=null;this.xmlstate=1;var d=this;this.XMLLoader=new dtmlXMLLoaderObject(function(a,c,b,g,h){try{eval("var t="+h.xmlDoc.responseText)}catch(i){dhtmlxError.throwError("LoadXML","Incorrect JSON",[h.xmlDoc,this]);return}var j=new jsonPointer(t);d._parse(j);d._p=j},this,!0,this.no_cashe);if(b)this.XMLLoader.waitCall=b;this.XMLLoader.loadXML(a)};
dhtmlXTreeObject.prototype.serializeTreeToJSON=function(){for(var a=['{"id":"'+this.rootId+'", "item":['],b=[],d=0;d<this.htmlNode.childsCount;d++)b.push(this._serializeItemJSON(this.htmlNode.childNodes[d]));a.push(b.join(","));a.push("]}");return a.join("")};
dhtmlXTreeObject.prototype._serializeItemJSON=function(a){var b=[];if(a.unParsed)return a.unParsed.text();var d=this._selected.length?this._selected[0].id:"",e=a.span.innerHTML;if(this._xescapeEntities)for(var c=0;c<this._serEnts.length;c++)e=e.replace(this._serEnts[c][2],this._serEnts[c][1]);this._xfullXML?b.push('{ "id":"'+a.id+'", '+(this._getOpenState(a)==1?' "open":"1", ':"")+(d==a.id?' "select":"1",':"")+' "text":"'+e+'", "im0":"'+a.images[0]+'", "im1":"'+a.images[1]+'", "im2":"'+a.images[2]+
'" '+(a.acolor?', "aCol":"'+a.acolor+'" ':"")+(a.scolor?', "sCol":"'+a.scolor+'" ':"")+(a.checkstate==1?', "checked":"1" ':a.checkstate==2?', "checked":"-1"':"")+(a.closeable?', "closeable":"1" ':"")+(this.XMLsource&&a.XMLload==0?', "child":"1" ':"")):b.push('{ "id":"'+a.id+'", '+(this._getOpenState(a)==1?' "open":"1", ':"")+(d==a.id?' "select":"1",':"")+' "text":"'+e+'"'+(this.XMLsource&&a.XMLload==0?', "child":"1" ':""));if(this._xuserData&&a._userdatalist){b.push(', "userdata":[');for(var f=a._userdatalist.split(","),
g=[],c=0;c<f.length;c++)g.push('{ "name":"'+f[c]+'" , "content":"'+a.userData["t_"+f[c]]+'" }');b.push(g.join(","));b.push("]")}if(a.childsCount){b.push(', "item":[');g=[];for(c=0;c<a.childsCount;c++)g.push(this._serializeItemJSON(a.childNodes[c]));b.push(g.join(","));b.push("]\n")}b.push("}\n");return b.join("")};

//v.3.0 build 110707

/*
Copyright DHTMLX LTD. http://www.dhtmlx.com
You allowed to use this component or parts of it under GPL terms
To use it on other terms or get Professional edition of the component please contact us at sales@dhtmlx.com
*/