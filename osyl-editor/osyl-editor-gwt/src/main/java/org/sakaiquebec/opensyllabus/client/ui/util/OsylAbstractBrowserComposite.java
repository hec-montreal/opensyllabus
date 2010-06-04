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
package org.sakaiquebec.opensyllabus.client.ui.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sakaiquebec.opensyllabus.client.OsylImageBundle.OsylImageBundleInterface;
import org.sakaiquebec.opensyllabus.client.controller.OsylController;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresItemListingAcquiredEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.FiresMySitesListingAcquiredEvents;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.MySitesListingAcquiredEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBAddFolderEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.RFBItemSelectionEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.UploadFileEventHandler;
import org.sakaiquebec.opensyllabus.client.controller.event.ItemListingAcquiredEventHandler.ItemListingAcquiredEvent;
import org.sakaiquebec.opensyllabus.client.ui.dialog.OsylAlertDialog;
import org.sakaiquebec.opensyllabus.client.ui.view.editor.OsylAbstractEditor;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylAbstractBrowserItem;
import org.sakaiquebec.opensyllabus.shared.model.file.OsylDirectory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract class defining common methods for resources browsers
 * 
 * @author <a href="mailto:laurent.danet@hec.ca">Laurent Danet</a>
 * @version $Id: $
 */
