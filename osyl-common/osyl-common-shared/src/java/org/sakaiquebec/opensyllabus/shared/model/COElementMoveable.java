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
package org.sakaiquebec.opensyllabus.shared.model;
/**
 *
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public interface COElementMoveable {

    /**
     * Test if the current element has successor in the structure (element of same level)
     * @return true if the element has successor, false otherwise
     */
    public boolean hasSuccessor() ;
    
    /**
     * Test if the current element has predecessor in the structure (element of same level)
     * @return true if the element has predecessor, false otherwise
     */
    public boolean hasPredecessor() ;

    /**
     * Move the element up (in the xml and in the page)
     */
    public void moveUp() ;

    /**
     * Move the element down (in the xml and in the page)
     */
    public void moveDown() ;
    
}

