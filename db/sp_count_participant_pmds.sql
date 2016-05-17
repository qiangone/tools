-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE  PROCEDURE sp_count_participant_pmds(sbuId int)
BEGIN


DECLARE done INT DEFAULT FALSE;
DECLARE  avaliable_seats int default 0;
DECLARE  used_seats int default 0;
DECLARE  lost_seats int default 0;
DECLARE  avaliable_pmds int default 0;
DECLARE  used_pmds int default 0;
DECLARE  lost_pmds int default 0;

DECLARE  temp1 int default 0;
DECLARE  temp2 int default 0;
DECLARE  temp3 int default 0;
DECLARE  temp4 int default 0;



/* cursor*/
DECLARE cursor_upcomming CURSOR FOR 
select 
sum(a.assign_seats) ,
sum(a.assign_pmds),
sum(a.seats)-sum(a.assign_seats) ,
sum(a.pmds)-sum(a.assign_pmds)
from 
sbu_course a left join sbu b on a.sbu_id=b.id
left join course c on c.id=a.course_id
where c.start_time>TIMESTAMPADD(WEEK,2, sysdate())
and b.id=sbuId
and b.tag=0
group by b.id;

DECLARE cursor_ongoing CURSOR FOR 
select 
sum(a.assign_seats) ,
sum(a.assign_pmds),
sum(a.seats)-sum(a.assign_seats),
sum(a.pmds)-sum(a.assign_pmds)
from 
sbu_course a left join sbu b on a.sbu_id=b.id
left join course c on c.id=a.course_id
where 
c.start_time< TIMESTAMPADD(WEEK,2, sysdate())
and c.end_time>sysdate()
and b.id=sbuId
and b.tag=0
group by b.id;


DECLARE cursor_closed_1 CURSOR FOR 
select 
sum(a.assign_seats),
sum(a.assign_pmds)
from 
sbu_course a left join sbu b on a.sbu_id=b.id
left join course c on c.id=a.course_id
left join course_participant d on d.course_id=a.course_id and d.sbu_id=a.sbu_id
where 
c.end_time <		sysdate()
and b.id=sbuId
and b.tag=0
and d.attend=0
group by b.id;

DECLARE cursor_closed_2 CURSOR FOR 
select 
sum(a.assign_seats),
sum(a.assign_pmds)
from 
sbu_course a left join sbu b on a.sbu_id=b.id
left join course c on c.id=a.course_id
left join course_participant d on d.course_id=a.course_id and d.sbu_id=a.sbu_id
where 
c.end_time <		sysdate()
and b.id=sbuId
and b.tag=0
and d.attend=1
group by b.id;

DECLARE cursor_closed_3 CURSOR FOR 
select 
sum(a.seats)-sum(a.assign_seats),
sum(a.pmds)-sum(a.assign_pmds)
from 
sbu_course a left join sbu b on a.sbu_id=b.id
left join course c on c.id=a.course_id
left join course_participant d on d.course_id=a.course_id and d.sbu_id=a.sbu_id
where 
c.end_time <		sysdate()
and b.id=sbuId
and b.tag=0
group by b.id;





DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

/* upcomming course begin*/
open cursor_upcomming;
FETCH cursor_upcomming INTO temp1, temp2, temp3, temp4;

while done = FALSE do
set used_seats = used_seats + temp1;
set used_pmds = used_pmds + temp2;
set avaliable_seats = avaliable_seats + temp3;
set avaliable_pmds = avaliable_pmds + temp4;
FETCH cursor_upcomming INTO temp1, temp2, temp3, temp4;
end while;
close cursor_upcomming;

/*reset done variable=false*/
set done = FALSE;
open cursor_ongoing;
FETCH cursor_ongoing INTO temp1, temp2, temp3, temp4;
while done = FALSE do
set used_seats = used_seats + temp1;
set used_pmds = used_pmds + temp2;
set lost_seats = lost_seats + temp3;
set lost_pmds = lost_pmds + temp4;
FETCH cursor_ongoing INTO temp1, temp2, temp3, temp4;
end while;
close cursor_ongoing;


set done = FALSE;
open cursor_closed_1;
FETCH cursor_closed_1 INTO temp1, temp2;
while done = FALSE do

set lost_seats = lost_seats + temp1;
set lost_pmds = lost_pmds + temp2;
FETCH cursor_closed_1 INTO temp1, temp2;
end while;
close cursor_closed_1;

set done = FALSE;
open cursor_closed_2;
FETCH cursor_closed_2 INTO temp1, temp2;
while done = FALSE do

set used_seats = used_seats + temp1;
set used_pmds = used_pmds + temp2;
FETCH cursor_closed_2 INTO temp1, temp2;
end while;
close cursor_closed_2;

set done = FALSE;
open cursor_closed_3;
FETCH cursor_closed_3 INTO temp1, temp2;
while done = FALSE do
set lost_seats = lost_seats + temp1;
set lost_pmds = lost_pmds + temp2;
FETCH cursor_closed_3 INTO temp1, temp2;
end while;
close cursor_closed_3;

select used_seats,used_pmds,avaliable_seats,avaliable_pmds,lost_seats,lost_pmds;

END