/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.shared.api;

import java.util.Arrays;
import java.util.List;

/**
 * This interface provides security and context-related methods used by the DAO
 * and services.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface SecurityInterface {

    /**
     * Community access security value.
     */
    public static final String ACCESS_COMMUNITY = "community";

    /**
     * Public access security value.
     */
    public static final String ACCESS_PUBLIC = "public";
    
    /**
     * Attendee access security value.
     */
    public static final String ACCESS_ATTENDEE = "attendee";
    
    /** the OsylManager fonctions **/
    public final static String OSYL_MANAGER_FUNCTION_IMPORT =
	    "osyl.manager.import";
    public final static String OSYL_MANAGER_FUNCTION_CREATE =
	    "osyl.manager.create";
    public final static String OSYL_MANAGER_FUNCTION_COPY = "osyl.manager.copy";
    public final static String OSYL_MANAGER_FUNCTION_EXPORT =
	    "osyl.manager.export";
    public final static String OSYL_MANAGER_FUNCTION_DELETE =
	    "osyl.manager.delete";
    public final static String OSYL_MANAGER_FUNCTION_ATTACH =
	    "osyl.manager.attach";
    public final static String OSYL_MANAGER_FUNCTION_ASSOCIATE =
	    "osyl.manager.associate";

    public final static List<String> OSYL_MANAGER_PERMISSIONS = Arrays.asList(new String[] {
	    OSYL_MANAGER_FUNCTION_IMPORT, OSYL_MANAGER_FUNCTION_CREATE,
	    OSYL_MANAGER_FUNCTION_COPY, OSYL_MANAGER_FUNCTION_EXPORT,
	    OSYL_MANAGER_FUNCTION_DELETE, OSYL_MANAGER_FUNCTION_ATTACH,
	    OSYL_MANAGER_FUNCTION_ASSOCIATE });

    /** the OsylEditor fonctions **/
    public final static String OSYL_FUNCTION_VIEW_STUDENT = "osyl.view.student";
    public final static String OSYL_FUNCTION_VIEW_COMMUNITY =
	    "osyl.view.community";
    public final static String OSYL_FUNCTION_VIEW_PUBLIC = "osyl.view.public";
    public final static String OSYL_FUNCTION_READ = "osyl.read";
    public final static String OSYL_FUNCTION_EDIT = "osyl.edit";
    public final static String OSYL_FUNCTION_PUBLISH = "osyl.publish";



}
