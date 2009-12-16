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
 * Tests all the features in the section Lectures. The exact steps are: 
 * log in as admin, creates a new site if needed, load it (OpenSyllabus is 
 * the first page), enters in the Lectures section, add two lectures,
 * name the last one, edit the last lecture, rename it, add text, add 
 * hyperlink, add document , add citation, switch the tow added lectures, 
 * delete the last one, click save, log out.
 * 
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:bouchra.laabissi@hec.ca">Bouchra Laabissi</a>
 */

public class SeancesTest extends AbstractOSYLTest{
    
    @Test(groups = "OSYL-Suite", description =
	"OSYLEditor test. Add a contact resource, edit it and save the changes")
	@Parameters( { "webSite" })
	public void TestAddSeance(String webSite) throws Exception {
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
//	                      Add Lecture        		             //
//---------------------------------------------------------------------------//

	//Open Lectures Section
        session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
        		"td/div[contains(text(),'ances')]");
        pause();
        
        // We keep track of how many resources are showing
        int resNb = getResourceCount()-1;
        log("We start with " + resNb + " resources");
        
        //We add a first Assessment Unit
        clickAddItem("addPedagogicalUnit");
        pause();
        
        //We add a first Assessment Unit
        clickAddItem("addPedagogicalUnit");
        pause();
        
//---------------------------------------------------------------------------//
//     			       Modify Lecture name			     //
//---------------------------------------------------------------------------//
        
        int Position = resNb +3;
    	//We edit the last Lecture
	session().click("//tr["+Position+"]/td/table/tbody/tr/td[2]/div/" +
			"table[2]/tbody/tr/td/button");
	pause();
	
	//Rename the last Lecture unit
	String newText1 = "FirstSeanceName" + timeStamp();
	session().type("//table/tbody/tr/td/table/tbody/tr/td/input", newText1);
	pause();
	
	//Click OK to close Editor
	session().click("//tr/td/table/tbody/tr/td/table/tbody/tr/td/button");
	pause();
	
	// Ensure the new name is visible
	if(!session().isTextPresent(newText1)) {
	    logAndFail("New lecture title not present");	    
	}
	log("OK: Lecture renamed");
	
	// Now we rename the lecture from inside
	int Val = resNb +2;
	if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
	pause();
	String newText2 = "SeanceReNamed" + timeStamp();
	session().click("//tr/td/div/table[2]/tbody/tr/td/button");
	session().type("//input[@type='text']", newText2);
	// Click OK
	session().click("//td/table/tbody/tr/td[1]/button");
	// Ensure the new name is visible
	if(!session().isTextPresent(newText2)) {
		logAndFail("New lecture title not present (inside the lecture)");
	}
	log("OK: Lecture renamed from inside");
	pause();
    
//---------------------------------------------------------------------------//
//      		Add Text in the Lecture Unit			     //
//---------------------------------------------------------------------------//
    
	//Open Lectures Section
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
	"td/div[contains(text(),'ances')]");
        pause();
	
	//We edit the last Lecture
        if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
	pause();
	
	//Add Text in the last Lecture Unit
	clickAddItem("addText");

	//We edit the new text Lecture
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	
	//We select randomly the rubric name  
	String selectedRubric1 = getRandomRubric();
	log("Selecting rubric [" + selectedRubric1 + "]");
	changeRubric(selectedRubric1);
	
	//We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select","index=0");
	
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
        	session().click("gwt-uid-6");
        	//Attendee Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
        			"div/table/tbody/tr/td");
        	pause();
                
        	//Open Lectures Section
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
			"td/div[contains(text(),'ances')]");
                pause();
        	//Open the last Lecture unit
        	session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
        	pause();
                        	
        	if (!session().isTextPresent(selectedRubric1)) {
        	    logAndFail("Expected to see rubric [" + selectedRubric1
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
        	
        	//Open Lectures Section
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
        		"td/div[contains(text(),'ances')]");
                pause();
        	//Open the last Lecture unit
        	session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
        	pause();
        	        	
                if (session().isTextPresent(selectedRubric1)) {
                    logAndFail("Expected to see rubric [" + selectedRubric1
                		+ "] after text edition on public overview");
                }
                log("OK: Selected rubric is visible");
        	        	
        	//Close Overview
                session().click("//html/body/table/tbody/tr/td/table/tbody" +
		"/tr[2]/td[2]/div/div/table/tbody/tr/td");
                pause();*/
	}
	
