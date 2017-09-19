<?xml version="1.0"  encoding="UTF-8" ?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&amp;#160;">
]>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<xsl:strip-space elements="comment availability"/>

	<xsl:output method="xml" encoding="UTF-8" indent="yes" />
	<xsl:param name="imagePath"></xsl:param>
	<xsl:param name="serverUrl"></xsl:param>
	<xsl:param name="currentDateTime"></xsl:param>
	<xsl:param name="siteId"></xsl:param>
	<xsl:param name="access"></xsl:param>
	<xsl:param name="currentYear"></xsl:param>

	<xsl:variable name='lang'>
		<xsl:value-of select="substring(/OSYL/CO/language,1,2)"/>
	</xsl:variable>

  	<xsl:template match="/">
		<xsl:variable name='courseCode'>
			<xsl:value-of select="substring-before(/OSYL/CO/courseId[@type='HEC'],'.')"/>
		</xsl:variable>
		<xsl:variable name='session_period_group'>
			<xsl:value-of select="substring-after(/OSYL/CO/courseId[@type='HEC'],'.')"/>
		</xsl:variable>
		<xsl:variable name='session'>
			<xsl:value-of select="substring-before($session_period_group,'.')"/>
		</xsl:variable>
		<xsl:variable name='period_group'>
			<xsl:value-of select="substring-after($session_period_group,'.')"/>
		</xsl:variable>
		<xsl:variable name='period'>
			<xsl:choose>
				<xsl:when test="contains($period_group,'.')">
					<xsl:value-of select="substring-before($period_group,'.')"/>
				</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name='group'>
			<xsl:choose>
				<xsl:when test="contains($period_group,'.')">
					<xsl:value-of select="substring-after($period_group,'.')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$period_group"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

    	<fo:root>
			<fo:layout-master-set>
        		<fo:simple-page-master master-name="first" page-height="11in" page-width="8.5in" margin-left="3cm" margin-right="3cm" margin-top="2.5cm" margin-bottom="1cm">
          			<fo:region-body margin-bottom="2cm"/>
					<fo:region-after region-name="Footer" extent="1.5cm" />
				</fo:simple-page-master>

        		<fo:simple-page-master master-name="rest" page-height="11in" page-width="8.5in" margin-left="3cm" margin-right="3cm" margin-top="1.5cm" margin-bottom="1.25cm">
					<fo:region-body margin-top="1.5cm" margin-bottom="2cm"/>
					<fo:region-before region-name="header-rest" extent="1cm"/>
					<fo:region-after region-name="Footer" extent="1.5cm"/>
				</fo:simple-page-master>

				<fo:page-sequence-master master-name="document" >
					<fo:repeatable-page-master-alternatives>
						<fo:conditional-page-master-reference master-reference="first" page-position="first" />
						<fo:conditional-page-master-reference master-reference="rest" page-position="rest"  />
		        		<fo:conditional-page-master-reference master-reference="rest"/>
					</fo:repeatable-page-master-alternatives>
				</fo:page-sequence-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="document">

				<fo:static-content flow-name="header-rest" font-size="11pt" font-family="serif">
					<fo:block>
						<fo:inline>
							<xsl:choose>
								<xsl:when test="$lang = 'FR'">
									<xsl:text>Plan de cours </xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'EN'">
									<xsl:text>Course outline </xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'ES'">
									<xsl:text>Plano de curso </xsl:text>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
						</fo:inline>
						<fo:inline><xsl:value-of select="$courseCode"/> (<xsl:value-of select="$group"/>)</fo:inline>
					</fo:block>
					<fo:block text-align="right" margin-top="-15pt"><xsl:value-of select="$session"/></fo:block>
          			<fo:block border-top-color="lightgrey" border-top-style="solid" border-top-width="1.5px" space-before="5pt"/>
				</fo:static-content>

				<fo:static-content flow-name="Footer" font-size="9pt" font-family="serif">
          			<fo:block border-top-color="black" border-top-style="solid" border-top-width="thin" margin-right="-5pt" margin-left="-5pt" padding-after="2pt"/>
					<fo:block font-size="8pt" font-style="italic" color="grey">
						<fo:inline>
							<xsl:choose>
								<xsl:when test="$lang = 'FR'">
									<xsl:text>Généré le </xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'EN'">
									<xsl:text>Generated on the </xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'ES'">
									<xsl:text>Generado el </xsl:text>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
						</fo:inline>
						<fo:inline>
							<xsl:call-template name="printDate">
								<xsl:with-param name="date"><xsl:value-of select="substring-before($currentDateTime,'T')"/></xsl:with-param>
							</xsl:call-template>
						</fo:inline>
						<fo:inline>
							<xsl:choose>
								<xsl:when test="$lang = 'FR'">
									<xsl:text> à </xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'EN'">
									<xsl:text> at </xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'ES'">
									<xsl:text> a las </xsl:text>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
						</fo:inline>
						<fo:inline><xsl:value-of select="substring-before(substring-after($currentDateTime,'T'),'-')"/></fo:inline>
						<fo:inline>
							<xsl:choose>
								<xsl:when test="$lang = 'FR'">
									<xsl:text> à partir de ZoneCours2 et n’est peut-être pas à jour avec la version en ligne.</xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'EN'">
									<xsl:text> from ZoneCours2 and may not be up to date with the online version.</xsl:text>
								</xsl:when>
								<xsl:when test="$lang = 'ES'">
									<xsl:text> a partir de ZoneCours2 y posiblemente no está al día con la versión en línea.</xsl:text>
								</xsl:when>
								<xsl:otherwise>
								</xsl:otherwise>
							</xsl:choose>
						</fo:inline>
					</fo:block>
					<fo:block>
						<xsl:choose>
							<xsl:when test="$lang = 'FR'">
								<xsl:text>©HEC Montréal </xsl:text><xsl:value-of select="$currentYear"/><xsl:text>, Tous droits réservés</xsl:text>
							</xsl:when>
							<xsl:when test="$lang = 'EN'">
								<xsl:text>©HEC Montréal </xsl:text><xsl:value-of select="$currentYear"/><xsl:text>, All rights reserved</xsl:text>
							</xsl:when>
							<xsl:when test="$lang = 'ES'">
								<xsl:text>©HEC Montréal </xsl:text><xsl:value-of select="$currentYear"/><xsl:text>, Reservados todos los derechos</xsl:text>
							</xsl:when>
							<xsl:otherwise>
							</xsl:otherwise>
						</xsl:choose>
					</fo:block>
					<fo:block text-align="right" margin-top="-10pt"><fo:page-number/>/<fo:page-number-citation ref-id="last-page"/></fo:block>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body" font-size="10pt" font-family="serif">
					<xsl:call-template name="FirtPageHeader">
						<xsl:with-param name="courseCode"><xsl:value-of select="$courseCode"/></xsl:with-param>
						<xsl:with-param name="session"><xsl:value-of select="$session"/></xsl:with-param>
						<xsl:with-param name="group"><xsl:value-of select="$group"/></xsl:with-param>
						<xsl:with-param name="period"><xsl:value-of select="$period"/></xsl:with-param>
					</xsl:call-template>
					<xsl:apply-templates select="//asmUnit[@xsi:type='StaffUnit']"/>
					<xsl:apply-templates select="//asmUnit[@xsi:type='OverviewUnit']"/>
					<xsl:apply-templates select="//asmUnit[@xsi:type='LearningMaterialUnit']"/>
					<xsl:apply-templates select="//asmStructure[@xsi:type='AssessmentStruct']"/>
					<xsl:if test="/OSYL/CO/asmStructure[@xsi:type='PedagogicalStruct']/node()">
						<xsl:call-template name="StructTitle">
							<xsl:with-param name="label">PedagogicalStruct</xsl:with-param>
						</xsl:call-template>
						<xsl:for-each select="//asmUnit[@xsi:type='Lecture' or @xsi:type='WorkSession']">
							<xsl:apply-templates select='.'>
								<xsl:with-param name="pos">
									<xsl:choose>
										<xsl:when test="prefix"><xsl:value-of select="prefix"/></xsl:when>
										<xsl:otherwise><xsl:value-of select="position()"/></xsl:otherwise>
									</xsl:choose>
								</xsl:with-param>
								<xsl:with-param name="preceding-sibling-parentId"><xsl:value-of select="preceding-sibling::asmUnit[1]/@id"/></xsl:with-param>
							</xsl:apply-templates>
						</xsl:for-each>
					</xsl:if>
					<xsl:apply-templates select="//asmUnit[@xsi:type='NewsUnit']"/>
					<xsl:call-template name="rulesSection"/>
					<fo:block id="last-page"/>
				</fo:flow>

			</fo:page-sequence>

    </fo:root>
  </xsl:template>

