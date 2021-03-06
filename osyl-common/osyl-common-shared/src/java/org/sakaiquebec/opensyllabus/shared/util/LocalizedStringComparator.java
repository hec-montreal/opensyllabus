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
package org.sakaiquebec.opensyllabus.shared.util;

import java.util.Comparator;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class LocalizedStringComparator implements Comparator<String> {

    private static final LocalizedStringComparator instance =
	    new LocalizedStringComparator();

    private LocalizedStringComparator() {
	super();
    }

    public static final LocalizedStringComparator getInstance() {
	return instance;
    }

    public native int compareJS(String source, String target)/*-{ 
	return source.localeCompare(target); 
    }-*/;

    public int compare(String o1, String o2) {
	return compareJS(o1, o2);
    }

}
