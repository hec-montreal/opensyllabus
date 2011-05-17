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
package org.sakaiquebec.opensyllabus.common.api.portal.publish3;
/**
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface ZCPublisherService {

    public final static String XSLT_DIR_NAME = "xslt";

    public final static String URL_CONN_PUBLIER = "/osyl-editor-sakai-tool/publish3/zcPublier3.jsp";

    public final static String URL_CONN_DEPUBLIER = "/osyl-editor-sakai-tool/publish3/zcDepublier3.jsp";

    public void publier(String koId, String langue, String nivSec);

    public void depublier(String koId, String langue);
}

