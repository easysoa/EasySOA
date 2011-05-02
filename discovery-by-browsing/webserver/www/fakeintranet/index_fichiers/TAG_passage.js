
var path_tracking='http://tracking.veille-referencement.com/';var path_tracking_secure='https://tracking.veille-referencement.com/';var URL=unescape(window.document.URL);if(URL.indexOf('http://127.0.0.1',0)==-1&&URL.indexOf('file://',0)==-1&&URL.indexOf('http://localhost',0)==-1)
{var id='';var redirect='';var idregie='';var testframe='';var type_page='';var curseur=-1;var curseur2=-1;if(document.getElementById)
{if(document.getElementById("tg_passage_cybercite"))
{var path=document.getElementById("tg_passage_cybercite").src;}}
else if(document.all)
{if(document.all["tg_passage_cybercite"])
{var path=document.all["tg_passage_cybercite"].src;}}
if(path!='')
{curseur=path.lastIndexOf("url=");if(curseur!=-1)
{curseur2=path.indexOf('&',curseur);if(curseur2!=-1)
{redirect=path.substring(curseur+4,curseur2);}
else
{redirect=path.substring(curseur+4);}}
curseur=path.lastIndexOf("idsite");if(curseur!=-1)
{curseur2=path.indexOf('&',curseur);if(curseur2!=-1)
{id=path.substring(curseur+7,curseur2);}
else
{id=path.substring(curseur+7);}}
curseur=path.lastIndexOf("idregie=");if(curseur!=-1)
{curseur2=path.indexOf('&',curseur);if(curseur2!=-1)
{idregie=path.substring(curseur+8,curseur2);}
else
{idregie=path.substring(curseur+8);}}
curseur=path.lastIndexOf("frame=");if(curseur!=-1)
{curseur2=path.indexOf('&',curseur);if(curseur2!=-1)
{testframe=path.substring(curseur+6,curseur2);}
else
{testframe=path.substring(curseur+6);}}
curseur=path.lastIndexOf("type_page=");if(curseur!=-1)
{curseur2=path.indexOf('&',curseur);if(curseur2!=-1)
{type_page=path.substring(curseur+10,curseur2);}
else
{type_page=path.substring(curseur+10);}}
curseur=path.lastIndexOf("https://");if(curseur!=-1)
{path_tracking=path_tracking_secure;}}
var now=new Date();var ts=Math.round(now.getTime());stats="id_regie="+idregie+"&id_site="+id+"&web_url="+escape(window.document.URL);if(testframe=='yes')
{stats+="&web_ref="+escape(window.top.document.referrer);}
else
{stats+="&web_ref="+escape(window.document.referrer);}
if(redirect!='')
{stats+="&web_redirect="+escape(redirect);}
if(type_page!='')
{stats+="&type_page="+escape(type_page);}
stats+="&ts="+ts;if(redirect!='')
{document.write("<SCRIPT LANGUAGE='javascript'>document.location.href='"+path_tracking+"statflow_v2.php?"+stats+"';</SCRIPT>");}
else
{document.write('<div style="position: absolute; top:0px; left:0px; z-index: 2; display:none"><img border="0" height="0" width="0" src="'+path_tracking+'statflow_v2.php?'+stats+'"></div>');}}