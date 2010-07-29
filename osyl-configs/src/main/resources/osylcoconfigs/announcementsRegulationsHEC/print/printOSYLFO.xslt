<?xml version="1.0"  encoding="UTF-8" ?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&amp;#160;">
]>
<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

<xsl:output method="xml" indent="yes" />
<xsl:param name="ppath"></xsl:param>

  <xsl:template match="/">
    <fo:root>
			<fo:layout-master-set>
        <fo:simple-page-master master-name="first" page-height="11in" page-width="8.5in" margin-left="3cm" margin-right="3cm" margin-top="2.5cm" margin-bottom="1cm">
          <fo:region-body margin-bottom="2cm"/>
					<fo:region-after region-name="Footer" extent="1.5cm" />
				</fo:simple-page-master>

        <fo:simple-page-master master-name="rest" page-height="11in" page-width="8.5in" margin-left="3cm" margin-right="3cm" margin-top="1.5cm" margin-bottom="1.25cm">
					<fo:region-body margin-top="1cm" margin-bottom="2cm"/>
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
					<fo:block>Plan de cours : <xsl:value-of select="/OSYL/CO/identifier"/></fo:block>
					<fo:block text-align="right" margin-top="-15pt"><xsl:value-of select="/OSYL/CO/identifier"/></fo:block>
          <fo:block border-top-color="lightgrey" border-top-style="solid" border-top-width="1.5px" space-before="5pt"/>
				</fo:static-content>

				<fo:static-content flow-name="Footer" font-size="9pt" font-family="serif">
          <fo:block border-top-color="black" border-top-style="solid" border-top-width="thin" margin-right="-5pt" margin-left="-5pt" padding-after="2pt"/>
					<fo:block font-size="8pt" font-style="italic" color="grey">Généré le 22/04/2010 à 11 :40 :02 à partir d’OpenSyllabus est n’est peut-être pas à jour avec la version enligne.</fo:block>
					<fo:block>©HEC Montréal 2010, Tous droits réservés</fo:block>
					<fo:block text-align="right" margin-top="-10pt"><fo:page-number/>/</fo:block>
				</fo:static-content>

				<fo:flow flow-name="xsl-region-body" font-size="10pt" font-family="serif">
					<xsl:call-template name="FirtPageHeader"/>
					<xsl:apply-templates select="//asmUnit[@xsi:type='StaffUnit']"/>
					<xsl:apply-templates select="//asmUnit[@xsi:type='OverviewUnit']"/>
					<xsl:apply-templates select="//asmUnit[@xsi:type='LearningMaterialUnit']"/>
					<xsl:apply-templates select="//asmStructure[@xsi:type='AssessmentStruct']"/>
					<xsl:apply-templates select="//asmStructure[@xsi:type='PedagogicalStruct'][asmStructure/node()]"/>
					<xsl:apply-templates select="//asmUnit[@xsi:type='NewsUnit']"/>
				</fo:flow>

			</fo:page-sequence>

    </fo:root>
  </xsl:template>

<!-- ===================================== -->
<!-- ====== FirtPageHeader =============== -->
<!-- ===================================== -->

<xsl:template name="FirtPageHeader">
		<fo:block>
			<fo:external-graphic content-height="41px" vertical-align="middle" padding-top="-10pt">
				<xsl:attribute name="src"><xsl:value-of select="$ppath"/>img/hecLogo.jpg</xsl:attribute>
			</fo:external-graphic>
		</fo:block>
		<fo:block text-align="right" margin-top="-20pt" font-size="14pt" padding-left="0pt" font-weight="bold">DESS</fo:block>
		<fo:block border-top-color="#555599" border-top-style="solid" border-top-width="3px" margin-right="0pt" margin-left="0pt" padding-before="10pt" padding-after="2pt"/>
		<fo:block font-size="14pt" font-weight="bold" space-before="16pt" text-align='center'><xsl:value-of select="/OSYL/CO/department"/></fo:block>
		<fo:block font-size="14pt" font-weight="bold" space-before="16pt" text-align='center'>
			<fo:inline><xsl:value-of select="/OSYL/CO/title"/> - </fo:inline>
			<fo:inline><xsl:value-of select="/OSYL/CO/identifier"/></fo:inline>
		</fo:block>

		<fo:table width="100%" table-layout="fixed" space-before="10pt">
			<fo:table-column column-width="50%" column-number="1" />
			<fo:table-column column-width="50%" column-number="2" />
			<fo:table-body >
				<fo:table-row>
					<fo:table-cell padding="5px" padding-bottom="3px" border-bottom="1.5px solid black" display-align="center">
						<fo:block text-align="left" font-size="14pt" font-weight="bold">
							<xsl:value-of select="/OSYL/CO/identifier"/>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="5px" padding-bottom="3px" border-bottom="1.5px solid black" display-align="center">
						<fo:block text-align="right" font-size="14pt" font-weight="bold">
							<xsl:value-of select="/OSYL/CO/identifier"/>
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
		<xsl:with-param name="label">Évaluations</xsl:with-param>
	</xsl:call-template>
	<fo:block font-size="13pt" font-weight="bold" space-before="15pt" space-after="5pt">
		Sommaires des évaluations
	</fo:block>
	<xsl:apply-templates select="//asmUnit[@xsi:type='AssessmentUnit']"/>
</xsl:template>

<xsl:template match="asmStructure[@xsi:type='PedagogicalStruct'][asmStructure/node()]">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label">Organisation du cours</xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select="asmStructure[@xsi:type='PedagogicalStruct']"/>
</xsl:template>

