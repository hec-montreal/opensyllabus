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
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.manager.impl.cms;

import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 *
 *Participant a site access roles
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class Participant {
	public String name = null;

	// Note: uniqname is really a user ID
	public String uniqname = null;

	public String role = null;

	/** role from provider */
	public String providerRole = null;

	/** The member credits */
	public String credits = null;

	/** The section */
	public String section = null;

	/** The regestration id */
	public String regId = null;

	/** removeable if not from provider */
	public boolean removeable = true;

	public String getName() {
		return name;
	}

	public String getUniqname() {
		return uniqname;
	}

	public String getRole() {
		return role;
	} // cast to Role

	public String getProviderRole() {
		return providerRole;
	}

	public boolean isRemoveable() {
		return removeable;
	}

	// extra info from provider
	public String getCredits() {
		return credits;
	} // getCredits

	public String getSection() {
		return section;
	} // getSection

	public String getRegId() {
		return regId;
	} // getRegId

	/**
	 * Access the user eid, if we can find it - fall back to the id if not.
	 * 
	 * @return The user eid.
	 */
	public String getEid() {
		try {
			return UserDirectoryService.getUserEid(uniqname);
		} catch (UserNotDefinedException e) {
			return uniqname;
		}
	}

	/**
	 * Access the user display id, if we can find it - fall back to the id
	 * if not.
	 * 
	 * @return The user display id.
	 */
	public String getDisplayId() {
		try {
			User user = UserDirectoryService.getUser(uniqname);
			return user.getDisplayId();
		} catch (UserNotDefinedException e) {
			return uniqname;
		}
	}

} // Participant
