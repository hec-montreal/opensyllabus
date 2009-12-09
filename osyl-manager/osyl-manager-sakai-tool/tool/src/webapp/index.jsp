<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=iso-8859-1" pageEncoding="UTF-8"%>
<%@page import="org.sakaiquebec.opensyllabus.manager.server.OsylManagerBackingBean"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.sakaiproject.util.ResourceLoader"%>
<%
	OsylManagerBackingBean osyl = new OsylManagerBackingBean();
	ResourceLoader rb = new ResourceLoader();
	Locale sessionLocale = rb.getLocale();
	String locale = sessionLocale.toString();

%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
	<%
		if (locale.equals("fr_CA")) {
    %>
				<meta name="gwt:property" content="locale=fr_CA"> 
	<%
 		} // if fr_CA
 		else {
 			if (locale.equals("es_ES")) {
 	%>
				<meta name="gwt:property" content="locale=es"> 
	<%
 		} //if es_ES 
 			else {
 	%>
				<meta name="gwt:property" content="locale=en_US"> 
	<%
 		} // else es
 		} // else fr
 	%>
<title>OpenSyllabus Manager</title>
<link rel="stylesheet" type="text/css" href="/osyl-manager-sakai-tool/css/OsylManager.css" />

<!-- This script loads our GWT compiled module.        -->
<!-- Any GWT meta tags must be added before this line. -->
<script language='javascript' src='<%=request.getContextPath() %>/OsylManagerEntryPoint/OsylManagerEntryPoint.nocache.js'></script>
<script> // Size of the JS application
	function myLoad() {
		setTimeout("<%=request.getAttribute("sakai.html.body.onload") %>", 500);
	}

	<%
	String bodyonload = (String)request.getAttribute("sakai.html.body.onload");
	String element = bodyonload.substring(bodyonload.indexOf('\'')+1, bodyonload.lastIndexOf('\''));
	%>
	window.parent.document.getElementById("<%=element%>").style.height="600px";
</script>

</head>
<body onload="myLoad()">
<div class="portletBody">
</div>
</body>
</html>