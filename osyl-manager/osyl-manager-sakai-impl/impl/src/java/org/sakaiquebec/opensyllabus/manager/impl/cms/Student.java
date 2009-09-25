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
/**
 * Student that will be used in the roster
 * 
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
/**
 * 
 * 
 */
public class Student {
	public String name = null;

	public String uniqname = null;

	public String id = null;

	public String level = null;

	public String credits = null;

	public String role = null;

	public String course = null;

	public String section = null;

	public String getName() {
		return name;
	}

	public String getUniqname() {
		return uniqname;
	}

	public String getId() {
		return id;
	}

	public String getLevel() {
		return level;
	}

	public String getCredits() {
		return credits;
	}

	public String getRole() {
		return role;
	}

	public String getCourse() {
		return course;
	}

	public String getSection() {
		return section;
	}

} // Student

