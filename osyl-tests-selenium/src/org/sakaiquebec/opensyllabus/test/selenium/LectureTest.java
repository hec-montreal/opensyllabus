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
 * Tests lecture operations: renaming, switching, deleting and adding a lecture.
 * The exact steps are: log in as admin, creates a new site if needed, load it
 * (OpenSyllabus is the first and only page), rename the first lecture, check it
 * worked, enter into it, rename it from inside, return to the lecture list,
 * rename the 2nd lecture, switch it with the 1st one, delete it, check that the
 * 2nd is not visible anymore and the 1st is still. Click Lecture in the Add
 * menu, check it was added, click edit for the last lecture (the new one) and
 * type a new text, click OK, check the text is here, click save, log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class LectureTest extends AbstractOSYLTest {

    private static final String TITLE_TEXTBOX =
	    "//input[@class=\"Osyl-LabelEditor-TextBox\"]";

    @Test(groups = "OSYL-Suite", description = "OSYLEditor test. Remove, add, edit lectures and save the changes")
    @Parameters( { "webSite" })
    public void testDeleteAddLecture(String webSite) throws Exception {
	// We log in
	logInAsAdmin(webSite);
	try {
	    goToCurrentSite();
	} catch (IllegalStateException e) {
	    createTestSite();
	    goToCurrentSite();
	}
	waitForOSYL();

	openOrganisationSection();

	// We keep the current lecture count for future comparison
	int lectureNb = countLecture();

	if (lectureNb == 0) {
	    // We add a first Lecture
	    clickAddItem("addLecture");
	    pause();
	    lectureNb = countLecture();
	}
	// Click Edit for the first lecture (actually we don't really specify it
	// is the 1st lecture)
	session()
		.click(
			"//div[@class=\"Osyl-UnitView-ResPanel\"]//button[@class=\"Osyl-Button Osyl-ImageAndTextButton\"]");
	// Type a new name
	String newName = "First lecture renamed on " + timeStamp();
	session().type(TITLE_TEXTBOX, newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if (!session().isTextPresent(newName)) {
	    logAndFail("New lecture title not present");
	}
	log("OK: Lecture renamed");

	// Now we rename the lecture from inside
	enterFirstLecture();
	session().click(
		"//table[@class=\"Osyl-MouseOverPopup-ButtonPanel\"]"
			+ "/tbody/tr/td[1]/button");
	newName = "First lecture renamed from inside on " + timeStamp();
	session().type(TITLE_TEXTBOX, newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if (!session().isTextPresent(newName)) {
	    logAndFail("New lecture title not present (inside the lecture)");
	}
	log("OK: Lecture renamed from inside");

	// Now we return to the lecture list
	clickHomeButton();

	// Ensure the new name is still visible
	if (!session().isTextPresent(newName)) {
	    logAndFail("New lecture title not present (renamed from inside)");
	}

	if (lectureNb < 2) {
	    // We add a first Lecture
	    clickAddItem("addLecture");
	    pause();
	    lectureNb = countLecture();
	}
	// We rename the 2nd lecture: this will help us check that the
	// following step (lecture switch) is working
	session().click(
		"//tr[3]/td/table/tbody/tr/td[2]/"
			+ "div/table[2]/tbody/tr/td[1]/button");
	// Type a new name
	String newName2 = "2nd lecture renamed on " + timeStamp();
	session().type(TITLE_TEXTBOX, newName2);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if (!session().isTextPresent(newName2)) {
	    logAndFail("2nd lecture new title not present");
	}
	if (!session().isTextPresent(newName)) {
	    logAndFail("1st lecture new title not present after 2nd lecture edit");
	}
	log("OK: 2nd lecture renamed");

	// We now switch the 1st and 2nd lectures (different ways to do it for
	// MSIE and FF, unfortunately):
	if (inInternetExplorer()) {
	    session()
		    .keyPress(
			    "//div[@class=\"Osyl-PushButton "
				    + "Osyl-PushButton-up\"]", "\r");
	} else {
	    session().mouseOver("//div[@class=\"Osyl-UnitView-ResPanel\"]");
	    session()
		    .mouseOver(
			    "//div[@class=\"Osyl-UnitView-ResPanel Osyl-UnitView-ResPanel-Hover\"]");
	    session()
		    .mouseOver(
			    "//table[@class=\"Osyl-MouseOverPopup-ArrowButtonPanel\"]/tbody/tr[2]");
	    session().mouseOver(
		    "//div[@class=\"Osyl-PushButton Osyl-PushButton-up\"]");
	    session()
		    .mouseDown(
			    "//div[@class=\"Osyl-PushButton Osyl-PushButton-up-hovering\"]");
	    session()
		    .mouseUp(
			    "//div[@class=\"Osyl-PushButton Osyl-PushButton-down-hovering\"]");
	}
	pause();

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
	if (!session().isTextPresent(newName)) {
	    logAndFail("1st lecture title not present after switch and delete");
	}
	// Ensure the new title of 2nd lecture is not visible
	if (session().isTextPresent(newName2)) {
	    logAndFail("2nd lecture title still present after switch and delete");
	}
	log("OK: Lecture 1 and 2 switched");
	log("OK: Lecture deleted");

	// We click Add lecture whose type is actually PedagogicalUnit
	clickAddItem("addLecture");

	// Save changes. We do it before checking the addition was done, this
	// way we can log into the site and look into the situation.
	saveCourseOutline();

	// We check the lecture was really added by comparing the lecture count
	int newLectureNb = countLecture();
	if (newLectureNb != lectureNb) {
	    fail("Found " + newLectureNb + " lectures instead of " + lectureNb
		    + " as expected.");
	}
	log("OK: Lecture added");

	// Click Edit for the last lecture (add 1 because 1st row is a label)
	session().click(
		"//tr[" + (1 + lectureNb) + "]/td/table/tbody/tr/td[2]/"
			+ "div/table[2]/tbody/tr/td[1]/button");
	// Type a new name
	newName = "Lecture added on " + timeStamp();
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]", newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if (!session().isTextPresent(newName)) {
	    logAndFail("Last lecture new title not present");
	}
	log("OK: Last lecture renamed");

	// And we save the changes
	saveCourseOutline();

	session().selectFrame("relative=parent");
	logOut();
	log("testAddLecture: test complete");
    } // testAddLecture

    private int countLecture() {
	return session().getXpathCount(
		"//td[@class=\"Osyl-ListItemView-labelNo\"]").intValue();

    }
}
