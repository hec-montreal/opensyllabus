package org.sakaiquebec.opensyllabus.client.remoteservice.json.callback;

import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mathieu Colombet
 * implementation of OsylCallBackAdaptator used when there is no returned object
 */
public class OsylVoidCallBackAdaptator extends OsylCallBackAdaptator<Void> {

	protected static final boolean TRACE = false;

	/**
	 * Constructor
	 * 
	 * @param asyncCallback
	 *            AsyncCallback<List<OsylAbstractBrowserItem>>
	 */
	public OsylVoidCallBackAdaptator(AsyncCallback<Void> asyncCallback) {
		super(asyncCallback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Void adaptResponse(Response response) {
		return null;
	}
}
