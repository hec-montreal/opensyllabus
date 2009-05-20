/**********************************************************************************
 * $Id: OsylViewModelable.java 1360 2008-10-01 18:39:09Z remi.saias@hec.ca $
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

package org.sakaiquebec.opensyllabus.client.ui.api;

import org.sakaiquebec.opensyllabus.shared.model.COModelInterface;

/**
 * Interface that every viewable part of the OpenSyllabus should implement to
 * interact with the model.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: OsylViewModelable.java 989 2008-06-11 01:56:43Z
 *          sacha.lepretre@crim.ca $
 */
public interface OsylViewModelable {
    /**
     * Returns the {@link COModelInterface} object that is being edited.
     * 
     * @return {@link COModelInterface}
     */
    public COModelInterface getModel();

    /**
     * Sets the {@link COModelInterface} object being edited by this
     * OsylViewable.
     * 
     * @param COModelInterface
     */
    public void setModel(COModelInterface model);
}
