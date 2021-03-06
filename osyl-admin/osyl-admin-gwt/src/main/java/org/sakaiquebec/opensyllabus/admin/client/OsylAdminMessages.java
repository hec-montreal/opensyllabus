/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.admin.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Interface to represent the messages contained in resource  bundle:
 *
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */
public interface OsylAdminMessages extends Messages {
    
     String OsylAdminCreateButton();
     
     String OsylAdminSiteRadioButton();

     String OsylAdminUsersRadioButton();
     
     String OsylAdminDialogSuccess();
     
     String OsylAdminDialogFailure();
     
     String OsylAdminMainPanelChoiceLabel();

     String OsylAdminDialogCreationTitle();
}

