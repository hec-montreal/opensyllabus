/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2010 The Sakai Foundation, The Sakai Quebec Team.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.calendar.api.Calendar;
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

//import lombok.Data;

/**
 * @author curtis.van-osch@hec.ca
 * @version $Id: $
 */
public class CreateCalendarEventsJobImpl extends OsylAbstractQuartzJobImpl implements Job {

    private static Log log = LogFactory.getLog(CreateCalendarEventsJobImpl.class);

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	int addcount = 0, updatecount = 0, deletecount = 0;

    private static final String EVENT_TYPE_CLASS_SESSION = "Class session";
    private static final String EVENT_TYPE_CANCELLATION = "Cancellation";

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

	private class EventRowMapper implements RowMapper {
		@Override
		public HashMap<String, Object> mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			HashMap<String, Object> results = new HashMap<String, Object>();
			results.put("catalog_nbr", rs.getString("CATALOG_NBR"));
			results.put("session_id", rs.getString("STRM"));
			results.put("section", rs.getString("CLASS_SECTION"));
			results.put("exam_type", rs.getString("CLASS_EXAM_TYPE"));
			results.put("sequence_number", rs.getInt("SEQ"));
			results.put("start_time", rs.getDate("DATE_HEURE_DEBUT"));
			results.put("end_time", rs.getDate("DATE_HEURE_FIN"));
			results.put("location", rs.getString("DESCR_FACILITY"));
			results.put("event_id", rs.getString("EVENT_ID"));
			results.put("state", rs.getString("STATE"));

			return results;
		}
	}

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
    	log.debug("starting CreateCalendarEventsJob");

    	loginToSakai("CreateCalendarEventsJob");

    	String select_from = "select CATALOG_NBR, STRM, CLASS_SECTION, CLASS_EXAM_TYPE, SEQ, DATE_HEURE_DEBUT, "
				+ "DATE_HEURE_FIN, DESCR_FACILITY, STATE, EVENT_ID from HEC_EVENT ";
    	try {

    		log.debug("get events to add");
    		List eventsAdd = jdbcTemplate.query(
    				select_from + "where STATE = 'A'",
    				new EventRowMapper());

    		log.debug("loop and add events");
    		for (Object event : eventsAdd) {
				HashMap<String, Object> t = (HashMap<String, Object>) event;
				String siteId = getSiteId(
						(String)t.get("catalog_nbr"),
						(String)t.get("session_id"),
						(String)t.get("section"));
    			createEvent(
    					siteId,
    					(Date)t.get("start_time"),
    					(Date)t.get("end_time"),
    					getEventTitle(siteId, (String)t.get("exam_type"), (Integer)t.get("sequence_num")),
    					EVENT_TYPE_CLASS_SESSION,
    					(String)t.get("location"));

    			/*
    			clearEventState(
    					(String)t.get("catalog_nbr"),
						(String)t.get("session_id"),
						(String)t.get("section"),
						(String)t.get("exam_type"),
						(Integer)t.get("sequence_num"));
						*/
    		}

    		log.debug("get events to update");
    		List eventsUpdate = jdbcTemplate.query(
    				select_from + "where STATE = 'M' or STATE = 'D'",
    				new EventRowMapper());

    		log.debug("loop and update events");
    		for (Object event : eventsUpdate) {
				HashMap<String, Object> t = (HashMap<String, Object>) event;
				String siteId = getSiteId(
						(String)t.get("catalog_nbr"),
						(String)t.get("session_id"),
						(String)t.get("section"));

    			updateEvent(
    					siteId,
    					(String)t.get("event_id"),
    					(String)t.get("state"),
    					(Date)t.get("start_time"),
    					(Date)t.get("end_time"),
    					(String)t.get("location"));

    			/*
    			clearEventState(
    					(String)t.get("catalog_nbr"),
						(String)t.get("session_id"),
						(String)t.get("section"),
						(String)t.get("exam_type"),
						(Integer)t.get("sequence_num"));
						*/
    		}
		} catch (PermissionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IdUnusedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logoutFromSakai();
    	log.debug("exiting CreateCalendarEventsJob");
    	log.debug("added: " + addcount + " updated: " + updatecount + " deleted: " + deletecount);
    } // execute

	private void createEvent(String siteId, Date startTime, Date endTime, String title, String type, String location)
    		throws PermissionException, IdUnusedException {
		/*
		Calendar calendar = getCalendar(siteId);

		// add event to calendar
		CalendarEvent event = calendar.addEvent(
				TimeService.newTimeRange(startTime.getTime(), endTime.getTime()),
				title,
				null,
				type,
				location,
				CalendarEvent.EventAccess.SITE,
				null, //groupRestrictions,
				EntityManager.newReferenceList());
				*/
		addcount++;
		log.info("creating event: " + title +
				" in " + siteId +
				" from " + formatter.format(startTime) +
				" to " + formatter.format(endTime) +
				" in " + location);
    }

    private void updateEvent(String siteId, String eventId, String state, Date newStartTime, Date newEndTime, String newLocation)
    		throws IdUnusedException, PermissionException, InUseException {

    	log.debug("update event entered");
    	Calendar calendar = getCalendar(siteId);

    	CalendarEventEdit edit = calendar.getEditEvent(eventId, CalendarService.EVENT_MODIFY_CALENDAR);
		if (state.equals("M")) {
			if (newStartTime != null && newEndTime != null)
				edit.setRange(TimeService.newTimeRange(newStartTime.getTime(), newEndTime.getTime()));
			if (newLocation != null)
				edit.setLocation(newLocation);
			updatecount++;
		}
		else if (state.equals("D")) {
			// set type
			deletecount++;
		}

//		calendar.commitEvent(edit);
		log.info("updating ("+state+") event: " + edit.getDisplayName() +
				" in " + siteId +
				" from " + formatter.format(newStartTime) +
				" to " + formatter.format(newEndTime) +
				" in " + newLocation);
    }

    private Calendar getCalendar(String siteId) throws IdUnusedException, PermissionException {
       	String calRef = calendarService.calendarReference(siteId, SiteService.MAIN_CONTAINER);
		return calendarService.getCalendar(calRef);
    }

    private String getSiteId(String catalog_nbr, String session_code, String section) {
    	return "siteid";
    }

    // Externalize labels
	private String getEventTitle(String siteId, String type, Integer seq_num) {
		String title = "";
		if (type == null)
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
		return "";
	}

	/*
	@Data
	private class HecEvent {
		String catalog_nbr, session_id, section, exam_type, location;
		Date startDate, endDate;
	}
	*/
}