<!-- ===================================== -->
<!-- ====== FirtPageHeader =============== -->
<!-- ===================================== -->

<xsl:template name="FirtPageHeader">
  <xsl:param name="courseCode"/>
  <xsl:param name="session"/>
  <xsl:param name="group"/>
  <xsl:param name="period"/>
		<fo:block>
			<fo:external-graphic content-height="41px" vertical-align="middle" padding-top="-10pt">
				<xsl:attribute name="src"><xsl:value-of select="$imagePath"/>img/hecmontreal.gif</xsl:attribute>
			</fo:external-graphic>
		</fo:block>
		<fo:block text-align="right" margin-top="-20pt" font-size="14pt" padding-left="0pt" font-weight="bold"><xsl:value-of select="/OSYL/CO/program[@type='HEC']"/></fo:block>
		<fo:block border-top-color="#555599" border-top-style="solid" border-top-width="3px" margin-right="0pt" margin-left="0pt" padding-before="10pt" padding-after="2pt"/>
		<fo:block font-size="14pt" font-weight="bold" space-before="16pt" text-align='center'><xsl:value-of select="/OSYL/CO/department[@type='HEC']"/></fo:block>
		<fo:block font-size="14pt" font-weight="bold" space-before="16pt" text-align='center'>
			<fo:inline><xsl:value-of select="/OSYL/CO/title[@type='HEC']"/> - </fo:inline>
			<fo:inline><xsl:value-of select="$courseCode"/></fo:inline>
			<fo:inline>(<xsl:value-of select="$access"/>)</fo:inline>
		</fo:block>

		<fo:table width="100%" table-layout="fixed" space-before="10pt">
			<fo:table-column column-width="50%" column-number="1" />
			<fo:table-column column-width="50%" column-number="2" />
			<fo:table-body >
				<fo:table-row>
					<fo:table-cell padding-left="5px" padding-bottom="2px" border-bottom="1.5px solid black" display-align="center">
						<fo:block text-align="left" font-size="14pt" font-weight="bold">
							<xsl:value-of select="$session"/><xsl:if test="$period!=''">.<xsl:value-of select="$period"/></xsl:if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding-right="5px" padding-bottom="2px" border-bottom="1.5px solid black" display-align="center">
						<fo:block text-align="right" font-size="14pt" font-weight="bold">
							<fo:inline>
								<xsl:choose>
									<xsl:when test="$lang = 'FR'">
										<xsl:text>Groupe </xsl:text>
									</xsl:when>
									<xsl:when test="$lang = 'EN'">
										<xsl:text>Group </xsl:text>
									</xsl:when>
									<xsl:when test="$lang = 'ES'">
										<xsl:text>Grupo </xsl:text>
									</xsl:when>
									<xsl:otherwise>
									</xsl:otherwise>
								</xsl:choose>
							</fo:inline>
							<fo:inline><xsl:value-of select="$group"/></fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
</xsl:template>

<!-- =================================== -->
<!-- ========== asmStructure =========== -->
<!-- =================================== -->
<xsl:template match="asmStructure[@xsi:type='AssessmentStruct']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label">AssessmentStruct</xsl:with-param>
	</xsl:call-template>
	<fo:block font-size="13pt" font-weight="bold" space-before="15pt" space-after="5pt">
		<xsl:choose>
			<xsl:when test="$lang = 'FR'">
				<xsl:text>Sommaire des évaluations</xsl:text>
			</xsl:when>
			<xsl:when test="$lang = 'EN'">
				<xsl:text>Assessment Summary</xsl:text>
			</xsl:when>
			<xsl:when test="$lang = 'ES'">
				<xsl:text>Resumen de la evaluación</xsl:text>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose>
	</fo:block>
	<xsl:for-each select="asmUnit">
		<fo:table width="100%" table-layout="fixed" font-size="10pt" space-before="0pt">
			<fo:table-column column-width="30%" column-number="1" />
			<fo:table-column column-width="20%" column-number="2" />
			<fo:table-column column-width="50%" column-number="3" />
			<fo:table-body >
				<fo:table-row>
					<fo:table-cell padding="0px" display-align="center">
						<fo:block text-align="left"><xsl:value-of select="label"/></fo:block>
					</fo:table-cell>
					<fo:table-cell padding="0px" display-align="center">
						<fo:block text-align="left"><xsl:value-of select="weight"/> %</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="0px" display-align="center">
						<fo:block text-align="left">
							<xsl:call-template name="printAssessmentDate">
								<xsl:with-param name="type"><xsl:value-of select="assessmentType"/></xsl:with-param>
								<xsl:with-param name="date"><xsl:value-of select="substring-before(date-start,'T')"/></xsl:with-param>
							</xsl:call-template>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:for-each>
	<xsl:apply-templates select="//asmUnit[@xsi:type='AssessmentUnit']"/>
</xsl:template>

