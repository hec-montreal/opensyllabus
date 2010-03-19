package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import java.util.Date;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.SectionCategory;

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

    /**
     * Method used to create the sessions
     */
    public void loadSessions();

    /**
     * Method used to load course sets
     */
    public void loadCourseSets();

    /**
     * Method used to load the courses
     */
    public void loadCourses();

    /**
     * This method is used to load teachers, secretaries, interns .... For now
     * it is used just for teachers and secretaries. Each secretary will be
     * automatically added to all the courses of the service she is associated
     * to;
     */
    public void loadMembership();

    /**
     * This method is used to assign students to their given course section
     */
    public void loadEnrollments();

    /**
     * This method is used to automatically register mid-term and final exams.
     * It can also be used for any prescheduled meeting
     */
    public void loadMeetings();


}
