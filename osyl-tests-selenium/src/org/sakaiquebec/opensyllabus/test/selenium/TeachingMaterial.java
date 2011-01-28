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
public class TeachingMaterial extends AbstractOSYLTest {

    @Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void TeachingMaterialTest(String webSite) throws Exception {
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
	// Add Text //
	// ---------------------------------------------------------------------------//

	// Open Teaching Material Section
	openTeachingMaterialSection();
	pause();

	String newText9 =
	    "this is a text resource typed by "
		    + "selenium, hope it works and you see it. Added on "
		    + timeStamp() + " in Firefox";
	addText(newText9,LEVEL_ATTENDEE);

	// Save modifications
	saveCourseOutline();
	pause();

	// ---------------------------------------------------------------------------//
	// Add Hyperlink //
	// ---------------------------------------------------------------------------//

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
	String newText10 = "http://webmail.hec.ca/";
	session().type("//tr[2]/td[2]/input", newText10);

	// We click OK to close editor
	session().click("//td/table/tbody/tr/td[1]/button");

	// We click URL to test
	session().click("link=" + newText11);

	// Save modifications
	saveCourseOutline();
	pause();

	// ---------------------------------------------------------------------------//
	// Add, Modify and delete Hyperlink //
	// ---------------------------------------------------------------------------//

	// Add Hyperlink
	clickAddItem("addURL");

	// We edit the new Hyperlink rubric
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We type the clickable text
	session().type("//td[2]/input", newText11);
	pause();

	// We type the URL link
	session().type("//tr[2]/td[2]/input", newText10);
	pause();

	// We click OK to close editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();

	// We delete new hyperlink
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[2]/button");
	pause();

	session()
		.click(
			"//tr[2]/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	pause();
	log("Hyperlink deleted");
	pause();

	// Save modifications
	saveCourseOutline();
	pause();
	// ---------------------------------------------------------------------------//
	// Add Document //
	// ---------------------------------------------------------------------------//
	if (inFireFox()) {	
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
		String newText12 = timeStamp();
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
	}
	// ---------------------------------------------------------------------------//
	// Add Citation //
	// ---------------------------------------------------------------------------//

	// Add new Citation
	clickAddItem("addBiblioResource");

	// open Citation resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We choose randomly a Rubric
	String selectedRubric4 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric4 + "]");
	changeRubric(selectedRubric4);

	// Create a new citation list
	String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	session().answerOnNextPrompt("NewListe" + newText1);
	log("NewListe" + newText1 + " is created...");
	
	if (inFireFox()) {
/*	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");*/
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
	} else {
	    //session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
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
/*	pause();
	assertTrue(session().isPromptPresent());*/

	// Open Citation list
/*	session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
	session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "index=0");
	session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");*/

	// Open form to upload a first Citation (Book)
/*	if (inFireFox()) {
	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");
	} else {
	    session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
	}*/
	log("Validation de données vides...");	
	session().click("//tr[22]/td/table/tbody/tr/td[1]/button");
	if (!session().isTextPresent(newText1)) {
		logAndFail("Expected to see text [" + newText1
				+ "] after text edition");
	}
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	// Fill the necessary fields
	String Titre = "Titre" + timeStamp();
	session().type("//tr[9]/td/table/tbody/tr/td[3]/input", Titre);
	String Auteur = "Auteur" + timeStamp();
	session().type("//tr[10]/td/table/tbody/tr/td[3]/input", Auteur);
	String Annee = "Annee" + timeStamp();
	session().type("//tr[11]/td/table/tbody/tr/td[3]/input", Annee);
	String Editeur = "Editeur" + timeStamp();
	session().type("//tr[12]/td/table/tbody/tr/td[3]/input", Editeur);
	String Lieu = "Lieu" + timeStamp();
	session().type("//tr[13]/td/table/tbody/tr/td[3]/input", Lieu);
	String ISBN = "ISBN" + timeStamp();
	session().type("//tr[19]/td/table/tbody/tr/td[3]/input", ISBN);

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

	// Save modifications
	saveCourseOutline();
	pause();

	// ---------------------------------------------------------------------------//
	// Add, Modify and delete Citation //
	// ---------------------------------------------------------------------------//

	// Add new Citation
	clickAddItem("addBiblioResource");

	// open Citation resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// Create a new citation list
	session().answerOnNextPrompt("NewListe" + newText1);
	if (inFireFox()) {
		/*	    session().mouseOver("//td[3]/div/img");
			    session().mouseDown("//td[3]/div/img");
			    session().mouseUp("//td[3]/div/img");*/
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
			} else {
			    //session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
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
	//pause();
	//assertTrue(session().isPromptPresent());

	// Open Citation list
/*	session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
	session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "index=0");
	session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");*/

	// Open form to upload a first Citation (Book)
/*	if (inFireFox()) {
	    session().mouseOver("//td[3]/div/img");
	    session().mouseDown("//td[3]/div/img");
	    session().mouseUp("//td[3]/div/img");
	} else {
	    session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
	}*/

	// Fill the necessary fields
	session().type("//tr[9]/td/table/tbody/tr/td[3]/input", Titre);
	session().type("//tr[10]/td/table/tbody/tr/td[3]/input", Auteur);
	session().type("//tr[11]/td/table/tbody/tr/td[3]/input", Annee);
	session().type("//tr[12]/td/table/tbody/tr/td[3]/input", Editeur);
	session().type("//tr[13]/td/table/tbody/tr/td[3]/input", Lieu);
	session().type("//tr[19]/td/table/tbody/tr/td[3]/input", ISBN);

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
	pause();

	// Close Editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();

	// We delete new citation
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[2]/button");
	pause();

	session()
		.click(
			"//tr[2]/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	pause();
	log("Citation deleted");
	pause();

	// Save modifications
	saveCourseOutline();
	pause();

	session().selectFrame("relative=parent");
	logOut();
	log("===================================");	
	log("TeachingMaterialTest: test complete");
	log("===================================");	
    } // TeachingMaterialTest

}