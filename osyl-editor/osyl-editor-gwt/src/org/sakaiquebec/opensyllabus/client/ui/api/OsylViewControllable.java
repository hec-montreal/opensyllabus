/**********************************************************************************
 * $Id: OsylViewControllable.java 1360 2008-10-01 18:39:09Z remi.saias@hec.ca $
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

import org.sakaiquebec.opensyllabus.client.controller.OsylController;

/**
 * Interface that every viewable part of the OpenSyllabus should implement
 * to interact with the OsylController.
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Leprêtre</a>
 * @version $Id: OsylViewControllable.java 1360 2008-10-01 18:39:09Z remi.saias@hec.ca $
 */
public interface OsylViewControllable {
    /**
     * Returns the {@link OsylController} instance which can be used to trigger
     * actions in the editor like changing context, saving the course outline,
     * etc.
     *
     * @return {@link OsylController}
     */
    public OsylController getController();

    /**
     * Sets the {@link OsylController} instance. It is necessary to provide a valid
     * {@link OsylController} instance when initializing an OsylViewable.
     *
     * @param OsylController
     */
    public void setController(OsylController osylController);

}
