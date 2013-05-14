DROP TABLE IF EXISTS updates;
DROP TABLE IF EXISTS error;
DROP TABLE IF EXISTS report;


CREATE  TABLE IF NOT EXISTS updates (
  id int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  otype VARCHAR(255) NOT NULL ,
  oid VARCHAR(255) NOT NULL ,
  `date` DATETIME NOT NULL ,
  status ENUM('NEW', 'MODIFIED', 'DELETED') NOT NULL
);

CREATE TABLE `report` (
  `report_id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `rdate` Date NOT NULL
);

CREATE TABLE `error` (
  `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `report_id` int(11) DEFAULT NULL,
  `bill_id` varchar(45) NOT NULL,
  `error_info` enum('summary','title','action','sponsor','cosponsor') DEFAULT NULL,
  `lbdc` text,
  `json` text,
  KEY `report_id_idx` (`report_id`),
  CONSTRAINT `report_id` FOREIGN KEY (`report_id`) REFERENCES `report` (`report_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
