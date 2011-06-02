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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.portal.client.rpc;

import java.util.List;

import org.sakaiquebec.opensyllabus.shared.model.CODirectorySite;
import org.sakaiquebec.opensyllabus.shared.model.COSite;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * OsylAdminGwtService defines the RPC (Remote Procedure Call) interface between
 * the GWT client application and the Sakai server.
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public interface OsylPortalGwtService extends RemoteService {

    public List<CODirectorySite> getCoursesForAcadCareer(String acadCareer);

    public List<CODirectorySite> getCoursesForResponsible(String responsible);

    public String getDescription(String siteId);

}