<!-- =================================== -->
<!-- ========== asmUnit ================ -->
<!-- =================================== -->
<xsl:template match="asmUnit[@xsi:type='StaffUnit']">
	<xsl:call-template name="Staff">
		<xsl:with-param name="role">lecturers</xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="Staff">
		<xsl:with-param name="role">coordinators</xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="Staff">
		<xsl:with-param name="role">secretaries</xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="Staff">
		<xsl:with-param name="role">teachingassistants</xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="Staff">
		<xsl:with-param name="role">speakers</xsl:with-param>
	</xsl:call-template>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='OverviewUnit']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='description']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='objectives']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='learningstrat']"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='LearningMaterialUnit']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='bibliographicres']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='complbibres']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='misresources']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='case']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='tools']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='pastexams']"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='AssessmentUnit']">
	<fo:table width="100%" table-layout="fixed" space-before="24pt" space-after="10pt">
		<fo:table-column column-width="50%" column-number="1" />
		<fo:table-column column-width="50%" column-number="2" />
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell padding="0px" border-bottom="1px solid black" display-align="center">
					<fo:block text-align="left" font-size="13pt" font-weight="bold">
						<fo:inline><xsl:value-of select="label"/><xsl:text> </xsl:text></fo:inline>
						<fo:inline>(<xsl:value-of select="weight"/> %)</fo:inline>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="0px" border-bottom="1px solid black" display-align="center">
					<fo:block text-align="left" font-size="13pt" font-weight="bold">
						<xsl:call-template name="printAssessmentDate">
							<xsl:with-param name="type"><xsl:value-of select="assessmentType"/></xsl:with-param>
							<xsl:with-param name="date"><xsl:value-of select="substring-before(date-end,'T')"/></xsl:with-param>
						</xsl:call-template>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block font-size="10pt" space-before="0pt" space-after="0pt">
		<xsl:if test="mode!=''">
			<fo:inline>
				<xsl:call-template name="TextByLang">
					<xsl:with-param name="label"><xsl:value-of select="mode"/></xsl:with-param>
				</xsl:call-template>
			</fo:inline>
		</xsl:if>
		<xsl:if test="location!=''">
			<fo:inline>
				<xsl:text> / </xsl:text>
				<xsl:choose>
					<xsl:when test="contains(location,'/')">
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="substring-before(location,'/')"/></xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label">and</xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="substring-after(location,'/')"/></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="location"/></xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</fo:inline>
		</xsl:if>
		<xsl:if test="modality!=''">
			<fo:inline>
				<xsl:text> / </xsl:text>
				<xsl:choose>
					<xsl:when test="contains(modality,'/')">
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="substring-before(modality,'/')"/></xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label">and</xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="substring-after(modality,'/')"/></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="modality"/></xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</fo:inline>
		</xsl:if>
	</fo:block>
	<xsl:if test="submition_type!=''">
		<fo:block font-size="10pt" space-before="0pt" space-after="0pt">
			<fo:inline>
				<xsl:choose>
					<xsl:when test="$lang = 'FR'">
						<xsl:text>Mode de remise : </xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'EN'">
						<xsl:text>Submission method: </xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'ES'">
						<xsl:text>Forma de entrega: </xsl:text>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</fo:inline>
			<fo:inline>
				<xsl:choose>
					<xsl:when test="contains(submition_type,'/')">
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="substring-before(submition_type,'/')"/></xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label">and</xsl:with-param>
						</xsl:call-template>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="substring-after(submition_type,'/')"/></xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="TextByLang">
							<xsl:with-param name="label"><xsl:value-of select="submition_type"/></xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</fo:inline>
		</fo:block>
	</xsl:if>

 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='description']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='objectives']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='evalcriteria']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='evalpreparation']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='evalsubproc']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='misresources']"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='Lecture' or @xsi:type='WorkSession']">
	<xsl:param name="pos"/>
	<xsl:param name="preceding-sibling-parentId"/>

  <xsl:if test="$preceding-sibling-parentId = ''">
		<xsl:call-template name="SubPedagogicalStructTitle">
			<xsl:with-param name="label"><xsl:value-of select="../label"/></xsl:with-param>
			<xsl:with-param name="description"><xsl:value-of select="../description"/></xsl:with-param>
		</xsl:call-template>
  </xsl:if>

	<xsl:call-template name="PedagogicalUnitTitle">
		<xsl:with-param name="pos"><xsl:value-of select="$pos"/></xsl:with-param>
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='description']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='objectives']"/>
	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='actResBefore']"/>
	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='actResDuring']"/>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='actResAfter']"/>
	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='misresources']"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='NewsUnit']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
 	<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']='news']"/>
</xsl:template>