//---------------------------------------------------------------------------//
//      			Add Hyperlink in Lecture Unit	             //
//---------------------------------------------------------------------------//
	
	//Open Lectures Section
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
		"td/div[contains(text(),'ances')]");
        pause();
	//Open the last Lecture unit 
        if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
	pause();
       
	//Add Hyperlink in the last Lecture Unit
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
	session().type("//input[@type='text']", newText11);

	//We type the URL link
	String newText10 = "http://webmail.hec.ca/";
	session().type("//tr[2]/td[2]/input", newText10);

	//We click OK to close editor
	session().click("//td/table/tbody/tr/td[1]/button");

	//We click URL to test 
	session().click("link=" + newText11);

	//Save modifications
	saveCourseOutline();
	pause();
	
	if (inFireFox()) {
        	//Overview
        	session().click("gwt-uid-6");
        	//Attendee Overview 
        	session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
        			"div/table/tbody/tr/td");
        	pause();

        	//Open Lectures Section
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
			"td/div[contains(text(),'ances')]");
                pause();
        	//Open the last Lecture unit 
        	session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
        	pause();
        
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
        	
        	//Open Lectures Section
        	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
        		"td/div[contains(text(),'ances')]");
                pause();
        	//Open the last Lecture unit 
        	session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
        	pause();
        	
                if (session().isTextPresent(selectedRubric2)) {
                    logAndFail("Expected to not see rubric [" + selectedRubric2
                		+ "] after text edition on public overview");
                }
                log("OK: Selected rubric is not visible");
        	
        	//Close Overview
        	session().click("//html/body/table/tbody/tr/td/table/tbody" +
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");
        	pause();*/
	}
	
//---------------------------------------------------------------------------//
//      			Add Document in Lecture Unit                  //
//---------------------------------------------------------------------------//
	//Open Lectures Section
	session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
		"td/div[contains(text(),'ances')]");
        pause();
	//Open the last Lecture unit 
        if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
	pause();
        
        //Add new document
        clickAddItem("addDocument");
        
        // We open Document resource editor
        session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
        
        // We choose randomly a Rubric
        String selectedRubric3 = getRandomRubric();
        log("Selecting rubric [" + selectedRubric3 + "]");
        changeRubric(selectedRubric3);
        
        //We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select","index=0");
        
        
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
        	"Content.IE5\\K0F6YKYM\\osyl-src[1].zip");
        //We select randomly the rights field
	String xpathRole4 = "//div[2]/form/table/tbody/tr[5]/td/select";
	String newText8 = getRandomOption(xpathRole4);
	session().select(xpathRole4, newText8);
	pause();
	//Close window
    	session().click("//tr[6]/td/table/tbody/tr/td/button");
    	pause();
        
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
        
        // Choose file and close window
        session().type("//input[@class=\"gwt-FileUpload\"]", "C:\\" +
        		"Documents and Settings\\" +
        	"clihec3\\Local Settings\\Temporary Internet Files\\" +
        	"Content.IE5\\K0F6YKYM\\powerpoint[1].ppt");
      //We select randomly the rights field
	String xpathRole4 = "//div[2]/form/table/tbody/tr[5]/td/select";
	String newText8 = getRandomOption(xpathRole4);
	session().select(xpathRole4, newText8);
	pause();
	//Close window
    	session().click("//tr[6]/td/table/tbody/tr/td/button");
    	pause();
        
        }/*else {
        session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");	    	
        session().focus("//input[@class=\"gwt-FileUpload\"]"); 
        
        
        }*/
        
        pause();
        
        // Select file in browser window
        session().select("//tr[2]/td/table/tbody/tr[2]/td/select","value= (F" +
        	")   osyl-src_1_.zip");
        session().mouseOver("//option[@value=' (F)   osyl-src_1_.zip']");
        session().focus("//option[@value=' (F)   osyl-src_1_.zip']");
        session().click("//option[@value=' (F)   osyl-src_1_.zip']");
        pause();
        
        // Close Editor
        session().click("//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/" +
        	"tbody/tr/td[1]/button");
        
        //Save modifications
        saveCourseOutline();
        pause();
        
        if (inFireFox()) {
                        
            //Overview
            session().click("gwt-uid-6");
            
            //Attendee Overview 
            session().click("//html/body/div/div/table/tbody/tr[2]/td[2]/div/" +
        			"div/table/tbody/tr/td");
            pause();
            
            //Open Lectures Section
            session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
		"td/div[contains(text(),'ances')]");
            pause();
            //Open the last Lecture unit 
            session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
    	    pause();
            
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
            
            //Open Lectures Section
            session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
        		"td/div[contains(text(),'ances')]");
            pause();
            //Open the last Lecture unit 
            session().click("//tr["+Position +"]/td/table/tbody/tr/td/div");
    	    pause();
            
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
        			"/tr[2]/td[2]/div/div/table/tbody/tr/td");
            pause();*/
        }

