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
package org.sakaiquebec.opensyllabus.client.ui;

import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class OsylRichTextArea extends RichTextArea {

    public String getHTML() {
	String html = super.getHTML();
	if (!nullOrEmpty(html))
	    return html;
	else
	    return "";
    }

    private boolean nullOrEmpty(String st) {
	if (st == null || st.trim().equals("")
		|| st.equalsIgnoreCase("<P>&nbsp;</P>")
		|| st.equalsIgnoreCase("<BR>"))
	    return true;
	else
	    return false;
    }

}
