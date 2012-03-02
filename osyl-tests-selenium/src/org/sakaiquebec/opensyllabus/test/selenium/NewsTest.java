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

import org.sakaiquebec.opensyllabus.test.selenium.utils.ResourceXpathHelper;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * Tests the addition of a contact-info resource. The exact steps are: log in
 * as admin, creates a new site if needed, load it (OpenSyllabus is the first
 * and only page), enters in the contact info page, click Contact in the Add
 * menu, open the editor, check that we get errors when trying to close the
 * editor without completing the required fields, fill in those fields,
 * change the rubric (randomly) click OK, check the text is here, check the
 * rubric is visible, click save, click public overview,enters in the contact 
 * info page,check the text is here, check the rubric is visible, log out.
 * 
 *@author<a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 *@author<a href="mailto:bouchra.laabissi.1@ens.etsmtl.ca">Bouchra Laabissi</a>
 */
public class NewsTest extends AbstractOSYLTest {
	
    @Test(groups = "OSYL-Suite", description =
	"OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void TestAddNews(String webSite) throws Exception {
	

	if (true) {
	    prettyLog("NewsTest is now obsolete!");
	    return;
	}
	
	logStartTest();
	
	// We log in
	logInAsAdmin(webSite);
	try {
	    goToCurrentSite();	    
	} catch (IllegalStateException e) {
	    createTestSite();
		logFile(OSYL_TEST, CT_002, PASSED);
	    goToCurrentSite();
	}
	waitForOSYL();
	logFile(OSYL_TEST, CT_069, PASSED);		
	
	// ---------------------------------------------------------------------------//
	// Add News in News Unit //
	// ---------------------------------------------------------------------------//	
	// We keep track of how many resources are showing to check that it
	// is incremented as expected when we add one
	int resNb = ResourceXpathHelper.getNbResource();
	log("We start with " + resNb + " resources");
        
	//Add News
	prettyLog("Add a News");
	clickAddItem("addNews");
	
	// We check that our news was added
	int resNb2 = ResourceXpathHelper.getNbResource();
	log("We now have " + resNb2 + " resources");
	
	//It is not necessary.		
	/**
	if ( 1+ resNb != resNb2) {
	    logAndFail("Resource count not incremented as expected!");
	} else {
	    log ("OK Text resource added");
	}
	**/
	saveCourseOutline();
	
	// open text resource editor
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	session().click(ResourceXpathHelper.getButtonModify(0));
	
	// Type some text in the rich-text area
	if (inFireFox()) {
	    // type text
	    session().selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
	    String newText = "this is a text resource typed by "
		+ "selenium, hope it works and you see it. Added on "
		+ timeStamp() + " in Firefox";

	    session().type("//html/body", newText);
	    // close editor
	    session().selectFrame("relative=parent");
	    session().click("//td/table/tbody/tr/td[1]/button");
	    // check if text is visible
		if (!session().isTextPresent(newText)) {
			logAndFail("Expected to see text [" + newText
					+ "] after text edition");
			// Add message to log file
			logFile(NEWS_TEST, CT_067, FAILED);
		}
	    log("OK: Text resource edited");
	} else {
	    log("RichText edition can only be tested in Firefox");
	    // close editor
	    session().click("//td/table/tbody/tr/td[1]/button");
	}
	
	
	saveCourseOutline();
	//Add message to log file
	logFile(NEWS_TEST, CT_067, PASSED);
	
	session().selectFrame("relative=parent");
	logOut();
	
	logEndTest();
    } // TestAddNews
    
}