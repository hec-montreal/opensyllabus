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
package org.sakaiquebec.opensyllabus.shared.model;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author <a href="mailto:mathieu.cantin@hec.ca">Mathieu Cantin</a>
 * @version $Id: $
 */
public class CMAcademicSession  implements Serializable,
Comparable<CMAcademicSession> {

    private static final long serialVersionUID = -1498844713154586116L;
    
//    private static final String  FALL = "A";
//    private static final String SUMMER = "E";
//    private static final String WINTER = "H";
    
    private String id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    
    public CMAcademicSession(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int compareTo(CMAcademicSession o) {
	return this.getId().compareTo(o.getId());
    }
    
//    public String getSessionName() {
//	String sessionName = null;
//	String year = getStartDate().toString().substring(0, 4);
//
//	if ((getId().charAt(3)) == '1')
//	    sessionName = WINTER + year;
//	if ((getId().charAt(3)) == '2')
//	    sessionName = SUMMER + year;
//	if ((getId().charAt(3)) == '3')
//	    sessionName = FALL + year;
//
//	return sessionName;
//    }
}

