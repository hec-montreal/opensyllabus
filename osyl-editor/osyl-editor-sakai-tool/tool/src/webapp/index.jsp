<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.sakaiquebec.opensyllabus.server.OsylBackingBean"%>
<%@ page import="org.sakaiquebec.opensyllabus.shared.model.COSerialized"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.sakaiquebec.opensyllabus.shared.api.SecurityInterface"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="org.sakaiproject.util.ResourceLoader"%>
<%@ page import="java.io.File"%>

<%
	WebApplicationContext context = WebApplicationContextUtils
			.getWebApplicationContext(application);
	OsylBackingBean osylMainBean = (OsylBackingBean) context
			.getBean("osylMainBean");

	// String userGroup = osylMainBean.getOsylSecurityService()
	// 		.getCurrentUserGroup();
	String userRole = osylMainBean.getOsylSecurityService()
			.getCurrentUserRole();
	String webappDir = getServletContext().getRealPath("/");

	ResourceLoader rb = new ResourceLoader();

	// This initialization procedures should have been done at the site creation.
	// We need a full maintain access to call them. Since they're called at each tool access,
	// let's make sure that only a user with maintain rights will init the tool
	if (osylMainBean.getOsylSecurityService().isAllowedToEdit(osylMainBean.getOsylSiteService().getCurrentSiteId())
			|| SecurityInterface.SECURITY_ROLE_PROJECT_MAINTAIN
					.equals(userRole)
			|| SecurityInterface.SECURITY_ROLE_COURSE_MAINTAIN
					.equals(userRole)) {
		osylMainBean.getOsylService().initService();
	}

	// userGroup = osylMainBean.getOsylSecurityService()
	// 		.getCurrentUserGroup();

	Locale sessionLocale = rb.getLocale();
	String locale = sessionLocale.toString();
%>
        
<html>
	<head>

	<!-- AJAXSLT -->
	<script src='<%=request.getContextPath() %>/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/js/ajaxslt-0.8.1/util.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/js/ajaxslt-0.8.1/xmltoken.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/js/ajaxslt-0.8.1/dom.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/js/ajaxslt-0.8.1/xpath.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/js/ajaxslt-0.8.1/xslt.js' type="text/javascript"></script>
	
	<!--                                           -->
	<!-- Sets the parameters of the EntryPoint     -->
	<!--                                           -->
	<%
		if (request.getParameter("sg") != null) {
	%>
	    <meta name="osyl:sg" content="<%= request.getParameter("sg") %>"/>
	<%
		}

		if (("true").equalsIgnoreCase(request.getParameter("ro"))) {
	%>
 			<meta name="osyl:ro" content="true"> 
    <%
     	} else if (osylMainBean.getOsylSecurityService().isAllowedToEdit(osylMainBean.getOsylSiteService().getCurrentSiteId())) {
     %>
 	        <meta name="osyl:ro" content="false"> 
    <%
     	} else {
     %>
            <meta name="osyl:ro" content="true">
    <%
    	}

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
	
		<title>OpenSyllabus</title>
		
		<!-- Headers from Sakai                        -->
		<%=request.getAttribute("sakai.html.head")%>

		<style>
			body,td,a,div,.p{font-family:arial,sans-serif}
			div,td{color:#000000}
			a:link,.w,.w a:link{color:#0000cc}
			a:visited{color:#551a8b}
			a:active{color:#ff0000}
		</style>

	    <%
	    	//This script gets the right css from the database considering the config.
	    	COSerialized co = osylMainBean.getOsylSiteService()
	    			.getSerializedCourseOutline(webappDir);
	    	Object configSiteProperty = osylMainBean.getOsylSiteService().getSiteConfigProperty(co.getSiteId());
	    	String configId = "";
	    	if (configSiteProperty == null) {
	    		configId = co.getOsylConfig().getConfigId();
	    	} else {
	    		configId = osylMainBean.getOsylConfigService().getConfigByRef(
					"osylcoconfigs"	+ File.separator
					+ configSiteProperty.toString(), webappDir).getConfigId();
	    	}
	    	String cssPath = osylMainBean.getOsylConfigService().getConfig(
	    			configId, webappDir).getCascadingStyleSheetURI();
	    %>
		<link rel="stylesheet" type="text/css" href="<%=cssPath%>" />
		<script>
			function isReadOnlyUI() {
				return true;
			}
		</script>
		
		<!-- This script loads our GWT compiled module.        -->
		<!-- Any GWT meta tags must be added before this line. -->
		<script language='javascript' src='<%=request.getContextPath() %>/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint/org.sakaiquebec.opensyllabus.OsylEditorEntryPoint.nocache.js'></script>

		<script> // Size of the JS application
			function myLoad() {
				setTimeout("<%=request.getAttribute("sakai.html.body.onload") %>", 500);
			}
		</script>
	</head>

	<body onload="myLoad()">
	</body>
</html>
