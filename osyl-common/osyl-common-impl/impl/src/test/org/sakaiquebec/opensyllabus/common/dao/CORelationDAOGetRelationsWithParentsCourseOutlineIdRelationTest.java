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

import java.util.List;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CORelationDAOGetRelationsWithParentsCourseOutlineIdRelationTest
	extends AbstractRelationDAOTest {

    public void testGetNull() throws Exception{
	getCoRelationDao().createRelation("childId", "parentId");
	try {
	    getCoRelationDao().getCourseOutlineChildren(null);
	    fail("Should not be able get relation with null id.");

	} catch (NullPointerException e) {
	    fail("Should not receive a NullPointerException: "
		    + e.getLocalizedMessage());
	} catch (Exception e) {
	    // fine
	}
    }

    public void testGetNoRelations() throws Exception {
	List<CORelation> relations =
		getCoRelationDao().getCourseOutlineChildren(
			"unexistingId");

	// We've not added any data yet
	assertEquals("getRelationsWithParentsCourseOutlineId() returned "
		+ relations.size() + " configs.", 0, relations.size());
    }

    public void testGetOneRelation() throws Exception {
	getCoRelationDao().createRelation("0", "1");
	List<CORelation> configs =
		getCoRelationDao().getCourseOutlineChildren("1");
	assertEquals("One COConfigSerialized was created.", 1, configs.size());
    }

    public void testGetManyRelations() throws Exception {
	List<CORelation> configs;
	;

	// a little overkill...
	final int SIZE = 10;
	for (int i = 0; i < SIZE; i++) {
	    getCoRelationDao().createRelation("" + i, "parentId");
	}
	configs =
		getCoRelationDao().getCourseOutlineChildren(
			"parentId");
	assertEquals("Invalid number of configs returned by getConfigs().",
		SIZE, configs.size());

	// still works if I delete one?
	getCoRelationDao().removeRelation("0", "parentId");
	configs =
		getCoRelationDao().getCourseOutlineChildren(
			"parentId");
	assertEquals("Invalid number of configs returned by getConfigs().",
		SIZE - 1, configs.size());
    }
}
