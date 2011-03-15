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
public class ContactInfoTest extends AbstractOSYLTest {
	
    @Test(groups = "OSYL-Suite", description =
	"OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void testAddContactInfo(String webSite) throws Exception {
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
	
//---------------------------------------------------------------------------//
//				Add Contact		                     //
//---------------------------------------------------------------------------//
        
	openContactInfoSection();
	
	//Add Contact Information
	clickAddItem("addPerson");
	//Add message to log file
	logFile(TEXT_TEST, CT_007, PASSED);
	
//---------------------------------------------------------------------------//
//				Modify Contact		                     //
//---------------------------------------------------------------------------//	
	//Edit Contact Information
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	pause();
	
	//Check if Opensyllabus displays a message error when the user click OK 
	//without fill in the fields First Name, Last Name and Title.	
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();
	pause();
	//isTextPresent(text='Erreur') doesn't work for modal windows 
	//It needs use isElementPresent(elementOfText)
	String Erreur1 = "//div[contains(text(),'Erreur')]";
	if (!session().isElementPresent(Erreur1)) {
	    logAndFail("Expected to see text [" + Erreur1 
		    + "] after text edition");	
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);
	}
	
	pause();
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	
	//Check if Opensyllabus displays a message error when the user click OK 
	//without fill in the fields First Name, Last Name. 
	String xpathRole = "//select[@name=\"listBoxContactInfoRole\"]";
	String newText = getRandomOption(xpathRole);
	session().select(xpathRole, newText);

	
	session().click("//td/table/tbody/tr/td[1]/button");
	
	if (!session().isElementPresent(Erreur1)) {	
	//if (!session().isTextPresent(Erreur1)) {
	    logAndFail("Expected to see text [" + Erreur1 
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	    
	}
	
	pause();
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
	
	//Check if Opensyllabus displays a message error when the user click OK 
	//without fill in the field Last Name.
	
	String newText1 = timeStamp();
	session().type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);
	
	session().click("//td/table/tbody/tr/td[1]/button");
	if (!session().isElementPresent(Erreur1)) {	
	//if (!session().isTextPresent(Erreur1)) {
	    logAndFail("Expected to see text [" + Erreur1 
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);		
	}
	
	pause();
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	pause();
		
	//Select required fields
	session().select(xpathRole, newText);
	
	//Select rubric.
	String selectedRubric = getRandomRubric();
	log("Selecting rubric [" + selectedRubric + "]");
	changeRubric(selectedRubric);

	
	//Fill in the required informations (FirstName, Last Name and Title) 	
	session().type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);
	
	String newText2 = "YYYYYYYY";
	session().type("//input[@title=\"Nom:\"]", newText2);
	pause();
	
	String newText3 = "Lundi AM et Mardi PM" + timeStamp();
	if (inFireFox()) {
        	session().selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
        	session().type("//html/body", newText3);
		session().selectFrame("relative=parent");
	}	
	
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();
	
	//Check if the new rubric is visible.
	if (!session().isTextPresent(newText1)) {
	    logAndFail("Expected to see text [" + newText1 
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	    
	}
	if (!session().isTextPresent(newText2)) {
	    logAndFail("Expected to see text [" + newText2 
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	    
	}
	if (inFireFox()) {
        	if (!session().isTextPresent(newText3)) {
        	    logAndFail("Expected to see text [" + newText3 + timeStamp()
        		    + "] after text edition");
        		//Add message to log file
        		logFile(TEXT_TEST, CT_018, FAILED);        	    
        	}
	}

	if (!session().isTextPresent(selectedRubric)) {
	    logAndFail("Expected to see rubric [" + selectedRubric
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	    
	}
	log("OK: Selected rubric is visible");

	//Add message to log file
	logFile(TEXT_TEST, CT_018, PASSED);

	
	//Save the new rubric
	saveCourseOutline();
	pause();
	
	//Overview
    session().click(BUTTON_PREVIEW); //It was "gwt-uid-8"
	pause();
	// Attendee Overview
	session().click(
		    "//html/body/div/div/table/tbody/tr[2]/td[2]/div/"
			    + "div/table/tbody/tr/td");
	pause();
	//session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
	//Will be tested when the problem is resolved(public & attendee)
	//session().click("//td[@class=\"gwt-MenuItem\"]");
	
	//Click on Contact Informations
	openContactInfoSection();
	pause();
	
	// check if the new rubric is visible.	
	if (!session().isTextPresent(newText1)) {
	    logAndFail("Expected to see text [" + newText1 
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	  	    
	}
	if (!session().isTextPresent(newText2)) {
	    logAndFail("Expected to see text [" + newText2 
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	  	    
	}
	
	if (inFireFox()) {
        	if (!session().isTextPresent(newText3)) {
        	    logAndFail("Expected to see text [" + newText3 + timeStamp()
        		    + "] after text edition");
        		//Add message to log file
        		logFile(TEXT_TEST, CT_018, FAILED);	          	    
        	}
	}

	if (!session().isTextPresent(selectedRubric)) {
	    logAndFail("Expected to see rubric [" + selectedRubric
		    + "] after text edition");
		//Add message to log file
		logFile(TEXT_TEST, CT_018, FAILED);	  	    
	}
	log("OK: Selected rubric is visible");
	pause();
	//Close Overview
	session().click("//html/body/table/tbody/tr[2]/td/div/div[2]/table/tbody/tr/td");
	//Add message to log file
	logFile(TEXT_TEST, CT_018, PASSED);	  
	
//---------------------------------------------------------------------------//
//				Delete Contact		                     //
//---------------------------------------------------------------------------//	
	
	//Click Contact Information
	openContactInfoSection();
	
	//Add Contact Information
	clickAddItem("addPerson");
	
	//Edit Contact Information
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	pause();
	
	//Select required fields
	session().select(xpathRole, newText);
	
	//Fill in the required informations (FirstName, Last Name and Title) 
	session().type("//input[@class=\"Osyl-ContactInfo-TextBox\"]", newText1);
	
	session().type("//input[@title=\"Nom:\"]", newText2);
	pause();

	if (inFireFox()) {
        	session().selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
        	session().type("//html/body", newText3);
		session().selectFrame("relative=parent");
	}	
	
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();
	
	//We delete new contact 
    session().click("//tr[2]/td/div/table[2]/tbody/tr/td[2]/button");
    pause();
    
    session().click("//tr[2]/td[2]/div/table/tbody/tr[2]/td/table/tbody/tr/td/button");
    pause();
    log("Contact deleted");
    pause();

	//Add message to log file
	logFile(TEXT_TEST, CT_024, PASSED);	 

	//Save modifications
	saveCourseOutline();
	pause();
	
	//Log out
	session().selectFrame("relative=parent");
	logOut();
	log("=================================");	
	log("testAddContactInfo: test complete");
	log("=================================");	
    } // testAddContactInfo
    
    private void openContactInfoSection(){
	// Click on Coordo Section
    pause();
	if (inFireFox()) {
	    session().mouseDown(
		    "//div[@class=\"gwt-TreeItem\"]/div/"
			    + "div[contains(text(),'Coordo')]");
	} else {
		// IE
		String imageLocator = "//div[contains(text(),'Coordonn')]";	
		session().mouseDownAt(imageLocator, "10,10");
		session().mouseUpAt(imageLocator, "10,10");	    
	}
	pause();
    }
        
}