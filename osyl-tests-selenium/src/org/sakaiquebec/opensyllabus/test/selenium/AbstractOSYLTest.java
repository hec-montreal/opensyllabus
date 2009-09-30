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

import java.io.File;
import com.thoughtworks.selenium.SeleneseTestCase;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.Reporter;

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;

/**
 * AbstractOSYLTest contains several methods that can be used to implement
 * various Selenium tests. Some methods have to be called in a specific order.
 * For instance createTestSite() won't work unless logInAsAdmin(webSite) has
 * been invoked before.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 */
public class AbstractOSYLTest extends SeleneseTestCase {

    // Current test site
    public static final String TEST_SITE_NAME = "tests-selenium-opensyllabus";

    // The screenshot capture is always done on the windows machine (running
    // the test) and not on the grid. This explains the following!
    private static final String SCREENSHOT_DIR = "C:/opt/selenium/screenshots/";

    // Current browser
    private String browserString;

    protected String fileServer;

    @BeforeClass(alwaysRun = true)
    @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite"})
    public void setUp(String seleniumHost, int seleniumPort, String browser,
	    String webSite) {

	startSeleniumSession(seleniumHost, seleniumPort, browser, webSite);

	browserString = browser;
	log("Starting test with browser: " + browser);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
	closeSeleniumSession();
    }

    /**
     * Shortcut for session.waitForPageToLoad("30000").
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
	if (!url.endsWith("/portal")) {
	    if (!url.endsWith("/")) {
		url += "/";
	    }
	    url += "portal";
	}
	// First access, we use a high timeout value as Sakai may have just
	// started and not be ready yet
	session().setTimeout("180000");
	session().open(url);
	session().setTimeout("30000");
	if (! session().isElementPresent("eid")
		&& session().isElementPresent("loginLink1")) {
	    // We seem to be logged in already. This happens when RC fails to
	    // close MSIE after a test.
	    log("logInAsAdmin: We are already logged in!");
	    return;
	}
	session().type("eid", "admin");
	session().type("pw", "osyl123");
	session().click("submit");
	waitForPageToLoad();
	assertFalse("Login failed", session().isElementPresent(
		"//div[@class=\"alertMessage\"]"));
    }

    /**
     * Creates a test site through the sakai.sites tool. The only tool added to
     * the site is OpenSyllabus. Caveat: right now (2009-09-01) when a Sakai
     * site is deleted, the OpenSyllabus content is kept in the database and it
     * will be reused when another site with the same name is recreated!
     * WARNING: this test assumes the admin user has fr_CA as preferred locale!
     * TODO: change the selenium locators so they are locale-independant.
     */
    public void createTestSite() throws Exception {
	// Click on Sites, the 4th item in the list menu
	session().click("//div[@id='toolMenu']/ul/li[4]/a/span");
	waitForPageToLoad();
	session().selectFrame("Mainxadminx310");
	// Ensure the site doesn't already exist
	assertFalse("Site '" + TEST_SITE_NAME + "' already exists!", session()
		.isTextPresent(TEST_SITE_NAME));
	session().click("link=Nouveau site");
	waitForPageToLoad();
	session().type("id", TEST_SITE_NAME);
	session().type("title", "Site for Selenium Tests");
	session().type("type", "project");
	session().type(
		"shortDescription",
		"This site is created automatically while running "
			+ "Selenium tests. It will be deleted as soon as the "
			+ "test suite is completed.");
	session().click("publishedtrue");
	session().click("eventSubmit_doPages");
	waitForPageToLoad();
	// Ensure the site doesn't already exist or another error
	assertFalse("Received Alert when creating Site '" + TEST_SITE_NAME
		+ "'", session().isElementPresent(
		"//div[@class=\"alertMessage\"]"));
	session().click("link=Nouvelle page");
	waitForPageToLoad();
	session().type("title", "OpenSyllabus");
	session().click("eventSubmit_doTools");
	waitForPageToLoad();
	session().click("link=Nouvel outil");
	waitForPageToLoad();
	session().click("//input[@value=\"sakai.opensyllabus.tool\"]");
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
	assertFalse("Got 'Site Unavailable' !", session().isTextPresent(
		"Site Unavailable"));
    }