//---------------------------------------------------------------------------//
//      			Add Citation in Lecture Unit                  //
//---------------------------------------------------------------------------//

        //Open Lectures Section
        session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
	"td/div[contains(text(),'ances')]");
        pause();
        
        //Click last Lecture  
        if(Val < 10){
	    session().click("link=0" + Val + " -");
	}else{
	    session().click("link=" + Val + " -");
	}
        pause();
        
        //Add new Citation
        clickAddItem("addBiblioResource");
        
        // open Citation resource editor
        session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
        
        //We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select","index=0");
        
        // We choose randomly a Rubric
        String selectedRubric4 = getRandomRubric();
        log("Selecting rubric [" + selectedRubric4 + "]");
        changeRubric(selectedRubric4);
        
        /*/Create a new citation list
        session().answerOnNextPrompt("NewListe"+ newText1);
        if (inFireFox()) {
        session().mouseOver("//td[3]/div/img");
        session().mouseDown("//td[3]/div/img");
        session().mouseUp("//td[3]/div/img");
        }else{
        session().keyPress("//td[3]/table/tbody/tr/td[3]/div","\r");
        }
        pause();
        assertTrue(session().isPromptPresent());*/
        
        //Open Citation list
        session().focus("//tr[2]/td/table/tbody/tr[2]/td/select/option");
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
        
        //Fill the mandatory fields
        session().select("//select[@name='cipvalues']", "label=Article");
        String Titre = "Titre"+ timeStamp();
        session().type("//tr[10]/td/table/tbody/tr/td[3]/input", Titre );
        String Auteur = "Auteur"+ timeStamp();
        session().type("//tr[11]/td/table/tbody/tr/td[3]/input", Auteur);
        String Periodique = "Periodique"+ timeStamp();
        session().type("//tr[13]/td/table/tbody/tr/td[3]/input", Periodique);
        String Date = "Date"+ timeStamp();
        session().type("//tr[15]/td/table/tbody/tr/td[3]/input", Date);
        String Volume = "Volume"+ timeStamp();
        session().type("//tr[16]/td/table/tbody/tr/td[3]/input", Volume);
        String Numero = "Numero"+ timeStamp();
        session().type("//tr[16]/td/table/tbody/tr/td[6]/input", Numero);
        String Pages = "Pages"+ timeStamp();
        session().type("//tr[18]/td/table/tbody/tr/td[3]/input", Pages);
        String ISSN = "ISSN"+ timeStamp();
        session().type("//tr[19]/td/table/tbody/tr/td[3]/input", ISSN);
        String DOI = "DOI"+ timeStamp();
        session().type("//tr[20]/td/table/tbody/tr/td[3]/input", DOI);
        
        //Close Window
        session().click("//tr[22]/td/table/tbody/tr/td/button");
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

        
//---------------------------------------------------------------------------//
//      			Switch two Lectures                            //
//---------------------------------------------------------------------------//
        
        //Open Lectures Section
        session().mouseDown("//div[@class=\"gwt-TreeItem\"]/table/tbody/tr/" +
		"td/div[contains(text(),'ances')]");
        pause();
        
        // We switch the 1st and 2nd Lecture 
        if (inInternetExplorer()) {
            session().keyPress("//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
            		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
            		"tbody/tr["+Val+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
            		"tbody/tr[2]/td/table/tbody/tr/td/div", "\r");
            		
            } else {
            session().mouseOver(
            		"//html/body/table/tbody/tr[2]/td/div/div/div[3]" +
            		"/table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table" +
            		"/tbody/tr["+Val+"]/td/table/tbody/tr/td[2]/div");
            session().mouseOver(
        	    	"//html/body/table/tbody/tr[2]/td/div/div/div[3]" +
            		"/table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table" +
            		"/tbody/tr["+Val+"]/td/table/tbody/tr/td[2]/div/table[3]");
            session().mouseOver(
        	    	"//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
            		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
            		"tbody/tr["+Val+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
            		"tbody/tr[2]/td/table/tbody/tr/td/div");
            session().mouseDown(
        	    	"//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
            		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
            		"tbody/tr["+Val+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
            		"tbody/tr[2]/td/table/tbody/tr/td/div");
            session().mouseUp(
        	    	"//html/body/table/tbody/tr[2]/td/div/div/div[3]/" +
            		"table/tbody/tr[2]/td[2]/div/table/tbody/tr/td/table/" +
            		"tbody/tr["+Val+"]//td/table/tbody/tr/td[2]/div/table[3]/" +
            		"tbody/tr[2]/td/table/tbody/tr/td/div");
            }

//---------------------------------------------------------------------------//
//			Delete Lecture Unit		                     //
//---------------------------------------------------------------------------//

       
        //We delete Lecture 1
        int Val1 = Val+1;
        session().click("//tr["+Val1+"]/td/table/tbody/tr/td[2]/div/table[2]" +
        	"/tbody/tr/td[2]/button");
        
        session().click("//tr[2]/td/table/tbody/tr/td/button");
        
        
        
        //Save modifications
        saveCourseOutline();
        pause();
        
        //Log out
        session().selectFrame("relative=parent");
        logOut();
        log("testAddContactInfo: test complete");	
        	
	
    }
       	private int getResourceCount() {
	return session().getXpathCount(
		"//div[@class=\"Osyl-UnitView-ResPanel\"]").intValue();

	}
    
}


