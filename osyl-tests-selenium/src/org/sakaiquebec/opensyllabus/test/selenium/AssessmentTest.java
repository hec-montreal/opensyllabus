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
import java.text.SimpleDateFormat;
import java.util.Date;
import org.sakaiquebec.opensyllabus.test.selenium.utils.PopupUtils;
import org.sakaiquebec.opensyllabus.test.selenium.utils.ResourceXpathHelper;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests all the features in the section Assessment. The exact steps are: log in
 * as admin, creates a new site if needed, load it (OpenSyllabus is the first ),
 * enters in the Assessment section, Add two assessments, edit the last
 * assessment, delete the first assessment unit,check if OpenSyllabus displays a
 * error message if a mandatory fields are not filled or filled with wrong
 * values,Add text in the last assessment, and check if the text is visible in
 * attendee overview and not visible in the public overview, add a hyperlink and
 * check if the hyperlink is visible in attendee overview and not visible in the
 * public overview, add a document and check if the document is visible in
 * attendee overview and not visible in the public overview, add a citation list
 * and check if the new citation is visible in attendee overview and not visible
 * in the public overview, switch the two last assessments, delete the last
 * assessment, save all modifications, log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:bouchra.laabissi@hec.ca">Bouchra Laabissi</a>
 */

public class AssessmentTest extends AbstractOSYLTest {

    @Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void TestAddAssessment(String webSite) throws Exception {
	
	logStartTest();

	// We log in
	logInAsAdmin(webSite);
	try {
	    deleteTestSite(false);
	    goToCurrentSite();
	} catch (IllegalStateException e) {
	    createTestSite();
		logFile(OSYL_TEST, CT_002, PASSED);
	    goToCurrentSite();    
	}
	//waitForOSYL();
	logFile(OSYL_TEST, CT_069, PASSED);
	
	//Add Assessment tool in OutLine Course
	addAssessmentInOutLinecours();
	//Add a work in Assessment tool
	goToMenuAssessment();		
	createAssignmentGrades();
	//On returning to OutLine Course
	goToMenuOsyl();	
	
	// ---------------------------------------------------------------------------//
	// Add Assessment Unit //
	// ---------------------------------------------------------------------------//

	prettyLog("Add 2 assessment units");

	//click on Assessment section
	openEvaluationsSection();
	//pause();
	

	// We add a first Assessment Unit
	clickAddItemWithLog("addAssessmentUnit");

	// We add a second Assessment Unit
	clickAddItemWithLog("addAssessmentUnit");
	pause(2000);

	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_009, PASSED);
	

	// We keep track of how many resources are showing to check that it
	// is incremented as expected when we add one
	int nbResources = ResourceXpathHelper.getNbResource();
	//int resNb = getResourceCount() - 1;
	log("We start with " + nbResources + " resources");

	// ---------------------------------------------------------------------------//
	// Modify Assessment Unit //
	// ---------------------------------------------------------------------------//

	prettyLog("modify last assessment unit");
	
	// We edit the last assessment
	int Position = nbResources - 1;
	session().click(ResourceXpathHelper.getButtonModify(Position));
	//session().click("//tr[" + Position + "]/td/table/tbody/tr/td[2]/div/"
	//		+ "table[2]/tbody/tr/td/button");
	pause();

	// We fill the weighting field
	String newText3 = "20";
	session().type("//tr[2]/td/table/tbody/tr/td/input", newText3);

	// We select randomly the assessment type field
	String xpathRole1 = "//select[@class=\"gwt-ListBox\"]";

	String newText5 = getRandomOption(xpathRole1);
	session().select(xpathRole1, newText5);

	// We close the assessment editor
	session().click(
		"//table/tbody/tr/td/table/tbody/tr/td/table/tbody/"
			+ "tr/td/button");
	pause();
	//pause();

	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_030, PASSED);
	
	// ---------------------------------------------------------------------------//
	// Modify Assessment Unit //
	// ---------------------------------------------------------------------------//

	// We open the last assessment
