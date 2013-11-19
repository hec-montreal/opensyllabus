package org.sakaiquebec.opensyllabus.admin.cmjob.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sakaiquebec.opensyllabus.admin.cmjob.api.CourseEventSynchroJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CourseEventSynchroJobTest {

	@Autowired
	private CourseEventSynchroJob courseEventSynchroJob;

	@Autowired
	private DataSource dataSource;

	@Test
	public void executeTest() throws IOException, SQLException {

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update("delete from HEC_EVENT");

		File file = File.createTempFile("horaires_cours", "dat");
		file.deleteOnExit();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter
				.write("CATALOG_NBR;STRM;SESSION_CODE;CLASS_SECTION;SEQ;CLASS_EXAM_TYPE;DATE_HEURE_DEBUT;DATE_HEURE_FIN;FACILITY_ID;DESCR_FACILITY;DESCR;STRM_ID\n");
		fileWriter
				.write("    ANGL-H;2123;1;A01;      1; ;2012-08-22 10:30;2012-08-22 13:00; ;; ;21231\n");
		fileWriter
				.write("    ANGL-H;2123;1;A01;      1;TEST;2012-08-23 10:30;2012-08-23 13:00;PGAM;Procter & Gamble;A à GIRA;21231\n");
		fileWriter
				.write("    ANGL-H;2123;1;A01;      2;TEST;2012-08-23 10:30;2012-08-23 13:00;RCGT;Raymond Chabot Grant Thornton;GIRB à SIMA;21231\n");
		fileWriter
				.write("    ANGL-H;2123;1;A01;      3;TEST;2012-08-23 10:30;2012-08-23 13:00;OCGA;Ordre des CGA du Québec;SIMB à Z;21231\n");
		fileWriter.close();
		courseEventSynchroJob.execute(file.getAbsolutePath());

		Assert.assertEquals(
				4,
				jdbcTemplate
						.queryForInt("select count(*) from HEC_EVENT where state = 'A'"));

		fileWriter = new FileWriter(file);
		fileWriter
				.write("CATALOG_NBR;STRM;SESSION_CODE;CLASS_SECTION;SEQ;CLASS_EXAM_TYPE;DATE_HEURE_DEBUT;DATE_HEURE_FIN;FACILITY_ID;DESCR_FACILITY;DESCR;STRM_ID\n");
		// suppression de la première ligne => l'événement du doit être supprimé
		// de la table car la date de début de l'événement le plus ancien dans
		// le second fichier est daté du 23
		// modification des dates de la seconde ligne
		fileWriter
				.write("    ANGL-H;2123;1;A01;      1;TEST;2012-08-24 10:30;2012-08-24 13:00;PGAM;Procter & Gamble;A à GIRA;21231\n");
		// suppression de la troisième ligne => l'événement doit être marqué
		// comme supprimé
		// La 4eme ligne reste identique
		fileWriter
				.write("    ANGL-H;2123;1;A01;      3;TEST;2012-08-23 10:30;2012-08-23 13:00;OCGA;Ordre des CGA du Québec;SIMB à Z;21231\n");
		// ajout de deux lignes
		fileWriter
				.write("    ANGL-H;2123;1;B02;      1; ;2012-08-23 10:30;2012-08-23 13:00; ;; ;21231\n");
		fileWriter
				.write("    ANGL-H;2123;1;B02;      1;TEST;2012-08-23 10:30;2012-08-23 13:00;CIBC;Banque CIBC;A à GLAU;21231\n");
		fileWriter.close();
		courseEventSynchroJob.execute(file.getAbsolutePath());
		Assert.assertEquals(5,
				jdbcTemplate.queryForInt("select count(*) from HEC_EVENT"));
		Assert.assertEquals(
				2,
				jdbcTemplate
						.queryForInt("select count(*) from HEC_EVENT where state = 'A'"));
		Assert.assertEquals(
				1,
				jdbcTemplate
						.queryForInt("select count(*) from HEC_EVENT where state = 'M'"));
		Assert.assertEquals(
				1,
				jdbcTemplate
						.queryForInt("select count(*) from HEC_EVENT where state = 'D'"));
		Assert.assertEquals(
				1,
				jdbcTemplate
						.queryForInt("select count(*) from HEC_EVENT where state is null"));
		jdbcTemplate.update("delete from HEC_EVENT");
	}

	@Ignore
	@Test
	public void executeRealDataTest() {
		courseEventSynchroJob
				.execute("test/resources/org/sakaiquebec/opensyllabus/admin/cmjob/impl/horaires_cours.dat");
	}
}
