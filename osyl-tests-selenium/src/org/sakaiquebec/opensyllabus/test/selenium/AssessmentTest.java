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
import java.util.Date;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;

/**
 * Tests all the features in the section Assessment. The exact steps are: log 
 * in as admin, creates a new site if needed, load it (OpenSyllabus is the 
 * first ), enters in the Assessment section, Add two assessments,
 * edit the last assessment, delete the first assessment unit,check if 
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

public class AssessmentTest extends AbstractOSYLTest{
    
    @Test(groups = "OSYL-Suite", description =
    	"OSYLEditor test. Add a contact resource, edit it and save the changes")
    @Parameters( { "webSite" })
    public void TestAddAssessment(String webSite) throws Exception {
    	// We log in
    	logInAsAdmin(webSite);
    	try {
    	    goToSite();
    	} catch (IllegalStateException e) {
    	    createTestSite();
    	    goToSite();
    	}
    	waitForOSYL();
    	
//---------------------------------------------------------------------------//
//      		Add Assessment Unit		                     //
//---------------------------------------------------------------------------//
    	
    	//Click Assessment section 
        session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");
        
        // We keep track of how many resources are showing to check that it
	// is incremented as expected when we add one
	int resNb = getResourceCount()-1;
	log("We start with " + resNb + " resources");
	
	pause();
        
        //We add a first Assessment Unit
	clickAddItem("addAssessmentUnit");
	
	//We add a second Assessment Unit
	clickAddItem("addAssessmentUnit");
	
//---------------------------------------------------------------------------//
//                    Modify Assessment Unit			             //
//---------------------------------------------------------------------------//
	
    	int Position = resNb +3;
    	//We edit the last assessment
	session().click("//tr["+Position+"]/td/table/tbody/tr/td[2]/div/" +
			"table[2]/tbody/tr/td/button");
	pause();
	
	//We fill the weighting field
	String newText3 = "20";
	session().type("//input[@type='text']", newText3);
	
	//We select randomly the assessment type field
	String xpathRole1 = "//select[@class=\"gwt-ListBox\"]";
	String newText5 = getRandomOption(xpathRole1);
	session().select(xpathRole1, newText5);
	
	
	//We close the assessment editor
	session().click("//table/tbody/tr/td/table/tbody/tr/td/table/tbody/" +
			"tr/td/button");
	pause();
	
	
	
//---------------------------------------------------------------------------//
//     			 Modify Assessment Unit			             //
//---------------------------------------------------------------------------//	
	
	//We open the last assessment
	int Val = resNb + 2;
	session().click("link=" + Val + "-");
	
	//We edit the last assessment
	session().click("//table/tbody/tr/td/div/table[2]/tbody/tr/td/button");
	pause();
	
	//We check if Opensyllabus displays a message error when the user click 
	//OK without fill the mandatory fields
	//--------------------------------------------------------------------//
	
	//We empty the fields "Weighting"
	session().type("//input[@type='text']", "");
	
	//We empty the the assessment type field
	session().select(xpathRole1, "label=");
	
	//We click OK without filling the mandatory fields
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//We verify if Opensyllabus display a message error
	String Erreur = "Erreur" ;
	if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur 
		    + "] after text edition");
		}
	log("OK: Error displayed");
	
	//We click OK to return to Assessment editor after message error 
	//displaying
	session().click("//tr[2]/td/table/tbody/tr/td/button");	
	
	
	//We check if the field "assessment type" is mandatory
	//--------------------------------------------------------------------//
	
	//We fill the weighting field
	session().type("//input[@type='text']", newText3);	
	
	//We select randomly the location field
	String xpathRole2 = "//tr/td/table/tbody/tr/td[3]/table/tbody/tr[2]/td/select";
	String newText6 = getRandomOption(xpathRole2);
	session().select(xpathRole2, newText6);
	pause();
	
	//We select randomly the work mode field
	String xpathRole3 = "//tr[2]/td/table/tbody/tr/td/table/tbody/tr[2]/td/select";
	String newText7 = getRandomOption(xpathRole3);
	session().select(xpathRole3, newText7);
	pause();
	
	//We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//We check if Opensyllabus displays a message error 
	if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur 
		    + "] after text edition");
		}
	log("OK: Error displayed");
	
	//We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");	
	
	//We check if the field "Location" is mandatory
	//--------------------------------------------------------------------//
	
	//We select randomly the assessment type field
	session().select(xpathRole1, newText5);
	
	//We empty the fields "Location"
	session().select(xpathRole2, "label=");
	
	//We select randomly the work mode field
	session().select(xpathRole3, newText7);
	
	//We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//We check if Opensyllabus displays a message error 
	if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur 
		    + "] after text edition");
		}
	log("OK: Error displayed");
	
	//We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	
	//We check if the field "Work mode" is mandatory
	//--------------------------------------------------------------------//
	
	//We select randomly the location field
	session().select(xpathRole2, newText6);
	
	//We empty the fields "Work mode"
	session().select(xpathRole3, "label=");
	
	//We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//We check if Opensyllabus displays a message error 
	if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur 
		    + "] after text edition");
		}
	
	//We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");
		
	//We check if the field "Weighting" is mandatory
	//-------------------------------------------------------------------//
	
	//We select randomly the work mode field
	session().select(xpathRole3, newText7);
	
	//We empty the fields "Weighting"
	session().type("//input[@type='text']", "");
	
	//We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//We check if Opensyllabus displays a message error 
	if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur 
		    + "] after text edition");
		}
	log("OK: Error displayed");
	
	//We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");
	
	//We check if Opensyllabus displays a message error when the user enter 
	//a wrong date format.	
	//-------------------------------------------------------------------//
	
	//We fill the weighting field
	session().type("//input[@type='text']", newText3);
	
	//We fill the assessment name field
	String newText1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	/*session().type("//tr[2]/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td" +
			"/input", newText1);
	
	//We fill the assessment date field by a wrong format
	//String newText2 = timeStamp();
	//session().type("//tr[2]/td/input", newText2);
	
	//We click OK to close assessment editor
	session().click("//td/table/tbody/tr/td[1]/button");
	
	//We check if Opensyllabus displays a message error 
	if (!session().isTextPresent(Erreur)) {
	    logAndFail("Expected to see text [" + Erreur 
		    + "] after text edition");
		}
	log("OK: Error displayed");
	
	//We click OK to return to assessment editor
	session().click("//tr[2]/td/table/tbody/tr/td/button");	

	
	//We Fill the assessment date field
	session().type("//tr[2]/td/input", newText1);
	
	//We fill the assessment name field
	String Name = "Evaluation" + timeStamp();
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]", 
		Name);*/

	//We close Editor
	session().click("//td/table/tbody/tr/td[1]/button");
	pause();

	//TODO: Check if the new rubric is visible.
	//if (!session().isTextPresent(newText2)){
	// logAndFail("Expected to see text [" + newText2 +" ("+ newText3 + ")"
	//	    + "] after text edition");
	//}
	

