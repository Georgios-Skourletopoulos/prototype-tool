CREATE DATABASE `tdq` /*!40100 DEFAULT CHARACTER SET utf8 */;
CREATE TABLE `tdq`.`jobtitle` (
  `id` bigint(20) NOT NULL,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`userlogin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `title` varchar(10) DEFAULT NULL,
  `firstName` varchar(45) DEFAULT NULL,
  `lastName` varchar(45) DEFAULT NULL,
  `jobId` bigint(20) DEFAULT NULL,
  `companyName` varchar(50) DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  `lastLog` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nickname_UNIQUE` (`nickname`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `jobId_idx` (`jobId`),
  CONSTRAINT `jobId` FOREIGN KEY (`jobId`) REFERENCES `jobtitle` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`cocomo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  `developmentMode` varchar(15) NOT NULL,
  `productSize` decimal(15,3) NOT NULL,
  `confidence` varchar(10) DEFAULT NULL,
  `justification` varchar(450) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `CocomoUserId_idx` (`userId`),
  CONSTRAINT `CocomoUserId` FOREIGN KEY (`userId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`costforimplementing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  `weightDevelopment` int(11) NOT NULL,
  `weightConfiguration` int(11) NOT NULL,
  `weightDeployment` int(11) NOT NULL,
  `weightLicences` int(11) NOT NULL,
  `weightInfrastructure` int(11) NOT NULL,
  `effortApplied` decimal(15,3) NOT NULL,
  `avgMonthlySalary` decimal(15,3) NOT NULL,
  `confidence` varchar(10) DEFAULT NULL,
  `productFlexibility` varchar(10) DEFAULT NULL,
  `marketFlexibility` varchar(10) DEFAULT NULL,
  `riskOfFutureTD` varchar(10) DEFAULT NULL,
  `realOptionsValuation` varchar(10) DEFAULT NULL,
  `justification` varchar(450) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `CostUserId_idx` (`userId`),
  CONSTRAINT `CostUserId` FOREIGN KEY (`userId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `projectName` varchar(50) NOT NULL,
  `projectCategory` varchar(45) NOT NULL,
  `projectGoals` varchar(450) NOT NULL,
  `projectStart` date NOT NULL,
  `projectEnd` date NOT NULL,
  PRIMARY KEY (`id`),
  KEY `userId_idx` (`userId`),
  CONSTRAINT `userId` FOREIGN KEY (`userId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`scenario` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `projectId` bigint(20) NOT NULL,
  `scenarioName` varchar(50) NOT NULL,
  `scenarioType` varchar(15) NOT NULL,
  `requirementType` varchar(255) NOT NULL,
  `priority` varchar(15) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `projectId_idx` (`projectId`),
  CONSTRAINT `projectId` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`tdinbuying` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) NOT NULL,
  `name` varchar(45) NOT NULL,
  `ROI` int(11) NOT NULL,
  `maxCapacity` bigint(20) NOT NULL,
  `currentUsers` bigint(20) NOT NULL,
  `demandRaise` decimal(15,3) NOT NULL,
  `subscriptionPrice` decimal(23,3) NOT NULL,
  `raiseSubscriptionPrice` decimal(15,3) NOT NULL,
  `cloudCost` decimal(23,3) NOT NULL,
  `raiseCloudCost` decimal(15,3) NOT NULL,
  `confidence` varchar(10) DEFAULT NULL,
  `serviceScalability` varchar(10) DEFAULT NULL,
  `QoS` varchar(10) DEFAULT NULL,
  `riskOfFutureTD` varchar(10) DEFAULT NULL,
  `realOptionsValuation` varchar(10) DEFAULT NULL,
  `justification` varchar(450) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `userId_idx` (`userId`),
  CONSTRAINT `TDuserId` FOREIGN KEY (`userId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`sharedbuying` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromUserId` bigint(20) NOT NULL,
  `toUserId` bigint(20) NOT NULL,
  `buyingId` bigint(20) NOT NULL,
  `dateShared` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `buyingFromUserId_idx` (`fromUserId`),
  KEY `buyingToUserId_idx` (`toUserId`),
  KEY `buyingId_idx` (`buyingId`),
  CONSTRAINT `buyingFromUserId` FOREIGN KEY (`fromUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `buyingId` FOREIGN KEY (`buyingId`) REFERENCES `tdinbuying` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `buyingToUserId` FOREIGN KEY (`toUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`sharedcocomo` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromUserId` bigint(20) NOT NULL,
  `toUserId` bigint(20) NOT NULL,
  `cocomoId` bigint(20) NOT NULL,
  `dateShared` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cocomoFromUserId_idx` (`fromUserId`),
  KEY `cocomoToUserId_idx` (`toUserId`),
  KEY `cocomoId_idx` (`cocomoId`),
  CONSTRAINT `cocomoFromUserId` FOREIGN KEY (`fromUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cocomoId` FOREIGN KEY (`cocomoId`) REFERENCES `cocomo` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cocomoToUserId` FOREIGN KEY (`toUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`sharedimplementing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromUserId` bigint(20) NOT NULL,
  `toUserId` bigint(20) NOT NULL,
  `implementingId` bigint(20) NOT NULL,
  `dateShared` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `implementFromUserId_idx` (`fromUserId`),
  KEY `implementToUserId_idx` (`toUserId`),
  KEY `implementId_idx` (`implementingId`),
  CONSTRAINT `implementFromUserId` FOREIGN KEY (`fromUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `implementId` FOREIGN KEY (`implementingId`) REFERENCES `costforimplementing` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `implementToUserId` FOREIGN KEY (`toUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;
CREATE TABLE `tdq`.`sharedproject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromUserId` bigint(20) NOT NULL,
  `toUserId` bigint(20) NOT NULL,
  `projectId` bigint(20) NOT NULL,
  `dateShared` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `fromId_idx` (`fromUserId`),
  KEY `toId_idx` (`toUserId`),
  KEY `projectId_idx` (`projectId`),
  CONSTRAINT `fromId` FOREIGN KEY (`fromUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sharedProjectId` FOREIGN KEY (`projectId`) REFERENCES `project` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `toId` FOREIGN KEY (`toUserId`) REFERENCES `userlogin` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

INSERT INTO `tdq`.`jobtitle` (`id`, `name`) VALUES ('1', 'Project Manager');
INSERT INTO `tdq`.`jobtitle` (`id`, `name`) VALUES ('2', 'Team Leader');
INSERT INTO `tdq`.`jobtitle` (`id`, `name`) VALUES ('3', 'Developer');
INSERT INTO `tdq`.`jobtitle` (`id`, `name`) VALUES ('4', 'Architect');