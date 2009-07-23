package org.sakaiquebec.opensyllabus.common.impl;

import org.jmock.Mockery;
import org.sakaiproject.authz.api.SecurityService;

final class SakaiSecurityServiceMock extends AbstractMock<SecurityService>{
	
	protected SakaiSecurityServiceMock(Mockery mockery) {
		super(mockery, SecurityService.class);
	}
	
}