//---------------------------------------------------------------------------//
//                   Add Text in Assessment Unit		             //
//---------------------------------------------------------------------------//
	
	//Open Assessment section
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
		"/div[contains(text(),'valuations')]");
	//Open last Assessment unit
	if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
	//Add Text in Assessment Unit
	clickAddItem("addText");

	//We edit the new text rubric
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	//We select randomly the rubric name  
	String selectedRubric1 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric1 + "]");
	changeRubric(selectedRubric1);
	
	
	// Type some text in the rich-text area
	if (inFireFox()) {
	    // type text
	    session().selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");
	    String newText9 = "this is a text resource typed by "
		+ "selenium, hope it works and you see it. Added on "
		+ timeStamp() + " in Firefox";
	    session().type("//html/body", newText9);
	    // close editor
	    session().selectFrame("relative=parent");
	    session().click("//td/table/tbody/tr/td[1]/button");
	    // check if text is visible
	    if (!session().isTextPresent(newText9)) {
		logAndFail("Expected to see text [" + newText9 
			+ "] after text edition");
	    }
	    log("OK: Text resource edited");
	} else {
	    log("RichText edition can only be tested in Firefox");
	    // close editor
	    session().click("//td/table/tbody/tr/td[1]/button");
	}
	
	//Save modifications
	saveCourseOutline();
	pause();
	
	if (inFireFox()) {
	    
        	//Overview
        	session().click("gwt-uid-7");
        	//Attendee Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
		"div/table/tbody/tr/td");
        	pause();

        	//Click Assessment section 
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
		"/div[contains(text(),'valuations')]");
                
        	//Open last Assessment unit
        	if(Val < 10){
        	    session().click("link=0" + Val + " -");
        	}else{
        	    session().click("link=" + Val + " -");
        	}
        	pause();
                        	
        	if (!session().isTextPresent(selectedRubric1)) {
        	    logAndFail("Expected to see rubric [" + selectedRubric1
        		+ "] after text edition");
        	}
        	log("OK: Selected rubric is visible");
        	
        	//Close Overview
        	session().click("//html/body/table/tbody/tr/td/table/tbody" +
		"/tr[2]/td[2]/div/div/table/tbody/tr/td");
        	
        	//Overview
        	session().click("gwt-uid-7");
        	//Public Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]" +
		"/div/div/table/tbody/tr[2]/td");
        	pause();
        	
        	//Click Assessment section 
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");
                
        	//Open last Assessment unit
        	if(Val < 10){
        	    session().click("link=0" + Val + " -");
        	}else{
        	    session().click("link=" + Val + " -");
        	}
        	        	
                if (!(session().isTextPresent(selectedRubric1))) {
                    logAndFail("Expected to see rubric [" + selectedRubric1
                		+ "] after text edition on public overview");
                }
                log("OK: Selected rubric is visible");
        	        	
        	//Close Overview
                session().click("//html/body/table/tbody/tr/td/table/tbody" +
		"/tr[2]/td[2]/div/div/table/tbody/tr/td");
	}
	
	
