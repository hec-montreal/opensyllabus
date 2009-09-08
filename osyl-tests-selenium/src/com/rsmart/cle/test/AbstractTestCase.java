package com.rsmart.cle.test;

import com.thoughtworks.selenium.SeleneseTestCase;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.closeSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.startSeleniumSession;
import static com.thoughtworks.selenium.grid.tools.ThreadSafeSeleniumSessionStorage.session;
import org.testng.annotations.*;

import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
/*
 * Copyright 2008 The rSmart Group
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Contributor(s): jbush
 */

abstract public class AbstractTestCase extends SeleneseTestCase {
   protected String fileServer;

   @BeforeClass(alwaysRun = true)
   @Parameters({"seleniumHost", "seleniumPort", "browser", "webSite", "timeout", "fileServer"})
   public void setUp(String seleniumHost, int seleniumPort, String browser, String webSite, String timeout, String fileServer) {
      startSeleniumSession(seleniumHost, seleniumPort, browser, webSite);
      // session().setTimeout(timeout);
      if (!fileServer.endsWith("/")) {
         fileServer =  fileServer + "/";
      }

      this.fileServer = fileServer;
   }

   @AfterClass(alwaysRun = true)
   public void tearDown() throws Exception {
      closeSeleniumSession();
   }

   protected String dateFormat(Date date, String format) {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      return sdf.format(date);
   }


   //##### TODAYS DATE MM/dd/yyyy hh:mm a #####
   public String date() {
      Date day = new Date();
      return dateFormat(day, "MM/dd/yyyy hh:mm a");
   }

   


   //##### TOMORROWS DATE MM/dd/yyyy hh:mm a #####
   public String tomorrowsdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 1);
      return dateFormat(cal.getTime(), "MM/dd/yyyy hh:mm a");
   }


   //##### YESTERDAYS DATE MM/dd/yyyy hh:mm a #####
   public String yesterdaysdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -1);
      return dateFormat(cal.getTime(), "MM/dd/yyyy hh:mm a");
   }

   //##### TODAYS DATE for SAMIGO and PODCASTS #####
   public String samdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 1);
      return dateFormat(cal.getTime(), "MM/dd/yyyy hh:mm:ss a");
   }

   //##### TOMORROWS DATE for SAMIGO and PODCASTS #####
   public String samtomorrowsdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 1);
      return dateFormat(cal.getTime(), "MM/dd/yyyy hh:mm:ss a");
   }

   //##### YESTERDAYS DATE for SAMIGO and PODCASTS #####
   public String samyesterdaysdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -1);
      return dateFormat(cal.getTime(), "MM/dd/yyyy hh:mm:ss a");
   }


   //##### NEXT WEEKS DATE MM/dd/yyyy hh:mm a #####
   public String nextweek() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 7);
      return dateFormat(cal.getTime(), "MM/dd/yyyy hh:mm a");
   }

   //##### TODAYS DATE for mmm d, yyyy hh:mm a #####
   public String meldate() {
      return dateFormat(new Date(), "MMM d, yyyy hh:mm a");
   }


   //##### TOMORROWS DATE for mmm d, yyyy hh:mm a #####
   public String meltomorrowsdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 1);
      return dateFormat(cal.getTime(), "MMM d, yyyy hh:mm a");
   }


   //##### YESTERDAYS DATE for mmm d, yyyy hh:mm a #####
   public String melyesterdaysdate() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -1);
      return dateFormat(cal.getTime(), "MMM d, yyyy hh:mm a");
   }


   //##### NEXT WEEKS DATE for mmm d, yyyy hh:mm a #####
   public String melnextweek() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 7);
      return dateFormat(cal.getTime(), "MMM d, yyyy hh:mm a");
   }

   //##### TOMORROW #####
   public String tomorrow() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, 1);
      return dateFormat(cal.getTime(), "dd");
   }


   //##### YESTERDAY #####
   public String yesterday() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DATE, -1);
      return dateFormat(cal.getTime(), "dd");
   }


   //##### ADD 1 HOUR #####
   public String addhour() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.HOUR, 1);
      return dateFormat(cal.getTime(), "h");
   }


   //##### AM or PM #####
   public String ampm() {
      return dateFormat(new Date(), "a");
   }


   //##### AM or PM for Add Hour #####
   public String ampmAH() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.HOUR, 1);
      return dateFormat(cal.getTime(), "a");
   }


   //##### AM or PM for ASSIGNMENTS #####
   public String ampmASN() {
      return dateFormat(new Date(), "a");
   }


   //##### AM or PM for ASSIGNMENTS with Add Hour #####
   public String ampmAHASN() {
      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.HOUR, 1);
      return dateFormat(cal.getTime(), "a");
   }


   //##### CURRENT HOUR #####
   public String currenthour() {
      return dateFormat(new Date(), "h");
   }
}


