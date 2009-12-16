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

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import java.util.*;


import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * Tests all the features in the section Electronic tool submission. The exact
 * steps are: log in as admin, creates a new site if needed, load it 
 * (OpenSyllabus is the first and only page), enters in the Assessment section, 
 * enters in the first assessment, Add electronic tool submission, edit it,
 * the last assessment, delete the first assessment unit,check if 
 * OpenSyllabus displays a error message if a mandatory fields are not filled  
 * or filled with wrong values,Add text in the last assessment, and check if 
 * the text is visible in attendee overview and not visible in the public 
 * overview, add a hyperlink and check if the hyperlink is visible in attendee 
 * overview and not visible in the public overview, add a document and check if
 * the document is visible in attendee overview and not visible in the public 
 * overview, add a citation list and check if the new citation is visible in 
 * attendee overview and not visible in the public overview, switch the two 
 * last assessments, delete the last assessment, save all modifications,
 * log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:bouchra.laabissi@hec.ca">Bouchra Laabissi</a>
 */

public class ElectronicToolSubmission extends AbstractOSYLTest{
    
    @Test(groups = "OSYL-Suite", description =
    	"OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void TestAddElectronicTool(String webSite) throws Exception {
    	// We log in
    	logInAsAdmin(webSite);
    	try {
    	    goToSite();
    	} catch (IllegalStateException e) {
    	    createTestSite();
    	    goToSite();
    	}
    	waitForOSYL();
    	
    	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
    			"td/div[contains(text(),'valuations')]");
    	pause();
    	
    	// If we don't have a Assessment we add one
	int LectNb = getResourceCount();
	if(LectNb == 0){
            //We add a first Lecture
            clickAddItem("addAssessmentUnit");
            pause();
        }
    	enterFirstLecture();
    	pause();
    	//int resNb = getResourceCount();
	//log("We start with " + resNb + " resources");
    	
    	//Add new Electronic Tool Submission
    	clickAddItem("addAssignment");
    	pause();
    	
    	//Edit the new added rubric
    	session().click("//tr[2]/td/div/table[2]/tbody/tr/td/button");
    	
    	//Set Title rubric to "Modalité de remise et pénalité"
    	session().select("listBoxFormElement", "index=5");
    	//Set dissemination level to attendee
    	session().select("//td[2]/table/tbody/tr[2]/td/select", "index=0");
    	//
    	session().click("//table/tbody/tr[2]/td/table/tbody/tr/td[2]/span/" +
    			"input");
    	// Add two days to today date
    	GregorianCalendar ExpireDate = new GregorianCalendar();
    	ExpireDate.add(Calendar.DATE,2);
    	
    	
    	//Fill the opening date field
    	session().type("//input[@type='text']", new SimpleDateFormat
    		("yyyy-MM-dd").format(new Date()));
    	//Fill the expiry date field
    	session().type("//td[4]/input", new SimpleDateFormat
    		("yyyy-MM-dd").format(ExpireDate.getTime()));
    	//type the text that the student must click to submit his work
    	session().type("//tr[3]/td/input", "Cliquez ici pour remettre votre" +
    			" travail"+ timeStamp());
    	//Click OK to close editor
    	session().click("//td/table/tbody/tr/td[1]/button");
    	pause();
    	
    	//Save modifications
	saveCourseOutline();
	pause(); 
	
	//Publish
	session().click("gwt-uid-3");
	pause();
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();
	session().click("//tr[3]/td/table/tbody/tr/td[2]/button");
	pause();
	
	//Open Submission tools
    	session().selectFrame("relative=parent");
    	session().click("//div[@id='toolMenu']/ul/li[2]/a/span");
    	pause();
    	
    	session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
    	int resNb2 = getElectronicCount();
	log("We now have " + resNb2 + " Electronic tool submission");
	
	//Configuring multiple sends
	session().click("//html/body/div/form/table/tbody/tr[2]/td[2]/div/a");
	pause();
	session().click("allowResToggle");
	session().select("allow_resubmit_number", "label=3");
	session().click("post");
	pause();
	session().click("post");
	pause();
	