//---------------------------------------------------------------------------//
//                      Add Hyperlink in Assessment Unit	             //
//---------------------------------------------------------------------------//
	
	//Click Assessment section 
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
	"/div[contains(text(),'valuations')]");
        
        //Edit first Assessment unit
	if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
	
	//Add Hyperlink in Assessment Unit
	clickAddItem("addURL");
	
	//We edit the new Hyperlink rubric
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	
	//We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select","index=0");
	
	//We select randomly the rubric name
	String selectedRubric2 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric2 + "]");
	changeRubric(selectedRubric2);
	
	//We type the clickable text
	String newText11 = timeStamp();
	session().type("//td[2]/input", newText11);

	//We type the URL link
	String newText10 = "http://webmail.hec.ca/";
	session().type("//tr[2]/td[2]/input", newText10);
	pause();

	//We click OK to close editor
	session().click("//td/table/tbody/tr/td[1]/button");

	//We click URL to test 
	session().click("link=" + newText11);

	//Save modifications
	saveCourseOutline();
	pause();
	
	if (inFireFox()) {
        	//Overview
        	session().click("gwt-uid-7");
        	//Attendee Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
        			"div/table/tbody/tr/td");
        	pause();

        	//Click Assessment section 
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
		"/div[contains(text(),'valuations')]");

        	//Edit the last Assessment unit
        	if(Val < 10){
        	    session().click("link=0" + Val + " -");
        	}else{
        	    session().click("link=" + Val + " -");
        	}
        
        	if (!session().isTextPresent(selectedRubric2)) {
        	    logAndFail("Expected to see rubric [" + selectedRubric2
        		+ "] after text edition");
        	}
        	log("OK: Selected rubric is visible");
        	
        	//Close Overview
        	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");
        	pause();
        	
        	/*/Overview
        	session().click("gwt-uid-6");
        	//Public Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]" +
        			"/div/div/table/tbody/tr[2]/td");
        	pause();
        	//Click Assessment section 
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");

        	//Edit the last Assessment unit
        	if(Val < 10){
        	    session().click("link=0" + Val + " -");
        	}else{
        	    session().click("link=" + Val + " -");
        	}
        	
                if (session().isTextPresent(selectedRubric2)) {
                    logAndFail("Expected to not see rubric [" + selectedRubric2
                		+ "] after text edition on public overview");
                }
                log("OK: Selected rubric is not visible");
        	
        	//Close Overview
        	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");*/
	}
	
