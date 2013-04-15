package org.sakaiquebec.opensyllabus.shared.util;

import org.sakaiproject.citation.api.ConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;

/*
* Class that gather utils functions to format citations urls
*/
public class CitationsUtils {
	private static ConfigurationService configService = 
			(ConfigurationService) ComponentManager.get(ConfigurationService.class);
	
	//Change citation url from old to new format (return parameter)
	public static String formatCitationUrl(String oldCitationUrl) {
		
		if (oldCitationUrl.startsWith("http://taos.hec.ca")) {
			String query = oldCitationUrl.substring(oldCitationUrl.lastIndexOf('=')+1);
			
			return configService.getSiteConfigLibraryUrlResolverAddress() + 
					query.replaceAll(" ", " AND ");
		}
		else 
			return oldCitationUrl;
	}
}
