/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.shared.model;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylTestXsl {

    public static final String XSL_PUBLIC =
	    "<?xml version=\"1.0\" standalone=\"yes\"?>"
		    + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
		    + "<xsl:output method=\"xml\" encoding=\"UTF-8\" omit-xml-declaration=\"no\"/>"
		    + "<xsl:output cdata-section-elements=\"text uri label\"/>"
		    + "<!--  =========================================================================== -->"
		    + "	<xsl:template match=\"/\">"
		    + "		<xsl:apply-templates select=\"/*[@scrty='public']|*[not(@scrty)]\" />"
		    + "	</xsl:template>"
		    + "	<xsl:template match=\"@*|node()[@scrty='public']\">"
		    + "	  <xsl:copy>"
		    + "		<xsl:copy-of select=\"@*\"/> "
		    + "	    <xsl:apply-templates select=\"@*|*[@scrty='public']|text()|*[not(@scrty)]\"/>"
		    + "	  </xsl:copy>"
		    + "	</xsl:template>"
		    + "	<xsl:template match=\"@*|text()|*[not(@scrty)]\">"
		    + "	  <xsl:copy-of select=\".\"/>"
		    + "	</xsl:template>"
		    + "<!-- ========================================================== -->"
		    + "<!-- ========================================================== -->"
		    + "</xsl:stylesheet>";

    public static final String XSL_ATTENDEE =
	    "<?xml version=\"1.0\" standalone=\"yes\"?>"
		    + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
		    + "<xsl:output method=\"xml\" encoding=\"UTF-8\" omit-xml-declaration=\"no\"/>"
		    + "<!--  =========================================================================== -->"
		    + "	<xsl:template match=\"/\">"
		    + "		<xsl:apply-templates select=\"/*[@scrty='public' or @scrty='community' or @scrty='attendee']|*[not(@scrty)]\" />"
		    + "	</xsl:template>"
		    + "	<xsl:template match=\"@*|node()[@scrty='public' or @scrty='community' or @scrty='attendee']\">"
		    + "	  <xsl:copy>"
		    + "	  	<xsl:copy-of select=\"@*\"/> "
		    + "	    <xsl:apply-templates select=\"@*|*[@scrty='public' or @scrty='community' or @scrty='attendee']|text()|*[not(@scrty)]\"/>"
		    + "	  </xsl:copy>"
		    + "	</xsl:template>"
		    + "	<xsl:template match=\"@*|text()|*[not(@scrty)]\">"
		    + "	  <xsl:copy-of select=\".\"/>"
		    + "	</xsl:template>"
		    + "<!-- ========================================================== -->"
		    + "<!-- ========================================================== -->"
		    + "</xsl:stylesheet>";

    public static final String XSL_COMMUNITY =
	    "<?xml version=\"1.0\" standalone=\"yes\"?>"
		    + "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
		    + "<xsl:output method=\"xml\" encoding=\"UTF-8\" omit-xml-declaration=\"no\"/>"
		    + "<!--  =========================================================================== -->"
		    + "	<xsl:template match=\"/\">"
		    + "		<xsl:apply-templates select=\"/*[@scrty='public' or @scrty='community']|*[not(@scrty)]\" />"
		    + "	</xsl:template>	"
		    + "	<xsl:template match=\"@*|node()[@scrty='public' or @scrty='community']\">"
		    + "	  <xsl:copy>"
		    + "	  	<xsl:copy-of select=\"@*\"/> "
		    + "	    <xsl:apply-templates select=\"@*|*[@scrty='public' or @scrty='community']|text()|*[not(@scrty)]\"/>"
		    + "	  </xsl:copy>"
		    + "	</xsl:template>	"
		    + "	<xsl:template match=\"@*|text()|*[not(@scrty)]\">"
		    + "	  <xsl:copy-of select=\".\"/>"
		    + "	</xsl:template>"
		    + "<!-- ========================================================== -->"
		    + "<!-- ========================================================== -->"
		    + "</xsl:stylesheet>";

}
