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
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ResourceDAOHasBeenPublishedTest extends AbstractResourceDAOTest {
    
    public void testHasBeenPublishedNull() throws Exception {
	ResourceDao resourceDAO = getResourceDAO();
	try {
	    resourceDAO.hasBeenPublished(null);
	    fail("hasBeenPublished(): expected an exception because of null parameters.");
	} catch (Exception e) {
	    // fine
	}
    }

    public void testHasBeenPublishedUnpublishedOne() throws Exception {
	final String GROUP = "UnitTest";
	ResourceDao resourceDAO = getResourceDAO();

	COSerialized course = newCourseOutline("0");
	course.setAccess(GROUP);
	course.setPublished(false);
	resourceDAO.createOrUpdateCourseOutline(course);

	boolean published =
		resourceDAO.hasBeenPublished(course.getSiteId());

	assertEquals(
		"boolean published retrieved using hasBeenPublished() doesn't match the created one.",
		course.isPublished(), published);
    }
    
    public void testHasBeenPublishedPublishedOne() throws Exception {
	final String GROUP = "UnitTest";
	ResourceDao resourceDAO = getResourceDAO();

	COSerialized course = newCourseOutline("0");
	course.setAccess(GROUP);
	course.setPublished(true);
	resourceDAO.createOrUpdateCourseOutline(course);

	boolean published =
		resourceDAO.hasBeenPublished(course.getSiteId());

	assertEquals(
		"boolean published retrieved using hasBeenPublished() doesn't match the created one.",
		course.isPublished(), published);
    }

    public void testHasBeenPublishedNonExistingCourse() throws Exception {
	ResourceDao resourceDAO = getResourceDAO();
	try {
	    resourceDAO.hasBeenPublished(""
		    + System.currentTimeMillis());
	    fail("Expected an Exception from 'hasBeenPublished' since the course doesn't exist.");
	} catch (Exception e) {
	    // fine
	}
    }
}

