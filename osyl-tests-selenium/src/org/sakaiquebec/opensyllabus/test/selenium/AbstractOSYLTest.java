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

import com.rsmart.cle.test.AbstractTestCase;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Reporter;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * AbstractOSYLTest contains several methods that can be used to implement
 * various Selenium tests. Some methods have to be called in a specific
 * order. For instance createTestSite() won't work unless logInAsAdmin(webSite) has
 * been invoked before.
 *
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class AbstractOSYLTest  extends AbstractTestCase {

	public static final String TEST_SITE_NAME = "tests-selenium-opensyllabus";

	/**
	 * Shortcut for session.waitForPageToLoad(30000).
	 */
	public void waitForPageToLoad() {
		session().waitForPageToLoad("30000");
	}

	/**
	 * Returns a time stamp that can be used to mark some test (insert in texts
	 * for instance).
	 */
	public String timeStamp() {
		return new java.util.Date().toString();
	}

	/**
	 * Log in Sakai using the admin account.
	 *
	 * @param String url of the Sakai instance to log in
	 */
	public void logInAsAdmin(String url) throws Exception {
		session().open(url);
		session().open("/portal");
		session().type("eid", "admin");
		session().type("pw", "osyl123");
		session().click("submit");
		waitForPageToLoad();
	}

	/**
	 * Creates a test site through the sakai.sites tool. The only tool added to
	 * the site is OpenSyllabus.  Caveat: right now (2009-09-01) when a Sakai
	 * site is deleted, the OpenSyllabus content is kept in the database and it
	 * will be reused when another site with the same name is recreated!
	 *
	 * WARNING: this test assumes the admin user has fr_CA as preferred locale!
	 * TODO: change the selenium locators so they are locale-independant.
	 */
	public void createTestSite() throws Exception {
		// Click on Sites, the 4th item in the list menu
		session().click("//div[@id='toolMenu']/ul/li[4]/a/span");
		waitForPageToLoad();
		session().selectFrame("Mainxadminx310");
		// Ensure the site doesn't already exist
		assertFalse("Site '" + TEST_SITE_NAME + "' already exists!",
					session().isTextPresent(TEST_SITE_NAME));
		session().click("link=Nouveau site");
		waitForPageToLoad();
		session().type("id", TEST_SITE_NAME);
		session().type("title", "Site for Selenium Tests");
		session().type("type", "project");
		session().type("shortDescription",
					   "This site is created automatically while running "
					   + "Selenium tests. It will be deleted as soon as the "
					   + "test suite is completed.");
		session().click("publishedtrue");
		session().click("eventSubmit_doPages");
		waitForPageToLoad();
		// Ensure the site doesn't already exist or another error
		assertFalse("Received Alert when creating Site '" + TEST_SITE_NAME
					+ "'", session().isElementPresent("//div[@class=\"alertMessage\"]"));
		session().click("link=Nouvelle page");
		waitForPageToLoad();
		session().type("title", "OpenSyllabus");
		session().click("eventSubmit_doTools");
		waitForPageToLoad();
		session().click("link=Nouvel outil");
		waitForPageToLoad();
		assertTrue(session().isTextPresent(""));
		// Note to myself: under my current localhost setup, OSYL is the 10th
		// tool, under our hudson-deployed server it is the 28th
		session().click("//form[@id='thisOne']/table/tbody/tr[2]/td/p[28]/label");
		waitForPageToLoad();
		session().click("eventSubmit_doSave_tool");
		waitForPageToLoad();

		// Warning: we leave the "Sites" tool otherwise a future test might fail
		// when trying to click on it!
		session().selectFrame("relative=parent");
		session().click("//div[@id='toolMenu']/ul/li[1]/a/span");
		waitForPageToLoad();
	} // createTestSite

	/**
	 * Loads the test site. Assumes we are already logged-in. A failure occurs
	 * if the site does not exist.
	 */
	public void goToSite() throws Exception {
		session().open("/portal/site/" + TEST_SITE_NAME);
		waitForPageToLoad();
		assertFalse("Got 'Site Unavailable' !", session().isTextPresent("Site Unavailable"));
	}

	/**
	 * Deletes the test site. Will fail if the operation is unsuccessful.
	 *
	 */
	public void deleteTestSite() throws Exception {
		deleteTestSite(true);
	}

	/**
	 * Deletes the test site. Will fail if the operation is unsuccessful and if
	 * the boolean parameter is true.
	 *
	 * @param boolean fail
	 */
	public void deleteTestSite(boolean fail) throws Exception {
		try {
			session().open("/portal/site/%7Eadmin");
			waitForPageToLoad();
			// Warning: the next command won't work if the current active tool
			// is already "Sites".
			session().click("//div[@id='toolMenu']/ul/li[4]/a/span");
			waitForPageToLoad();
			session().selectFrame("Mainxadminx310");
			session().type("search_site", TEST_SITE_NAME);
			session().click("link=ID du site");
			waitForPageToLoad();
			waitForPageToLoad();

			if(session().isTextPresent(TEST_SITE_NAME)) {
				Reporter.log("Found site '" + TEST_SITE_NAME + "' to delete",
							 true);
			} else {
				if (fail) {
					fail("Cannot delete site '" + TEST_SITE_NAME
						 + "' because it could not be found!");
				} else {
					Reporter.log("Did not delete site '" + TEST_SITE_NAME
								 + "' because it did not exist", true);
					return;
				}
			}

			session().click("link=" + TEST_SITE_NAME);
			waitForPageToLoad();
			session().click("link=Supprimer site");
			waitForPageToLoad();
			session().click("eventSubmit_doRemove_confirmed");
			waitForPageToLoad();
			assertFalse("Site not deleted as expected!",
						session().isTextPresent(TEST_SITE_NAME));
		} finally {
			// Return to Home "tool"
			session().selectFrame("relative=parent");
			session().click("//div[@id='toolMenu']/ul/li[1]/a/span");
			waitForPageToLoad();
		}
	} // deleteTestSite

	/**
	 * Terminates the current session by clicking on element loginLink1. After
	 * the click, we expect to see element eid (user ID input field).
	 */
	public void logOut() throws Exception {
		session().click("loginLink1");
		waitForPageToLoad();
		for (int second = 0;; second++) {
			if (second >= 60) fail("Timeout (logOut): can't find element eid");
			try { if (session().isElementPresent("eid")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
	} // logOut

	/**
	 * After clicking on the OpenSyllabus tool link, a call to this method
	 * ensures that it has loaded and it is displayed.
	 */
	public void waitForOSYL() throws Exception {

		session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
		for (int second = 0;; second++) {
			if (second >= 60) {
				fail("Timeout waiting for Osyl-UnitView-UnitPanel sub-structure");
			}
			try {
				if (session().isElementPresent(
						"//table[@class=\"Osyl-UnitView-UnitPanel\"]"
						+ "/tbody/tr/td/table/tbody/tr/td/div/a")) {
					break;
				}
			} catch (Exception e) {
			}
			Thread.sleep(1000);
		}

		// Increase this to be able to see the test running. Do not set it below
		// 100 as it tends to cause problems (menu items not found for instance).
		session().setSpeed("200");

	} // waitForOSYL

	/**
	 * Clicks on the first lecture in OpenSyllabus while in the view displaying
	 * the lecture list (initial view). This method was not thoroughly tested!
	 */
	public void enterFirstLecture() throws Exception {
		// enter first lecture
		session().click("//table[@class=\"Osyl-UnitView-UnitPanel\"]"
						+ "/tbody/tr/td/table/tbody/tr/td/div/a");
	} // enterFirstLecture

	/**
	 * Clicks on the Save button in OpenSyllabus. It expects to see an
	 * UnobtrusiveAlert in the next 60 seconds. We can safely assume that it
	 * confirms the operation was successful. Otherwise a pop-up would display
	 * an error.
	 */
	public void saveCourseOutline() throws Exception {
		session().click("gwt-uid-2");
		for (int second = 0;; second++) {
			if (second >= 600) fail("Timed out waiting for save confirmation (after 60 seconds)");
			try { if (session().isVisible("//div[@class=\"Osyl-UnobtrusiveAlert\"]")) break; } catch (Exception e) {}
			Thread.sleep(100);
		}
	} // saveCourseOutline

}