//	int Val = nbResources + 3;
//	session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//
//	// We edit the last assessment
//	session().click("//table/tbody/tr/td/div/table[2]/tbody/tr/td/button");
//	pause();
//	pause();
	
	// We open the last assessment
	prettyLog("Provoke error by emptying Weighting");
	
	session().click(ResourceXpathHelper.getButtonModify(Position));
	//session().click("//tr[" + Position + "]/td/table/tbody/tr/td[2]/div/"
	//		+ "table[2]/tbody/tr/td/button");
	pause();

	
	// We check if Opensyllabus displays a message error when the user click
	// OK without fill the mandatory fields
	// --------------------------------------------------------------------//

	// We empty the fields "Weighting"
	session().type("//tr[2]/td/table/tbody/tr/td/input", "");

	// We empty the the assessment type field
	session().select(xpathRole1, "label=");

	// We click OK without filling the mandatory fields
	session().click("//td/table/tbody/tr/td[1]/button");
	expectErreur();

	// We click OK to return to Assessment editor after message error
	// displaying
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause(1000);
	//pause();
	
	
	// We check if the field "assessment type" is mandatory
	// --------------------------------------------------------------------//
	prettyLog("Provoke error: Empty date,");


	// We fill the weighting field
	session().type("//tr[2]/td/table/tbody/tr/td/input", newText3);

	// We select randomly the work mode field (mode de travail)
	String xpathRole2 =
		"//tr[2]/td/table/tbody/tr/td[3]/table/tbody/tr[2]/td/select";
	String newText6 = getRandomOption(xpathRole2);
	session().select(xpathRole2, newText6);
	pause();

	// We define elements to click on checkboxs 
	String localisat1 = "//input[@id=(//label[text()=\"En classe\"]/@for)]";
	String localisat2 = "//input[@id=(//label[contains(text(),'la maison')]/@for)]";
	String modalidad1 = "//input[@id=(//label[text()=\"Oral\"]/@for)]";
	String modalidad2 = "//input[@id=(//label[contains(text(),'crit')]/@for)]";
	String modeRemis1 = "//input[@id=(//label[text()=\"Papier\"]/@for)]";
	String modeRemis2 = "//input[@id=(//label[contains(text(),'tronique')]/@for)]";	
	int optionId = 1 + (int) Math.round(Math.random() * 2);
	
	// We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	expectErreur();
	log("OK: Error displayed");

	// We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause(1000);
	//pause();
	
	prettyLog("Provoke error: emptying Work mode");

	//Clicks for location, work mode and submission mode fields
	if (optionId==1){
		session().click(localisat1);
		session().click(modalidad1);
		session().click(modeRemis1);
	}else {
		session().click(localisat2);
		session().click(modalidad2);
		session().click(modeRemis2);		
	}	
	
	// We select randomly the assessment type field
	session().select(xpathRole1, newText5);

	// We empty the fields work mode
	session().select(xpathRole2, "label=");

	// We select randomly the "Location" field
	//session().select(xpathRole3, newText7);

	// We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	expectErreur();

	// We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	//pause();	
	// We check if the field "Work mode" is mandatory - it needs an update
	// --------------------------------------------------------------------//
	// We select randomly the location field
	session().select(xpathRole2, newText6);

	// We empty the fields "Weighting"
	session().type("//tr[2]/td/table/tbody/tr/td/input", "10");
	
	// We check if Opensyllabus displays a message error when the user enter
	// a wrong date format. -it needs an update
	// -------------------------------------------------------------------//
	// We fill the weighting field
	session().type("//tr[2]/td/table/tbody/tr/td/input", newText3);

	// We fill the assessment name field
	String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	// We close Editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();
	pause();
	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_050, PASSED);

	// ---------------------------------------------------------------------------//
	// Add Document in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click on Assessment section	
	openEvaluationsSection();

	// Edit first Assessment unit
	prettyLog("Edit first Assessment unit: Add document");	
	openResource(0);// 0 = first resource

	if (inFireFox()) {	
	    // Add new document
	    clickAddItemWithLog("addDocument");

	    // We open Document resource editor
	    session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	    // We select attendee on dissemination level
	    // session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
	    // ---It isn't necessary
	    // "index=0");

	    // We choose randomly a Rubric
	    String selectedRubric3 = getRandomRubric();
	    log("Selecting rubric [" + selectedRubric3 + "]");
	    changeRubric(selectedRubric3);

	    // We type the clickable text
	    String newText12 = timeStamp();
	    session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]",
		    newText12);

	// Open form to upload a first document

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

	    // Open form to upload a first document
		session().type("uploadFormElement", FILE_DIR + XLS_FILE);

	    // We select randomly the rights field
	    String xpathRole4 = "//div[2]/form/table/tbody/tr[5]/td/select";
	    String newText8 = getRandomOption(xpathRole4);
	    session().select(xpathRole4, newText8);
	    pause();
		
	    //We select randonly the resource type field
	    String xpathRole5 = "//div[2]/form/table/tbody/tr[7]/td/select";	    
	    session().select(xpathRole5, getRandomOption(xpathRole5));
	    pause();

	    // Close window
	    session().click("//tr[8]/td/table/tbody/tr/td[1]/button");
	    pause();
	    // session().click("document.forms[0].elements[2]");

	    // Open form to upload a second document
	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");

	    // Choose file
		session().type("//input[@class=\"gwt-FileUpload\"]",FILE_DIR + PPT_FILE);	    

	    // We select randomly the rights field
	    xpathRole4 = "//div[2]/form/table/tbody/tr[5]/td/select";
	    newText8 = getRandomOption(xpathRole4);
	    session().select(xpathRole4, newText8);
	    //pause();

	    //We select randonly the resource type field
	    xpathRole5 = "//div[2]/form/table/tbody/tr[7]/td/select";	    
	    session().select(xpathRole5, getRandomOption(xpathRole5));
	    pause();

	    // Close window
	    session().click("//tr[8]/td/table/tbody/tr/td[1]/button");
	    pause();

	//else
	//{	
		/*
		* else { session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
		* session().focus("//input[@class=\"gwt-FileUpload\"]"); }
		*/
		/**
		String locator = "//div[contains(@title,'Ajouter')]";	
		session().mouseOver(locator);		
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
		**/	
	//}
	// Select the excel file in browser window	
	
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
			"value= (F" + ")   excel_file_1_.xls"); //fichier-excel_1_.xls
		session().mouseOver("//option[@value=' (F)   excel_file_1_.xls']"); //fichier-excel_1_.xls
		session().focus("//option[@value=' (F)   excel_file_1_.xls']");
		session().click("//option[@value=' (F)   excel_file_1_.xls']"); // fichier-excel_1_.xlsx
		pause();

		// Close Editor
		session().click(
			"//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/"
				+ "tbody/tr/td[1]/button");
			
		// Save modifications
		saveCourseOutline();
		pause();

		//Add message to log file
		logFile(ASSESSMENT_TEST, CT_0092, PASSED);
		
	// ---------------------------------------------------------------------------//
	// Add Text in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click on Assessment section
	openEvaluationsSection();
	prettyLog("Edit first Assessment: Add Text");	

	openResource(0);

	String newText9 =
	    "this is a text resource typed by "
		    + "selenium, hope it works and you see it. Added on "
		    + timeStamp() + " in Firefox";

	// Save modifications
