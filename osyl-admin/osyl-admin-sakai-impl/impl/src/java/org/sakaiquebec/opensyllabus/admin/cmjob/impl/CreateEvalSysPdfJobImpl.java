package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;

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
import org.sakaiproject.evaluation.logic.EvalAuthoringService;
import org.sakaiproject.evaluation.logic.EvalCommonLogic;
import org.sakaiproject.evaluation.logic.EvalDeliveryService;
import org.sakaiproject.evaluation.logic.EvalEvaluationService;
import org.sakaiproject.evaluation.logic.EvalSettings;
import org.sakaiproject.evaluation.logic.externals.ExternalHierarchyLogic;
import org.sakaiproject.evaluation.logic.model.EvalGroup;
import org.sakaiproject.evaluation.logic.model.EvalUser;
import org.sakaiproject.evaluation.model.EvalAnswer;
import org.sakaiproject.evaluation.model.EvalEvaluation;
import org.sakaiproject.evaluation.model.EvalItem;
import org.sakaiproject.evaluation.model.EvalTemplateItem;
import org.sakaiproject.evaluation.tool.reporting.EvalPDFReportBuilder;
import org.sakaiproject.evaluation.tool.utils.RenderingUtils;
import org.sakaiproject.evaluation.tool.utils.RenderingUtils.AnswersMean;
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

    private int displayNumber = 0;

    private PropertyResourceBundle bundle = null;

    private List<EvalEvaluation> selectedEval = null;

    String evalsysReportsFolder = null;

    private static Log log = LogFactory.getLog(CreateEvalSysPdfJobImpl.class);

    private EvalCommonLogic commonLogic;

    public void setCommonLogic(EvalCommonLogic commonLogic) {
	this.commonLogic = commonLogic;
    }

    private EvalSettings settings;

    public void setSettings(EvalSettings settings) {
	this.settings = settings;
    }

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

    private ExternalHierarchyLogic hierarchyLogic;

    public void setHierarchyLogic(ExternalHierarchyLogic logic) {
	this.hierarchyLogic = logic;
    }

    private EvalAuthoringService authoringService;

    public void setAuthoringService(EvalAuthoringService authoringService) {
	this.authoringService = authoringService;
    }

    private EvalDeliveryService deliveryService;

    public void setDeliveryService(EvalDeliveryService deliveryService) {
	this.deliveryService = deliveryService;
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

	String lang = null;

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

		    //Get the language of the section
		    lang = getLang(groupIds[0]);

		    byteOutputStream =
			    (ByteArrayOutputStream) buildPDFReport(eval,
				    ((EvalGroup) evalGs.get(j)), lang);

		    // Get the department and create folder
		    departmentFolderName = getDepartment(groupIds[0]);
		    departmentFolderName = removeAccents(departmentFolderName);
		    departmentFolderId = evalsysReportsFolder + DEPARTMENT_FOLDER_NAME + "/"+departmentFolderName + "/";
		    departmentFolderCollection =
			    createOrGetContentCollection(departmentFolderId,
				    departmentFolderName);

		    // Get le programme and create folder
		    progFolderName = getProgramme(groupIds[0]);
		    progFolderId = evalsysReportsFolder + PROG_FORLDER_NAME +"/"+ progFolderName + "/";
		    progFolderCollection =
			    createOrGetContentCollection(progFolderId,
				    progFolderName);

		    // add name to file
		    resourceProperties =
			    contentHostingService.newResourceProperties();
		    resourceProperties.addProperty(
			    ResourceProperties.PROP_DISPLAY_NAME,
			    ((EvalGroup) evalGs.get(j)).title
			    + "_" + eval.getTitle()  + ".pdf");

		    // Save pdf to department folder
		    reportPdfId =
			    departmentFolderCollection.getId()
				    + ((EvalGroup) evalGs.get(j)).title
				    + "_" + eval.getTitle()  + ".pdf";

		    resourceEdit = (ContentResourceEdit) contentHostingService.addResource(reportPdfId,
			    "application/pdf", new ByteArrayInputStream(
				    byteOutputStream.toByteArray()),
			    resourceProperties, 0);
		   // contentHostingService.commitResource(resourceEdit);

		    // Save pdf to programme folder
		    reportPdfId =
			    progFolderCollection.getId()
				    + ((EvalGroup) evalGs.get(j)).title
				    + "_" + eval.getTitle() + ".pdf"  ;

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

     private String removeAccents (String name){
	 String cleanName = "";
	 String chars= "àâäéèêëîïôöùûüç";
         String replace= "aaaeeeeiioouuuc";
         int position = -2;

         for (char letter: name.toCharArray()){
            position = chars.indexOf(letter);
            if (position > -1)
        	cleanName = cleanName.concat(replace.charAt(position)+ "");
            else
        	cleanName = cleanName.concat(letter+"");
         }

	 return cleanName;
     }

    // TODO: Une fois que les plans de cours seront section aware il faudra
    // modifier
    // ce code pour gérer toute la liste de provider ids
    /**
     * Get the department from the providerId associated to the Realm
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

    private ContentCollection createOrGetContentCollection(String departmentFolderId,
	    String departmentFolderName) throws Exception {
	ContentCollection departmentFolderCollection = null;
	ResourcePropertiesEdit resourceProperties = null;

	try {
	    departmentFolderCollection =
		    contentHostingService.getCollection(departmentFolderId);
	} catch (IdUnusedException e) {
	    departmentFolderCollection =
		    contentHostingService.addCollection(departmentFolderId);
	    resourceProperties =
		    (ResourcePropertiesEdit) departmentFolderCollection.getProperties();
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


    private OutputStream buildPDFReport(EvalEvaluation evaluation,
	    EvalGroup evalGroup, String lang) {

	ResourceLoader rb = null;
	OutputStream outputStream = new ByteArrayOutputStream();
	EvalPDFReportBuilder evalPDFReportBuilder =
		new EvalPDFReportBuilder(outputStream);
	Boolean instructorViewAllResults =
		(boolean) evaluation.getInstructorViewAllResults();
	String currentUserId = commonLogic.getCurrentUserId();
	String evalOwner = evaluation.getOwner();
	Boolean useBannerImage =
		(Boolean) settings.get(EvalSettings.ENABLE_PDF_REPORT_BANNER);
	byte[] bannerImageBytes = null;

	String[] groupIds = { evalGroup.evalGroupId };

	// Get the message files
	rb =
		new ResourceLoader(
			"org.sakaiquebec.opensyllabus.admin.cmjob.impl.bundle.evaluation_messages");
	if ("FR_CA".equalsIgnoreCase(lang))
	    rb =
		    new ResourceLoader(
			    "org.sakaiquebec.opensyllabus.admin.cmjob.impl.bundle.evaluation_messages_fr_CA");
	if ("ES".equalsIgnoreCase(lang))
	    rb =
		    new ResourceLoader(
			    "org.sakaiquebec.opensyllabus.admin.cmjob.impl.bundle.evaluation_messages_es_ES");

	if (useBannerImage != null && useBannerImage == true) {
	    String bannerImageLocation =
		    (String) settings
			    .get(EvalSettings.PDF_BANNER_IMAGE_LOCATION);
	    if (bannerImageLocation != null) {
		bannerImageBytes =
			commonLogic.getFileContent(bannerImageLocation);
	    }
	}

	DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

	int responsesCount =
		evaluationService.countResponses(null,
			new Long[] { evaluation.getId() }, groupIds, null);
	int enrollmentsCount =
		evaluationService.countParticipantsForEval(evaluation.getId(),
			groupIds);

	// TODO this is so hard to read it makes me cry, it should not be
	// written as a giant single line like this -AZ
	evalPDFReportBuilder.addTitlePage(
		evaluation.getTitle(),
		evalGroup.title,
		rb.getString("reporting.pdf.resultstitle"),
		rb.getString("reporting.pdf.startdatetime")
			+ df.format(evaluation.getStartDate()),
		rb.getString("reporting.pdf.enddatetime")
			+ df.format(evaluation.getDueDate()),
		rb.getString("reporting.pdf.replyrate")
			+ EvalUtils.makeResponseRateStringFromCounts(
				responsesCount, enrollmentsCount),
		bannerImageBytes,
		rb.getString("reporting.pdf.defaultsystemname"));

	// set title and instructions
	evalPDFReportBuilder
		.addIntroduction(evaluation.getTitle(), commonLogic
			.makePlainTextFromHTML(evaluation.getInstructions()));

	// Reset question numbering
	displayNumber = 0;

	// 1 Make TIDL
	TemplateItemDataList tidl =
		new TemplateItemDataList(evaluation.getId(), groupIds,
			authoringService, deliveryService, hierarchyLogic);

	// Loop through the major group types: Course Questions, Instructor
	// Questions, etc.
	int renderedItemCount = 0;
	EvalUser user = null;

	for (TemplateItemGroup tig : tidl.getTemplateItemGroups()) {

	    // Section for questions related to course
	    if (EvalConstants.ITEM_CATEGORY_COURSE.equals(tig.associateType)) {
		evalPDFReportBuilder.addSectionHeader(rb
			.getString("viewreport.itemlist.course"));
	    }

	    // Section for questions related to instructor
	    else if (EvalConstants.ITEM_CATEGORY_INSTRUCTOR
		    .equals(tig.associateType)) {
		user = commonLogic.getEvalUserById(tig.associateId);
		String instructorMsg =
			rb.getString("reporting.spreadsheet.instructor")
				+ user.displayName;
		evalPDFReportBuilder.addSectionHeader(instructorMsg);
	    } else if (EvalConstants.ITEM_CATEGORY_ASSISTANT
		    .equals(tig.associateType)) {
		user = commonLogic.getEvalUserById(tig.associateId);
		String assistantMsg =
			rb.getString("reporting.spreadsheet.ta")
				+ user.displayName;
		evalPDFReportBuilder.addSectionHeader(assistantMsg);
	    } else {
		evalPDFReportBuilder.addSectionHeader(rb
			.getString("unknown.caps"));
	    }

	    for (HierarchyNodeGroup hng : tig.hierarchyNodeGroups) {
		// Render the Node title if it's enabled in the admin
		// settings.
		if (hng.node != null) {
		    // Showing the section title is system configurable via
		    // the administrate view
		    Boolean showHierSectionTitle =
			    (Boolean) settings
				    .get(EvalSettings.DISPLAY_HIERARCHY_HEADERS);
		    if (showHierSectionTitle) {
			evalPDFReportBuilder.addSectionHeader(hng.node.title);
		    }
		}

		List<DataTemplateItem> dtis = hng.getDataTemplateItems(true); // include
									      // block
									      // children
		for (int i = 0; i < dtis.size(); i++) {
		    DataTemplateItem dti = dtis.get(i);

		    renderDataTemplateItem(evalPDFReportBuilder, dti, rb);
		    renderedItemCount++;
		}
	    }
	}

	evalPDFReportBuilder.close();

	return outputStream;
    }

    /**
     * Renders a single question given the DataTemplateItem.
     *
     * @param evalPDFReportBuilder
     * @param dti the data template item
     */
    private void renderDataTemplateItem(
	    EvalPDFReportBuilder evalPDFReportBuilder, DataTemplateItem dti, ResourceLoader rb) {
	EvalTemplateItem templateItem = dti.templateItem;
	EvalItem item = templateItem.getItem();
	String questionText =
		commonLogic.makePlainTextFromHTML(item.getItemText());

	List<EvalAnswer> itemAnswers = dti.getAnswers();

	String templateItemType =
		TemplateItemUtils.getTemplateItemType(templateItem);

	if (EvalConstants.ITEM_TYPE_HEADER.equals(templateItemType)) {
	    evalPDFReportBuilder.addSectionHeader(questionText);
	} else if (EvalConstants.ITEM_TYPE_BLOCK_PARENT
		.equals(templateItemType)) {
	    evalPDFReportBuilder.addSectionHeader(questionText);
	} else if (EvalConstants.ITEM_TYPE_TEXT.equals(templateItemType)) {
	    displayNumber++;
	    List<String> essays = new ArrayList<String>();
	    for (EvalAnswer answer : itemAnswers) {
		essays.add(answer.getText());
	    }
	    evalPDFReportBuilder.addTextItemsList(displayNumber + ". "
		    + questionText, essays);
	} else if (EvalConstants.ITEM_TYPE_MULTIPLEANSWER
		.equals(templateItemType)
		|| EvalConstants.ITEM_TYPE_MULTIPLECHOICE
			.equals(templateItemType)
		|| EvalConstants.ITEM_TYPE_SCALED.equals(templateItemType)
		|| EvalConstants.ITEM_TYPE_BLOCK_CHILD.equals(templateItemType)) {
	    // always showing percentages for now
	    boolean showPercentages = true;
	    // boolean showPercentages = false;
	    // if
	    // (EvalConstants.ITEM_TYPE_MULTIPLEANSWER.equals(templateItemType))
	    // {
	    // showPercentages = true;
	    // }

	    int responseNo = itemAnswers.size();
	    displayNumber++;
	    String[] itemScaleOptions = item.getScale().getOptions();
	    int[] responseArray =
		    TemplateItemDataList.getAnswerChoicesCounts(
			    templateItemType, itemScaleOptions.length,
			    itemAnswers);

	    String[] optionLabels;
	    if (templateItem.getUsesNA()) {
		optionLabels = new String[itemScaleOptions.length + 1];
		for (int m = 0; m < itemScaleOptions.length; m++) {
		    optionLabels[m] = itemScaleOptions[m];
		}
		optionLabels[optionLabels.length - 1] =
			rb.getString("reporting.notapplicable.longlabel");
	    } else {
		optionLabels = itemScaleOptions;
	    }

	    // http://www.caret.cam.ac.uk/jira/browse/CTL-1504
	    AnswersMean answersMean =
		    RenderingUtils.calculateMean(responseArray);
	    String answersAndMean =
		    answersMean.getAnswersCount() + " "
			    + rb.getString("viewreport.answers.mean")
			    + answersMean.getMeanText();

	    evalPDFReportBuilder.addLikertResponse(displayNumber + ". "
		    + questionText, optionLabels, responseArray, responseNo,
		    showPercentages, answersAndMean);

	    // handle comments
	    if (dti.usesComments()) {
		List<String> comments = dti.getComments();
		evalPDFReportBuilder.addCommentList(
			rb.getString("viewreport.comments.header"), comments,
			rb.getString("viewreport.no.comments"));
	    }

	} else {
	    log.warn("Trying to add unknown type to PDF: " + templateItemType);
	}
    }
}
