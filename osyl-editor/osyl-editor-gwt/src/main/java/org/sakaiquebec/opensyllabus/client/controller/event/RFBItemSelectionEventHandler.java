/**
 * 
 */
package org.sakaiquebec.opensyllabus.client.controller.event;

import java.util.EventObject;

/**
 * @author <a href="mailto:claude.coulombe@umontreal.ca">Claude Coulombe</a>
 * @version $Id: $
 */

public interface RFBItemSelectionEventHandler {

    /**
     * Event triggered when the Validate Button is pushed
     * 
     * @version $Id: $
     */
    public class RFBItemSelectionEvent extends EventObject {

	/**
	 * constructor.
	 * 
	 * @param source
	 */
	public RFBItemSelectionEvent(Object source) {
	    super(source);
	}

	private static final long serialVersionUID = 1L;

    }

    /*
     * This method is called when the handler is notified a click on the
     * validate button @param event
     */
//   void onClickValidateButton(RFBFileSelectionEvent event);
     void onItemSelectionEvent(RFBItemSelectionEvent event);
}
