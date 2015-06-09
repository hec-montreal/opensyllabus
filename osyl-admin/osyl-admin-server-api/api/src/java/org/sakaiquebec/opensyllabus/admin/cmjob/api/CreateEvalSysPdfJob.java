package org.sakaiquebec.opensyllabus.admin.cmjob.api;

public interface CreateEvalSysPdfJob extends OsylAbstractQuartzJob{
    
    public static final String BUNDLE_KEY = "term_id";
    
    public static final String REPORTS_SITE = "evalsys.reports.site";
    
    public static final String DEPARTMENT_FOLDER_NAME = "departement";
    
    public static final String PROG_FORLDER_NAME = "programmes"; 

}
