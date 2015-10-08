-- une requete qui permet de sortir la liste de tous les matricules qui ont à la fois le rôle instructor dans un site de cours (Section)
-- et aussi le rôle coordinator dans le CourseOffering du CM (et donc dans tous les sections du cours).  Il y a un conflit.
select instructor_id as matricule, es.enterprise_id as course_id, mc.enterprise_id as course_offering_id 
from cm_member_container_t mc, cm_official_instructors_t i, cm_enrollment_set_t es, cm_membership_t m
where m.member_container_id = mc.member_container_id
and es.enrollment_set_id = i.enrollment_set_id
and mc.enterprise_id = substr(es.enterprise_id,1,length(es.enterprise_id)-3)
and substr(mc.enterprise_id, -2) <> '00'
and i.instructor_id = m.user_id
and m.role = 'C'
and instr(mc.enterprise_id, 2153) <> 0

