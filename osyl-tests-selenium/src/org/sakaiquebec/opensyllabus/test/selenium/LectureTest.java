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
 * Tests lecture operations: renaming, deleting and adding a lecture. The exact
 * steps are: log in as admin, delete the previous test site if applicable,
 * creates a new one, load it (OpenSyllabus is the first and only page), rename
 * the first lecture, check it worked, delete it, click Lecture in the Add menu,
 * click edit for the last lecture (the new one) and type a new text, click OK,
 * check the text is here, click save, log out.<br/>
 * <br/>
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class LectureTest extends AbstractOSYLTest {

    @Test(groups = "OSYL-Suite", description = "OSYL test: remove the 1st lecture, add another one, edit it and save the changes")
    @Parameters( { "webSite" })
    public void testDeleteAddLecture(String webSite) throws Exception {
	// We log in
	logInAsAdmin(webSite);
	// We delete the test site if it exists. If it does not, we don't fail!
	deleteTestSite(false);
	// We create a brand new one to ensure a constant "playground"!
	createTestSite();
	goToSite();
	waitForOSYL();

	// We keep the current lecture count for future comparison
	int lectureNb =
		session().getXpathCount(
			"//div[@class=\"Osyl-LabelEditor-View\"]").intValue();

	// Click Edit for the first lecture (actually we don't really specify it
	// is the 1st lecture)
	session().click(
		"//button[@class=\"Osyl-Button Osyl-ImageAndTextButton\"]");
	// Type a new name
	String newName = "First lecture renamed on " + timeStamp();
	session().type("//input[@type='text']", newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	assertTrue("New lecture title not present",
		session().isTextPresent(newName));
	log("OK: Lecture renamed");
	
	// Now we rename the lecture from inside
	enterFirstLecture();
	session().click("//table[@class=\"Osyl-MouseOverPopup-ButtonPanel\"]"
			+ "/tbody/tr/td[1]/button");
	newName = "First lecture renamed from inside on " + timeStamp();
	session().type("//input[@type='text']", newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	assertTrue("New lecture title not present (inside the lecture)",
		session().isTextPresent(newName));
	log("OK: Lecture renamed from inside");

	// Now we return to the lecture list
	clickHomeButton();

	// Ensure the new name is still visible
	assertTrue("New lecture title not present (renamed from inside)",
		session().isTextPresent(newName));

	// We rename the 2nd lecture: this will help us check that the
	// following step (lecture switch) is working
	session().click("//tr[2]/td/table/tbody/tr/td[2]/"
			+ "div/table[2]/tbody/tr/td[1]/button");
	// Type a new name
	String newName2 = "2nd lecture renamed on " + timeStamp();
	session().type("//input[@type='text']", newName2);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	assertTrue("2nd lecture new title not present",
		session().isTextPresent(newName2));
	log("OK: 2nd lecture renamed");
	
	
	// We now switch the 1st and 2nd lectures (this might not work in
	// MSIE):
	session().mouseOver("//div[@class=\"Osyl-UnitView-ResPanel\"]");
	session().mouseOver("//div[@class=\"Osyl-UnitView-ResPanel Osyl-UnitView-ResPanel-Hover\"]");
	session().mouseOver("//table[@class=\"Osyl-MouseOverPopup-ButtonPanel\"][2]/tbody/tr[2]");
	session().mouseOver("//div[@class=\"Osyl-PushButton Osyl-PushButton-up\"]");
	session().mouseDown("//div[@class=\"Osyl-PushButton Osyl-PushButton-up-hovering\"]");
	session().mouseUp("//div[@class=\"Osyl-PushButton Osyl-PushButton-down-hovering\"]");
	
	// Click Delete for the first lecture (the one titled "2nd lecture..."
	session().click(
		"//td[2]/button[@class=\"Osyl-Button "
			+ "Osyl-ImageAndTextButton\"]");
	// We click OK to confirm the deletion
	session().click("//td/table/tbody/tr/td[1]/button");
	// We pause to allow for the delete confirmation to disappear!
	// Otherwise the test below ensuring that the 2nd lecture title is not
	// visible anymore will fail
	pause();
	saveCourseOutline();
	// Ensure the new title of first lecture is still visible
	assertTrue("1st lecture title not present after switch and delete",
		session().isTextPresent(newName));
	// Ensure the new title of 2nd lecture is not visible
	assertFalse("2nd lecture title still present after switch and delete",
		session().isTextPresent(newName2));
	log("OK: Lecture 1 and 2 switched");
	log("OK: Lecture deleted");

	// Click menu "Add..."
	session().click("gwt-uid-5");
	// Click 1st item in menu which is "Lecture" in this context (don't use
	// gwt-uid because it's dynamically generated)
	session().click(
		"//div[@class=\"gwt-MenuBar gwt-MenuBar-vertical\"]"
			+ "/table/tbody/tr[1]/td");

	// Save changes. We do it before checking the addition was done, this
	// way we can log into the site and look into the situation.
	saveCourseOutline();

	// We check the lecture was really added by comparing the lecture count
	int newLectureNb =
		session().getXpathCount(
			"//div[@class=\"Osyl-LabelEditor-View\"]").intValue();
	if (newLectureNb != lectureNb) {
	    fail("Found " + newLectureNb + " lectures instead of " + lectureNb
		    + " as expected.");
	}
	log("OK: Lecture added");

	// Click Edit for the last lecture
	session().click("//tr[" + lectureNb + "]/td/table/tbody/tr/td[2]/"
			+ "div/table[2]/tbody/tr/td[1]/button");
	// Type a new name
	newName = "last lecture renamed on " + timeStamp();
	session().type("//input[@type='text']", newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	assertTrue("Last lecture new title not present",
		session().isTextPresent(newName));
	log("OK: Last lecture renamed");

	// And we save the changes
	saveCourseOutline();

	session().selectFrame("relative=parent");
	logOut();
    } // testAddLecture
}
