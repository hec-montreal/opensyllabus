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

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CORelationDAOGetParentOfCourseOutlineTest extends
	AbstractRelationDAOTest {

    public void testGetNull() throws Exception{
	getCoRelationDao().createRelation("childId", "parentId");
	try {
	    getCoRelationDao().getParentOfCourseOutline(null);
	    fail("Should not be able get relation with null id.");

	} catch (NullPointerException e) {
	    fail("Should not receive a NullPointerException: "
		    + e.getLocalizedMessage());
	} catch (Exception e) {
	    // fine
	}
    }

    public void testGetUnexistingRelation() throws Exception {
	try {
	    getCoRelationDao().getParentOfCourseOutline("unexistingId");
	    fail("Should throw an exception");
	} catch (Exception e) {
	    // fine
	}
    }

    public void testGetExistiongRelation() throws Exception {
	getCoRelationDao().createRelation("0", "1");
	String parentId = getCoRelationDao().getParentOfCourseOutline("0");
	assertEquals("One COConfigSerialized was created.", "1", parentId);
    }

}