    /**
     * Deletes the test site. Will fail if the operation is unsuccessful.
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

	    if (session().isTextPresent(TEST_SITE_NAME)) {
		log("Found site '" + TEST_SITE_NAME + "' to delete");
	    } else {
		if (fail) {
		    fail("Cannot delete site '" + TEST_SITE_NAME
			    + "' because it could not be found!");
		} else {
		    log("Did not delete site '" + TEST_SITE_NAME
			    + "' because it did not exist");
		    return;
		}
	    }

	    session().click("link=" + TEST_SITE_NAME);
	    waitForPageToLoad();
	    session().click("link=Supprimer site");
	    waitForPageToLoad();
	    session().click("eventSubmit_doRemove_confirmed");
	    waitForPageToLoad();
	    assertFalse("Site not deleted as expected!", session()
		    .isTextPresent(TEST_SITE_NAME));
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
	    if (second >= 60) {
		fail("Timeout (logOut): can't find element eid");
	    }
	    try {
		if (session().isElementPresent("eid")) {
		    break;
		}
	    } catch (Exception e) {
	    }
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
		fail("Timeout waiting for Osyl-UnitView-UnitPanel sub-structure:"
			+ " __Was OpenSyllabus added to the site?__");
	    }
	    if (session().isTextPresent("Exception")
		    && session().isTextPresent("Stacktrace")
		    && session().isTextPresent("at org.sakaiproject.portal")) {
		logAndFail("Found exception waiting for OpenSyllabus: "
			+ "deployment may have failed. See screenshot.");
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

	log("Found OpenSyllabus: tests will begin now");

	// Increase this to be able to see the test running. Do not set it below
	// 100 as it tends to cause problems (menu items not found for
	// instance).
	session().setSpeed("200");

    } // waitForOSYL

    /**
     * Clicks on the first lecture in OpenSyllabus while in the view displaying
     * the lecture list (initial view). This method was not thoroughly tested!
     */
    public void enterFirstLecture() throws Exception {
	// enter first lecture
	session().click(
		"//table[@class=\"Osyl-UnitView-UnitPanel\"]"
			+ "/tbody/tr/td/table/tbody/tr/td/div/a");
    } // enterFirstLecture

    /**
     * Clicks on the Home button in OpenSyllabus. It expects to see at least one
     * td with class Osyl-ListItemView-labelNo and fails otherwise. Note: This
     * could be OK that there is no such td (zero lecture in the course outline)
     * but this is the way this method behaves for now.
     */
    public void clickHomeButton() throws Exception {
	log("Entering clickHomeButton");

	// Click the button
	session().click("gwt-uid-1");

	// We check that we see at least one LectureNo label. Actually there
	// could be zero such label if all lectures have been deleted!
	String xpath = "//td[@class=\"Osyl-ListItemView-labelNo\"]";
	int lectureNb = session().getXpathCount(xpath).intValue();
	log("clickHomeButton: found " + lectureNb + " lectures");
	if (lectureNb == 0) {
	    fail("clickHomeButton: Could not find " + xpath);
	}
    } // clickHomeButton

    /**
     * Calls clickAddButton and clicks on the item containing the specified
     * text (case sensitive).
     *  
     * @param itemText to click
     * @throws Exception if the item is not found
     */
    public void clickAddItem(String itemText) throws Exception {
	log("Entering clickAddItem " + itemText);

	// Click the add button
	clickAddButton();

	try {
	    session().click(
		    "//div[@class=\"gwt-MenuBar gwt-MenuBar-vertical\"]"
		    + "/table/tbody/tr/td[contains(text(),'" + itemText
		    + "')]");
	} catch (Exception e) {
	    log("clickAddButton(" + itemText + ") FAILED: " + e);
	    logAndFail("clickAddButton(" + itemText + ") FAILED: " + e);
	    throw e;
	}
    } // clickAddItem

    /**
     * Clicks on the Add button in the toolBar (identified by its id gwt-uid-5
     * for now).
     */
    public void clickAddButton() {
	session().click("gwt-uid-5");
    }
    
    /**
     * Clicks on the Save button in OpenSyllabus. It expects to see an
     * UnobtrusiveAlert in the next 60 seconds. We can safely assume that it
     * confirms the operation was successful. We also detect an alert (either
     * native alert or an OSYL one) to diagnose an error which is logged and
     * cause the test to fail.
     */
    public void saveCourseOutline() throws Exception {
	log("Entering saveCourseOutline");

	String origSpeed = session().getSpeed();
	session().setSpeed("30");
	long start = System.currentTimeMillis();
	
	clickSaveButton();
	for (int second = 0;; second++) {
	    if (System.currentTimeMillis() - start >= 60000) {
		// After 60s we still did not see our confirmation we try
		// detect some common alerts
		if (session().isAlertPresent()) {
		    String alertTxt = session().getAlert();
		    logAndFail("saveCourseOutline failed: got an alert: "
			    + alertTxt);
		    return;
		}
		if (session().isTextPresent("RPC FAILURE")) {
		    logAndFail("saveCourseOutline failed: RPC FAILURE");
		    return;
		}
		logAndFail("saveCourseOutline: Timed out waiting"
			+ " for confirmation (after 60 seconds)");
		return; // for readability :-)
	    }
	    try {
		if (isSaveConfirmationVisible()) {
		    // We are OK
		    log("Leaving saveCourseOutline: OK");
		    session().setSpeed(origSpeed);
		    return;
		}
	    } catch (Exception e) {
		log("saveCourseOutline: Got exception: " + e);
		if (inInternetExplorer()) {
		    // We try again if in MSIE (No sleep as the processing is
		    // slow enough!)
		    log("saveCourseOutline: trying again...");
		    clickSaveButton();
		    // TODO: après trois fois on pourrait assumer que le save a fonctionné!
		} else {
		    Thread.sleep(100);
		}
	    }
	} // for
    } // saveCourseOutline

