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

import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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
	// We log in
	logInAsAdmin(webSite);
	try {
	    goToCurrentSite();
	} catch (IllegalStateException e) {
	    createTestSite();
	    goToCurrentSite();
	}
	waitForOSYL();

	// ---------------------------------------------------------------------------//
	// Add Assessment Unit //
	// ---------------------------------------------------------------------------//

	openEvaluationsSection();

	// We keep track of how many resources are showing to check that it
	// is incremented as expected when we add one
	int resNb = getResourceCount() - 1;
	log("We start with " + resNb + " resources");

	pause();

	// We add a first Assessment Unit
	clickAddItem("addAssessmentUnit");

	// We add a second Assessment Unit
	clickAddItem("addAssessmentUnit");

	// ---------------------------------------------------------------------------//
	// Modify Assessment Unit //
	// ---------------------------------------------------------------------------//

	int Position = resNb + 3;
	// We edit the last assessment
	session().click(
		"//tr[" + Position + "]/td/table/tbody/tr/td[2]/div/"
			+ "table[2]/tbody/tr/td/button");
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

	// ---------------------------------------------------------------------------//
	// Modify Assessment Unit //
	// ---------------------------------------------------------------------------//

	// We open the last assessment
	int Val = resNb + 3;
	session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

	// We edit the last assessment
	session().click("//table/tbody/tr/td/div/table[2]/tbody/tr/td/button");
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

	// We verify if Opensyllabus display a message error
	//isTextPresent(text='Erreur') doesn't work for modal windows 
	//It needs use isElementPresent(elementOfText)
	String Erreur = "//div[contains(text(),'Erreur')]";	
	if (!session().isElementPresent(Erreur)) {	
	    logAndFail("Expected to see text [" + Erreur
		    + "] after text edition");
	}
	log("OK: Error displayed");

	// We click OK to return to Assessment editor after message error
	// displaying
	session().click("//tr[2]/td/table/tbody/tr/td/button");

	// We check if the field "assessment type" is mandatory
	// --------------------------------------------------------------------//

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

	// We check if Opensyllabus displays a message error
	if (!session().isElementPresent(Erreur)) {	
	//if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur
		    + "] after text edition");
	}
	log("OK: Error displayed");

	// We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");

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

	// We check if Opensyllabus displays a message error
	if (!session().isElementPresent(Erreur)) {	
	//if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur
		    + "] after text edition");
	}
	log("OK: Error displayed");

	// We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");

	// We check if the field "Work mode" is mandatory
	// --------------------------------------------------------------------//

	// We select randomly the location field
	session().select(xpathRole2, newText6);

	// We empty the fields "Weighting"
	session().type("//tr[2]/td/table/tbody/tr/td/input", "10");
	

	// We check if Opensyllabus displays a message error when the user enter
	// a wrong date format.
	// -------------------------------------------------------------------//

	// We fill the weighting field
	session().type("//tr[2]/td/table/tbody/tr/td/input", newText3);

	// We fill the assessment name field
	String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

	// We close Editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();

	// ---------------------------------------------------------------------------//
	// Add Text in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Open Assessment section

	openEvaluationsSection();
	
	// Open last Assessment unit
	if (inFireFox()) {
		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	} else { // IE
		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
	}
	String newText9 =
	    "this is a text resource typed by "
		    + "selenium, hope it works and you see it. Added on "
		    + timeStamp() + " in Firefox";
	//saveCourseOutline();
	String selectedRubric1 = addText(newText9,LEVEL_PUBLIC);
	// Save modifications
	saveCourseOutline();
	pause();

	if (inFireFox()) {

	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Attendee Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	    pause();

	    // Click Assessment section
	    openEvaluationsSection();

	    // Open last Assessment unit
	    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

	    pause();

	    if (!session().isTextPresent(selectedRubric1)) {
		logAndFail("Expected to see rubric [" + selectedRubric1
			+ "] after text edition");
	    }
	    log("OK: Selected rubric is visible");

	    // Close Overview
	    session()
		    .click(
			    "//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");

	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Public Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]"
			    + "/div/div/table/tbody/tr[2]/td");
	    pause();

	    // Click Assessment section
	    openEvaluationsSection();

	    // Open last Assessment unit
	    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

	    if (!(session().isTextPresent(selectedRubric1))) {
		logAndFail("Expected to see rubric [" + selectedRubric1
			+ "] after text edition on public overview");
	    }
	    log("OK: Selected rubric is visible");

	    // Close Overview
	    session()
		    .click(
			    "//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	}

	// ---------------------------------------------------------------------------//
	// Add Hyperlink in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click Assessment section
	openEvaluationsSection();

	// Edit first Assessment unit
	if (inFireFox()) {
		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	} else { // IE
		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
	}
	
	// Add Hyperlink in Assessment Unit
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

	if (inFireFox()) {
	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Attendee Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	    pause();

	    // Click Assessment section
	    openEvaluationsSection();

	    // Edit the last Assessment unit
	    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

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
	// Add Document in Assessment Unit //
	// ---------------------------------------------------------------------------//
	// Click Assessment section
	
	openEvaluationsSection();

	// Edit first Assessment unit
	if (inFireFox()) {	
		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	} else { // IE
		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
	}

	if (inFireFox()) {	
		// Add new document
		clickAddItem("addDocument");
	
		// We open Document resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	
		// We select attendee on dissemination level
		session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
			"index=0");
	
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

	    // Choose file and close window
	    session().type(
			    "uploadFormElement",
			    "C:\\Documents and Setti"
				    + "ngs\\clihec3\\Local Settings\\"
				    + "fichier-excel[1].xlsx");
	    
	    String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
	    String newText8 = getRandomOption(xpathRole4);
	    session().select(xpathRole4, newText8);
	    pause();
	    // Close window
	    session().click("//tr[5]/td/table/tbody/tr/td/button");
	    pause();
	    // session().click("document.forms[0].elements[2]");
	  
	// Open form to upload a second document
	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");

	    // Choose file
	    session()
		    .type(
			    "//input[@class=\"gwt-FileUpload\"]",
			    "C:\\"
				    + "Documents and Settings\\"
				    + "clihec3\\Local Settings\\Temporary Internet Files\\"
				    + "Content.IE5\\K0F6YKYM\\powerpoint[1].ppt");
	    // We select randomly the rights field
	    xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
	    newText8 = getRandomOption(xpathRole4);
	    session().select(xpathRole4, newText8);
	    pause();
	    // Close window
	    session().click("//tr[5]/td/table/tbody/tr/td/button");
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
			"value= (F" + ")   fichier-excel_1_.xlsx");
		session().mouseOver("//option[@value=' (F)   fichier-excel_1_.xlsx']");
		session().focus("//option[@value=' (F)   fichier-excel_1_.xlsx']");
		session().click("//option[@value=' (F)   fichier-excel_1_.xlsx']");
		pause();

		// Close Editor
		session().click(
			"//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/"
				+ "tbody/tr/td[1]/button");
			
		// Save modifications
		saveCourseOutline();
		pause();

	    // Overview
	    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	    // Attendee Overview
	    session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	    pause();
	    // Click Assessment section
	    openEvaluationsSection();

	    // Edit first Assessment unit
		//if (inFireFox()) {	    
		    session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		    session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		    session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
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
	    session()
		    .click(
			    "//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	    pause();
	
	}

	// ---------------------------------------------------------------------------//
	// Add Citation in Assessment Unit //
	// ---------------------------------------------------------------------------//

	// Click Assessment section
	openEvaluationsSection();

	// Edit first Assessment unit
	if (inFireFox()) {	    
		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	} else { // IE
		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		session().mouseDownAt(locator, "16,16");
		session().mouseUpAt(locator, "16,16");
	}

	// Add new Citation
	clickAddItem("addBiblioResource");

	// open Citation resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

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
		session().select("//tr[2]/td/select", "(LREF) NewListe" + newText1);
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
		// Open Citation for IE
		session().click("//tr[2]/td/select");
		session().select("//tr[2]/td/select", "index=1");
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
	session().click("//tr[22]/td/table/tbody/tr/td[1]/button");
	if (!session().isTextPresent(newText1)) {
		logAndFail("Expected to see text [" + newText1
				+ "] after text edition");
	}
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	
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
	session().click("//tr[22]/td/table/tbody/tr/td[1]/button");
	pause();
	pause();
	log("Fields before showing...");	log("Fields before showing...");
	// Select first resource in browser window
	session().focus("//option[@value=' (REF)   " + Titre + "']");
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "value= " + "(REF)   " + Titre);
	session().mouseOver("//option[@value=' (REF)   " + Titre + "']");
	session().click("//option[@value=' (REF)   " + Titre + "']");
	pause();
	
	// Close Editor
	session().click("//td/table/tbody/tr/td[1]/button");

	// Save Assessment for FF and IE	
	saveCourseOutline(); 
	pause();

	// Overview
    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	// Attendee Overview
	session().click(
		"//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			+ "div/table/tbody/tr/td");
	pause();
	// Click Assessment section
	openEvaluationsSection();

	// Edit first Assessment unit
	if (inFireFox()) {	    
		session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	} else { // IE
		String locator = "//div/div/div[2]/div/div[5]/div/div[" + Val + "]/div/div/div";
		session().mouseDownAt(locator, "16,16");
		session().mouseUpAt(locator, "16,16");
	}	

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
	// Switch two Assessments //
	// ---------------------------------------------------------------------------//

	// Click Assessment section
	openEvaluationsSection();
	
	pause();

	// We now switch the 1st and 2nd assessments (different ways to do it for
	// MSIE and FF, unfortunately):
	/**
	if (inInternetExplorer()) {
	    session()
		    .keyPress(
			    "//div[@class=\"Osyl-PushButton "
				    + "Osyl-PushButton-up\"]", "\r");
	} else {
	    session().mouseOver("//div[@class=\"Osyl-UnitView-ResPanel\"]");
	    session().mouseOver("//div[@class=\"Osyl-UnitView-ResPanel Osyl-UnitView-ResPanel-Hover\"]");
	    session().mouseOver("//table[@class=\"Osyl-MouseOverPopup-ArrowButtonPanel\"]/tbody/tr[2]");
	    session().mouseOver("//div[@class=\"Osyl-PushButton Osyl-PushButton-up\"]");
	    session().mouseDown("//div[@class=\"Osyl-PushButton Osyl-PushButton-up-hovering\"]");
	    session().mouseUp("//div[@class=\"Osyl-PushButton Osyl-PushButton-down-hovering\"]");
	}
	**/
	// ---------------------------------------------------------------------------//
	// Delete Assessment Unit //
	// ---------------------------------------------------------------------------//

	// We delete Assessment 1
	int Val2 = Val;
	session().click("//tr[" + Val2 + "]/td/table/tbody/tr/td[2]/div/table[2]" + "/tbody/tr/td[2]/button");
	session().click("//tr[2]/td/table/tbody/tr/td/button");

	// Save modifications
	saveCourseOutline();
	pause();

	// Log out
	session().selectFrame("relative=parent");
	logOut();
	log("==============================");	
	log("AssessmentTest: test complete");
	log("==============================");	
    }

	private void openEvaluationsSection() {
		// Click Assessment section
		if (inFireFox()) {
			session().mouseDown(
					"//div[@class=\"gwt-TreeItem\"]/div/" + "div[contains(text(),'valuation')]");
		} else {
			// IE
			//String imageLocator = "//div/div/div[2]/div/div[5]/div[@id='gwt-uid-20']";
			//String imageLocator = "//div[@id='gwt-uid-20']";			
			String imageLocator = "//div[contains(text(),'valuation')]";			
			session().mouseDownAt(imageLocator, "10,10");
			session().mouseUpAt(imageLocator, "10,10");
		}
		pause();
	}	
	
}
