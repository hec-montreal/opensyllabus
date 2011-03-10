<?xml version="1.0"  encoding="ISO-8859-1" standalone="yes"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- ========================= -->
<!-- ======= Pages =========== -->
<!-- ========================= -->

<xsl:variable name='a11Lbl'>Presentaci�n</xsl:variable>
<xsl:variable name='a12Lbl'>Datos personales</xsl:variable>
<xsl:variable name='a13Lbl'>Material pedag�gico</xsl:variable>
<xsl:variable name='a14Lbl'>Trabajos y ex�menes</xsl:variable>
<xsl:variable name='a14Lbla'>Evaluaci�n</xsl:variable>
<xsl:variable name='a15Lbl'>Lista de periodos de clase</xsl:variable>
<xsl:variable name='a15LblTP'>Lista de trabajos pr�cticos</xsl:variable>
<xsl:variable name='a15LblTheme'>Lista de temas</xsl:variable>
<xsl:variable name='a16Lbl'>Documentos para imprimir</xsl:variable>
<xsl:variable name='a18Lbl'>Lista de ejercicios</xsl:variable>
<xsl:variable name='a19Lbl'>Lista de referencias bibliogr�ficas</xsl:variable>
<xsl:variable name='a21Lbl'>Carpeta de avisos</xsl:variable>
<xsl:variable name='a22Lbl'>FAQ</xsl:variable>
<xsl:variable name='a22Lbla'>Foire aux questions</xsl:variable>

<!-- ========================= -->
<!-- ======= Menus =========== -->
<!-- ========================= -->

<xsl:variable name='a11menuLbl'>Presentaci�n</xsl:variable>
<xsl:variable name='a12menuLbl'>Datos personales</xsl:variable>
<xsl:variable name='a13menuLbl'>Material pedag�gico</xsl:variable>
<xsl:variable name='a14menuLbl'>Trabajos y ex�menes</xsl:variable>
<xsl:variable name='a14menuLbla'>Evaluaci�n</xsl:variable>
<xsl:variable name='a15menuLbl'>Periodos de clase</xsl:variable>
<xsl:variable name='a22menuLbl'>FAQ</xsl:variable>
<xsl:variable name='a16menuLbl'>Documentos para imprimir</xsl:variable>
<xsl:variable name='DiversCoursmenuLbl'>Avisos - Curso</xsl:variable>


<!-- Mise a jour: 15-avril-2008 apr Van: Message d'usage des calculatrices
	sont toujours affiches tout de suite apres le message Plagiat a gauche -->
<xsl:variable name='usageCalculatriceLabel'>
	&lt;b&gt; Calculadora &lt;/b&gt;
	&lt;br&gt; los estudiantes son rogados que se informado de la  &lt;a href="http://www.hec.ca/plagiat/examens/calculatrice.html" target="_blank"&gt; pol�tica de utilizaci�n de calculadora  &lt;/a&gt;en el momento de ex�menes cuando �stas son autorizadas.
	&lt;br&gt;
</xsl:variable> 

<!-- Mise a jour: 15-avril-2008 par Van: Message plagiat et politique d'usage des calculatrices
	sont toujours affiches dans le menu a droite -->
<xsl:variable name='plagiatCalculatriceLabel_15Avril2008'>
		&lt;br&gt;
		&lt;div class="bulletCarreVert" &gt;
			&lt;b&gt; &amp;nbsp;&amp;nbsp;&amp;nbsp; Plagio &lt;/b&gt; : Los estudiantes deben leer el &lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12  &lt;/a&gt; del R�glement r�gissant l'activit� �tudiante � HEC Montr�al que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n. 
		&lt;/div &gt;
		&lt;br&gt;
		&lt;div class="bulletCarreVert" &gt;
			&lt;b&gt; &amp;nbsp;&amp;nbsp;&amp;nbsp; Calculadora &lt;/b&gt;: Se ruega a los estudiantes tener conocimiento de la  &lt;a href="http://www.hec.ca/plagiat/examens/calculatrice.html" target="_blank"&gt; pol�tica de utilizaci�n de calculadoras  &lt;/a&gt;en ex�menes cuando se autorizan las.
		&lt;/div &gt;
		&lt;br&gt;
