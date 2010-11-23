package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import java.util.List;

import org.quartz.Job;

import java.util.Arrays;

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
 * This job adds a new role with the correct permissions to all the sites and
 * the realm of type course . All the sites will be checked to make sure the
 * role has at least the specified permissions.
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface RolesSynchronizationJob extends Job {

   /**
     * Id of the users that will be add
     */
    public final static String SITE_TYPE = "course";

    public final static String REALM_PREFIX = "/site/";

    public final static String TEMPLATE_ID = "!site.template.course";

}