//---------------------------------------------------------------------------//
//                          Add Document in Assessment Unit                  //
//---------------------------------------------------------------------------//
	//Click Assessment section 
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
	"/div[contains(text(),'valuations')]");
        
	//Edit first Assessment unit
	if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}

	//Add new document
	clickAddItem("addDocument");

	// We open Document resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	//We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select","index=0");
	
	// We choose randomly a Rubric
	String selectedRubric3 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric3 + "]");
	changeRubric(selectedRubric3);

	//We type the clickable text
	String newText12 = timeStamp();
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]", 
		newText12);

	// Open form to upload a first document
	if (inFireFox()) {
	
        	session().mouseOver("//div[@class=\"Osyl-FileBrowserTopButto" +
        			"n Osyl-FileBrowserTopButton-up\"]");
        	session().mouseOver("//div[@class=\"Osyl-FileBrowserTopButto" +
        			"n Osyl-FileBrowserTopButton-up\"]");
        	session().mouseOver("//div[@class=\"Osyl-FileBrowserTopButto" +
        			"n Osyl-FileBrowserTopButton-up\"]");
        	session().mouseOut("//div[@class=\"Osyl-FileBrowserTopButton" +
        			" Osyl-FileBrowserTopButton-up-hovering\"]");
        	session().mouseOut("//div[@class=\"Osyl-FileBrowserTopButton" +
        			" Osyl-FileBrowserTopButton-up-hovering\"]");
        	session().mouseDown("//div[@class=\"Osyl-FileBrowserTopButto" +
        			"n Osyl-FileBrowserTopButton-up-hovering\"]");
        	session().mouseUp("//div[@class=\"Osyl-FileBrowserTopButton" +
        			" Osyl-FileBrowserTopButton-down-hovering\"]");
        	
        	// Choose file and close window
        	session().type("uploadFormElement", "C:\\Documents and Setti" +
        			"ngs\\clihec3\\Local Settings\\Temporary Int" +
        			"ernet Files\\" +
			"Content.IE5\\K0F6YKYM\\fichier-excel[1].xlsx");
        	String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
		String newText8 = getRandomOption(xpathRole4);
		session().select(xpathRole4, newText8);
        	pause();
		//Close window
	    	session().click("//tr[5]/td/table/tbody/tr/td/button");
	    	pause();
        	//session().click("document.forms[0].elements[2]");
	    	
	}/*else {
	    	session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
	    	session().focus("//input[@class=\"gwt-FileUpload\"]");
	    	session().getCursorPosition("//input[@class=\"gwt-FileUpload\
	    	"]");
	    
	}*/
	
	// Open form to upload a second document
	if (inFireFox()) {
	    
	    	session().mouseOver("//td[3]/div/img");
	    	session().mouseDown("//td[3]/div/img");
	    	session().mouseUp("//td[3]/div/img");
	    	
	    	// Choose file 
	    	session().type("//input[@class=\"gwt-FileUpload\"]", "C:\\" +
	    			"Documents and Settings\\" +
			"clihec3\\Local Settings\\Temporary Internet Files\\" +
			"Content.IE5\\K0F6YKYM\\powerpoint[1].ppt");
	    	//We select randomly the rights field
		String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
		String newText8 = getRandomOption(xpathRole4);
		session().select(xpathRole4, newText8);
		pause();
		//Close window
	    	session().click("//tr[5]/td/table/tbody/tr/td/button");
	    	pause();
	    
	}/*else {
	    	session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");	    	
	    	session().focus("//input[@class=\"gwt-FileUpload\"]"); 
	    	
	    
	}*/
	
	// Select the excel file in browser window
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select","value= (F" +
			")   fichier-excel_1_.xlsx");
	session().mouseOver("//option[@value=' (F)   fichier-excel_1_.xlsx']");
	session().focus("//option[@value=' (F)   fichier-excel_1_.xlsx']");
	session().click("//option[@value=' (F)   fichier-excel_1_.xlsx']");
	pause();

	// Close Editor
	session().click("//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/" +
			"tbody/tr/td[1]/button");
	
	

	//Save modifications
	saveCourseOutline();
	pause();
	
	if (inFireFox()) {
        	//Overview
        	session().click("gwt-uid-7");
        	//Attendee Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
        			"div/table/tbody/tr/td");
        	pause();
        	//Click Assessment section 
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
		"/div[contains(text(),'valuations')]");

        	//Edit first Assessment unit
        	if(Val < 10){
        	    session().click("link=0" + Val + " -");
        	}else{
        	    session().click("link=" + Val + " -");
        	}
        
        	if (!session().isTextPresent(selectedRubric3)) {
        	    logAndFail("Expected to see rubric [" + selectedRubric3
        		+ "] after text edition");
        	}
        	log("OK: Selected rubric is visible");
        	
        	if (!session().isTextPresent(newText12)) {
        	    logAndFail("Expected to see rubric [" + newText12
        		+ "] after text edition");
        	}
        	log("OK: Text is visible");
        	
        	//Close Overview
        	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");
        	pause();
        	
        	/*/Overview
        	session().click("gwt-uid-6");

        	//Public Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]" +
        			"/div/div/table/tbody/tr[2]/td");
        	pause();
        	//Click Assessment section 
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");

        	//Edit first Assessment unit
        	if(Val < 10){
        	    session().click("link=0" + Val + " -");
        	}else{
        	    session().click("link=" + Val + " -");
        	}
        	
                if (session().isTextPresent(selectedRubric3)) {
                    logAndFail("Expected to not see rubric [" + selectedRubric3
                		+ "] after text edition on public overview");
                }
                log("OK: Selected rubric is not visible");
                	
                if (session().isTextPresent(newText12)) {
                    logAndFail("Expected to not see rubric [" + newText12
                		+ "] after text edition on public overview");
                }
                log("OK: Text is not visible");
        	
        	
        	//Close Overview
        	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");*/
	}
	