</xsl:variable>


<!-- Mise a jour: 9-juillet-2008 par Van: Message plagiat et politique d'usage des calculatrices
	sont toujours affiches dans le menu a droite, sous forme hyperlien -->
<xsl:variable name='plagiatCalculatriceLabel'>
		&lt;br&gt;
		Reglamentos
		&lt;br&gt;
			&amp;nbsp;&amp;nbsp;
			-&amp;nbsp; &lt;a href="http://www.hec.ca/en/plagiarism/reglement.html" target="_blank" &gt; Plagio &lt;/a&gt;  

		&lt;br&gt;
			&amp;nbsp;&amp;nbsp;
			-&amp;nbsp; &lt;a href="http://www.hec.ca/en/plagiarism/exams/calculatrice.html" target="_blank" &gt; Calculadoras &lt;/a&gt;  
		&lt;br&gt;
		
		<xsl:call-template name="separateur"/>
</xsl:variable>






<xsl:variable name='DiversSeancemenuLbl'>Avisos - Periodo de clase</xsl:variable>
<xsl:variable name='archiveMenuLbl'>Carpeta de avisos</xsl:variable>

<!-- ========================= -->
<!-- = documents imprimables = -->
<!-- ========================= -->

<xsl:variable name='planCoursImprimable'>Plan de curso para imprimir</xsl:variable>
<xsl:variable name='planCoursImprimableSsTitre'>Datos personales | Presentaci�n | Evaluaci�n | Material pedag�gico | Periodos de clase</xsl:variable>

<xsl:variable name='exercicesImprimable'>Lista de ejercicios</xsl:variable>
<xsl:variable name='exercicesImprimableSsTitre'>Enunciados | Soluciones</xsl:variable>

<xsl:variable name='referencesImprimable'>Lista de referencias</xsl:variable>
<xsl:variable name='referencesImprimableSsTitre'>Referencias bibliogr�ficas | Lecturas</xsl:variable>


<!-- ========================= -->
<!-- ======= Libell�s ======== -->
<!-- ========================= -->

<xsl:variable name='seanceLbl'>Periodo de clase </xsl:variable>
<xsl:variable name='tpLbl'>Trabajo pr�ctico</xsl:variable>
<xsl:variable name='themeLbl'>Tema</xsl:variable>
<xsl:variable name='themeTitreLbl'>T�tulo</xsl:variable>
<xsl:variable name='seancesLbl'>Periodos de clase</xsl:variable>
<xsl:variable name='tpsLbl'>Trabajos pr�cticos</xsl:variable>
<xsl:variable name='enoncesLbl'>Enunciados</xsl:variable>
<xsl:variable name='solutionsLbl'>Soluciones</xsl:variable>

<!-- ========================= -->
<!-- ======= Rubriques ======= -->
<!-- ========================= -->

<xsl:variable name='biblioTag'>rubriqueBibliographie</xsl:variable>
<xsl:variable name='biblioLbl'>Referencias bibliogr�ficas</xsl:variable>

<xsl:variable name='descriptionTag'>rubriqueDescription</xsl:variable>
<xsl:variable name='descriptionLbl'>Descripci�n</xsl:variable>

<xsl:variable name='objectifsTag'>rubriqueObjectifs</xsl:variable>
<xsl:variable name='objectifsLbl'>Objetivos</xsl:variable>

<xsl:variable name='evaluationTag'>evaluations</xsl:variable>
<xsl:variable name='evaluationLbl'>Evaluaci�n</xsl:variable>

<xsl:variable name='approcheTag'>rubriqueApprochePedagogique</xsl:variable>
<xsl:variable name='approcheLbl'>Enfoque pedag�gico</xsl:variable>

<xsl:variable name='supportTag'>rubriqueSupportsCours</xsl:variable>
<xsl:variable name='supportLbl'>Recursos utilizados durante el periodo de clase</xsl:variable>

