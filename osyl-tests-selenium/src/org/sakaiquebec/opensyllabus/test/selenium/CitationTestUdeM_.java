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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * Tests all the features in the section Assessment-Information, Pedagogic  
 * Resources, Bibliography, Library and The exact steps are: 
 * log in as admin, creates a new site if needed, load it (OpenSyllabus is 
 * the first page), enters in those sections, add two citations for each one,
 * name the last one, edit the last citation, rename it, delete the last one, 
 * click save, and log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:bouchra.laabissi@hec.ca">Bouchra Laabissi</a>
 * @author <a href="mailto:alejandro.hernandez@umontreal.">Alejandro Hernandez</a> * 
 */

public class CitationTestUdeM_ extends AbstractOSYLTest{
    
	@Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a contact resource, edit it and save the changes")
	@Parameters( { "webSite" })
	public void TestAddPeriode(String webSite) throws Exception {
		// We log in
		logInAsAdmin(webSite);
		try {
			goToSite();
		} catch (IllegalStateException e) {
			createTestSite();
			goToSite();
		}
		waitForOSYL();

		//-------------------------------------------------------------------------------
		// Open Assessment Section
		session()
				.mouseDown(
						"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
								+ "td/div[contains(text(),'Évaluation des apprentissages')]");

		// Open Information Unit
		session()
				.mouseDown(
						"//html/body/table/tbody/tr[2]/td/div/div/div/table/tbody/tr[2]/td[2]/div/table/tbody/tr[2]/td/div/div[2]/div/div[5]/div/div");

		// We keep track of how many resources are showing
		int resNb = getResourceCount() - 1;
		log("We start with " + resNb + " resources");

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		
		
		// Add citation for assessment unit
		addCitationForAssessmentUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForAssessmentUnit();
		
		//-------------------------------------------------------------------------------		
		// Open Resources Section
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Ressource pédagogique')]");

		// We keep track of how many resources are showing
		resNb = getResourceCount() - 1;
		log("We start with " + resNb + " resources");

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		
		
		// Add citation for assessment unit
		addCitationForAssessmentUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForAssessmentUnit();

		//-------------------------------------------------------------------------------		
		// Open Bibliography Section
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Bibliographie')]");

		// We keep track of how many resources are showing
		resNb = getResourceCount() - 1;
		log("We start with " + resNb + " resources");

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();

		// Add citation for assessment unit
		addCitationForAssessmentUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForAssessmentUnit();
		
		//-------------------------------------------------------------------------------		
		// Open Library Section
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Bibliothèque')]");

		// We keep track of how many resources are showing
		resNb = getResourceCount() - 1;
		log("We start with " + resNb + " resources");

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();

		// Add citation for assessment unit
		addCitationForAssessmentUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForAssessmentUnit();

		// We delete Period 1
		/*
		 * int Val1 = Val+1;
		 * session().click("//tr["+Val1+"]/td/table/tbody/tr/td[2]/div/table[2]"
		 * + "/tbody/tr/td[2]/button");
		 * 
		 * session().click("//tr[2]/td/table/tbody/tr/td/button");
		 */

		// Save modifications
		saveCourseOutline();
		pause();

		// Log out
		session().selectFrame("relative=parent");
		logOut();
		log("============================");
		log("testCitation: test complete");
		log("============================");

	}
       	
    /**
     * Shortcut for addCitation on Assessment-Information Unit and others
     * @throws Exception 
     */
	public void addCitationForAssessmentUnit() throws Exception {
		// open Citation resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

		// We choose randomly a Rubric
		String selectedRubric4 = getRandomRubric();
		log("Selecting rubric [" + selectedRubric4 + "]");
		changeRubric(selectedRubric4);

		// Create a new citation list
		String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		session().answerOnNextPrompt("NewListe" + newText1);
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}
		pause();
		assertTrue(session().isPromptPresent());

