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

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Tests all the features in the section Lectures. The exact steps are: log in
 * as admin, creates a new site if needed, load it (OpenSyllabus is the first
 * page), enters in the Lectures section, add two lectures, name the last one,
 * edit the last lecture, rename it, add text, add hyperlink, add document , add
 * citation, switch the tow added lectures, delete the last one, click save, log
 * out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:bouchra.laabissi@hec.ca">Bouchra Laabissi</a>
 */

public class SeancesTest extends AbstractOSYLTest {
	
    @Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a lecture, edit it and save the changes")
    @Parameters( { "webSite" })
    public void TestAddSeance(String webSite) throws Exception {
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
	// Add Lecture //
	// ---------------------------------------------------------------------------//

	// Click on Organisation Section
	openOrganisationSection();
	pause();

	// We keep track of how many resources are showing
	int resNb = getResourceCount() - 1;
	log("We start with " + resNb + " resources");

	// We add a first Assessment Unit
	clickAddItem("addLecture");
	pause();

	// We add a first Assessment Unit
	clickAddItem("addLecture");
	pause();

	//Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_076, PASSED);

	// ---------------------------------------------------------------------------//
	// Modify Lecture name //
	// ---------------------------------------------------------------------------//

	int Position = resNb + 3;
	// We edit the last Lecture
	session().click("//tr[" + Position + "]/td/table/tbody/tr/td[2]/div/table[2]/tbody/tr/td/button");
	pause();

	// Rename the last Lecture unit
	String newText1 = "FirstSeanceName" + timeStamp();
	session().type("//tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/input",
		newText1);

	// Click OK to close Editor
	session().click("//tr/td/table/tbody/tr/td/table/tbody/tr/td/button");
	pause();

	// Ensure the new name is visible
	if (!session().isTextPresent(newText1)) {
	    //logAndFail("New lecture title not present");
		log("New lecture title not present");
	} else {
		log("New lecture title is present");				
	}
	
	// Now we rename the lecture from inside
	int Val = resNb + 3;
	session().click("//tr[" + Val + "]/td/table/tbody/tr/td[2]/div/table[2]/tbody/tr/td/button");
	pause();
	
	//session().mouseOver("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	//session().mouseDown("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	//session().mouseUp("//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");

	// We edit the last assessment	
	//session().click("//tr/td/div/table[2]/tbody/tr/td/button"); AH
	//session().click("//table/tbody/tr/td/div/table[2]/tbody/tr/td/button");
	  
	
	pause();
			
	String newText2 = "SeanceReNamed" + timeStamp();
	session().type("//tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/input",newText2); 
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if (!session().isTextPresent(newText2)) {
	    //logAndFail("New lecture title not present (inside the lecture)");
		log("New lecture title not present (inside the lecture)");
	} else {
		log("OK: Lecture renamed from inside");	
	}		

	//Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_084, PASSED);
	
	// ---------------------------------------------------------------------------//
	// Add Text in the Lecture Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section	
	openOrganisationSection();
	
	// We edit the last Lecture
	openSeanceSection(newText2);
	pause();	

	String newText9 =
		"this is a text resource typed by "
			+ "selenium, hope it works and you see it. Added on "
			+ timeStamp() + " in Firefox";
	String selectedRubric1 = addText(newText9,LEVEL_ATTENDEE);

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

	    // Open Lectures Section
	    openOrganisationSection();

	    pause();
	    // Open the last Lecture unit
	    session().mouseOver(
		    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseDown(
		    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseUp(
		    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    // session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
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
	    pause();

		//Add message to log file
		logFile(PEDAGOGICAL_TEST, CT_0111, PASSED);

	}

	// ---------------------------------------------------------------------------//
	// Add Hyperlink in Lecture Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section
	openOrganisationSection();

	// Open the last Lecture unit
	openSeanceSection(newText2);
	pause();		

	// Add Hyperlink in the last Lecture Unit
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

	    // Open Lectures Section
	    openOrganisationSection();

	    pause();
	    // Open the last Lecture unit
	    session().mouseOver(
		    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseDown(
		    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    session().mouseUp(
		    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
	    // session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
	    pause();

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

		//Add message to log file
		logFile(PEDAGOGICAL_TEST, CT_028, PASSED);
	}

	//Now, addDocument doesn't work on IE
	String newText12 = timeStamp();	
	if (inFireFox()) {	
		// ---------------------------------------------------------------------------//
		// Add Document in Lecture Unit //
		// ---------------------------------------------------------------------------//
		// Open Lectures Section
		openOrganisationSection();
	
		// Open the last Lecture unit
		openSeanceSection(newText2);
		pause();	
		
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
			newText12);
	
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
		    session().type(
			    "uploadFormElement",
			    "C:\\Documents and Setti"
				    + "ngs\\clihec3\\Local Settings\\Temporary Int"
				    + "ernet Files\\"
				    + "Content.IE5\\K0F6YKYM\\osyl-src[1].zip");
		    // We select randomly the rights field
		    String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
		    String newText8 = getRandomOption(xpathRole4);
		    session().select(xpathRole4, newText8);
		    pause();
		    // Close window
		    session().click("//tr[5]/td/table/tbody/tr/td/button");
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
		    session()
			    .type(
				    "//input[@class=\"gwt-FileUpload\"]",
				    "C:\\"
					    + "Documents and Settings\\"
					    + "clihec3\\Local Settings\\Temporary Internet Files\\"
					    + "Content.IE5\\K0F6YKYM\\powerpoint[1].ppt");
		    // We select randomly the rights field
		    String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
		    String newText8 = getRandomOption(xpathRole4);
		    session().select(xpathRole4, newText8);
		    pause();
		    // Close window
		    session().click("//tr[5]/td/table/tbody/tr/td/button");
		    pause();
	
		}/*
		  * else { session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
		  * session().focus("//input[@class=\"gwt-FileUpload\"]"); }
		  */
	
		pause();
	
		// Select file in browser window
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
	
		    // Open Lectures Section
		    openOrganisationSection();
		    pause();
		    // Open the last Lecture unit
		    session().mouseOver(
			    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		    session().mouseDown(
			    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		    session().mouseUp(
			    "//tr[" + Val + "]/td/table/tbody/tr/td[1]/div/div");
		    // session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
		    pause();
	
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
		    
			//Add message to log file		    
			logFile(PEDAGOGICAL_TEST, CT_0112, PASSED);			
		}
		else {
		log("addDocument is denied on IE because it doesn't support gwt-FileUpload"); //Close Overview
		}
	}	

	//Now, addDocument is only do for FF, it doesn't work with IE
	if (inFireFox()) {	
		
		// ---------------------------------------------------------------------------//
		// Add, Modify and delete Document in Lecture Unit //
		// ---------------------------------------------------------------------------//
		// Open Lectures Section
		openOrganisationSection();
	
		// Open the last Lecture unit
		openSeanceSection(newText2);
		pause();
	
		// Add new document
		clickAddItem("addDocument");
	
		// We open Document resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	
		// We type the clickable text
		session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]",
			newText12);
	
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
		    session().type(
			    "uploadFormElement",
			    "C:\\Documents and Setti"
				    + "ngs\\clihec3\\Local Settings\\Temporary Int"
				    + "ernet Files\\"
				    + "Content.IE5\\K0F6YKYM\\osyl-src[1].zip");
		    // We select randomly the rights field
		    String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
		    String newText8 = getRandomOption(xpathRole4);
		    session().select(xpathRole4, newText8);
		    pause();
		    // Close window
		    session().click("//tr[5]/td/table/tbody/tr/td/button");
		    pause();
	
		}/*
		  * else { session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
		  * session().focus("//input[@class=\"gwt-FileUpload\"]");
		  * session().getCursorPosition("//input[@class=\"gwt-FileUpload\ "]");
		  * }
		  */
	
		// Select file in browser window
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

		//Add message to log file
		logFile(PEDAGOGICAL_TEST, CT_013, PASSED);	

		// We delete new added docuement
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[2]/button");
		pause();
	
		session()
			.click(
				"//tr[2]/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
		pause();		
		log("Document deleted");
		//Add message to log file
		logFile(PEDAGOGICAL_TEST, CT_020, PASSED);			
		pause();		
		// Save modifications
		saveCourseOutline();
		pause();
	}	
	else {
		log("addDocument is denied on IE because it doesn't support gwt-FileUpload"); //Close Overview
	}

	// ---------------------------------------------------------------------------//
	// Add Citation in Lecture Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section
	openOrganisationSection();

	// Click last Lecture
	openSeanceSection(newText2);
	pause();	

	// Add new Citation
	clickAddItem("addBiblioResource");

	// open Citation resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We select attendee on dissemination level
	//session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
	//	"index=0");

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
		session().click("//tr[2]/td/select");
		session().select("//tr[2]/td/select", "index=1");
		session().doubleClick("//tr[2]/td/select/option");
		log("New elements is selected...");			
	    session().mouseOver(locator);		
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");		
	}	
	log("The format to fill is ready to open...");
	
	// Fill the mandatory fields
	session().select("//select[@name='cipvalues']", "label=Article");
	String Titre = "Titre" + timeStamp();
	session().type("//tr[9]/td/table/tbody/tr/td[3]/input", Titre);	 
	String Auteur = "Auteur" + timeStamp();
	session().type("//tr[10]/td/table/tbody/tr/td[3]/input", Auteur);
	String Annee = "Annee" + timeStamp();
	session().type("//tr[11]/td/table/tbody/tr/td[3]/input", Annee);
	String Periodique = "Periodique" + timeStamp();
	session().type("//tr[14]/td/table/tbody/tr/td[3]/input", Periodique);
	String Volume = "Volume" + timeStamp();
	session().type("//tr[16]/td/table/tbody/tr/td[3]/input", Volume);
	String Numero = "Numero" + timeStamp();
	session().type("//tr[16]/td/table/tbody/tr/td[6]/input", Numero);
	String Pages = "Pages" + timeStamp();
	session().type("//tr[18]/td/table/tbody/tr/td[3]/input", Pages);
	// Close Window
	session().click("//tr[22]/td/table/tbody/tr/td/button");
	pause();
	pause();
	// Select first resource in browser window
	session().focus("//option[@value=' (REF)   " + Titre + "']");
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
		"value= " + "(REF)   " + Titre);
	session().mouseOver("//option[@value=' (REF)   " + Titre + "']");
	session().click("//option[@value=' (REF)   " + Titre + "']");