<xsl:variable name='lecturesTag'>rubriqueLectures</xsl:variable>
<xsl:variable name='lecturesLbl'>Lecturas</xsl:variable>

<xsl:variable name='exercicesTag'>rubriqueExercice</xsl:variable>
<xsl:variable name='exercicesLbl'>Ejercicios</xsl:variable>

<xsl:variable name='ressourcesTag'>rubriqueRessourcesGenerales</xsl:variable>
<xsl:variable name='ressourcesLbl'>Diferentes recursos</xsl:variable>

<xsl:variable name='ressourcesComplementairesTag'>rubriqueRessourcesComplementaires</xsl:variable>
<xsl:variable name='ressourcesComplementairesLbl'>Recursos complementarios</xsl:variable>

<xsl:variable name='devoirsTag'>rubriqueDevoirs</xsl:variable>
<xsl:variable name='devoirsLbl'>Tareas</xsl:variable>

<xsl:variable name='outilsTag'>rubriqueOutils</xsl:variable>
<xsl:variable name='outilsLbl'>Herramientas</xsl:variable>

<xsl:variable name='anciensexamsTag'>rubriqueAnciensExamens</xsl:variable>
<xsl:variable name='anciensexamsLbl'>Ex�menes anteriores</xsl:variable>

<xsl:variable name='casTag'>rubriqueCas</xsl:variable>
<xsl:variable name='casLbl'>Estudios de caso</xsl:variable>

<xsl:variable name='notesFormTag'>rubriqueNotesFormateur</xsl:variable>
<xsl:variable name='notesFormLbl'>Avisos para el equipo docente</xsl:variable>

<xsl:variable name='diversTag'>rubriqueDivers</xsl:variable>
<xsl:variable name='diversLbl'>Varios</xsl:variable>

<xsl:variable name='nouvelleTag'>rubriqueNouvelle</xsl:variable>
<xsl:variable name='diversCoursLbl'>Avisos para el curso</xsl:variable>

<xsl:variable name='diversSeanceLbl'>Avisos para el periodo de clase</xsl:variable>

<xsl:variable name='actResBeforeTag'>rubriqueActivitesRessourcesAvantSeance</xsl:variable>
<xsl:variable name='actResBeforeLbl'>Actividades y/o Recursos antes de la sesi�n</xsl:variable>

<xsl:variable name='actResDuringTag'>rubriqueActivitesRessourcesPendantSeance</xsl:variable>
<xsl:variable name='actResDuringLbl'>Actividades y/o Recursos durante de la sesi�n</xsl:variable>

<xsl:variable name='actResAfterTag'>rubriqueActivitesRessourcesApresSeance</xsl:variable>
<xsl:variable name='actResAfterLbl'>Actividades y/o Recursos despu�s de la sesi�n</xsl:variable>

<!-- ========================= -->
<!-- ======= Attributs ======= -->
<!-- ========================= -->

<xsl:variable name='bureauLbl'> Despacho: </xsl:variable>
<xsl:variable name='courrielLbl'> Direcci�n electr�nica: </xsl:variable>
<xsl:variable name='telLbl'> Tel�fono: </xsl:variable>
<xsl:variable name='dispoLbl'> Disponibilidad: </xsl:variable>
<xsl:variable name='evalDateLbl'> Fecha: </xsl:variable>
<xsl:variable name='isbnLbl'> ISBN: </xsl:variable>

<!-- ========================= -->
<!-- ======= Autres ========== -->
<!-- ========================= -->

<xsl:variable name='sujetLbl'>Tema</xsl:variable>
<xsl:variable name='solutionLbl'>Soluci�n</xsl:variable>
<xsl:variable name='dateMajLbl'>�ltima actualizaci�n: </xsl:variable>
<xsl:variable name='plagiatLbl'>Plagio</xsl:variable>
<xsl:variable name='calculatriceLbl'>Calculadora</xsl:variable>

