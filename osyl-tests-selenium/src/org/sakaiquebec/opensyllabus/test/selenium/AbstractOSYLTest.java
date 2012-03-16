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

import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.sakaiquebec.opensyllabus.test.selenium.MsgLog;
import org.sakaiquebec.opensyllabus.test.selenium.utils.AddFileResourcePopup;
import org.sakaiquebec.opensyllabus.test.selenium.utils.ResourceXpathHelper;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.thoughtworks.selenium.SeleneseTestBase;

/**
 * AbstractOSYLTest contains several methods that can be used to implement
 * various Selenium tests. Some methods have to be called in a specific order.
 * For instance createTestSite() won't work unless logInAsAdmin(webSite) has
 * been invoked before.
 *
 * @author <a href="mailto:remi.saias@hec.ca">Remi Saias</a>
 * @author <a href="mailto:alejandro.hernandez-pena@hec.ca">Alejandro Hernandez</a>
 */
public class AbstractOSYLTest extends SeleneseTestBase  {

    // Current test site
    private String siteName;
    // Base name for test sites
    public static final String TEST_SITE_BASE_NAME = "Test_Se_";

    //Button's click event names in Osyl
    public static final String BUTTON_HOME = "//td[@id='gwt-uid-2']";  //gwt-uid-2
    public static final String BUTTON_ALL_VIEW = "//td[@id='gwt-uid-8']"; //gwt-uid-8
    public static final String BUTTON_ADD = "//td[@id='gwt-uid-10']"; //gwt-uid-10
    public static final String BUTTON_SAVE = "//td[@id='gwt-uid-3']"; //gwt-uid-3
    public static final String BUTTON_PUBLISH = "//td[@id='gwt-uid-4']"; //gwt-uid-4
    public static final String BUTTON_PRINT = "//td[@id='gwt-uid-17']"; //gwt-uid-17
    public static final String BUTTON_UPDATE = "//td[@id='gwt-uid-8']"; //gwt-uid-8
    public static final String BUTTON_PREVIEW = "//td[@id='gwt-uid-11']"; //gwt-uid-11

    // The screenshot capture is always done on the windows machine (running
    // the test) and not on the grid. This explains the following!
    private static final String SCREENSHOT_DIR = "C:/opt/selenium/screenshots/";

    // Current browser
    private String browserString;

    // Current user
    private String userString;
    private String passwordString;
    
    // Formatter for timeStamps
    private static final SimpleDateFormat TIMESTAMP_FORMATOR = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");


    protected String fileServer;
    
    // Dissemination level for OSYL
    protected static final String LEVEL_ATTENDEE = "attendee";
    protected static final String LEVEL_COMMUNITY = "community";
    protected static final String LEVEL_PUBLIC = "public";

    //Result of scenario
    protected static final String PASSED = "PASS�";
    protected static final String FAILED = "�CHEC";

    //Environments
    protected static final String IC_ORACLE = "icoracle";
    protected static final String IC_MYSQL = "icmysql";
    protected static final String ZC2A = "zc2a";
    
    //Scripts java selenium
    protected static final String OSYL_TEST = "AbstractOSYLTest";
    protected static final String ASSESSMENT_TEST = "AssessmentTest";
    protected static final String ASSOCIATION_SITES_TEST = "AssociateToParentSite";
    protected static final String STAFF_INFO_TEST = "ContactInfoTest";
    protected static final String LECTURE_TEST = "LectureTest";
    protected static final String NEWS_TEST = "NewsTest";
    protected static final String OVERVIEW_TEST = "PresentationOfCourse";
    protected static final String PEDAGOGICAL_TEST = "SeancesTest";
    protected static final String TEACHING_MATERIAL_TEST = "TeachingMaterial";
    protected static final String TEXT_TEST = "TextTest";

