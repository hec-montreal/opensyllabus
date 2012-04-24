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
    private static final String VOLUME_IDENTIFIER = "%v";
    private static final String ISSUE_IDENTIFIER = "%i";
    private static final String SOURCE_TITLE_IDENTIFIER = "%o";
    private static final String DATE_IDENTIFIER = "%a";
    private static final String PAGES_IDENTIFIER = "%p";
    private static final String PUBLISHER_IDENTIFIER = "%b";
    private static final String YEAR_IDENTIFIER = "%y";
    private static final String ISN_IDENTIFIER = "%s";
    private static final String DOI_IDENTIFIER = "%d";
    private static final String PUBLICATION_LOCATION_IDENTIFIER = "%l";
    private static final String EDITION_IDENTIFIER = "%e";
    private static final String EDITOR_IDENTIFIER = "%r";
    private static final String START_PAGE_IDENTIFIER = "%q";
    private static final String END_PAGE_IDENTIFIER = "%z";
    

    public static String format(OsylCitationItem oci, String format) {
	String formatString = replaceKeysByMessage(format);
	COProperties cop = oci.getProperties();

	String year = getProperty(cop, CitationSchema.YEAR);
	year = (year.equals("n.d.")) ? "" : year;
	formatString = replace(formatString, YEAR_IDENTIFIER, year);
	
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

	String publisher = getProperty(cop, CitationSchema.PUBLISHER);
	publisher = (publisher.equals("s.n.")) ? "" : publisher;
	formatString = replace(formatString, PUBLISHER_IDENTIFIER, publisher);

	String creator = getProperty(cop, CitationSchema.CREATOR);
	if (creator.indexOf("&") != -1)
	    creator = creator.substring(0, creator.indexOf("&")) + "et al.";
	formatString = replace(formatString, CREATOR_IDENTIFIER, creator);

	String city = getProperty(cop, CitationSchema.PUBLICATION_LOCATION);
	city = (city.equals("S.l.")) ? "" : city;
	formatString =
		replace(formatString, PUBLICATION_LOCATION_IDENTIFIER, city);
	formatString =
		replace(formatString, TITLE_IDENTIFIER, getProperty(cop,
			CitationSchema.TITLE));
	formatString =
			replace(formatString, SOURCE_TITLE_IDENTIFIER, getProperty(cop,
				CitationSchema.SOURCE_TITLE));
	formatString =
			replace(formatString, EDITION_IDENTIFIER, getProperty(cop,
				CitationSchema.EDITION));
	formatString =
			replace(formatString, EDITOR_IDENTIFIER, getProperty(cop,
				CitationSchema.EDITOR));
	formatString =
			replace(formatString, START_PAGE_IDENTIFIER, getProperty(cop,
				CitationSchema.START_PAGE));
	formatString =
			replace(formatString, END_PAGE_IDENTIFIER, getProperty(cop,
				CitationSchema.END_PAGE));
	
	return formatString;
    }

    public static String format(COContentResource ressource, String format) {
	String formatString = replaceKeysByMessage(format);
	COProperties cop = ressource.getProperties();
	
	String year = getProperty(cop, CitationSchema.YEAR);
	year = (year.equals("n.d.")) ? "" : year;
	formatString = replace(formatString, YEAR_IDENTIFIER, year);
	
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

	String publisher = getProperty(cop, CitationSchema.PUBLISHER);
	publisher = (publisher.equals("s.n.")) ? "" : publisher;
	formatString = replace(formatString, PUBLISHER_IDENTIFIER, publisher);

	String creator = getProperty(cop, COPropertiesType.AUTHOR);
	if (creator.indexOf("&") != -1)
	    creator = creator.substring(0, creator.indexOf("&")) + "et al.";
	formatString = replace(formatString, CREATOR_IDENTIFIER, creator);

	String city = getProperty(cop, CitationSchema.PUBLICATION_LOCATION);
	city = (city.equals("S.l.")) ? "" : city;
	formatString =
		replace(formatString, PUBLICATION_LOCATION_IDENTIFIER, city);

	formatString =
		replace(formatString, TITLE_IDENTIFIER, getProperty(cop,
			CitationSchema.TITLE));
	formatString =
		replace(formatString, SOURCE_TITLE_IDENTIFIER, getProperty(cop,
			COPropertiesType.JOURNAL));
	formatString =
			replace(formatString, EDITION_IDENTIFIER, getProperty(cop,
				CitationSchema.EDITION));
	formatString =
			replace(formatString, EDITOR_IDENTIFIER, getProperty(cop,
				CitationSchema.EDITOR));
	formatString =
			replace(formatString, START_PAGE_IDENTIFIER, getProperty(cop,
				CitationSchema.START_PAGE));
	formatString =
			replace(formatString, END_PAGE_IDENTIFIER, getProperty(cop,
				CitationSchema.END_PAGE));
	
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