    private boolean isSaveConfirmationVisible() {
	if (inInternetExplorer()) {
	    return session().isVisible(
		    "//div[@class=\"Osyl-UnobtrusiveAlert\"]");
	} else {
	    return session().isElementPresent(
		    "//div[@class=\"Osyl-UnobtrusiveAlert\"]")
		&& session().isVisible(
		    "//div[@class=\"Osyl-UnobtrusiveAlert\"]");
	}
    }

    private void clickSaveButton() {
	session().click("gwt-uid-2");
    }

    /**
     * Pauses for a specified delay (in milliseconds). This might be needed in
     * some cases to allow an unobtrusive message to disappear, for instance.
     */
    public void pause(int millis) {
	try {
	    Thread.sleep(millis);
	} catch (InterruptedException e) {
	    log("pause(" + millis + ") failed :" + e);
	    e.printStackTrace();
	}
    }

    /**
     * Pauses for a default delay (5 seconds). This allow for any unobtrusive
     * alert to disappear.
     */
    public void pause() {
	pause(5000);
    }

    /**
     * Shortcut for <code>org.testng.Reporter.log(msg, true)</code>.
     */
    protected void log(String msg) {
	Reporter.log(msg, true);
    }

    /**
     * Logs and fail with the same message. This is to ease reports reading. A
     * screenShot capture is taken if possible.
     */
    protected void logAndFail(String msg) {
	log(msg);
	captureScreenShotFailure(msg);
	fail(msg);
    }

    /**
     * Returns the screenshot file name corresponding to the specified
     * message, removing forbidden chars and adding ".png".
     */    
    private static String getScreenShotFileName(String msg) {
	return SCREENSHOT_DIR + msg.replaceAll("[/\\:\\?\\!\\|><\"\\*]", "_")
		+ ".png";
    }

    /**
     * Ensures the directory for screenshots exists.    
     */
    private static void ensureScreenShotDirOK() {
	File dir = new File(SCREENSHOT_DIR);
	dir.mkdirs();
	if (!dir.exists()) {
	    fail("Unable to create screenshot dir: " + SCREENSHOT_DIR);
	}
    }
    
    /**
     * Captures a screenShot to document current failure. Use the specified
     * error message to generate the file name. Warning: this method performs a
     * selectFrame("relative=parent") to capture the whole page, therefore if
     * the test procedure is not stopped right after its invocation, it may
     * fail as the context is changed.
     */    
    protected void captureScreenShotFailure(String msg) {
	// If we are in FF we do a screenshot capture
	if (inFireFox()) {
	    String fileName = getScreenShotFileName(msg);
	    log("capturing to " + fileName);
	    try {
		// Do not put this selectFrame in captureScreenShot()
		session().selectFrame("relative=parent");
		captureScreenShot(fileName);
	    } catch (Exception e) {
		log("Unable to capture screenshot [" + fileName + "]: " + e);
		e.printStackTrace();
	    }
	}
    }
    
    /**
     * Captures a screenshot of current page as PNG file whose name is
     * specified. Only works in Firefox.
     */    
    protected void captureScreenShot(String fileName) {
	if (inInternetExplorer()) {
	    log ("Cannot capture a screenshot in Internet Explorer: "
		    + fileName);
	    return;
	}
	ensureScreenShotDirOK();
	session().captureEntirePageScreenshot(fileName, "background=white");
    }
    
    /**
     * Returns true if we are running in Internet Explorer.
     */
    public boolean inInternetExplorer() {
	// Note: we don't use runtimeBrowserString() which seems to always
	// return *iexplore!
	return browserString.equals("*iexplore");
    }
    
    /**
     * Returns true if we are running in Firefox.
     */
    public boolean inFireFox() {
	// Note: we don't use runtimeBrowserString() which seems to always
	// return *iexplore!
	return browserString.equals("*firefox");
    }
    
    /**
     * Verifies the specified boolean is true (if we are in MSIE), asserts it
     * otherwise. This method is handy for tests which can't be fixed in MSIE.
     * 
     * @param result
     */
    protected void assertOrVerify(String msg, boolean result) {
	if (inInternetExplorer()) {
	    // First we do the check (supposed fail at tearDown)
	    verifyTrue(result);
	    // And we log it
	    if (result) {
		log("assertOrVerify MSIE PASSED : " + msg);
	    } else {
		log("assertOrVerify MSIE FAILED : " + msg);
	    }
	} else {
	    assertTrue(msg, result);
	}
    } // assertOrVerify
    
}
