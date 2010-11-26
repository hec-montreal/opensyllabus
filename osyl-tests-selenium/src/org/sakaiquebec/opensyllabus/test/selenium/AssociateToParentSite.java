/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AssociateToParentSite extends AbstractOSYLTest {

    private static int ALLOWED_TRIES = 10;
    
    @Test(groups = "OSYL-Suite", description = "OSYLEditor and OSYLManager test. Associates sites and check that content is added or deleted as expected.")
    @Parameters( { "webSite" })
    public void testHierarchy(String webSite) throws Exception {
	// We log in
	// test if site exist, else create it
	logInAsAdmin(webSite);
	try {
	    goToCurrentSite();
	} catch (IllegalStateException e) {
	    createTestSite();
	    goToCurrentSite();
	}
	waitForOSYL();

	session().selectFrame("relative=parent");
	String childSiteName = getCurrentTestSiteName() + "_Child";
	try {
	    goToSite(childSiteName);
	} catch (IllegalStateException e) {
	    createSite(childSiteName);
	    goToSite(childSiteName);
	}
	waitForOSYL();

	session().selectFrame("relative=parent");
	String parentSiteName = getCurrentTestSiteName() + "_Parent";
	try {
	    goToSite(parentSiteName);
	} catch (IllegalStateException e) {
	    createSite(parentSiteName);
	    goToSite(parentSiteName);
	}
	waitForOSYL();

	try {
	    // add content
	    openTeachingMaterialSection();

	    String parentText =
		    "this is a text resource typed by "
			    + "selenium, hope it works and you see it. Added on "
			    + timeStamp() + " in Firefox";

	    addText(parentText, LEVEL_ATTENDEE);

	    // publish
	    session().click("gwt-uid-5");
	    session().click("//button[contains(text(),'Publier')]");
	    ensureElementPresent("//div[@class='Osyl-PublishView-publishedDate']");
	    ensurePublishDateOK();

	    // The next block purpose is to attach currentSite to parentSiteName 
	    session().selectFrame("relative=parent");
	    // go to OSYLManager
	    goToOsylManagerTool();
	    // Search for the site
	    session().type("//input[@class='gwt-TextBox']",
		    getCurrentTestSiteName());
	    clickSearch();
	    waitForListToShow(getCurrentTestSiteName());
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    ensureElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'Lier')]]");
	    log("Link action is available");
	    clickActionWithLabel("Lier");
	    ensureElementPresent("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/input[@class='OsylManager-form-element']");
	    session().type("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/input[@class='OsylManager-form-element']",
		    parentSiteName);
	    log("Typed parent site name");
	    session().click("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/button[@class='gwt-Button']");
	    waitForLinkDropDownToShow(parentSiteName);
	    session().select("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/select[@class='gwt-ListBox']",
		    "label=" + parentSiteName);
	    clickButtonWithLabel("Lier");
	    // We ensure the confirmation is displayed
	    ensureElementPresent("//img[contains(@title,'Le site a bien ')]");
	    clickButtonWithLabel("Ok");
	    // verify if text is present
	    goToCurrentSite();
	    openTeachingMaterialSection();
	    assertTrue(session().isTextPresent(parentText));

	    String currentSiteText =
		    "this is a text resource typed by "
			    + "selenium, hope it works and you see it. Added on "
			    + timeStamp() + " in Firefox";
	    addText(currentSiteText, LEVEL_ATTENDEE);

	    // publish
	    session().click("gwt-uid-5");
	    session().click("//button[contains(text(),'Publier')]");
	    ensureElementPresent("//div[@class='Osyl-PublishView-publishedDate']");
	    ensurePublishDateOK();

	    // attach child to current site
	    session().selectFrame("relative=parent");
	    goToOsylManagerTool();
	    session().type("//input[@class='gwt-TextBox']", childSiteName);
	    clickSearch();
	    waitForListToShow(childSiteName);
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    ensureElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'Lier')]]");
	    clickActionWithLabel("Lier");
	    ensureElementPresent("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/input[@class='OsylManager-form-element']");
	    session().type("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/input[@class='OsylManager-form-element']",
		    getCurrentTestSiteName());
	    log("Typed site name " + getCurrentTestSiteName());
	    session().click("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/button[@class='gwt-Button']");
	    waitForLinkDropDownToShow(getCurrentTestSiteName());
	    session().select("//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/select[@class='gwt-ListBox']",
		    "label=" + getCurrentTestSiteName());
	    clickButtonWithLabel("Lier");
	    // We ensure the confirmation is displayed
	    ensureElementPresent("//img[contains(@title,'Le site a bien ')]");
	    clickButtonWithLabel("Ok");

	    // verify if text is present
	    goToSite(childSiteName);
	    openTeachingMaterialSection();
	    assertTrue(session().isTextPresent(parentText));
	    assertTrue(session().isTextPresent(currentSiteText));

	    // go to parent site
	    session().selectFrame("relative=parent");
	    goToSite(parentSiteName);
	    openTeachingMaterialSection();

	    // delete text
	    // We delete newly added document
	    session().click("//tr/td/div/table[2]/tbody/tr/td[2]/button");
	    ensureElementPresent("//tr/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	    session().click(
		    "//tr/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	    pause();

	    // ---------------------------------------------------------------------------//
	    // Add Document //
	    // ---------------------------------------------------------------------------//
	    String parentDocumentClickabletext = timeStamp();
	    addDocument("osyl-src[1].zip", parentDocumentClickabletext);

	    // publish
	    session().click("gwt-uid-5");
	    session().click("//button[contains(text(),'Publier')]");
	    ensureElementPresent("//div[@class='Osyl-PublishView-publishedDate']");
	    ensurePublishDateOK();

	    // verify if text of parent is present in current site
	    session().selectFrame("relative=parent");
	    goToCurrentSite();
	    openTeachingMaterialSection();
	    assertTrue(session().isTextPresent(parentDocumentClickabletext));
	    // verify if text of parent is not present
	    assertFalse(session().isTextPresent(parentText));

	    // verify if text of parent is present in child site
	    session().selectFrame("relative=parent");
	    goToSite(childSiteName);
	    openTeachingMaterialSection();
	    assertTrue(session().isTextPresent(parentDocumentClickabletext));
	    // verify if text of parent is not present in child site
	    assertFalse(session().isTextPresent(parentText));

	    // dissociate current site from parent
	    session().selectFrame("relative=parent");
	    goToOsylManagerTool();
	    session().type("//input[@class='gwt-TextBox']",
		    getCurrentTestSiteName());
	    clickSearch();
	    waitForListToShow(getCurrentTestSiteName());
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    ensureElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'tacher')]]");
	    clickActionWithLabel("tacher");
	    // We ensure the confirmation is displayed
	    ensureElementPresent("//img[contains(@title,'Le site a bien ')]");

	    // verify if text is not present in current site
	    goToCurrentSite();
	    openTeachingMaterialSection();
	    assertFalse(session().isTextPresent(parentDocumentClickabletext));

	    // verify if text is not present in child site
	    session().selectFrame("relative=parent");
	    goToSite(childSiteName);
	    openTeachingMaterialSection();
	    assertFalse(session().isTextPresent(parentDocumentClickabletext));
	    // verify if text from current site is present
	    assertTrue(session().isTextPresent(currentSiteText));

	    // dissociate child from current site name
	    session().selectFrame("relative=parent");
	    goToOsylManagerTool();
	    session().type("//input[@class='gwt-TextBox']", childSiteName);
	    clickSearch();
	    waitForListToShow(childSiteName);
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    ensureElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'tacher')]]");
	    clickActionWithLabel("tacher");
	    pause();

	    // verify if text from current site is not present
	    goToSite(childSiteName);
	    openTeachingMaterialSection();
	    assertFalse(session().isTextPresent(currentSiteText));
	    
	    //delete resource in parent for future tests
	    session().selectFrame("relative=parent");
	    goToSite(parentSiteName);
	    openTeachingMaterialSection();
	    session().click("//tr/td/div/table[2]/tbody/tr/td[2]/button");
	    ensureElementPresent(
		    "//tr/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	    session().click(
	    	    "//tr/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	    pause();

	    // publish
	    session().click("gwt-uid-5");
	    session().click("//button[contains(text(),'Publier')]");
	    ensureElementPresent("//div[@class='Osyl-PublishView-publishedDate']");
	    ensurePublishDateOK();

	    logOut();
	    log("testHierarchy: test complete");
	} catch (Exception e) {
	    logAndFail("Hierarchy test FAILED:" + e);
	}
    } // testHierarchy

    private void waitForLinkDropDownToShow(String siteName) {
	int i = 0;
	while(i<ALLOWED_TRIES){
	    if (session().isElementPresent(
		    "//table[@class='OsylManager-form-genericPanel']/tbody/tr/td/select[@class='gwt-ListBox']/option[@value='"
		    +siteName+"']")) {
		log("Found site in drop-down list after " + i + " tries");
		return;
	    }
	    pause();
	    i++;
	}
	logAndFail("Unable to find site [" + siteName
		+ "] in drop-down to link sites.");
    }
    
    
    /**
     * Clicks on the Search button in OSYLManager
     */
    private void clickSearch() {
	session().mouseOver("//div[@class='Osyl-Button Osyl-Button-up']");
	session().mouseDown("//div[@class='Osyl-Button Osyl-Button-up-hovering']");
	session().mouseUp("//div[@class='Osyl-Button Osyl-Button-down-hovering']");
    }
    
    /**
     * Waits until the specified siteName appears in the main OSYLManager list.
     * Fails otherwise, after ALLOWED_TRIES.
     * 
     * @param siteName
     */
    private void waitForListToShow(String siteName) {
	int i = 0;
	while(i<ALLOWED_TRIES){
	    if (session().isElementPresent(
		"//tr[@class='OsylManager-scrollTable-row']/td[2][contains(.,'"
		    +siteName+"')]")) {
		log("Found site in list after " + i + " tries");
		return;
	    }
	    pause();
	    i++;
	}
	logAndFail("Unable to find site [" + siteName + "] in list");
    }


    private void clickActionWithLabel(String label) {
	session().mouseOver(
		"//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'"
		+ label + "')]]");
	session().mouseDown(
		"//div[@class='OsylManager-action OsylManager-action-up-hovering' and ./div[contains(.,'"
		+ label + "')]]");
	session().mouseUp(
		"//div[@class='OsylManager-action OsylManager-action-down-hovering' and ./div[contains(.,'"
		+ label + "')]]");
    }
    
    private void clickButtonWithLabel(String label) {
	session().mouseOver(
		"//div[@class='Osyl-Button Osyl-Button-up' and ./div[contains(.,'"
		+ label + "')]]");
	session().mouseDown(
		"//div[@class='Osyl-Button Osyl-Button-up-hovering' and ./div[contains(.,'"
		+ label + "')]]");
	session().mouseUp(
		"//div[@class='Osyl-Button Osyl-Button-down-hovering' and ./div[contains(.,'"
		+ label + "')]]");
    }

    private void ensurePublishDateOK() {
	try {
	    String dateDiv = session().getText("//div[@class='Osyl-PublishView-publishedDate']");
	    log("published: " + dateDiv);
	    String pubDateStr = dateDiv.substring(dateDiv.indexOf(" : ") + 3);
	    SimpleDateFormat publicationDateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    long pubTime = publicationDateFormatter.parse(pubDateStr).getTime();
	    long diff = System.currentTimeMillis() - pubTime;
	    log("published: " + diff + " ms ago");
	    if (Math.abs(diff) > 30000) {
		logAndFail("Incorrect publish date: " + pubDateStr);
	    }
	} catch (ParseException e) {
	    logAndFail("Unable to parse publish date " + e);
	}
    }
}
