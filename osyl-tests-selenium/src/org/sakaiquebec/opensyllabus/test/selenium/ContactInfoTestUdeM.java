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
public class ContactInfoTestUdeM extends AbstractOSYLTest {

	@Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a contact resource, edit it and save the changes")
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

		// Click Contact Information
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td/"
						+ "div[contains(text(),'Enseignant')]");

		// Add Contact Information
		clickAddItem("addPerson");

		// Edit Contact Information
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
		pause();

		// Check if Opensyllabus displays a message error when the user click OK
		// without fill in the fields First Name, Last Name and Title.

		session().click("//td/table/tbody/tr/td[1]/button");
		String Erreur1 = "Erreur";
		if (!session().isTextPresent(Erreur1)) {
			logAndFail("Expected to see text [" + Erreur1
					+ "] after text edition");
		}

		pause();
		session().click("//tr[2]/td/table/tbody/tr/td/button");
		pause();

		// Check if Opensyllabus displays a message error when the user click OK
		// without fill in the fields First Name, Last Name.
		String xpathRole = "//select[@name=\"listBoxContactInfoRole\"]";
		String newText = getRandomOption(xpathRole);
		session().select(xpathRole, newText);

		session().click("//td/table/tbody/tr/td[1]/button");
		if (!session().isTextPresent(Erreur1)) {
			logAndFail("Expected to see text [" + Erreur1
					+ "] after text edition");
		}

		pause();
		session().click("//tr[2]/td/table/tbody/tr/td/button");
		pause();

		// Check if Opensyllabus displays a message error when the user click OK
		// without fill in the field Last Name.

		String newText1 = timeStamp();
		session()
				.type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);

		session().click("//td/table/tbody/tr/td[1]/button");
		if (!session().isTextPresent(Erreur1)) {
			logAndFail("Expected to see text [" + Erreur1
					+ "] after text edition");
		}

		pause();
		session().click("//tr[2]/td/table/tbody/tr/td/button");
		pause();

		// Select required fields
		session().select(xpathRole, newText);

		// Select rubric.
		String selectedRubric = getRandomRubric();
		log("Selecting rubric [" + selectedRubric + "]");
		changeRubric(selectedRubric);

		// Fill in the required informations (FirstName, Last Name and Title)

		session()
				.type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);

		String newText2 = "YYYYYYYY";
		session().type("//input[@title=\"Nom:\"]", newText2);
		pause();

		String newText3 = "Lundi AM et Mardi PM" + timeStamp();
		if (inFireFox()) {
			session()
					.selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
			session().type("//html/body", newText3);
			session().selectFrame("relative=parent");
		}

		session().click("//td/table/tbody/tr/td[1]/button");
		pause();

		// Check if the new rubric is visible.
		if (!session().isTextPresent(newText1)) {
			logAndFail("Expected to see text [" + newText1
					+ "] after text edition");
		}
		if (!session().isTextPresent(newText2)) {
			logAndFail("Expected to see text [" + newText2
					+ "] after text edition");
		}
		if (inFireFox()) {
			if (!session().isTextPresent(newText3)) {
				logAndFail("Expected to see text [" + newText3 + timeStamp()
						+ "] after text edition");
			}
		}

		if (!session().isTextPresent(selectedRubric)) {
			logAndFail("Expected to see rubric [" + selectedRubric
					+ "] after text edition");
		}
		log("OK: Selected rubric is visible");

		// Save the new rubric
		saveCourseOutline();
		pause();

		// Overview
		session().click("gwt-uid-6");
		pause();
		// Will be tested when the problem is resolved(public & attendee)
		session().click("//td[@class=\"gwt-MenuItem\"]");

		// Click on Contact Informations
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Enseignant')]");
		pause();

		// check if the new rubric is visible.
		if (!session().isTextPresent(newText1)) {
			logAndFail("Expected to see text [" + newText1
					+ "] after text edition");
		}
		if (!session().isTextPresent(newText2)) {
			logAndFail("Expected to see text [" + newText2
					+ "] after text edition");
		}

		if (inFireFox()) {
			if (!session().isTextPresent(newText3)) {
				logAndFail("Expected to see text [" + newText3 + timeStamp()
						+ "] after text edition");
			}
		}

		if (!session().isTextPresent(selectedRubric)) {
			logAndFail("Expected to see rubric [" + selectedRubric
					+ "] after text edition");
		}
		log("OK: Selected rubric is visible");

		// Log out
		session().selectFrame("relative=parent");
		logOut();
		log("=================================");
		log("testAddContactInfo: test complete");
		log("=================================");		
	} // testAddContactInfo

}