	//Log as student
	AddStudent();
 	LogAsStudent();
	
 	session().open("http://osyldev.hec.ca:12345/portal/site/" + getCurrentTestSiteName());
 	waitForPageToLoad();
    	
 	//Open Submission tools
    	session().selectFrame("relative=parent");
    	session().click("//div[@id='toolMenu']/ul/li[2]/a/span");
    	pause();
    	
    	//Open Submission form
    	session().click("//html/body/div/form/table/tbody/tr[2]/td[2]/h4/a");
    	waitForPageToLoad();
    	
    	//Upload file
    	session().click("attach");
    	pause();
    	session().type("upload", "C:\\Documents and Settings\\clihec3\\Local" +
    			" Settings\\Temporary Internet Files\\Content.IE5\\" +
    			"B1AO9TBK\\fichier-excel[4].xlsx");
 	pause();
 	session().click("attachButton");
 	pause();
 	session().click("Assignment.view_submission_honor_pledge_yes");
 	
 	//Send work for the first time
 	session().click("post");
 	pause();
    	session().click("eventSubmit_doConfirm_assignment_submission");
 	 	
    	session().selectFrame("relative=parent");
    	session().click("//div[@id='toolMenu']/ul/li[1]/a/span");
    	pause();
    	
    	//Open Submission tools
    	session().selectFrame("relative=parent");
    	session().click("//div[@id='toolMenu']/ul/li[2]/a/span");
    	pause();
    	//Open Submission form
    	session().click("//html/body/div/form/table/tbody/tr[2]/td[2]/h4/a");
    	waitForPageToLoad();
    	
    	//Upload file
    	session().click("attach");
    	pause();
    	session().type("upload", "C:\\Documents and Settings\\clihec3\\Local" +
    			" Settings\\Temporary Internet Files\\Content.IE5\\" +
    			"B1AO9TBK\\fichier-excel[4].xlsx");
 	pause();
 	session().click("attachButton");
 	pause();
 	session().click("Assignment.view_submission_honor_pledge_yes");
 	
 	//Send work for the second time
 	session().click("post");
 	pause();
    	session().click("eventSubmit_doConfirm_assignment_submission");
    	

    	
	
	//Log out
	session().selectFrame("relative=parent");
	logOut();
	log("ElectronicToolSubmission: test complete");


    }
    private int getResourceCount() {
	return session().getXpathCount(
		"//div[@class=\"Osyl-UnitView-ResPanel\"]").intValue();

	}
    
    private String getStudent() {
	String Matricule= "11135614";
	return Matricule;
    }
    
    private void AddStudent() throws Exception {
	log("Add Student " + getStudent());
	//session().open("http://osyldev.hec.ca:12345/portal/site/" + getCurrentTestSiteName());
	//waitForPageToLoad();
	session().selectFrame("relative=parent");
	session().click("//div[@id='toolMenu']/ul/li[4]/a/span");
	waitForPageToLoad();
	// Ensure the student doesn't already exist
	session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
	if(session().isTextPresent(getStudent())){
	    log("Student '" + getStudent() + "' already exists!");
	}else{
        	session().click("link=Ajouter participants");
        	waitForPageToLoad();
        	session().type("content::officialAccountParticipant", getStudent());
        	pause();
        	session().click("//html/body/div/div/form/p/input");
        	pause();
        	session().click("content::role-row:0:role-select");
        	pause();
        	session().click("//html/body/div/div/form/p/input");
        	pause();
        	session().click("//html/body/div/div/form/p/input");
        	pause();
        	session().click("//html/body/div/div/form/p/input");
        	pause();
	}
    }
    private void LogAsStudent() throws Exception {
	session().selectFrame("relative=parent");
	logOut();
	session().type("eid", getStudent());
	pause();
	session().type("pw", "hec123456");
	pause();
	session().click("submit");
	pause();
    }
    private int getElectronicCount() {
	return session().getXpathCount(
		"//div[@class=\"itemAction\"]").intValue();
    }
    
    

}