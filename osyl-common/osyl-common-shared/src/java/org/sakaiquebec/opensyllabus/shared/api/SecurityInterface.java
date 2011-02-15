/*******************************************************************************
 * $Id: $
 *******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package org.sakaiquebec.opensyllabus.shared.api;

/**
 * This interface provides security and context-related methods used by the DAO
 * and services.
 * 
 * @author <a href="mailto:tom.landry@crim.ca">Tom Landry</a>
 * @version $Id: $
 */
public interface SecurityInterface {

    /**
     * Community access security value.
     */
    public static final String ACCESS_COMMUNITY = "community";

    /**
     * Public access security value.
     */
    public static final String ACCESS_PUBLIC = "public";
    
    /**
     * Attendee access security value.
     */
    public static final String ACCESS_ATTENDEE = "attendee";


}
