package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import org.quartz.Job;

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
 *****************************************************************************/

/**
 * This class contains all methods needed to connect our institutional system to
 * the course management of sakai, we use to provide users, courses, sessions,
 * teachers. This class can be used to implement the methods to provide the job
 * used to populate tables and a service to request or send information to the
 * course management.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylCMJob extends Job {
	
	public final static String SESSION_FILE = "session.dat";
	
	public final static String COURS_FILE = "cours.dat";
	
	public final static String ETUDIANT_FILE = "etudiant_cours3.dat";
	
	public final static String HORAIRES_FILE = "horaires_cours.dat";
	
	public final static String PROF_FILE = "prof_cours3.dat";
	
	public final static String SECRETAIRES_FILE = "secretaires_serv_ens.dat";
	
	public final static String SERV_ENS_FILE = "service_enseignement.dat";
	
	public final static String PROG_ETUD_FILE = "programme_etudes.dat";

    public final static String EXTRACTS_PATH_CONFIG_KEY =
	    "coursemanagement.extract.files.path";

    public final static String SECRETARY_ROLE = "SEC";

    public final static String ACTIVE_STATUS = "active";

    public final static String COORDONNATEUR_ROLE = "C";

    /**
     * Value used to represent the credits associated to this course. This is a
     * default value.
     */
    // TODO: Implement correct retrieval of the credits associated to the
    // course.
    public final static String CREDITS = "3";

    /**
     * Grading process
     */
    public final static String GRADING_SCHEME = "Letter";

    /**
     * Status of all the students currently enrolled and active
     */
    public final static String ENROLLMENT_STATUS = "enrolled";

    /**
     * Mapping to the type of site asociated to this course in Sakai.
     */
    public final static String COURSE_OFF_STATUS = "course";
    
    /**
     * Special section associated to sharable sites.
     */
    public final static String SHARABLE_SECTION = "00";

    /**
     * Le programme du certificat
     */
    public final static String CERTIFICAT = "CERT";

}
