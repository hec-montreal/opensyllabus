
package org.sakaiquebec.opensyllabus.admin.impl.extracts;


import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2011 The Sakai Foundation, The Sakai Quebec Team.
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
 *****************************************************************************/

/**
 *
 * Accéder aux informations provenant du fichier charge_formation.dat
 *
 * @author <a href="mailto:mame-awa.diop@hec.ca">Mame Awa Diop</a>
 * @version $Id: $
 */
public class DetailChargeFormationMap extends HashMap<String,DetailChargeFormationMapEntry> {

     static final long serialVersionUID = 1489188447867453239L;

    private static Log log = LogFactory.getLog(DetailChargeFormationMap.class);

    public DetailChargeFormationMapEntry get (String matricule){
	return (DetailChargeFormationMapEntry) super.get(matricule);
    }

    public void put(DetailChargeFormationMapEntry chargeFormation) {
	put(chargeFormation.getEmplId(),chargeFormation);
    }

    public boolean contains(MatriculeNomMapEntry matricule) {
	return containsKey(matricule.getMatricule());
    }
}