	//Uses Cases for testing
    protected static final String CT_001 = "001-Associer plan de cours enfant � un parent";
    protected static final String CT_002 = "002-Cr�er un plan de cours non-associer � un cours";
	protected static final String CT_003 = "003-Cr�er un plan de cours - Plan de cours existant";
	protected static final String CT_004 = "004-Dissocier plan de cours parent d'un plan enfant";
	protected static final String CT_005 = "005-Ajouter Outil de remise �lectronique";
	protected static final String CT_006 = "006-Ajouter texte � la section pr�sentation";
    protected static final String CT_007 = "007-Ajouter une coordonn�e";
    protected static final String CT_008 = "008-Ajouter une s�ance de cours";
    protected static final String CT_009 = "009-Ajouter une �valuation";
    protected static final String CT_010 = "010-Supprimer texte de la section pr�sentation";
    protected static final String CT_013 = "013-Modifier un document dans une s�ance de cours";
    protected static final String CT_015 = "015-Modifier Hyperlien";
    protected static final String CT_016 = "016-Supprimer une s�ance de cours";
    protected static final String CT_018 = "018-Modifier une coordonn�es";
    protected static final String CT_020 = "020-Supprimer Document(dans une s�ance)";
    protected static final String CT_024 = "024-Supprimer une coordonn�e";
    protected static final String CT_028 = "028-Ajouter Hyperlien";
    protected static final String CT_027 = "027-Ajouter Document (dans une S�ance)";
	protected static final String CT_030 = "030-Modifier une �valuation";
	protected static final String CT_045 = "045-Ajouter une citation de type livre d'une liste � un content unit";
	protected static final String CT_050 = "050-Ajouter une �valuation avec des valeurs invalides � la cr�ation";
	protected static final String CT_054 = "054-Supprimer une liste de citations";
    protected static final String CT_061 = "061-G�n�rer aper�u �tudiants inscrits";
    protected static final String CT_067 = "067-Ajouter une nouvelle (Section nouvelle)";
	protected static final String CT_068 = "068-Ajouter une nouvelle (S�ance)";
    protected static final String CT_069 = "069-Consulter un plan de cours vue �tudiant";
	protected static final String CT_070 = "070-Supprimer un devoir �valu�";
	protected static final String CT_076 = "076-Ajouter un regroupement au premier niveau (cas limite)";
	protected static final String CT_084 = "084-Modifier un regroupement de premier niveau";
	protected static final String CT_096 = "096-Ajouter texte, document, r�f. biblio., hyperlien et nouvelle en diffusion \"�tudiants inscrits\" dans un plan de cours coordonn�";
    protected static final String CT_087 = "087-Supprimer un regroupement de premier niveau";
    protected static final String CT_104 = "104-Ajouter une citation de type article d'une liste � un content unit";
    protected static final String CT_105 = "105-Ajouter l'outil de remise �lectronique";
    protected static final String CT_106 = "106-Modifier le lien vers l'outil de remise �lectronique";
    protected static final String CT_119 = "119-Publier un plan de cours - sc�nario principal";
    protected static final String CT_0091 = "CT-???-Ajouter un texte dans �valuation";
    protected static final String CT_0092 = "CT-???-Ajouter un document dans �valuation";
    protected static final String CT_0093 = "CT-???-Ajouter une r�f. biblio. dans �valuation";
    protected static final String CT_0101 = "CT-???-Ajouter un texte dans mat�riel p�dagogique";
    protected static final String CT_0102 = "CT-???-Ajouter un document dans mat�riel p�dagogique";
    protected static final String CT_0103 = "CT-???-Ajouter une r�f. biblio. dans mat�riel p�dagogique";
    protected static final String CT_0111 = "CT-???-Ajouter un texte dans s�ances";
    protected static final String CT_0112 = "CT-???-Ajouter un document dans s�ances";
    protected static final String CT_0113 = "CT-???-Ajouter une r�f. biblio. dans s�ances";
    protected static final String PT_19_2 = "PT 19.2 Cr�ation du devoir �valu� � partir de l'Outil de remise";

    //File and directory for upload test
    //protected static final String FILE_DIR = "C:\\Documents and Settings\\utilisateur\\Documents\\";
    protected static String FILE_DIR;// = "D:\\OSYL-TEST-FICHIERS\\";// will be renamed 'resourcesFolder'
    protected static final String DESKTOP_DIR = "d:\\utilisateur\\Bureau\\";
    protected static final String PPT_FILE = "powerpoint_file[1].ppt";
    protected static final String XLS_FILE = "excel_file[1].xls";
    protected static final String XLS5_FILE = "excel_file[5].xls";
    protected static final String ZIP_FILE = "osyl-src[1].zip";

    // Maximum time we wait (in ms) for an element to appear as expected
    private static final int MAX_TIME = 30000;
    
    @BeforeClass(alwaysRun = true)
    @Parameters( { "seleniumHost", "seleniumPort", "browser", "webSite", "fileFolder" })
    public void setUp(String seleniumHost, int seleniumPort, String browser, String webSite, String fileFolder) {
	
	System.out.println("seleniumHost = "  + seleniumHost);
	System.out.println("fileFolder = "  + fileFolder);

	startSeleniumSession(seleniumHost, seleniumPort, browser, webSite);

	browserString = browser;
	FILE_DIR = fileFolder;// repertoire qui contient nos fichiers de test.
	
	log("Starting test with browser: " + browser);
	initCurrentTestSiteName(); // siteName = "Se-2012-02-21"

    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
	closeSeleniumSession();
	super.tearDown();
    }

    /**
     * Shortcut for session.waitForPageToLoad("30000").
     */
    public void waitForPageToLoad() {
	session().waitForPageToLoad("30000");
    }

    /**
     * Cree le nom du site de test. Exemple 'Test_Se_2012_02_21'.
     */
    private void initCurrentTestSiteName() {
	
	//String randomSuffix = RandomStringUtils.randomAlphanumeric(10);
	siteName =
		TEST_SITE_BASE_NAME
			+ (new SimpleDateFormat("yyyy_MM_dd"))
				.format(new Date()) ;
				//"_" + randomSuffix;
	prettyLog(">>> Test site name is " + siteName);
    }

    /**
     * Returns a time stamp that can be used to mark some test (insert in texts
     * for instance).
     * Exemple "2012-02-22 10-34-19".
     */
    public String timeStamp() {
	return TIMESTAMP_FORMATOR.format(new Date());
    }
    
    /**
     * Print elapsed time in hours/minutes/seconds, like "2:30:54"
     * From http://www.coderanch.com/t/378404/java/java/Convert-milliseconds-time.
     * @return String
     */
    public String getElapsedTimeHoursMinutesSecondsString(long elapsedTime) {     
        String format = String.format("%%0%dd", 2);
        elapsedTime = elapsedTime / 1000;
        String seconds = String.format(format, elapsedTime % 60);
        String minutes = String.format(format, (elapsedTime % 3600) / 60);
        String hours = String.format(format, elapsedTime / 3600);
        String time =  (!hours.equals("00") ? hours + "hr " : "") + minutes + "min " + seconds + "secs";
        return time;
    }


