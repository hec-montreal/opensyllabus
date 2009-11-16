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
package org.sakaiquebec.opensyllabus.client.helper;

import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class FormHelper {

    /**
     * Creates a hidden field with given name and value.
     * 
     * @param name to set as name of field
     * @param value to set as value of field
     * @return the hidden field
     */
    public static Hidden createHiddenField(String name, String value) {
	return new Hidden(name, value);
    }
    
    /**
     * Creates an empty listBox and sets CSS style 
     * and width
     * 
     * @param text to set as value
     * @return the listBox
     */
    public static ListBox createListBox(String stylePrimaryName) {
	ListBox lb = new ListBox();
	lb.setStylePrimaryName(stylePrimaryName);
	lb.setWidth("99%");
	return lb;
    }
    
    
    /**
     * Creates a textBox with given text as value
     * and sets CSS style and width
     * 
     * @param text to set as value
     * @return the textBox
     */
    public static TextBox createTextBox(String text, String stylePrimaryName) {
	TextBox tb = new TextBox();
	tb.setText(text);
	tb.setStylePrimaryName(stylePrimaryName);
	return tb;
    }

    /**
     * Creates a textArea with given text as value 
     * and sets CSS style, width, and height
     * 
     * @param text to set as value
     * @return the textArea
     */
    public static TextArea createTextArea(String text, String stylePrimaryName) {
	TextArea ta = new TextArea();
	ta.setStylePrimaryName(stylePrimaryName);
	ta.setText(text);
	return ta;
    }

}
