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
 * Tests the addition of a text resource. The exact steps are: log in as admin,
 * creates a new site if needed, load it (OpenSyllabus is the first and only
 * page), enters in the first lecture, click Text in the Add menu, check the
 * resource count has incremented by 1, open the editor, type in some text,
 * change the rubric (randomly) click OK, check the text is here, check the
 * rubric is visible, click save, log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class TextTestUdeM extends AbstractOSYLTest {

	@Test(groups = "OSYL-Suite", description = "OSYLEditor test. Add a text resource, edit it and save the changes")
	@Parameters( { "webSite" })
	public void testAddText(String webSite) throws Exception {
		// We log in
		logInAsAdmin(webSite);
		try {
			goToSite();
		} catch (IllegalStateException e) {
			createTestSite();
			goToSite();
		}
		waitForOSYL();
			
		// Open Period Section
		session().mouseDown(
				"//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/"
						+ "td/div[contains(text(),'Organisation du cours')]");
		pause();

		// If we don't have a Lecture we add one
		int LectNb = getResourceCount();
		if (LectNb == 1) {
			// We add a first Lecture
			clickAddItem("addPeriodeUnit");
			pause();
		}

		enterFirstLecture();

		// We keep track of how many resources are showing to check that it
		// is incremented as expected when we add one
		int resNb = getResourceCount();
		log("We start with " + resNb + " resources");

		// Click menu Add/Text
		clickAddItem("addText");

		// We check that our new text was added
		int resNb2 = getResourceCount();
		log("We now have " + resNb2 + " resources");
		if (1 + resNb != resNb2) {
			logAndFail("Resource count not incremented as expected!");
		} else {
			log("OK Text resource added");
		}
		saveCourseOutline();

		// open text resource editor
		session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

		String selectedRubric = getRandomRubric();
		log("Selecting rubric [" + selectedRubric + "]");
		changeRubric(selectedRubric);

		// Type some text in the rich-text area
		if (inFireFox()) {
			// type text
			session()
					.selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
			String newText = "this is a text resource typed by "
					+ "selenium, hope it works and you see it. Added on "
					+ timeStamp() + " in Firefox";

			session().type("//html/body", newText);
			// close editor
			session().selectFrame("relative=parent");
			session().click("//td/table/tbody/tr/td[1]/button");
			// check if text is visible
			if (!session().isTextPresent(newText)) {
				logAndFail("Expected to see text [" + newText
						+ "] after text edition");
			}
			log("OK: Text resource edited");
		} else {
			log("RichText edition can only be tested in Firefox");
			// close editor
			session().click("//td/table/tbody/tr/td[1]/button");
		}
		saveCourseOutline();

		// check if the new rubric is visible.
		if (!session().isTextPresent(selectedRubric)) {
			logAndFail("Expected to see rubric [" + selectedRubric
					+ "] after text edition");
		}
		log("OK: Selected rubric is visible");
		
		session().selectFrame("relative=parent");
		logOut();
		log("==========================");
		log("testAddText: test complete");
		log("==========================");		
	} // testAddText

	private int getResourceCount() {
		return session().getXpathCount(
				"//div[@class=\"Osyl-UnitView-ResPanel\"]").intValue();
	}
}