    /**
     * Log in Sakai using the admin account.
     *
     * @param String url of the Sakai instance to log in
     */
    public void logInAsAdmin(String url) throws Exception {
	
	userString = "prof_selenium";
	passwordString = "123456";

	prettyLog("Log in as " + userString + " at "+ url);
	
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
	
	waitForElement("//input[@id='eid']", 5000);	
	
	if (!session().isElementPresent("eid")
		&& session().isElementPresent("loginLink1")) {
	    // We seem to be logged in already. This happens when RC fails to
	    // close MSIE after a test.
	    log("logInAsAdmin: We are already logged in!");
	    return;
	}
	session().type("eid", userString);  // admin
	session().type("pw", passwordString); // osyl123
	session().click("submit");


	session().waitForPageToLoad("300000");
	assertFalse("Login failed", session().isElementPresent(
		"//div[@class=\"alertMessage\"]"));
    }
    
    /**
     * Log in Sakai using the username passed.
     *
     * @param String url of the Sakai instance to log in
     */
    public void logIn(String url, String username, String password) throws Exception {
	
	prettyLog("Log in as " + username + " at "+ url);
	
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

	waitForElement("//input[@id='eid']", 5000);	
	
	if (!session().isElementPresent("eid")
		&& session().isElementPresent("loginLink1")) {
	    // We seem to be logged in already. This happens when RC fails to
	    // close MSIE after a test.
	    log("logInAsAdmin: We are already logged in!");
	    return;
	}
	session().type("eid", username);  // admin
	session().type("pw", password); // osyl123
	session().click("submit");


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
	createSite(getCurrentTestSiteName());
    } // createTestSite

    /**
     * Creer site avec - nom du site ('Se-2012-02-21')
     *                 - langue (Francais canadien)
     *                 - default configuration.
     * @param parentChild
     * @throws Exception
     */
    public void createTestSite(String parentChild) throws Exception {
    	createSite(parentChild);
    	goToSite(parentChild);
        } // createTestSite

	public void createSite(String siteName) throws Exception {
		// Create an outline course from OsylManager
		log("**** Creating site " + siteName);
		//Search Osyl Manager location
		goToOsylManagerTool();
		
		//Open create site
		pause();
		if (session().isElementPresent("//div[contains(text(),'er un nouveau site')]")) { // @LITERAL-TEXT		
			String createSite = "//tr[7]/td/table/tbody/tr/td[1]/div/div";
			clickWithMouse(createSite);
		}
		pause(10000);
		// Course Name
		ensureElementPresent("//tr[2]/td/table/tbody/tr/td[2]/input");
		session().type("//tr[2]/td/table/tbody/tr/td[2]/input", siteName);
		
		// Course Language "Francais (canadien)"
		session().select("//tr[3]/td/table/tbody/tr/td[2]/select", "index=0");
		
		// Default configuration course
		session().select("//tr[4]/td/table/tbody/tr/td[2]/select",
				"value=default");
		
		// Click on button "Create Course"
		// Xpath: S�lectionne �l�ment 'input' dont le sibling est un div contenant le texte 'Cr�er'
		//smartMouse("//div[text()='Cr�er']/preceding-sibling::input");
		smartMouse("//tr[@class='popupMiddle']//div[@class='html-face']");// le bouton 'Creer'
		
		//smartMouse("//tr[5]/td/table/tbody/tr/td[2]/select");
		pause(10000);
		
		// Click button "Close" (confirmation)
		if (userString.equalsIgnoreCase("prof_selenium")) {
			ensureElementPresent("//tr[4]/td/div");			
			smartMouse("//tr[4]/td/div");					
		} else if (userString.equalsIgnoreCase("admin")) {
			ensureElementPresent("//tr[4]/td/div");			
			smartMouse("//tr[4]/td/div");		
		}		
		log("**** Site created " + siteName + "*******");
	} // createSite

    /**
     * Opens windows to create a new site (if in FF) or selects it and press Enter (in IE).
     * @param element
     */
    protected void clickWithMouse(String element) {
		if (inFireFox()) {
		    session().mouseOver(element);
		    session().mouseDown(element);
		    session().mouseUp(element);
		    session().click(element);
		} else {
		    session().mouseOver(element);
			session().mouseDownAt(element, "10,10");
			session().mouseUpAt(element, "10,10");
		}
    }

    /**
     * Clicks the element (if in FF) or selects it and press Enter (in IE).
     * @param element
     */
//    private void smartClick(String element) {
//		if (inFireFox()) {
//			session().click(element);
//		} else {
//			session().click(element);
//		}
//    }
//
    /**
     * Clicks the element (if in FF) or selects it and press Enter (in IE).
     * @param element
     */
    private void smartMouse(String element) {
		if (inFireFox()) {
		    session().mouseOver(element);
		    session().mouseDown(element);
		    session().mouseUp(element);
		} else {
		    session().mouseOver(element);
			session().mouseDownAt(element, "10,10");
			session().mouseUpAt(element, "10,10");
		}
    }

    /**
     * Aller a "Gestionnaire de sites de cours"
     */
    public void goToOsylManagerTool() {
	// Go to Workspace
	session().click("css=span");
	
	// Go to Osyl Manager
	String osylManagerChoice = "//*[@class='icon-sakai-opensyllabus-manager-tool']"; // "//div[@id='toolMenu']/ul/li[5]/a/span";
	ensureElementPresent(osylManagerChoice);
	// if
	// (session().isElementPresent("//*[@class='icon-sakai-opensyllabus-manager-tool']"))
	// {
	clickWithMouse(osylManagerChoice);
	// }
    }

    public String getCurrentTestSiteName() {
    	return siteName;
    }

