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
        javax.naming.*
        "%>

<%@page import="publishing.Publication" %>


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

	Class.forName("oracle.jdbc.driver.OracleDriver");

	DataSource dsPublication = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/Publication");
	if (dsPublication == null) throw new ServletException("data source :Publication non trouvé");

	connexionPublication = dsPublication.getConnection();

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
