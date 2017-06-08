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

import ca.hec.commons.utils.FormatUtils;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.calendar.api.Calendar;
import org.sakaiproject.calendar.api.CalendarEvent;
import org.sakaiproject.calendar.api.CalendarEventEdit;
import org.sakaiproject.calendar.api.CalendarService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.time.cover.TimeService;
import org.sakaiproject.util.ResourceLoader;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CreateCalendarEventsJob;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;


/**
 * @author curtis.van-osch@hec.ca
 * @version $Id: $
 */
public class CreateCalendarEventsJobImpl extends OsylAbstractQuartzJobImpl implements CreateCalendarEventsJob {

	private static Log log = LogFactory.getLog(CreateCalendarEventsJobImpl.class);
	private static ResourceLoader rb = new ResourceLoader("org.sakaiquebec.opensyllabus.admin.cmjob.impl.bundle.CMJobMessages");

	private final String EVENT_TYPE_CLASS_SESSION = "Class session";
	private final String EVENT_TYPE_EXAM = "Exam";
	private final String EVENT_TYPE_QUIZ = "Quiz";
	private final String EVENT_TYPE_SPECIAL = "Special event";
	private final String PSFT_EXAM_TYPE_INTRA = "INTR";
	private final String PSFT_EXAM_TYPE_FINAL = "FIN";
	private final String PSFT_EXAM_TYPE_TEST = "TEST";
	private final String PSFT_EXAM_TYPE_QUIZ = "QUIZ";

	@Setter
	private CalendarService calendarService;

	@Setter
	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.info("starting CreateCalendarEventsJob");
		int addcount = 0, updatecount = 0, deletecount = 0;

		loginToSakai("CreateCalendarEventsJob");

		String select_from = "select CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, CLASS_EXAM_TYPE, SEQ, DATE_HEURE_DEBUT, "
				+ "DATE_HEURE_FIN, DESCR_FACILITY, STATE, DESCR, EVENT_ID from HEC_EVENT ";
		String order_by = " order by CATALOG_NBR, STRM, CLASS_SECTION ";

		List<HecEvent> eventsAdd = jdbcTemplate.query(
				select_from + "where (EVENT_ID is null and (STATE is null or STATE <> 'D'))" + order_by,
				new HecEventRowMapper());

		// keep track of the last event's site id, calendar and courseOffering, so we can use the calendar if it was already found
		Calendar calendar = null;
		String previousSiteId = "";
		boolean calendarFound = false;

		List<String> piloteE2017 = adminConfigService.getPiloteE2017();