    public void goToSite(String siteName) throws Exception {
	session().open("/portal/site/" + siteName);
	waitForPageToLoad();

	if (!session().isTextPresent(siteName) || session().isTextPresent("Site Unavailable")) {
	    try {
		createTestSite(siteName);
	    } catch (Exception e) {
		logAndFail("goToSite: " + e);
		// Add message to log file
		logFile(OSYL_TEST, CT_069, FAILED);
	    }
	} else {
	    goToMenuOsyl();
	}

    }

    /**
     * Loads the test site. Assumes we are already logged-in. A failure occurs
     * if the site does not exist.
     */
    public void goToCurrentSite() throws Exception {
    	goToSite(getCurrentTestSiteName());
    }

    /**
     * Deletes the test site. Will fail if the operation is unsuccessful.
     */
    public void deleteTestSite() throws Exception {
	deleteTestSite(true);
    }

    /**
     * Go to "Plan de Cours"
     * @throws IllegalStateException
     */
    public void goToMenuOsyl() throws Exception {
	String elementOsylMenu = "//*[@class='icon-sakai-opensyllabus-tool']";

	if (session().isElementPresent(elementOsylMenu)) {
		// open Osyl tool
		clickWithMouse(elementOsylMenu);
	}
	
	//pause3();
	waitForElement("//div[@class='Osyl-MainView-HorizontalSplitPanel']", 10000);
	pause(200);// just for safety....

    }

    public void goToMenuAssessment() throws IllegalStateException {
	String elementAssessmentMenu = "//*[@class='icon-sakai-assignment-grades']";
	if (session().isElementPresent(elementAssessmentMenu)) {
		// open Assessment tool
		if (inFireFox()) {
			session().mouseOver(elementAssessmentMenu);
			session().mouseDown(elementAssessmentMenu);
			session().mouseUp(elementAssessmentMenu);
			session().click(elementAssessmentMenu);
			pause();
		} else {
			session().click(elementAssessmentMenu);
		}
	}
	pause3();
    }

    public void goToMenuSiteSetup() throws IllegalStateException {
	String elementSiteSetupMenu = "//*[@class='icon-sakai-sitesetup']";
	// open site setup
	pause3();
	if (inFireFox()) {
		session().mouseDown(elementSiteSetupMenu);
		session().mouseUp(elementSiteSetupMenu);
		session().click(elementSiteSetupMenu);		
		pause();
	} else {
		session().click(elementSiteSetupMenu);
	}
	pause3();
    }

