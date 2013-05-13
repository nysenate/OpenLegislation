CREATE SCHEMA IF NOT EXISTS `OpenLegUpdateTracker` ;
USE 'OpenLegUpdateTracker';

DROP TABLE IF EXISTS `OpenLegUpdateTracker`.`Updates`;
CREATE  TABLE IF NOT EXISTS `OpenLegUpdateTracker`.`Updates` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `otype` VARCHAR(40) NOT NULL ,
  `oid` VARCHAR(120) NOT NULL ,
  `date` DATETIME NOT NULL ,
  `status` SET('NEW', 'MODIFIED', 'DELETED') NOT NULL ,
  PRIMARY KEY (`id`));