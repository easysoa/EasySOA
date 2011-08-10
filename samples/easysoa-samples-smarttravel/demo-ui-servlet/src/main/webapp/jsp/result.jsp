<HTML>
<HEAD>
<TITLE>Galaxy DEMO</TITLE>
<link rel="stylesheet" type="text/css" href="css/style.css" /> 
</HEAD>

<BODY>
<div id="center">
  <div id="header"></div>
  <div id="content">
  
  <br/>
  <%= request.getAttribute("response") %>
  <br/>
  <a href="../servlet-bpel">Try another request</a>
  
  <div id="footer">
    <img id="left" src="img/inria.gif"/>
    <img id="right" src="img/j1.png" />
  </div>
  </div>
</div>
</BODY>
</HTML>