    /**
     * Deletes the test site. Will fail if the operation is unsuccessful and if
     * the boolean parameter is true.
     *
     * @param boolean fail
     */
    public void deleteTestSite(boolean fail) throws Exception {
	try {

	    // Why do we go to the Admin page ?????????
	    // session().open("/portal/site/%7Eadmin");
	    //  waitForPageToLoad();
	    
	    // Warning: the next command won't work if the current active tool
	    // is already "Sites".
	    //session().click("//div[@id='toolMenu']/ul/li[5]/a/span");// "Clicker sur 'Gestionnaire de site de cours'
	   
	    //session().click("//span[text() = 'Gestionnaire de sites de cours']/parent::a");
	    session().click("//a[@class='icon-sakai-opensyllabus-manager-tool']");
	    // osylManagerChoice = "//*[@class='icon-sakai-opensyllabus-manager-tool']"; // "//div[@id='toolMenu']/ul/li[5]/a/span";

	    waitForPageToLoad();

	    // Chercher le site
	    String searchTextBox = "//table[@class='OsylManager-panel']//td/input[@class='gwt-TextBox']";
	    waitForElement(searchTextBox, 6000);
	    session().type(searchTextBox, getCurrentTestSiteName());//
	    
	    clickWithMouse("//div[text()='Chercher']/parent::div");
	    pause(4000);
	    
	    if (session().isAlertPresent()) {
		log("Alert: " + session().getAlert());
		return;
	    }
	    
	    /**
	     * Verify if site to delete exists
	     */
	    String siteCheckboxLocator = "//td[text() = '" + getCurrentTestSiteName() + "']/preceding-sibling::td/input";
	    boolean siteFound;
	    
	    try {
		waitForElement(siteCheckboxLocator, 5000);
		siteFound = true;
	    } catch (Exception e) {
		siteFound = false;
	    }

	    if (siteFound) {
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
	    

	    // Click du checkbox du site (s'il est trouv�!)
	    //session().click("link=ID du site");
	    session().click(siteCheckboxLocator); 
	    
	    
	    clickWithMouse("//div[text() = 'Supprimer']/parent::div");
	    pause(1000);

	    //session().click("eventSubmit_doRemove_confirmed");
	    session().click("//button[text()='Ok']"); // click du bouton "ok"
	    pause(3000);
    	    
	    // removed by Thanh!
	    //assertFalse("Site not deleted as expected!", session()
	    //    .isTextPresent(getCurrentTestSiteName()));
    	    
	} catch (Exception e) {
	    // do nothing. Just log error found.
	    log("Exception dans deleteTestSite (cas normal): " + e.getMessage());
	  
	} finally {
	    // Return to Home "tool"
	    session().selectFrame("relative=parent");
	    //session().click("//div[@id='toolMenu']/ul/li[1]/a/span");
	    session().click("//span[text() = 'Accueil']/parent::a");
	    pause(3000);
	}
    } // deleteTestSite

    /**
     * Deletes the test site. Will fail if the operation is unsuccessful and if
     * the boolean parameter is true.
     *
     * @param siteToDelete 
     * @param boolean fail
     */
    public void deleteTestSite(String siteToDelete, boolean fail) throws Exception {
	try {

	    prettyLog("Deleting site " + siteToDelete);
	    
	    // Why do we go to the Admin page ?????????
	   // session().open("/portal/site/%7Eadmin");
	   //  waitForPageToLoad();
	    
	    // Warning: the next command won't work if the current active tool
	    // is already "Sites".
	    //session().click("//div[@id='toolMenu']/ul/li[5]/a/span");// "Clicker sur 'Gestionnaire de site de cours'
	    session().click("//span[text() = 'Gestionnaire de sites de cours']/parent::a");
	    //waitForPageToLoad();

	    // Chercher le site
	    String searchTextBox = "//table[@class='OsylManager-panel']//td/input[@class='gwt-TextBox']";
	    waitForElement(searchTextBox, 10000);
	    session().type(searchTextBox, siteToDelete);//
	    clickWithMouse("//div[text()='Chercher']/parent::div");
	    pause(4000);
	    
	    if (session().isAlertPresent()) {
		log("Alert: " + session().getAlert());
		return;
	    }
	    
	    if (session().isTextPresent(siteToDelete)) {
		log("Found site '" + siteToDelete + "' to delete");
	    } else {
		if (fail) {
		    fail("Cannot delete site '" + siteToDelete
			    + "' because it could not be found!");
		} else {
		    log("Did not delete site '" + siteToDelete
			    + "' because it did not exist");
		    return;
		}
	    }

	    // Click du checkbox du site (s'il est trouv�!)
	    String siteCheckbox = "//td[text() = '" + siteToDelete + "']/preceding-sibling::td/input";
	    if(session().isElementPresent(siteCheckbox)) {
        	session().click(siteCheckbox);
        	    
        
        	//session().click("link=" + getCurrentTestSiteName());
        	//waitForPageToLoad();
        	//session().click("link=Supprimer site");
        	    
        	clickWithMouse("//div[text() = 'Supprimer']/parent::div");
        	pause(1000);
        
        	//session().click("eventSubmit_doRemove_confirmed");
        	session().click("//button[text()='Ok']"); // click du bouton "ok"
        	pause(3000);
	    }
	    
	    // removed by Thanh!
	    //assertFalse("Site not deleted as expected!", session()
		//    .isTextPresent(getCurrentTestSiteName()));
	    
	    
	    
	} catch (Exception e) {
	    // do nothing. Just log error found.
	    log("Exception dans deleteTestSite (cas normal): " + e.getMessage());
	  
	} finally {
	    // Return to Home "tool"
	    session().selectFrame("relative=parent");
	    //session().click("//div[@id='toolMenu']/ul/li[1]/a/span");
	    session().click("//span[text() = 'Accueil']/parent::a");
	    pause(3000);
	}
    } // deleteTestSite
    
    

    /**
     * Terminates the current session by clicking on element loginLink1. After
     * the click, we expect to see element eid (user ID input field).
     */
    public void logOut() throws Exception {
	session().selectFrame("relative=parent");
	session().click("loginLink1");
	pause(1000);
	
	/**
	 * Alert: "Leave page ?"
	 * This confirmation will be shown when user is editing a site for example.
	 */
	if (session().isConfirmationPresent()) {
	    log("Alert: " + session().getConfirmation());
	    return;
	}
	if (session().isAlertPresent()) {
	    log("Alert: " + session().getAlert());
	    return;
	}

	if (session().isPromptPresent()) {
	    log("Alert: " + session().getPrompt());
	    return;
	}


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
     * It creates messages, after creating a log file
     */
    public void logFile(String testName, String ctName, String result) {
    	String role = userString;
    	String browser = browserString;
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String currentDate = dateFormat.format(new java.util.Date());
		StringBuffer msg = new StringBuffer(1000);
		msg.append("(");
		msg.append(testName);
		msg.append(")");
		msg.append("CT-");
		msg.append(ctName);
		msg.append(',');
		msg.append("r�sultat:");
		msg.append(result);
		msg.append(',');
		msg.append("r�le:");
		msg.append(role);
		msg.append(',');
		msg.append("fureteur:");
		msg.append(browser);
		msg.append(',');
		msg.append("date:");
		msg.append(currentDate);
		//msg.append('\n');
		MsgLog.write(msg.toString());
    }


    /**
     * After clicking on the OpenSyllabus tool link, a call to this method
     * ensures that it has loaded and it is displayed.
     */
    public void waitForOSYL() throws Exception {

	session().selectFrame("//iframe[@class=\"portletMainIframe\"]");
	for (int decisecond = 0;; decisecond++) {
	    try {
	    	String element = "//div[@class=\"Osyl-TreeItem-HorizontalPanel\"]";
	    	if (session().isElementPresent(element)) {
	    		break;
	    	}
	    } catch (Exception e) {
	    }
	    Thread.sleep(100);

	    if (decisecond >= 600) {
			logAndFail("Timeout waiting for Osyl-TreeItem-HorizontalPanel sub-structure:"
				+ " __Was OpenSyllabus added to the site?__");
	    }
	    if (session().isTextPresent("Exception")
		    && session().isTextPresent("Stacktrace")
		    && session().isTextPresent("at org.sakaiproject.portal")) {
	    		logAndFail("Found exception waiting for OpenSyllabus: "
	    				+ "deployment may have failed. See screenshot.");
	    }
	}

	log("Found OpenSyllabus: tests will begin now");

	// Increase this to be able to see the test running. Do not set it below
	// 100 as it tends to cause problems (menu items not found for
	// instance).
	session().setSpeed("1000");

    } // waitForOSYL

    /**
     * Clicks on the first lecture in OpenSyllabus while in the view displaying
     * the lecture list (initial view). This method was not thoroughly tested!
     */
    public void enterFirstLecture() throws Exception {
	try {
//	    String locator = "//table[@class=\"Osyl-WorkspaceView-MainPanel\"]/tbody/tr[2]/td/table/tbody/tr/td[1]/div";
	    String locator = "//table[@class=\"Osyl-WorkspaceView-MainPanel\"]/tbody/tr[2]/td/table/tbody/tr/td[1]/div";
	    // enter first lecture
	    session().mouseOver(locator);
	    session().mouseDown(locator);
	    session().mouseUp(locator);
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
	session().click(BUTTON_HOME); // to save
	// Open Organisation section
	openOrganisationSection();

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
     * @param itemId to click
     */
    public void clickAddItem(String itemId) {
	log("Entering clickAddItem " + itemId);

	try {
	    // Click the add button
	    clickAddButton();
		if (inFireFox()) {
		    // Click the item or fail if it is not found
		    session().click("//div[@id=\"" + itemId + "\"]");
		} else { // IE
		    session().click("//div[@id=\"" + itemId + "\"]");
			//session().mouseDownAt("//div[@id=\"" + itemText + "\"]", "10,10");
			//session().mouseUpAt("//div[@id=\"" + itemText + "\"]", "10,10");
		}
	} catch (Exception e) {
	    logAndFail("clickAddButton(" + itemId + ") FAILED: " + e);
	}
    } // clickAddItem(String)
    
    public void clickAddItemWithLog(String itemId) {
	prettyLog(itemId);
	clickAddItem(itemId);
    }

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
	session().click(BUTTON_ADD); //It was "gwt-uid-7"
	waitForElement("//div[contains(@class,'gwt-MenuBarPopup')]", 3000);// vertical bar of items 'Text', 'Document', etc...
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
	pause(5000);
	//pause3();
	String origSpeed = session().getSpeed();
	session().setSpeed("30");
	long start = System.currentTimeMillis();

	clickSaveButton();
	//for (int second = 0;; second++) {
	for (;;) {
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
		    // TODO: After three times one could assume that save works well
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
	session().click(BUTTON_SAVE);  //It was "gwt-uid-4"
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
     * Pauses for a default delay (15 seconds). This allow for any unobtrusive
     * alert to disappear.
     */
    public void pause3() {
	pause(15000);
    }
    
    /**
     * Shortcut for <code>org.testng.Reporter.log(msg, true)</code>.
     */
    protected void log(String msg) {
	Reporter.log(msg, true);
    }
    
    /**
     * Affiche un message bien encadre. Utiliser pour demarquer les sections importantes.
     * @param msg
     */
    protected void prettyLog(String msg) {
 	log("==============================");	
 	log(msg);
 	log("==============================");	
 	
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
    private String getScreenShotFileName(String msg) {
	String fileName = msg.replaceAll("[/\\:\\?\\!\\|><\"\\*\\[\\]\\(\\)]", "_");
	if (fileName.length() > 220) {
	    fileName = fileName.substring(0, 220);
	}
	return SCREENSHOT_DIR + timeStamp() + " " + fileName + ".png";
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


    protected void ensureElementPresent(String element) {
	log("ensureElementPresent: entering " + element);
	int time = 0;
	int increment = 100; //ms
	while(time < MAX_TIME){
	    if (session().isElementPresent(element)) {
		log("ensureElementPresent: found element after " + time + " ms");
		return;
	    }
	    pause(increment);
	    time += increment;
	    log("ensureElementPresent: time spent " + time);
	}
	logAndFail("Unable to find element [" + element + "] after "
		+ time + " ms");
    }

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
     * Does a random selection for this selection box.
     * @param selectBoxXpath
     */
    protected void selectAtRandom(String selectBoxXpath) {
	//String xpathTypeResource = "//form/table/tbody/tr[7]/td/select";		 
	String randomTypeRes = getRandomOption(selectBoxXpath);
	session().select(selectBoxXpath, randomTypeRes);
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

    protected void openOrganisationSection() {
	// Open Seances Section
    pause();
    
    String divLocator = "//div[text() = 'Organisation du cours']";
	if (inFireFox()) {
	    //session().mouseDown("//div[@class=\"gwt-TreeItem\"]/div/div[contains(text(),'Organisation')]");
	    session().mouseDown(divLocator);
	} else {
		//IE
		//String imageLocator = "//div[text() = 'Organisation du cours')]";
		session().mouseDownAt(divLocator, "10,10");
		session().mouseUpAt(divLocator, "10,10");
	}
    pause();
    }

    /**
     *
     * @return The number of resource rows. NOTE: The first row is unused. So the first is at position '.1'
     */
    protected int getResourceCount() {
    	if (session().isTextPresent("//div[@class=\"Osyl-UnitView-ResPanel\"]")) {
    		return session().getXpathCount("//div[@class=\"Osyl-UnitView-ResPanel\"]").intValue();
    	}
        else {
    	    return 0;
        }
    }

    protected void openTeachingMaterialSection() {
	// click on Teaching Material Section
    pause();
	if (inFireFox()) {
	    session().mouseDown(
		    "//div[@class=\"gwt-TreeItem\"]/div/"
			    + "div[contains(text(),'dagogique')]");
	} else {
		// IE
		String imageLocator = "//div[contains(text(),'dagogique')]";
		session().mouseDownAt(imageLocator, "10,10");
		session().mouseUpAt(imageLocator, "10,10");
	}
       pause();
    }

    protected String addText(String text, String level) {
	// Add Text in the last Lecture Unit
	clickAddItem("addText");

	// We edit the new text Lecture
	session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We select randomly the rubric name
	String rubric = getRandomRubric();
	log("Selecting rubric [" + rubric + "]");
	changeRubric(rubric);

	// We select attendee on dissemination level
	if (LEVEL_ATTENDEE.equals(level))
	    session().select(
		    "//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		    "index=0");
	else
	    session().select(
		    "//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		    "index=1");

	// Type some text in the rich-text area
	if (inFireFox()) {
	    // type text
	    session()
		    .selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");

	    session().type("//html/body", text);
	    
	    // close editor
	    session().selectFrame("relative=parent");

	    session().click("//tr/td/button[contains(text(),'OK')]");
	    // session().click("//td/table/tbody/tr/td[1]/button");
	    
	    // check if text is visible
	    if (!session().isTextPresent(text)) {
			logAndFail("Expected to see text [" + text
				+ "] after text edition");
			//Add message to log file
			logFile(OVERVIEW_TEST, CT_006, FAILED);
	    }
	    log("OK: Text resource edited");
	} else {
	    log("RichText edition can only be tested in Firefox");
	    // close editor
	    session().click("//td/table/tbody/tr/td[1]/button");
	}
	
	return rubric;
    }

    /**
     * There is a bug in addText(): the modifier button should be on the latest element added !!
     * @param text
     * @param level
     * @return
     */
    protected String addTextFIXED(String text, String level) {
	// Add Text in the last Lecture Unit
	clickAddItem("addText");
	
	int nbResources = ResourceXpathHelper.getNbResource();
	// We edit the new text Lecture
	session().click(ResourceXpathHelper.getButtonModify(nbResources - 1));// the one was just added is the last
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We select randomly the rubric name
	String rubric = getRandomRubric();
	log("Selecting rubric [" + rubric + "]");
	changeRubric(rubric);

	// We select attendee on dissemination level
	if (LEVEL_ATTENDEE.equals(level))
	    session().select(
		    "//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		    "index=0");
	else
	    session().select(
		    "//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		    "index=1");

	// Type some text in the rich-text area
	if (inFireFox()) {
	    // type text
	    session()
		    .selectFrame("//iframe[@class=\"Osyl-UnitView-TextArea\"]");

	    session().type("//html/body", text);
	    
	    // close editor
	    session().selectFrame("relative=parent");

	    session().click("//tr/td/button[contains(text(),'OK')]");
	    // session().click("//td/table/tbody/tr/td[1]/button");
	    
	    // check if text is visible
	    if (!session().isTextPresent(text)) {
			logAndFail("Expected to see text [" + text
				+ "] after text edition");
			//Add message to log file
			logFile(OVERVIEW_TEST, CT_006, FAILED);
	    }
	    log("OK: Text resource edited");
	} else {
	    log("RichText edition can only be tested in Firefox");
	    // close editor
	    session().click("//td/table/tbody/tr/td[1]/button");
	}
	
	return rubric;
    }

    protected String addDocument(String docName, String clickableText) {

	String docNameModified = docName.replaceAll("\\[", "_");
	docNameModified = docNameModified.replaceAll("\\]", "_");
	// Add new document
	clickAddItem("addDocument");

	// We open Document resource editor
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");
	int nbResources = ResourceXpathHelper.getNbResource();
	// We edit the new text Lecture
	session().click(ResourceXpathHelper.getButtonModify(nbResources - 1));// the one was just added is the last
	//session().click("//tr[2]/td/div/table[2]/tbody/tr/td[1]/button");

	// We choose randomly a Rubric
	String rubric = getRandomRubric();
	log("Selecting rubric [" + rubric + "]");
	changeRubric(rubric);

	// We select attendee on dissemination level
	session().select("//table/tbody/tr/td[2]/table/tbody/tr[2]/td/select",
		"index=0");

	// We type the clickable text
	session().type("//input[@class=\"Osyl-LabelEditor-TextBox\"]",
		clickableText);

	// Open form to upload the document
	if (inFireFox()) {
	    session().mouseOver(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up\"]");
	    session().mouseOver(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up\"]");
	    session().mouseOver(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up\"]");
	    session().mouseOut(
		    "//div[@class=\"Osyl-FileBrowserTopButton"
			    + " Osyl-FileBrowserTopButton-up-hovering\"]");
	    session().mouseOut(
		    "//div[@class=\"Osyl-FileBrowserTopButton"
			    + " Osyl-FileBrowserTopButton-up-hovering\"]");
	    session().mouseDown(
		    "//div[@class=\"Osyl-FileBrowserTopButto"
			    + "n Osyl-FileBrowserTopButton-up-hovering\"]");
	    session().mouseUp(
		    "//div[@class=\"Osyl-FileBrowserTopButton"
			    + " Osyl-FileBrowserTopButton-down-hovering\"]");

	    // Choose file and close window
	    session().type("uploadFormElement", FILE_DIR + docName);
	    
	    // We select randomly the rights field
//	    String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
//	    String newText8 = getRandomOption(xpathRole4);
//	    session().select(xpathRole4, newText8);
//	    pause();
//	    // Close window
//	    session().click("//tr[5]/td/table/tbody/tr/td/button");
//	    pause();
	    
	    // Select randomly the Rights field
	    selectAtRandom(AddFileResourcePopup.getRightsSelect());
	    // Select randomly the type of resources field (eg. 'Recueil de textes')
	    selectAtRandom(AddFileResourcePopup.getTypeOfResourceSelect());
	    pause();
	    
	    // Press OK to Close window
	    session().click(AddFileResourcePopup.getInstance().getButtonOk());
	    pause();

	    
	    
	} else {
		String locator = "//div[contains(@title,'Ajouter')]";
	    session().mouseOver(locator);
		session().mouseDownAt(locator, "10,10");
		session().mouseUpAt(locator, "10,10");
	    // Choose file and close window
		session().type("//input[@name='uploadFormElement']", DESKTOP_DIR +docName);
		/*
	    session().type("uploadFormElement", FILE_DIR + docName);
		 **/
	    // We select randomly the rights field
	    String xpathRole4 = "//div[2]/form/table/tbody/tr[4]/td/select";
	    String newText8 = getRandomOption(xpathRole4);
	    session().select(xpathRole4, newText8);
	    pause();
	    // Close window
	    session().click("//tr[5]/td/table/tbody/tr/td/button");

	}
	pause3();
	// Select file in browser window
	session().select("//tr[2]/td/table/tbody/tr[2]/td/select",
		"value= (F" + ")   " + docNameModified);
	session()
		.mouseOver("//option[@value=' (F)   " + docNameModified + "']");
	session().focus("//option[@value=' (F)   " + docNameModified + "']");
	session().click("//option[@value=' (F)   " + docNameModified + "']");
	pause();

	// Close Editor
	session().click(
		"//td/table/tbody/tr/td[2]/table/tbody/tr/td/table/"
			+ "tbody/tr/td[1]/button");

	return rubric;
    }

    /**
     * Verify if Assignment is present for the course or not.
     * If not, then add it.
     */
    public void addAssessmentInOutLinecours() {

	//Add assessment inside outline course
   	prettyLog("Is Assessment Tool enabled ? If not, enable it for course.");
   	
   	if (session().isElementPresent("//*[@class='icon-sakai-assignment-grades']")) {
   	    return;
   	}
	
	String element = null;   	   	
	if (userString.equalsIgnoreCase("admin")) {
		element = "//html/body/div[@id='portalOuterContainer']/div[@id='portalContainer']/div[@id='headerMax']/div[2]/div[@id='siteNav']/div[@id='linkNav']/ul[@id='siteLinkList']/li[1]/a/span";
	    session().click(element);
		pause();
		goToMenuSiteSetup();
		pause();
		//Click on search siteName
		session().type("search", siteName);
		session().click("//input[@value='Recherche']");
		pause();
		session().click("site1");
		//Reviser
		session().click("//div[1]/ul[@id='actionToolBar']/li[4]/span/a");
		pause();
		//Click on Course Site Tools 
		session().click("//html/body/div/ul/li[2]/span/a");		
		session().click("//html/body/div/ul/li[2]/span/a");		
		session().waitForPageToLoad("30000");		
	} else {
		//Menu Site Information
		element = "//div[@id='container']/div[@id='toolMenuWrap']/div[@id='toolMenu']/ul/li[5]/a/span";// 'Gestionnaire de site de cours' ?
		session().click(element);
		pause();	
		//Edit tools
		session().click("//html/body/div[1]/ul/li[2]/span/a");
		pause3();
	}	
	
	//It checks if assignment is a tool already selected
	//if (!session().getValue("sakai.assignment.grades").equals("on"))  {
		session().click("//input[@id='sakai.assignment.grades']");
	//}
	session().click("Continue");
	pause();
	
	//Save added tool
	session().click("eventSubmit_doSave_revised_features");
	pause();
	
	if (userString.equalsIgnoreCase("prof_selenium")) {
		session().open("/portal/site/" + siteName);
	} else if (userString.equalsIgnoreCase("admin")) {
		session().select("//div[@id='selectNav']/select", "label=" + siteName);		
	}
	
	pause();
	session().click("//div[@id='toolMenu']/ul/li[4]/a/span");
	pause();
    }

    /**
     * Pretty print the test start and end. 
     * @param testName
     */
    private long testStartTime;
    
    protected void logStartTest() {
	int pauseSecs = 30;
	
	log("");log("");// a few blank lines
	prettyLog("===> " + this.getClass().getSimpleName() + " started at " + timeStamp() + " (" + pauseSecs + " secs pause)");
	
	/**
	 * Pause 30 secs to make the test suite work!!
	 * Not pausing WILL create a problem for test suites.
	 * I dont know why!!!
	 * @TODO Make this a configurable testNG parameter
	 */ 
	pause(pauseSecs * 1000);

	testStartTime = System.currentTimeMillis();
	
	
    }
    
    protected void logEndTest() {
	long testDuration = System.currentTimeMillis() - testStartTime;
	prettyLog(this.getClass().getSimpleName() + " completed at " + timeStamp() 
		+ " (duration: " + getElapsedTimeHoursMinutesSecondsString(testDuration) + ")");
    }
    
    /**
     * Wait until an element is present.
     * This should speed up the tests!!!
     * @param locator
     * @param timeout
     * @throws Exception Throws an exception if no
     */
    public void waitForElement(String locator, long timeout) {
	boolean found = false;
	long timeSlice = 250;
	long elapsedTime = 0;

	do {
	    try {
		Thread.sleep(timeSlice);
		elapsedTime += timeSlice;
		found = session().isElementPresent(locator);
	    } catch (Exception e) {
		// nothing
	    }
	} while (!found && elapsedTime < timeout);

	if (!found) {
	    throw new RuntimeException("Element '" + locator
		    + "' not found. Timeout after " + (timeout / 1000)
		    + " seconds.");
	}
    }

    /**
     * Check that file exists.
     * @param filePath
     * @throws Exception
     */
    public void assertFileExists(String filePath) throws Exception {
	File file = new File(filePath);
	assertTrue("File existence: " + filePath, file.exists());// verify file existence
    }



}