<xsl:template name="rulesSection">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label">
			<xsl:choose>
					<xsl:when test="$lang = 'FR'">
						<xsl:text>Règlements de HEC Montréal</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>HEC Montréal Regulations</xsl:text>
					</xsl:otherwise>
			</xsl:choose>
		</xsl:with-param>
	</xsl:call-template>
	<xsl:call-template name="RubricTitle">
		<xsl:with-param name="label">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Plagiat</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>Plagiarism</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:with-param>
	</xsl:call-template>
	<fo:block space-after="10px">
		<xsl:choose>
			<xsl:when test="$lang='FR'">
				<fo:block>
					<fo:block font-weight="bold">
						<xsl:text>Mon principal devoir: être présent en classe</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>Selon le</xsl:text>
						<fo:inline font-size="10pt" color="blue" text-decoration="underline">
							<fo:basic-link>
								<xsl:attribute name="external-destination">http://www.hec.ca/direction_services/secretariat_general/juridique/reglements_politiques/documents/Reglement_pedagogique.pdf</xsl:attribute>
								<xsl:text> règlement de l'École</xsl:text>
							</fo:basic-link>
						</fo:inline>
						<xsl:text>, la présence de l'étudiant en classe (ou aux activités du cours) est présumée. Ainsi, l'enseignant n'est pas tenu de fournir de l'aide ou d'adapter le cours ou son évaluation en raison d'une absence.</xsl:text>
					</fo:block>
					<fo:block font-weight="bold">
						<xsl:text>L'intégrité intellectuelle: tout le monde y gagne!</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>Notez que toute évaluation peut faire l'objet d'une analyse par un logiciel de détection de similitudes. Informez-vous sur </xsl:text>
						<fo:inline font-size="10pt" color="blue" text-decoration="underline">
							<fo:basic-link>
								<xsl:attribute name="external-destination">http://www.hecca/integrite/bonnes-pratiques-et-ressources/index.html</xsl:attribute>
								<xsl:text>les façons d'éviter le plagiat</xsl:text>
							</fo:basic-link>
						</fo:inline>
						<xsl:text>et attention aux travaux d'équipe et à la collaboration hors-classe! </xsl:text>
						<fo:inline font-size="10pt" color="blue" text-decoration="underline">
							<fo:basic-link>
								<xsl:attribute name="external-destination">http://www.hec.ca/integrite/index.html</xsl:attribute>
								<xsl:text>Pour en savoir plus sur l'intégrité intellectuelle…</xsl:text>
							</fo:basic-link>
						</fo:inline>
					</fo:block>
					<fo:block font-weight="bold">
						<xsl:text>Les examens: à vérifier avant le jour J!</xsl:text>
					</fo:block>
					<fo:block>
						<fo:block>1- La validité de sa carte d’étudiant.
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/rentree-scolaire/carte-etudiante/carte-etudiante.html</xsl:attribute>
									<xsl:text>En savoir plus...</xsl:text>
								</fo:basic-link>
							</fo:inline>
						</fo:block>
						<fo:block>2- L'horaire et la salle de l'examen (dans
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/rentree-scolaire/carte-etudiante/carte-etudiante.html</xsl:attribute>
									<xsl:text>HEC en ligne</xsl:text>
								</fo:basic-link>
							</fo:inline>
							<xsl:text> )</xsl:text>
						</fo:block>
						<fo:block>3- La documentation autorisée a l'examen (dans mon site
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">https://zonecours.hec.ca/</xsl:attribute>
									<xsl:text>ZoneCours</xsl:text>
								</fo:basic-link>
							</fo:inline>
							<xsl:text> )</xsl:text>
						</fo:block>
						<fo:block>4- La conformité de ma calculatrice.
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/services-offerts/ressources-technologiques/calculatrices/calculatrices.html</xsl:attribute>
									<xsl:text>En savoir plus</xsl:text>
								</fo:basic-link>
							</fo:inline>
						</fo:block>
					</fo:block>
				</fo:block>
			</xsl:when>
			<xsl:otherwise>
				<!--<fo:block>
					<fo:block font-weight="bold">
						<xsl:text>Mon principal devoir: être présent en classe</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>Selon le</xsl:text>
						<fo:inline font-size="10pt" color="blue" text-decoration="underline">
							<fo:basic-link>
								<xsl:attribute name="external-destination">http://www.hec.ca/direction_services/secretariat_general/juridique/reglements_politiques/documents/Reglement_pedagogique.pdf</xsl:attribute>
								<xsl:text> règlement de l'École</xsl:text>
							</fo:basic-link>
						</fo:inline>
						<xsl:text>, la présence de l'étudiant en classe (ou aux activités du cours) est présumée. Ainsi, l'enseignant n'est pas tenu de fournir de l'aide ou d'adapter le cours ou son évaluation en raison d'une absence.</xsl:text>
					</fo:block>
					<fo:block font-weight="bold">
						<xsl:text>L'intégrité intellectuelle: tout le monde y gagne!</xsl:text>
					</fo:block>
					<fo:block>
						<xsl:text>Notez que toute évaluation peut faire l'objet d'une analyse par un logiciel de détection de similitudes. Informez-vous sur </xsl:text>
						<fo:inline font-size="10pt" color="blue" text-decoration="underline">
							<fo:basic-link>
								<xsl:attribute name="external-destination">http://www.hecca/integrite/bonnes-pratiques-et-ressources/index.html</xsl:attribute>
								<xsl:text>les façons d'éviter le plagiat</xsl:text>
							</fo:basic-link>
						</fo:inline>
						<xsl:text>et attention aux travaux d'équipe et à la collaboration hors-classe! </xsl:text>
						<fo:inline font-size="10pt" color="blue" text-decoration="underline">
							<fo:basic-link>
								<xsl:attribute name="external-destination">http://www.hec.ca/integrite/index.html</xsl:attribute>
								<xsl:text>Pour en savoir plus sur l'intégrité intellectuelle…</xsl:text>
							</fo:basic-link>
						</fo:inline>
					</fo:block>
					<fo:block font-weight="bold">
						<xsl:text>Les examens: à vérifier avant le jour J!</xsl:text>
					</fo:block>
					<fo:block>
						<fo:block>1- La validité de sa carte d’étudiant.
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/rentree-scolaire/carte-etudiante/carte-etudiante.html</xsl:attribute>
									<xsl:text>En savoir plus...</xsl:text>
								</fo:basic-link>
							</fo:inline>
						</fo:block>
						<fo:block>2- L'horaire et la salle de l'examen (dans
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/rentree-scolaire/carte-etudiante/carte-etudiante.html</xsl:attribute>
									<xsl:text>HEC en ligne</xsl:text>
								</fo:basic-link>
							</fo:inline>
							<xsl:text> )</xsl:text>
						</fo:block>
						<fo:block>3- La documentation autorisée a l'examen (dans mon site
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">https://zonecours.hec.ca/</xsl:attribute>
									<xsl:text>ZoneCours</xsl:text>
								</fo:basic-link>
							</fo:inline>
							<xsl:text> )</xsl:text>
						</fo:block>
						<fo:block>4- La conformité de ma calculatrice.
							<fo:inline font-size="10pt" color="blue" text-decoration="underline">
								<fo:basic-link>
									<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/services-offerts/ressources-technologiques/calculatrices/calculatrices.html</xsl:attribute>
									<xsl:text>En savoir plus</xsl:text>
								</fo:basic-link>
							</fo:inline>
						</fo:block>
					</fo:block>
				</fo:block>-->
				<fo:block>
					<xsl:text>
						Please consult the acts and gestures that are considered plagiarism or another academic
						violation, along with the applicable procedure and sanctions, which range up to suspension and
						even expulsion from HEC Montréal. Violations are analyzed  based on the facts and circumstances,
						and sanctions are applied accordingly.
					</xsl:text>
					<fo:inline font-size="10pt" color="blue" text-decoration="underline">
						<fo:basic-link>
							<xsl:attribute name="external-destination">http://www.hec.ca/en/programs_training/plagiarism.html</xsl:attribute><xsl:text>Learn more about plagiarism...</xsl:text>
						</fo:basic-link>
					</fo:inline>
				</fo:block>
				<xsl:call-template name="RubricTitle">
					<xsl:with-param name="label">
						<xsl:choose>
							<xsl:when test="$lang = 'FR'">
								<xsl:text>Calculatrices</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>Calculators</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
				<fo:block space-after="10px">
					<xsl:choose>
						<xsl:when test="$lang='FR'">
							<fo:block>
								<xsl:text>Les étudiants sont priés de prendre connaissance de la politique d'utilisation de calculatrices lors d'examens lorsque celles-ci sont autorisées.</xsl:text>
								<fo:inline font-size="10pt" color="blue" text-decoration="underline">
									<fo:basic-link>
										<xsl:attribute name="external-destination">http://www.hec.ca/etudiant_actuel/technologies/calculatrices_autorisees/calculatrices.html</xsl:attribute>
										<xsl:text> En savoir plus sur la politique d'usage de calculatrices...</xsl:text>
									</fo:basic-link>
								</fo:inline>
							</fo:block>
						</xsl:when>
						<xsl:otherwise>
							<fo:block>
								<xsl:text>Please consult the </xsl:text>
								<fo:inline font-size="10pt" color="blue" text-decoration="underline">
									<fo:basic-link>
										<xsl:attribute name="external-destination">http://www.hec.ca/en/current_student/technologies/authorized_calculators/calculators.html</xsl:attribute><xsl:text>calculator usage policy</xsl:text>
									</fo:basic-link>
								</fo:inline>
								<xsl:text> during exams when applicable.</xsl:text>
							</fo:block>
						</xsl:otherwise>
					</xsl:choose>
				</fo:block>
			</xsl:otherwise>
		</xsl:choose>
	</fo:block>

</xsl:template>

<!-- ===================================== -->
<!-- ========== Contextes ================ -->
<!-- ===================================== -->
<xsl:template match="asmContext[../@xsi:type != 'StaffUnitContent']">
	<xsl:if test="position()=1">
		<xsl:call-template name="RubricTitle">
			<xsl:with-param name="label"><xsl:value-of select="semanticTag[@type='HEC']/@userDefLabel"/></xsl:with-param>
		</xsl:call-template>
	</xsl:if>
	<xsl:choose>
		<xsl:when test="(level/text()) and (level != 'undefined')">
			<fo:block>
				<fo:external-graphic content-height="20px" vertical-align="middle" padding-left="-30pt" padding-top="0pt">
					<xsl:attribute name="src"><xsl:value-of select="$imagePath"/>img/<xsl:value-of select="level"/>.png</xsl:attribute>
				</fo:external-graphic>
			</fo:block>
			<fo:block padding-top="-15pt">
				<xsl:call-template name="Ressources"/>
			</fo:block>
		</xsl:when>
		<xsl:otherwise>
			<xsl:call-template name="Ressources"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="Staff">
  <xsl:param name="role"/>
  	<xsl:if test=".//asmContext[semanticTag[@type='HEC']=$role]/node()">
  		<xsl:call-template name="StaffUnitTitle">
			<xsl:with-param name="label"><xsl:value-of select=".//asmContext[semanticTag[@type='HEC']=$role][1]/semanticTag/@userDefLabel"/></xsl:with-param>
		</xsl:call-template>
		<xsl:variable name="threshold" select="(count(.//asmContext[semanticTag[@type='HEC']=$role]) + 1) div 2"/>
		<fo:table width="100%" table-layout="fixed" space-before="5pt">
			<fo:table-column column-width="50%" column-number="1" />
			<fo:table-column column-width="50%" column-number="2" />
			<fo:table-body >
				<fo:table-row>
					<fo:table-cell padding="5px">
						<fo:block>
						<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']=$role][(position() &lt;= $threshold)]"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="5px">
						<fo:block>
						<xsl:apply-templates select=".//asmContext[semanticTag[@type='HEC']=$role][(position() &gt; $threshold)]"/>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</xsl:if>
</xsl:template>

