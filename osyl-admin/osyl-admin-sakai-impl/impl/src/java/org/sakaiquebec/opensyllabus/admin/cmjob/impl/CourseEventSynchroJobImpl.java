package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CourseEventSynchroJob;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * Job de synchro du fichier d'extract contenant les événements de cours avec la
 * table HEC_EVENT
 *
 * @author 11183065
 *
 */
public class CourseEventSynchroJobImpl implements CourseEventSynchroJob {

	private static Log log = LogFactory.getLog(CourseEventSynchroJobImpl.class);

	private static final String SEPARATOR = ";";

	private JdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Transactional
	public void execute(String filePath) {

		BufferedReader bufferedReader = null;

		log.info("Début de la job de synchro du fichier d'extract contenant les événements de cours avec la table HEC_EVENT");

		try {

			log.info("création d'une table temporaire TMP_HEC_EVENT");
			jdbcTemplate
					.execute("create table TMP_HEC_EVENT as select * from HEC_EVENT where 1=0");

			log.info("Lecture du fichier d'extract");
			bufferedReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath), "ISO-8859-1"));

			String line = bufferedReader.readLine();
			log.debug("noms des colonnes : " + line);
			final List<String> columnNames = Arrays.asList(line
					.split(SEPARATOR));

			final ArrayList<String[]> extractLines = new ArrayList<String[]>();
			while ((line = bufferedReader.readLine()) != null) {
				extractLines.add(line.split(SEPARATOR));
			}

			log.info("Alimentation de la table temporaire TMP_HEC_EVENT");
			jdbcTemplate
					.batchUpdate(
							"insert into TMP_HEC_EVENT (CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE, DATE_HEURE_DEBUT, DATE_HEURE_FIN, FACILITY_ID, DESCR_FACILITY, DESCR) " +
							"values (?, ?, ?, ?, ?, ?, to_date(?, 'yyyy-mm-dd hh24:mi'),  to_date(?, 'yyyy-mm-dd hh24:mi'), ?, ?, ?)",
							new BatchPreparedStatementSetter() {

								@Override
								public void setValues(PreparedStatement ps,
										int i) throws SQLException {
									String[] columnValues = extractLines.get(i);
									ps.setString(1, columnValues[columnNames
											.indexOf("CATALOG_NBR")]);
									ps.setString(2, columnValues[columnNames
											.indexOf("STRM")]);
									ps.setString(3, columnValues[columnNames
											.indexOf("SESSION_CODE")]);
									ps.setString(4, columnValues[columnNames
											.indexOf("CLASS_SECTION")]);
									ps.setString(5, columnValues[columnNames
											.indexOf("SEQ")]);
									ps.setString(6, columnValues[columnNames
											.indexOf("CLASS_EXAM_TYPE")]);
									ps.setString(7, columnValues[columnNames
											.indexOf("DATE_HEURE_DEBUT")]);
									ps.setString(8, columnValues[columnNames
											.indexOf("DATE_HEURE_FIN")]);
									ps.setString(9, columnValues[columnNames
											.indexOf("FACILITY_ID")]);
									ps.setString(10, columnValues[columnNames
											.indexOf("DESCR_FACILITY")]);
									ps.setString(11, columnValues[columnNames
											.indexOf("DESCR")]);
								}

								@Override
								public int getBatchSize() {
									return extractLines.size();
								}
							});

			log.info("Récupération de la date de début de l'événement le plus ancien présent dans le fichier d'extract");
			Date dateDebutMin = (Date) jdbcTemplate.queryForObject(
					"select min(DATE_HEURE_DEBUT) from TMP_HEC_EVENT",
					Date.class);

			log.info("Suppression des événements dont la date de début est inférieure à "
					+ dateDebutMin);
			jdbcTemplate
					.update("delete from HEC_EVENT where DATE_HEURE_DEBUT < ?",
							new Object[] { dateDebutMin });

			log.info("Réinitialisation du champ STATE de la table HEC_EVENT");
			jdbcTemplate.update("update HEC_EVENT set STATE = null");

			log.info("Ajout des nouveaux événements");
			jdbcTemplate
					.update("insert into HEC_EVENT (CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE, DATE_HEURE_DEBUT, DATE_HEURE_FIN, FACILITY_ID, DESCR_FACILITY, DESCR, STATE)"
							+ "select CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE, DATE_HEURE_DEBUT, DATE_HEURE_FIN, FACILITY_ID, DESCR_FACILITY, DESCR, 'A' "
							+ "from TMP_HEC_EVENT "
							+ "where (CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE) not in ("
							+ "select CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE from HEC_EVENT)");

			log.info("Maj des événements modifiés");
			jdbcTemplate
					.update("update HEC_EVENT t1 set (DATE_HEURE_DEBUT, DATE_HEURE_FIN, FACILITY_ID, DESCR_FACILITY, DESCR, STATE) = "
							+ "		(select DATE_HEURE_DEBUT, DATE_HEURE_FIN, FACILITY_ID, DESCR_FACILITY, DESCR, 'M' "
							+ "		from TMP_HEC_EVENT t2 "
							+ "		where t2.CATALOG_NBR = t1.CATALOG_NBR "
							+ "		and t2.STRM = t1.STRM "
							+ "		and t2.SESSION_CODE = t1.SESSION_CODE "
							+ "		and t2.CLASS_SECTION =  t1.CLASS_SECTION "
							+ "		and t2.SEQ = t1.SEQ "
							+ "		and t2.CLASS_EXAM_TYPE = t1.CLASS_EXAM_TYPE) "
							+ "where (CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE) in ( "
							+ "		select t2.CATALOG_NBR, t2.STRM, t2.SESSION_CODE, t2.CLASS_SECTION, t2.SEQ, t2.CLASS_EXAM_TYPE "
							+ "		from TMP_HEC_EVENT t2 "
							+ "		where t2.CATALOG_NBR = t1.CATALOG_NBR "
							+ "		and t2.STRM = t1.STRM "
							+ "		and t2.SESSION_CODE = t1.SESSION_CODE "
							+ "		and t2.CLASS_SECTION =  t1.CLASS_SECTION "
							+ "		and t2.SEQ = t1.SEQ "
							+ "		and t2.CLASS_EXAM_TYPE = t1.CLASS_EXAM_TYPE "
							+ "		and (t1.DATE_HEURE_DEBUT != t2.DATE_HEURE_DEBUT "
							+ "			or t1.DATE_HEURE_FIN != t2.DATE_HEURE_FIN "
							+ "			or t1.FACILITY_ID != t2.FACILITY_ID "
							+ "			or t1.DESCR_FACILITY != t2.DESCR_FACILITY "
							+ "			or t1.DESCR != t2.DESCR))");

			log.info("Marquage des événements supprimés");
			jdbcTemplate
					.update("update HEC_EVENT set STATE = 'D' where (CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE) not in "
							+ "(select CATALOG_NBR, STRM, SESSION_CODE, CLASS_SECTION, SEQ, CLASS_EXAM_TYPE from TMP_HEC_EVENT)");

			log.info("suppression de la table temporaire TMP_HEC_EVENT");
			jdbcTemplate.execute("drop table TMP_HEC_EVENT");

			log.info("Fin de la job de synchro du fichier d'extract contenant les événements de cours avec la table HEC_EVENT");

		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.warn(e);
				}
			}
		}
	}
}
