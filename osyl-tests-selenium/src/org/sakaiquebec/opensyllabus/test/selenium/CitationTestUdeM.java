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

public class CitationTestUdeM extends AbstractOSYLTest{
	
	private static final String MY_CITATION_BOOK = "Livre";
	private static final String MY_CITATION_ARTICLE = "Article";
	private static final String MY_CITATION_OTHERS = "Autre";
	private static final String MY_CITATION_REPORTS = "Rapport";
	private static final String MY_CITATION_ACTS = "Actes de conférence";	
	
	@Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a contact resource, edit it and save the changes")
	@Parameters( { "webSite" })
	public void TestAddPeriode(String webSite) throws Exception {
	
		
		// We log in to create a site
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
		addCitationForUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForUnit();
/*		
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
		addCitationForUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForUnit();

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
		addCitationForUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForUnit();
		
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
		addCitationForUnit();

		// We add a first Assessment Unit
		clickAddItem("addBiblioResource");
		pause();		

		// Add citation for assessment unit
		addCitationForUnit();
*/
		// We delete Period 1
		/*
		 * int Val1 = Val+1;
		 * session().click("//tr["+Val1+"]/td/table/tbody/tr/td[2]/div/table[2]"
		 * + "/tbody/tr/td[2]/button");
		 * 
		 * session().click("//tr[2]/td/table/tbody/tr/td/button");
		 */

		// Save modifications
		//saveCourseOutline();
		//pause();

		// Log out
		session().selectFrame("relative=parent");
		logOut();
		log("============================");		// We log in
		logInAsAdmin(webSite);
		try {
			goToSite();
		} catch (IllegalStateException e) {
			createTestSite();
			goToSite();
		}
		waitForOSYL();
		log("testCitation: test complete");
		log("============================");

	}

	private int getResourceCount() {
		return session().getXpathCount(
				"//div[@class=\"Osyl-UnitView-ResPanel\"]").intValue();

	}
       	
    /**
     * Shortcut for addCitation on Assessment-Information Unit and others
     * @throws Exception 
     */
	public void addCitationForUnit() throws Exception {
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
		pause();

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
		
		//session().type("//tr[8]/td/table/tbody/tr/td/select", titre);
		// We choose randomly a Rubric
		String selectedMaterial = getRandomOption("//select[@name=\"cipvalues\"]");
		log("Selecting material [" + selectedMaterial + "]");
		session().select("//select[@name=\"cipvalues\"]",
				"label=" + selectedMaterial);

		//-----------------------------------------------------------------------
		// Fill the necessary fields for validation
		Random randomNumber = new Random();
		String token = Long.toString(Math.abs(randomNumber.nextLong()), 10);
		//String titre = "Titre_" + token;
		
		if (selectedMaterial.equals(MY_CITATION_BOOK)
				|| selectedMaterial.equals(MY_CITATION_REPORTS)) {
			String auteur = "Auteur_" + token;
			String annee = "19" + token.substring(2);
			String isbn = "101010" + token.substring(1,4);
			session().click("//tr[10]/td/table/tbody/tr/td[3]/input");
			//session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
			session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);

		} else if (selectedMaterial.equals(MY_CITATION_ARTICLE)) {
			String auteur = "Auteur_" + token;
			String periodique = "Periodique_" + token;
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String volume = token.substring(4);
			String numero = "Numero_" + token;
			String pages = token.substring(1,3);
			String issn = "1110" + token.substring(1,4);
			String doi = token.substring(3);
			session().click("//tr[10]/td/table/tbody/tr/td[3]/input");
			//session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
			
		} else if (selectedMaterial.equals(MY_CITATION_OTHERS)) {
			String link = "http://fr.wikipedia.org/wiki/Informatique";			
			session().click("//tr[9]/td/table/tbody/tr[3]/td/textarea");
			//session().type("//tr[9]/td/table/tbody/tr[3]/td/textarea", titre);

		} else if (selectedMaterial.equals(MY_CITATION_ACTS)) {
			String auteur = "Auteur_" + token;
			String annee = "19" + token.substring(2);			
			String conference = "Conférence_" + token;
			String volume = token.substring(4);
			String pages = token.substring(1,3);
			session().click("//tr[10]/td/table/tbody/tr/td[3]/input");
			//session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
			
		}

