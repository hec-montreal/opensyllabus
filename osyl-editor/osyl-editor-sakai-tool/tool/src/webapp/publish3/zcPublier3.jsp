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
        javax.naming.*,
        org.sakaiquebec.opensyllabus.common.impl.portal.javazonecours.*,
        org.sakaiproject.component.cover.ServerConfigurationService
        "%>




<%
int v = 0;
if (request.getParameter("c")!= null)
	v = Integer.parseInt(request.getParameter("c"));
//-------------------------------
if (v == 0) {
	String url = "zcPublier3.jsp?c=2&file="+request.getParameter("file")+"&lang="+request.getParameter("lang");
	//------------ TRACE -----------
	String tr = request.getParameter("trace");

	if (tr != null && tr.equals("1"))
		url+= "&trace=1";
	//-------------------------------
	//------------ FORCE -----------
	String frc = request.getParameter("force");

	if (frc != null && frc.equals("1"))
		url+= "&force=1";
	//-------------------------------

	%>
	<!-- ======================================================================= -->
	<!-- ============================= HEC MONTREAL ============================ -->
	<!-- ======================================================================= -->
	<html>
	<head>
	<title>Publication</title>
	</head>
	<frameset rows = "250,*">
		<frame frameborder='0' src='zcPublier3.jsp?c=1' name='attente'/>
		<frame frameborder='0' src='<%=url%>' name='publier'/>
	</frameset>
	</html>
	<%
}
// ===================================================================
if (v == 1) {
	%>
	<html>
		<head>
		<title>
			Publication
		</title>

				<link name='css' rel="stylesheet" TYPE='text/css' href='css/attente.css' title='default'/>

		</head>
		<body>
		<div id='mainPage'>
		<center>
			<div id='headerPage'>
				<table border="0" cellspacing="0" cellpadding="0">
					<tr valign="top">
						<td>
							<a href="http://www.hec.ca">
								<img src="img/Logo1.gif" border="0" alt="HEC Montréal">
							</a>
						</td>
						<td>
							<img src="img/Logo2.gif" width="343" height="55" border="0" alt="Site de cours">
						</td>
						<td valign="top">
						</td>
					</tr>
				</table>
			</div>
			<img src='img/grandfather_clock.gif'/>
			<div id='Clock'> </DIV>

				<script language="JavaScript">
					function tick() {
					  var hours, minutes, seconds, ap;
					  var intHours, intMinutes, intSeconds;
					  var today;

					  today = new Date();

					  intHours = today.getHours();
					  intMinutes = today.getMinutes();
					  intSeconds = today.getSeconds();

					  if (intHours == 0) {
						 hours = "12:";
						 ap = "Midnight";
					  } else if (intHours < 12) {
						 hours = intHours+":";
						 ap = "A.M.";
					  } else if (intHours == 12) {
						 hours = "12:";
						 ap = "Noon";
					  } else {
						 intHours = intHours - 12
						 hours = intHours + ":";
						 ap = "P.M.";
					  }

					  if (intMinutes < 10) {
						 minutes = "0"+intMinutes+":";
					  } else {
						 minutes = intMinutes+":";
					  }

					  if (intSeconds < 10) {
						 seconds = "0"+intSeconds+" ";
					  } else {
						 seconds = intSeconds+" ";
					  }

					  timeString = hours+minutes+seconds+ap;

					  Clock.innerHTML = timeString;

					  window.setTimeout("tick();", 100);
					}

					window.onload = tick;

				</script>
			</div>

			<div class='attente'>
				Patience, la publication peut prendre plusieurs minutes ...
			</div>
		</center>
		</div>
		</body>
	</html>
	<%
}
// ===================================================================
if (v == 2) {
	String koId = request.getParameter("file");
	String langue = request.getParameter("lang");

	//------------ FORCE -----------
	boolean force = false;
	String frc = request.getParameter("force");
	if (frc != null && frc.equals("1"))
		force = true;
	//------------ TRACE -----------
	boolean trace = false;
	String tr = request.getParameter("trace");
	if (tr != null && tr.equals("1"))
		trace = true;
	//-------------------------------
	//-------------------------------
	boolean ok = true;
	StringBuffer outPrint = new StringBuffer();
	StringBuffer outTrace = new StringBuffer();

	Publication p = new Publication();

	p.appDirName = "C:\\TransfoPortail\\";  //serveur olivier

	// ================ chargement des xslt =============

	int n = 999;	// aucune recompilation - 9999 tout est recompilé
					// spécifier le numéro pour recharger un xsl
	if (request.getParameter("n")!= null)
		n = Integer.parseInt(request.getParameter("n"));

	//Local
	String xsltDirName = getServletContext().getRealPath("/")
		+ File.separator + "publish3" + File.separator + "xslt"
		+ File.separator;

	//String xsltDirName = getServletContext().getRealPath(".") + File.separator + "xslt" + File.separator;
	String[] xsltNamesTable = {
		  "securite0.xsl"		// 0
		, "securite1.xsl"		// 1
		, "securite2.xsl"		// 2
		, "testSecurite1.xsl"	// 3
		, "testSecurite2.xsl"	// 4
		, "parseDocInt.xsl"		// 5
		, "parseDocExt.xsl"		// 6
		, "fr-10.xsl"			// 7
		, "fr-11.xsl"			// 8  Présentation
		, "fr-11a.xsl"			// 9  Présentation Annuaire
		, "fr-12.xsl"			// 10 Coordonnées
		, "fr-13.xsl"			// 11
		, "fr-14.xsl"			// 12
		, "fr-15.xsl"			// 13
		, "fr-16.xsl"			// 14
		, "fr-17.xsl"			// 15
		, "fr-18.xsl"			// 16
		, "fr-19.xsl"			// 17
		, "fr-20.xsl"			// 18
		, "fr-21.xsl"			// 19
		, "fr-22.xsl"	
		, "en-10.xsl"			// 20
		, "en-11.xsl"			// 21
		, "en-11a.xsl"			// 22
		, "en-12.xsl"			// 23
		, "en-13.xsl"			// 24
		, "en-14.xsl"			// 25
		, "en-15.xsl"			// 26
		, "en-16.xsl"			// 27
		, "en-17.xsl"			// 28
		, "en-18.xsl"			// 29
		, "en-19.xsl"			// 30
		, "en-20.xsl"			// 31
		, "en-21.xsl"			// 32
		, "en-22.xsl"	
		, "es-10.xsl"			// 33
		, "es-11.xsl"			// 34
		, "es-11a.xsl"			// 35
		, "es-12.xsl"			// 36
		, "es-13.xsl"			// 37
		, "es-14.xsl"			// 38
		, "es-15.xsl"			// 39
		, "es-16.xsl"			// 40
		, "es-17.xsl"			// 41
		, "es-18.xsl"			// 42
		, "es-19.xsl"			// 43
		, "es-20.xsl"			// 44
		, "es-21.xsl"			// 45
		, "es-22.xsl"	
	};
	p.transformersTable = new Hashtable();

	for (int i=0;i<xsltNamesTable.length;i++){
		Document xslt = null;
		String xsltName = xsltNamesTable[i];
		if (application.getAttribute(xsltName) == null || i == n || n == 9999) {
			String xsltFileName = xsltName;
			if (xsltName.indexOf("-")>0) {
				String lang = xsltName.substring(0,2);
				xsltFileName = xsltName.substring(3,xsltName.length());
				xslt = p.loadDOM(xsltDirName+xsltFileName);
				p.majLang(xslt,lang);
			}
			else
				xslt = p.loadDOM(xsltDirName+xsltFileName);
			application.setAttribute(xsltName,p.createTransformer(xslt, xsltDirName));

			//out.print("création transformer "+xsltName+"<br>");
			//out.print("Recompilation des fichiers"+"<br>");

		}
		p.transformersTable.put(xsltName,application.getAttribute(xsltName));
	}
	//=============================================

	java.util.Date dateDeb;
	java.util.Date dateFin;
	dateDeb = new java.util.Date(System.currentTimeMillis());
	if (trace) outTrace.append("<br>début: " + dateDeb.toString());

	if (trace) outTrace.append("<br>appDirName : "+p.appDirName);

	outPrint.append("<div class='titre'>Rapport de publication</div>");

	Connection connexionPublication = null;		// hors du try pour fermer dans finally
	Connection connexionPeopleSoft = null;			// hors du try pour fermer dans finally

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

		try {
		    Class.forName(driverName);
		    connexionPublication = DriverManager.getConnection(url, user, password);
		    connexionPublication.setAutoCommit(false);
		    
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}
		
		
		driverName =
			ServerConfigurationService
				.getString("hec.peoplesoft.conn.portail.driver.name");
		url =
			ServerConfigurationService
				.getString("hec.peoplesoft.conn.portail.url");
		user =
			ServerConfigurationService
				.getString("hec.peoplesoft.conn.portail.user");
		password =
			ServerConfigurationService
				.getString("hec.peoplesoft.conn.portail.password");

		try {
		    Class.forName(driverName);
		    connexionPeopleSoft = DriverManager.getConnection(url, user, password);

		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

			// ==================================================================
		if (trace) outTrace.append("<br>connexions établies :" + new java.util.Date(System.currentTimeMillis()));

		ok = p.chargerTraiter(connexionPublication,connexionPeopleSoft,koId,langue, force, outPrint, outTrace, trace);

		dateFin = new java.util.Date(System.currentTimeMillis());
		outPrint.append("<div class='duree'>Durée: " + (((double)dateFin.getTime()-(double)dateDeb.getTime())/1000)+" secondes</div>");

	}

	catch(Exception e){
		outPrint.append("Erreur : " +e);
	}

	finally {
		if (connexionPublication != null) connexionPublication.close();
		if (connexionPeopleSoft != null) connexionPeopleSoft.close();
		%>
		<!-- ======================================================================= -->
		<!-- ============================= HEC MONTREAL ============================ -->
		<!-- ======================================================================= -->
		<html>
		<head>
		<title>Publication</title>
			<script>
				function fin(){
					if (window.parent.frames.attente != null)
						window.parent.frames["attente"].location='zcpublier3.jsp?c=3'
					};
			</script>
			<link name='css' rel="stylesheet" TYPE='text/css' href='css/attente.css' title='default'/>
		</head>
		<body onLoad="fin()">
		<div id='mainPage'>
		<div id='resultat'>
		<%
			if (!ok)
				out.print("<br><font color='red'><i>Une erreur s'est produite. Voir ci-dessous</i></font><br><br>");

			out.print(outPrint.toString());


			if (trace){
				out.print("<br><br><div id='trace'>");
				out.print(outTrace.toString());
				out.print("</div>");
			}
		%>
		</div><!-- resultat -->
		</div><!-- mainPage -->
		</body>
		</html>
		<%
	}
} // fin v==2
// ===================================================================
if (v == 3) {
	%>
	<html>
	<head>
	<title>
		Publication
	</title>

			<link name='css' rel="stylesheet" TYPE='text/css' href='css/attente.css' title='default'/>

	</head>
	<body>
	<div id='mainPage'>
	<center>

		<div id='headerPage'>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr valign="top">
								<td>
									<a href="http://www.hec.ca">
										<img src="img/Logo1.gif" border="0" alt="HEC Montr&amp;eacute;al">
									</a>
								</td>
								<td>
									<img src="img/Logo2.gif" width="343" height="55" border="0" alt="Site de cours">
								</td>
								<td valign="top">
								</td>
							</tr>
						</table>
		</div>
		<div id='imagePendule'>
			<img src='img/grandfather_clock.gif'/>
		</div>

		<div class='attente'>
			Terminé!
		</div>
	</center>
	</div>
	</body>
	</html>
	<%
}
	%>

