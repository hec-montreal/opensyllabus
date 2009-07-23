package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;

final class SakaiSiteServiceMock extends AbstractMock<SiteService>{
	
	private Site site;
	
	protected SakaiSiteServiceMock(Mockery mockery) {
		super(mockery, SiteService.class);
		this.site = mockery.mock(Site.class);
	}

	@Override
	protected Expectations getExpectations(final SiteService mock) {
		return new Expectations() {
            {
                allowing(equal(site)).
                method("getId"); 
                will(returnValue(OsylMockery.SITE_ID));
            }            
            {
                try {
                    allowing(mock).
                    getSite(OsylMockery.SITE_ID);
                    will(returnValue(site));
                }
                catch (IdUnusedException e) {
                    //this is abnormal and should never happen
                    throwException(e);
                }
            }
            {
                allowing(equal(mock)).
                method("getSite").
                with(any(String.class));
                will(throwException(new IdUnusedException("Mocked")));                    
            }
            
        };
	}
	
}
