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

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public class FormHelper {

    /**
     * Creates a invisible textbox with given name as name of textbox and given
     * value as its value
     * 
     * @param name to set as name of textbox
     * @param value to set as value of textbox
     * @return the textbox
     */
    public static TextBox createHiddenField(String name, String value) {
	TextBox tb = new TextBox();
	tb.setName(name);
	tb.setText(value);
	tb.setVisible(false);
	return tb;
    }
    
    /**
     * Creates an empty lixtbox and sets CSS style 
     * and width
     * 
     * @param text to set as value
     * @return the textarea
     */
    public static ListBox createListBox(String stylePrimaryName) {
	ListBox lb = new ListBox();
	lb.setStylePrimaryName(stylePrimaryName);
	lb.setWidth("99%");
	return lb;
    }
    
    
    /**
     * Creates a textbox with given text as value
     * and sets CSS style and width
     * 
     * @param text to set as value
     * @return the textbox
     */
    public static TextBox createTextBox(String text, String stylePrimaryName) {
	TextBox tb = new TextBox();
	tb.setStylePrimaryName(stylePrimaryName);
	tb.setWidth("99%");
	tb.setText(text);
	return tb;
    }

    /**
     * Creates a textarea with given text as value 
     * and sets CSS style, width, and height
     * 
     * @param text to set as value
     * @return the textarea
     */
    public static TextArea createTextArea(String text, String stylePrimaryName) {
	TextArea ta = new TextArea();
	ta.setStylePrimaryName(stylePrimaryName);
	ta.setWidth("99%");
	ta.setHeight("110px");
	ta.setText(text);
	return ta;
    }

}
