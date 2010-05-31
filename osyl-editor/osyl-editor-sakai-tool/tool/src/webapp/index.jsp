<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.Locale"%>
<%@ page import="org.sakaiquebec.opensyllabus.server.OsylBackingBean"%>
<%@ page import="org.sakaiquebec.opensyllabus.shared.model.COSerialized"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.sakaiquebec.opensyllabus.common.api.OsylSecurityService"%>
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
			|| OsylSecurityService.SECURITY_ROLE_PROJECT_MAINTAIN
					.equals(userRole)
			|| OsylSecurityService.SECURITY_ROLE_COURSE_INSTRUCTOR
					.equals(userRole)) {
		osylMainBean.getOsylService().initService();
	}

	// userGroup = osylMainBean.getOsylSecurityService()
	// 		.getCurrentUserGroup();

	Locale sessionLocale = rb.getLocale();
	String locale = sessionLocale.toString();
%>
        

<%@page import="org.sakaiquebec.opensyllabus.common.api.OsylConfigService"%>
<%@page import="org.sakaiquebec.opensyllabus.shared.model.COConfig"%><html>
	<head>

	<!-- AJAXSLT -->
	<script src='<%=request.getContextPath() %>/OsylEditorEntryPoint/js/ajaxslt-0.8.1/util.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/OsylEditorEntryPoint/js/ajaxslt-0.8.1/xmltoken.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/OsylEditorEntryPoint/js/ajaxslt-0.8.1/dom.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/OsylEditorEntryPoint/js/ajaxslt-0.8.1/xpath.js' type="text/javascript"></script>
    <script src='<%=request.getContextPath() %>/OsylEditorEntryPoint/js/ajaxslt-0.8.1/xslt.js' type="text/javascript"></script>
	
	<!--                                           -->
	<!-- Sets the parameters of the EntryPoint     -->
	<!--                                           -->
	<%
		boolean readonly=false;
		if (request.getParameter("sg") != null) {
	%>
	    <meta name="osyl:sg" content="<%= request.getParameter("sg") %>"/>
	<%
		}

		if (("true").equalsIgnoreCase(request.getParameter("ro"))) {
		    readonly=true;
	%>
 			<meta name="osyl:ro" content="true"> 
    <%
     	} else if (osylMainBean.getOsylSecurityService().isAllowedToEdit(osylMainBean.getOsylSiteService().getCurrentSiteId())) {
     	    readonly=false;
     %>
 	        <meta name="osyl:ro" content="false"> 
    <%
     	} else {
     		readonly=true;
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
	    	String configId = co.getOsylConfig().getConfigId();
	    	String cssPath = osylMainBean.getOsylConfigService().getConfig(
	    			configId, webappDir).getCascadingStyleSheetPath();
	    %>
		<link rel="stylesheet" type="text/css" href="<%=cssPath+COConfig.MAIN_CSS%>" />
		<link rel="stylesheet" type="text/css" href="<%=cssPath+COConfig.PRINT_CSS%>" media="print"/>
		<link rel="stylesheet" type="text/css" href="<%=cssPath+COConfig.READONLY_CSS%>"/>
		<script>
			function isReadOnlyUI() {
				return true;
			}
		</script>
		
		<!-- This script loads our GWT compiled module.        -->
		<!-- Any GWT meta tags must be added before this line. -->
		<script language='javascript' src='<%=request.getContextPath() %>/OsylEditorEntryPoint/OsylEditorEntryPoint.nocache.js'></script>

		<script> // Size of the JS application
			function myLoad() {
				setTimeout("<%=request.getAttribute("sakai.html.body.onload") %>", 500);
			}
		</script>
	</head>

	<body onload="myLoad()">
	</body>
</html>
