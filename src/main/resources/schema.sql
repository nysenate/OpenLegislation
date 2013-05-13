DROP TABLE IF EXISTS updates;
CREATE  TABLE IF NOT EXISTS updates (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  otype VARCHAR(255) NOT NULL ,
  oid VARCHAR(255) NOT NULL ,
  datetime DATETIME NOT NULL ,
  status ENUM('NEW', 'MODIFIED', 'DELETED') NOT NULL ,
);
DROP TABLE IF EXISTS report;
CREATE TABLE `report` (
  `report_id` int(11) NOT NULL AUTO_INCREMENT,
  `rdate` Date NOT NULL,
   PRIMARY KEY (`report_id`)
);
DROP TABLE IF EXISTS error;
CREATE TABLE `error` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `report_id` int(11) DEFAULT NULL,
  `bill_id` varchar(45) NOT NULL,
  `error_info` enum('summary','title','action','sponsor','cosponsor') DEFAULT NULL,
  `lbdc` text,
  `json` text,
  PRIMARY KEY (`id`),
  KEY `report_id_idx` (`report_id`),
  CONSTRAINT `report_id` FOREIGN KEY (`report_id`) REFERENCES `report` (`report_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);
