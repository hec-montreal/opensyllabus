/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
package org.sakaiquebec.opensyllabus.portal.client.view;

import java.util.Comparator;
import java.util.List;

import org.sakaiquebec.opensyllabus.portal.client.controller.PortalController;
import org.sakaiquebec.opensyllabus.portal.client.image.PortalImages;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class AbstractPortalView extends Composite {

    public final static String SUMMER = "E";

    public final static String WINTER = "H";

    public final static String FALL = "A";

    private String viewKey;

    private PortalImages images = (PortalImages) GWT.create(PortalImages.class);

    protected Comparator<CODirectorySite> courseNameComparator =
	    new Comparator<CODirectorySite>() {

		public int compare(CODirectorySite cods1, CODirectorySite cods2) {
		    String siteName1 = cods1.getCourseNumber();
		    String siteName2 = cods2.getCourseNumber();
		    return siteName1.compareTo(siteName2);
		}
	    };

    protected Comparator<String> courseSectionComparator =
	    new Comparator<String>() {

		public int compare(String siteName1, String siteName2) {
		    int res = 0;
		    if (siteName1 != null && siteName2 != null
			    && !siteName1.trim().equals("")
			    && !siteName2.trim().equals("")) {
			res =
				getSessionYear(siteName1).compareTo(
					getSessionYear(siteName2));
			if (res == 0) {
			    res =
				    getSessionLetterIndex(siteName1).compareTo(
					    getSessionLetterIndex(siteName2));
			    if (res == 0) {
				res =
					getNumber(siteName1).compareTo(
						getNumber(siteName2));
				if (res == 0)
				    return getSection(siteName1).compareTo(
					    getSection(siteName2));
			    }
			}
		    }
		    return res;

		}
	    };

    public AbstractPortalView(String key) {
	this.setViewKey(key);
	History.newItem(viewKey);
    }

    public String getMessage(String key) {
	return getController().getMessage(key);
    }

    public PortalController getController() {
	return PortalController.getInstance();
    }

    public PortalImages getImages() {
	return images;
    }

    public void setViewKey(String viewKey) {
	this.viewKey = viewKey;
    }

    public String getViewKey() {
	return viewKey;
    }

    protected String getSessionName(String siteName) {
	return getMessage("session_" + getSessionLetter(siteName)) + " "
		+ getSessionYear(siteName).toString();

    }

    protected Integer getSessionYear(String title) {
	return new Integer(getSession(title).substring(1));
    }

    protected Integer getSessionLetterIndex(String title) {
	String letter = getSessionLetter(title);
	Integer i;
	if (WINTER.equals(letter))
	    i = new Integer(0);
	else if (SUMMER.equals(letter))
	    i = new Integer(1);
	else
	    i = new Integer(2);
	return i;
    }

    protected String getSessionLetter(String title) {
	return getSession(title).substring(0, 1);
    }

    protected String getSection(String title) {
	return title.substring(title.lastIndexOf(".") + 1);
    }

    protected String getSession(String title) {
	String temp = title.substring(title.indexOf(".") + 1);
	return temp.substring(0, temp.indexOf("."));
    }

    protected String getNumber(String title) {
	return title.substring(0, title.indexOf("."));
    }

    public String getNameSession(String session) {
	if (session != null && !"".equals(session)) {
	    if (session.startsWith("A")) {
		return getMessage("session_A") + " " + session.substring(1);
	    } else if (session.startsWith("E")) {
		return getMessage("session_E") + " " + session.substring(1);
	    } else if (session.startsWith("H")) {
		return getMessage("session_H") + " " + session.substring(1);
	    }
	}
	return "";
    }
    
    public boolean existsSectionInCourse(String field, List<String> achivedSections, List<String> currentSections) {
	if (!isNull(field)) {
	    if (isFieldInCollection(field,achivedSections) || isFieldInCollection(field, currentSections)) {
		return true;
	    } else {
		return false;
	    }
	}
	return true;
    }  
    
    public boolean isFieldInCollection(String field, List<String> list) {
	for (String item : list) {
	    if (item.indexOf(field) > 0){
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    public boolean isNull(String field) {
	if (field!=null && !field.equals("")) {
	    return false;
	} else {
	    return true;
	}
    }
    
    public static String getViewKeyPrefix(){
	return null;
    }

}
