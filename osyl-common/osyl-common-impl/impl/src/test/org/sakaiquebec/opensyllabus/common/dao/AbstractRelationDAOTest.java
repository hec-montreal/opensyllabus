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
public class AbstractRelationDAOTest extends AbstractDAOTest {

    private CORelationDao coRelationDao;

    public CORelationDao getCoRelationDao() {
	return coRelationDao;
    }

    public void setCoRelationDao(CORelationDao coRelationDao) {
	this.coRelationDao = coRelationDao;
    }

    protected static final CORelation newCoRelation(String idChild,
	    String idParent) {
	CORelation coRelation = new CORelation();

	coRelation.setChild(idChild);
	coRelation.setParent(idParent);

	return coRelation;
    }

    protected static final void assertEquals(CORelation cr1, CORelation cr2) {
	assertEquals("Child don't match.", cr1.getChild(), cr2.getChild());

	assertEquals("Parent don't match.", cr1.getParent(), cr2.getParent());
    }
}
