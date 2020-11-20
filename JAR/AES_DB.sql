CREATE DATABASE  IF NOT EXISTS `project` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `project`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: project
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activeexams`
--

DROP TABLE IF EXISTS `activeexams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activeexams` (
  `exId` varchar(45) NOT NULL,
  `lId` int(11) NOT NULL,
  `activeExam` int(11) NOT NULL,
  `lecturerRequestFlag` int(11) NOT NULL,
  `lecturerRequest` varchar(45) DEFAULT NULL,
  `principalAnswerFlag` int(11) DEFAULT NULL,
  `principalAnswer` varchar(45) DEFAULT NULL,
  `extraTime` int(11) DEFAULT NULL,
  `ableToAccess` int(11) DEFAULT '1',
  PRIMARY KEY (`exId`),
  KEY `FKExam34_idx` (`exId`),
  CONSTRAINT `FKExam34` FOREIGN KEY (`exId`) REFERENCES `exam` (`exid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activeexams`
--

LOCK TABLES `activeexams` WRITE;
/*!40000 ALTER TABLE `activeexams` DISABLE KEYS */;
INSERT INTO `activeexams` VALUES ('010101',1,0,0,NULL,0,NULL,0,1),('010201',3,0,0,NULL,0,NULL,0,1),('010302',1,0,0,NULL,0,NULL,0,0),('020801',2,0,0,NULL,0,NULL,0,1),('031001',4,0,0,NULL,0,NULL,0,1);
/*!40000 ALTER TABLE `activeexams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `changeexamtime`
--

DROP TABLE IF EXISTS `changeexamtime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `changeexamtime` (
  `lId` int(11) NOT NULL,
  `cId` int(11) NOT NULL,
  `exId` varchar(45) NOT NULL,
  `lReason` varchar(45) NOT NULL,
  `pAnswer` varchar(45) DEFAULT NULL,
  `approveOrDecline` int(11) DEFAULT NULL,
  `alreadyChecked` int(11) DEFAULT NULL,
  PRIMARY KEY (`lId`,`cId`,`exId`),
  KEY `FXExam12_idx` (`exId`),
  KEY `FKLecturer23_idx` (`cId`),
  CONSTRAINT `FKLecturer23` FOREIGN KEY (`cId`) REFERENCES `course` (`cid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKLecturerId2` FOREIGN KEY (`lId`) REFERENCES `lecturer` (`lid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FXExam12` FOREIGN KEY (`exId`) REFERENCES `exam` (`exid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `changeexamtime`
--

