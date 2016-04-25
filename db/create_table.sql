DROP DATABASE IF EXISTS  university_tool;
CREATE DATABASE university_tool character set utf8;
USE university_tool;

DROP TABLE IF EXISTS lbps;
CREATE TABLE lbps (
  id int(11) NOT NULL AUTO_INCREMENT,
  name varchar(45) DEFAULT NULL,
  email varchar(45) DEFAULT NULL,
  logo varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS sbu_lbps;
CREATE TABLE sbu_lbps (
  id int(11) NOT NULL AUTO_INCREMENT,
  sbu_id int(11) DEFAULT NULL,
  lbps_id int(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sbu;
CREATE TABLE sbu (
  id int(11) NOT NULL AUTO_INCREMENT,
  sbu_name varchar(45) DEFAULT NULL COMMENT '名称',
  parent_id varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS course;
CREATE TABLE course (
  id int(11) NOT NULL AUTO_INCREMENT,
  event_name varchar(100) DEFAULT NULL,
  name varchar(100) DEFAULT NULL COMMENT '课程名称',
  start_time date DEFAULT NULL COMMENT '课程开始时间',
  end_time date DEFAULT NULL COMMENT '课程结束时间',
  duration float DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS sbu_course;
CREATE TABLE sbu_course (
  id int(11) NOT NULL AUTO_INCREMENT,
  sbu_id int(11) NOT NULL,
  course_id int(11) NOT NULL,
  duration float DEFAULT NULL,
  seats int(11) DEFAULT NULL,
  pmds int(11) DEFAULT NULL,
  assign_seats int(11) DEFAULT '0',
  assign_pmds int(11) DEFAULT '0',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS course_participant;
CREATE TABLE course_participant (
  id int(11) NOT NULL AUTO_INCREMENT,
  course_id int(11) NOT NULL,
  sbu_id int(11) DEFAULT NULL,
  participant_login_name varchar(45) DEFAULT NULL COMMENT '参与人员姓名',
  participant_email varchar(45) DEFAULT NULL,
  participant_dispaly_name varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS sbu_swap_history;
CREATE TABLE sbu_swap_history (
  id int(11) NOT NULL AUTO_INCREMENT,
  from_sbu_id int(11) DEFAULT NULL,
  to_sbu_id int(11) DEFAULT NULL,
  giveout_course_id int(11) DEFAULT NULL,
  takein_course_id int(11) DEFAULT NULL,
  giveout_duration float DEFAULT NULL,
  takein_duration float DEFAULT NULL,
  seats int(11) DEFAULT NULL,
  action_by varchar(45) DEFAULT NULL,
  action_time datetime DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE event_logo (
  id int(11) NOT NULL AUTO_INCREMENT,
  event_name varchar(45) DEFAULT NULL,
  logo varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



alter table sbu_course add unique key uniquekey_sbu_course(sbu_id,course_id);