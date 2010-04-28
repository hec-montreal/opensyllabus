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

package org.sakaiquebec.opensyllabus.common.api;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2009 The Sakai Foundation, The Sakai Quebec Team.
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
 * This interface contains all the methods used to publish a course outline or
 * to retrieve a published course outline.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylPublishService {

    /**
     * Name of the chs folder in which the course outline content will be
     * stored.
     */
    public static final String WORK_DIRECTORY = "work";

    /**
     * Name of the chs folder in which the course outline content will be
     * published.
     */
    public static final String PUBLISH_DIRECTORY = "publish";
    
    public static final String PRINT_VERSION_FILENAME="osylPrintVersion.pdf";

    /**
     * Returns the unique URL for the published course plan
     * 
     * @return a String that is the published course plan URL
     */
    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String accessType, String webappdir);

    /**
     * Publish the actual course plan in the web application directory
     */
    public void publish(String webappDir, String siteId) throws Exception;

    /**
     * Make an xsl transformation of the specified xml for the specified group
     * 
     * @param xml
     * @param group
     * @return the result of the transformation
     */
    public String transformXmlForGroup(String xml, String group, String webappDir) throws Exception;
}