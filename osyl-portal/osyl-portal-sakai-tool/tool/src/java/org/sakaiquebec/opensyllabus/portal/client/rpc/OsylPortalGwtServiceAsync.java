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

package org.sakaiquebec.opensyllabus.portal.client.rpc;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.CMAcademicSession;
import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylPortalGwtServiceAsync {

    public void getCoursesForAcadCareer(String acadCareer,
	    AsyncCallback<List<CODirectorySite>> callback);

    public void getCoursesForResponsible(String responsible,
	    AsyncCallback<List<CODirectorySite>> callback);

    public void getCoursesForFields(String courseNumber,
	    String courseTitle, String instructor, String program,
	    String responsible, String trimester,
	    AsyncCallback<List<CODirectorySite>> callback);

    public void getDescription(String siteId, AsyncCallback<String> callback);

    public void getCODirectorySite(String courseNumber,
	    AsyncCallback<CODirectorySite> callback);
    
}
