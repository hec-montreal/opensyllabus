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

package org.sakaiquebec.opensyllabus.test.selenium;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

import org.sakaiquebec.opensyllabus.test.selenium.utils.AddFileResourcePopup;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests the addition of a contact-info resource. The exact steps are: log in as
 * admin, creates a new site if needed, load it (OpenSyllabus is the first and
 * only page), enters in the contact info page, click Contact in the Add menu,
 * open the editor, check that we get errors when trying to close the editor
 * without completing the required fields, fill in those fields, change the
 * rubric (randomly) click OK, check the text is here, check the rubric is
 * visible, click save, click public overview,enters in the contact info
 * page,check the text is here, check the rubric is visible, log out.
 * 
 *@author<a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 *@author<a href="mailto:bouchra.laabissi.1@ens.etsmtl.ca">Bouchra Laabissi</a>
 */
public class PresentationOfCourse extends AbstractOSYLTest {
	
    @Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void PresentationOfCourseTest(String webSite) throws Exception {
	
	logStartTest();
	
	// We log in
	logInAsAdmin(webSite);
	
	try {
	    goToCurrentSite();	    
	} catch (IllegalStateException e) {
	    createTestSite();
		logFile(OSYL_TEST, CT_002, PASSED);
	    goToCurrentSite();	
	}
	waitForOSYL();
	logFile(OSYL_TEST, CT_069, PASSED);		

	// ---------------------------------------------------------------------------//
	// Add Text in Overview Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section
	//It's not necessary because now overview is first element on course outline.	
	//openPresentationSection();
	pause();

	String newText9 =
	    "this is a text resource typed by "
		    + "selenium, hope it works and you see it. Added on "
		    + timeStamp() + " in Firefox";
	
	prettyLog("Add Text: " + LEVEL_ATTENDEE);
	addText(newText9,LEVEL_ATTENDEE);

	//Add message to log file
	logFile(OVERVIEW_TEST, CT_006, PASSED);
	
	// Save modifications
	saveCourseOutline();
	pause();
	
	// ---------------------------------------------------------------------------//
	// Add, Modify and delete Text //
	// ---------------------------------------------------------------------------//
	prettyLog("Add then delete text");
	 String newText9Bis =
		    "this is a text resource typed by "
			    + "selenium, hope it works and you see it. Added on "
			    + timeStamp() + " in Firefox";
	addText(newText9Bis,LEVEL_ATTENDEE);
	//Add message to log file
	logFile(OVERVIEW_TEST, CT_006, PASSED);
	 
	// We delete the new text Lecture
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[2]/button");
	pause();

	session()
		.click(
			"//tr[2]/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	pause();
	log("Text deleted");
	pause();

	//Add message to log file
	logFile(OVERVIEW_TEST, CT_010, PASSED);
	
	// Save modifications
	saveCourseOutline();
	pause();
	
	// ---------------------------------------------------------------------------//
	// Add Hyperlink //
	// ---------------------------------------------------------------------------//
	String hyperlink = "http://webmail.hec.ca/";
	prettyLog("Add Hylerlink: " + hyperlink);
	// Add Hyperlink
	clickAddItem("addURL");

	// We edit the new Hyperlink rubric
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		"index=0");

	// We select randomly the rubric name
	String selectedRubric2 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric2 + "]");
	changeRubric(selectedRubric2);

	// We type the clickable text
	String newText11 = timeStamp();
	session().type("//td[2]/input", newText11);

	// We type the URL link
	session().type("//tr[2]/td[2]/input", hyperlink);

	// We click OK to close editor
	session().click("//td/table/tbody/tr/td[1]/button");

	// We click URL to test
	session().click("link=" + newText11);// open a new window (you may manualy close)

	// Save modifications
	saveCourseOutline();
	pause();
	//Add message to log file
	logFile(OVERVIEW_TEST, CT_028, PASSED);

	// ---------------------------------------------------------------------------//
	// Add Document //
	// ---------------------------------------------------------------------------//
	String documentText = "Document text is " + timeStamp();
	prettyLog("addDocument: " + documentText);

	// Add new document
	clickAddItem("addDocument");

	// We open Document resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We choose randomly a Rubric
	String selectedRubric3 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric3 + "]");
	changeRubric(selectedRubric3);

	// We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		"index=0");

	// We type the clickable text
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]",
		documentText);

	// Open form to upload a first document
	if (inFireFox()) {

	    session().mouseOver(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up\"]");
	    session().mouseOver(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up\"]");
	    session().mouseOver(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up\"]");
	    session().mouseOut(
		    "//div[@class=\"Osyl-FileBrowserTopButton"
			    + " Osyl-FileBrowserTopButton-up-hovering\"]");
	    session().mouseOut(
		    "//div[@class=\"Osyl-FileBrowserTopButton"
			    + " Osyl-FileBrowserTopButton-up-hovering\"]");
	    session().mouseDown(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up-hovering\"]");
	    session().mouseUp(
		    "//div[@class=\"Osyl-FileBrowserTopButton"
			    + " Osyl-FileBrowserTopButton-down-hovering\"]");

	    // Choose file and close window
	    session().type("uploadFormElement", FILE_DIR + ZIP_FILE);
	    
	    // Select randomly the Rights field
	    selectAtRandom(AddFileResourcePopup.getRightsSelect());
	    // Select randomly the type of resources field (eg. 'Recueil de textes')
	    selectAtRandom(AddFileResourcePopup.getTypeOfResourceSelect());
	    pause();
	    
	    // Close window
	    session().click(AddFileResourcePopup.getInstance().getButtonOk());
	    pause();

	}/*
	  * else { session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
	  * session().focus("//input[@class=\"gwt-FileUpload\"]");
	  * session().getCursorPosition("//input[@class=\"gwt-FileUpload\ "]");
	  * }
	  */

	// Open form to upload a second document
	if (inFireFox()) {

	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");

	    // Choose file and close window
	    session().type("//input[@class=\"gwt-FileUpload\"]", FILE_DIR + PPT_FILE);
	    
	    // Select randomly the Rights field
	    selectAtRandom(AddFileResourcePopup.getRightsSelect());
	    // Select randomly the type of resources field (eg. 'Recueil de textes')
	    selectAtRandom(AddFileResourcePopup.getTypeOfResourceSelect());
	    pause();
	    
	    // Close window
	    session().click(AddFileResourcePopup.getInstance().getButtonOk());
	    pause();

	}/*
	  * else { session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
	  * session().focus("//input[@class=\"gwt-FileUpload\"]"); }
	  */

	pause();

	// Select file in browser window
	if (inFireFox()) {		
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
			"value= (F" + ")   osyl-src_1_.zip");
		session().mouseOver("//option[@value=' (F)   osyl-src_1_.zip']");
		session().focus("//option[@value=' (F)   osyl-src_1_.zip']");
		session().click("//option[@value=' (F)   osyl-src_1_.zip']");
		pause();
	
		// Close Editor
		session().click(
			"//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/"
				+ "tbody/tr/td[1]/button");
	} else {
		// Cancel editor
		session().click("//td/table/tbody/tr/td[2]/button");
	}
	
	//Add message to log file
	logFile(OVERVIEW_TEST, CT_027, PASSED);
	
	// Save modifications
	saveCourseOutline();
	pause();

	session().selectFrame("relative=parent");
	logOut();
	
	logEndTest();
	
    } // PresentationOfCourseTest
 }