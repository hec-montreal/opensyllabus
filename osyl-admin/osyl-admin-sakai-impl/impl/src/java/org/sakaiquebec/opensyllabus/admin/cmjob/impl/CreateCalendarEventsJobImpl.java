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
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import lombok.Data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CreateCalendarEventsJob;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import ca.hec.commons.utils.FormatUtils;

/**
 * @author curtis.van-osch@hec.ca
 * @version $Id: $
 */
public class CreateCalendarEventsJobImpl extends OsylAbstractQuartzJobImpl implements CreateCalendarEventsJob {

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

	public void execute(JobExecutionContext arg0) {
    	log.info("starting CreateCalendarEventsJob");
    	int addcount = 0, updatecount = 0, deletecount = 0;

    	loginToSakai("CreateCalendarEventsJob");

    	String select_from = "select CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, CLASS_EXAM_TYPE, SEQ, DATE_HEURE_DEBUT, "
				+ "DATE_HEURE_FIN, DESCR_FACILITY, STATE, EVENT_ID from HEC_EVENT ";
    	String order_by = " order by CATALOG_NBR, STRM, CLASS_SECTION ";

    	List<HecEvent> eventsAdd = jdbcTemplate.query(
    			select_from + "where (EVENT_ID is null and (STATE is null or STATE <> 'D'))" + order_by,
    			new HecEventRowMapper());

    	// keep track of the last event's site id, so we know if the calendar was found or not
    	String previousSiteId = "";
    	boolean calendarFound = false;

    	log.info("loop and add " + eventsAdd.size() + " events");
    	for (HecEvent event : eventsAdd) {
    		String siteId = getSiteId(
    				event.getCatalogNbr(),
    				event.getSessionId(),
    				event.getSection());

    		String eventId = null;
    		// only attempt event creation if this is a new site or the calendar was found before
    		if (!siteId.equals(previousSiteId) || calendarFound)
    		{
    			try {

    				Calendar calendar = getCalendar(siteId);
    				calendarFound = true;

    				eventId = createCalendarEvent(
    						calendar,
    						event.getStartTime(),
    						event.getEndTime(),
    						getEventTitle(siteId, event.getExamType(), event.getSequenceNumber()),
    						getType(event.getExamType()),
    						event.getLocation());

    			} catch (IdUnusedException e) {
    				log.debug("Calendar for site " + siteId + " not found");
    				calendarFound = false;
    			} catch (PermissionException e) {
    				e.printStackTrace();
    				return;
    			}
    		}

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

    		previousSiteId = siteId;
    	}

    	List<HecEvent> eventsUpdate = jdbcTemplate.query(
    			select_from + "where (STATE = 'M' or STATE = 'D')" + order_by,
    			new HecEventRowMapper());

    	log.info("loop and update "+ eventsUpdate.size() + " events");
    	for (HecEvent event : eventsUpdate) {

    		String siteId = getSiteId(
    				event.getCatalogNbr(),
    				event.getSessionId(),
    				event.getSection());

			boolean updateSuccess = false;
    		// only attempt event update if this is a new site or the calendar was found before
    		if (!siteId.equals(previousSiteId) || calendarFound)
    		{
    			Calendar calendar;
    			try {
    				calendar = getCalendar(siteId);
    				calendarFound = true;

    				updateSuccess = updateCalendarEvent(
    						calendar,
    						event.getEventId(),
    						event.getState(),
    						event.getStartTime(),
    						event.getEndTime(),
    						event.getLocation());

    			} catch (IdUnusedException e) {
    				log.debug("Calendar for site " + siteId + " not found");
    				calendarFound = false;
    			} catch (PermissionException e) {
    				e.printStackTrace();
    				return;
    			}
    		}

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

			previousSiteId = siteId;
    	}

    	logoutFromSakai();
    	log.info("added: " + addcount + " updated: " + updatecount + " deleted: " + deletecount);
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

	private String createCalendarEvent(Calendar calendar, Date startTime, Date endTime, String title, String type, String location)
	{
		CalendarEvent event;
		try {
			// add event to calendar
			event = calendar.addEvent(
					TimeService.newTimeRange(TimeService.newTime(startTime.getTime()), TimeService.newTime(endTime.getTime())),
					title,
					null,
					type,
					location,
					CalendarEvent.EventAccess.SITE,
					null,
					EntityManager.newReferenceList());

		} catch (PermissionException e) {
			e.printStackTrace();
			return null;
		}

		log.debug("created event: " + title +
				" from " + startTime.toString() +
				" to " + endTime.toString() +
				" in " + location);

		return event.getId();
    }

    private boolean updateCalendarEvent(Calendar calendar, String eventId, String state,
    		Date newStartTime, Date newEndTime, String newLocation)
    {
    	CalendarEventEdit edit;

		try {
			edit = calendar.getEditEvent(eventId, CalendarService.EVENT_MODIFY_CALENDAR);
		} catch (Exception e) {
			log.info("Error retrieving event " + eventId);
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
				" from " + newStartTime.toString() +
				" to " + newEndTime.toString() +
				" in " + newLocation);

		return true;
    }

    private Calendar getCalendar(String siteId) throws IdUnusedException, PermissionException {
    	if (siteService.siteExists(siteId)) {
    		String calRef = calendarService.calendarReference(siteId, SiteService.MAIN_CONTAINER);
    		return calendarService.getCalendar(calRef);
    	} else {
    		throw new IdUnusedException("Site does not exist");
    	}
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
