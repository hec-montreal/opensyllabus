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
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private String siteName;
    // Base name for test sites
    public static final String TEST_SITE_BASE_NAME = "Se-";

    // The screenshot capture is always done on the windows machine (running
    // the test) and not on the grid. This explains the following!
    private static final String SCREENSHOT_DIR = "C:/opt/selenium/screenshots/";

    // Current browser
    private String browserString;

    // Formatter for timeStamps
    private SimpleDateFormat timeStampFormatter;

    protected String fileServer;

    @BeforeClass(alwaysRun = true)
    @Parameters( { "seleniumHost", "seleniumPort", "browser", "webSite" })
    public void setUp(String seleniumHost, int seleniumPort, String browser,
	    String webSite) {

	startSeleniumSession(seleniumHost, seleniumPort, browser, webSite);

	browserString = browser;
	log("Starting test with browser: " + browser);
	initCurrentTestSiteName();
	timeStampFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
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

    private void initCurrentTestSiteName() {
	siteName =
		TEST_SITE_BASE_NAME
			+ (new SimpleDateFormat("yyyy-MM-dd"))
				.format(new Date());
	log("Test site is " + siteName);
    }

    /**
     * Returns a time stamp that can be used to mark some test (insert in texts
     * for instance).
     */
    public String timeStamp() {
	return timeStampFormatter.format(new Date());
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
	session().setTimeout("300000");
	if (!session().isElementPresent("eid")
		&& session().isElementPresent("loginLink1")) {
	    // We seem to be logged in already. This happens when RC fails to
	    // close MSIE after a test.
	    log("logInAsAdmin: We are already logged in!");
	    return;
	}
	session().type("eid", "admin");
	session().type("pw", "osyl123");
	session().click("submit");
	// waitForPageToLoad();
	session().waitForPageToLoad("300000");
	assertFalse("Login failed", session().isElementPresent(
		"//div[@class=\"alertMessage\"]"));
    }

    /**
     * Creates a test site through the sakai.sites tool. The only tool added to
     * the site is OpenSyllabus. Caveat: right now (2009-09-01) when a Sakai
     * site is deleted, the OpenSyllabus content is kept in the database and it
     * will be reused when another site with the same name is recreated!
     * WARNING: this test assumes the admin user has fr_CA as preferred locale!
     * TODO: change the locators so they are locale-independent.
     */
    public void createTestSite() throws Exception {
	log("Creating site " + getCurrentTestSiteName());
	session().open("/portal/site/opensyllabusManager");

	session().answerOnNextPrompt("osyl123");
	if (inFireFox()) {
	    session().mouseOver("//tr[7]/td/table/tbody/tr/td[1]/div");
	    session().mouseDown("//tr[7]/td/table/tbody/tr/td[1]/div");
	    session().mouseUp("//tr[7]/td/table/tbody/tr/td[1]/div");
	    pause();
	} else {
	    session().keyPress("//tr[7]/td/table/tbody/tr/td[1]/div", "\r");
	}

	session().type("//tr[2]/td/table/tbody/tr/td[2]/input",
		getCurrentTestSiteName());
	session().select("//tr[4]/td/table/tbody/tr/td[2]/select",
		"value=default");
	session().select("//tr[3]/td/table/tbody/tr/td[2]/select", "index=2");

	if (inFireFox()) {
	    session().mouseOver("//tr[5]/td/div/div");
	    session().mouseDown("//tr[5]/td/div/div");
	    session().mouseUp("//tr[5]/td/div/div");
	    pause();
	} else {
	    session().keyPress("//tr[5]/td/div/div", "\r");
	}

	if (inFireFox()) {
	    session().mouseOver("//tr[4]/td/div/div");
	    session().mouseDown("//tr[4]/td/div/div");
	    session().mouseUp("//tr[4]/td/div/div");
	} else {
	    session().keyPress("//tr[4]/td/div/div", "\r");
	}

    } // createTestSite

    public String getCurrentTestSiteName() {
	return siteName;
    }

    /**
     * Loads the test site. Assumes we are already logged-in. A failure occurs
     * if the site does not exist.
     */
    public void goToSite() throws IllegalStateException {
	try {
	    session().open("/portal/site/" + getCurrentTestSiteName());
	    waitForPageToLoad();
	    if (session().isTextPresent("Site Unavailable")) {
	        throw new IllegalStateException("Got 'Site Unavailable' !");
	    }

	    session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
	    pause();
	    if (!session().isVisible("gwt-uid-4")) {
	        log("Course outline locked: waiting 15 minutes");
	        pause(900000);
	        session().refresh();
	        waitForPageToLoad();
	    }
	    if (!session().isVisible("gwt-uid-4")) {
	        logAndFail("Course outline still locked after 15 minutes");
	    }
	} catch (IllegalStateException e) {
	    throw e;
	} catch (Exception e) {
	    logAndFail("goToSite: " + e);
	}
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
	    session().type("search_site", getCurrentTestSiteName());
	    session().click("link=ID du site");
	    waitForPageToLoad();
	    waitForPageToLoad();

	    if (session().isTextPresent(getCurrentTestSiteName())) {
		log("Found site '" + getCurrentTestSiteName() + "' to delete");
	    } else {
		if (fail) {
		    fail("Cannot delete site '" + getCurrentTestSiteName()
			    + "' because it could not be found!");
		} else {
		    log("Did not delete site '" + getCurrentTestSiteName()
			    + "' because it did not exist");
		    return;
		}
	    }

	    session().click("link=" + getCurrentTestSiteName());
	    waitForPageToLoad();
	    session().click("link=Supprimer site");
	    waitForPageToLoad();
	    session().click("eventSubmit_doRemove_confirmed");
	    waitForPageToLoad();
	    assertFalse("Site not deleted as expected!", session()
		    .isTextPresent(getCurrentTestSiteName()));
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
	session().selectFrame("relative=parent");
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

	// session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
	for (int second = 0;; second++) {
	    if (second >= 60) {

		fail("Timeout waiting for Osyl-TreeItem-HorizontalPanel sub-structure:"
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
			"//div[@class=\"Osyl-TreeItem-HorizontalPanel\"]")) {
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
	try {
	    // enter first lecture
	    session().mouseOver(
		    "//table[@class=\"Osyl-WorkspaceView-MainPanel\"]"
			    + "/tbody/tr[2]/td/table/tbody/tr/td/div");
	    session().mouseDown(
		    "//table[@class=\"Osyl-WorkspaceView-MainPanel\"]"
			    + "/tbody/tr[2]/td/table/tbody/tr/td/div");
	    session().mouseUp(
		    "//table[@class=\"Osyl-WorkspaceView-MainPanel\"]"
			    + "/tbody/tr[2]/td/table/tbody/tr/td/div");
	} catch (Exception e) {
	    logAndFail("Unable to enter first lecture: " + e);
	}

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
     * Calls clickAddButton and clicks on the item whose id is specified (case
     * sensitive). This ID uses the pattern addType. For instance addText or
     * addPedagogicalUnit. Calls logAndFail if the item is not found.
     * 
     * @param itemText to click
     */
    public void clickAddItem(String itemText) {
	log("Entering clickAddItem " + itemText);

	try {
	    // Click the add button
	    clickAddButton();

	    // Click the item or fail if it is not found
	    session().click("//div[@id=\"" + itemText + "\"]");
	} catch (Exception e) {
	    logAndFail("clickAddButton(" + itemText + ") FAILED: " + e);
	}
    } // clickAddItem(String)

    /**
     * Calls clickAddButton and clicks on the item at the specified index. Calls
     * logAndFail if the index is out of bounds. Using this method is
     * discouraged as it doesn't allow to click on a specific known item.
     * 
     * @param index of item to click
     */
    public void clickAddItem(int index) {
	log("Entering clickAddItem " + index);

	try {
	    // Click the add button
	    clickAddButton();

	    // Click the item or fail if it is not found
	    session().click(
		    "//div[@class=\"gwt-MenuBar gwt-MenuBar-vertical\"]"
			    + "/table/tbody/tr[" + index + "]/td");
	} catch (Exception e) {
	    logAndFail("clickAddButton(" + index + ") FAILED: " + e);
	}
    } // clickAddItem(int)

    /**
     * Clicks on the Add button in the toolBar (identified by its id gwt-uid-5
     * for now).
     */
    public void clickAddButton() {
	session().click("gwt-uid-8");
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
		    // TODO: après trois fois on pourrait assumer que le save a
		    // fonctionné!
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
	session().click("gwt-uid-4");
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
	log("logAndFail: " + msg);
	captureScreenShotFailure(msg);
	try {
	    logOut();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	fail(msg);
    }

    /**
     * Returns the screenshot file name corresponding to the specified message,
     * removing forbidden chars and adding ".png".
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
     * the test procedure is not stopped right after its invocation, it may fail
     * as the context is changed.
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
	    log("Cannot capture a screenshot in Internet Explorer: " + fileName);
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

    /**
     * Given a select menu whose XPath is specified, returns a String
     * corresponding to a random option. The select menu must be visible before
     * calling this method.
     * 
     * @return random option
     */
    protected String getRandomOption(String xpath) {
	// Get list of available options
	String[] options = session().getSelectOptions(xpath);
	// Generate a random number between 1 and last option index (avoid 0
	// because first one is "Select a option")
	int optionCount = options.length - 2;
	int optionId = 1 + (int) Math.round(Math.random() * optionCount);
	return options[optionId];
    }

    /**
     * Returns a String corresponding to one of the rubrics available. The
     * corresponding select menu (name=listBoxFormElement) must be visible
     * before calling this method.
     * 
     * @return random rubric
     */
    protected String getRandomRubric() {
	return getRandomOption("//select[@name=\"listBoxFormElement\"]");
    }

    /**
     * Changes the rubric in the resource being edited. The corresponding select
     * menu (name=listBoxFormElement) must be visible before calling this
     * method.
     * 
     * @param rubricLabel
     */
    protected void changeRubric(String rubricLabel) {
	session().select("//select[@name=\"listBoxFormElement\"]",
		"label=" + rubricLabel);
    }

}