//---------------------------------------------------------------------------//
//                          Add Citation in Assessment Unit                  //
//---------------------------------------------------------------------------//
	
	//Click Assessment section 
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");
        
	//Edit first Assessment unit
	if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}

	//Add new Citation
	clickAddItem("addBiblioResource");

	// open Citation resource editor
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We choose randomly a Rubric
	String selectedRubric4 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric4 + "]");
	changeRubric(selectedRubric4);

	//Create a new citation list
        session().answerOnNextPrompt("NewListe"+ newText1);
        if (inFireFox()) {
            session().mouseOver("//td[3]/div/img");
            session().mouseDown("//td[3]/div/img");
            session().mouseUp("//td[3]/div/img");
        }else{
            session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
        }
        pause();
        assertTrue(session().isPromptPresent());
        
        //Open Citation list
        session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
        session().click("//tr[2]/td/table/tbody/tr[2]/td/select/option");
        session().select("//tr[2]/td/table/tbody/tr[2]/td/select","index=0");
        session().doubleClick("//tr[2]/td/table/tbody/tr[2]/td/select/option/");
        
        //Open form to upload a first Citation (Book)
        if (inFireFox()) {
            session().mouseOver("//td[3]/div/img");
            session().mouseDown("//td[3]/div/img");
            session().mouseUp("//td[3]/div/img");
        }else{
            session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
        }
        
        //Fill the necessary fields
        String Titre = "Titre"+ timeStamp();
        session().type("//tr[10]/td/table/tbody/tr/td[3]/input", Titre );        
        String Auteur = "Auteur"+ timeStamp();
        session().type("//tr[11]/td/table/tbody/tr/td[3]/input", Auteur);
        String Annee = "Annee"+ timeStamp();
        session().type("//tr[12]/td/table/tbody/tr/td[3]/input", Annee);
        String Editeur = "Editeur"+ timeStamp();
        session().type("//tr[13]/td/table/tbody/tr/td[3]/input", Editeur);
        String Lieu = "Lieu"+ timeStamp();
        session().type("//tr[14]/td/table/tbody/tr/td[3]/input", Lieu);
        String ISBN = "ISBN"+ timeStamp();
        session().type("//tr[20]/td/table/tbody/tr/td[3]/input", ISBN);
        
        //Close Window
        session().click("//tr[23]/td/table/tbody/tr/td/button");
        pause();
        pause();
 
        // Select first resource in browser window
        session().focus("//option[@value=' (REF)   " + Titre +"']");
        session().select("//tr[2]/td/table/tbody/tr[2]/td/select","value= " +
        		"(REF)   "+ Titre);
	session().mouseOver("//option[@value=' (REF)   " + Titre +"']");
	session().click("//option[@value=' (REF)   " + Titre +"']");

	// Close Editor
        session().click("//td/table/tbody/tr/td[1]/button");
        
       //Save modifications
	saveCourseOutline();
	pause();
	
	/*/Overview
	session().click("gwt-uid-6");
	pause();
	//Attendee Overview 
	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
        			"div/table/tbody/tr/td");
        pause();
	//Click Assessment section 
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");
        pause();
        //Open Assessment 1
	session().click("link=Evaluation 1 -");
	
	if (!session().isTextPresent(selectedRubric4)) {
	    logAndFail("Expected to see rubric [" + selectedRubric4
		+ "] after text edition");
	}
	log("OK: Selected rubric is visible");
	
	if (!session().isTextPresent(Auteur+"."+Titre+"."+Annee+"."+ISBN)) {
	    logAndFail("Expected to see text [" + Auteur+"."+Titre+"."+Annee+"."
		    +ISBN + "] after text edition");
	}
	log("OK: Text is visible");
	
	//Close Overview
	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");
	
	//Overview
	session().click("gwt-uid-6");
	pause();
	//Public Overview 
	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]" +
        			"/div/div/table/tbody/tr[2]/td");
        pause();
	//Click Assessment section 
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
        		"/div[contains(text(),'valuations')]");
        pause();
        //Open Assessment 1
	session().click("link=Evaluation 1 -");
	
	if (session().isTextPresent(selectedRubric4)) {
	    logAndFail("Expected to not see rubric [" + selectedRubric4
		+ "] after text edition on public overview");
	}
	log("OK: Selected rubric is not visible");
	
	if (session().isTextPresent(Auteur+"."+Titre+"."+Annee+"."+ISBN)) {
	    logAndFail("Expected to not see rubric [" + Auteur+"."+Titre+"."+Annee+"."
		    +ISBN + "] after text edition on public overview");
	}
	log("OK: Text is not visible");
	
	//Close Overview
	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");*/
  

