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

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * Tests the addition of a contact-info resource. The exact steps are: log in
 * as admin, creates a new site if needed, load it (OpenSyllabus is the first
 * and only page), enters in the contact info page, click Contact in the Add
 * menu, open the editor, check that we get errors when trying to close the
 * editor without completing the required fields, fill in those fields,
 * change the rubric (randomly) click OK, check the text is here, check the
 * rubric is visible, click save, log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class ContactInfoTest extends AbstractOSYLTest {

    @Test(groups = "OSYL-Suite", description =
	"OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void testAddContactInfo(String webSite) throws Exception {
	// We log in
	logInAsAdmin(webSite);
	try {
	    goToSite();
	} catch (IllegalStateException e) {
	    createTestSite();
	    goToSite();
	}
	waitForOSYL();
	
	//Cliquer sur la page Coordonnées
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td/div[contains(text(),'Coordo')]");

	
	//Ajouter coordonnée
	clickAddItem("addPerson");
	
	//Editer coordonnée
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	pause();
	
	//Verifier si le message d'erreur s'affiche qd on clique OK sans remplir les champs
	//Nom, Prenom et Titre.
	session().click("//td/table/tbody/tr/td[1]/button");
	String Erreur1 = "Missing key: ResProxContactInfoView_TitleMandatory" ;
	if (!session().isTextPresent(Erreur1)) {
	    logAndFail("Expected to see text [" + Erreur1 
		    + "] after text edition");
		}
	
	pause();
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	
	//Verifier si le message d'erreur s'affiche qd on clique OK sans remplir les champs
	//Nom et Prenom 
	String newText = "Professeur titulaire" ;
	session().select("//select[@class=\"Osyl-ContactInfo-ListBox\"]", newText );
	
	session().click("//td/table/tbody/tr/td[1]/button");
	String Erreur2 = "Le nom de famille de la coordonnée ne peut pas être vide" ;
	if (!session().isTextPresent(Erreur2)) {
	    logAndFail("Expected to see text [" + Erreur2 
		    + "] after text edition");
		}
	
	pause();
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	
	//Verifier si le message d'erreur s'affiche qd on clique OK sans remplir les champs
	//Nom 
	
	String newText1 = "XXXXXXXX";
	session().type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);
	
	session().click("//td/table/tbody/tr/td[1]/button");
	String Erreur3 = "Le nom de famille de la coordonnée ne peut pas être vide" ;
	if (!session().isTextPresent(Erreur3)) {
	    logAndFail("Expected to see text [" + Erreur3 
		    + "] after text edition");
		}
	
	pause();
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
		
	//Selectionner les champs necessaires
	session().select("//select[@class=\"Osyl-ContactInfo-ListBox\"]", newText );
	
	//Selectionner une rubrique
	String selectedRubric = getRandomRubric();
	log("Selecting rubric [" + selectedRubric + "]");
	changeRubric(selectedRubric);

	
	//Enterer les informations obligatoires (nom, prenom et titres) 
	
	session().type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);
	
	String newText2 = "YYYYYYYY";
	session().type("//input[@title=\"Nom:\"]", newText2);
	pause();
	
	session().selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
	String newText3 = "Lundi AM et Mardi PM";
	session().type("//html/body", newText3);
	
	//Sauvegarder localement
	session().selectFrame("relative=parent");
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//Verifier la creation de la coordonnée
	if (!session().isTextPresent(newText1)) {
	    logAndFail("Expected to see text [" + newText1 
		    + "] after text edition");
	}
	if (!session().isTextPresent(newText2)) {
	    logAndFail("Expected to see text [" + newText2 
		    + "] after text edition");
	}
	if (!session().isTextPresent(newText3)) {
	    logAndFail("Expected to see text [" + newText3 
		    + "] after text edition");
	}

	// check if the new rubric is visible.
	if (!session().isTextPresent(selectedRubric)) {
	    logAndFail("Expected to see rubric [" + selectedRubric
		    + "] after text edition");
	}
	log("OK: Selected rubric is visible");

	//Sauvegarder sur le serveur
	saveCourseOutline();
	
	//Faire un aperçu
	
	//Log out
	
	
	

	

	session().selectFrame("relative=parent");
	logOut();
	log("testAddContactInfo: test complete");
    } // testAddContactInfo
    
    
}