		//-----------------------------------------------------------------------
		// Check if Opensyllabus displays a message error when the user click OK
		// without fill in the fields First Name, Last Name and Title.

		session().click("//tr[22]/td/table/tbody/tr/td/button");
		String Erreur1 = "Erreur";
		if (!session().isTextPresent(newText1)) {
			logAndFail("Expected to see text [" + newText1
					+ "] after text edition");
		}

		// Fill the necessary fields
		randomNumber = new Random();
		token = Long.toString(Math.abs(randomNumber.nextLong()), 10);
		String titre = "Titre_" + token;
		
		if (selectedMaterial.equals(MY_CITATION_BOOK)
				|| selectedMaterial.equals(MY_CITATION_REPORTS)) {
			String auteur = "Auteur_" + token;
			String annee = "19" + token.substring(2);
			String isbn = "101010" + token.substring(1,4);
			session().click("//tr[10]/td/table/tbody/tr/td[3]/input");
			session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
			session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
			session().type("//tr[12]/td/table/tbody/tr/td[3]/input", annee);
			session().type("//tr[19]/td/table/tbody/tr/td[3]/input", isbn);

		} else if (selectedMaterial.equals(MY_CITATION_ARTICLE)) {
			String auteur = "Auteur_" + token;
			String periodique = "Periodique_" + token;
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			String volume = token.substring(4);
			String numero = "Numero_" + token;
			String pages = token.substring(1,3);
			String issn = "1110" + token.substring(1,4);
			String doi = token.substring(3);
			session().click("//tr[10]/td/table/tbody/tr/td[3]/input");
			session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
			session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
			session()
					.type("//tr[13]/td/table/tbody/tr/td[3]/input", periodique);
			session().type("//tr[15]/td/table/tbody/tr/td[3]/input",
					formatter.format(date));
			session().type("//tr[16]/td/table/tbody/tr/td[3]/input", volume);
			session().type("//tr[16]/td/table/tbody/tr/td[6]/input", numero);
			session().type("//tr[18]/td/table/tbody/tr/td[3]/input", pages);
			session().type("//tr[19]/td/table/tbody/tr/td[3]/input", issn);
			session().type("//tr[20]/td/table/tbody/tr/td[3]/input", doi);

		} else if (selectedMaterial.equals(MY_CITATION_OTHERS)) {
			String link = "http://fr.wikipedia.org/wiki/Informatique";			
			session().click("//tr[9]/td/table/tbody/tr[3]/td/textarea");
			session().type("//tr[9]/td/table/tbody/tr[3]/td/textarea", titre);
			session().type("//tr[21]/td/table/tbody/tr[3]/td/input", link);

		} else if (selectedMaterial.equals(MY_CITATION_ACTS)) {
			String auteur = "Auteur_" + token;
			String annee = "19" + token.substring(2);			
			String conference = "Conférence_" + token;
			String volume = token.substring(4);
			String pages = token.substring(1,3);
			session().click("//tr[10]/td/table/tbody/tr/td[3]/input");
			session().type("//tr[10]/td/table/tbody/tr/td[3]/input", titre);
			session().type("//tr[11]/td/table/tbody/tr/td[3]/input", auteur);
			session().type("//tr[12]/td/table/tbody/tr/td[3]/input", annee);
			session().type("//tr[14]/td/table/tbody/tr/td[3]/input", conference);
			session().type("//tr[17]/td/table/tbody/tr/td[3]/input", volume);
			session().type("//tr[18]/td/table/tbody/tr/td[3]/input", pages);
		}
		
		pause();
		// Close Window
		session().click("//tr[22]/td/table/tbody/tr/td/button");
		pause();

		// Select first resource in browser window
		session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
				"value= " + "(REF)   " + titre);
		session().focus("//option[@value=' (REF)   " + titre + "']");
		session().mouseOver("//option[@value=' (REF)   " + titre + "']");
		session().click("//option[@value=' (REF)   " + titre + "']");

		pause();
		
		// Close Editor
		session().click("//td/table/tbody/tr/td[1]/button");
		
		// Save modifications
		saveCourseOutline();
		pause();

	}       	
	
}
