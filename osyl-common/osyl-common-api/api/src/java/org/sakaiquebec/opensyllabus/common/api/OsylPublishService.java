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

import java.util.Map;
import java.util.Vector;

import org.sakaiproject.exception.PermissionException;
import org.sakaiquebec.opensyllabus.shared.exception.FusionException;
import org.sakaiquebec.opensyllabus.shared.exception.PdfGenerationException;
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
     * Prefix of the site realm id.
     */
    public static final String REALM_ID_PREFIX = "/site/";

    /**
     * Returns the unique URL for the published course plan
     * 
     * @return a String that is the published course plan URL
     */
    public COSerialized getSerializedPublishedCourseOutlineForAccessType(
	    String siteId, String accessType, String webappdir);

    /**
     * Publish the actual course plan in the web application directory
     */
    public Vector<Map<String, String>> publish(String webappDir, String siteId,
                                               String nonce)
	    throws Exception, FusionException;

    /**
     * Make an xsl transformation of the specified xml for the specified group
     * 
     * @param xml
     * @param group
     * @return the result of the transformation
     */
    public String transformXmlForGroup(String xml, String group,
	    String webappDir) throws Exception;

    public void createEditionPrintVersion(COSerialized cos, String webappdir)
	    throws Exception;
    
    public void createPublishPrintVersion(String siteId, String webappdir) throws PdfGenerationException;

    public void unpublish(String siteId, String webappDir) throws Exception, PermissionException;
}