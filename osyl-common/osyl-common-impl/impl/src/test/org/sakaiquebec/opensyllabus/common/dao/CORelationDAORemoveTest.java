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
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class CORelationDAORemoveTest extends AbstractRelationDAOTest {

    
    public void testRemoveNull() throws Exception{
	getCoRelationDao().createRelation("0", "1");
	try {
	    getCoRelationDao().removeRelation(null, null);
	    fail("Should not be able to remove a relation with null ids.");
	} catch (NullPointerException e) {
	    fail("Should not receive a NullPointerException: "
		    + e.getLocalizedMessage());
	} catch (Exception e) {
	    // fine
	}
    }
    
    public void testRemoveNullChildId() throws Exception{
	getCoRelationDao().createRelation("0", "1");
	try {
	    getCoRelationDao().removeRelation("0", null);
	    fail("Should not be able to remove a relation with null child id.");
	} catch (NullPointerException e) {
	    fail("Should not receive a NullPointerException: "
		    + e.getLocalizedMessage());
	} catch (Exception e) {
	    // fine
	}
    }
    
    public void testRemoveNullParentId() throws Exception{
	getCoRelationDao().createRelation("0", "1");
	try {
	    getCoRelationDao().removeRelation(null, "1");
	    fail("Should not be able to remove a relation with null parent id.");
	} catch (NullPointerException e) {
	    fail("Should not receive a NullPointerException: "
		    + e.getLocalizedMessage());
	} catch (Exception e) {
	    // fine
	}
    }
    
    public void testRemoveExistingRelation() throws Exception {
	try{
	    getCoRelationDao().createRelation("0", "1");
	    getCoRelationDao().removeRelation("0", "1");
	}catch (Exception e) {
	    fail("Should not generate an exception when remove relation");
	}
    }
    
    public void testRemoveNonExistingRelation() throws Exception {
	try{
	    getCoRelationDao().createRelation("0", "1");
	    getCoRelationDao().removeRelation("0", "2");
	    fail("Should generate an exception when remove unexisting relation");
	}catch (Exception e) {
	    //fine
	}
    }
}

