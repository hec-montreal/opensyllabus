/*******************************************************************************
 * *****************************************************************************
 * $Id: $
 * ******************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.ui.base;


import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusListenerAdapter;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Window;

/**
 * A {@link OsylPushButton} that displays a small list of choices in a
 * {@link ListBox} when pushed.
 * 
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 */
public class OsylMenuButton extends Composite {

    private OsylPushButton menuButton;
    private ListBox menuListBox;
    private PopupPanel popUp;
    private boolean firstTime;
    private String selectedValue = "defaultValue";
    // xFitting and YFitting are relative to the dimension of the button image
    public final int xFitting = 30;
    public final int yFitting = 35;

    /**
     * Constructor of the OsylMenuButton
     * 
     * @param imageEnabled : image showed when the button is enabled
     * @param imageDisabled : image showed when the button is disabled
     * @param newListItems : a list of couple Strings itemId, itemValue
     */
    public OsylMenuButton(Image imageEnabled, Image imageDisabled,
	    Image imageHover, String[] listItems) {

	// If needed initialize the List of Items
	if (listItems == null) {
	    listItems = new String[2];
	    listItems[0] = "";
	    listItems[1] = "";
	}
	final HorizontalPanel mainButtonPanel = new HorizontalPanel();

	// The ListBox Menu which contains the choices
	setMenuListBox(new ListBox());
	getMenuListBox().setVisible(false);
	getMenuListBox().setStylePrimaryName("Osyl-MenuButton-ListBox");

	// PopUpPanel is used to content and show the List of Choice
	popUp = new PopupPanel();

	setMenuButton(new OsylPushButton(imageEnabled, imageDisabled,
		imageHover));
	// We set the style of both button and panel otherwise we cannot set its
	// background to transparent!
	getMenuButton().setStylePrimaryName("Osyl-MenuButton");
	mainButtonPanel.setStylePrimaryName("Osyl-MenuButton");
	getMenuButton().setEnabled(true);
	firstTime = true;

	// Adding a ClickListener to the PushButton to show a List of choices
	getMenuButton().addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		getMenuListBox().setVisible(true);
		int x = sender.getAbsoluteLeft();
		int y = sender.getAbsoluteTop();
		// To avoid to add many MenuListBox
		if (firstTime) {
		    x += 34;
		    popUp.setWidget(getMenuListBox());
		}
		// xFitting and YFitting are relative to the dimension of the
		// image
		popUp.setPopupPosition(x + xFitting, y + yFitting);
		getMenuListBox().setVisibleItemCount(
			getMenuListBox().getItemCount());
		popUp.show();
		getMenuListBox().setVisible(true);
		getMenuListBox().setFocus(true);
/*		// Tricks to be able to show the ListBox without the arrow icon
		int indexLastElement = getMenuListBox().getItemCount() - 1;
		String temporaryText =
			getMenuListBox().getItemText(indexLastElement);
		String temporaryValue = getMenuListBox().getValue(indexLastElement);
		getMenuListBox().removeItem(indexLastElement);
		getMenuListBox().addItem(temporaryText, temporaryValue);
		// Tricks to avoid to show the arrow icon when only one item
		// is visible, then the box was displayed a drop-down list.
		if (indexLastElement == 0) {
		    getMenuListBox().addItem("", "");
		    getMenuListBox().setVisibleItemCount(2);
		}
*/		firstTime = false;
	    }
	});

	// Adding a ClickListener to the ListBox in order to select a value
	getMenuListBox().addClickListener(new ClickListener() {
	    public void onClick(Widget sender) {
		getMenuListBox().setFocus(false);
		getMenuButton().getPushButton().setFocus(true);
		int selectedIndex = getMenuListBox().getSelectedIndex();
		String selectedValue = getMenuListBox().getValue(selectedIndex);
		if (selectedValue.equals("")) {
		    return;
		}
		setSelectedValue(selectedValue);
	    }
	});

	// Adding a FocusListener to the ListBox in order to hide
	// the ListBox after the choice was done
	getMenuListBox().addFocusListener(new FocusListenerAdapter() {
	    public void onLostFocus(Widget sender) {
		getMenuListBox().setVisible(false);
	    }
	});

	// Call the initilization of the list of choices
	addListItems(listItems);
	mainButtonPanel.add(getMenuButton());
	mainButtonPanel.add(getMenuListBox());

	// All composites must call initWidget() in their constructors
	initWidget(mainButtonPanel);
    }

    /**
     * Practical 2 parameters constructor of OsylMenuButton
     * 
     * @param imageEnabled : image showed when the button is enabled
     * @param imageDisabled : image showed when the button is disabled
     */
    public OsylMenuButton(Image imageEnabled, Image imageDisabled,
	    Image imageHover) {
	this(imageEnabled, imageDisabled, imageHover, null);
    }

    public ListBox getMenuListBox() {
	return this.menuListBox;
    }

    public void setMenuListBox(ListBox newMenuListBox) {
	this.menuListBox = newMenuListBox;
    }

    public OsylPushButton getMenuButton() {
	return this.menuButton;
    }

    public void setMenuButton(OsylPushButton newMenuButton) {
	this.menuButton = newMenuButton;
    }

    public void setEnabledButton() {
	getMenuButton().setEnabledButton();
    }
    
    public void setDisableButton(){
	getMenuButton().setDisabledButton();
    }

    public void addClickListener(ClickListener listener) {
	getMenuButton().addClickListener(listener);
    }

    /**
     * SelectedValue is used to store the last selected value from the ListBox
     * in order to get it for further process
     * 
     * @return String selectedValue
     */
    public String getSelectedValue() {
	return this.selectedValue;
    }

    public void setSelectedValue(String selectedValue) {
	this.selectedValue = selectedValue;
    }

    /**
     * Setter of a list of choices to be displayed when pushing the button
     * 
     * @param newListItems : list of pairs : itemId, itemValue so item's number
     *                should be divisible by 2
     */
    public void addListItems(String[] newListItems) {
	getMenuListBox().clear();
	if ((newListItems.length % 2) != 0) {
	    Window.alert("Internal error: item count is not divisible by 2.\n"
		    + "Pair: itemString, itemValue");
	    return;
	}
	int i = 0;
	while (i < newListItems.length) {
	    getMenuListBox().addItem(newListItems[i], newListItems[i + 1]);
	    i += 2;
	}
    }

}
