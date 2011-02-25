<%@page contentType="text/html; charset=iso-8859-1"
        buffer="64kb"
        import="java.sql.*,
        javax.sql.*,
        java.util.*,
        java.io.*,
        java.text.*,
        java.lang.*,
        oracle.sql.*,
        oracle.jdbc.driver.*,
        oracle.sql.CLOB.*,
        org.w3c.dom.*,
        javax.xml.transform.*,
        javax.xml.transform.dom.*,
        javax.xml.transform.stream.*,
        javax.xml.parsers.*,
        org.xml.sax.*,
        org.sakaiquebec.opensyllabus.common.impl.portal.javazonecours.*,
        javax.naming.*,
        org.sakaiproject.component.cover.ServerConfigurationService
        "%>



<%

StringBuffer outTrace = new StringBuffer();
StringBuffer outPrint = new StringBuffer();

//------------ TRACE -----------
boolean trace = false;
String tr = request.getParameter("trace");

if (tr != null && tr.equals("1"))
	trace=true;
else
	trace=false;
//-------------------------------

String file = request.getParameter("file");
String lang = request.getParameter("lang");

Publication p = new Publication();

Connection connexionPublication = null;		// hors du try pour fermer dans finally

try{
    String driverName =
	ServerConfigurationService
		.getString("hec.zonecours.conn.portail.driver.name");
	String url =
	ServerConfigurationService
		.getString("hec.zonecours.conn.portail.url");
	String user =
	ServerConfigurationService
		.getString("hec.zonecours.conn.portail.user");
	String password =
	ServerConfigurationService
		.getString("hec.zonecours.conn.portail.password");
    
	Class.forName(driverName);

	connexionPublication = DriverManager.getConnection(url, user, password);

	p.depublier(connexionPublication, file, lang, outPrint, outTrace, trace);
}

catch(Exception e){
	outPrint.append("Erreur : " +e);
}

finally {
	if (connexionPublication != null)
		connexionPublication.close();

	out.print(outPrint.toString());
	if (trace){
		out.print("<br><br><div id='trace'>");
		out.print(outTrace.toString());
		out.print("</div>");
	}
}
%>
