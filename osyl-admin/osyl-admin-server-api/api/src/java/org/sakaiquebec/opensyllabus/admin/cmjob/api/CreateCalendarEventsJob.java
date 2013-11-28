package org.sakaiquebec.opensyllabus.admin.cmjob.api;

import java.io.IOException;

import org.quartz.JobExecutionContext;

/**
 * Job Quartz pour créer les événements dans les calendriers sakai à partir de la table HEC_EVENT
 * @author Curtis van Osch
 *
 */
public interface CreateCalendarEventsJob extends OsylAbstractQuartzJob {

	/**
	 * execute la job
	 */
    void execute(JobExecutionContext arg0);
}