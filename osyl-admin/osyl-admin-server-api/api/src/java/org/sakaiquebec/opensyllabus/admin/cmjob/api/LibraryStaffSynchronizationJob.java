package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import java.util.List;

import org.quartz.Job;

import edu.emory.mathcs.backport.java.util.Arrays;

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
 *****************************************************************************/

/**
 * This job adds the role library staff with the correct permissions to all the
 * sites of type course. All the sites will be checked to make sure the role has
 * at least the specified permissions.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface LibraryStaffSynchronizationJob extends Job {

    /**
     * These are the functions that will allowed with this role. If we encounter
     * any other function the job will try to remove it.
     */
    public final static List FUNCTIONS_TO_ALLOW =
	    Arrays.asList(new String[] { "site.visit", "site.viewRoster",
		    "content.delete.own", "content.new", "content.revise.any",
		    "content.revise.own", "content.read" });

    public final static String LIBRARYSTAFF_ROLE = "libraryStaff";

    public final static String SITE_TYPE = "course";

    public final static String REALM_PREFIX = "/site/";

    public final static String TEMPLATE_ID = "!site.template.course";

}
