/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.manager.client.message;

import com.google.gwt.i18n.client.ConstantsWithLookup;

/**
 * @author <a href="mailto:Laurent.Danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface Messages extends ConstantsWithLookup {

    String courseName();

    String file();

    String isZip();

    String create();

    String siteCreated();
    
    String siteNotCreated();
    
    String importXML();
    
    String rpcFailure();
    
    String siteIdUnable();
    
    String createSiteTitle();
    
    String importCOTitle();
    
    String finishMessage();
    
    String exportCOTitle();
    
    String exportCO();
    
    String osylSitesList();
    
    String optionsTitle();
    
    String createOption();
    
    String exportOption();
    
    String valid();
    
    String noCOSite();
    
    String unableToExportCO();
    
    String associateDissociate();
    
    String associate();
    
    String dissociate();
    
    String noAssociableCOSite();

}
