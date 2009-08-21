/**********************************************************************************
 * $Id:  $
 **********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Qu�bec Team.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
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
 **********************************************************************************/

package org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylHostedModeInit;
import org.sakaiquebec.opensyllabus.client.remoteservice.hostedMode.util.OsylHostedModeTransformUtil;
import org.sakaiquebec.opensyllabus.client.remoteservice.rpc.OsylEditorGwtServiceAsync;
import org.sakaiquebec.opensyllabus.shared.api.SecurityInterface;
import org.sakaiquebec.opensyllabus.shared.model.COConfigSerialized;
import org.sakaiquebec.opensyllabus.shared.model.COSerialized;
import org.sakaiquebec.opensyllabus.shared.model.ResourcesLicencingInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

// OsylTestCOMessages
/**
 * @author mathieu colombet This is the embedded implementation of
 *         OsylEditorGwtServiceAsync used in hosted mode only It simulate the
 *         server behavior.
 */
public class OsylEditorHostedModeImpl implements OsylEditorGwtServiceAsync {

	private final static String SITE_ID = "6b9188e5-b3ca-49dd-be7c-540ad9bd60c4";
	private COConfigSerialized configSer;

	/**
	 * Initialization properties object used in hosted mode such as ConfigPath,
	 * ModelPath, implement class will be choose by GWT differed binding
	 * mechanism please see module.xml definition to find implementation class
	 */
	private static OsylHostedModeInit osylHostedModeInit = (OsylHostedModeInit) GWT
			.create(OsylHostedModeInit.class);

