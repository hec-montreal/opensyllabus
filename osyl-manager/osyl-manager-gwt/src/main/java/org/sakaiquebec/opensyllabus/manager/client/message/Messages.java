/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.client.message;

import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * Implements the message interface used by Osyl manager classes to retrieve the
 * messages to show in the GUI.
 * @author <a href="mailto:Laurent.Danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface Messages extends ConstantsWithLookup {

    String courseName();

    String file();

    String create();

    String siteCreated();

    String siteNotCreated();

    String importXML();

    String siteIdUnable();

    String createSiteTitle();

    String importCOTitle();

    String exportCOTitle();

    String exportCO();

    String osylSitesList();

    String optionsTitle();

    String createOption();

    String exportOption();

    String valid();

    String unableToExportCO();

    String associateDissociate();

    String associateDissociateCM();

    String attach();

    String associateToCM();

    String associateToCMSuccess();

    String associateToCMFailure();

    String associateToCMChooseSite();

    String associateToCMChooseCourse();

    String unexistingCMcourse();

    String dissociate();

    String noAssociableCOSite();

    String chooseConfig();

    String chooseLang();

    String siteNameNotValid();

    String noConfig();

    String config_default();

    String config_udem();

    String config_announcementsRegulationsHEC();

    String language_en();

    String language_es();

    String language_fr_CA();

    String infoView_associatedCourse();

    String infoView_label();

    String infoView_lastPublished();

    String infoView_lastSave();

    String infoView_parentSite();

    String mainView_label();

    String mainView_creationOfNewSite();
    
    String mainView_operationsOnExistingSites();
    
    String mainView_searchForExistingSites();
    
    String mainView_searchForExistingSites_input();

    String mainView_or();

    String mainView_action_createSite();

    String mainView_action_importSite();

    String mainView_action_edit();

    String mainView_action_associate();

    String mainView_action_attach();

    String mainView_action_clean();

    String mainView_action_copy();

    String mainView_action_delete();

    String mainView_action_dissociate();

    String mainView_action_export();

    String mainView_action_publish();
    
    String mainView_action_search();

    String mainView_action_unattach();

    String mainView_action_unpublish();

    String mainView_action_import();

    String mainView_tabs_all();

    String rpcFailure();

    String noCOSite();

    String siteForm_create_ok();

    String importForm_import_ok();

    String form_close();

    String courseListView_loading();

    String createForm_edit();

    String select_parent_site();

    String attachForm_attach_ok();

    String attachForm_attach_error();

    String attachForm_attach_error_detail();

    String unattachAction_unattach_error();

    String unattachAction_unattach_error_detail();
    
    String unattachAction_unattach_ok();

    String cminfoView_label();

    String cminfoView_name();

    String cminfoView_session();

    String cminfoView_instructor();

    String cminfoView_studentNumber();

    String associateForm_instruction();
    
    String associateForm_courseIdentifier();

    String associateForm_courseName();

    String associateForm_ok();

    String associateForm_cancel();
    
    String dissociateAction_dissociate_error();
    
    String dissociateAction_dissociate_error_detail();
    
    String dissociateAction_dissociate_ok();

    String associateForm_confirmation();

    String cminfoView_section();

    String publishAction_publish_error();

    String publishAction_publish_error_detail();

    String publishAction_publish_ok();
    
    String CourseListView_scSiteList_col0();
    String CourseListView_scSiteList_col1();
    String CourseListView_scSiteList_col2();
    String CourseListView_scSiteList_col3();
    String CourseListView_scSiteList_col4();
    String CourseListView_scSiteList_col5();
    String CourseListView_scSiteList_col6();
    String CourseListView_scSiteList_col7();
    
    String OsylAlertDialog_Title();
    String OsylAlertDialog_Ok_Button();
    
    String OsylOKCancelDialog_Ok_Button();
    String OsylOKCancelDialog_Cancel_Button();
    String OsylOkCancelDialog_Title();
    
    String OsylCancelDialog_Cancel_Button();
    String OsylCancelDialog_Title();
    String OsylCancelDialog_Content();
    
    String OsylWarning_Title();

    String associateForm_search();

    String deleteAction_delete_error();
    String deleteAction_delete_ok();
    String deleteAction_delete_error_detail();

    String deleteAction_delete_confirmation();
    String deleteAction_delete_siteHasChild();
}
