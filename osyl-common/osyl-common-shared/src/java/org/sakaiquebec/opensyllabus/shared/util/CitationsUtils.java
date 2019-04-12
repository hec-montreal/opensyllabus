package org.sakaiquebec.opensyllabus.shared.util;

import org.sakaiproject.citation.api.ConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;

/*
* Class that gather utils functions to format citations urls
*/
public class CitationsUtils {
	private static ConfigurationService configService = 
			(ConfigurationService) ComponentManager.get(ConfigurationService.class);

	// ne charger qu'une seule fois la liste
	private static final String[] openUrlAddressesToReplace = 
			ServerConfigurationService.getStrings("osyl.replace.openurl.address");
	
	//Change citation url from old to new format (return parameter)
	public static String formatCitationUrl(String oldCitationUrl) {
		
		// Depuis 19.0 la méthode getSiteConfigLibraryUrlResolverAddress n'existe plus dans configService
		// On enlève le code qui traite les citations qui sont sur taos.hec.ca

		/* if (oldCitationUrl.startsWith("http://taos.hec.ca")) {
			String query = oldCitationUrl.substring(oldCitationUrl.lastIndexOf('=')+1);
			
			return configService.getSiteConfigLibraryUrlResolverAddress() + 
					query.replaceAll(" ", " AND ");
		} */

		// ZCII-2002 : permettre de spécifier une liste d'url dans sakai.properties
		//             qui devraient être remplacé par celui qui est dans la config
		//             lorsqu'on charge un plan de cours
		if (null != openUrlAddressesToReplace) {
			for (String url : openUrlAddressesToReplace) {
				if (oldCitationUrl.startsWith(url)) {
					String query = oldCitationUrl.substring(oldCitationUrl.indexOf('?'));		
					return configService.getSiteConfigOpenUrlResolverAddress() + query;					
				}
			}
		}
		
		return oldCitationUrl;
	}
}
