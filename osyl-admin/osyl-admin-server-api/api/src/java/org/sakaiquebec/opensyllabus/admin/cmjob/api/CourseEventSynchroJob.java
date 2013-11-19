package org.sakaiquebec.opensyllabus.admin.cmjob.api;

/**
 * Job de synchro du fichier d'extract contenant les événements de cours avec la
 * table HEC_EVENT
 * @author 11183065
 *
 */
public interface CourseEventSynchroJob {

	/**
	 * execute la job
	 * @param filePath chemin du fichier d'extract contenant les événements de cours
	 */
	void execute(String filePath);

}
