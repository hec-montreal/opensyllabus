/**********************************************************************************
 * $Id: OsylTestUIMessages.java 1822 2009-01-27 16:20:46Z laurent.danet@hec.ca $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
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

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util;

import java.util.HashMap;
import java.util.Map;

/**
 * UI Messages map for hosted mode (used in development only). This map is
 * needed because the application cannot get the message bundle from the server
 * when used in hosted (disconnected) mode.<br/>
 * <br/>
 * ATTENTION: Any message added to the default message bundle should be added
 * here!
 * 
 * @author <a href="mailto:sacha.lepretre@crim.ca">Sacha Lepretre</a>
 * @version $Id: OsylTestUIMessages.java 1046 2008-06-11 21:15:46Z
 *          claude.coulombe@umontreal.ca $
 */
public class OsylTestUIMessages {

    private static Map<String, String> messages;

    /**
     * @return the messages map
     */
    public static Map<String, String> getMap() {
	if (messages == null) {
	    messages = new HashMap<String, String>();

	    messages.put("Global.error", "Error");
	    messages.put("Global.ok", "OK");
	    messages.put("Global.cancel", "Cancel");
	    messages.put("Global.warning", "Warning");
	    messages.put("Global.yes", "Yes");
	    messages.put("Global.no", "No");

	    // Toolbar texts
	    messages.put("ButtonAddToolBar", "Add...");
	    messages.put("ButtonHomeToolBar", "Home");
	    messages.put("ButtonImportToolBar", "Documents"); 
	    messages.put("ButtonPrintToolBar", "Print");
	    messages.put("ButtonPublishToolBar", "Publish");
	    messages.put("ButtonSaveToolBar", "Save ");
	    messages.put("ButtonViewToolBar", "Preview...");
	    messages.put("ButtonCloseToolBar", "Close");

	    // Toolbar tooltips
	    messages.put("ButtonAddToolBarTooltip",
		    "Add an Element to the Current View...");
	    messages.put("ButtonHomeToolBarTooltip",
		    "Return to the Main Home View...");
	    messages.put("ButtonImportToolBarTooltip", "Add a Document...");
	    messages
		    .put("ButtonPrintToolBarTooltip", "Print Course Outline...");
	    messages.put("ButtonPublishToolBarTooltip",
		    "Publish Course Outline...");
	    messages.put("ButtonSaveToolBarTooltip", "Save Course Outline...");
	    messages.put("ButtonViewToolBarTooltip", "View Course outline...");
	    messages
		    .put("ButtonCloseToolBarTooltip", "Close Preview Window...");

	    messages.put("OsylToolbar", "OpenSyllabus Toolbar");

	    messages.put("OsylTreeView", "OpenSyllabus Tree View");

	    messages.put("OsylWorkSpaceView", "OpenSyllabus Work Space View");

	    messages.put("RpcError", "*** RPC Error ***");

	    messages.put("RpcSuccess", "*** RPC success ***");

	    messages.put("Welcome", "Welcome!  The current time is {0}.");

	    messages.put("cancel", "Cancel");

	    messages.put("delete", "Delete");

	    messages.put("edit", "Edit");

	    messages.put("follow", "Follow the link");

	    messages.put("addButton", "Add");

	    messages.put("upButton", "Up");

	    messages.put("addFileButton", "Add File...");

	    messages.put("UtilityRemoteFileBrowser_directoryTag", "(D)");

	    messages.put("UtilityRemoteFileBrowser_fileTag", "(F)");

	    messages.put("UtilityRemoteFileBrowser_citationTag", "(C)");

	    messages.put("enterImgURL", "Enter an Image URL:");

	    messages.put("enterLinkURL", "Enter a Link URL:");

	    messages.put("fileUpload.addResource", "Add a Resource");

	    messages.put("fileUpload.selectDest", "Select Destination Folder:");

	    messages.put("fileUpload.plsSelectFile", "Please select a file!");

	    messages.put("fileUpload.resNotSaved",
		    "The resource could not be saved!");

	    messages.put("fileUpload.resSaved", "Resource Saved");

	    messages
		    .put("fileUpload.unableAddRes",
			    "Unable to add resource to current context!\n\nDetails: {0}");

	    messages.put("fileUpload.unableReadRemoteDir",
		    "Unable to read remote directory content.");

	    messages.put("FormatingToolbar", "Formatting");

	    messages
		    .put("fileUpload.unableToGetSiteID",
			    "Error: Unable to get site ID. Resource Upload won't work.");

	    messages.put("courseOutlinePublication",
		    "Course Outline Publication");

	    messages.put("publish", "Publish");

	    messages.put("publishing", "Please wait: Publishing...");

	    messages.put("publishedVersions",
		    "Published Versions of this Course Outline:");

	    messages.put("close", "Close");

	    messages.put("publishOK",
		    "The course outline was published successfully.");

	    messages.put("publishError",
		    "Unable to publish the course outline.");

	    messages.put("resourceEditor", "Resource Details");

	    messages.put("resourceEditorTabCfg",
	    "Configuration");

	    messages.put("resourceEditorTabCfg", "Configuration");

	    messages.put("resourceEditorTabDoc", "Attachment");

	    messages.put("rtt_background", "Background");

	    messages.put("rtt_black", "Black");

	    messages.put("rtt_blue", "Blue");

	    messages.put("rtt_bold", "Toggle Bold");

	    messages.put("rtt_cancel", "Cancel");

	    messages.put("rtt_color", "Color");

	    messages.put("rtt_createLink", "Create Link");

	    messages.put("rtt_font", "Font");

	    messages.put("rtt_foreground", "Foreground");

	    messages.put("rtt_green", "Green");

	    messages.put("rtt_hr", "Insert Horizontal Rule");

	    messages.put("rtt_indent", "Indent Right");

	    messages.put("rtt_insertImage", "Insert Image");

	    messages.put("rtt_italic", "Toggle Italic");

	    messages.put("rtt_justifyCenter", "Center");

	    messages.put("rtt_justifyLeft", "Left Justify");

	    messages.put("rtt_justifyRight", "Right Justify");

	    messages.put("rtt_large", "Large");

	    messages.put("rtt_medium", "Medium");

	    messages.put("rtt_normal", "Normal");

	    messages.put("rtt_ol", "Insert Ordered List");

	    messages.put("rtt_outdent", "Indent Left");

	    messages.put("rtt_red", "Red");

	    messages.put("rtt_removeFormat", "Remove Formatting");

	    messages.put("rtt_removeLink", "Remove Link");

	    messages.put("rtt_size", "Size");

	    messages.put("rtt_small", "Small");

	    messages.put("rtt_strikeThrough", "Toggle Strikethrough");

	    messages.put("rtt_subscript", "Toggle Subscript");

	    messages.put("rtt_superscript", "Toggle Superscript");

	    messages.put("rtt_ul", "Insert Unordered List");

	    messages.put("rtt_underline", "Toggle Underline");

	    messages.put("rtt_validate", "Valid");

	    messages.put("rtt_white", "White");

	    messages.put("rtt_xlarge", "X-Large");

	    messages.put("rtt_xsmall", "X-Small");

	    messages.put("rtt_xxlarge", "XX-Large");

	    messages.put("rtt_xxsmall", "XX-Small");

	    messages.put("rtt_yellow", "Yellow");

	    messages.put("save", "Save");

	    messages.put("savedOK", "Saved Successfully");

	    messages.put("selectRubric", "<select a rubric>");
	    
	    messages.put("unableToInitServer",
		    "Unable to connect to server.\r\n\r\nDetails:\r\n{0}");

	    messages
		    .put(
			    "unableToLoadCO",
			    "Unable to load course outline from server. Using default content for development.\r\n\r\nDetails:\r\n{0}");

	    messages.put("ResDocumentConfigView_Welcome", "Select a Resource");

	    messages.put("ResDocumentConfigView_Display_Label", "Display");

	    messages.put("ResDocumentConfigView_Hide_CheckBox", "Hide");

	    messages
		    .put("ResDocumentConfigView_Importance_Label", "Importance");

	    messages.put("ResDocumentConfigView_NotImportant_CheckBox",
		    "Not Important");

	    messages.put("ResDocumentConfigView_Diffusion_Label",
		    "Diffusion Level");

	    messages.put("ResDocumentConfigView_Public_RadioButton", "Public");

	    messages.put("ResDocumentConfigView_Institution_RadioButton",
		    "Institution");

	    messages.put("ResDocumentConfigView_Attendee_RadioButton",
		    "Attendee");

	    messages.put("UtilityRemoteFileBrowser_TitleLabel",
		    "Remote File Browser");

	    messages.put("UtilityRemoteFileBrowser_FolderPanelLabel",
		    "Look in Folder: ");

	    messages
		    .put("UtilityRemoteFileBrowser_FileListingLabel", "Files: ");

	    messages.put("UtilityRemoteFileBrowser_LoadingLabel", "Loading...");

	    messages.put("UtilityRemoteFileBrowser_Up_Button", "Upload");

	    messages.put("UtilityRemoteFileBrowser_Choose_Button", "Select");

	    messages.put("UtilityRemoteFileBrowser_Selected_File",
		    "Selected File: ");

	    messages.put("UtilityRemoteFileBrowser_Selected_File_None", "None");

	    messages.put("ResDocumentConfigView_AssignmentCloseDate_TabLabel",
		    "Due Date");

	    messages.put("ResDocumentConfigView_AssignmentOpenDate_TabLabel",
		    "Open Date");

	    messages.put("ResDocumentConfigView_AssignmentRating_Label",
		    "Rating");

	    messages.put("ResProxAssignmentView_AttachDocLabel",
		    "Attached Document");

	    messages.put("ResProxAssignmentView_CloseDateLabel", "Close Date");

	    messages.put("ResProxAssignmentView_OpenDateLabel", "Open Date");

	    messages.put("OsylOKCancelDialog_Ok_Button", "OK");

	    messages.put("OsylOKCancelDialog_Cancel_Button", "Cancel");

	    messages.put("OsylOkCancelDialog_Delete_Title", "Warning!");

	    messages.put("OsylOkCancelDialog_Title", "Warning!");

	    messages.put("OsylOkCancelDialog_Delete_Content",
		    "Are you sure you want to delete this item?");

	    messages.put("OsylOkCancelDialog_Delete_Ok_Button", "Yes");

	    messages.put("OsylOkCancelDialog_Delete_Cancel_Button", "No");

	    messages.put("OsylAlertDialog_Ok_Button", "OK");

	    messages.put("OsylAlertDialog_Title", "Alert");

	    messages.put("saveBeforeQuit", "Do you want to save before quit?");

	    messages.put("RemovedContentUnit", "Removed: '{0}'");

	    messages.put("Preview.attendee_version", "Attendee Version");

	    messages.put("Preview.public_version", "Public Version");

	    messages.put("MetaInfo.audience", "Diffusion Level");

	    messages.put("MetaInfo.audience.attendee", "Attendee");

	    messages.put("MetaInfo.audience.onsite", "Institution");

	    messages.put("MetaInfo.audience.public", "Public");

	    messages.put("MetaInfo.audience.title",
		    "Select Audience of this Element");

	    messages.put("MetaInfo.hidden", "Hidden");

	    messages.put("MetaInfo.hide", "Hide");

	    messages
		    .put("MetaInfo.hide.title",
			    "Check to Hide the Element (until you have complete it, for example)");

	    messages.put("MetaInfo.important", "Important");

	    messages.put("MetaInfo.important.title",
		    "Check to Highlight the Element");

	    messages.put("MetaInfo.title", "Options");

	    messages.put("EditorPopUp.options.rubric", "Rubric");

	    messages
		    .put("EditorPopUp.options.rubric.choose", "Choose a Rubric");

	    messages.put("EditorPopUp.options.rubric.choose.title",
		    "Choose the Rubric in which the Element Should Appear");

	    messages.put("EditorPopUp.title", "Edition");

	    messages.put("MetaInfo.requirement", "Requirement Level");
	    // we don't want to display text by default for the requirement
	    // level
	    messages.put("MetaInfo.requirement.undefined", " ");
	    messages.put("MetaInfo.requirement.mandatory", "Mandatory");
	    messages.put("MetaInfo.requirement.recommended", "Recommended");
	    messages.put("MetaInfo.requirement.complementary", "Complementary");
	    messages.put("MetaInfo.requirement.title",
		    "Select Requirement Level of this Element");

	    messages.put("MetaInfo.library", "Library");

	    messages.put("MetaInfo.library.title",
		    "Check if the Reference had been Reserved at the Library");

	    messages.put("MetaInfo.bookstore", "Bookstore");

	    messages
		    .put("MetaInfo.bookstore.title",
			    "Check if the Reference is Available to the Institution Bookstore");

	    messages.put("MetaInfo.citation.exception",
		    "The citation can not be empty!");

	    messages.put("clickable_text", "Clickable Text");
	    messages.put("description", "Description");
	    messages.put("comment", "Comment");
	    messages.put("DocumentEditor.title.add", "Add a Document");
	    messages.put("DocumentEditor.title.edit", "Edit a Document");
	    messages.put("DocumentEditor.document.details",
		    "Show Document Details...");
	    messages.put("DocumentEditor.document.selection",
		    "1. Select a Document");
	    messages.put("DocumentEditor.document.description",
		    "Description of the Document");
	    messages.put("DocumentEditor.document.license", "License");
	    messages.put("DocumentEditor.save.name", "Save");
	    messages.put("DocumentEditor.save.title", "Save Modifications");
	    messages.put("DocumentEditor.save.error.documentUndefined","You must choose a document");

	    messages.put("DocumentEditor.context", "2. Use Context");

	    messages.put("DocumentEditor.document.PropUpdateError",
		    "Document properties update failed.");
	    messages.put("DocumentEditor.document.PropUpdateSuccess",
		    "Document properties update succeeded.");

	    messages.put("CitationEditor.title.add", "Add a Citation");
	    messages.put("CitationEditor.title.edit", "Edit a Citation");
	    messages.put("CitationEditor.document.details",
		    "Show Citation Details...");
	    messages.put("CitationEditor.document.selection",
		    "1. Select a Citation");
	    messages.put("CitationEditor.document.description",
		    "Description of the Citation");
	    messages.put("CitationEditor.document.license", "License");
	    messages.put("CitationEditor.save.name", "Save");
	    messages.put("CitationEditor.save.title", "Save Modifications");
	    messages.put("CitationEditor.save.error.citationUndefined","You must choose a citation");

	    messages.put("CitationEditor.context", "2. Use Context");

	    messages.put("CitationEditor.document.PropUpdateError",
		    "Citation properties update failed.");
	    messages.put("CitationEditor.document.PropUpdateSuccess",
		    "Citation properties update succeeded.");
	    
	    messages.put("Evaluation.EndDate","End Date");
	    messages.put("Evaluation.EndDate.tooltip"," The date at whitch the assessment is due. \r\nFormat: yyyy-mm-dd"); 
	    messages.put("Evaluation.StartDate"," Start Date");
	    messages.put("Evaluation.StartDate.tooltip"," Date at which the assessment is available for start. \r\nFormat: yyyy-mm-dd"); 
	    messages.put("Evaluation.deliverable"," Deliverable");
	    messages.put("Evaluation.deliverable.tooltip "," The tangible and measurable result produced by the end of assessment execution by students. E.g.: Report, Portfolio, Presentation. \r\nNote: this field is not restricted. Many deliverables could be used for the same assessment.");
	    messages.put("Evaluation.location"," Location");
	    messages.put("Evaluation.location.tooltip"," Where to do the assessment. e.g.: home, in class");
	    messages.put("Evaluation.mode"," Work Mode");
	    messages.put("Evaluation.mode.tooltip"," Work mode used by students to complete the assessment. E.g.: individual, team");
	    messages.put("Evaluation.name"," Assessment Name");
	    messages.put("Evaluation.name.tooltip"," An assessment can be given a personnalized name chosen by the instructor. This name could also coorespond to the assessment type.");
	    messages.put("Evaluation.rating"," Weight");
	    messages.put("Evaluation.rating.tooltip"," Points or percentage allocated to the assessment according to the rating system used by the institution. E.g.: 10%");
	    messages.put("Evaluation.scope"," Requirement Level");
	    messages.put("Evaluation.scope.tooltip"," The requirement level of the assessment. Allows to define whitch assessments students must do in the course. E.g.: Mandatory, Facultative");
	    messages.put("Evaluation.subtype"," Submission Mode");
	    messages.put("Evaluation.subtype.tooltip"," The way used by the student to render the work done for the assessment. E.g.: Paper, Electronic, Oral");
	    messages.put("Evaluation.type"," Assessment Type");
	    messages.put("Evaluation.type.tooltip"," Different types of assessment exist. This field allows the user to categorize the assessment. E.g.: Final exam, Case study"); 

	    messages.put("Link.label", "Label");
	    messages.put("Link.url", "URL");
	    messages.put("Link.description", "Description of Link");

	    messages.put("UpButton.title", "Up");
	    messages.put("DownButton.title", "Down");

	    messages.put("Browser.upButton.tooltip", "Go one Level up");
	    messages.put("Browser.addFolderButton.tooltip", "Add Folder...");
	    messages.put("Browser.addFileButton.tooltip", "Add a File...");
	    messages.put("Browser.addCitationButton.tooltip", "Add a Citation");

	    messages.put("Browser.selected_citation", "Selected Citation:");
	    messages.put("Browser.selected_file", "Selected File: ");

	    messages
		    .put("CitationForm.createCitation", "Create a new Citation");
	    messages.put("CitationForm.editCitation", "Edit Citation");
	    messages.put("CitationForm.fillRequiredFields",
		    "Please fill the required fields!");
	    messages.put("AssignmentEditor.title",
		    "Edition: link to the Assignment");
	    messages.put("AssignmentEditor.changeParams",
		    "To change parameters like the due date or the " +
		    "instructions for this assignment, use the Assignment " +
		    "tool available in the left column.");
	    
	    messages.put("editor.moveTo", "Move to:");
	    
	    messages.put("element.moved", "The element was moved.");
	    
	    messages.put("publish.error", "Error in publishing");
	    
	    messages.put("Evaluation.field.required","The field '{0}' is required");
	    messages.put("Evaluation.field.date.unISO","The field '{0}' no respect the date format yyyy-mm-dd");
	    messages.put("Evaluation.field.date.order","The end date is not after the start date");
	    messages.put(" Evaluation.field.weight.format","Field weight must be expressed as a percentage");
	   
	}
	return messages;
    }
}