<xsl:template match="asmContext[@xsi:type='PeopleContext']">
	<fo:block font-size="10pt" space-before="5pt">
		<fo:inline><xsl:value-of select="Person/firstname"/><xsl:text> </xsl:text></fo:inline>
		<fo:inline><xsl:value-of select="Person/surname"/></fo:inline>
	</fo:block>
	<fo:block font-size="8pt">
		<xsl:value-of select="Person/title"/>
	</fo:block>
	<fo:block font-size="8pt" color="blue" text-decoration="underline">
		<xsl:value-of select="Person/email"/>
	</fo:block>
	<fo:block font-size="8pt">
		<xsl:value-of select="Person/tel"/>
	</fo:block>
	<xsl:if test="Person/officeroom != ''">
		<fo:block font-size="8pt">
			<fo:inline>
				<xsl:choose>
					<xsl:when test="$lang = 'FR'">
						<xsl:text>Bureau : </xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'EN'">
						<xsl:text>Office :</xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'ES'">
						<xsl:text>Oficina : </xsl:text>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</fo:inline>
			<fo:inline><xsl:value-of select="Person/officeroom"/></fo:inline>
		</fo:block>
	</xsl:if>
	<xsl:if test="availability != ''">
		<fo:block font-size="8pt">
			<fo:inline>
				<xsl:choose>
					<xsl:when test="$lang = 'FR'">
						<xsl:text>Disponibilité : </xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'EN'">
						<xsl:text>Availability :</xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'ES'">
						<xsl:text>Disponibilidad : </xsl:text>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</fo:inline>
			<fo:inline><xsl:apply-templates select="availability"/></fo:inline>
		</fo:block>
	</xsl:if>
	<xsl:if test="comment !=''">
		<fo:block font-size="8pt">
			<xsl:apply-templates select="comment"/>
		</fo:block>
	</xsl:if>
</xsl:template>

<!-- ===================================== -->
<!-- ========== Ressources =============== -->
<!-- ===================================== -->

<xsl:template name="Ressources">
	<xsl:choose>
		<xsl:when test="importance = 'true'">
			<fo:block font-size="10pt" margin-left="-1px" margin-right="0px" padding-left="5px" padding-top="3px" padding-bottom="2px" space-before="0pt" space-after="0pt" background-color="lightgrey">
				<xsl:choose>
					<xsl:when test="$lang = 'FR'">
						<xsl:text>IMPORTANT</xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'EN'">
						<xsl:text>IMPORTANT</xsl:text>
					</xsl:when>
					<xsl:when test="$lang = 'ES'">
						<xsl:text>IMPORTANTE</xsl:text>
					</xsl:when>
					<xsl:otherwise>
					</xsl:otherwise>
				</xsl:choose>
			</fo:block>
			<fo:table width="100%" table-layout="fixed" space-before="0pt">
				<fo:table-column column-width="100%" column-number="1" />
				<fo:table-body >
					<fo:table-row>
						<fo:table-cell padding="10pt" padding-top="3px" border="1px solid lightgrey">
							<xsl:apply-templates select="./asmResource"/>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates select="./asmResource"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="asmResource[@xsi:type='Text']">
	<fo:block space-after="10px">
		<xsl:apply-templates select="text/node()"/>
	</fo:block>
</xsl:template>

<xsl:template match="asmResource[@xsi:type='News']">
	<fo:block>
		<xsl:apply-templates select="text/node()"/>
	</fo:block>
	<fo:block space-after="10px">
		<fo:inline>
			<xsl:call-template name="printDate">
				<xsl:with-param name="date"><xsl:value-of select="substring-before(../modified,'T')"/></xsl:with-param>
			</xsl:call-template>
		</fo:inline>
		<fo:inline padding-left="5px">
			<xsl:value-of select="substring-before(substring-after(../modified,'T'),'-')"/>
		</fo:inline>
	</fo:block>
</xsl:template>

<xsl:template match="asmResource[@xsi:type='URL']">
	<fo:block space-after="10px">
		<fo:block font-size="10pt">
			<fo:inline color="blue" text-decoration="underline">
				<fo:basic-link>
						<xsl:attribute name="external-destination"><xsl:value-of select="identifier"/></xsl:attribute>
					<xsl:value-of select="../label"/>
				</fo:basic-link>
			</fo:inline>
			<xsl:if test="asmResourceType!=''">
				<fo:inline color="gray" font-weight="bold">[<xsl:value-of select="asmResourceType"/>]</fo:inline>
			</xsl:if>
		</fo:block>
		<fo:block font-size="10pt" color="gray">
			(<xsl:value-of select="identifier"/>)
		</fo:block>
		<xsl:if test="../comment!=''">
			<fo:block font-size="10pt">
				<xsl:apply-templates select="../comment"/>
			</fo:block>
		</xsl:if>
	</fo:block>
</xsl:template>

<xsl:template match="asmResource[@xsi:type='Entity']">
	<fo:block space-after="10px">
		<fo:block font-size="10pt">
			<fo:inline color="blue" text-decoration="underline">
				<fo:basic-link>
						<xsl:attribute name="external-destination"><xsl:value-of select="$serverUrl"/>/direct<xsl:value-of select="identifier"/></xsl:attribute>
					<xsl:value-of select="../label"/>
				</fo:basic-link>
			</fo:inline>
			<fo:inline color="gray">
				(<xsl:value-of select="identifier"/>)
			</fo:inline>
		</fo:block>
		<xsl:if test="../comment!=''">
			<fo:block font-size="10pt">
				<xsl:apply-templates select="../comment"/>
			</fo:block>
		</xsl:if>
	</fo:block>
</xsl:template>

<xsl:template match="asmResource[@xsi:type='Document']">
	<xsl:variable name='file'>
		<xsl:call-template name="lastIndexOf">
			 <xsl:with-param name="string" select="identifier"/>
			 <xsl:with-param name="char">/</xsl:with-param>
		</xsl:call-template>
	</xsl:variable>

	<fo:block space-after="10px">
		<fo:block font-size="10pt">
			<fo:inline color="blue" text-decoration="underline">
				<fo:basic-link>
					<xsl:attribute name="external-destination"><xsl:value-of select="$serverUrl"/>/sdata/c<xsl:value-of select="identifier"/>?child=<xsl:value-of select="$siteId"/></xsl:attribute>
					<xsl:value-of select="../label"/>
				</fo:basic-link>
			</fo:inline>
			<xsl:if test="asmResourceType!=''">
				<fo:inline color="gray" font-weight="bold">[<xsl:value-of select="asmResourceType"/>]</fo:inline>
			</xsl:if>
		</fo:block>
		<fo:block font-size="10pt" color="gray">
			(<xsl:value-of select="$file"/>)
		</fo:block>
		<xsl:if test="../comment!=''">
			<fo:block font-size="10pt">
				<xsl:apply-templates select="../comment"/>
			</fo:block>
		</xsl:if>
	</fo:block>
</xsl:template>