		log.info("loop and add " + eventsAdd.size() + " events");
		for (HecEvent event : eventsAdd) {
			String siteId = getSiteId(
					event.getCatalogNbr(),
					event.getSessionId(),
					event.getSessionCode(),
					event.getSection());

			//Continue if course is to be in E2017 pilote
			if (adminConfigService.inE2017Pilote(event.getCatalogNbr()+event.getSessionId(), piloteE2017))
				continue;

			String eventId = null;

			if (!siteId.equals(previousSiteId))
			{
				// this is a new site id, calendar not found yet
				calendarFound = false;

				try {
					calendar = getCalendar(siteId);
					calendarFound = true;

				} catch (IdUnusedException e) {
					log.debug("Site or Calendar for " + siteId + " does not exist");
				} catch (PermissionException e) {
					e.printStackTrace();
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// only attempt event creation if the calendar was found
			if (calendarFound) {
				boolean createEvent = true;

				if (event.getStartTime().getYear() != event.getEndTime().getYear() ||
						event.getStartTime().getMonth() != event.getEndTime().getMonth() ||
						event.getStartTime().getDate() != event.getEndTime().getDate()) {

					createEvent = false;
					log.debug("Skipping event creation: " + getEventTitle(siteId, event.getExamType(), event.getSequenceNumber()) +
							" for site " + siteId + " (end date is after start date)");
				}

				// don't bother adding the events if this is an MBA site (ZCII-1495) or DF (ZCII-1665)
				// and the event is not a final or mid-term (intratrimestriel) exam
				if (siteId.contains("DF") &&
						!event.getExamType().equals(PSFT_EXAM_TYPE_INTRA) &&
						!event.getExamType().equals(PSFT_EXAM_TYPE_FINAL)) {
					createEvent = false;
					log.debug("Skipping event creation: " + getEventTitle(siteId, event.getExamType(), event.getSequenceNumber()) +
							" for site " + siteId + " (course is MBA or DF and event is not an exam)");
				}

				if (createEvent) {
					eventId = createCalendarEvent(
							calendar,
							event.getStartTime(),
							event.getEndTime(),
							getEventTitle(siteId, event.getExamType(), event.getSequenceNumber()),
							getType(event.getExamType()),
							event.getLocation(),
							event.getDescription());
				}
			}

			// clear the state in HEC_EVENT regardless
			if (!clearHecEventState(
					eventId,
					event.getCatalogNbr(),
					event.getSessionId(),
					event.getSessionCode(),
					event.getSection(),
					event.getExamType(),
					event.getSequenceNumber())) {

				throw new JobExecutionException();
			}

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

			//Continue if course is to be in E2017 pilote
			if (adminConfigService.inE2017Pilote(event.getCatalogNbr()+event.getSessionId(), piloteE2017)) {
				log.debug("Skipping event for " + event.getCatalogNbr()+event.getSessionId() + " because it is in section-aware pilot.");
				continue;
			}

			//ZCII-2821: Do not sync data during and after A2017
			if (!isBeforeA2017(Integer.parseInt(event.getSessionId()))) {
				log.debug("Skipping event for " + event.getCatalogNbr() + event.getSessionId() + " because it's session is equal to or after A2017.");
				continue;
			}

			String siteId = getSiteId(
					event.getCatalogNbr(),
					event.getSessionId(),
					event.getSessionCode(),
					event.getSection());

			boolean updateSuccess = false;

			if (!siteId.equals(previousSiteId))
			{
				// new site, calendar not yet found
				calendarFound = false;

				try {
					calendar = getCalendar(siteId);
					calendarFound = true;

				} catch (IdUnusedException e) {
					log.debug("Site or Calendar for " + siteId + " does not exist.");
				} catch (PermissionException e) {
					e.printStackTrace();
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (calendarFound) {

				// don't bother adding the events if this is an MBA site (ZCII-1495) or DF (ZCII-1665)
				// and the event is not a final or mid-term (intratrimestriel) exam
				if (!siteId.contains("DF") ||
						event.getExamType().equals(PSFT_EXAM_TYPE_INTRA) ||
						event.getExamType().equals(PSFT_EXAM_TYPE_FINAL)) {

					updateSuccess = updateCalendarEvent(
							calendar,
							event.getEventId(),
							event.getState(),
							event.getStartTime(),
							event.getEndTime(),
							event.getLocation(),
							event.getDescription());
				} else {
					log.debug("Skipping event update: " + getEventTitle(siteId, event.getExamType(), event.getSequenceNumber()) +
							" for site " + siteId + " (course is MBA or DF and event is not an exam)");
				}
			}

			if (event.getState().equals("M")) {
				if (!clearHecEventState(
						event.getEventId(),
						event.getCatalogNbr(),
						event.getSessionId(),
						event.getSessionCode(),
						event.getSection(),
						event.getExamType(),
						event.getSequenceNumber())) {
					throw new JobExecutionException();
				}

				if (updateSuccess)
					updatecount++;
			}
			else if (event.getState().equals("D")) {
				if (!deleteHecEvent(
						event.getCatalogNbr(),
						event.getSessionId(),
						event.getSessionCode(),
						event.getSection(),
						event.getExamType(),
						event.getSequenceNumber())) {
					throw new JobExecutionException();
				}

				if (updateSuccess)
					deletecount++;
			}

			previousSiteId = siteId;
		}

		logoutFromSakai();
		log.info("added: " + addcount + " updated: " + updatecount + " deleted: " + deletecount);
	} // execute

	private boolean clearHecEventState(String event_id, String catalog_nbr, String session_id, String session_code, String section,
									   String exam_type, Integer sequence_num) {

		try {
			int affectedRows = jdbcTemplate.update("update HEC_EVENT set STATE = null, EVENT_ID = ? where CATALOG_NBR = ? and STRM = ? and " +
							"SESSION_CODE = ? and CLASS_SECTION = ? and CLASS_EXAM_TYPE = ? and SEQ = ?",
					new Object[] {event_id, catalog_nbr, session_id, session_code, section, exam_type, sequence_num});

			if (affectedRows != 1) {
				return false;
			} else {
				return true;
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean deleteHecEvent(String catalog_nbr, String session_id, String session_code, String section,
								   String exam_type, Integer sequence_num) {

		try {
			int affectedRows = jdbcTemplate.update("delete from HEC_EVENT where CATALOG_NBR = ? and STRM = ? and SESSION_CODE = ? and CLASS_SECTION = ? " +
							"and CLASS_EXAM_TYPE = ? and SEQ = ?",
					new Object[] {catalog_nbr, session_id, session_code, section, exam_type, sequence_num });

			if (affectedRows != 1) {
				return false;
			} else {
				return true;
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String createCalendarEvent(Calendar calendar, Date startTime, Date endTime, String title, String type, String location, String description)
	{
		CalendarEvent event;
		try {
			// add event to calendar
			event = calendar.addEvent(
					TimeService.newTimeRange(TimeService.newTime(startTime.getTime()), TimeService.newTime(endTime.getTime()), true, false),
					title,
					description,
					type,
					location,
					CalendarEvent.EventAccess.SITE,
					null,
					EntityManager.newReferenceList());

		} catch (PermissionException e) {
			e.printStackTrace();
			return null;
		}

		log.debug("created event: " + title + " in site " + calendar.getContext());

		return event.getId();
	}

	private boolean updateCalendarEvent(Calendar calendar, String eventId, String state,
										Date newStartTime, Date newEndTime, String newLocation, String newDescription)
	{
		if (eventId == null)
			return false;

		CalendarEventEdit edit;

		try {
			edit = calendar.getEditEvent(eventId, CalendarService.EVENT_MODIFY_CALENDAR);
		} catch (IdUnusedException e) {
			log.debug("Event " + eventId + " does not exist");
			return false;
		} catch (NullPointerException e) {
			log.debug("Event " + eventId + " does not exist");
			return false;
		} catch (Exception e) {
			log.error("Error retrieving event " + eventId);
			e.printStackTrace();
			return false;
		}

		if (state.equals("M")) {
			if (newStartTime != null && newEndTime != null)
				edit.setRange(TimeService.newTimeRange(TimeService.newTime(newStartTime.getTime()), TimeService.newTime(newEndTime.getTime()), true, false));
			if (newLocation != null)
				edit.setLocation(newLocation);
			if (newDescription != null)
				edit.setDescription(newDescription);
		}
		else if (state.equals("D")) {
			try {
				calendar.removeEvent(edit);
			} catch (PermissionException e) {
				log.error("User doesn't have permission to delete event " + eventId);
				return false;
			}
		}

		calendar.commitEvent(edit);
		log.debug("updated ("+state+") event: " + edit.getDisplayName() + " in site " + calendar.getContext());

		return true;
	}

	private Calendar getCalendar(String siteId) throws IdUnusedException, PermissionException {
		if (siteService.siteExists(siteId)) {
			String calRef = calendarService.calendarReference(siteId, siteService.MAIN_CONTAINER);
			return calendarService.getCalendar(calRef);
		} else {
			throw new IdUnusedException("Site does not exist");
		}
	}

	private String getSiteId(String catalog_nbr, String session_id, String session_code, String section) {
		String siteId = FormatUtils.formatCourseId(catalog_nbr);
		siteId += "." + FormatUtils.getSessionName(session_id);

		if (!session_code.equals("1"))
			siteId += "." + session_code;

		siteId += "." + section;

		return siteId;
	}

	private String getEventTitle(String siteId, String type, Integer seq_num) {

		Site site = null;
		String courseSiteTittle = "";

		try{
			site = siteService.getSite(siteId);
			courseSiteTittle = site.getProperties().getPropertyFormatted("title");
		}catch (IdUnusedException e){
			log.error("The site " + siteId + "does not exist");
		}


		if (type.equals(" ")){
			if (courseSiteTittle != "")
				return (courseSiteTittle + " (" + rb.getFormattedMessage("calendar.event-title.session", new Object[] { seq_num, siteId }) + ")");
			else
				return rb.getFormattedMessage("calendar.event-title.session", new Object[] { seq_num, siteId });
		}
		else if (type.equals(PSFT_EXAM_TYPE_INTRA)){
			if (courseSiteTittle != "")
				return (courseSiteTittle + " (" + rb.getFormattedMessage("calendar.event-title.intra", new Object[] { siteId }) + ")");
			else
				return rb.getFormattedMessage("calendar.event-title.intra", new Object[] { siteId });
		}
		else if (type.equals(PSFT_EXAM_TYPE_FINAL)){
			if (courseSiteTittle != "")
				return (courseSiteTittle + " (" + rb.getFormattedMessage("calendar.event-title.final", new Object[] { siteId }) + ")");
			else
				return rb.getFormattedMessage("calendar.event-title.final", new Object[] { siteId });
		}
		else if (type.equals(PSFT_EXAM_TYPE_TEST) || type.equals(PSFT_EXAM_TYPE_QUIZ)){
			if (courseSiteTittle != "")
				return (courseSiteTittle + " (" + rb.getFormattedMessage("calendar.event-title.test", new Object[] { siteId }) + ")");
			else
				rb.getFormattedMessage("calendar.event-title.test", new Object[] { siteId });
		}

		else{
			if (courseSiteTittle != "")
				return (courseSiteTittle + " (" + rb.getFormattedMessage("calendar.event-title.other", new Object[] { type, siteId }) + ")");
		}

		return rb.getFormattedMessage("calendar.event-title.other", new Object[] { type, siteId });

	}

	private String getType(String exam_type) {
		if (exam_type.equals(" "))
			return EVENT_TYPE_CLASS_SESSION;
		else if (exam_type.equals(PSFT_EXAM_TYPE_INTRA) || exam_type.equals(PSFT_EXAM_TYPE_FINAL))
			return this.EVENT_TYPE_EXAM;
		else if (exam_type.equals(PSFT_EXAM_TYPE_TEST) || exam_type.equals(PSFT_EXAM_TYPE_QUIZ))
			return EVENT_TYPE_QUIZ;
		else
			return EVENT_TYPE_SPECIAL;
	}

	@Data
	private class HecEvent {
		String catalogNbr, sessionId, sessionCode, section, state, examType, location, description, eventId;
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
			event.setDescription(rs.getString("DESCR"));
			event.setEventId(rs.getString("EVENT_ID"));
			event.setState(rs.getString("STATE"));

			return event;
		}
	}
}
