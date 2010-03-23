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
package org.sakaiquebec.opensyllabus.admin.api;

import java.util.Date;
import java.util.List;

/**
 * This class is used to retrieved informations saved in the config.xml file.
 * The properties in the config.xml file are used for administration purposes
 * such as determine the courses that will be created automatically.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface ConfigurationService {

    public final static String ADMINSITENAME = "opensyllabusAdmin";

    public final static String CONFIGFORLDER = "config";

    public final static String CONFIGFILE = "config.xml";

    public Date getStartDate();

    public Date getEndDate();

    public List<String> getCourses();

}
