package org.sakaiquebec.opensyllabus.client.remoteservice.json.callback;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Mathieu Colombet
 * @param <T>
 * Class to adapt AsyncCallback which are the standard call back in our application 
 * in RequestCallback which are use by the json remote call implementations.
 */
public abstract class OsylCallBackAdaptator<T> implements RequestCallback {

	final private AsyncCallback<T> asyncCallback;
	protected static final boolean TRACE = false;

	/**
	 * constructor
	 * 
	 * @param asyncCallback
	 */
	public OsylCallBackAdaptator(AsyncCallback<T> asyncCallback) {
		super();
		this.asyncCallback = asyncCallback;
	}

	/**
	 * {@inheritDoc}
	 */
	public final void onError(Request request, Throwable exception) {
		this.asyncCallback.onFailure(exception);
	}

	/**
	 * {@inheritDoc}
	 */
	public final void onResponseReceived(Request request, Response response) {
		if (response.getStatusCode() == Response.SC_OK) {
			T t = null;
			try {
				t = adaptResponse(response);
			} catch (Exception e) {
				if(TRACE){
					Window.alert(e.getMessage());
				}
				Exception myE = new Exception("Error during response serialization", e);
				this.asyncCallback.onFailure(myE);
			}
			this.asyncCallback.onSuccess(t);
		} else {
			Exception e = new Exception("Invalid response status code : "
					+ response.getStatusCode());
			this.asyncCallback.onFailure(e);
		}
	}

	/**
	 * Method to override to provide the adequate request conversion
	 * 
	 * @param response
	 * @return T
	 */
	protected abstract T adaptResponse(Response response);

}
