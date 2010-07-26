<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

			<fo:layout-master-set>
				<fo:simple-page-master master-name="titreGH"
					page-height="29.7cm" page-width="21cm" margin-top="2cm"
					margin-bottom="1cm" margin-left="2.5cm" margin-right="2.5cm">
					<fo:region-body margin-top="1cm" />
					<fo:region-before extent="3cm" />
					<fo:region-after extent="1.5cm" />
				</fo:simple-page-master>
			</fo:layout-master-set>

			<fo:page-sequence master-reference="titreGH">
				<fo:flow flow-name="xsl-region-body">

					<fo:block font-size="30pt" space-after.optimum="200pt"
						text-align="end">
						Titre du cours
						<xsl:value-of select="//identifier[@type='HEC']" />
					</fo:block>

					<fo:block font-size="48pt" font-family="sans-serif"
						line-height="48pt" space-after.optimum="350pt" background-color="white"
						color="blue" text-align="center" padding-top="3pt">
						<xsl:value-of select="//courseId[@type='HEC']" />
					</fo:block>

					<fo:block font-size="26pt" font-family="sans-serif"
						line-height="26pt" space-after.optimum="0pt" text-align="center">
						HEC	Montréal
           				</fo:block>

				</fo:flow>
			</fo:page-sequence>

		</fo:root>
	</xsl:template>

</xsl:stylesheet>