//	saveCourseOutline(); WE dont need this.... Nothing to save (button is grayed out....)
	pause();
	//pause();
	String selectedRubric1 = addText(newText9,LEVEL_PUBLIC);

	if (inFireFox()) {
	    
	    // ---------------------------------------------------------------------------//
	    // Open Attendee Overview
	    // ---------------------------------------------------------------------------//
	    prettyLog("Open Attendee Overview. Verify: " + selectedRubric1);

	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Attendee Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	    pause();
	    //pause();
	    // Click on Assessment section
	    openEvaluationsSection();

	    // Open first Assessment unit
	    openResource(0);
//	    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

	    pause();
	    
	    
//prettyLog("[[[ Skipped this portion!!!");
	    if (!session().isTextPresent(selectedRubric1)) {
		logAndFail("Expected to see rubric [" + selectedRubric1
			+ "] after text edition");
	    }
	    log("OK: Selected rubric is visible");
//prettyLog("]]] Skipped this portion!!!");
	    
	    

	    // Close Overview
	    session()
		    .click(
			    "//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");

	    // ---------------------------------------------------------------------------//
	    // Open Public Overview
	    // ---------------------------------------------------------------------------//
	    prettyLog("Open Public Overview: Verify: " + selectedRubric1);

	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Public Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]"
			    + "/div/div/table/tbody/tr[2]/td");
	    pause();
		pause();
	    // Click on Assessment section
	    openEvaluationsSection();

	    // Open fist (not last!) Assessment unit