<xsl:variable name='plagiatBAA'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b> &lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>
<xsl:variable name='plagiatPHD'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b>&lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>
<xsl:variable name='plagiatMSC'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b>&lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>
<xsl:variable name='plagiatMBA'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b>&lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>
<xsl:variable name='plagiatCERT'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b>&lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>
<xsl:variable name='plagiatDES'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b>&lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>
<xsl:variable name='plagiatAPRE'>Los estudiantes deben leer el &lt;b>&lt;a href="http://www.hec.ca/plagiat/reglement.html" target="_blank"&gt; articulo 12 &lt;/a>&lt;/b> del &lt;b>&lt;i>R�glement r�gissant l'activit� �tudiante � HEC Montr�al &lt;/i> &lt;/b>que tiene por t�tulo Plagio y Fraude, tambi�n deben informarse sobre los actos y las actividades que son considerados como plagio u otro tipo de infracci�n de tipo pedag�gico (12.1), de procedimiento (12.2) y de las sanciones, que pueden llegar hasta la suspensi�n e inclusive la expulsi�n de la Escuela (12.3). Toda infracci�n ser� analizada seg�n los hechos y las circunstancias y en consecuencia se aplicar� una sanci�n.</xsl:variable>

<!-- ========================= -->
<!-- ======= Annuaire ======== -->
<!-- ========================= -->

<xsl:variable name='responsable'>Responsable: </xsl:variable>
<xsl:variable name='programme'>Programa de estudios: </xsl:variable>
<xsl:variable name='credit'>Cr�ditos: </xsl:variable>
<xsl:variable name='exigence'>Exigencias: </xsl:variable>
<xsl:variable name='details'>Si necesita m�s informaci�n, pulse aqu�</xsl:variable>
<xsl:variable name='sessionCourante'>Plan de curso del trimestre actual</xsl:variable>
<xsl:variable name='sessionArchive'>Para la carpeta de los planes de curso, pulse aqu�</xsl:variable>
<xsl:variable name='horaire'>Horario: </xsl:variable>
<xsl:variable name='horaireLibelle'>Herramienta de b�squeda de horario</xsl:variable>
<xsl:variable name='horaireLibelle2'>V�a HEC en l�nea</xsl:variable>

<!-- ===== Libell�s =========== -->
<xsl:variable name='programmeAPRE'>Ann�e pr�pa.</xsl:variable>
<xsl:variable name='programmeBAA'>Licenciatura en Administraci�n de Empresas</xsl:variable>
<xsl:variable name='programmeCERT'>Certificados</xsl:variable>
<xsl:variable name='programmeDES'> Diploma de Estudios Superiores Especializados</xsl:variable>
<xsl:variable name='programmeMBA'>Maestr�a en Administraci�n de Empresas</xsl:variable>
<xsl:variable name='programmeMSC'> Maestr�a en Gesti�n </xsl:variable>
<xsl:variable name='programmePHD'>Doctorado en Administraci�n</xsl:variable>

<xsl:variable name='serviceBAA'>Direcci�n de la Licenciatura en Administraci�n de Empresas</xsl:variable>
<xsl:variable name='serviceCERT'>Direcci�n de Certificados</xsl:variable>
<xsl:variable name='serviceDES'> Direcci�n de los diplomas de estudios superiores </xsl:variable>
<xsl:variable name='serviceMBA'>Direcci�n de la Maestr�a en Administraci�n de Empresas</xsl:variable>
<xsl:variable name='serviceMSC'>Direcci�n de la Maestr�a en Gesti�n y del Doctorado en Administraci�n</xsl:variable>
<xsl:variable name='servicePHD'>Direcci�n de la Maestr�a en Gesti�n y del Doctorado en Administraci�n</xsl:variable>

<xsl:variable name='serviceBUR.REGIST'>Oficina de Registro de estudiantes</xsl:variable>
<xsl:variable name='serviceCETAI'>Centro de Estudios en Administraci�n Internacional</xsl:variable>
<xsl:variable name='serviceQUAL.COMM.'>Calidad de la comunicaci�n</xsl:variable>

