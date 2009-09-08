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
 * delete the previous test site if applicable, creates a new one, load it
 * (OpenSyllabus is the first and only page), enters in the first lecture, click
 * Text in the Add menu, type in some text, click OK, check the text is here,
 * click save, log out.<br/><br/>
 *
 * In a previous version, the test also deleted the site at the end. This could
 * be reactivated later, once we are confident enough that everything really
 * happens as it should!
 *
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class TextTest extends AbstractOSYLTest {

	@Test(groups = "OSYL-Suite", description = "OSYL test: add a text resource, edit it and save the changes")
	@Parameters({"webSite"})
	public void testAddText(String webSite) throws Exception {
		// We log in
		logInAsAdmin(webSite);
		// We delete the test site if it exists. If it does not, we don't fail!
		deleteTestSite(false);
		// We create a brand new one to ensure a constant "playground"!
		createTestSite();
		goToSite();
		waitForOSYL();
		enterFirstLecture();

		// Click menu "Add..."
		session().click("gwt-uid-5");
		// Click 1st item in menu which is "Text" in this context (don't use
		// gwt-uid because it's dynamically generated)
		session().click("//div[@class=\"gwt-MenuBar gwt-MenuBar-vertical\"]/table/tbody/tr[1]/td");
		// open text resource editor
		session().click("//tr[3]/td/table/tbody/tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
		// type text
		session().selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
		session().type("//html/body", "this is a text resource typed by "
					   + "selenium, hope it works and you see it. Added on"
					   + timeStamp());
		session().selectFrame("relative=parent");
		// close editor
		session().click("//td/table/tbody/tr/td[1]/button");
		// check if text is visible
		verifyTrue(session().isTextPresent("text resource typed by selenium"));
		Reporter.log("OK: Text resource renamed");
		// Save changes
		saveCourseOutline();
		Reporter.log("OK: Course outline saved");

		session().selectFrame("relative=parent");
		// See class comment above
		// deleteTestSite();
		logOut();
	} // testAddText
}