	// Close Editor
	session().click("//td/table/tbody/tr/td[1]/button");

	//Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_104, PASSED);	

	// Save modifications
	saveCourseOutline();
	pause();

	// ---------------------------------------------------------------------------//
	// Switch two Lectures //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section
	openOrganisationSection();
	//openSeanceSection(newText2);	
	//pause();

	// We switch the 1st and 2nd assessment
	//it needs an update

	// ---------------------------------------------------------------------------//
	// Delete Lecture Unit //
	// ---------------------------------------------------------------------------//
	int Val2 = Val;
	session().click("//tr[" + Val2 + "]/td/table/tbody/tr/td[2]/div/table[2]/tbody/tr/td[2]/button");
	session().click("//tr[2]/td/table/tbody/tr/td/button");	
	log("Lecture deleted...");

	//Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_106, PASSED);	
	
	// Save modifications
	saveCourseOutline();
	pause();
	// Log out
	logOut();
	log("==============================");	
	log("TestAddSeance: test complete");
	log("==============================");	
    }
    
    private void openSeanceSection(String nameSeance) {
    	// click on Seances Section
        pause();
    	if (inFireFox()) {
    	    session().mouseDown(
    		    "//div[@class=\"gwt-TreeItem\"]/div/"
    			    + "div[contains(text(),'" + nameSeance + "')]");	    
    	} else {
    		String imageLocator = "//div[contains(text(),'" + nameSeance + "')]";			
    		session().mouseDownAt(imageLocator, "10,10");
    		session().mouseUpAt(imageLocator, "10,10");	    
    	}
        pause();
        }    
}
