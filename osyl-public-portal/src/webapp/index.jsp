<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.sakaiquebec.opensyllabus.publicportal.server.OsylPublicPortalBackingBean"%>
<%@ page import="org.springframework.web.context.WebApplicationContext"%>
<%@ page import="org.sakaiquebec.opensyllabus.common.api.OsylSiteService"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ page import="java.util.List" %>

<%
	WebApplicationContext context = WebApplicationContextUtils
			.getWebApplicationContext(application);
	OsylPublicPortalBackingBean osylMainBean = (OsylPublicPortalBackingBean) context
			.getBean("osylPublicPortalMainBean");
%>
<html>
	<head>

	<script type="text/javascript">
		function redirectToOsyl(id){
			var s = '<%=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()%>/osyl-editor-sakai-tool/index.jsp?siteId='+id;
			window.location.href = s;
		}
	</script>
	</head>

	<body onload="myLoad()">
		<select name="test" id="selSite">
		<%
			for(String s:osylMainBean.getOsylSiteService().getPublishCOSiteIds()){
		%>
				<option value="<%=s%>"><%=s%></option>			
		<%
			}
		%>
		</select>
		<input type="button" value="ok" onclick="redirectToOsyl(document.getElementById('selSite').options[document.getElementById('selSite').selectedIndex].value)"/>
	</body>
</html>
