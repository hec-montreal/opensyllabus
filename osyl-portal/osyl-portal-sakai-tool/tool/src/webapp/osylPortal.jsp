<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import=" java.util.Locale" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />


<!-- Utiliser le francais par default -->
<meta name="gwt:property" content="locale=fr_CA"> 


<link media="all" href="css/opensyllabus-portal-tool.css" rel="stylesheet" type="text/css"/>
<title>OpenSyllabus Portal</title>

<!-- This script loads our GWT compiled module.        -->
<!-- Any GWT meta tags must be added before this line. -->
<script language='javascript' src='<%=request.getContextPath() %>/OsylPortalEntryPoint/OsylPortalEntryPoint.nocache.js'></script>
<script> // Size of the JS application
	function myLoad() {
		setTimeout("<%=request.getAttribute("sakai.html.body.onload") %>", 500);
	}
</script>
</head>
<body onload="myLoad()">
	<iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>
</body>
</html>