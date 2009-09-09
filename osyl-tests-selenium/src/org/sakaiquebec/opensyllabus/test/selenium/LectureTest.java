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
	String newName = "lecture renamed on " + timeStamp();
	session().type("//input[@type='text']", newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	verifyTrue(session().isTextPresent(newName));
	log("OK: Lecture renamed");

	/* Now we switch the edited lecture with the 2nd one which comes at the
	 * 1st position. Then we edit the 1st one with a new name, we ensure it
	 * is visible, then we delete the lecture and we ensure it is not
	 * visible anymore.

	======= All this section is commented because right now it
	======= doesn't work at all (can't click on an arrow)

	// We could move the 1st lecture to the 2nd position
	// -- click on the down-arrow (1st lecture) doesn't work:
	//    session().click("//tr[2]/td/table/tbody/tr/td/div/img");
	// -- Click on the up-arrow (2nd lecture) doesn't work either
	//    session().click("//tr[2]/td/table/tbody/tr/td[2]/div/table[3]/tbody/tr[1]/td/table/tbody/tr/td/div/img");
	// Click edit for the 1st lecture
	session().click("//button[@class=\"Osyl-Button Osyl-ImageAndTextButton\"]");
	// Type a new name
	String newName2 = "lecture to delete " + timeStamp();
	session().type("//input[@type='text']", newName2);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	verifyTrue(session().isTextPresent(newName2));
*/

	// Click Delete for the first lecture
	session().click(
		"//td[2]/button[@class=\"Osyl-Button "
			+ "Osyl-ImageAndTextButton\"]");
	// We click OK to confirm the deletion
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is not visible anymore
	verifyTrue(!session().isTextPresent(newName));
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

	// Click Edit for the last lecture (actually we don't really specify it
	// is the 1st lecture)
	session().click("//tr["	+ lectureNb + "]/td/table/tbody/tr/td[2]/" +
			"div/table[2]/tbody/tr/td[1]/button");
	// Type a new name
	newName = "last lecture renamed on " + timeStamp();
	session().type("//input[@type='text']", newName);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	verifyTrue(session().isTextPresent(newName));
	log("OK: Last lecture renamed");

	// And we save the changes
	saveCourseOutline();

	session().selectFrame("relative=parent");
	logOut();
    } // testAddLecture
}
