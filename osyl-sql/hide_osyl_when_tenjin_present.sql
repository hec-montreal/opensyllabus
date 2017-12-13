-- supprimer les properties existant
delete from sakai_site_tool_property where name = 'sakai-portal:visible' and TOOL_ID in
(select t1.TOOL_ID
from sakai_site_tool t1, sakai_site_tool t2
where t1.SITE_ID = t2.SITE_ID
and t1.site_id like '%A2017%'
and t1.REGISTRATION = 'sakai.opensyllabus.tool'
and t2.registration = 'sakai.tenjin');

insert into sakai_site_tool_property (SITE_ID,TOOL_ID,NAME,VALUE)
select t1.SITE_ID, t1.TOOL_ID, 'sakai-portal:visible', 'false'
from sakai_site_tool t1, sakai_site_tool t2
where t1.SITE_ID = t2.SITE_ID
and t1.site_id like '%A2017%'
and t1.REGISTRATION = 'sakai.opensyllabus.tool'
and t2.registration = 'sakai.tenjin';
