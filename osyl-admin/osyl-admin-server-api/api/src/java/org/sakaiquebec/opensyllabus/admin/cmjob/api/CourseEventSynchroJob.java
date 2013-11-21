package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import java.io.IOException;

/**
 * Job de synchro du fichier d'extract contenant les événements de cours avec la
 * table HEC_EVENT
 *
 * @author 11183065
 *
 */
public interface CourseEventSynchroJob {

	/**
	 * execute la job
	 *
	 * @param filePath
	 *            chemin du fichier d'extract contenant les événements de cours
	 * @throws IOException
	 *             En cas de problème lors de la lecture du fichier
	 * @throws InvalidStateException
	 * 			Dans la cas ou la colonne state d'une ligne de la table hec_event n'est pas nulle
	 */
	void execute(String filePath) throws IOException, InvalidStateException;

}
