<?xml version="1.0"  encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="text" encoding="ISO-8859-1" omit-xml-declaration="yes"/>
<!--  =========================================================================== -->
<xsl:include href="libelles_fr.xsl"/>
<xsl:include href="commun.xsl"/>
<xsl:include href="ressources.xsl"/>

<xsl:template match="/">

	&lt;html>

		&lt;head&gt;
			&lt;meta http-equiv="Content-Type" content="text/html; charset=Windows-1252" /&gt;
			&lt;link name='css' rel="stylesheet" TYPE='text/css' href='css/cmsie.css' title='default'/&gt;
			&lt;script language='JavaScript1.2' src='js/commun.js'&gt;&lt;/script&gt;

			&lt;title&gt;
				<xsl:call-template name="codeTiret"/>
				<xsl:text> - </xsl:text>
				<xsl:value-of select="/planCours/cours/libelle"/>
			&lt;/title&gt;
		&lt;/head&gt;

	&lt;body>
	
		&lt;div id='mainPageNew'>
		<!-- ========================================= -->
			&lt;div&gt;
				&lt;table width="200" border="0" cellspacing="0" cellpadding="0"&gt;
					&lt;tr&gt;
					  &lt;td&gt;&lt;a href="http://www.hec.ca"&gt;&lt;img src="img/top-menu_1-01.gif" width="101" height="56" border="0"&gt;&lt;/a&gt;&lt;a href="http://www.hec.ca"&gt;&lt;img src="img/top-menu_1-02.gif" width="78" height="56" border="0"&gt;&lt;/a&gt;&lt;a href="http://zonecours.hec.ca"&gt;&lt;img src="img/top-menu_1-03.gif" width="128" height="56" border="0"&gt;&lt;/a&gt;&lt;img src="img/top-menu_1-04.gif" width="66" height="56"&gt;&lt;img src="img/top-menu_1-05.gif" width="105" height="56"&gt;&lt;img src="img/top-menu_1-06.gif" width="26" height="56"&gt;&lt;img src="img/top-menu_1-07.gif" width="21" height="56"&gt;&lt;/td&gt;
					&lt;/tr&gt;
					&lt;tr&gt;
					  &lt;td&gt;&lt;a href="monAccueil.jsp?lang=<xsl:value-of select="planCours/@lang"/>"&gt;&lt;img src="img/top-menu_2-01.gif" width="101" height="25" border="0"&gt;&lt;/a&gt;&lt;a href="login.jsp?lang=<xsl:value-of select="planCours/@lang"/>&amp;reload=af1Presentation&amp;instId=<xsl:value-of select="planCours/@koId"/>"&gt;&lt;img src="img/top-menu_2-02.gif" width="78" height="25" border="0"&gt;&lt;/a&gt;&lt;img src="img/top-menu_2-03.gif" width="128" height="25"&gt;&lt;img src="img/top-menu_2-04.gif" width="66" height="25"&gt;&lt;a href="recherche.jsp?lang=<xsl:value-of select="planCours/@lang"/>"&gt;&lt;img src="img/top-menu_2-05.gif" width="105" height="25" border="0"&gt;&lt;/a&gt;&lt;a href="javascript:zoomPlus()"&gt;&lt;img src="img/top-menu_2-06.gif" width="26" height="25" border="0"&gt;&lt;/a&gt;&lt;a href="javascript:zoomMoins()"&gt;&lt;img src="img/top-menu_2-07.gif" width="21" height="25" border="0"&gt;&lt;/a&gt;&lt;/td&gt;
					&lt;/tr&gt;
				&lt;/table&gt;
			&lt;/div&gt;
			
			&lt;userActif/>

			&lt;div class='titrePageNew'&gt;
				<xsl:call-template name="codeTiret"/><xsl:text> - </xsl:text>
				<xsl:value-of select="/planCours/cours/libelle"/>
			&lt;/div&gt;
			
			&lt;table width="94%" border="0" cellpadding="0" cellspacing="0" class="tableauStructure"&gt;
				&lt;tr&gt;
					&lt;td width="70%" valign="top"&gt;
					
						<xsl:call-template name="rubriquesAnnuaire">
							<xsl:with-param name="chemin" select="/planCours/presentation/ressources"/>
						</xsl:call-template>

						<xsl:call-template name="session" />

						<xsl:call-template name="archive" />
			
					&lt;/td&gt;
					&lt;td width="30%" valign="top"&gt;
			
						&lt;div id='encadreNewsAnnuaire'&gt;
						
							&lt;div class='encadreNews-titre'&gt;<xsl:value-of select="$responsable"/>&lt;/div&gt;
							&lt;div class='encadreNews-texte2'&gt;
							&lt;a href='<xsl:call-template name="service"><xsl:with-param name="type" select="'url'"/></xsl:call-template>'&gt;
								<xsl:call-template name="service"><xsl:with-param name="type" select="'service'"/></xsl:call-template>
							&lt;/a&gt;
							&lt;/div&gt;

							&lt;div class='encadreNews-titre'&gt;<xsl:value-of select="$programme"/>&lt;/div&gt;
							&lt;div class='encadreNews-texte2'&gt;
							&lt;a href='<xsl:call-template name="programme"><xsl:with-param name="type" select="'url'"/></xsl:call-template>'&gt;
								<xsl:call-template name="programme"><xsl:with-param name="type" select="'programme'"/></xsl:call-template>
							&lt;/a&gt;
							&lt;/div&gt;

							&lt;div class='encadreNews-titre'&gt;<xsl:value-of select="$credit"/>&lt;/div&gt;
							&lt;div class='encadreNews-texte2'&gt;
								<xsl:value-of select="planCours/@credit"/>
							&lt;/div&gt;

							&lt;div class='encadreNews-titre'&gt;<xsl:value-of select="$exigence"/>&lt;/div&gt;
							&lt;div class='encadreNews-texte2'&gt;
								<xsl:value-of select="planCours/prealables"/>
							&lt;/div&gt;
							
							&lt;div class='encadreNews-titre'&gt;<xsl:value-of select="$horaire"/>&lt;/div&gt;
							&lt;div class='encadreNews-texte2'&gt;
								&lt;a href='http://enligne.hec.ca/GAPET/guest.html' TARGET='_blank'&gt;<xsl:value-of select="$horaireLibelle"/>&lt;br&gt;(&lt;i&gt;<xsl:value-of select="$horaireLibelle2"/>&lt;/i&gt;)&lt;/a&gt;
							&lt;/div&gt;

						&lt;/div&gt;
			
					&lt;/td&gt;
				&lt;/tr&gt;
			&lt;/table&gt;

			<!-- ============================== -->
			<xsl:call-template name="footerPage2" />
			<!-- ============================== -->
			
			&lt;/div> <!-- mainPageSeance -->
		&lt;/body>
	&lt;/html>

