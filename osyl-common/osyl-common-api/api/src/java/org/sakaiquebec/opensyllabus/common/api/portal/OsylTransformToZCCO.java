//FILE HEC ONLY SAKAI-2723

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
package org.sakaiquebec.opensyllabus.common.api.portal;

import java.io.IOException;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * This class takes the public course outline created at the publishing of a
 * OpenSyllabus course outline related to a section of the course management and
 * transforms it into a ZoneCours course outline XML compatible. The new course
 * outline it deposited in the ZoneCours database.
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylTransformToZCCO {

    public final static String ANNUAIRE_KOID_PREFIX = "a-";

    public final static String PUBLISH_COLL_PREFIX = "/group/";


    public final static String SUMMER = "E";

    public final static String WINTER = "H";

    public final static String FALL = "A";

    public static final String SITE_PREFIX = "/site/";

    /**
     * Creates a XML course outline ZoneCours compatible and sends it to the
     * ZoneCours database.
     *
     * @param documentSecurityMap TODO
     * @param documentVisibilityMap TODO
     * @return
     * @throws IOException
     * @throws Exception
     */
    public boolean sendXmlAndDoc(COSerialized published, String access) throws IOException,
	    Exception;

    public void unpublish(String siteId, String lang);

}
