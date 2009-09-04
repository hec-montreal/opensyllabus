package org.sakaiquebec.opensyllabus.client.OsylImageBundle;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.MenuBar.MenuBarImages;

/**
 * An ImageBundle that provides images for OsylMenuBar.
 * 
 * @version $Id: $
 */
public interface OsylMenuBarImagesInterface extends MenuBarImages {

    // icon for close button
    public AbstractImagePrototype cross();

    public AbstractImagePrototype home();

    // icon for adding item
    public AbstractImagePrototype plus();

    public AbstractImagePrototype preview();

    public AbstractImagePrototype printer();

    public AbstractImagePrototype publish();

}