		// Open Citation list
		session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
		session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "index=0");
		session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");

		// Open form to upload a first Citation (Book)
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}

		// Fill the necessary fields
		Random r1 = new Random();
		String token = Long.toString(Math.abs(r1.nextLong()), 10);
		String titre = "Titre_" + token;
		session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
		String auteur = "Auteur_" + token;
		session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
		String annee = "Annee_" + token;
		session().type("//tr[12]/td/table/tbody/tr/td[3]/input", annee);
		String isbn = "ISBN_" + token;
		session().type("//tr[19]/td/table/tbody/tr/td[3]/input", isbn);
		pause();

		// Close Window
		session().click("//tr[22]/td/table/tbody/tr/td/button");
		pause();
		pause();

		// Select first resource in browser window
		session().focus("//option[@value=' (REF)   " + titre + "']");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
				"value= " + "(REF)   " + titre);
		session().mouseOver("//option[@value=' (REF)   " + titre + "']");
		session().click("//option[@value=' (REF)   " + titre + "']");

		// Close Editor
		session().click("//td/table/tbody/tr/td[1]/button");
		
		// Save modifications
		saveCourseOutline();
		pause();

	}
       	
	/**
	 * Shortcut for addCitation on Pedagogic Resources Unit
	 * @throws Exception 
	 */
	public void addCitationForPedagogicResourcesUnit() throws Exception {
		// Open Lectures Section
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Ressource pédagogique')]");

		// We keep track of how many resources are showing
		int resNb = getResourceCount() - 1;
		log("We start with " + resNb + " resources");

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();

		// open Citation resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

		// We choose randomly a Rubric
		String selectedRubric4 = getRandomRubric();
		log("Selecting rubric [" + selectedRubric4 + "]");
		changeRubric(selectedRubric4);

		// Create a new citation list
		String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		session().answerOnNextPrompt("NewListe" + newText1);
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}
		pause();
		assertTrue(session().isPromptPresent());

		// Open Citation list
		session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
		session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "index=0");
		session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");

		// Open form to upload a first Citation (Book)
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}

		// Fill the necessary fields
		Random r2 = new Random();
		String token = Long.toString(Math.abs(r2.nextLong()), 10);
		String titre = "Titre_" + token;		
		session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
		String auteur = "Auteur_" + token;
		session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
		String annee = "Annee_" + token;
		session().type("//tr[12]/td/table/tbody/tr/td[3]/input", annee);
		String isbn = "ISBN_" + token;
		session().type("//tr[19]/td/table/tbody/tr/td[3]/input", isbn);
		pause();

		// Close Window
		session().click("//tr[22]/td/table/tbody/tr/td/button");
		pause();
		pause();

		// Select first resource in browser window
		session().focus("//option[@value=' (REF)   " + titre + "']");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
				"value= " + "(REF)   " + titre);
		session().mouseOver("//option[@value=' (REF)   " + titre + "']");
		session().click("//option[@value=' (REF)   " + titre + "']");

		// Close Editor
		session().click("//td/table/tbody/tr/td[1]/button");

		// Save modifications
		saveCourseOutline();
		pause();

	}
 
	/**
	 * Shortcut for addCitation on Bibliography Unit
	 * @throws Exception 
	 */
	public void addCitationForBibliographyUnit() throws Exception {

		// open Citation resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

		// We choose randomly a Rubric
		String selectedRubric4 = getRandomRubric();
		log("Selecting rubric [" + selectedRubric4 + "]");
		changeRubric(selectedRubric4);

		// Create a new citation list
		String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		session().answerOnNextPrompt("NewListe" + newText1);
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}
		pause();
		assertTrue(session().isPromptPresent());

		// Open Citation list
		session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
		session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "index=0");
		session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");

		// Open form to upload a first Citation (Book)
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}

		// Fill the necessary fields
		Random r3 = new Random();
		String token = Long.toString(Math.abs(r3.nextLong()), 10);
		String titre = "Titre_" + token;
		session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
		String auteur = "Auteur_" + token;
		session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
		String annee = "Annee_" + token;
		session().type("//tr[12]/td/table/tbody/tr/td[3]/input", annee);
		String isbn = "ISBN_" + token;
		session().type("//tr[19]/td/table/tbody/tr/td[3]/input", isbn);
		pause();

		// Close Window
		session().click("//tr[22]/td/table/tbody/tr/td/button");
		pause();
		pause();

		// Select first resource in browser window
		session().focus("//option[@value=' (REF)   " + titre + "']");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
				"value= " + "(REF)   " + titre);
		session().mouseOver("//option[@value=' (REF)   " + titre + "']");
		session().click("//option[@value=' (REF)   " + titre + "']");

		// Close Editor
		session().click("//td/table/tbody/tr/td[1]/button");

		// Save modifications
		saveCourseOutline();
		pause();

	}

	/**
	 * Shortcut for addCitation on Library Unit
	 * @throws Exception 
	 */
	public void addCitationForLibraryUnit() throws Exception {
		// Open Lectures Section
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Bibliothèque')]");

		// We keep track of how many resources are showing
		int resNb = getResourceCount() - 1;
		log("We start with " + resNb + " resources");

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();

		// open Citation resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

		// We choose randomly a Rubric
		String selectedRubric4 = getRandomRubric();
		log("Selecting rubric [" + selectedRubric4 + "]");
		changeRubric(selectedRubric4);

		// Create a new citation list
		String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		session().answerOnNextPrompt("NewListe" + newText1);
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}
		pause();
		assertTrue(session().isPromptPresent());

		// Open Citation list
		session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
		session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select", "index=0");
		session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");

		// Open form to upload a first Citation (Book)
		if (inFireFox()) {
			session().mouseOver("//td[3]/div/img");
			session().mouseDown("//td[3]/div/img");
			session().mouseUp("//td[3]/div/img");
		} else {
			session().keyPress("//td[3]/table/tbody/tr/td[3]/div", "\r");
		}

		// Fill the necessary fields
		Random r4 = new Random();
		String token = Long.toString(Math.abs(r4.nextLong()), 10);
		String titre = "Titre_" + token;
		session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
		String auteur = "Auteur_" + token;
		session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
		String annee = "Annee_" + token;
		session().type("//tr[12]/td/table/tbody/tr/td[3]/input", annee);
		String isbn = "ISBN_" + token;
		session().type("//tr[19]/td/table/tbody/tr/td[3]/input", isbn);
		pause();

		// Close Window
		session().click("//tr[22]/td/table/tbody/tr/td/button");
		pause();
		pause();

		// Select first resource in browser window
		session().focus("//option[@value=' (REF)   " + titre + "']");
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
				"value= " + "(REF)   " + titre);
		session().mouseOver("//option[@value=' (REF)   " + titre + "']");
		session().click("//option[@value=' (REF)   " + titre + "']");

		// Close Editor
		session().click("//td/table/tbody/tr/td[1]/button");

		// Save modifications
		saveCourseOutline();
		pause();

	}
	
}