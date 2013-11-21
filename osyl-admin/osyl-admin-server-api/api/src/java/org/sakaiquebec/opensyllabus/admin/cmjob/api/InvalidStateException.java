package org.sakaiquebec.opensyllabus.admin.cmjob.api;

/**
 * Exception déclenchée par la job de synchro du fichier d'extract des
 * événements de cours avec la table HEC_EVENT lorsque la colonne state d'une
 * des lignes de la table n'est pas nulle. Cela signifie que soit la job de
 * propagation des événements dans l'outil calendrier n'est pas passée, soit
 * qu'elle a planté.
 *
 * @author 11183065
 *
 */
public class InvalidStateException extends Exception {

	public InvalidStateException(String message) {
		super(message);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1023268187436521671L;

}
