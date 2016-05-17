-- --------------------------------------------------------------------------------
-- Routine DDL
-- Note: comments before and after the routine body will not be stored by the server
-- --------------------------------------------------------------------------------
DELIMITER $$

CREATE  PROCEDURE sp_convert_to_free_seats()
BEGIN

DECLARE  courseId int default 0;
DECLARE  duration float default 0;
DECLARE  startTime date default NULL;
DECLARE  endTime date default NULL;
DECLARE  freeSeats int default 0;
DECLARE done INT DEFAULT FALSE;
/* cursor*/
DECLARE cursor_get_free_seats CURSOR FOR 
select 
a.course_id,
a.duration,
c.start_time,
c.end_time,
sum(a.seats-a.assign_seats-a.swap_seats) free_seats
from sbu_course a left join sbu b on a.sbu_id=b.id 
left join course c on a.course_id=c.id
where   1=1
and TIMESTAMPDIFF(DAY, sysdate(),c.start_time)=13
and b.parent_id=0
group by a.course_id
order by a.course_id;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

open cursor_get_free_seats;

FETCH cursor_get_free_seats INTO courseId, duration, startTime, endTime, freeSeats;

while done = FALSE do
insert into free_seat_pool(course_id,duration,seats,start_time,end_time) values(courseId, duration, freeSeats,startTime, endTime);
FETCH cursor_get_free_seats INTO courseId, duration, startTime, endTime, freeSeats;
end while;

select * from free_seat_pool;
close cursor_get_free_seats;




END