/**********************************************************************************
 * $Id: ClientSerializablePojo.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Québec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.shared.api;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An interface for serialization, (in order to have non coupled POJO from GWT source)
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: SerializablePojo.java 661 2008-05-28 15:24:39Z sacha.lepretre@crim.ca $
 */
public interface ClientSerializablePojo extends IsSerializable {

}