<xsl:template match="asmStructure[@xsi:type='PedagogicalStruct'][asmUnit/node()]">
	<xsl:call-template name="SubPedagogicalStructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
		<xsl:with-param name="description"><xsl:value-of select="description"/></xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select="asmUnit[@xsi:type='Lecture']|asmUnit[@xsi:type='WorkSession']"/>
</xsl:template>

<!-- =================================== -->
<!-- ========== asmUnit ================ -->
<!-- =================================== -->
<xsl:template match="asmUnit[@xsi:type='StaffUnit']">
					<fo:block font-size="9pt" text-align='right' space-before="3pt" margin-right="-5pt">
						<fo:inline>Date :</fo:inline>
						<fo:inline padding-left="5pt"><xsl:value-of select="/OSYL/CO/modified"/></fo:inline>
					</fo:block>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='OverviewUnit']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select=".//asmResource"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='LearningMaterialUnit']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select=".//asmResource"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='AssessmentUnit']">
	<fo:table width="100%" table-layout="fixed" space-before="24pt" space-after="10pt">
		<fo:table-column column-width="50%" column-number="1" />
		<fo:table-column column-width="50%" column-number="2" />
		<fo:table-body >
			<fo:table-row>
				<fo:table-cell padding="0px" border-bottom="1px solid black" display-align="center">
					<fo:block text-align="left" font-size="13pt" font-weight="bold">
						<fo:inline><xsl:value-of select="assessmentType"/><xsl:text> </xsl:text></fo:inline>
						<fo:inline>(<xsl:value-of select="weight"/> %)</fo:inline>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="0px" border-bottom="1px solid black" display-align="center">
					<fo:block text-align="left" font-size="13pt" font-weight="bold">
						<xsl:value-of select="date-start"/>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	<fo:block font-size="10pt" space-before="0pt" space-after="0pt">
		<fo:inline><xsl:value-of select="mode"/><xsl:text> / </xsl:text></fo:inline>
		<fo:inline><xsl:value-of select="location"/></fo:inline>
	</fo:block>
	<fo:block font-size="10pt" space-before="0pt" space-after="0pt">
		<fo:inline><xsl:text>Mode de remise : </xsl:text></fo:inline>
		<fo:inline><xsl:value-of select="submition_type"/></fo:inline>
	</fo:block>

 	<xsl:apply-templates select=".//asmResource"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='Lecture']|asmUnit[@xsi:type='WorkSession']">
	<xsl:call-template name="PedagogicalUnitTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select=".//asmResource"/>
</xsl:template>

<xsl:template match="asmUnit[@xsi:type='NewsUnit']">
	<xsl:call-template name="StructTitle">
		<xsl:with-param name="label"><xsl:value-of select="label"/></xsl:with-param>
	</xsl:call-template>
	<xsl:apply-templates select=".//asmResource[@xsi:type='News']"/>
	<xsl:apply-templates select=".//asmResource[@xsi:type='Text']"/>
</xsl:template>

<!-- ===================================== -->
<xsl:template name="AssessmentHeader">
  <xsl:param name="label"/>
	<fo:block font-size="14pt" font-weight="bold" space-before="15pt" space-after="5pt" background-color="lightgrey">
		<xsl:value-of select="$label"/></fo:block>
</xsl:template>

<xsl:template name="StructTitle">
  <xsl:param name="label"/>
	<fo:block font-size="14pt" font-weight="bold" space-before="24pt" space-after="0pt" background-color="lightgrey">
		<xsl:value-of select="$label"/>
	</fo:block>
</xsl:template>

<xsl:template name="SubPedagogicalStructTitle">
  <xsl:param name="label"/>
  <xsl:param name="description"/>
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
</xsl:template>

<xsl:template name="PedagogicalUnitTitle">
  <xsl:param name="label"/>
	<fo:block font-size="13pt" font-weight="bold" space-before="24pt" space-after="0pt" border-bottom="1px solid black">
		<xsl:value-of select="$label"/></fo:block>
</xsl:template>

<!-- ===================================== -->
<!-- ========== Ressource Text =========== -->
<!-- ===================================== -->

<xsl:template match="asmResource[@xsi:type='Text']">
	<fo:block font-size="13pt" font-weight="bold" space-before="10pt" space-after="10pt">
		<xsl:value-of select="../label"/>
	</fo:block>
	<fo:block>
		<xsl:apply-templates select="text/node()"/>
	</fo:block>
</xsl:template>

<!-- ===================================== -->
<!-- ========== Ressource News =========== -->
<!-- ===================================== -->

<xsl:template match="asmResource[@xsi:type='News']">
	<fo:block font-size="13pt" font-weight="bold" space-before="10pt" space-after="10pt">
		<xsl:value-of select="../label"/>
	</fo:block>
	<fo:block>
		<xsl:apply-templates select="text/node()"/>
	</fo:block>
	<fo:block>
		<xsl:value-of select="../modified"/>
	</fo:block>
</xsl:template>

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

<xsl:template match="p">
  <fo:block space-before="5pt" space-after="5pt">
		<xsl:apply-templates select="*|text()"/>
  </fo:block>
</xsl:template>

<xsl:template match="ul|ol">
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
            <xsl:value-of select="1 + (count(ancestor::ol) + count(ancestor::ul)) * 1"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>1</xsl:text>
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
								<xsl:value-of select="1 + (count(ancestor::ol) + count(ancestor::ul)) * 1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>1</xsl:text>
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