<xsl:template match="asmResource[@xsi:type='BiblioResource']">
	<fo:block space-after="10px">
		<fo:block font-size="10pt">
			<xsl:choose>
				<xsl:when test="resourceType='article'" >
					<xsl:choose>
						<xsl:when test="contains(author,'&amp;')">
							<fo:inline><xsl:value-of select="substring-before(author,'&amp;')"/> et al.</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline><xsl:value-of select="author"/></fo:inline>
						</xsl:otherwise>
					</xsl:choose>
					<fo:inline> (<xsl:value-of select="year"/>). </fo:inline>
					<fo:inline>« <xsl:value-of select="title"/> »</fo:inline>
					<fo:inline font-style="italic">, <xsl:value-of select="journal"/></fo:inline>
					<fo:inline>, vol.<xsl:value-of select="volume"/></fo:inline>
					<fo:inline>, no.<xsl:value-of select="issue"/>.</fo:inline>
				</xsl:when>
				<xsl:when test="resourceType='chapter'" >
					<xsl:choose>
						<xsl:when test="contains(author,'&amp;')">
							<fo:inline><xsl:value-of select="substring-before(author,'&amp;')"/> et al.</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline><xsl:value-of select="author"/></fo:inline>
						</xsl:otherwise>
					</xsl:choose>
					<fo:inline> (<xsl:value-of select="year"/>). </fo:inline>
					<fo:inline>« <xsl:value-of select="title"/> »</fo:inline>
					<fo:inline font-style="italic">, <xsl:value-of select="journal"/></fo:inline>
					<fo:inline>, <xsl:value-of select="edition"/> éd</fo:inline>
					<fo:inline>, <xsl:value-of select="publicationLocation"/></fo:inline>
					<fo:inline>, p. <xsl:value-of select="startPage"/></fo:inline>
					<fo:inline>-<xsl:value-of select="endPage"/>.</fo:inline>
				</xsl:when>
				<xsl:when test="resourceType='electronic'" >
					<xsl:choose>
						<xsl:when test="contains(author,'&amp;')">
							<fo:inline><xsl:value-of select="substring-before(author,'&amp;')"/> et al.</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline><xsl:value-of select="author"/></fo:inline>
						</xsl:otherwise>
					</xsl:choose>
					<fo:inline> (<xsl:value-of select="year"/>). </fo:inline>
					<fo:inline font-style="italic"><xsl:value-of select="title"/></fo:inline>
					<fo:inline>, <xsl:value-of select="journal"/>.</fo:inline>
					<fo:inline>
						<xsl:choose>
							<xsl:when test="$lang = 'FR'">
								<xsl:text> Récupéré le </xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text> Retrieved on </xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</fo:inline>
					<fo:inline><xsl:value-of select="dateRetrieved"/></fo:inline>
				</xsl:when>
				<xsl:when test="resourceType='proceed'" >
					<xsl:choose>
						<xsl:when test="contains(author,'&amp;')">
							<fo:inline><xsl:value-of select="substring-before(author,'&amp;')"/> et al.</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline><xsl:value-of select="author"/></fo:inline>
						</xsl:otherwise>
					</xsl:choose>
					<fo:inline> (<xsl:value-of select="year"/>). </fo:inline>
					<fo:inline>« <xsl:value-of select="title"/> »</fo:inline>
					<fo:inline font-style="italic">, <xsl:value-of select="journal"/>.</fo:inline>
				</xsl:when>
				<xsl:when test="resourceType='thesis'" >
					<xsl:choose>
						<xsl:when test="contains(author,'&amp;')">
							<fo:inline><xsl:value-of select="substring-before(author,'&amp;')"/> et al.</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline><xsl:value-of select="author"/></fo:inline>
						</xsl:otherwise>
					</xsl:choose>
					<fo:inline> (<xsl:value-of select="year"/>)</fo:inline>
					<fo:inline font-style="italic">. <xsl:value-of select="title"/></fo:inline>
					<fo:inline>, Thèse (Ph.D.)</fo:inline>
					<fo:inline>, <xsl:value-of select="publicationLocation"/></fo:inline>
					<fo:inline>, <xsl:value-of select="university"/></fo:inline>
					<fo:inline>, <xsl:value-of select="pages"/>p.</fo:inline>
				</xsl:when>
				<xsl:when  test="resourceType='book' or resourceType='report'">
					<xsl:choose>
						<xsl:when test="contains(author,'&amp;')">
							<fo:inline><xsl:value-of select="substring-before(author,'&amp;')"/> et al.</fo:inline>
						</xsl:when>
						<xsl:otherwise>
							<fo:inline><xsl:value-of select="author"/></fo:inline>
						</xsl:otherwise>
					</xsl:choose>
					<fo:inline> (<xsl:value-of select="year"/>)</fo:inline>
					<fo:inline font-style="italic">. <xsl:value-of select="title"/></fo:inline>
					<fo:inline>, <xsl:value-of select="publicationLocation"/></fo:inline>
					<fo:inline>, <xsl:value-of select="publisher"/></fo:inline>
					<fo:inline>.</fo:inline>
					<xsl:if test="identifier[@type='isn']">
						<fo:block>
							<fo:inline>ISBN:<xsl:value-of select="identifier[@type='isn']"/></fo:inline>
						</fo:block>
					</xsl:if>
				</xsl:when>
				<xsl:otherwise>
					<fo:inline><xsl:value-of select="title"/></fo:inline>
				</xsl:otherwise>
			</xsl:choose>
		</fo:block>
		<xsl:if test="asmResourceType!=''">
			<fo:block color="gray" font-weight="bold">
				[<xsl:value-of select="asmResourceType"/>]
			</fo:block>
		</xsl:if>
		<xsl:if test="../comment!=''">
			<fo:block font-size="10pt">
				<xsl:apply-templates select="../comment"/>
			</fo:block>
		</xsl:if>
		<xsl:if test="identifier/@type='library'">
			<xsl:call-template name="BiblioResource_link">
				 <xsl:with-param name="img">library_link</xsl:with-param>
				 <xsl:with-param name="link_text">library_link_text</xsl:with-param>
				 <xsl:with-param name="identifier"><xsl:value-of select="identifier[@type='library']"/></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:choose>
			<xsl:when test="identifier/@type='bookstore'">
				<xsl:if test="identifier[@type='bookstore']!='inactif'">
					<xsl:call-template name="BiblioResource_link">
						<xsl:with-param name="img">bookstore_link</xsl:with-param>
						<xsl:with-param name="link_text">bookstore_link_text</xsl:with-param>
						<xsl:with-param name="identifier"><xsl:value-of select="identifier[@type='bookstore']"/></xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:when test="identifier/@type='isn' and (resourceType='book' or resourceType='chapter' or resourceType='report')">
				<xsl:call-template name="BiblioResource_link">
					<xsl:with-param name="img">bookstore_link</xsl:with-param>
					<xsl:with-param name="link_text">bookstore_link_text</xsl:with-param>
					<xsl:with-param name="identifier">http://www.coophec.com/product_detail.aspx?isbn=<xsl:value-of select="identifier[@type='isn']"/></xsl:with-param>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
		<xsl:if test="identifier/@type='other_link'">
			<xsl:variable name="other_link_label">
				<xsl:choose>
					<xsl:when test="identifier/@label!='' "><xsl:value-of select="identifier/@label"/></xsl:when>
					<xsl:otherwise>other_link_text</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:call-template name="BiblioResource_link">
				 <xsl:with-param name="img">other_link</xsl:with-param>
				 <xsl:with-param name="link_text"><xsl:value-of select="$other_link_label"/></xsl:with-param>
				 <xsl:with-param name="identifier"><xsl:value-of select="identifier[@type='other_link']"/></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</fo:block>
</xsl:template>

<xsl:template name="BiblioResource_link">
  <xsl:param name="img"/>
  <xsl:param name="link_text"/>
  <xsl:param name="identifier"/>
	<fo:table width="100%" table-layout="fixed">
		<fo:table-column column-width="10%" column-number="1" />
		<fo:table-column column-width="90%" column-number="2" />
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell padding="0px" display-align="center">
					<fo:block text-align="left">
						<fo:external-graphic content-height="10px" vertical-align="middle" padding-left="0pt" padding-top="0pt">
							<xsl:attribute name="src"><xsl:value-of select="$imagePath"/>img/<xsl:value-of select="$img"/>.gif</xsl:attribute>
						</fo:external-graphic>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="0px" display-align="center">
					<fo:block text-align="left" font-size="10pt" color="blue" text-decoration="underline">
						<fo:basic-link>
								<xsl:attribute name="external-destination"><xsl:value-of select="$identifier"/></xsl:attribute>
							<xsl:call-template name="TextByLang">
								<xsl:with-param name="label"><xsl:value-of select="$link_text"/></xsl:with-param>
							</xsl:call-template>
						</fo:basic-link>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
