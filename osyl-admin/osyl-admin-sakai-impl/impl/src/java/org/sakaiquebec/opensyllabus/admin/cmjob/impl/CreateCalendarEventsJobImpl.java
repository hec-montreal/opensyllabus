/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2013 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import lombok.Data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.time.cover.TimeService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import ca.hec.commons.utils.FormatUtils;

/**
 * @author curtis.van-osch@hec.ca
 * @version $Id: $
 */
public class CreateCalendarEventsJobImpl extends OsylAbstractQuartzJobImpl implements Job {

    private static Log log = LogFactory.getLog(CreateCalendarEventsJobImpl.class);

    private final String EVENT_TYPE_CLASS_SESSION = "Class session";
    private final String EVENT_TYPE_CANCELLATION = "Cancellation";
    private final String EVENT_TYPE_EXAM = "Exam";
    private final String EVENT_TYPE_QUIZ = "Quiz";

    private CalendarService calendarService;

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public CalendarService getCalendarService() {
		return calendarService;
	}

	public void setCalendarService(CalendarService calendarService) {
		this.calendarService = calendarService;
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
    	log.debug("starting CreateCalendarEventsJob");
    	int addcount = 0, updatecount = 0, deletecount = 0;

    	loginToSakai("CreateCalendarEventsJob");

    	String select_from = "select CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, CLASS_EXAM_TYPE, SEQ, DATE_HEURE_DEBUT, "
				+ "DATE_HEURE_FIN, DESCR_FACILITY, STATE, EVENT_ID from HEC_EVENT ";
    	try {

    		log.debug("get events to add");
    		List<HecEvent> eventsAdd = jdbcTemplate.query(
    				select_from + "where STATE = 'A' or (EVENT_ID is null and STATE <> 'D')",
    				new HecEventRowMapper());

    		log.debug("loop and add " + eventsAdd.size() + " events");
    		for (HecEvent event : eventsAdd) {
				String siteId = getSiteId(
						event.getCatalogNbr(),
						event.getSessionId(),
						event.getSection());

    			String eventId = createCalendarEvent(
    					siteId,
    					event.getStartTime(),
    					event.getEndTime(),
    					getEventTitle(siteId, event.getExamType(), event.getSequenceNumber()),
    					getType(event.getExamType()),
    					event.getLocation());

    			clearHecEventState(
    					eventId,
    					event.getCatalogNbr(),
						event.getSessionId(),
						event.getSessionCode(),
						event.getSection(),
						event.getExamType(),
						event.getSequenceNumber());

    			if (eventId != null) {
    				addcount++;
    			}

    		}

    		log.debug("get events to update");
    		List<HecEvent> eventsUpdate = jdbcTemplate.query(
    				select_from + "where (STATE = 'M' or STATE = 'D') ",
    				new HecEventRowMapper());

    		log.debug("loop and update "+ eventsUpdate.size() + " events");
    		for (HecEvent event : eventsUpdate) {

				String siteId = getSiteId(
						event.getCatalogNbr(),
						event.getSessionId(),
						event.getSection());

    			boolean updateSuccess = updateCalendarEvent(
    					siteId,
    					event.getEventId(),
    					event.getState(),
    					event.getStartTime(),
    					event.getEndTime(),
    					event.getLocation());

    			if (event.getState().equals("M")) {
        			clearHecEventState(
        					event.getEventId(),
        					event.getCatalogNbr(),
    						event.getSessionId(),
    						event.getSessionCode(),
    						event.getSection(),
    						event.getExamType(),
    						event.getSequenceNumber());

        			if (updateSuccess)
        				updatecount++;
    			}
    			else if (event.getState().equals("D")) {
    				deleteHecEvent(
    						event.getCatalogNbr(),
    						event.getSessionId(),
    						event.getSessionCode(),
    						event.getSection(),
    						event.getExamType(),
    						event.getSequenceNumber());

    				if (updateSuccess)
    					deletecount++;
    			}
    		}
		} catch (PermissionException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		logoutFromSakai();
    	log.debug("exiting CreateCalendarEventsJob");
    	log.debug("added: " + addcount + " updated: " + updatecount + " deleted: " + deletecount);
    } // execute

	private void clearHecEventState(String event_id, String catalog_nbr, String session_id, String session_code, String section,
			String exam_type, Integer sequence_num) {

		jdbcTemplate.update("update HEC_EVENT set STATE = null, EVENT_ID = ? where CATALOG_NBR = ? and STRM = ? and " +
				"SESSION_CODE = ? and CLASS_SECTION = ? and CLASS_EXAM_TYPE = ? and SEQ = ?",
				new Object[] {event_id, catalog_nbr, session_id, session_code, section, exam_type, sequence_num});
	}

	private void deleteHecEvent(String catalog_nbr, String session_id, String session_code, String section,
			String exam_type, Integer sequence_num) {

		jdbcTemplate.update("delete from HEC_EVENT where CATALOG_NBR = ? and STRM = ? and SESSION_CODE = ? and CLASS_SECTION = ? " +
				"and CLASS_EXAM_TYPE = ? and SEQ = ?",
				new Object[] {catalog_nbr, session_id, session_code, section, exam_type, sequence_num });

	}

	private String createCalendarEvent(String siteId, Date startTime, Date endTime, String title, String type, String location)
    		throws PermissionException {

		Calendar calendar;
		try {
			calendar = getCalendar(siteId);
		} catch (IdUnusedException e) {
			log.info("Error retrieving calendar for site " + siteId);
			return null;
		}

		// add event to calendar
		CalendarEvent event = calendar.addEvent(
				TimeService.newTimeRange(TimeService.newTime(startTime.getTime()), TimeService.newTime(endTime.getTime())),
				title,
				null,
				type,
				location,
				CalendarEvent.EventAccess.SITE,
				null,
				EntityManager.newReferenceList());

		log.info("creating event: " + title +
				" in " + siteId +
				" from " + startTime.toString() +
				" to " + endTime.toString() +
				" in " + location);

		return event.getId();
    }

    private boolean updateCalendarEvent(String siteId, String eventId, String state, Date newStartTime, Date newEndTime, String newLocation)
    		throws PermissionException {

    	Calendar calendar;
    	CalendarEventEdit edit;

		try {
			calendar = getCalendar(siteId);
			edit = calendar.getEditEvent(eventId, CalendarService.EVENT_MODIFY_CALENDAR);
		} catch (Exception e) {
			log.info("Error retrieving event " + eventId + " for site " + siteId);
			e.printStackTrace();
			return false;
		}

		if (state.equals("M")) {
			if (newStartTime != null && newEndTime != null)
				edit.setRange(TimeService.newTimeRange(TimeService.newTime(newStartTime.getTime()), TimeService.newTime(newEndTime.getTime())));
			if (newLocation != null)
				edit.setLocation(newLocation);
		}
		else if (state.equals("D")) {
			edit.setType(EVENT_TYPE_CANCELLATION);
		}

		calendar.commitEvent(edit);
		log.debug("updated ("+state+") event: " + edit.getDisplayName() +
				" in " + siteId +
				" from " + newStartTime.toString() +
				" to " + newEndTime.toString() +
				" in " + newLocation);

		return true;
    }

    private Calendar getCalendar(String siteId) throws IdUnusedException, PermissionException {
       	String calRef = calendarService.calendarReference(siteId, SiteService.MAIN_CONTAINER);
		return calendarService.getCalendar(calRef);
    }

    private String getSiteId(String catalog_nbr, String session_code, String section) {
    	String siteId = FormatUtils.formatCourseId(catalog_nbr);
    	siteId += "." + FormatUtils.getSessionName(session_code);
    	siteId += "." + section;

    	return siteId;
    }

    // TODO Externalize labels
	private String getEventTitle(String siteId, String type, Integer seq_num) {
		String title = "";
		if (type.equals(" "))
			title = "Séance " + seq_num + " du cours " + siteId;
		else if (type.equals("INTR"))
			title = "Examen intra-trimestriel du cours " + siteId;
		else if (type.equals("FIN"))
			title = "Examen final du cours " + siteId;
		else if (type.equals("TEST"))
			title = "Test du cours " + siteId;
		return title;
	}

	private String getType(String exam_type) {
		if (exam_type.equals("INTR") || exam_type.equals("FIN"))
			return this.EVENT_TYPE_EXAM;
		else if (exam_type.equals("TEST"))
			return EVENT_TYPE_QUIZ;
		else return this.EVENT_TYPE_CLASS_SESSION;
	}

	@Data
	private class HecEvent {
		String catalogNbr, sessionId, sessionCode, section, state, examType, location, eventId;
		Integer sequenceNumber;
		Date startTime, endTime;
	}

	private class HecEventRowMapper implements RowMapper {
		@Override
		public HecEvent mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			HecEvent event = new HecEvent();
			event.setCatalogNbr(rs.getString("CATALOG_NBR"));
			event.setSessionId(rs.getString("STRM"));
			event.setSessionCode(rs.getString("SESSION_CODE"));
			event.setSection(rs.getString("CLASS_SECTION"));
			event.setExamType(rs.getString("CLASS_EXAM_TYPE"));
			event.setSequenceNumber(rs.getInt("SEQ"));
			event.setStartTime(rs.getTimestamp("DATE_HEURE_DEBUT"));
			event.setEndTime(rs.getTimestamp("DATE_HEURE_FIN"));
			event.setLocation(rs.getString("DESCR_FACILITY"));
			event.setEventId(rs.getString("EVENT_ID"));
			event.setState(rs.getString("STATE"));

			return event;
		}
	}
}
