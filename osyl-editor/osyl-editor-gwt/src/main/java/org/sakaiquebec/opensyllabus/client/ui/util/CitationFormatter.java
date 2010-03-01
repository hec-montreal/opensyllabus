/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.client.ui.util;

import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.shared.model.COContentResource;
import org.sakaiquebec.opensyllabus.shared.model.COProperties;
import org.sakaiquebec.opensyllabus.shared.model.COPropertiesType;
import org.sakaiquebec.opensyllabus.shared.model.CitationSchema;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CitationFormatter {

    private static final String MESSAGE_PREFIX = "Citation.format.";

    private static final String TITLE_IDENTIFIER = "%t";
    private static final String CREATOR_IDENTIFIER = "%c";
    private static final String EDITOR_IDENTIFIER = "%e";
    private static final String VOLUME_IDENTIFIER = "%v";
    private static final String ISSUE_IDENTIFIER = "%i";
    private static final String SOURCE_TITLE_IDENTIFIER = "%o";
    private static final String DATE_IDENTIFIER = "%a";
    private static final String PAGES_IDENTIFIER = "%p";
    private static final String PUBLISHER_IDENTIFIER = "%b";
    private static final String YEAR_IDENTIFIER = "%y";
    private static final String ISN_IDENTIFIER = "%s";
    private static final String DOI_IDENTIFIER = "%d";

    public static String format(OsylCitationItem oci, String format) {
	String formatString = replaceKeysByMessage(format);
	COProperties cop = oci.getProperties();

	formatString =
		replace(formatString, YEAR_IDENTIFIER, getProperty(cop,
			CitationSchema.YEAR));
	formatString =
		replace(formatString, ISN_IDENTIFIER, getProperty(cop,
			CitationSchema.ISN));
	formatString =
		replace(formatString, DOI_IDENTIFIER, getProperty(cop,
			CitationSchema.DOI));
	formatString =
		replace(formatString, DATE_IDENTIFIER, getProperty(cop,
			CitationSchema.DATE));
	formatString =
		replace(formatString, PAGES_IDENTIFIER, getProperty(cop,
			CitationSchema.PAGES));
	formatString =
		replace(formatString, VOLUME_IDENTIFIER, getProperty(cop,
			CitationSchema.VOLUME));
	formatString =
		replace(formatString, ISSUE_IDENTIFIER, getProperty(cop,
			CitationSchema.ISSUE));
	formatString =
		replace(formatString, EDITOR_IDENTIFIER, getProperty(cop,
			CitationSchema.EDITOR));
	formatString =
		replace(formatString, PUBLISHER_IDENTIFIER, getProperty(cop,
			CitationSchema.PUBLISHER));
	formatString =
		replace(formatString, CREATOR_IDENTIFIER, getProperty(cop,
			CitationSchema.CREATOR));
	formatString =
		replace(formatString, TITLE_IDENTIFIER, getProperty(cop,
			CitationSchema.TITLE));
	formatString =
		replace(formatString, SOURCE_TITLE_IDENTIFIER, getProperty(cop,
			CitationSchema.SOURCE_TITLE));
	return formatString;
    }

    public static String format(COContentResource ressource, String format) {
	String formatString = replaceKeysByMessage(format);
	COProperties cop = ressource.getProperties();
	formatString =
		replace(formatString, YEAR_IDENTIFIER, getProperty(cop,
			CitationSchema.YEAR));
	formatString =
		replace(formatString, ISN_IDENTIFIER, getProperty(cop,
			COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_ISN));
	formatString =
		replace(formatString, DOI_IDENTIFIER, getProperty(cop,
			COPropertiesType.IDENTIFIER,
			COPropertiesType.IDENTIFIER_TYPE_DOI));
	formatString =
		replace(formatString, DATE_IDENTIFIER, getProperty(cop,
			CitationSchema.DATE));
	formatString =
		replace(formatString, PAGES_IDENTIFIER, getProperty(cop,
			CitationSchema.PAGES));
	formatString =
		replace(formatString, VOLUME_IDENTIFIER, getProperty(cop,
			CitationSchema.VOLUME));
	formatString =
		replace(formatString, ISSUE_IDENTIFIER, getProperty(cop,
			CitationSchema.ISSUE));
	formatString =
		replace(formatString, EDITOR_IDENTIFIER, getProperty(cop,
			CitationSchema.EDITOR));
	formatString =
		replace(formatString, PUBLISHER_IDENTIFIER, getProperty(cop,
			CitationSchema.PUBLISHER));
	formatString =
		replace(formatString, CREATOR_IDENTIFIER, getProperty(cop,
			COPropertiesType.AUTHOR));
	formatString =
		replace(formatString, TITLE_IDENTIFIER, getProperty(cop,
			CitationSchema.TITLE));
	formatString =
		replace(formatString, SOURCE_TITLE_IDENTIFIER, getProperty(cop,
			COPropertiesType.JOURNAL));
	return formatString;
    }

    private static String replace(String s, String identifier, String prop) {
	String exp = findExpression(s, identifier);
	if (prop == null || prop.trim().equals("")) {
	    s = s.replace(exp, "");
	} else {
	    String replacement = exp.replace(identifier, prop);
	    if (replacement.startsWith("[")) {
		replacement =
			replacement.substring(1, replacement.length() - 1);
	    }
	    s = s.replace(exp, replacement);
	}
	return s;
    }

    private static String findExpression(String f, String identifier) {
	String regex = ".*\\[.*\\" + identifier + ".*\\].*";
	if (f.matches(regex)) {
	    String[] splits = f.split("\\" + identifier);
	    return splits[0].substring(splits[0].lastIndexOf("[")) + identifier
		    + splits[1].substring(0, splits[1].indexOf("]") + 1);
	} else {
	    return identifier;
	}
    }

    private static String replaceKeysByMessage(String f) {
	String regex = ".*\\@.*\\@.*";
	while (f.matches(regex)) {
	    String key = f.substring(f.indexOf("@") + 1);
	    key = key.substring(0, key.indexOf("@"));
	    String message =
		    OsylController.getInstance().getUiMessage(
			    MESSAGE_PREFIX + key);
	    f = f.replace("@" + key + "@", message);
	}
	return f;
    }

    private static String getProperty(COProperties p, String ident) {
	String s = "";
	s = p.getProperty(ident);
	return s == null ? "" : s;
    }

    private static String getProperty(COProperties p, String ident, String type) {
	String s = "";
	s = p.getProperty(ident, type);
	return s == null ? "" : s;
    }
}