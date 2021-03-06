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
package org.sakaiquebec.opensyllabus.common.helper;

import org.sakaiquebec.opensyllabus.common.model.COModeledServer;
import org.sakaiquebec.opensyllabus.shared.exception.CompatibilityException;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class ModelHelper {

    public static void createAssociationInXML(COSerialized co,
	    COModeledServer coModelParent) throws CompatibilityException {
	COModeledServer coModelChild = new COModeledServer(co);
	coModelChild.XML2Model();
	coModelChild.associate(coModelParent);
	coModelChild.model2XML();
	co.setContent(coModelChild.getSerializedContent());
    }
}
