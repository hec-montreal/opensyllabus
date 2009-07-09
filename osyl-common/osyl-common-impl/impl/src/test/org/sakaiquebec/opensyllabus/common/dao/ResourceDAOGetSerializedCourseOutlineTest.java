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
package org.sakaiquebec.opensyllabus.common.dao;

import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ResourceDAOGetSerializedCourseOutlineTest extends
	AbstractResourceDAOTest {

    public void testGetNull() throws Exception {
	ResourceDao resourceDAO = getResourceDAO();
	
	COSerialized course = newCourseOutline("0");
	course.setSecurity("sec");
	resourceDAO.createOrUpdateCourseOutline(course);
	
	try {
	    resourceDAO.getSerializedCourseOutline(null);
	    fail("getSerializedCourseOutline(): expected an exception because of null parameters.");
	} catch (Exception e) {
	    // fine
	}
    }

    public void testGetExistingCourse() throws Exception {
	final String GROUP = "UnitTest";
	ResourceDao resourceDAO = getResourceDAO();

	COSerialized course = newCourseOutline("0");
	course.setSecurity(GROUP);
	resourceDAO.createOrUpdateCourseOutline(course);

	COSerialized otherCourse =
		resourceDAO.getSerializedCourseOutline(course.getSiteId());

	assertEquals(
		"Course retrieved using getSerializedCourseOutline() doesn't match the created one.",
		course, otherCourse);
    }

    public void testGetNonExistingCourse() throws Exception {
	ResourceDao resourceDAO = getResourceDAO();
	try {
	    resourceDAO.getSerializedCourseOutline(""
		    + System.currentTimeMillis());
	    fail("Expected an Exception from 'getSerializedCourseOutline' since the course doesn't exist.");
	} catch (Exception e) {
	    // fine
	}
    }

}