public abstract class OsylAbstractBrowserComposite extends Composite implements
	UploadFileEventHandler, FiresItemListingAcquiredEvents,
	FiresMySitesListingAcquiredEvents {

    protected static final boolean TRACE = false;

    private static final String RESSOURCE_URI_PREFIX = "/group/";

    // Number of elements shown in the file listing
    private static final int LISTING_ITEM_NUMBER = 6;

    private String entityUri;

    private String entityText;

    private OsylDirectory currentDirectory = null;

    private String baseDirectoryPath;

    private String initialDirPath;

    private String browsedSiteId;
    
    private String oldSelectedEntity;

    private Map<String, String> mySites;

    // private ListBox mySitesListBox;

    private TextBox currentSiteTextBox;
    private TextBox currentDirectoryTextBox;

    // And the html content
    HTML currentSelectionHtml = new HTML();

    private DoubleClickListBox fileListing;

    private OsylAbstractBrowserItem selectedAbstractBrowserItem;

    private OsylAbstractBrowserItem itemToSelect;

    private TextBox currentSelectionTextBox;

    private AsyncCallback<List<OsylAbstractBrowserItem>> remoteDirListingRespHandler =
	    new AsyncCallback<List<OsylAbstractBrowserItem>>() {

		/**
		 * {@inheritDoc}
		 */
		public void onFailure(Throwable caught) {
		    removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
		    final OsylAlertDialog alertBox =
			    new OsylAlertDialog(false, true, getController()
				    .getUiMessage("Global.error"),
				    getController().getUiMessage(
					    "fileUpload.unableReadRemoteDir")
					    + caught.getMessage());
		    alertBox.center();
		    alertBox.show();
		}

		/**
		 * {@inheritDoc}
		 */
		public void onSuccess(List<OsylAbstractBrowserItem> dirListing) {
		    try {
			OsylAbstractBrowserComposite.this
				.refreshFileListing(dirListing);
		    } finally {
			getFileListing().removeStyleName(
				"Osyl-RemoteFileBrowser-WaitingState");
			removeStyleName("Osyl-RemoteFileBrowser-WaitingState");
		    }
		}
	    };

    private AsyncCallback<Map<String, String>> siteProvidersCallback =
	    new AsyncCallback<Map<String, String>>() {
		public void onSuccess(Map<String, String> sitesProviders) {
		    try {
			getController().setAllowedProviders(sitesProviders);
			getController().getExistingEntities(getController().getSiteId(), getSitesEntitiesCallback());

		    } catch (Exception error) {
			Window
				.alert("Error - Unable to getAllowedProviders(...) on RPC Success: "
					+ error.toString());
		    }
		}

		// And we define the behavior in case of failure
		public void onFailure(Throwable error) {
		    System.out
			    .println("RPC FAILURE - getAllowedProviders(...): "
				    + error.toString()
				    + " Hint: Check GWT version");
		    Window.alert("RPC FAILURE - getAllowedProviders(...): "
			    + error.toString() + " Hint: Check GWT version");
		}
	    };

    private Tree providers;

    public Tree getProviders() {
	return providers;
    }

    public void setProviders(Tree providers) {
	this.providers = providers;
    }

    public AsyncCallback<Map<String, String>> getSiteProvidersCallback() {
	return siteProvidersCallback;
    }

    public void setSiteProvidersCallback(
	    AsyncCallback<Map<String, String>> siteProvidersCallback) {
	this.siteProvidersCallback = siteProvidersCallback;
    }

    private AsyncCallback<Map<String, String>> sitesEntitiesCallback =
	    new AsyncCallback<Map<String, String>>() {
		public void onSuccess(Map<String, String> sitesEntites) {
		    try {
			getController().setExistingEntities(sitesEntites);
			refreshSitesEntitiesListing(getEntityUri());
		    } catch (Exception error) {
			Window
				.alert("Error - Unable to getExistingEntities(...) on RPC Success: "
					+ error.toString());
		    }
		}

		// And we define the behavior in case of failure
		public void onFailure(Throwable error) {
		    System.out
			    .println("RPC FAILURE - getExistingEntities(...): "
				    + error.toString()
				    + " Hint: Check GWT version");
		    Window.alert("RPC FAILURE - getExistingEntities(...): "
			    + error.toString() + " Hint: Check GWT version");
		}
	    };

    public AsyncCallback<Map<String, String>> getSitesEntitiesCallback() {
	return sitesEntitiesCallback;
    }

    public void setSitesEntitiesCallback(
	    AsyncCallback<Map<String, String>> sitesEntitiesCallback) {
	this.sitesEntitiesCallback = sitesEntitiesCallback;
    }

    private ArrayList<RFBAddFolderEventHandler> RFBAddFolderEventHandlerList;

    private ArrayList<RFBItemSelectionEventHandler> RFBFileSelectionEventHandlerList;

    private ArrayList<ItemListingAcquiredEventHandler> fileListingAcquiredEventHandlerList;

    private ArrayList<MySitesListingAcquiredEventHandler> mySitesAcquiredEventHandlerList;

    private PushButton folderAddButton;

    private PushButton addFileButton;

    // Image Bundle
    private OsylImageBundleInterface osylImageBundle =
	    (OsylImageBundleInterface) GWT
		    .create(OsylImageBundleInterface.class);

    /**
     * Constructors for OsylRemoteFileBrowser with default directory and no
     * filter
     */

    public OsylAbstractBrowserComposite() {
	super();
	initView();
    }

    public OsylAbstractBrowserComposite(String newDirPath) {
	super();
	setInitialDirPath(newDirPath);
    }

    public OsylAbstractBrowserComposite(String entity, boolean isEntity) {
	super();
	if (isEntity)
	    setEntityUri(entity);
	initView();
    }

    /**
     * Constructor.
     * 
     * @param model
     * @param newController
     */
    public OsylAbstractBrowserComposite(String newResDirName,
	    OsylAbstractBrowserItem fileToSelect) {
	setBrowsedSiteId(extractRessourceSiteId(newResDirName));
	setCurrentDirectory(new OsylDirectory(newResDirName, newResDirName, "",
		new ArrayList<OsylAbstractBrowserItem>()));

	setInitialDirPath(getCurrentDirectory().getDirectoryPath());
	setItemToSelect(fileToSelect);
	initView();
    }

    /**
     * Initialize and renders the view of the <code>OsylRemoteFileBrowser</code>
     * .
     */

    public void initView() {

	// Browser Main Panel
	VerticalPanel mainContentPanel = new VerticalPanel();

	// All composites must call initWidget() in their constructors.
	initWidget(mainContentPanel);

	// First row sub-panel
	HorizontalPanel firstRowPanel = new HorizontalPanel();
	mainContentPanel.add(firstRowPanel);
	firstRowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

	// 1st row, 1st widget: Folder name label
	Label folderPanelLabel =
		new Label(getController().getUiMessage(
			"UtilityRemoteFileBrowser_FolderPanelLabel"));
	folderPanelLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	folderPanelLabel.setWidth("130px");
	firstRowPanel.add(folderPanelLabel);

	// 1st row, 2nd widget: Folder TextBox
	TextBox folderName = new TextBox();
	if (currentDirectory.getDirectoryName() != null)
	    folderName.setText(extractRessourceUri(currentDirectory
		    .getDirectoryName()));
	setCurrentDirectoryTextBox(folderName);
	getCurrentDirectoryTextBox().setWidth("315px");
	// To disallow direct edition of the file path, set to true:
	// and comment-out the 4 lines below
	getCurrentDirectoryTextBox().setReadOnly(false);
	getCurrentDirectoryTextBox().addKeyPressHandler(
		new CurrentFolderEnterKeyPressHandler());
	getCurrentDirectoryTextBox().addBlurHandler(
		new CurrentFolderBlurHandler());
	firstRowPanel.add(getCurrentDirectoryTextBox());

	// 1st row, 3rd widget, button panel:
	HorizontalPanel buttonPanel = new HorizontalPanel();
	firstRowPanel.add(buttonPanel);

	// One Level Up Button
	PushButton upButton =
		createTopButton(getOsylImageBundle().action_OneLevelUp(),
			getController()
				.getUiMessage("Browser.upButton.tooltip"));
	upButton.addClickHandler(new UpButtonClickHandler());
	buttonPanel.add(upButton);

	// Folder Creation button
	folderAddButton =
		createTopButton(getOsylImageBundle().folder_add(),
			getController().getUiMessage(
				"Browser.addFolderButton.tooltip"));
	folderAddButton.addClickHandler(new FolderAddButtonClickHandler());
	buttonPanel.add(folderAddButton);

	// Add file button
	addFileButton = createAddPushButton();
	buttonPanel.add(addFileButton);

	// edit button
	PushButton editButton = createEditButton();
	if (editButton != null) {
	    buttonPanel.add(editButton);
	}

	// 2nd row: File Listing
	setFileListing(new DoubleClickListBox());
	getFileListing().setVisibleItemCount(LISTING_ITEM_NUMBER);
	getFileListing().addStyleName("Osyl-RemoteFileBrowser-FileListing");

	// Put the Listing in place
	// Trick to avoid selection of the first item at initialization
	getFileListing().setSelectedIndex(-1);
	getFileListing().addClickHandler(new SpecialDoubleClickHandler());

	mainContentPanel.add(getFileListing());

	// 3rd row: File field (first a label then a TextBox)
	HorizontalPanel fileSelectionSubPanel = new HorizontalPanel();
	mainContentPanel.add(fileSelectionSubPanel);

	// The Label
	HTML currentSelectionLabel =
		new HTML(getCurrentSelectionLabel()
			+ OsylAbstractEditor.MANDATORY_FIELD_INDICATOR);
	currentSelectionLabel.addStyleName("Osyl-RemoteFileBrowser-TitleLabel");
	currentSelectionLabel.setWidth("130px");
	fileSelectionSubPanel.add(currentSelectionLabel);

	// And the TextBox
	setCurrentSelectionTextBox(new TextBox());
	getCurrentSelectionTextBox().setText("");
	getCurrentSelectionTextBox().setReadOnly(true);
	getCurrentSelectionTextBox().setWidth("315px");
	fileSelectionSubPanel.add(getCurrentSelectionTextBox());

	// Give the overall composite a style name
	this.setStylePrimaryName("Osyl-RemoteFileBrowser");

	// Fill the listing
	getRemoteDirectoryListing(getCurrentDirectory().getDirectoryPath());
	// getCurrentDirectoryTextBox().setText(
	// getCurrentDirectory().getDirectoryPath());
    }

    public OsylAbstractBrowserItem getItemToSelect() {
	return itemToSelect;
    }

    public void setItemToSelect(OsylAbstractBrowserItem item) {
	this.itemToSelect = item;
    }

    public Map<String, String> getMySites() {
	return mySites;
    }

    protected void updateCurrentSelectionHtml(String entity) {
	getCurrentSelectionHtml().setHTML(entity);

    }

    public HTML getCurrentSelectionHtml() {
	return currentSelectionHtml;
    }

    public void setCurrentSelectionHtml(HTML currentSelectionHtml) {
	this.currentSelectionHtml = currentSelectionHtml;
    }

    // public AsyncCallback<Map<String, String>> getMySitesCallback() {
    // return mySitesCallback;
    // }
    //
    // public void setMySitesCallback(
    // AsyncCallback<Map<String, String>> mySitesCallback) {
    // this.mySitesCallback = mySitesCallback;
    // }

    public void setMySites(Map<String, String> mySites) {
	this.mySites = mySites;
    }

    protected PushButton createTopButton(ImageResource img, String tooltip) {
	PushButton button = new PushButton(new Image(img));
	button.setTitle(tooltip);
	button.setStylePrimaryName("Osyl-FileBrowserTopButton");
	button.setSize("24px", "24px");
	return button;
    }

    /**
     * Sorts the specified list according to the type and name of each element.
     * Folders come before files and name is case insensitive.
     */
    private void sortDirContent(List<OsylAbstractBrowserItem> dirListing) {
	java.util.Collections.sort(dirListing,
		new Comparator<OsylAbstractBrowserItem>() {
		    public int compare(OsylAbstractBrowserItem file1,
			    OsylAbstractBrowserItem file2) {

			if (file1.isFolder() == file2.isFolder()) {
			    return file1.getFileName().toLowerCase().compareTo(
				    file2.getFileName().toLowerCase());
			} else {
			    if (file1.isFolder()) {
				return -1;
			    } else {
				return 1;
			    }
			}
		    }
		});
    }

    /**
     * Retrieves the remote directory listing via sdata
     * 
     * @param directoryPath the path where to look to get the file listing.
     */
    public abstract void getRemoteDirectoryListing(String directoryPath);

    public String formatListingLine(OsylAbstractBrowserItem abstractBrowserItem) {
	String formattedLine = " ";
	String fileName = abstractBrowserItem.getFileName();
	formattedLine +=
		getController().getUiMessage(abstractBrowserItem.getItemTag());
	formattedLine += "   " + fileName;
	return formattedLine;
    }

    /**
     * This class represents the box that contains the file listing and where
     * files can be selected by a single click and where folders are accessed by
     * a double click or by pressing Enter key.
     */
    public class DoubleClickListBox extends ListBox {

	private static final int DBCLICK_DURATION = 200;

	private Timer clickTimer = null;

	public DoubleClickListBox() {
	    // Create a normal ListBox
	    super();
	    // Sink the double click
	    this.sinkEvents(Event.ONDBLCLICK);
	}

	public void onBrowserEvent(Event event) {
	    // Handle events as a normal ListBox
	    super.onBrowserEvent(event);
	    // Look at the type of event
	    int type = DOM.eventGetType(event);
	    switch (type) {
	    // If it is a double click event, handle it
	    case Event.ONDBLCLICK: {
		// Code to execute for a double click event
		if (clickTimer != null) {
		    if (TRACE)
			Window.alert("double clicked");
		    this.onDoubleClick();
		    clickTimer.cancel();
		    clickTimer = null;
		}
		break;
	    }
		// If it is a click event, handle it
	    case Event.ONCLICK: {
		// Create a new timer that calls Window.alert()
		if (clickTimer == null) {
		    clickTimer = new Timer() {
			public void run() {
			    if (TRACE)
				Window.alert("simple click");
			    onSimpleClick();
			}
		    };
		}
		// Schedule the timer to run once in 200 ms
		clickTimer.schedule(DBCLICK_DURATION);
		break;
	    }
	    default:
		break;
	    }
	}

	public void onSimpleClick() {
	    int indexSelectedFile = getFileListing().getSelectedIndex();
	    if (indexSelectedFile >= 0) {
		getFileListing().setItemSelected(indexSelectedFile, true);
		getFileListing().addStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
		String newFilePath =
			getCurrentDirectory().getDirectoryPath()
				+ "/"
				+ getCurrentDirectory().getFilesList().get(
					indexSelectedFile).getFileName();
		newFilePath = uriSlashCorrection(newFilePath);
		OsylAbstractBrowserItem selectedFile =
			getCurrentDirectory().getFilesList().get(
				indexSelectedFile);

		setSelectedAbstractBrowserItem(selectedFile);
		setItemToSelect(selectedFile);
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");

		// Notify all the event handlers and
		// call the onFileSelectionEvent on each one
		RFBItemSelectionEventHandler.RFBItemSelectionEvent event =
			new RFBItemSelectionEventHandler.RFBItemSelectionEvent(
				getBaseDirectoryPath()
					+ RESSOURCE_URI_PREFIX
					+ getCurrentDirectoryTextBox()
						.getText() + "/"
					+ selectedFile.getFileName());
		if (getRFBFileSelectionEventHandlerList() != null) {
		    Iterator<RFBItemSelectionEventHandler> iter =
			    getRFBFileSelectionEventHandlerList().iterator();
		    while (iter.hasNext()) {
			RFBItemSelectionEventHandler handler =
				(RFBItemSelectionEventHandler) iter.next();
			handler.onItemSelectionEvent(event);
		    }
		}
	    }
	}

	public void onDoubleClick() {
	    int index = getFileListing().getSelectedIndex();
	    if (index >= 0) {
		getFileListing().addStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");

		OsylAbstractBrowserItem selectedFile =
			getCurrentDirectory().getFilesList().get(index);
		setSelectedAbstractBrowserItem(selectedFile);

		if (selectedFile.isFolder()) {
		    String newFilePath = selectedFile.getFilePath();
		    newFilePath = uriSlashCorrection(newFilePath);
		    getCurrentDirectory().setDirectoryPath(newFilePath);
		    setCurrentDirectory((OsylDirectory) getCurrentDirectory()
			    .getFilesList().get(index));
		    getFileListing().clear();
		    getRemoteDirectoryListing(newFilePath);
		    getFileListing().removeStyleName(
			    "Osyl-RemoteFileBrowser-WaitingState");
		    getCurrentDirectoryTextBox().setText(
			    extractRessourceUri(newFilePath));
		    getCurrentSelectionTextBox().setText("");
		    getCurrentDirectory().setDirectoryPath(newFilePath);
		    // Trick to avoid selection of the first item at
		    // initialization
		    getFileListing().setSelectedIndex(-1);
		} else {
		    setItemToSelect(selectedFile);
		    onFileDoubleClicking();
		}
		RFBItemSelectionEventHandler.RFBItemSelectionEvent event =
			new RFBItemSelectionEventHandler.RFBItemSelectionEvent(
				getBaseDirectoryPath()
					+ RESSOURCE_URI_PREFIX
					+ getCurrentDirectoryTextBox()
						.getText() + "/"
					+ selectedFile.getFileName());
		if (getRFBFileSelectionEventHandlerList() != null) {
		    Iterator<RFBItemSelectionEventHandler> iter =
			    getRFBFileSelectionEventHandlerList().iterator();
		    while (iter.hasNext()) {
			RFBItemSelectionEventHandler handler =
				(RFBItemSelectionEventHandler) iter.next();
			handler.onItemSelectionEvent(event);
		    }
		}
		// }
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
	    }
	}
    }

    private final class CurrentFolderBlurHandler implements BlurHandler {

	public void onBlur(BlurEvent event) {
	    getCurrentDirectoryTextBox().setText(
		    extractRessourceUri(getCurrentDirectory()
			    .getDirectoryPath()));
	}
    }

    private final class CurrentFolderEnterKeyPressHandler implements
	    KeyPressHandler {
	public void onKeyPress(KeyPressEvent event) {
	    if (event.getCharCode() == KeyCodes.KEY_ENTER) {
		// Fill the listing
		String newFilePath = getCurrentDirectoryTextBox().getText();
		try {
		    getRemoteDirectoryListing(newFilePath);
		    getCurrentDirectoryTextBox().setText(
			    extractRessourceUri(getCurrentDirectory()
				    .getDirectoryPath()));
		    getCurrentSelectionTextBox().setText("");
		    int nbrElements =
			    getCurrentDirectory().getFilesList().size();
		    for (int i = 0; i < nbrElements; i++) {
			getFileListing().addItem(
				formatListingLine(getCurrentDirectory()
					.getFilesList().get(i)));
		    }
		    nbrElements = getCurrentDirectory().getFilesList().size();
		    for (int i = 0; i < nbrElements; i++) {
			if (newFilePath.endsWith(getCurrentDirectory()
				.getFilesList().get(i).getFileName())) {
			    getFileListing().setItemSelected(i, true);
			}
		    }
		} catch (Exception e) {
		    // TODO: handle exception
		}
		getFileListing().removeStyleName(
			"Osyl-RemoteFileBrowser-WaitingState");
		return;
	    }
	}
    }

    class SpecialDoubleClickHandler implements ClickHandler {
	// just get the click event and dispatch it
	public void onClick(Widget sender) {
	    sender.sinkEvents(Event.ONDBLCLICK);
	}

	public void onClick(ClickEvent event) {

	}
    }

    private final class UpButtonClickHandler implements ClickHandler {
	public void onClick(ClickEvent event) {
	    if (currentDirectory == null) {
		return;
	    }
	    String newFilePath = getCurrentDirectory().getDirectoryPath();
	    if (newFilePath.contains("/")) {
		newFilePath =
			newFilePath.substring(0, newFilePath.lastIndexOf("/"));
	    }
	    setCurrentDirectory(new OsylDirectory());
	    getCurrentDirectory().setDirectoryPath(newFilePath);
	    // Fill the listing
	    getFileListing().clear();
	    getRemoteDirectoryListing(newFilePath);
	    getCurrentDirectoryTextBox().setText(
		    extractRessourceUri(newFilePath));
	    getCurrentSelectionTextBox().setText("");
	    getFileListing().removeStyleName(
		    "Osyl-RemoteFileBrowser-WaitingState");
	    onUpButtonClick();
	}
    }

    private final class FolderAddButtonClickHandler implements ClickHandler {
	public void onClick(ClickEvent event) {
	    // Notify all the event handlers and
	    // call the onClickAddFolderButton on each one
	    RFBAddFolderEventHandler.RFBAddFolderEvent rFBevent =
		    new RFBAddFolderEventHandler.RFBAddFolderEvent(
			    RESSOURCE_URI_PREFIX
				    + getCurrentDirectoryTextBox().getText());
	    if (getRFBAddFolderEventHandlerList() != null) {
		Iterator<RFBAddFolderEventHandler> iter =
			getRFBAddFolderEventHandlerList().iterator();
		while (iter.hasNext()) {
		    RFBAddFolderEventHandler handler =
			    (RFBAddFolderEventHandler) iter.next();
		    handler.onClickAddFolderButton(rFBevent);
		}
	    }
	}
    }

    public OsylController getController() {
	return OsylController.getInstance();
    }

    /**
     * Utilitary method to correct a url.
     * 
     * @param initialURL The url to correct.
     * @return
     */
    public static String uriSlashCorrection(String initialURL) {
	String correctURL = initialURL;
	// Removes antislash
	correctURL = initialURL.replace("\\", "/");
	// Remove double slash
	if (correctURL.contains("://")) {
	    correctURL = correctURL.replace("://", "\\");
	    correctURL = correctURL.replace("//", "/");
	    correctURL = correctURL.replace("\\", "://");
	} else {
	    correctURL = correctURL.replace("//", "/");
	}
	return correctURL;
    }

    // GETTERS AND SETTERS
    public AsyncCallback<List<OsylAbstractBrowserItem>> getRemoteDirListingRespHandler() {
	return remoteDirListingRespHandler;
    }

    public ArrayList<MySitesListingAcquiredEventHandler> getMySitesAcquiredEventHandlerList() {
	return mySitesAcquiredEventHandlerList;
    }

    public void setMySitesAcquiredEventHandlerList(
	    ArrayList<MySitesListingAcquiredEventHandler> mySitesAcquiredEventHandlerList) {
	this.mySitesAcquiredEventHandlerList = mySitesAcquiredEventHandlerList;
    }

    public OsylDirectory getCurrentDirectory() {
	return currentDirectory;
    }

    public void setCurrentDirectory(OsylDirectory currentDirectory) {
	this.currentDirectory = currentDirectory;
    }

    public TextBox getCurrentDirectoryTextBox() {
	return currentDirectoryTextBox;
    }

    public void setCurrentDirectoryTextBox(TextBox currentDirectoryTextBox) {
	this.currentDirectoryTextBox = currentDirectoryTextBox;
    }

    public TextBox getCurrentSiteTextBox() {
	return currentSiteTextBox;
    }

    public void setCurrentSiteTextBox(TextBox currentSiteTextBox) {
	this.currentSiteTextBox = currentSiteTextBox;
    }

    public DoubleClickListBox getFileListing() {
	return fileListing;
    }

    public void setFileListing(DoubleClickListBox fileListing) {
	this.fileListing = fileListing;
    }

    public TextBox getCurrentSelectionTextBox() {
	return currentSelectionTextBox;
    }

    // public ListBox getMySitesListBox() {
    // return mySitesListBox;
    // }
    //
    // public void setMySitesListBox(ListBox mySitesListBox) {
    // this.mySitesListBox = mySitesListBox;
    // }

    public void setCurrentSelectionTextBox(TextBox currentSelectionTextBox) {
	this.currentSelectionTextBox = currentSelectionTextBox;
    }

    public String getInitialDirPath() {
	return initialDirPath;
    }

    /**
     * @return the baseDirectoryPath value.
     */
    public String getBaseDirectoryPath() {
	return this.baseDirectoryPath;
    }

    /**
     * @param newDirPath the new value of Previous Directory Path.
     */
    public void setInitialDirPath(String newDirPath) {
	this.initialDirPath = newDirPath;
    }

    /**
     * @return the selectedAbstractFileItem value.
     */
    public OsylAbstractBrowserItem getSelectedAbstractBrowserItem() {
	return this.selectedAbstractBrowserItem;
    }

    /**
     * @param selectedAbstractFileItem the new value of
     *            selectedAbstractFileItem.
     */
    public void setSelectedAbstractBrowserItem(
	    OsylAbstractBrowserItem selectedAbstractFileItem) {
	this.selectedAbstractBrowserItem = selectedAbstractFileItem;
    }

    public OsylImageBundleInterface getOsylImageBundle() {
	return osylImageBundle;
    }

    public String getBrowsedSiteId() {
	return browsedSiteId;
    }

    public void setBrowsedSiteId(String currentSiteId) {
	this.browsedSiteId = currentSiteId;
    }

    /**
     * @return the rFBFileSelectionEventHandlerList value.
     */
    public ArrayList<RFBAddFolderEventHandler> getRFBAddFolderEventHandlerList() {
	return this.RFBAddFolderEventHandlerList;
    }

    public String getEntityUri() {
	return entityUri;
    }

    public void setEntityUri(String entityUri) {
	this.entityUri = entityUri;
    }

    /**
     * @param arrayList the new value of rFBFileSelectionEventHandlerList.
     */
    public void setRFBAddFolderEventHandlerList(
	    ArrayList<RFBAddFolderEventHandler> arrayList) {
	this.RFBAddFolderEventHandlerList = arrayList;
    }

    /**
     * @return the rFBFileSelectionEventHandlerList value.
     */
    public ArrayList<RFBItemSelectionEventHandler> getRFBFileSelectionEventHandlerList() {
	return this.RFBFileSelectionEventHandlerList;
    }

    public String getEntityText() {
	return entityText;
    }

    public void setEntityText(String entityText) {
	this.entityText = entityText;
    }

    /**
     * @param arrayList the new value of rFBFileSelectionEventHandlerList.
     */
    public void setRFBFileSelectionEventHandlerList(
	    ArrayList<RFBItemSelectionEventHandler> arrayList) {
	this.RFBFileSelectionEventHandlerList = arrayList;
    }

    public ArrayList<ItemListingAcquiredEventHandler> getFileListingAcquiredEventHandlerList() {
	return fileListingAcquiredEventHandlerList;
    }

    public void setFileListingAcquiredEventHandlerList(
	    ArrayList<ItemListingAcquiredEventHandler> fileListingAcquiredEventHandlerList) {
	this.fileListingAcquiredEventHandlerList =
		fileListingAcquiredEventHandlerList;
    }

    //
    /** {@inheritDoc} */
    public void addEventHandler(RFBAddFolderEventHandler handler) {
	if (handler == null) {
	    return;
	}
	if (getRFBAddFolderEventHandlerList() == null) {
	    setRFBAddFolderEventHandlerList(new ArrayList<RFBAddFolderEventHandler>());
	}
	getRFBAddFolderEventHandlerList().add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(RFBAddFolderEventHandler handler) {
	if (getRFBAddFolderEventHandlerList() != null) {
	    getRFBAddFolderEventHandlerList().remove(handler);
	}
    }

    /** {@inheritDoc} */
    public void addEventHandler(RFBItemSelectionEventHandler handler) {
	if (handler == null) {
	    return;
	}
	if (getRFBFileSelectionEventHandlerList() == null) {
	    setRFBFileSelectionEventHandlerList(new ArrayList<RFBItemSelectionEventHandler>());
	}
	getRFBFileSelectionEventHandlerList().add(handler);
    }

    /** {@inheritDoc} */
    public void removeEventHandler(RFBItemSelectionEventHandler handler) {
	if (getRFBFileSelectionEventHandlerList() != null) {
	    getRFBFileSelectionEventHandlerList().remove(handler);
	}
    }

    public void addEventHandler(ItemListingAcquiredEventHandler handler) {
	if (handler != null) {
	    if (getFileListingAcquiredEventHandlerList() == null) {
		setFileListingAcquiredEventHandlerList(new ArrayList<ItemListingAcquiredEventHandler>());
	    }
	    getFileListingAcquiredEventHandlerList().add(handler);
	}
    }

    public void addEventHandler(MySitesListingAcquiredEventHandler handler) {
	if (handler != null) {
	    if (getMySitesAcquiredEventHandlerList() == null) {
		setMySitesAcquiredEventHandlerList(new ArrayList<MySitesListingAcquiredEventHandler>());
	    }
	    getMySitesAcquiredEventHandlerList().add(handler);
	}
    }

    public void removeEventHandler(MySitesListingAcquiredEventHandler handler) {
	if (getMySitesAcquiredEventHandlerList() != null) {
	    getMySitesAcquiredEventHandlerList().remove(handler);
	}
    }

    public void removeEventHandler(ItemListingAcquiredEventHandler handler) {
	if (getFileListingAcquiredEventHandlerList() != null) {
	    getFileListingAcquiredEventHandlerList().remove(handler);
	}
    }

    public void notifyFileListingAcquiredEventHandlers() {

	if (getFileListingAcquiredEventHandlerList() != null) {
	    ItemListingAcquiredEvent event = new ItemListingAcquiredEvent(this);
	    for (int i = 0; i < getFileListingAcquiredEventHandlerList().size(); i++) {
		getFileListingAcquiredEventHandlerList().get(i)
			.onItemListingAcquired(event);
	    }
	}
    }

    /** to refresh the listing */
    public void onUploadFile(UploadFileEvent event) {
	getRemoteDirectoryListing(getCurrentDirectory().getDirectoryPath());
    }

    /**
     * Refreshes the file browser according to the newly selected file.
     */
    public void refreshBrowser() {

	boolean fileItemFound = false;
	OsylAbstractBrowserItem itemToSelect = getItemToSelect();
	if (itemToSelect != null) {
	    List<OsylAbstractBrowserItem> fileList =
		    getCurrentDirectory().getFilesList();
	    for (int i = 0; i < fileList.size(); i++) {
		OsylAbstractBrowserItem fileItem = fileList.get(i);
		if (fileItem.equals(itemToSelect)) {
		    fileItemFound = true;
		    setSelectedAbstractBrowserItem(fileItem);
		    getFileListing().setSelectedIndex(i);
		    getFileListing().setFocus(true);
		    getCurrentDirectoryTextBox().setText(
			    extractRessourceUri(getCurrentDirectory()
				    .getDirectoryPath()));
		    if (fileItem.isFolder()) {
			getCurrentSelectionTextBox().setText("");
		    } else {
			getCurrentSelectionTextBox().setText(
				fileItem.getFileName());
		    }
		}
	    }
	}

	if (!fileItemFound) {
	    setSelectedAbstractBrowserItem(null);
	    getFileListing().setSelectedIndex(-1);
	}
    }

    public String getOldSelectedEntity() {
        return oldSelectedEntity;
    }

    public void setOldSelectedEntity(String oldSelectedEntity) {
        this.oldSelectedEntity = oldSelectedEntity;
    }

    protected String extractRessourceUri(String uri) {
	String uu =
		uri.substring(uri.indexOf(RESSOURCE_URI_PREFIX)
			+ RESSOURCE_URI_PREFIX.length());
	return uu;
    }

    /**
     * Returns the site id provided in the document path
     * 
     * @param uri
     * @return uri for presentation
     */
    protected String extractRessourceSiteId(String uri) {
	String uu = extractRessourceUri(uri);
	return uu.substring(0, uu.indexOf("/"));
    }

    public void refreshSitesEntitiesListing(String selectedEntity) {
	getProviders().clear();

	int countProviders = 0;
	Map<String, String> entities =
		getController()
			.getExistingEntities(getController().getSiteId());
	Map<String, String> allowedProviders =
		getController().getAllowedProviders();
	TreeItem providers = new TreeItem();
	Set<String> entitiesKeys = entities.keySet();
	Set<String> providersKeys = allowedProviders.keySet();
	TreeItem selected = null;
	for (String pkey : providersKeys) {
	    providers = new TreeItem(pkey.toUpperCase());
	    for (String key : entitiesKeys) {
		if (key.contains(pkey)) {
		    selected = providers.addItem(entities.get(key));
		    if (selectedEntity != null && selectedEntity.contains(key)) {
			providers.setState(true, true);
			selected.setSelected(true);
			selected.addStyleName("Osyl-RemoteEntityBrowser-SelectedEntity");
			updateSelectedEntity(selectedEntity);
			updateCurrentSelectionHtml(getLinkURI(selectedEntity,
				entities.get(key)));
		    }
		    countProviders++;
		}
	    }
	    if (providers.getChildCount() > 0) {
		getProviders().addItem(providers);

	    }

	}

	if (countProviders == 0) {
	    // TODO: internationaliser le message
	    providers =
		    new TreeItem("This site contains no entities".toUpperCase());
	    getProviders().addItem(providers);

	}
    }

    protected void updateSelectedEntity(String entity ){
	TreeItem item, subItem;
	String oldEntity = getOldSelectedEntity();
	int count = getProviders().getItemCount();
	for(int i=0; i<count; i++){
	    item = getProviders().getItem(i);
	    for (int j=0; j< item.getChildCount(); j++){
		subItem = item.getChild(j);
	    if (oldEntity != null)
		if (oldEntity.contains(subItem.getText()))
		    item.removeStyleName("Osyl-RemoteEntityBrowser-SelectedEntity");
	    if (entity != null)
		if(entity.contains(subItem.getText())){
		    item.addStyleName("Osyl-RemoteEntityBrowser-SelectedEntity");
		    setOldSelectedEntity(entity);
		}
	    
	    }
	}
    }
    
    
    protected  String getLinkURI(String uri, String text) {
	// We get the URI from the model
	String link;

	// Otherwise we have to prepend Sakai stuff in the URI
	String url = GWT.getModuleBaseURL();
	String serverId = url.split("\\s*/portal/tool/\\s*")[0];
	link = serverId + "/direct" + uri;

	return "<a href=\"" + link + "\" target=\"_blank\">" + text + "</a>";
    } // getLinkURI

    protected void refreshFileListing(List<OsylAbstractBrowserItem> dirListing) {
	getFileListing().clear();
	getCurrentDirectory().setFilesList(dirListing);
	if (dirListing != null) {
	    sortDirContent(dirListing);
	    for (int i = 0; i < dirListing.size(); i++) {
		OsylAbstractBrowserItem fileItem = dirListing.get(i);
		getFileListing().addItem(formatListingLine(fileItem));
	    }
	}
	notifyFileListingAcquiredEventHandlers();
    }

    protected PushButton getFolderAddButton() {
	return folderAddButton;
    }

    protected PushButton getAddFileButton() {
	return addFileButton;
    }

    protected void onUpButtonClick() {
	// nothing to do
    }

    // ABSTRACT METHOD
    protected abstract PushButton createEditButton();

    protected abstract PushButton createAddPushButton();

    protected abstract String getCurrentSelectionLabel();

    protected abstract void onFileDoubleClicking();

}
