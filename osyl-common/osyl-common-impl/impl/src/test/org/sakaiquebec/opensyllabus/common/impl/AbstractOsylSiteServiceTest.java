package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiquebec.opensyllabus.common.api.OsylConfigService;
import org.sakaiquebec.opensyllabus.common.api.OsylSecurityService;
import org.sakaiquebec.opensyllabus.common.api.OsylSiteService;
import org.sakaiquebec.opensyllabus.common.dao.COConfigDao;
import org.sakaiquebec.opensyllabus.common.dao.CORelationDao;
import org.sakaiquebec.opensyllabus.common.dao.ResourceDao;

public abstract class AbstractOsylSiteServiceTest extends AbstractServiceTest {

    private OsylSiteService service;

    private SiteService siteService;

    public final OsylSiteService getService() {
	return this.service;
    }

    @Override
    protected void onServiceSetUp() throws Exception {
	this.siteService = getMockery().newSiteService();
	OsylSiteServiceImpl serviceImpl = new OsylSiteServiceImpl();

	serviceImpl.setConfigDao(getMockery().mock(COConfigDao.class));
	serviceImpl.setCoRelationDao(getMockery().mock(CORelationDao.class));
	serviceImpl.setResourceDao(getMockery().mock(ResourceDao.class));

	serviceImpl.setContentHostingService(getMockery()
		.newContentHostingService());
	serviceImpl.setSiteService(this.siteService);
	serviceImpl.setToolManager(getMockery().newToolManager());
	serviceImpl.setIdManager(getMockery().newIdManager());

	serviceImpl
		.setConfigService(getMockery().mock(OsylConfigService.class));
	serviceImpl.setSecurityService(getMockery().mock(
		OsylSecurityService.class));
	serviceImpl.setUserDirectoryService(getMockery().mock(
		UserDirectoryService.class));

	final EventTrackingService eventTrackingService =
		getMockery().mock(EventTrackingService.class);
	serviceImpl.setEventTrackingService(eventTrackingService);
	
	final EntityManager entityManager = getMockery().mock(EntityManager.class);
	serviceImpl.setEntityManager(entityManager);
	
	getMockery().checking(new Expectations() {
	    {
		ignoring(eventTrackingService);
		ignoring(entityManager);
	    }
	});
	
	serviceImpl.setSessionManager(getMockery().mock(SessionManager.class));

	serviceImpl.init();
	this.service = serviceImpl;
    }

    protected final SiteService getSiteService() {
	return this.siteService;
    }
}
