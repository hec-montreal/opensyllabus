package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.coursemanagement.api.AcademicCareer;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.evaluation.constant.EvalConstants;
import org.sakaiproject.evaluation.logic.EvalEvaluationService;
import org.sakaiproject.evaluation.logic.model.EvalGroup;
import org.sakaiproject.evaluation.logic.model.EvalUser;
import org.sakaiproject.evaluation.model.EvalAnswer;
import org.sakaiproject.evaluation.model.EvalEvaluation;
import org.sakaiproject.evaluation.model.EvalItem;
import org.sakaiproject.evaluation.model.EvalTemplateItem;
import org.sakaiproject.evaluation.utils.EvalUtils;
import org.sakaiproject.evaluation.utils.TemplateItemDataList;
import org.sakaiproject.evaluation.utils.TemplateItemUtils;
import org.sakaiproject.evaluation.utils.TemplateItemDataList.DataTemplateItem;
import org.sakaiproject.evaluation.utils.TemplateItemDataList.HierarchyNodeGroup;
import org.sakaiproject.evaluation.utils.TemplateItemDataList.TemplateItemGroup;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiquebec.opensyllabus.admin.api.ConfigurationService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CreateEvalSysPdfJob;

public class CreateEvalSysPdfJobImpl extends OsylAbstractQuartzJobImpl
	implements CreateEvalSysPdfJob {

    private String termEid;
    
    private PropertyResourceBundle bundle = null;

    private int displayNumber = 0;

    private List<EvalEvaluation> selectedEval = null;

    String evalsysReportsFolder = null;

    private static Log log = LogFactory.getLog(CreateEvalSysPdfJobImpl.class);

    protected ContentHostingService contentHostingService = null;

    public void setContentHostingService(ContentHostingService service) {
	contentHostingService = service;
    }

    EvalEvaluationService evaluationService;

    public void setEvaluationService(EvalEvaluationService evaluationService) {
	this.evaluationService = evaluationService;
    }

    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
	this.entityManager = entityManager;
    }

    @Override
    public void execute(JobExecutionContext context)
	    throws JobExecutionException {

	// Get folder where reports will be saved
	evalsysReportsFolder =
		"/group/" + ServerConfigurationService.getString(REPORTS_SITE)
			+ "/";
	String reportPdfId = null;
	ResourcePropertiesEdit resourceProperties = null;

	EvalEvaluation eval = null;
	Map<Long, List<EvalGroup>> evalGroups;
	List<EvalGroup> evalGs;
	Long[] evalIds;
	String[] groupIds;
	ContentResourceEdit resourceEdit = null;
	ByteArrayOutputStream byteOutputStream;
	String departmentFolderName = null;
	String departmentFolderId = null;
	ContentCollection departmentFolderCollection = null;

	String progFolderName = null;
	String progFolderId = null;
	ContentCollection progFolderCollection = null;
	String fileTitle = null;

	String lang = null;

	int fileCount = 0;

	loginToSakai();

	try {

	    // Check if property file exists and retrieve it
	    Reference reference =
		    entityManager
			    .newReference(ConfigurationService.CONFIG_FOLDER
				    + ConfigurationService.EVALSYS_TERMS_FILE);
	    ContentResource resource =
		    contentHostingService.getResource(reference.getId());
	    bundle = getResouceBundle(resource);
	    // TODO: get a list from the bundle
	    termEid = bundle.getString(BUNDLE_KEY);

	    selectedEval = evaluationService.getEvaluationsByTermId(termEid);
	    evalIds = new Long[selectedEval.size()];
	    for (int i = 0; i < selectedEval.size(); i++) {
		eval = selectedEval.get(i);
		evalIds = new Long[1];
		evalIds[0] = new Long(eval.getId());

		// Retrieve group
		evalGroups =
			evaluationService.getEvalGroupsForEval(evalIds, true,
				null);
		evalGs = evalGroups.get(eval.getId());
		groupIds = new String[1];
		for (int j = 0; j < evalGs.size(); j++) {
		    groupIds[0] = ((EvalGroup) evalGs.get(j)).evalGroupId;

		    // Get the language of the section
		    lang = getLang(groupIds[0]);

		    //Create PDF
		    byteOutputStream = new ByteArrayOutputStream();
		    evaluationService.exportReport(eval,   groupIds[0], byteOutputStream,  EvalEvaluationService.PDF_RESULTS_REPORT);
//		    byteOutputStream =
//			    (ByteArrayOutputStream) buildPDFReport(eval,
//				    ((EvalGroup) evalGs.get(j)), lang);

		    // Get the department and create folder
		    departmentFolderName = getDepartment(groupIds[0]);
		    departmentFolderName = removeAccents(departmentFolderName);
		    departmentFolderId =
			    evalsysReportsFolder + DEPARTMENT_FOLDER_NAME + "/"
				    + departmentFolderName + "/";
		    departmentFolderCollection =
			    createOrGetContentCollection(departmentFolderId,
				    departmentFolderName);

		    // Get le programme and create folder
		    progFolderName = getProgramme(groupIds[0]);
		    progFolderId =
			    evalsysReportsFolder + PROG_FORLDER_NAME + "/"
				    + progFolderName + "/";
		    progFolderCollection =
			    createOrGetContentCollection(progFolderId,
				    progFolderName);

		    fileTitle = ((EvalGroup) evalGs.get(j)).title;

		    // add name to file
		    resourceProperties =
			    contentHostingService.newResourceProperties();
		    resourceProperties.addProperty(
			    ResourceProperties.PROP_DISPLAY_NAME, fileTitle
				    + ".pdf");

		    // Save pdf to department folder
		    reportPdfId =
			    departmentFolderCollection.getId() + fileTitle
				    + ".pdf";

		    // Check if file already exists
		    if (resourceExists(reportPdfId)) {
			fileCount += 1;
			 resourceProperties.addProperty(
				    ResourceProperties.PROP_DISPLAY_NAME, fileTitle
				    + "_" + fileCount + ".pdf");
			reportPdfId =
				departmentFolderCollection.getId() + fileTitle
					+ "_" + fileCount + ".pdf";
		    }
		    resourceEdit =
			    (ContentResourceEdit) contentHostingService
				    .addResource(
					    reportPdfId,
					    "application/pdf",
					    new ByteArrayInputStream(
						    byteOutputStream
							    .toByteArray()),
					    resourceProperties, 0);
		    // contentHostingService.commitResource(resourceEdit);

		    // Save pdf to programme folder
		    reportPdfId =
			    progFolderCollection.getId() + fileTitle + ".pdf";

		    // Check if file already exists
		    if (resourceExists(reportPdfId)) {
			reportPdfId =
				progFolderCollection.getId() + fileTitle + "_"
					+ fileCount + ".pdf";
		    }
		    contentHostingService.addResource(reportPdfId,
			    "application/pdf", new ByteArrayInputStream(
				    byteOutputStream.toByteArray()),
			    resourceProperties, 0);

		}

	    }

	} catch (PermissionException e) {
	    e.printStackTrace();
	} catch (IdUnusedException e) {
	    e.printStackTrace();
	} catch (TypeException e) {
	    e.printStackTrace();
	} catch (IdUsedException e) {
	    e.printStackTrace();
	} catch (IdInvalidException e) {
	    e.printStackTrace();
	} catch (InconsistentException e) {
	    e.printStackTrace();
	} catch (OverQuotaException e) {
	    e.printStackTrace();
	} catch (ServerOverloadException e) {
	    e.printStackTrace();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	logoutFromSakai();
    }

    private boolean resourceExists(String resourceId) {
	try {
	    contentHostingService.getResource(resourceId);
	    return true;
	} catch (Exception e) {
	    return false;
	}
    }

    private String removeAccents(String name) {
    	if (name == null)
    		return null;
	String cleanName = "";
	String chars = "àâäéèêëîïôöùûüç";
	String replace = "aaaeeeeiioouuuc";
	int position = -2;

	for (char letter : name.toCharArray()) {
	    position = chars.indexOf(letter);
	    if (position > -1)
		cleanName = cleanName.concat(replace.charAt(position) + "");
	    else
		cleanName = cleanName.concat(letter + "");
	}

	return cleanName;
    }

    // TODO: Une fois que les plans de cours seront section aware il faudra
    // modifier
    // ce code pour gérer toute la liste de provider ids
    /**
     * Get the department from the providerId associated to the Realm
     * 
     * @param realmId
     * @return
     */
    private String getDepartment(String realmId) {
	String department = null;
	String category = null;
	Set<String> providerIds;
	Section section = null;

	providerIds = authzGroupService.getProviderIds(realmId);
	for (String providerId : providerIds) {
	    section = cmService.getSection(providerId);
	    category = section.getCategory();
	    department = cmService.getSectionCategoryDescription(category);
	}
	return department;

    }

    /**
     * Get the language from the providerId associated to the Realm
     * 
     * @param realmId
     * @return
     */
    private String getLang(String realmId) {
	String lang = null;
	Set<String> providerIds;
	Section section = null;

	providerIds = authzGroupService.getProviderIds(realmId);
	for (String providerId : providerIds) {
	    section = cmService.getSection(providerId);
	    lang = section.getLang();
	}
	return lang;

    }

    /**
     * Get program from course management associated to providerId in Realm
     * 
     * @param realmId
     * @return
     */
    private String getProgramme(String realmId) {
	String programme = null;
	Set<String> providerIds;
	CourseOffering courseOff = null;
	Section section = null;
	AcademicCareer acadCareer = null;

	providerIds = authzGroupService.getProviderIds(realmId);
	for (String providerId : providerIds) {
	    section = cmService.getSection(providerId);
	    courseOff =
		    cmService.getCourseOffering(section.getCourseOfferingEid());
	    acadCareer =
		    cmService.getAcademicCareer(courseOff.getAcademicCareer());
	    programme = acadCareer.getDescription_fr_ca();
	}
	return programme;

    }

    private ContentCollection createOrGetContentCollection(
	    String departmentFolderId, String departmentFolderName)
	    throws Exception {
	ContentCollection departmentFolderCollection = null;
	ResourcePropertiesEdit resourceProperties = null;

	try {
	    departmentFolderCollection =
		    contentHostingService.getCollection(departmentFolderId);
	} catch (IdUnusedException e) {
	    departmentFolderCollection =
		    contentHostingService.addCollection(departmentFolderId);
	    resourceProperties =
		    (ResourcePropertiesEdit) departmentFolderCollection
			    .getProperties();
	    resourceProperties.addProperty(
		    ResourceProperties.PROP_DISPLAY_NAME, departmentFolderName);

	    contentHostingService
		    .commitCollection((ContentCollectionEdit) departmentFolderCollection);
	} catch (TypeException e) {
	    e.printStackTrace();
	} catch (PermissionException e) {
	    e.printStackTrace();
	}

	return departmentFolderCollection;
    }

    /**
     * Logs in the sakai environment
     */
    protected void loginToSakai() {
	super.loginToSakai("CreateEvalSysPdfJob");
    }

 
}
