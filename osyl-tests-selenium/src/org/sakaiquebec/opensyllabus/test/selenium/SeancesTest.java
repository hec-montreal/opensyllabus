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
import java.io.File;

import org.sakaiquebec.opensyllabus.test.selenium.utils.PopupUtils;
import org.sakaiquebec.opensyllabus.test.selenium.utils.ResourceXpathHelper;
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
	
	/**
	 * Test started
	 */
	logStartTest();
	pause();
	
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
	
	waitForOSYL();
	logFile(OSYL_TEST, CT_069, PASSED);	
	
	try {

    	    /**
    	     * =========================================================================
    	     * Ajoute une seance.
    	     * Il peut avoir deja d'autres seances.
    	     * La nouvelle seance est la derniere dans la liste
    	     */
    	    SeanceData seance = addSeance("Last seance " + timeStamp());
    	    String seanceName = seance.getName();
    	    int seanceResourcePos = seance.getPosition();
    	
    	    /**
    	     * ==========================================================================
    	     * AddText
    	     */
    	    addText(seanceName, seanceResourcePos);
    
    	    addHyperlink(seanceName, seanceResourcePos);
    
    	    String seanceDescription = timeStamp();	
    	    //Now, addDocument doesn't work on IE
    	    if (inFireFox()) {	
    		addDocument(seanceName, seanceResourcePos, seanceDescription);
    	    }	
    
    	    //Now, addDocument is only do for FF, it doesn't work with IE
    	    if (inFireFox()) {
    	       addDocumentAndDelete(seanceName, seanceDescription);
    	    } else {
    	       log("addDocument is denied on IE because it doesn't support gwt-FileUpload"); // Close
    											  // Overview
    	    }
    	
    	    //	String seanceName = "Séance de cours";
    	    //	int seancePos = 2;
    
    	    addCitation(seanceName);
    
    	    openOrganisationSection();
    
    	    // ---------------------------------------------------------------------------//
    	    // Delete Lecture Unit //
    	    // ---------------------------------------------------------------------------//
    	    //session().click("//tr[" + seanceResourcePos + "]/td/table/tbody/tr/td[2]/div/table[2]/tbody/tr/td[2]/button");
    	    //session().click("//tr[2]/td/table/tbody/tr/td/button");
    	    
    	    session().click(ResourceXpathHelper.getButtonDelete(seanceResourcePos));
    	    session().click("//tr[2]/td/table/tbody/tr/td/button");
    	    log("Lecture deleted...");

    	    // Save modifications
    	    saveCourseOutline();
    	    pause();
    
    	    //Add message to log file
    	    logFile(PEDAGOGICAL_TEST, CT_106, PASSED);	
    	
    	
    	    // Print duration of test.
    	    logEndTest();
    	   
	} catch (Exception e) {
	    prettyLog("EXCEPTION: " + e.toString());
	} finally {
    	
    	    // Log out
    	    logOut();
	}
    }
    
    
    /**
     * 
     * @param seanceName1
     * @param seanceName
     * @throws Exception
     */

    private void addCitation(String seanceName) throws Exception {
	
	prettyLog("addCitation");
	
	// ---------------------------------------------------------------------------//
	// Add Citation in Lecture Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section
	openOrganisationSection();

	// Click last Lecture
	openSeanceSection(seanceName);
	pause(1000);	

	// Add new Citation
	clickAddItem("addBiblioResource");	

	// modify the Citation, (first resource on the list)
	String modifierButtonLocator = ResourceXpathHelper.getButtonModify(0); 
	session().click(modifierButtonLocator);

	// We select attendee on dissemination level
	//session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
	//	"index=0");

	// We choose randomly a Rubric
	String selectedRubric4 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric4 + "]");
	changeRubric(selectedRubric4);

	// Create a new citation list
	session().answerOnNextPrompt("NewListe" + seanceName);
	log("NewListe" + seanceName + " is created...");
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
		session().select("//tr[2]/td/select", "(LREF) NewListe" + seanceName);
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
	
	/**
	 * Presser 'Enregistrer' and Close Window
	 * Explication du xpath: \
	 *   'Chercher le 2eme div dont la classe contient 'popupContent', et prendre le premier bouton dont vous trouvez.\
	 *   (le premier bouton est Modifier, le suivant est Supprimer).	 *    
	 */
	session().click("xpath=((//tr[@class='popupMiddle'])[2]//button)[1]");
	//session().click("xpath=((//div[contains(@class,'popupContent')])[2]//button)[1]");
	
	
	//pause();
	pause();
	// Select first resource in browser window
	session().focus("//option[@value=' (REF)   " + Titre + "']");
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
		"value= " + "(REF)   " + Titre);
	session().mouseOver("//option[@value=' (REF)   " + Titre + "']");
	session().click("//option[@value=' (REF)   " + Titre + "']");

	// Close Editor
	session().click(PopupUtils.DocumentEditPopup.getButtonOk());
	//session().click("//td/table/tbody/tr/td[1]/button");

	//Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_104, PASSED);	

	// Save modifications
	saveCourseOutline();
	pause();
    }

    /**
     * Add a document to the seance and delete it.
     * @param seanceName
     * @param seanceDescription
     * @throws Exception
     */
    private void addDocumentAndDelete(String seanceName, String seanceDescription) throws Exception {

	prettyLog("addDocumentAndDelete");

	// ---------------------------------------------------------------------------//
	// Add, Modify and delete Document in Lecture Unit //
	// ---------------------------------------------------------------------------//
	// Open Lectures Section
	openOrganisationSection();

	// Open the Lecture unit
	openSeanceSection(seanceName);
	pause();

	// Add new document
	clickAddItem("addDocument");

	// We open Document editor (which is the first resource)
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	session().click(ResourceXpathHelper.getButtonModify(0));
	pause(2000);

	// We type the clickable text
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]",
	    seanceDescription);

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
	String currentFile = XLS_FILE;
	String documentFilePath = FILE_DIR + currentFile;// ZIP_FILE;
	assertFileExists(documentFilePath);
	session().type("uploadFormElement", documentFilePath);
	
	// We select randomly the rights field (eg. 'Je detiens le copyright')
	selectAtRandom("//form/table/tbody/tr[5]/td/select");

	// We select randomly the type of resources field (eg. 'Recueil de textes')
	selectAtRandom("//form/table/tbody/tr[7]/td/select");
	pause(1000);
	// Close window
	session().click("//button[contains(text(),'Enregistrer')]");
	// session().click("//tr[5]/td/table/tbody/tr/td/button");
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
	// //ERROR: Cette ressource est déjà utilisée dans le plan de cours
	// avec un type de ressource différent (Document de nature
	// pédagogique). Êtes-vous sûr de vouloir changer le type de cette
	// ressource ?
	// Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_013, PASSED);

	// We delete new added docuement
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[2]/button");
	pause();

	session()
	    .click("//tr[2]/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	pause();
	log("Document deleted");
	// Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_020, PASSED);
	pause();
	// Save modifications
	saveCourseOutline();
	pause();
    }
    
    /**
     * Add a document to seanceName.
     * 2 files are added to the document.
     * 
     * @param seanceName Seance to which we want to add document to.
     * @param seancePos
     * @param seanceDescription
     * @throws Exception
     */

    private void addDocument(String seanceName, int seancePos, String seanceDescription) throws Exception {

	prettyLog("addDocument");
	
	// ---------------------------------------------------------------------------//
	// Add Document in Lecture Unit //
	// ---------------------------------------------------------------------------//
	// Open Lectures Section
	openOrganisationSection();

	// Open the last Lecture unit
	openSeanceSection(seanceName);
	pause();	
	
	// Add new document
	clickAddItem("addDocument");

	// We open Document resource editor
	// modify the Document, which is the first on the list
	String modifierButtonLocator = ResourceXpathHelper.getButtonModify(0); 
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	session().click(modifierButtonLocator);

	// We choose randomly a Rubric
	String selectedRubric3 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric3 + "]");
	changeRubric(selectedRubric3);

	// We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select[@title]",
		"index=0");

	// We type the clickable text
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]",
		seanceDescription);

	// Open form to upload a first document
	if (inFireFox()) {
	    
	    // what does this do ?????

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

	   
	    /**
	     *  Choose file and close window.
	     *  Type the hard-coded file name we want.
	     *  Exemple: D:\OSYL-TEST-FICHIERS\osyl-src.zip
	     */
	    String documentFilePath = FILE_DIR + ZIP_FILE;
	    assertFileExists(documentFilePath);		    
	    session().type("uploadFormElement", documentFilePath);
	    
	    // We select randomly the rights field  (eg. 'Je detiens le copyright')
	    selectAtRandom("//form/table/tbody/tr[5]/td/select");
	    
	    // We select randomly the type of resources field (eg. 'Recueil de textes')
	    selectAtRandom("//form/table/tbody/tr[7]/td/select");
	    
	    pause(1000);
	    // Save and close window
	    session().click("//button[contains(text(),'Enregistrer')]");
	    //session().click("//tr[5]/td/table/tbody/tr/td/button");
	    pause();

	}
	
	
	// Open form to upload a second document
	if (inFireFox()) {

	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");

	    // Choose file and close window
	    String documentFilePath = FILE_DIR + PPT_FILE;
	    assertFileExists(documentFilePath);		    
	    session().type("//input[@class=\"gwt-FileUpload\"]", FILE_DIR + PPT_FILE);
	    
	    // We select randomly the rights field  (eg. 'Je detiens le copyright')
	    selectAtRandom("//form/table/tbody/tr[5]/td/select");
	    
	    // We select randomly the type of resources field (eg. 'Recueil de textes')
	    selectAtRandom("//form/table/tbody/tr[7]/td/select");
	    pause();
	    // Close window
	    session().click("//button[contains(text(),'Enregistrer')]");
	    //session().click("//tr[5]/td/table/tbody/tr/td/button");
	    pause();

	}

	pause();

	// Select file in browser window
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
		"value= (F" + ")   osyl-src_1_.zip");
	session().mouseOver("//option[@value=' (F)   osyl-src_1_.zip']");
	session().focus("//option[@value=' (F)   osyl-src_1_.zip']");
	session().click("//option[@value=' (F)   osyl-src_1_.zip']");
	pause();

	// Close Editor
	session().click("//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/tbody/tr/td[1]/button");

	// Save modifications
	saveCourseOutline();
	pause();

	if (inFireFox()) {

	    // Overview
		session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	
	    // Attendee Overview
	    session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	    pause();

	    // Open Lectures Section
	    openOrganisationSection();
	    pause();
	    
	    // Open the Lecture unit
	    String hrefLocator = ResourceXpathHelper.getItemHref(seancePos);
	    session().mouseOver(hrefLocator);
	    session().mouseDown(hrefLocator);
	    session().mouseUp(hrefLocator);
//	    session().mouseOver("//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseDown("//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseUp(  "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
	    pause();

	    if (!session().isTextPresent(selectedRubric3)) {
		logAndFail("Expected to see rubric [" + selectedRubric3
			+ "] after text edition");
	    }
	    log("OK: Selected rubric is visible");

	    if (!session().isTextPresent(seanceDescription)) {
		logAndFail("Expected to see rubric [" + seanceDescription + "] after text edition");
	    }
	    log("OK: Text is visible");

	    // Close Overview
	    session().click("//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	    pause();
	    
	    //Add message to log file		    
	    logFile(PEDAGOGICAL_TEST, CT_0112, PASSED);			
	
	} else {
	    log("addDocument is denied on IE because it doesn't support gwt-FileUpload"); //Close Overview
	}
    }

    
    /**
     * Add a text element to seance.
     * @param seanceName
     * @param seancePos
     * @throws Exception
     */
    private void addText(String seanceName, int seancePos) throws Exception {
	
	prettyLog("addText");
	
	// ---------------------------------------------------------------------------//
	// Add Text in the Lecture Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section	
	openOrganisationSection();
	
	// We edit the last Lecture
	openSeanceSection(seanceName);
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
	    String hrefLocator = ResourceXpathHelper.getItemHref(seancePos);
	    session().mouseOver(hrefLocator);
	    session().mouseDown(hrefLocator);
	    session().mouseUp(hrefLocator);
//	    // Open the Lecture unit
//	    session().mouseOver(
//		    "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseDown(
//		    "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseUp(
//		    "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    // session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
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
    }

    
    /**
     * Add a hyperlink to seance.
     *
     * @param seanceName
     * @param seancePos
     * @throws Exception
     */
    private void addHyperlink(String seanceName, int seancePos) throws Exception {
	
	prettyLog("addHyperlink");
	
	// ---------------------------------------------------------------------------//
	// Add Hyperlink in Lecture Unit //
	// ---------------------------------------------------------------------------//

	// Open Lectures Section
	openOrganisationSection();

	// Open the Lecture unit
	openSeanceSection(seanceName);
	pause();		

	// Add Hyperlink in the last Lecture Unit
	clickAddItem("addURL");

	// We edit the just-added Hyperlink rubric (the first)
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	session().click(ResourceXpathHelper.getButtonModify(0));

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

	// We click URL to testb (a new window will open at this point. You may close it manually!!)
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
	    String hrefLocator = ResourceXpathHelper.getItemHref(seancePos);
	    session().mouseOver(hrefLocator);
	    session().mouseDown(hrefLocator);
	    session().mouseUp(hrefLocator);
//	    session().mouseOver(
//		    "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseDown(
//		    "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
//	    session().mouseUp(
//		    "//tr[" + seancePos + "]/td/table/tbody/tr/td[1]/div/div");
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
    
    /**
     * Ajout d'une seance de cours (lecture en Anglais).
     * @return seance data.
     */
    private SeanceData addSeance(String seanceName) {
	
	prettyLog("addSeance");
	
	// ---------------------------------------------------------------------------//
	// Add Lecture //
	// ---------------------------------------------------------------------------//

	// Click on Organisation Section
	openOrganisationSection();
	pause();

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

	// We keep track of how many resources are showing
	//int resNb = getResourceCount() - 1;// -1 because there is an extra resource row.
	int nbResources = ResourceXpathHelper.getNbResource();
	log("We start with " + nbResources + " resources (which are Lectures here)");

	//int Position = resNb + 3;
	// We edit the last Lecture
	// (contrary to other resources, newly added Lectures are the last item in list)
	int seanceResourcePos = nbResources - 1;
	session().click(ResourceXpathHelper.getButtonModify(seanceResourcePos));
	pause();

	session().type("//tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/input", seanceName);

	// Click OK to close Editor
	session().click("//tr/td/table/tbody/tr/td/table/tbody/tr/td/button");
	pause();

	// Ensure the new name is visible
	if (!session().isTextPresent(seanceName)) {
	    //logAndFail("New lecture title not present");
	    log("New lecture title not present");
	} else {
	    log("New lecture title is present");				
	}
	
	/**
	 * Now we rename the lecture
	 */
	session().click(ResourceXpathHelper.getButtonModify(seanceResourcePos));
	pause();
			
	String newSeanceName = "Last seance renamed " + timeStamp();
	session().type("//tr[2]/td/table/tbody/tr/td/table/tbody/tr/td/input",newSeanceName); 
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if (!session().isTextPresent(newSeanceName)) {
	    logAndFail("New lecture title not present (inside the lecture)");
	    log("New lecture title not present (inside the lecture)");
	} else {
	    log("OK: Lecture renamed from inside");	
	}		

	//Add message to log file
	logFile(PEDAGOGICAL_TEST, CT_084, PASSED);
	
	return new SeanceData(newSeanceName, seanceResourcePos);
    }
    
     
    /**
     * Simply a class to keep a lecture seance to pass as function parameter.
     * 
     */
    private class SeanceData {
	private String name;
	private int position;
	public SeanceData(String name, int position) {
	    this.name = name;
	    this.position = position;
	}
	public String getName() {
	    return name;
	}
	public int getPosition() {
	    return position;
	}
    }
}