<xsl:variable name='serviceFINANCE'>Finanzas</xsl:variable>
<xsl:variable name='serviceGOP'>Gesti�n de las Operaciones y de la Logistica</xsl:variable>
<xsl:variable name='serviceGOL'>Gesti�n de las Operaciones y de la Logistica</xsl:variable>
<xsl:variable name='serviceGRH'>Gesti�n de los recursos humanos</xsl:variable>
<xsl:variable name='serviceIEA'>Instituto de Econom�a Aplicada</xsl:variable>
<xsl:variable name='serviceMARKETING'>Mercadotecnia</xsl:variable>
<xsl:variable name='serviceMNGT'>Gesti�n</xsl:variable>
<xsl:variable name='serviceMQG'>M�todos cuantitativos de gesti�n</xsl:variable>
<xsl:variable name='serviceSC.COMPT.'>Ciencias contables</xsl:variable>
<xsl:variable name='serviceTI'>Tecnolog�as de la Informaci�n</xsl:variable>
<xsl:variable name='serviceINTERNAT'>Servicio de ense�anza de Asuntos Internacionales</xsl:variable>

<!-- ===== URLs =========== -->
<xsl:variable name='programmeUrlAPRE'>http://www.hec.ca/programmes/baa/prepa</xsl:variable>
<xsl:variable name='programmeUrlBAA'>http://www.hec.ca/programmes/baa</xsl:variable>
<xsl:variable name='programmeUrlCERT'>http://www.hec.ca/programmes/certificats</xsl:variable>
<xsl:variable name='programmeUrlDES'>http://www.hec.ca/programmes/des</xsl:variable>
<xsl:variable name='programmeUrlMBA'>http://www.hec.ca/programmes/mba</xsl:variable>
<xsl:variable name='programmeUrlMSC'>http://www.hec.ca/programmes/msc</xsl:variable>
<xsl:variable name='programmeUrlPHD'>http://www.hec.ca/programmes/phd</xsl:variable>


<xsl:variable name='serviceUrlBAA'>http://www.hec.ca/programmes/baa</xsl:variable>
<xsl:variable name='serviceUrlCERT'>http://www.hec.ca/programmes/certificats</xsl:variable>
<xsl:variable name='serviceUrlDES'>http://www.hec.ca/programmes/des</xsl:variable>
<xsl:variable name='serviceUrlMBA'>http://www.hec.ca/programmes/mba</xsl:variable>
<xsl:variable name='serviceUrlMSC'>http://www.hec.ca/programmes/msc</xsl:variable>
<xsl:variable name='serviceUrlPHD'>http://www.hec.ca/programmes</xsl:variable>

<xsl:variable name='serviceUrlBUR.REGIST'>http://www.hec.ca/registraire</xsl:variable>
<xsl:variable name='serviceUrlCETAI'>http://cetai.hec.ca</xsl:variable>
<xsl:variable name='serviceUrlQUAL.COMM.'>http://www.hec.ca/qualitecomm</xsl:variable>

<xsl:variable name='serviceUrlFINANCE'>http://www.hec.ca/finance</xsl:variable>
<xsl:variable name='serviceUrlGOP'>http://www.hec.ca/gop</xsl:variable>
<xsl:variable name='serviceUrlGOL'>http://www.hec.ca/gop</xsl:variable>
<xsl:variable name='serviceUrlGRH'>http://www.hec.ca/grh</xsl:variable>
<xsl:variable name='serviceUrlIEA'>http://www.hec.ca/iea</xsl:variable>
<xsl:variable name='serviceUrlMARKETING'>http://www.hec.ca/marketing</xsl:variable>
<xsl:variable name='serviceUrlMNGT'>http://www.hec.ca/management</xsl:variable>
<xsl:variable name='serviceUrlMQG'>http://www.hec.ca/mqg</xsl:variable>
<xsl:variable name='serviceUrlSC.COMPT.'>http://www.hec.ca/sco</xsl:variable>
<xsl:variable name='serviceUrlTI'>http://www.hec.ca/ti</xsl:variable>
<xsl:variable name='serviceUrlINTERNAT'>http://www.hec.ca/affint</xsl:variable>


</xsl:stylesheet>
