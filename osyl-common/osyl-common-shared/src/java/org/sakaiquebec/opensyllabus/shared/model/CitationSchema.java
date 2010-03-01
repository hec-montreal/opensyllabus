/**********************************************************************************
 * $Id: CitationSchema.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;

/**
 * This code gives the properties for a citation
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: CitationSchema.java 661 2008-05-28 15:24:39Z
 *          sacha.lepretre@crim.ca $
 */
public interface CitationSchema extends Serializable {

    public static final String CITATION_TYPE_PREFIX = "Citation.type.";
    public static final String TYPE_UNKNOWN = "unknown";
    public static final String TYPE_BOOK = "book";
    public static final String TYPE_CHAPTER = "chapter";
    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_REPORT = "report";
    public static final String TYPE_PROCEED = "proceed";

    public static final String CITATION_IDENTIFIER = "sakai:url_string";
    public static final String CITATION_IDENTIFIER_TYPE = "type_url";

    public static final String CITATIONID = "citationId";
    public static final String TYPE = "type";
    public static final String LONGTEXT = "longtext";
    public static final String SHORTTEXT = "shorttext";
    public static final String NUMBER = "number";
    public static final String DATE = "date";

    public static final String TITLE = "title";
    public static final String EDITOR = "editor";
    public static final String CREATOR = "creator";
    public static final String VOLUME = "volume";
    public static final String ISSUE = "issue";
    public static final String PAGES = "pages";
    public static final String PUBLISHER = "publisher";
    public static final String YEAR = "year";
    public static final String ISN = "isnIdentifier";
    public static final String DOI = "doi";
    public static final String SOURCE_TITLE = "sourceTitle";
    public static final String URL = "url";
}