//	    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    openResource(0);

//prettyLog("[[[ Skipped this portion!!!");
	    if (!(session().isTextPresent(selectedRubric1))) {
	    	logAndFail("Expected to see rubric [" + selectedRubric1
			+ "] after text edition on public overview");
	    	//Add message to log file
	    	logFile(ASSESSMENT_TEST, CT_0091, FAILED);	    	
	    }
	    log("OK: Selected rubric is visible");
//prettyLog("]]] Skipped this portion!!!");

	    // Close Overview
	    session()
		    .click(
			    "//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	}
	
	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_0091, PASSED);	  
	
	// ---------------------------------------------------------------------------//
	// Add Citation in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click on Assessment section
	openEvaluationsSection();

	// Edit first Assessment unit
	prettyLog("Edit first Assessment: Add BiblioResource: " + newText1);
	openResource(0);
//	if (inFireFox()) {	    
//		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	} else { // IE
//		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
//		session().mouseDownAt(locator, "16,16");
//		session().mouseUpAt(locator, "16,16");
//	}

	// Add new Citation
	clickAddItemWithLog("addBiblioResource");

	// open Citation resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	pause();
	pause();
	
	// We choose randomly a Rubric
	String selectedRubric4 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric4 + "]");
	changeRubric(selectedRubric4);

	// Create a new citation list
	session().answerOnNextPrompt("NewListe" + newText1);
	log("NewListe" + newText1 + " is created...");
	pause();
	pause();	
	pause();
	pause();
	// Open form to upload a first Citation (Book)
	if (inFireFox()) {
		// Create a new citation list
		String locator = "//div[contains(@title,'Ajouter')]";	
	    session().mouseOver(locator);
	    session().mouseDown(locator);
	    session().mouseUp(locator);	    
		assertTrue(session().isPromptPresent());				
		// Open Citation list works different
		session().click("//tr[2]/td/select");
		pause();
		session().select("//tr[2]/td/select", "(LREF) NewListe" + newText1);
		pause();		
		session().doubleClick("//tr[2]/td/select/option");
		log("New elements is selected...");		
	    session().mouseOver(locator);
	    session().mouseDown(locator);
	    session().mouseUp(locator);			    
	} else { //IE
		// Create a new citation list
		String locator = "//div[contains(@title,'Ajouter')]";	
	    session().mouseOver(locator);		
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
		pause();
		pause();				
		// Open Citation for IE
		session().click("//tr[2]/td/select");
		pause();		
		session().select("//tr[2]/td/select", "index=1");
		pause();		
		session().doubleClick("//tr[2]/td/select/option");
		log("New elements is selected...");			
	    session().mouseOver(locator);		
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");		
	}	
	log("The format to fill is ready to open...");
	//-----------------------------------------------------------------------
	// Check if Opensyllabus displays a message error when the user click OK
	// without fill in the fields First Name, Last Name and Title.
	log("Validation de données vides...");	
	
	//session().click("//tr[22]/td/table/tbody/tr/td[1]/button");
	session().click(PopupUtils.DocumentAttributesPopup.getButtonOk());
	
	if (!session().isTextPresent(newText1)) {
		logAndFail("Expected to see text [" + newText1
				+ "] after text edition");
		//Add message to log file
		logFile(ASSESSMENT_TEST, CT_0093, FAILED);		
	}
	session().click("//tr[2]/td/table/tbody/tr/td/button");// close warning popup
	pause(1000);
	//pause();
	log("Button Citation selected...");	
	String Titre = "Titre" + timeStamp();
	String Auteur = "Auteur" + timeStamp();		
	String Annee = "Annee" + timeStamp();
	String Editeur = "Editeur" + timeStamp();
	String Lieu = "Lieu" + timeStamp();
	String ISBN = "ISBN" + timeStamp();
	log("Fields before showing...");	

	// Fill the necessary fields	
	session().select("//select[@name='cipvalues']", "label=Livre");
	session().click("//tr[9]/td/table/tbody/tr/td[3]/input");		
	session().type("//tr[9]/td/table/tbody/tr/td[3]/input", Titre);	
	session().type("//tr[10]/td/table/tbody/tr/td[3]/input", Auteur);
	session().type("//tr[11]/td/table/tbody/tr/td[3]/input", Annee);
	session().type("//tr[12]/td/table/tbody/tr/td[3]/input", Editeur);
	session().type("//tr[13]/td/table/tbody/tr/td[3]/input", Lieu);
	session().type("//tr[19]/td/table/tbody/tr/td[3]/input", ISBN);
	log("Fields before showing...");
	// Close Window
	pause();
	session().click(PopupUtils.DocumentAttributesPopup.getButtonOk());
	//session().click("//tr[22]/td/table/tbody/tr/td[1]/button");
	pause();
	pause();
	//pause();
	//pause();	
	log("Fields before showing...");
	// Select first resource in browser window
	session().focus("//option[@value=' (REF)   " + Titre + "']");
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "value= " + "(REF)   " + Titre);
	session().mouseOver("//option[@value=' (REF)   " + Titre + "']");
	session().click("//option[@value=' (REF)   " + Titre + "']");
	pause();
	pause();
	//pause();
	
	// Close Editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();
	pause();	
	// Save Assessment for FF and IE	
	saveCourseOutline(); 
	pause();

	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_0093, PASSED);
	
	// ---------------------------------------------------------------------------//
	// Overview for Assessment Unit //
	// ---------------------------------------------------------------------------//	
    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	// Attendee Overview
	session().click(
		"//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			+ "div/table/tbody/tr/td");
	pause();
	pause();	
	// Click on Assessment section
	openEvaluationsSection();

	//int Val = nbResources + 3;

	// Edit first Assessment unit
	openResource(0);
//	if (inFireFox()) {	    
//		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	} else { // IE
//		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
//		session().mouseDownAt(locator, "16,16");
//		session().mouseUpAt(locator, "16,16");
//	}	

	/** AH
	if (!session().isTextPresent(selectedRubric4)) {
	    logAndFail("Expected to see rubric [" + selectedRubric4
		    + "] after text edition");
	}
	log("OK: Selected rubric is visible");
	 **/
	/**
	if (inFireFox()) {	
		if (!session().isTextPresent(Auteur)) {
		    logAndFail("Expected to see text [" + Auteur + " (" + Annee + ")."
			    + Titre + "," + Editeur + "," + Lieu + "."
			    + "] after text edition");
		}
		log("OK: Text is visible");
	}	**/

	// Close Overview
	session()
		.click(
			"//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	pause();
	
	
	// ---------------------------------------------------------------------------//
	// Add Assignment tool link in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click on Assessment section
	openEvaluationsSection();
	
	// Edit first Assessment unit
	openResource(0);
//	if (inFireFox()) {
//		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	} else { // IE
//		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
//		session().mouseDownAt(locator, "10,10");
//		session().mouseUpAt(locator, "10,10");
//	}	
	
	// Add Hyperlink in Assessment Unit
	prettyLog("addEntity");
	clickAddItem("addEntity");
	
	// We edit the new Hyperlink rubric
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	// We add an assignment 
	pause();
	pause();
	//pause();
	
	session().clickAt("//div/div/div/div[2]/table/tbody/tr/td[1]/img", "");
	pause();
	
	session().mouseOver("//tr[2]/td/div/div/div/div[2]/div/div/div[@class='gwt-TreeItem']");
	session().mouseDown("//tr[2]/td/div/div/div/div[2]/div/div/div[@class='gwt-TreeItem']");
	pause();	
	
	session().select("listBoxFormElement", "label=Description");
	session().type("//tr[2]/td/input", "It's an example for assessment (remis de travaux)");
	session().select("//td[4]/table/tbody/tr[2]/td/select", "label=Obligatoire");
	pause();	
	
	// We close Editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();	
	
	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_105, PASSED);
	
	// ---------------------------------------------------------------------------//
	// Add Hyperlink in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click on Assessment section
	openEvaluationsSection();

	// Edit first Assessment unit
	openResource(0);
//	if (inFireFox()) {
//		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	} else { // IE
//		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
//		session().mouseDownAt(locator, "10,10");
//		session().mouseUpAt(locator, "10,10");
//	}
	
	// Add Hyperlink in Assessment Unit
	prettyLog("addURL");
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
	String newText10 = "http://webmail.hec.ca/";
	session().type("//tr[2]/td[2]/input", newText10);
	pause();

	// We click OK to close editor
	session().click("//td/table/tbody/tr/td[1]/button");

	// We click URL to test
	session().click("link=" + newText11);

	// Save modifications
	saveCourseOutline();
	pause();

	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_028, PASSED);

	// ---------------------------------------------------------------------------//
	// Overview //
	// ---------------------------------------------------------------------------//
	
	if (inFireFox()) {
	    prettyLog("Attendee overview. Expected: " + selectedRubric2);
	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Attendee Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	    pause();

	    // Click on Assessment section
	    openEvaluationsSection();

	    // Edit the first (? not last) Assessment unit
	    openResource(0);
//	    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

	    if (!session().isTextPresent(selectedRubric2)) {
		logAndFail("Expected to see rubric [" + selectedRubric2
			+ "] after text edition");
	    }
	    log("OK: Selected rubric is visible");

	    // Close Overview
	    session()
		    .click(
			    "//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	    pause();

	 }  
		
	// ---------------------------------------------------------------------------//
	// Overview //
	// ---------------------------------------------------------------------------//
		
	prettyLog("Attendee overview. Expected: " + selectedRubric3);
	session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	// Attendee Overview
	session().click(
		"//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			+ "div/table/tbody/tr/td");
	pause();
	// Click on Assessment section
	openEvaluationsSection();
	
	// Edit first Assessment unit
	openResource(0);

		//if (inFireFox()) {	    
//		    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//		    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		//} else { // IE
		//	String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		//	session().mouseDownAt(locator, "10,10");
		//	session().mouseUpAt(locator, "10,10");
		//}

	if (!session().isTextPresent(selectedRubric3)) {
	    logAndFail("Expected to see rubric [" + selectedRubric3
		    + "] after text edition");
	}
	log("OK: Selected rubric is visible");

	if (!session().isTextPresent(newText12)) {
	    logAndFail("Expected to see rubric [" + newText12
		   + "] after text edition");
	}
	log("OK: Text is visible");

	// Close Overview
	session().click(
		"//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	pause();
	
	}

	// ---------------------------------------------------------------------------//
	// Switch two Assessments //
	// ---------------------------------------------------------------------------//

	// Click on Assessment section
	openEvaluationsSection();
	
	//pause();

	// We now switch the 1st and 2nd assessments (different ways to do it for
	// MSIE and FF, unfortunately):
	//It needs coding

	// ---------------------------------------------------------------------------//
	// Delete Assessment Unit //
	// ---------------------------------------------------------------------------//

	// We delete Assessment 1
	prettyLog("Delete first assessment");
	session().click(ResourceXpathHelper.getButtonDelete(0));
	//session().click("//tr[" + Val2 + "]/td/table/tbody/tr/td[2]/div/table[2]" + "/tbody/tr/td[2]/button");

	//session().click("//tr[2]/td/table/tbody/tr/td/button");// confirm deletion
	session().click("xpath=(//div[@class='gwt-DialogBox']//button)[1]");
	pause(1000);
	
	// Save modifications
	saveCourseOutline();
	pause(3000);	
	//Add message to log file
	logFile(ASSESSMENT_TEST, CT_070, PASSED);	
	
	// Log out
	session().selectFrame("relative=parent");
	logOut();
	
	// ----------------------------------------------
	// Test completion
	// ----------------------------------------------
	logEndTest();
    }

    /**
     * Opens the resource by clicking on its number. This allows to add sub-elements to it, like Documents, Texts, etc.
     * (is different from 'modifyResource' which is done by clicking on its Modify button.)
     * 
     * @param resourceIndex Position of resource starting from 0.
     */
    private void openResource(int resourceIndex) {
	if (inFireFox()) {
	    String locator = ResourceXpathHelper.getItemHref(resourceIndex);
	    session().mouseOver(locator);
	    session().mouseDown(locator);
	    session().mouseUp(locator);
//			session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//			session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
//			session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	} else { // IE
	    String locator = ResourceXpathHelper.getItemHref(resourceIndex);
		//String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
	}
    }
    
    /**
     * Expect to see an erreur box.
     * If not return false.
     * @return
     */
    private boolean expectErreur() {
	String errorLocator = "//div[contains(text(),'Erreur')]";
	try {
	    waitForElement(errorLocator, 6000);
	    return true;
	} catch (Exception e) {
	    logAndFail("Expected to see text [" + errorLocator + "] after text edition");
	    logFile(ASSESSMENT_TEST, CT_050, FAILED);
	    return false;
	}

    }

    /**
     * 
     */
    private void openEvaluationsSection() throws Exception {
	prettyLog("Open Evaluations section");
	// Click on Assessment section
	// pause();
	if (inFireFox()) {
	    session().mouseDown(
		    "//div[@class=\"gwt-TreeItem\"]/div/"
			    + "div[contains(text(),'valuation')]");
	} else {
	    // IE
	    String imageLocator = "//div[contains(text(),'valuation')]";
	    session().mouseDownAt(imageLocator, "10,10");
	    session().mouseUpAt(imageLocator, "10,10");
	}
	// pause();
	waitForElement("//div[@class='Osyl-WorkspaceView-Header' and contains(text(),'valuations')]", 3000);//LANG
    }

    private void createAssignmentGrades() {

	prettyLog("Create an assignment grades");
	// Click Assessment section
	// Create an assignment grades
	//pause();
	session().click("//*[contains(@alt,'initialiser')]");
	pause();
	session().click("link=Ajouter");
	session().waitForPageToLoad("30000");
	session().type("new_assignment_title", "Travail TP" + timeStamp());
	session().select("new_assignment_openampm", "label=AM");
	session().select("new_assignment_grade_type", "label=Points");
	session().type("new_assignment_grade_points", "10");
	session()
		.click("//td[@id='xToolbar']/table[1]/tbody/tr/td[2]/div/table/tbody/tr/td[1]");
	session().type("//td[@id='xEditingArea']/textarea",
		"Instructions pour le travail de devoir " + timeStamp());
	pause(1000);
	session().click("post");
	pause();
	pause();
	session().waitForPageToLoad("30000");
	session().selectFrame("relative=up");
	// Add message to log file
	logFile(ASSESSMENT_TEST, PT_19_2, PASSED);
    }	
	
	
	    /**
	     * {@inheritDoc}
	     */
	    public void goToMenuAssessment() throws IllegalStateException {
		String elementAssessmentMenu = "//*[@class='icon-sakai-assignment-grades']";
		if (session().isElementPresent(elementAssessmentMenu)) {
			// open Assessment tool
			if (inFireFox()) {
				session().mouseOver(elementAssessmentMenu);
				session().mouseDown(elementAssessmentMenu);
				session().mouseUp(elementAssessmentMenu);
				session().click(elementAssessmentMenu);
				pause();
			} else {
				session().click(elementAssessmentMenu);
			}
		}
		pause(3000);
	    }
}
