<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.sakaiquebec.opensyllabus.admin.server.*" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<link media="all" href="css/opensyllabus-admin-tool.css" rel="stylesheet" type="text/css"/>
<title>OpenSyllabus Administrator</title>

<!-- This script loads our GWT compiled module.        -->
<!-- Any GWT meta tags must be added before this line. -->
<script language='javascript' src='<%=request.getContextPath() %>/OsylAdminEntryPoint/OsylAdminEntryPoint.nocache.js'></script>
<script> // Size of the JS application
	function myLoad() {
		OsylAdminBackingBean osyl = new OsylAdminBackingBean();
		setTimeout("<%=request.getAttribute("sakai.html.body.onload") %>", 500);
	}

	<%
	String bodyonload = (String)request.getAttribute("sakai.html.body.onload");
	String element = bodyonload.substring(bodyonload.indexOf('\'')+1, bodyonload.lastIndexOf('\''));
	%>
	window.parent.document.getElementById("<%=element%>").style.height="300px";
</script>

</head>
<body onload="onload="myLoad()">
<div class="portletBody">


</div>
</body>
</html>