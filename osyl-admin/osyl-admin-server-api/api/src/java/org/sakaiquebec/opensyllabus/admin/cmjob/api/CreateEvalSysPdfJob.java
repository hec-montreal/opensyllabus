package org.sakaiquebec.opensyllabus.admin.cmjob.api;

public interface CreateEvalSysPdfJob extends OsylAbstractQuartzJob{
    
    public static final String BUNDLE_KEY = "term_id";
    
    public static final String REPORTS_SITE = "evalsys.reports.site";
    
    public static final String SERV_ENS_FOLDER_NAME = "service_enseignement";
    
    public static final String PROG_FORLDER_NAME = "programmes"; 

}
