package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Mockery;
import org.sakaiproject.content.api.ContentHostingService;

final class SakaiContentHostingServiceMock extends AbstractMock<ContentHostingService>{
	
	protected SakaiContentHostingServiceMock(Mockery mockery) {
		super(mockery, ContentHostingService.class);
	}
	
}