//---------------------------------------------------------------------------//
//                          Switch two Assessments                           //
//---------------------------------------------------------------------------//

        //Click Assessment section 
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/td" +
	"/div[contains(text(),'valuations')]");
	pause();
        
        // We switch the 1st and 2nd assessment 
        int Val1 = resNb +2;
        if (inInternetExplorer()) {
        session().keyPress("//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
        		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
        		"tbody/tr["+Val1+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
        		"tbody/tr[2]/td/table/tbody/tr/td/div", "\r");
        		
        } else {
        session().mouseOver(
        		"//html/body/table/tbody/tr[2]/td/div/div/div[3]" +
        		"/table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table" +
        		"/tbody/tr["+Val1+"]/td/table/tbody/tr/td[2]/div");
        session().mouseOver(
        		"//html/body/table/tbody/tr[2]/td/div/div/div[3]" +
        		"/table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table" +
        		"/tbody/tr["+Val1+"]/td/table/tbody/tr/td[2]/div/table[3]");
        session().mouseOver(
        		"//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
        		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
        		"tbody/tr["+Val1+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
        		"tbody/tr[2]/td/table/tbody/tr/td/div");
        session().mouseDown(
        		"//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
        		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
        		"tbody/tr["+Val1+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
        		"tbody/tr[2]/td/table/tbody/tr/td/div");
        session().mouseUp(
        		"//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
        		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
        		"tbody/tr["+Val1+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
        		"tbody/tr[2]/td/table/tbody/tr/td/div");
        }
        
//---------------------------------------------------------------------------//
//                Delete Assessment Unit		                     //
//---------------------------------------------------------------------------//

        //We delete Assessment 1
        int Val2 = Val+1;
        session().click("//tr["+Val2+"]/td/table/tbody/tr/td[2]/div/table[2]" +
        		"/tbody/tr/td[2]/button");
        
        session().click("//tr[2]/td/table/tbody/tr/td/button");


	
	//Save modifications
	saveCourseOutline();
	pause();
	
	//Log out
	session().selectFrame("relative=parent");
	logOut();
	log("AssessmentTest: test complete");	
    }

	private int getResourceCount() {
	return session().getXpathCount(
		"//div[@class=\"Osyl-UnitView-ResPanel\"]").intValue();

	}
}