</xsl:template>

<!-- ===================================== -->
<xsl:template name="AssessmentHeader">
  <xsl:param name="label"/>
	<fo:block font-size="14pt" font-weight="bold" space-before="15pt" space-after="5pt" background-color="lightgrey">
		<xsl:call-template name="TextByLang">
			<xsl:with-param name="label"><xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
	</fo:block>
</xsl:template>

<xsl:template name="StructTitle">
  <xsl:param name="label"/>
	<fo:block font-size="14pt" font-weight="bold" space-before="24pt" space-after="0pt" background-color="lightgrey">
		<xsl:call-template name="TextByLang">
			<xsl:with-param name="label"><xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
	</fo:block>
</xsl:template>

<xsl:template name="SubPedagogicalStructTitle">
  <xsl:param name="label"/>
  <xsl:param name="description"/>
  <xsl:if test="$label != '' ">
	<fo:table width="100%" table-layout="fixed" space-before="24pt" space-after="10pt">
		<fo:table-column column-width="100%" column-number="1" />
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell padding="0px" border-top="1px solid black" border-left="1px solid black" border-bottom="1.5px solid black" border-right="1.5px solid black" display-align="center">
					<fo:block text-align="center" font-size="14pt" font-weight="bold">
						<fo:inline><xsl:value-of select="$label"/></fo:inline>
					</fo:block>
					<fo:block text-align="center" font-size="10pt">
						<fo:inline><xsl:value-of select="$description"/></fo:inline>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
  </xsl:if>
</xsl:template>

<xsl:template name="PedagogicalUnitTitle">
  <xsl:param name="pos"/>
  <xsl:param name="label"/>
	<fo:block font-size="13pt" font-weight="bold" space-before="24pt" space-after="0pt" border-bottom="1px solid black">
		<xsl:value-of select="$pos"/> - <xsl:value-of select="$label"/>
	</fo:block>
</xsl:template>

<xsl:template name="RubricTitle">
  <xsl:param name="label"/>
	<fo:block font-size="13pt" font-weight="bold" space-before="10pt" space-after="10pt">
		<xsl:call-template name="TextByLang">
			<xsl:with-param name="label"><xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
	</fo:block>
</xsl:template>

<xsl:template name="StaffUnitTitle">
  <xsl:param name="label"/>
	<fo:block font-size="12pt" font-weight="bold" margin-left="0px" padding-left="5px" padding-top="3px" padding-bottom="2px" space-before="15pt" space-after="5pt" background-color="lightgrey">
		<xsl:value-of select="$label"/>
	</fo:block>
</xsl:template>

<xsl:template name="StaffUnitSubtitle">
  <xsl:param name="label"/>
	<fo:block font-size="12pt" font-weight="bold" margin-left="0px" padding-left="5px" space-before="10pt" space-after="5pt" background-color="lightgrey">
		<xsl:call-template name="TextByLang">
			<xsl:with-param name="label"><xsl:value-of select="$label"/></xsl:with-param>
		</xsl:call-template>
	</fo:block>
</xsl:template>

<!-- =======================================================================
-->
<!-- ===================================== -->

<xsl:template name="TextByLang">
  <xsl:param name="label"/>

	<xsl:choose>
		<xsl:when test="$label = 'library_link_text'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Disponible à la bibliothèque</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Available at the library</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Disponible en la biblioteca</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'bookstore_link_text'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Disponible à la coop HEC</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Available at the coop HEC</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Disponible en la coop HEC</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'other_link_text'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Version libre de droit en ligne</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Free legal online version</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Versión en línea</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'PedagogicalStruct'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Organisation du cours</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Course Organisation</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Organización del curso</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'AssessmentStruct'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Évaluations</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Evaluations</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Evaluaciones</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'home'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>À la maison - Hors classe</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>At Home - Out of class</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>En Casa - Fuera de clase</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'inClass'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>En classe</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>In Class</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>En la clase</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'oral'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Oral</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Oral</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Oral</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'written'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Écrit</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Written</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Por escrito</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'individual'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Individuel</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Individual</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Individual</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'team'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>En équipe</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Team</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Equipo</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'elect'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Électronique</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Electronic</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Electrónica</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'paper'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text>Papier</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text>Paper</xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text>Papel</xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:when test="$label = 'and'">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<xsl:text> et </xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<xsl:text> and </xsl:text>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<xsl:text> y </xsl:text>
				</xsl:when>
				<xsl:otherwise>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="$label"/>
		</xsl:otherwise>
	</xsl:choose>

</xsl:template>

<xsl:template name="printAssessmentDate">
  <xsl:param name="type"/>
  <xsl:param name="date"/>

	<xsl:choose>
		<xsl:when test="$date!=''">
			<xsl:call-template name="printDate">
				<xsl:with-param name="date"><xsl:value-of select="$date"/></xsl:with-param>
			</xsl:call-template>
		</xsl:when>
		<xsl:when test="$date='' and ($type='intra_exam' or $type='final_exam')">
			<xsl:choose>
				<xsl:when test="$lang = 'FR'">
					<fo:inline>Voir </fo:inline>
					<fo:inline font-style="italic">HEC en ligne</fo:inline>
					<fo:inline> pour date</fo:inline>
				</xsl:when>
				<xsl:when test="$lang = 'EN'">
					<fo:inline>See </fo:inline>
					<fo:inline font-style="italic">HEC en ligne</fo:inline>
					<fo:inline> for date</fo:inline>
				</xsl:when>
				<xsl:when test="$lang = 'ES'">
					<fo:inline>Ver </fo:inline>
					<fo:inline font-style="italic">HEC en ligne</fo:inline>
					<fo:inline> para fecha</fo:inline>
				</xsl:when>
			</xsl:choose>
		</xsl:when>
		<xsl:otherwise>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="getDay">
	<xsl:param name="date"></xsl:param>

	<xsl:variable name="dateWithoutYear" select="substring-after(normalize-space($date),'-')"/>
	<xsl:choose>
		<xsl:when test="substring-before(substring-after(normalize-space($dateWithoutYear),'-'), ' ') != ''">
			<xsl:value-of select="substring-before(substring-after(normalize-space($dateWithoutYear),'-'), ' ')"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:value-of select="substring-after(normalize-space($dateWithoutYear),'-')"/>
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template name="getMonth">
	<xsl:param name="date"></xsl:param>

	<xsl:variable name="dateWithoutYear" select="substring-after(normalize-space($date),'-')"/>
	<xsl:value-of select="substring-before($dateWithoutYear,'-')"/>

</xsl:template>

<xsl:template name="getYear">
	<xsl:param name="date"></xsl:param>

	<xsl:value-of select="substring-before($date,'-')"/>
</xsl:template>

<xsl:template name="printDate">
	<xsl:param name="date"></xsl:param>

	<xsl:call-template name="getDay">
		<xsl:with-param name="date" select="$date"/>
	</xsl:call-template>
	<xsl:text disable-output-escaping="yes">/</xsl:text>
	<xsl:call-template name="getMonth">
		<xsl:with-param name="date" select="$date"/>
	</xsl:call-template>
	<xsl:text disable-output-escaping="yes">/</xsl:text>
	<xsl:call-template name="getYear">
		<xsl:with-param name="date" select="$date"/>
	</xsl:call-template>

</xsl:template>

<!-- ===================================== -->
<!-- ===================================== -->

