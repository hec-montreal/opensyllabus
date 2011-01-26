/*******************************************************************************
 * ****************************************************************************
 * $Id: $
 * *****************************************************************************
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team. Licensed
 * under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a
 * copy of the License at http://www.opensource.org/licenses/ecl1.php Unless
 * required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.sakaiquebec.opensyllabus.client.OsylImageBundle;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * The image bundle interface
 * 
 * @version $Id: $
 */
public interface OsylImageBundleInterface extends ClientBundle {

    public ImageResource action_validate();

    public ImageResource action_cancel();

    public ImageResource action_OneLevelUp();

    public ImageResource cancelEdit();

    public ImageResource carreVert();

    public ImageResource delete();
    
    public ImageResource document_add();
    
    public ImageResource document_edit();

    public ImageResource edit();

    public ImageResource folder_add();

    public ImageResource notImportant();

    public ImageResource notVisible();
     
    public ImageResource rtt_backColors();

    public ImageResource rtt_bold();

    public ImageResource rtt_createLink();

    public ImageResource rtt_fonts();

    public ImageResource rtt_fontSizes();

    public ImageResource rtt_foreColors();

    public ImageResource rtt_hr();

    public ImageResource rtt_indent();

    public ImageResource rtt_insertImage();

    public ImageResource rtt_italic();

    public ImageResource rtt_justifyCenter();

    public ImageResource rtt_justifyLeft();

    public ImageResource rtt_justifyRight();

    public ImageResource rtt_ol();

    public ImageResource rtt_outdent();

    public ImageResource rtt_removeFormat();

    public ImageResource rtt_removeLink();

    public ImageResource rtt_strikeThrough();

    public ImageResource rtt_subscript();

    public ImageResource rtt_superscript();

    public ImageResource rtt_ul();
    
    public ImageResource treeClosed();

    public ImageResource treeLeaf();

    public ImageResource treeOpen();

    public ImageResource rtt_underline();

    public ImageResource visible();

    public ImageResource up_full();
    
    public ImageResource up_empty();

    public ImageResource down_full();
    
    public ImageResource down_empty();
    
    // Specific Methods for the MenuBar
    
    // icon for close button
    public ImageResource cross();

    public ImageResource home();
    
    public ImageResource view_all();

    // icon for adding item
    public ImageResource plus();

    public ImageResource preview();

    public ImageResource printer();
    
    public ImageResource pdf_export();
    
    public ImageResource options();

    public ImageResource publish();
    
    public ImageResource save();
    
    public ImageResource save_disabled();
    
    //DisclosurePanel images
    public ImageResource expand();

    public ImageResource collapse();
    
    public ImageResource check16();
    
    public ImageResource cross16();
}