LOCK TABLES `changeexamtime` WRITE;
/*!40000 ALTER TABLE `changeexamtime` DISABLE KEYS */;
INSERT INTO `changeexamtime` VALUES (1,1,'010101','reason',NULL,0,0),(1,3,'010302','reason',NULL,0,0),(2,8,'020801','reason',NULL,0,0),(3,2,'010201','reason',NULL,0,0),(4,10,'031001','reason',NULL,0,0);
/*!40000 ALTER TABLE `changeexamtime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `course` (
  `cId` int(11) NOT NULL AUTO_INCREMENT,
  `cName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`cId`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,'matam'),(2,'malam'),(3,'atam'),(4,'algebra'),(5,'logic'),(6,'electricity'),(7,'physics'),(8,'voltag'),(9,'cellBiology'),(10,'chemistry'),(11,'probabillity');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courseindepartment`
--

DROP TABLE IF EXISTS `courseindepartment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `courseindepartment` (
  `cId` int(11) NOT NULL,
  `dId` int(11) NOT NULL,
  PRIMARY KEY (`cId`,`dId`),
  KEY `FKdepartment_idx` (`dId`),
  CONSTRAINT `FKcourse` FOREIGN KEY (`cId`) REFERENCES `course` (`cId`),
  CONSTRAINT `FKdepartment` FOREIGN KEY (`dId`) REFERENCES `department` (`did`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courseindepartment`
--

LOCK TABLES `courseindepartment` WRITE;
/*!40000 ALTER TABLE `courseindepartment` DISABLE KEYS */;
INSERT INTO `courseindepartment` VALUES (1,1),(2,1),(3,1),(4,1),(5,1),(6,2),(7,2),(8,2),(9,3),(10,3),(11,3);
/*!40000 ALTER TABLE `courseindepartment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coursestatistics`
--

DROP TABLE IF EXISTS `coursestatistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coursestatistics` (
  `cId` int(11) NOT NULL,
  `courseName` varchar(45) NOT NULL,
  `a` int(11) DEFAULT '0',
  `b` int(11) DEFAULT '0',
  `c` int(11) DEFAULT '0',
  `d` int(11) DEFAULT '0',
  `e` int(11) DEFAULT '0',
  `f` int(11) DEFAULT '0',
  `g` int(11) DEFAULT '0',
  `h` int(11) DEFAULT '0',
  `i` int(11) DEFAULT '0',
  `j` int(11) DEFAULT '0',
  PRIMARY KEY (`cId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coursestatistics`
--

LOCK TABLES `coursestatistics` WRITE;
/*!40000 ALTER TABLE `coursestatistics` DISABLE KEYS */;
INSERT INTO `coursestatistics` VALUES (1,'matam',0,0,0,1,0,0,1,0,0,2),(2,'malam',1,0,0,0,0,0,1,0,0,1),(3,'atam',1,0,0,0,0,0,0,0,0,1),(4,'algebra',0,0,0,0,0,0,0,0,0,0),(5,'logic',0,0,0,0,0,0,0,0,0,0),(6,'electricity',0,0,0,0,0,0,0,0,0,0),(7,'physics',0,0,0,0,0,0,0,0,0,0),(8,'voltag',0,0,0,0,1,0,0,1,0,1),(9,'cellBiology',0,0,0,0,0,0,0,0,0,0),(10,'chemistry',0,0,1,0,0,0,0,0,0,0),(11,'probabillity',0,0,0,0,0,0,0,0,0,0);
/*!40000 ALTER TABLE `coursestatistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `dId` int(11) NOT NULL AUTO_INCREMENT,
  `dName` varchar(45) NOT NULL,
  PRIMARY KEY (`dId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'software'),(2,'electrical'),(3,'biology');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doneexamdetails`
--

DROP TABLE IF EXISTS `doneexamdetails`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doneexamdetails` (
  `exid` varchar(45) NOT NULL,
  `date` varchar(45) DEFAULT NULL,
  `lengthExam` varchar(45) DEFAULT NULL,
  `studentStart` int(11) DEFAULT NULL,
  `studentFinishAlone` int(11) DEFAULT NULL,
  `studentFinishNotAlone` int(11) DEFAULT NULL,
  PRIMARY KEY (`exid`),
  CONSTRAINT `FKExamDone` FOREIGN KEY (`exid`) REFERENCES `exam` (`exid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doneexamdetails`
--

LOCK TABLES `doneexamdetails` WRITE;
/*!40000 ALTER TABLE `doneexamdetails` DISABLE KEYS */;
INSERT INTO `doneexamdetails` VALUES ('010101','Sat Jun 23 18:32:04 IDT 2018','120',4,4,0),('010201','Sat Jun 23 19:14:21 IDT 2018','90',3,3,0),('010302','Sat Jun 23 18:47:16 IDT 2018','160',2,2,0),('010401',NULL,NULL,NULL,NULL,NULL),('010501',NULL,NULL,NULL,NULL,NULL),('020603',NULL,NULL,NULL,NULL,NULL),('020701',NULL,NULL,NULL,NULL,NULL),('020801','Sat Jun 23 19:30:45 IDT 2018','120',3,3,0),('030901',NULL,NULL,NULL,NULL,NULL),('031001','Sat Jun 23 19:48:48 IDT 2018','120',2,1,1);
/*!40000 ALTER TABLE `doneexamdetails` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam`
--

DROP TABLE IF EXISTS `exam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exam` (
  `exId` varchar(45) NOT NULL,
  `cId` int(11) NOT NULL,
  `dId` int(11) NOT NULL,
  `lId` int(11) NOT NULL,
  `exCode` varchar(45) DEFAULT NULL,
  `timeInMin` int(11) DEFAULT NULL,
  `lecInst` varchar(45) DEFAULT NULL,
  `studInst` varchar(45) DEFAULT NULL,
  `author` varchar(45) DEFAULT NULL,
  `exDone` int(11) DEFAULT NULL,
  PRIMARY KEY (`exId`,`cId`,`dId`,`lId`),
  KEY `FKExemCourse_idx` (`cId`),
  KEY `FKExemDep_idx` (`dId`),
  KEY `FKLecturerExamBuild_idx` (`lId`),
  CONSTRAINT `FKDepartmentExam` FOREIGN KEY (`dId`) REFERENCES `department` (`dId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKExemCourse` FOREIGN KEY (`cId`) REFERENCES `course` (`cId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKExemDep` FOREIGN KEY (`dId`) REFERENCES `department` (`dId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam`
--

LOCK TABLES `exam` WRITE;
/*!40000 ALTER TABLE `exam` DISABLE KEYS */;
INSERT INTO `exam` VALUES ('010101',1,1,1,'0101',120,'good luck','Answer all the questions','Ilan',0),('010201',2,1,3,'0201',90,'For futher instruction ake for Orna','Answer all the questions','Mor',0),('010302',3,1,3,'0302',160,'Funrun is a grate game','Answer all questions','Mor',0),('010401',4,1,3,'----',60,'Ask Marina for further help','Answer all the questions','Mor',0),('010501',5,1,1,'----',180,'Call Eli for further instructions','Answer all question','Ilan',0),('020603',6,2,2,'----',100,'Good luck ','Answer all the questions','Meir',0),('020701',7,2,2,'----',120,'For further instructions ask shimshon','Answer all questions','Meir',0),('020801',8,2,2,'0801',120,'for further questions ask mr aviv goldstein','Answer all the questions','Meir',0),('030901',9,3,4,'----',120,'Good Luck','Answer all the questions','Yan',0),('031001',10,3,4,'1001',120,'For further questions go to google','Answer all the questions','Yan',0);
/*!40000 ALTER TABLE `exam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finishedexams`
--

DROP TABLE IF EXISTS `finishedexams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finishedexams` (
  `sId` int(11) NOT NULL,
  `cId` int(11) NOT NULL,
  `exId` varchar(45) NOT NULL,
  `grade` int(11) DEFAULT NULL,
  `started` int(11) DEFAULT NULL,
  `finished` int(11) DEFAULT NULL,
  `time` varchar(45) DEFAULT NULL,
  `authorId` varchar(45) NOT NULL,
  `courseName` varchar(45) NOT NULL,
  `readyToView` int(11) DEFAULT NULL,
  `changeGradeReason` varchar(45) DEFAULT NULL,
  `ongoing` int(11) DEFAULT '0',
  PRIMARY KEY (`sId`,`cId`,`exId`),
  KEY `FKCoures23_idx` (`cId`),
  KEY `FKExam345_idx` (`exId`),
  CONSTRAINT `FKCoures23` FOREIGN KEY (`cId`) REFERENCES `course` (`cId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKExam345` FOREIGN KEY (`exId`) REFERENCES `exam` (`exId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKStudent234` FOREIGN KEY (`sId`) REFERENCES `student` (`sid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finishedexams`
--

LOCK TABLES `finishedexams` WRITE;
/*!40000 ALTER TABLE `finishedexams` DISABLE KEYS */;
INSERT INTO `finishedexams` VALUES (1,1,'010101',100,1,1,'12','10','matam',1,NULL,0),(1,3,'010302',100,1,1,'12','12','atam',1,NULL,0),(2,1,'010101',100,1,1,'8','10','matam',1,NULL,0),(2,2,'010201',0,1,1,'19','12','malam',1,NULL,0),(3,1,'010101',40,1,1,'7','10','matam',1,NULL,0),(3,2,'010201',63,1,1,'11','12','malam',1,NULL,0),(3,3,'010302',NULL,1,1,'7','12','atam',0,NULL,0),(4,8,'020801',45,1,1,'9','11','voltag',1,NULL,0),(5,8,'020801',75,1,1,'8','11','voltag',1,NULL,0),(6,8,'020801',100,1,1,'31','11','voltag',1,NULL,0),(7,10,'031001',25,1,1,'53','13','chemistry',1,NULL,0),(8,10,'031001',30,1,0,'178','13','chemistry',0,NULL,0),(9,1,'010101',70,1,1,'10','10','matam',1,NULL,0),(9,2,'010201',100,1,1,'18','12','malam',1,NULL,0),(9,3,'010302',0,1,1,'5','12','atam',1,NULL,0);
/*!40000 ALTER TABLE `finishedexams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `finishedquestions`
--

DROP TABLE IF EXISTS `finishedquestions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `finishedquestions` (
  `sId` int(11) NOT NULL,
  `cId` int(11) NOT NULL,
  `exId` varchar(45) NOT NULL,
  `qId` varchar(45) NOT NULL,
  `ansStId` int(11) DEFAULT NULL,
  `corAns` int(11) DEFAULT NULL,
  `notes` varchar(45) DEFAULT NULL,
  `Grade` int(11) DEFAULT NULL,
  PRIMARY KEY (`sId`,`cId`,`exId`,`qId`),
  KEY `FKStudentExamCourse_idx` (`cId`),
  KEY `FKExamid_idx` (`exId`),
  KEY `FKQuestionid_idx` (`qId`),
  KEY `FKcorrectAns_idx` (`corAns`),
  CONSTRAINT `FKExamid` FOREIGN KEY (`exId`) REFERENCES `exam` (`exId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKQuestionid` FOREIGN KEY (`qId`) REFERENCES `question` (`qid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKStudentExamCopy` FOREIGN KEY (`sId`) REFERENCES `student` (`sid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKStudentExamCourse` FOREIGN KEY (`cId`) REFERENCES `studentincourse` (`cid`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKcorrectAns` FOREIGN KEY (`corAns`) REFERENCES `question` (`cid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `finishedquestions`
--

LOCK TABLES `finishedquestions` WRITE;
/*!40000 ALTER TABLE `finishedquestions` DISABLE KEYS */;
INSERT INTO `finishedquestions` VALUES (1,1,'010101','01001',2,NULL,'true',15),(1,1,'010101','01002',4,NULL,'correct',25),(1,1,'010101','01003',1,NULL,'good work',30),(1,1,'010101','01004',1,NULL,'super',30),(1,3,'010302','03007',2,NULL,'correct',5),(1,3,'010302','03008',4,NULL,'true',15),(1,3,'010302','03027',1,NULL,'great!',40),(1,3,'010302','03036',4,NULL,'true',40),(2,1,'010101','01001',2,NULL,'good answer',15),(2,1,'010101','01002',4,NULL,'thats right',25),(2,1,'010101','01003',1,NULL,'super',30),(2,1,'010101','01004',1,NULL,'correct',30),(2,2,'010201','02005',2,NULL,'not true',0),(2,2,'010201','02006',4,NULL,'wrong answer',0),(2,2,'010201','02034',2,NULL,'not true',0),(2,2,'010201','02035',1,NULL,'false answer',0),(3,1,'010101','01001',2,NULL,'correct',15),(3,1,'010101','01002',4,NULL,'true',25),(3,1,'010101','01003',3,NULL,'wrong, review the material',0),(3,1,'010101','01004',2,NULL,'not true',0),(3,2,'010201','02005',3,NULL,'review the material again',0),(3,2,'010201','02006',2,NULL,'wrong answer',0),(3,2,'010201','02034',3,NULL,'correct',33),(3,2,'010201','02035',3,NULL,'true',30),(4,8,'020801','08018',3,NULL,'that\'s right',20),(4,8,'020801','08019',4,NULL,'wrong answer mate',0),(4,8,'020801','08030',3,NULL,'correct!',25),(4,8,'020801','08031',2,NULL,'not true',0),(5,8,'020801','08018',3,NULL,'true',20),(5,8,'020801','08019',2,NULL,'super',20),(5,8,'020801','08030',4,NULL,'not good',0),(5,8,'020801','08031',3,NULL,'correct',35),(6,8,'020801','08018',3,NULL,'good',20),(6,8,'020801','08019',2,NULL,'great',20),(6,8,'020801','08030',3,NULL,'thats good',25),(6,8,'020801','08031',3,NULL,'super!',35),(7,10,'031001','10030',2,NULL,'not good',0),(7,10,'031001','10031',3,NULL,'super',25),(7,10,'031001','10032',2,NULL,'that is incorrect',0),(7,10,'031001','10033',2,NULL,'no true',0),(9,1,'010101','01001',2,NULL,'thats right',15),(9,1,'010101','01002',4,NULL,'correct',25),(9,1,'010101','01003',1,NULL,'true',30),(9,1,'010101','01004',4,NULL,'not true',0),(9,2,'010201','02005',1,NULL,'good work',12),(9,2,'010201','02006',3,NULL,'true',25),(9,2,'010201','02034',3,NULL,'correct',33),(9,2,'010201','02035',3,NULL,'super',30);
/*!40000 ALTER TABLE `finishedquestions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturer`
--

DROP TABLE IF EXISTS `lecturer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lecturer` (
  `lId` int(11) NOT NULL,
  `uId` int(11) NOT NULL,
  `dId` int(11) NOT NULL,
  PRIMARY KEY (`lId`),
  KEY `FKteacher_idx` (`uId`),
  KEY `FKdepartmentlec_idx` (`dId`),
  CONSTRAINT `FKdepartmentlec` FOREIGN KEY (`dId`) REFERENCES `department` (`dId`),
  CONSTRAINT `FKlecturer` FOREIGN KEY (`uId`) REFERENCES `user` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturer`
--

LOCK TABLES `lecturer` WRITE;
/*!40000 ALTER TABLE `lecturer` DISABLE KEYS */;
INSERT INTO `lecturer` VALUES (1,10,1),(2,11,2),(3,12,1),(4,13,3);
/*!40000 ALTER TABLE `lecturer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturerincourse`
--

DROP TABLE IF EXISTS `lecturerincourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lecturerincourse` (
  `lId` int(11) NOT NULL,
  `cId` int(11) NOT NULL,
  PRIMARY KEY (`lId`,`cId`),
  KEY `FKCid_idx` (`cId`),
  CONSTRAINT `FKCid` FOREIGN KEY (`cId`) REFERENCES `course` (`cId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKLid` FOREIGN KEY (`lId`) REFERENCES `lecturer` (`lId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturerincourse`
--

LOCK TABLES `lecturerincourse` WRITE;
/*!40000 ALTER TABLE `lecturerincourse` DISABLE KEYS */;
INSERT INTO `lecturerincourse` VALUES (1,1),(3,1),(3,2),(1,3),(3,3),(3,4),(1,5),(2,6),(2,7),(2,8),(4,9),(4,10),(4,11);
/*!40000 ALTER TABLE `lecturerincourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lecturerstatistics`
--

DROP TABLE IF EXISTS `lecturerstatistics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lecturerstatistics` (
  `lId` int(11) NOT NULL,
  `a` int(11) DEFAULT '0',
  `b` int(11) DEFAULT '0',
  `c` int(11) DEFAULT '0',
  `d` int(11) DEFAULT '0',
  `e` int(11) DEFAULT '0',
  `f` int(11) DEFAULT '0',
  `g` int(11) DEFAULT '0',
  `h` int(11) DEFAULT '0',
  `i` int(11) DEFAULT '0',
  `j` int(11) DEFAULT '0',
  PRIMARY KEY (`lId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lecturerstatistics`
--

LOCK TABLES `lecturerstatistics` WRITE;
/*!40000 ALTER TABLE `lecturerstatistics` DISABLE KEYS */;
INSERT INTO `lecturerstatistics` VALUES (1,1,0,0,1,0,0,1,0,0,3),(2,0,0,0,0,1,0,0,1,0,1),(3,1,0,0,0,0,0,1,0,0,1),(4,0,0,1,0,0,0,0,0,0,0);
/*!40000 ALTER TABLE `lecturerstatistics` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `question` (
  `qId` varchar(45) NOT NULL,
  `cId` int(11) NOT NULL,
  `authorQues` varchar(45) NOT NULL,
  `notes` varchar(45) DEFAULT NULL,
  `quesText` varchar(45) NOT NULL,
  `ans1` varchar(45) NOT NULL,
  `ans2` varchar(45) NOT NULL,
  `ans3` varchar(45) NOT NULL,
  `ans4` varchar(45) NOT NULL,
  `correctAns` int(11) NOT NULL,
  PRIMARY KEY (`qId`),
  KEY `FKCourseQues_idx` (`cId`),
  CONSTRAINT `FKCourseQues` FOREIGN KEY (`cId`) REFERENCES `course` (`cId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES ('01001',1,'ilan','Select the correct answer','2+2 = ?','3','4','5','6',2),('01002',1,'mor','Select the correct answer','5+5 = ? ','7','8','9','10',4),('01003',1,'ilan','Select the correct answer','what is the value of PIE?','3.124','4.23','4.21','4.1',1),('01004',1,'mor','Select the correct answer','8 + 8 = ?','16','15','22','12',1),('02005',2,'mor','Select the correct answer','10 + 2 = ?','12','2','13','10',1),('02006',2,'mor','Select the correct answer','15 + 10 = ?','2','6','25','24',3),('02034',2,'Mor','Select the correct answer','What is bigger ?','Int','Float','Double','Unsingn int',3),('02035',2,'Mor','Select the correct answer','Which sort algorithem is the fastest?','Quick Sort','Bubble Sort','Merge Sort','Selection Sort',3),('03007',3,'ilan','Select the correct answer','10 + 12 = ?','21','22','24','25',2),('03008',3,'mor','Select the correct answer','1 * 8 = ?','1','3','5','8',4),('03027',3,'Ilan','Answer','ADT=?','Abstract data type','All data typical','Any day tobleron','Aye di ti?',1),('03036',3,'Mor','Select the correct answer','What the MOV ax,bx comand does?','add ax to bx','add bx to ax','move ax to bx','move bx to ax',4),('04009',4,'mor','Select the correct answer','7 * 7 = ?','50','49','29','2',2),('04010',4,'ilan','Select the correct answer','6 * 6 = ?','56','67','36','8',3),('04011',4,'ilan','Select the correct answer','8 + 1 = ?','9','10','12','4',1),('04037',4,'Mor','Select the correct answer','How many roots to x^2?','2','3','0','1',1),('05012',5,'ilan','Select the correct answer','4 + 4 = ? ','5','6','7','8',4),('05038',5,'Ilan','Select the correct answer','Fairies or dragons?','Fairy','Dragons','Ask Eli Bar Yehalom','Orks',4),('05039',5,'Ilan','Select the correct answer','Best method to answer a question','Inception mode','The purple marker method','The goggles ','Dragons',2),('05040',5,'Ilan','Select the correct question','What came first?','Egg','Dragon','Karl Marks','Kalisi',4),('06013',6,'meir','Select the correct answer','what is 1HZ?','1 time in a second','1 time in a minute','1 time in a hour','1 time in a day',1),('06014',6,'meir','Select the correct answer','what is 100MHZ?','1M time in a day','1M time in a hour','1M time in a minute','1M time in a second',4),('06028',6,'Meir','Selecto the correct question','Who Discovered Electricity?','Benjamin Button','Benjamin Netanyahu','Benjamin Franklin','Carl Marks',3),('06029',6,'Meir','Select the correct answer','What is a Dinamo','A board game','A power generator','A movie','A cocktail',2),('06041',6,'Meir','ddd','ddd','dd','dddd','ddddd','ddddd',1),('07015',7,'meir','Select the correct answer','G force = ?','9.5','9.4','9.8','8.9',3),('07016',7,'meir','Select the correct answer','Ratio between Mass to Energy is  ?','E = M*C*C','M= E+C+C','C=M*E','E=M*C',1),('07017',7,'meir','Select the correct answer','what happens if we drop a weight in space?','nothig','fly','move right','fall down',4),('07029',7,'Meir','Select the correct question','Acceleration = ','F/M','M/F','G/H','T/M',1),('08018',8,'meir','Select the correct answer','what is ohm\'s law?','V = R \\ I','R = V * I','V = R * I','I = V * R',3),('08019',8,'meir','Select the correct answer','what is the value of volte in isreal ? ','230','220','110','400',2),('08030',8,'Meir','Select the correct answer','What is Watt?','What ?','A nickname for a singer','A unit of power','A Slang',3),('08031',8,'Meir','Selece the correct answer','What is bigger?','Attowatt','Femtowatt','Microwatt','Picowatt',3),('09026',9,'Yan','Select the correct answer','Waht is Mithocondria','Cell reproduction','A virus cell','And organ','An arm',1),('09027',9,'Yan','Select the corerct answer','What is the down syndrom?','XY chromosome','X chromosome','Z chromosome','Y chromosome',4),('09028',9,'Yan','Select the correct answer','What is the rate of cell devidion ?','x^2','e^x','x^e','2^x',4),('09029',9,'Yan','Select the correct answer','Who discovered the cell ?','Robert Langdon','Robert Hooke','Roberto Carlos','Rober De Niro',2),('10030',10,'Yan','Select the correct answer','How much salt is in the average human body?','1 kilogram','500 grams','250 grams','Practicly none',3),('10031',10,'Yan','Select the correc answer','Fill glass with ice water , what will happen?','The glass will over flow','The level in the glass will remain unchanged','The water level will drop  as the ice melts.','I\'ll drink the water before anything happens.',3),('10032',10,'Yan','Select the correct answer','What is the most protein in the human body?','Tubulin','Albumin','Keratin','Collagen',4),('10033',10,'Yan','Select the correct answer','What is its chemical formula of water?','H2','O2','H2O','H2O2',3);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questioninexam`
--

DROP TABLE IF EXISTS `questioninexam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `questioninexam` (
  `qid` varchar(45) NOT NULL,
  `exId` varchar(45) NOT NULL,
  `score` int(11) DEFAULT NULL,
  PRIMARY KEY (`qid`,`exId`),
  KEY `FKExam_idx` (`exId`),
  CONSTRAINT `FKExam12` FOREIGN KEY (`exId`) REFERENCES `exam` (`exId`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKQuestionInExam` FOREIGN KEY (`qid`) REFERENCES `question` (`qId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questioninexam`
--

LOCK TABLES `questioninexam` WRITE;
/*!40000 ALTER TABLE `questioninexam` DISABLE KEYS */;
INSERT INTO `questioninexam` VALUES ('01001','010101',15),('01002','010101',25),('01003','010101',30),('01004','010101',30),('02005','010201',12),('02006','010201',25),('02034','010201',33),('02035','010201',30),('03007','010302',5),('03008','010302',15),('03027','010302',40),('03036','010302',40),('04009','010401',14),('04010','010401',12),('04011','010401',20),('04037','010401',2),('05012','010501',10),('05038','010501',25),('05039','010501',35),('05040','010501',30),('06013','020603',25),('06014','020603',25),('06028','020603',25),('06029','020603',25),('07015','020701',25),('07016','020701',25),('07017','020701',25),('07029','020701',25),('08018','020801',20),('08019','020801',20),('08030','020801',25),('08031','020801',35),('09026','030901',20),('09027','030901',25),('09028','030901',35),('09029','030901',20),('10030','031001',15),('10031','031001',25),('10032','031001',30),('10033','031001',30);
/*!40000 ALTER TABLE `questioninexam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reportexam`
--

DROP TABLE IF EXISTS `reportexam`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reportexam` (
  `exId` varchar(45) NOT NULL,
  `avgExam` float DEFAULT NULL,
  `medianExam` float DEFAULT NULL,
  PRIMARY KEY (`exId`),
  CONSTRAINT `FKExamreport` FOREIGN KEY (`exId`) REFERENCES `exam` (`exId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reportexam`
--

LOCK TABLES `reportexam` WRITE;
/*!40000 ALTER TABLE `reportexam` DISABLE KEYS */;
INSERT INTO `reportexam` VALUES ('010101',77.5,70),('010201',54.3333,63),('010302',50,0),('020801',73.3333,75),('031001',27.5,27.5);
/*!40000 ALTER TABLE `reportexam` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `student` (
  `sId` int(11) NOT NULL AUTO_INCREMENT,
  `uId` int(11) NOT NULL,
  `dId` int(11) NOT NULL,
  `avg` float DEFAULT NULL,
  PRIMARY KEY (`sId`),
  KEY `FKstudent_idx` (`uId`),
  KEY `FKStudentDep_idx` (`dId`),
  CONSTRAINT `FKStudentDep` FOREIGN KEY (`dId`) REFERENCES `department` (`dId`),
  CONSTRAINT `FKStudentId` FOREIGN KEY (`uId`) REFERENCES `user` (`uid`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (1,1,1,100),(2,2,1,50),(3,3,1,51.5),(4,4,2,45),(5,5,2,75),(6,6,2,100),(7,7,3,25),(8,8,3,0),(9,9,1,56.6667);
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `studentincourse`
--

DROP TABLE IF EXISTS `studentincourse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `studentincourse` (
  `sId` int(11) NOT NULL,
  `cId` int(11) NOT NULL,
  PRIMARY KEY (`cId`,`sId`),
  KEY `FKstudentInCourse_idx` (`sId`),
  CONSTRAINT `FKCourse123` FOREIGN KEY (`cId`) REFERENCES `course` (`cId`),
  CONSTRAINT `FKstudentInCourse` FOREIGN KEY (`sId`) REFERENCES `student` (`sId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `studentincourse`
--

LOCK TABLES `studentincourse` WRITE;
/*!40000 ALTER TABLE `studentincourse` DISABLE KEYS */;
INSERT INTO `studentincourse` VALUES (1,1),(1,2),(1,3),(2,1),(2,2),(2,4),(2,5),(3,1),(3,2),(3,3),(3,4),(4,6),(4,7),(4,8),(5,6),(5,7),(5,8),(6,7),(6,8),(7,9),(7,10),(7,11),(8,9),(8,10),(8,11),(9,1),(9,2),(9,3),(9,4),(9,5);
/*!40000 ALTER TABLE `studentincourse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `uId` int(11) NOT NULL AUTO_INCREMENT,
  `Id` varchar(45) DEFAULT NULL,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `isLogged` varchar(45) DEFAULT NULL,
  `Permission` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`uId`),
  UNIQUE KEY `Id_UNIQUE` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'1','Eliran','1','0','Limited'),(2,'2','Roni','2','0','Limited'),(3,'3','Ron','3','0','Limited'),(4,'4','Aviv','4','0','Limited'),(5,'5','Andrey','5','0','Limited'),(6,'6','Or','6','0','Limited'),(7,'7','Elias','7','0','Limited'),(8,'8','Tzach','8','0','Limited'),(9,'9','Vladi','9','0','Limited'),(10,'10','Ilan','10','0','Limited'),(11,'11','Meir','11','0','Limited'),(12,'12','Mor','12','0','Limited'),(13,'13','Yan','13','0','Limited'),(14,'14','Din','14','0','Administrator');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-06-24 11:26:44
