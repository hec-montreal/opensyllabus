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

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * The image bundle interface
 * 
 * @version $Id: $
 */
public interface OsylImageBundleInterface extends ImageBundle {

    public AbstractImagePrototype action_validate();

    public AbstractImagePrototype action_cancel();

    public AbstractImagePrototype action_OneLevelUp();

    public AbstractImagePrototype cancelEdit();

    public AbstractImagePrototype carreVert();

    public AbstractImagePrototype delete();
    
    public AbstractImagePrototype document_add();

    public AbstractImagePrototype edit();

    public AbstractImagePrototype folderAdd();

    public AbstractImagePrototype notImportant();

    public AbstractImagePrototype notVisible();
     
    public AbstractImagePrototype publish();

    public AbstractImagePrototype rtt_backColors();

    public AbstractImagePrototype rtt_bold();

    public AbstractImagePrototype rtt_createLink();

    public AbstractImagePrototype rtt_fonts();

    public AbstractImagePrototype rtt_fontSizes();

    public AbstractImagePrototype rtt_foreColors();

    public AbstractImagePrototype rtt_hr();

    public AbstractImagePrototype rtt_indent();

    public AbstractImagePrototype rtt_insertImage();

    public AbstractImagePrototype rtt_italic();

    public AbstractImagePrototype rtt_justifyCenter();

    public AbstractImagePrototype rtt_justifyLeft();

    public AbstractImagePrototype rtt_justifyRight();

    public AbstractImagePrototype rtt_ol();

    public AbstractImagePrototype rtt_outdent();

    public AbstractImagePrototype rtt_removeFormat();

    public AbstractImagePrototype rtt_removeLink();

    public AbstractImagePrototype rtt_strikeThrough();

    public AbstractImagePrototype rtt_subscript();

    public AbstractImagePrototype rtt_superscript();

    public AbstractImagePrototype rtt_ul();
    
    public AbstractImagePrototype save();

    public AbstractImagePrototype treeClosed();

    public AbstractImagePrototype treeLeaf();

    public AbstractImagePrototype treeOpen();

    public AbstractImagePrototype rtt_underline();

    public AbstractImagePrototype visible();
    
    public AbstractImagePrototype iconeObl();

    public AbstractImagePrototype up_full();
    
    public AbstractImagePrototype up_empty();

    public AbstractImagePrototype down_full();
    
    public AbstractImagePrototype down_empty();
    
}
