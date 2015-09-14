package org.sakaiquebec.opensyllabus.admin.impl.extracts;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represente les donnees provenant de l'extract prof_cours.dat<br/>
 * <br/>
 */
@AllArgsConstructor
@Data
public class ProfCoursMapEntry {

	private static Log log = LogFactory.getLog(ProfCoursMapEntry.class);

    private String emplId;
    private String catalogNbr;
    private String strm;
    private String sessionCode;
    private String classSection;
    private String acadOrg;
    private String role;
    private String strmId;
}
