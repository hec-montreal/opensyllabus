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
 * Tests the addition of a comment in a document resource. This test doesn't
 * work anymore because we now have to pick a resource to be able to do
 * this. TODO: to be completed.
 *
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class OSYLAddDocComment  extends AbstractOSYLTest {

	@Test(groups = "OSYL-Suite", description = "OSYL test case")
	@Parameters({"webSite"})

	public void testOSYLAddDocComment(String webSite) throws Exception {
	    
	    

		if (true) {
		    prettyLog("OSYLAddDocComment is now obsolete!");
		    return;
		}

		logInAsAdmin(webSite);
		// We delete the test site if it exists. If it does not, we don't fail!
		deleteTestSite(false);
		// We create a brand new one to ensure a constant "playground"!
		createTestSite();
		goToCurrentSite();
		waitForOSYL();
		enterFirstLecture();

		// add document
		session().click("gwt-uid-5");
		session().click("//div[@class=\"gwt-MenuBar gwt-MenuBar-vertical\"]/table/tbody/tr[2]/td");
		// open editor of document
		session().click("//tr[3]/td/table/tbody/tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
		// change title of document
		session().type("//tr[2]/td/input", "new text");
		// change comment of document
		session().selectFrame("//iframe[@class=\"gwt-RichTextArea\"]");
		session().type("//html/body", "this is a comment, hope it works and you see it");
		session().selectFrame("relative=parent");
		// close editor
		session().click("//td/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td[1]/button");
		// check if title and comment are visible
		verifyTrue(session().isTextPresent("new text"));
		verifyTrue(session().isTextPresent("hope it works"));
		// Save changes
		saveCourseOutline();

		session().selectFrame("relative=parent");
		logOut();
	} // testOSYLAddDocComment
}
