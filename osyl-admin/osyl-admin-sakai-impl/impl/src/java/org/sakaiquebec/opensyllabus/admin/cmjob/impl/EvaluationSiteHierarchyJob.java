package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.hierarchy.HierarchyService;
import org.sakaiproject.hierarchy.model.HierarchyNode;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.evaluation.logic.externals.ExternalHierarchyLogic;
import org.sakaiproject.evaluation.logic.model.EvalHierarchyNode;

/**
 *
 *
 * @author Curtis van Osch (curtis.van-osch@hec.ca)
 *
 */
public class EvaluationSiteHierarchyJob implements Job{

	private static Log log = LogFactory
    	    .getLog(EvaluationSiteHierarchyJob.class);

    @Getter @Setter
	private HierarchyService hierarchyService;
    @Getter @Setter
    private ExternalHierarchyLogic evalHierarchyLogic;
    @Getter @Setter
	private CourseManagementService courseManagementService;
    @Getter @Setter
	private SiteService siteService;

	private static boolean semaphore = false;

	public void init() {

	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		//this will stop the job if there is already another instance running
		if(semaphore){
			log.warn("Stopping job since this job is already running");
			return;
		}
		semaphore = true;

		try{
			log.info("EvaluationSiteHierarchyJob started");

			int numberOfSessionsToProcess = ServerConfigurationService.getInt("evaluation.hierarchy.numberOfSessionsToProcess", 3);

			List<AcademicSession> sessions = courseManagementService.getAcademicSessions();
			Collections.reverse(sessions);
			String previousSessionTitle = null;
			int processedSessionCount = 0;

			for (AcademicSession session : sessions) {
				if (processedSessionCount == numberOfSessionsToProcess) {
					break;
				}

				if (previousSessionTitle != null &&
						previousSessionTitle.equals(session.getTitle())) {
					// we've already handled this session title
					continue;
				}

			    Map<String, String> siteProps = new HashMap<String, String>();
			    siteProps.put("term", session.getTitle());

			    //get one page of results for test
			    List<Site> sites = siteService.getSites(SelectionType.NON_USER, "course", null, siteProps,
			    		SortType.CREATED_ON_DESC, new PagingPosition(1, 10000));

			    Map<String, Set<String>> nodeMap = createNodeMapForSites(sites);

				createHierarchyNodesAndAssignGroups(nodeMap);

			    previousSessionTitle = session.getTitle();
			    processedSessionCount++;
			}

		}
		finally {
			log.info("EvaluationSiteHierarchyJob end");
			semaphore = false;
		}
	}

	/*
	 *
	 */
	private Map<String, Set<String>> createNodeMapForSites(List<Site> sites) {
		Map<String, Set<String>> nodeMap = new HashMap<String, Set<String>>();

		if (sites == null || sites.isEmpty())  {
			return nodeMap;
		}

		for (Site site : sites) {
			String providerGroup = site.getProviderGroupId();
			// skip if provider group id is null or ends in 00 (means it's a shareable site)
			if (providerGroup == null || providerGroup.substring(providerGroup.length()-2).equals("00")) {
				continue;
			}

			try {
				// section specifies department (in category field), evaluation template, and language
				Section section = courseManagementService.getSection(providerGroup);
				// course offering specifies program (in academic career)
				CourseOffering courseOffering = courseManagementService.getCourseOffering(section.getCourseOfferingEid());

				//session
				String nodeKey = courseOffering.getAcademicSession().getTitle();
				//language
				nodeKey += "|" + section.getLang();
				// program
				nodeKey += "|" + courseOffering.getAcademicCareer();
				//format
//				nodeKey += "|" + section.getTypeEvaluation();

				if (nodeMap.containsKey(nodeKey)) {
					Set<String> siteRefs = nodeMap.get(nodeKey);
					siteRefs.add(site.getReference());
				}
				else {
					Set<String> siteRefs = new HashSet<String>();
					siteRefs.add(site.getReference());
					nodeMap.put(nodeKey, siteRefs);
				}
			}
			catch (IdNotFoundException e) {
				log.debug("Section or CourseOffering not found for "+site.getId());
			}
		}
		return nodeMap;
	}

	/*
	 * Create hierarchy nodes (in hierarchy tables) if they don't exist and assign group ids to the appropriate nodes.
	 *
	 * Note: Assigned group ids will be overridden!
	 */
	private void createHierarchyNodesAndAssignGroups(Map<String, Set<String>> nodeMap) {
		if (nodeMap == null || nodeMap.isEmpty()) {
			return;
		}
		for(String nodeKey : nodeMap.keySet()) {
			String[] splitNodeKey = nodeKey.split("\\|");

			EvalHierarchyNode parentNode = evalHierarchyLogic.getRootLevelNode();
			for (int i = 0; i < splitNodeKey.length; i++) {
				boolean nodeExists = false;
				Set<EvalHierarchyNode> children = evalHierarchyLogic.getChildNodes(parentNode.id, true);
				for (EvalHierarchyNode child : children) {
					if (child.title.equals(splitNodeKey[i])) {
						nodeExists = true;
						parentNode = child;
						break;
					}
				}
				if (!nodeExists) {
					EvalHierarchyNode newNode = evalHierarchyLogic.addNode(parentNode.id);
					evalHierarchyLogic.updateNodeData(newNode.id, splitNodeKey[i], splitNodeKey[i]);
					parentNode = newNode;
				}
			}
			evalHierarchyLogic.setEvalGroupsForNode(parentNode.id, nodeMap.get(nodeKey));
		}
	}
}