	public void applyPermissions(String resourceId, String permission,
			AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void createOrUpdateAssignment(String assignmentId, String title,
			String instructions, int openYear, int openMonth, int openDay,
			int openHour, int openMinute, int closeYear, int closeMonth,
			int closeDay, int closeHour, int closeMinute, int percentage,
			AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void createOrUpdateAssignment(String assignmentId, String title,
			AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void createOrUpdateCitation(String citationListId, String citation,
			String author, String type, String isbnIssn, String link,
			AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void createTemporaryCitationList(AsyncCallback<String> callback) {
		callback.onSuccess("dummyCitationListId");
	}

	public void getCurrentUserRole(AsyncCallback<String> callback) {
		callback.onSuccess(SecurityInterface.SECURITY_ROLE_PROJECT_MAINTAIN);
	}

	public void getResourceLicenceInfo(
			AsyncCallback<ResourcesLicencingInfo> callback) {

		ResourcesLicencingInfo ress = new ResourcesLicencingInfo();
		ress.setCopyrightTypeList(new ArrayList<String>());
		ress.getCopyrightTypeList().add(
				"hosted mode : Material is in public domain.");
		ress.getCopyrightTypeList().add("hosted mode : I hold copyright.");
		ress.getCopyrightTypeList().add(
				"hosted mode : Material is subject to fair une exception.");
		ress
				.getCopyrightTypeList()
				.add(
						"hosted mode : I have obtained permission to use this material.");
		ress.getCopyrightTypeList().add(
				"hosted mode : Copyright status is not yet determined.");
		ress.getCopyrightTypeList().add("hosted mode : Use copyright below.");
		callback.onSuccess(ress);

	}

	public void getSerializedConfig(
			final AsyncCallback<COConfigSerialized> callback) {
		if (configSer == null) {
			configSer = new COConfigSerialized("config-test-id");

			// Getting UI messages for config
			getFileByRequest(osylHostedModeInit.getUIMessagesPath(),
					new RequestCallback() {
						public void onError(Request request, Throwable exception) {
							Window.alert("Error while reading "
									+ osylHostedModeInit.getUIMessagesPath()
									+ " :" + exception.toString());
						}

						public void onResponseReceived(Request request,
								Response response) {
							String responseTxt = response.getText();
							// transform text to map
							Map<String, String> messages = OsylHostedModeTransformUtil
									.propertyTxt2Map(responseTxt);
							configSer.setCoreBundle(messages);
						}
					});

			// Getting RolesList for config
			getFileByRequest(osylHostedModeInit.getRolesListPath(),
					new RequestCallback() {
						public void onError(Request request, Throwable exception) {
							Window.alert("Error while reading "
									+ osylHostedModeInit.getRolesListPath()
									+ " :" + exception.toString());
						}

						public void onResponseReceived(Request request,
								Response response) {
							String responseTxt = response.getText();
							// transform text to List
							List<String> list = new ArrayList<String>();
							try {
								list = OsylHostedModeTransformUtil
										.xmlTxt2List(responseTxt);
							} catch (Exception e) {
								e.printStackTrace();
								Window.alert(e.getMessage() + " " + e);
							}
							configSer.setRolesList(list);
						}
					});

			// Getting EvalTypeList for config
			getFileByRequest(osylHostedModeInit.getEvalTypeListPath(),
					new RequestCallback() {
						public void onError(Request request, Throwable exception) {
							Window.alert("Error while reading "
									+ osylHostedModeInit.getEvalTypeListPath()
									+ " :" + exception.toString());
						}

						public void onResponseReceived(Request request,
								Response response) {
							String responseTxt = response.getText();
							// transform text to List
							List<String> list = new ArrayList<String>();
							try {
								list = OsylHostedModeTransformUtil
										.xmlTxt2List(responseTxt);
								configSer.setEvalTypeList(list);
							} catch (Exception e) {
								e.printStackTrace();
								Window.alert(e.getMessage() + " " + e);
							}							
						}
					});

			// Getting Rules for config
			RequestBuilder requestBuilder = null;

			requestBuilder = new RequestBuilder(RequestBuilder.GET,
					osylHostedModeInit.getConfigPath());

			try {
				requestBuilder.sendRequest(null, new RequestCallback() {
					public void onError(Request request, Throwable exception) {
						Window.alert("Error while reading "
								+ osylHostedModeInit.getConfigPath() + " :"
								+ exception.toString());
					}

					public void onResponseReceived(Request request,
							Response response) {
						configSer.setRulesConfig(response.getText());
						callback.onSuccess(configSer);
					}
				});
			} catch (RequestException ex) {
				Window.alert("Error while reading "
						+ osylHostedModeInit.getConfigPath() + " :"
						+ ex.toString());
			}
		}
	}

	public void getSerializedCourseOutline(String id,
			AsyncCallback<COSerialized> callback) {
		getSerializedCourseOutline(callback);

	}

	private void getFileByRequest(final String path,
			final RequestCallback callback) {

		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET,
				path);

		try {
			requestBuilder.sendRequest(null, callback);
		} catch (RequestException ex) {
			Window.alert("Error while reading " + path + " :" + ex.toString());
		}
	}

	public void getSerializedCourseOutline(
			final AsyncCallback<COSerialized> callback) {

		final COSerialized modeledCo = new COSerialized();
		modeledCo.setSiteId(SITE_ID);

		getFileByRequest(osylHostedModeInit.getCOMessagesPath(),
				new RequestCallback() {
					public void onError(Request request, Throwable exception) {
						Window.alert("Error while reading "
								+ osylHostedModeInit.getCOMessagesPath() + " :"
								+ exception.toString());
					}

					public void onResponseReceived(Request request,
							Response response) {
						String responseTxt = response.getText();
						// transform text to map
						Map<String, String> messages = OsylHostedModeTransformUtil
								.propertyTxt2Map(responseTxt);
						modeledCo.setMessages(messages);
					}
				});

		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET,
				osylHostedModeInit.getModelPath());

		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					Window.alert("Error while reading "
							+ osylHostedModeInit.getModelPath() + " :"
							+ exception.toString());
				}

				public void onResponseReceived(Request request,
						Response response) {
					modeledCo.setContent(response.getText());
					callback.onSuccess(modeledCo);
				}
			});
		} catch (RequestException ex) {
			Window.alert("Error while reading "
					+ osylHostedModeInit.getModelPath() + " :" + ex.toString());
		}

	}

	public void getSerializedPublishedCourseOutlineForAccessType(
			String accessType, AsyncCallback<COSerialized> callback) {
		getSerializedCourseOutline(callback);
	}

	public void getXslForGroup(String group, AsyncCallback<String> callback) {
		// TODO Auto-generated method stub

	}

	public void hasBeenPublished(AsyncCallback<Boolean> callback) {
		callback.onSuccess(true);
	}

	public void initTool(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void ping(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void publishCourseOutline(AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void removeAssignment(String assignmentId,
			AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void removeCitation(String citationId, AsyncCallback<Void> callback) {
		callback.onSuccess(null);
	}

	public void updateSerializedCourseOutline(COSerialized co,
			AsyncCallback<String> callback) {
		callback.onSuccess("hostedId");
	}

}
