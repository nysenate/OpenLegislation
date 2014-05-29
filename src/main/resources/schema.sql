DROP TABLE IF EXISTS changelog;
DROP TABLE IF EXISTS report_observation;
DROP TABLE IF EXISTS report_error;
DROP TABLE IF EXISTS report;



CREATE TABLE `changelog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `otype` varchar(255) NOT NULL,
  `oid` varchar(255) NOT NULL,
  `time` datetime NOT NULL,
  `status` enum('NEW','MODIFIED','DELETED') NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `report` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `time` (`time`)
)  ENGINE=INNODB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `report_error` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `oid` varchar(255) CHARACTER SET utf8 NOT NULL,
  `field` enum('BILL_SUMMARY','BILL_TITLE','BILL_ACTION','BILL_SPONSOR','BILL_COSPONSOR','BILL_TEXT_PAGE', 'BILL_AMENDMENT') CHARACTER SET utf8 NOT NULL,
  `openedAt` datetime NOT NULL,
  `closedAt` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `openedAt` (`openedAt`),
  KEY `closedAt` (`closedAt`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `report_observation` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `reportId` int(11) DEFAULT NULL,
  `oid` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `field` enum('BILL_SUMMARY','BILL_TITLE','BILL_ACTION','BILL_SPONSOR','BILL_COSPONSOR','BILL_TEXT_PAGE', 'BILL_AMENDMENT') COLLATE utf8_unicode_ci NOT NULL,
  `actualValue` text COLLATE utf8_unicode_ci,
  `observedValue` text COLLATE utf8_unicode_ci,
  `errorId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_id_idx` (`reportId`),
  KEY `errorId` (`errorId`),
  CONSTRAINT `report_observation_ibfk_1` FOREIGN KEY (`reportId`) REFERENCES `report` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `report_observation_ibfk_2` FOREIGN KEY (`errorId`) REFERENCES `report_error` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
