package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represente la liste des professeurs et des coordonnateurs pour un cours, provenant de l'extract prof_cours.dat<br/>
 * <br/>
 */
@NoArgsConstructor
@Data
public class ProfCoursMapEntry {

	Set<String> instructors = new HashSet<String>();
	Set<String> coordinators = new HashSet<String>();
}