</xsl:template>

<!-- ========================================= -->
<xsl:template name="evaluations">
	&lt;div class='rubrique'>
		&lt;div class="logoRubrique">&lt;/div><!-- logoRubrique -->
		&lt;div class='titreRubrique'>
			&lt;a name="#jumpEvaluation"><xsl:value-of select="$evaluationLbl"/>&lt;/a>
		&lt;/div>&lt;!-- titreRubrique -->
		&lt;div class='contenuRubrique'>
			<xsl:for-each select='//evaluation'>
				&lt;div class='ressource'>
					&lt;div class="logoRessource">&lt;/div><!-- logoRessource -->
					&lt;div class='titreRessource'>
						<xsl:value-of select="global/libelle"/><xsl:text> (</xsl:text><xsl:value-of select="global/valeur"/><xsl:text>%)</xsl:text>
					&lt;/div><!-- titreRessource -->
				&lt;/div><!-- div ressource -->
			</xsl:for-each>
		&lt;/div>&lt;!-- contenuRubrique -->
	&lt;/div>&lt;!-- div rubrique -->

</xsl:template>

<!-- ========================================= -->

<xsl:template name="programme">
	<xsl:param name="type">plagiat</xsl:param>
	<xsl:if test="/planCours[@programme='APRE']">
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmeAPRE"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlAPRE"/>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours[@programme='BAA']">
		<xsl:if test ="$type='plagiat'">
			<xsl:value-of select="$plagiatBAA"/>
		</xsl:if>
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmeBAA"/>
		</xsl:if>
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceBAA"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlBAA"/>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours[@programme='MSC']">
		<xsl:if test ="$type='plagiat'">
			<xsl:value-of select="$plagiatMSC"/>
		</xsl:if>
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmeMSC"/>
		</xsl:if>
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceMSC"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlMSC"/>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours[@programme='MBA']">
		<xsl:if test ="$type='plagiat'">
			<xsl:value-of select="$plagiatMBA"/>
		</xsl:if>
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmeMBA"/>
		</xsl:if>
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceMBA"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlMBA"/>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours[@programme='DOCTORAT']">
		<xsl:if test ="$type='plagiat'">
			<xsl:value-of select="$plagiatPHD"/>
		</xsl:if>
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmePHD"/>
		</xsl:if>
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$servicePHD"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlPHD"/>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours[@programme='CERT']">
		<xsl:if test ="$type='plagiat'">
			<xsl:value-of select="$plagiatCERT"/>
		</xsl:if>
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmeCERT"/>
		</xsl:if>
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceCERT"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlCERT"/>
		</xsl:if>
	</xsl:if>

	<xsl:if test="/planCours[@programme='DES']">
		<xsl:if test ="$type='plagiat'">
			<xsl:value-of select="$plagiatDES"/>
		</xsl:if>
		<xsl:if test ="$type='programme'">
			<xsl:value-of select="$programmeDES"/>
		</xsl:if>
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceDES"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$programmeUrlDES"/>
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template name="service">
	<xsl:param name="type">service</xsl:param>
	<xsl:if test="/planCours[@service='TI']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceTI"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlTI"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='IEA']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceIEA"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlIEA"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='GOP']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceGOP"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlGOP"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='GOL']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceGOL"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlGOL"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='FINANCE']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceFINANCE"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlFINANCE"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='GRH']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceGRH"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlGRH"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='MARKETING']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceMARKETING"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlMARKETING"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='MNGT']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceMNGT"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlMNGT"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='MQG']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceMQG"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlMQG"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='SC.COMPT.']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceSC.COMPT."/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlSC.COMPT."/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='QUAL.COMM.']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceQUAL.COMM."/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlQUAL.COMM."/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='CETAI']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceCETAI"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlCETAI"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='BUR.REGIST']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceBUR.REGIST"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlBUR.REGIST"/>
		</xsl:if>
	</xsl:if>
	<xsl:if test="/planCours[@service='INTERNAT']">
		<xsl:if test ="$type='service'">
			<xsl:value-of select="$serviceINTERNAT"/>
		</xsl:if>
		<xsl:if test ="$type='url'">
			<xsl:value-of select="$serviceUrlINTERNAT"/>
		</xsl:if>
	</xsl:if>
</xsl:template>

<xsl:template name="session">
	&lt;div class='bulletCarreVert'&gt;
	&lt;div class='ssTitrePageBullet'&gt;
		<xsl:value-of select="$sessionCourante"/>
	&lt;/div&gt;
	&lt;/div&gt;
	&lt;sessionCourante/&gt;
</xsl:template>

<xsl:template name="archive">
	&lt;br&gt;										
	&lt;div class='alignementTitre'&gt;
		&lt;a href='accueil.jsp?t=cours&amp;v=<xsl:value-of select="/planCours/cours/no"/>&amp;lang=<xsl:value-of select="planCours/@lang"/>' class='titreUrl'&gt;<xsl:value-of select="$sessionArchive"/>&lt;/a&gt;
	&lt;/div&gt;
</xsl:template>


<!-- ========================================================== -->
<!-- ========================================================== -->
</xsl:stylesheet>