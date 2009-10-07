/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
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

package org.sakaiquebec.opensyllabus.client.ui.listener;

import org.sakaiquebec.opensyllabus.client.ui.view.OsylAbstractView;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Listener for handling click events in a {@link RichTextArea}. When a click
 * happens it notifies interested parties about the current resource proxy and
 * ensures that the richText toolBar is visible.
 * 
 * @author <a href="mailto:Remi.Saias@hec.ca">Remi Saias</a>
 */
public class OsylRichTextAreaClickListener implements ClickListener {
// TODO: Voir si cette classe doit etre gardee ou non... peut-être qu'on va en avoir besoin
    // dans le cas des ressources avec plusieurs zones de texte...

    private OsylAbstractView view;
    
    /**
     * Constructor.
     * 
     * @param view
     */
    public OsylRichTextAreaClickListener(OsylAbstractView view) {
	this.view = view;
    }
    
    /** {@inheritDoc} */
    public void onClick(Widget sender) {

	// We notify interested parties about the newly selected resource proxy
	view.getController().getViewContext().setChild(view);

    }

} // MyRichTextAreaListener
