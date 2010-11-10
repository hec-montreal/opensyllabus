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

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class AssociateToParentSite extends AbstractOSYLTest {

    private static int ALLOWED_TRIES = 10;
    
    @Test(groups = "OSYL-Suite", description = "OSYLEditor OsylManagertest. Associate to a parent site")
    @Parameters( { "webSite" })
    public void testAssociateToParentSite(String webSite) throws Exception {
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
	    pause();

	    // attach to parent
	    session().selectFrame("relative=parent");
	    goToOsylManagerTool();
	    session().type("//input[@class='gwt-TextBox']",
		    getCurrentTestSiteName());
	    session().mouseOver("//div[@class='Osyl-Button Osyl-Button-up']");
	    session().mouseDown(
		    "//div[@class='Osyl-Button Osyl-Button-up-hovering']");
	    session().mouseUp(
		    "//div[@class='Osyl-Button Osyl-Button-down-hovering']");
	    pause();
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    pause();
	    assertTrue(session().isElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'Lier')]]"));
	    session()
		    .mouseOver(
			    "//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseDown(
			    "//div[@class='OsylManager-action OsylManager-action-up-hovering' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseUp(
			    "//div[@class='OsylManager-action OsylManager-action-down-hovering' and ./div[contains(.,'Lier')]]");
	    pause();
	    int i =0;
	    while(!session().isElementPresent("//table[@class='OsylManager-form-element']/tbody/tr/td/select[@class='gwt-ListBox']/option[@value='"
		    +parentSiteName+"']") && i<ALLOWED_TRIES){
		pause();
		i++;
	    }
	    if(!session().isElementPresent(
		    "//table[@class='OsylManager-form-element']/tbody/tr/td/select[@class='gwt-ListBox']/option[@value='"
		    +parentSiteName+"']")) {
		logAndFail("Cannot attach to parent site " + parentSiteName);
	    }
	    session().select("//table[@class='OsylManager-form-element']/tbody/tr/td/select[@class='gwt-ListBox']",
		    "label=" + parentSiteName);
	    pause();
	    session()
		    .mouseOver(
			    "//div[@class='Osyl-Button Osyl-Button-up' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseDown(
			    "//div[@class='Osyl-Button Osyl-Button-up-hovering' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseUp(
			    "//div[@class='Osyl-Button Osyl-Button-down-hovering' and ./div[contains(.,'Lier')]]");
	    pause();
	    assertTrue(session().isElementPresent("//div[contains(.,'bien')]"));
	    session()
		    .mouseOver(
			    "//div[@class='Osyl-Button Osyl-Button-up' and ./div[contains(.,'Fermer')]]");
	    session()
		    .mouseDown(
			    "//div[@class='Osyl-Button Osyl-Button-up-hovering' and ./div[contains(.,'Fermer')]]");
	    session()
		    .mouseUp(
			    "//div[@class='Osyl-Button Osyl-Button-down-hovering' and ./div[contains(.,'Fermer')]]");

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
	    pause();

	    // attach child to current site
	    session().selectFrame("relative=parent");
	    goToOsylManagerTool();
	    session().type("//input[@class='gwt-TextBox']", childSiteName);
	    session().mouseOver("//div[@class='Osyl-Button Osyl-Button-up']");
	    session().mouseDown(
		    "//div[@class='Osyl-Button Osyl-Button-up-hovering']");
	    session().mouseUp(
		    "//div[@class='Osyl-Button Osyl-Button-down-hovering']");
	    pause();
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    pause();
	    assertTrue(session().isElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'Lier')]]"));
	    session()
		    .mouseOver(
			    "//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseDown(
			    "//div[@class='OsylManager-action OsylManager-action-up-hovering' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseUp(
			    "//div[@class='OsylManager-action OsylManager-action-down-hovering' and ./div[contains(.,'Lier')]]");
	    pause();
	    while(!session().isElementPresent("//table[@class='OsylManager-form-element']/tbody/tr/td/select[@class='gwt-ListBox']/option[@value='"
		    +getCurrentTestSiteName()+"']") && i<ALLOWED_TRIES){
		pause();
		i++;
	    }
	    assertTrue(session().isElementPresent("//table[@class='OsylManager-form-element']/tbody/tr/td/select[@class='gwt-ListBox']/option[@value='"
		    +getCurrentTestSiteName()+"']"));
	    session().select("//table[@class='OsylManager-form-element']/tbody/tr/td/select[@class='gwt-ListBox']",
		    "label=" + getCurrentTestSiteName());
	    pause();
	    session()
		    .mouseOver(
			    "//div[@class='Osyl-Button Osyl-Button-up' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseDown(
			    "//div[@class='Osyl-Button Osyl-Button-up-hovering' and ./div[contains(.,'Lier')]]");
	    session()
		    .mouseUp(
			    "//div[@class='Osyl-Button Osyl-Button-down-hovering' and ./div[contains(.,'Lier')]]");
	    pause();
	    assertTrue(session().isElementPresent("//div[contains(.,'bien')]"));
	    session()
		    .mouseOver(
			    "//div[@class='Osyl-Button Osyl-Button-up' and ./div[contains(.,'Fermer')]]");
	    session()
		    .mouseDown(
			    "//div[@class='Osyl-Button Osyl-Button-up-hovering' and ./div[contains(.,'Fermer')]]");
	    session()
		    .mouseUp(
			    "//div[@class='Osyl-Button Osyl-Button-down-hovering' and ./div[contains(.,'Fermer')]]");

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
	    // We delete new added docuement
	    session().click("//tr/td/div/table[2]/tbody/tr/td[2]/button");
	    pause();

	    session()
		    .click(
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
	    pause();

	    // verify if text of parentis present in current site
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

	    // dissociate current site form parent
	    session().selectFrame("relative=parent");
	    goToOsylManagerTool();
	    session().type("//input[@class='gwt-TextBox']",
		    getCurrentTestSiteName());
	    session().mouseOver("//div[@class='Osyl-Button Osyl-Button-up']");
	    session().mouseDown(
		    "//div[@class='Osyl-Button Osyl-Button-up-hovering']");
	    session().mouseUp(
		    "//div[@class='Osyl-Button Osyl-Button-down-hovering']");
	    pause();
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    pause();
	    assertTrue(session().isElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'tacher')]]"));
	    session()
		    .mouseOver(
			    "//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'tacher')]]");
	    session()
		    .mouseDown(
			    "//div[@class='OsylManager-action OsylManager-action-up-hovering' and ./div[contains(.,'tacher')]]");
	    session()
		    .mouseUp(
			    "//div[@class='OsylManager-action OsylManager-action-down-hovering' and ./div[contains(.,'tacher')]]");
	    pause();

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
	    session().mouseOver("//div[@class='Osyl-Button Osyl-Button-up']");
	    session().mouseDown(
		    "//div[@class='Osyl-Button Osyl-Button-up-hovering']");
	    session().mouseUp(
		    "//div[@class='Osyl-Button Osyl-Button-down-hovering']");
	    pause();
	    session().click(
		    "//tr[@class='OsylManager-scrollTable-row']/td/input");
	    pause();
	    assertTrue(session().isElementPresent("//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'tacher')]]"));
	    session()
		    .mouseOver(
			    "//div[@class='OsylManager-action OsylManager-action-up' and ./div[contains(.,'tacher')]]");
	    session()
		    .mouseDown(
			    "//div[@class='OsylManager-action OsylManager-action-up-hovering' and ./div[contains(.,'tacher')]]");
	    session()
		    .mouseUp(
			    "//div[@class='OsylManager-action OsylManager-action-down-hovering' and ./div[contains(.,'tacher')]]");
	    pause();

	    // verify if text from current site is not present
	    goToSite(childSiteName);
	    openTeachingMaterialSection();
	    assertFalse(session().isTextPresent(currentSiteText));
	    
	    //delete ressource in parent for future tests
	    session().selectFrame("relative=parent");
	    goToSite(parentSiteName);
	    openTeachingMaterialSection();
	    session().click("//tr/td/div/table[2]/tbody/tr/td[2]/button");
	    pause();

	    session()
		    .click(
			    "//tr/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
	    pause();
	    // publish
	    session().click("gwt-uid-5");
	    session().click("//button[contains(text(),'Publier')]");
	    pause();

	    logOut();
	    log("testAddLecture: test complete");
	} catch (Exception e) {
	    logAndFail("Associate to parent FAILED:" + e);
	}
    } // testAddLecture

}