<xsl:template match="span">
	<xsl:variable name='style'><xsl:value-of select="@style"/></xsl:variable>
  <fo:inline>
		<xsl:if test="contains($style, 'font-weight')">
			<xsl:attribute name="font-weight">
				<xsl:call-template name="lastIndexOfAB">
					 <xsl:with-param name="string" select="$style" />
					 <xsl:with-param name="a">font-weight: </xsl:with-param>
					 <xsl:with-param name="b">;</xsl:with-param>
				</xsl:call-template>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="contains($style, 'text-decoration')">
			<xsl:attribute name="text-decoration">
				<xsl:call-template name="lastIndexOfAB">
					 <xsl:with-param name="string" select="$style" />
					 <xsl:with-param name="a">text-decoration: </xsl:with-param>
					 <xsl:with-param name="b">;</xsl:with-param>
				</xsl:call-template>
			</xsl:attribute>
		</xsl:if>
		<xsl:if test="contains($style, 'font-style')">
			<xsl:attribute name="font-style">
				<xsl:call-template name="lastIndexOfAB">
					 <xsl:with-param name="string" select="$style" />
					 <xsl:with-param name="a">font-style: </xsl:with-param>
					 <xsl:with-param name="b">;</xsl:with-param>
				</xsl:call-template>
			</xsl:attribute>
		</xsl:if>
		<xsl:apply-templates select="*|text()"/>
  </fo:inline>
</xsl:template>

<xsl:template match="br">
  <fo:block line-height="3pt"><fo:inline><xsl:text>&#160;</xsl:text></fo:inline></fo:block>
</xsl:template>

<xsl:template match="strong">
	<fo:inline>
		<xsl:attribute name="font-weight">bold</xsl:attribute>
		<xsl:apply-templates select="*|text()"/>
	</fo:inline>
</xsl:template>

<xsl:template match="em">
	<fo:inline>
		<xsl:attribute name="font-style">italic</xsl:attribute>
		<xsl:apply-templates select="*|text()"/>
	</fo:inline>
</xsl:template>

<xsl:template match="u">
	<fo:inline>
		<xsl:attribute name="text-decoration">underline</xsl:attribute>
		<xsl:apply-templates select="*|text()"/>
	</fo:inline>
</xsl:template>

<xsl:template match="p">
  <fo:block space-before="5pt" space-after="5pt">
		<xsl:apply-templates select="*|text()"/>
  </fo:block>
</xsl:template>

<xsl:template match="ul|ol">
  <fo:list-block provisional-distance-between-starts="0.5cm" provisional-label-separation="0.5cm">
    <xsl:attribute name="space-after">
      <xsl:choose>
        <xsl:when test="ancestor::ul or ancestor::ol">
          <xsl:text>0pt</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>12pt</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
    <xsl:attribute name="start-indent">
      <xsl:variable name="ancestors">
        <xsl:choose>
          <xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
            <xsl:value-of select="0 + (count(ancestor::ol) + count(ancestor::ul)) * 1"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>0</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:value-of select="concat($ancestors, 'cm')"/>
    </xsl:attribute>
    <xsl:apply-templates select="*"/>
  </fo:list-block>
</xsl:template>

<xsl:template match="ul/li">
	<fo:list-item>
		<fo:list-item-label end-indent="label-end()">
			<fo:block margin-top="-3pt"><fo:inline font-family="Symbol" font-size="10pt" baseline-shift="super">&#8226;</fo:inline></fo:block>
		</fo:list-item-label>
		<fo:list-item-body start-indent="body-start()">
			<fo:block><xsl:apply-templates select="*|text()"/></fo:block>
		</fo:list-item-body>
	</fo:list-item>
</xsl:template>

<xsl:template match="ol/li">
  <fo:list-item>
    <fo:list-item-label end-indent="label-end()">
      <fo:block>
        <xsl:variable name="value-odr-li">
          <xsl:number level="multiple" count="li" format="1"/>
        </xsl:variable>
        <xsl:variable name="value-attr">
          <xsl:choose>
            <xsl:when test="../@start">
              <xsl:number value="$value-odr-li + ../@start - 1"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:number value="$value-odr-li"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:choose>
          <xsl:when test="../@type='i'">
            <xsl:number value="$value-attr" format="i."/>
          </xsl:when>
          <xsl:when test="../@type='I'">
            <xsl:number value="$value-attr" format="I."/>
          </xsl:when>
          <xsl:when test="../@type='a'">
            <xsl:number value="$value-attr" format="a."/>
          </xsl:when>
          <xsl:when test="../@type='A'">
            <xsl:number value="$value-attr" format="A."/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:number value="$value-attr" format="1."/>
          </xsl:otherwise>
        </xsl:choose>
      </fo:block>
    </fo:list-item-label>
    <fo:list-item-body start-indent="body-start()">
      <fo:block>
        <xsl:apply-templates select="*|text()"/>
      </fo:block>
    </fo:list-item-body>
  </fo:list-item>
</xsl:template>

<xsl:template match="ul/ul|ul/ol|ol/ul|ol/ol">
	<fo:list-item>
		<fo:list-item-label end-indent="label-end()">
			<fo:block></fo:block>
		</fo:list-item-label>
		<fo:list-item-body start-indent="body-start()">
			<fo:list-block provisional-distance-between-starts="0.5cm"
				provisional-label-separation="0.5cm">
				<xsl:attribute name="space-after">
					<xsl:choose>
						<xsl:when test="ancestor::ul or ancestor::ol">
							<xsl:text>0pt</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>12pt</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="start-indent">
					<xsl:variable name="ancestors">
						<xsl:choose>
							<xsl:when test="count(ancestor::ol) or count(ancestor::ul)">
								<xsl:value-of select="0 + (count(ancestor::ol) + count(ancestor::ul)) * 1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>0</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:value-of select="concat($ancestors, 'cm')"/>
				</xsl:attribute>
				<xsl:apply-templates select="*"/>
			</fo:list-block>
		</fo:list-item-body>
	</fo:list-item>
</xsl:template>

<xsl:template match="a">
  <xsl:choose>
    <xsl:when test="@href">
      <fo:basic-link color="blue" text-decoration="underline">
        <xsl:choose>
          <xsl:when test="starts-with(@href, '#')">
            <xsl:attribute name="internal-destination">
              <xsl:value-of select="substring(@href, 2)"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="external-destination">
              <xsl:value-of select="@href"/>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="*|text()"/>
      </fo:basic-link>
      <xsl:if test="starts-with(@href, '#')">
        <fo:page-number-citation ref-id="{substring(@href, 2)}"/>
      </xsl:if>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!-- ===================================== -->
<xsl:template name="lastIndexOf">
   <xsl:param name="string" />
   <xsl:param name="char" />
   <xsl:choose>
      <xsl:when test="contains($string, $char)">
         <xsl:call-template name="lastIndexOf">
            <xsl:with-param name="string"
                            select="substring-after($string, $char)" />
            <xsl:with-param name="char" select="$char" />
         </xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$string" /></xsl:otherwise>
   </xsl:choose>
</xsl:template>

<xsl:template name="lastIndexOfAB">
   <!-- declare that it takes two parameters - the string and the char -->
   <xsl:param name="string" />
   <xsl:param name="a" />
   <xsl:param name="b" />

		<xsl:variable name="lastIndexOfA">
				<xsl:call-template name="lastIndexOf">
					 <xsl:with-param name="string" select="$string" />
					 <xsl:with-param name="char" select="$a" />
				</xsl:call-template>
		</xsl:variable>
   <xsl:choose>
      <xsl:when test="contains($lastIndexOfA, $b)">
      		<xsl:value-of select="substring-before($lastIndexOfA,$b)" />
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$lastIndexOfA" /></xsl:otherwise>
   </xsl:choose>
</xsl:template>

</xsl:stylesheet>