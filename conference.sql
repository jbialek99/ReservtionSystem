-- MySQL dump 10.13  Distrib 9.2.0, for Linux (x86_64)
--
-- Host: localhost    Database: conference
-- ------------- -----------------------------------------
-- Server version	9.2.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `buildings`
--

DROP TABLE IF EXISTS `buildings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `buildings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `localization_id` bigint DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk9rgbbvnnmm917hj9w2khjowy` (`localization_id`),
  CONSTRAINT `FKk9rgbbvnnmm917hj9w2khjowy` FOREIGN KEY (`localization_id`) REFERENCES `localizations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buildings`
--

LOCK TABLES `buildings` WRITE;
/*!40000 ALTER TABLE `buildings` DISABLE KEYS */;
INSERT INTO `buildings` VALUES (1,1,'Główne biuro','Budynek A'),(2,2,'Centrum konferencyjne','Budynek B'),(3,3,'Nowoczesny kompleks','Budynek C'),(4,4,'Biura i sale spotkań','Budynek D'),(5,5,'Siedziba zarządu','Budynek E'),(6,2,'Centrum konferencyjne','Budynek B'),(7,2,'Hala Expo','Budynek J'),(8,2,'Nowoczesne biuro','Budynek K'),(9,2,'Stary Ratusz','Budynek L'),(10,2,'Obiekt sportowy','Budynek M'),(11,3,'Nowoczesny kompleks','Budynek C'),(12,3,'Biurowiec Delta','Budynek N'),(13,3,'Technopark','Budynek O'),(14,3,'Centrum Innowacji','Budynek P'),(15,3,'Hala Przemysłowa','Budynek Q'),(16,4,'Biura i sale spotkań','Budynek D'),(17,4,'Wieżowiec SkyTower','Budynek R'),(18,4,'Nowe Centrum','Budynek S'),(19,4,'Akademik','Budynek T'),(20,4,'Stacja badawcza','Budynek U'),(21,5,'Siedziba zarządu','Budynek E'),(22,5,'Dom Handlowy','Budynek V'),(23,5,'Kompleks Rekreacyjny','Budynek W'),(24,5,'Galeria Handlowa','Budynek X'),(25,5,'Hotel i biura','Budynek Y');
/*!40000 ALTER TABLE `buildings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `halls`
--

DROP TABLE IF EXISTS `halls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `halls` (
  `available` bit(1) NOT NULL,
  `building_id` bigint DEFAULT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `capacity` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKowc82hok5hwtuhrhaxw1tok55` (`building_id`),
  CONSTRAINT `FKowc82hok5hwtuhrhaxw1tok55` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `halls`
--

LOCK TABLES `halls` WRITE;
/*!40000 ALTER TABLE `halls` DISABLE KEYS */;
INSERT INTO `halls` VALUES (_binary '',1,1,'50','Sala 101','Konferencyjna','Sala do spotkań'),(_binary '',2,2,'30','Sala 202','Szkoleniowa','Sala do warsztatów'),(_binary '\0',3,3,'20','Sala 303','Spotkań','Sala do konferencji'),(_binary '',4,4,'100','Sala 404','Wykładowa','Sala do spotkań'),(_binary '\0',5,5,'200','Sala 505','Audytoryjna','Sala do spotkań'),(_binary '',1,6,'50','Sala 101','Konferencyjna','Sala do konferencji'),(_binary '',1,7,'30','Sala 102','Warsztatowa','Sala do warsztatów'),(_binary '\0',1,8,'20','Sala 103','Spotkań','Sala do spotkań'),(_binary '',1,9,'100','Sala 104','Wykładowa','Sala do wykładów'),(_binary '\0',1,10,'200','Sala 105','Audytoryjna','Duża sala audytoryjna'),(_binary '',2,11,'50','Sala 201','Konferencyjna','Sala do konferencji'),(_binary '',2,12,'30','Sala 202','Warsztatowa','Sala do warsztatów'),(_binary '\0',2,13,'20','Sala 203','Spotkań','Sala do spotkań'),(_binary '',2,14,'100','Sala 204','Wykładowa','Sala do wykładów'),(_binary '\0',2,15,'200','Sala 205','Audytoryjna','Duża sala audytoryjna'),(_binary '',3,16,'50','Sala 301','Konferencyjna','Sala do konferencji'),(_binary '',3,17,'30','Sala 302','Warsztatowa','Sala do warsztatów'),(_binary '\0',3,18,'20','Sala 303','Spotkań','Sala do spotkań'),(_binary '',3,19,'100','Sala 304','Wykładowa','Sala do wykładów'),(_binary '\0',3,20,'200','Sala 305','Audytoryjna','Duża sala audytoryjna'),(_binary '',4,21,'50','Sala 401','Konferencyjna','Sala do konferencji'),(_binary '',4,22,'30','Sala 402','Warsztatowa','Sala do warsztatów'),(_binary '\0',4,23,'20','Sala 403','Spotkań','Sala do spotkań'),(_binary '',4,24,'100','Sala 404','Wykładowa','Sala do wykładów'),(_binary '\0',4,25,'200','Sala 405','Audytoryjna','Duża sala audytoryjna'),(_binary '',5,26,'50','Sala 501','Konferencyjna','Sala do konferencji'),(_binary '',5,27,'30','Sala 502','Warsztatowa','Sala do warsztatów'),(_binary '\0',5,28,'20','Sala 503','Spotkań','Sala do spotkań'),(_binary '',5,29,'100','Sala 504','Wykładowa','Sala do wykładów'),(_binary '\0',5,30,'200','Sala 505','Audytoryjna','Duża sala audytoryjna'),(_binary '',6,31,'50','Sala 601','Konferencyjna','Sala do konferencji'),(_binary '',6,32,'30','Sala 602','Warsztatowa','Sala do warsztatów'),(_binary '\0',6,33,'20','Sala 603','Spotkań','Sala do spotkań'),(_binary '',6,34,'100','Sala 604','Wykładowa','Sala do wykładów'),(_binary '\0',6,35,'200','Sala 605','Audytoryjna','Duża sala audytoryjna'),(_binary '',7,36,'50','Sala 701','Konferencyjna','Sala do konferencji'),(_binary '',7,37,'30','Sala 702','Warsztatowa','Sala do warsztatów'),(_binary '\0',7,38,'20','Sala 703','Spotkań','Sala do spotkań'),(_binary '',7,39,'100','Sala 704','Wykładowa','Sala do wykładów'),(_binary '\0',7,40,'200','Sala 705','Audytoryjna','Duża sala audytoryjna'),(_binary '',8,41,'50','Sala 801','Konferencyjna','Sala do konferencji'),(_binary '',8,42,'30','Sala 802','Warsztatowa','Sala do warsztatów'),(_binary '\0',8,43,'20','Sala 803','Spotkań','Sala do spotkań'),(_binary '',8,44,'100','Sala 804','Wykładowa','Sala do wykładów'),(_binary '\0',8,45,'200','Sala 805','Audytoryjna','Duża sala audytoryjna'),(_binary '',9,46,'50','Sala 901','Konferencyjna','Sala do konferencji'),(_binary '',9,47,'30','Sala 902','Warsztatowa','Sala do warsztatów'),(_binary '\0',9,48,'20','Sala 903','Spotkań','Sala do spotkań'),(_binary '',9,49,'100','Sala 904','Wykładowa','Sala do wykładów'),(_binary '\0',9,50,'200','Sala 905','Audytoryjna','Duża sala audytoryjna'),(_binary '',10,51,'50','Sala 1001','Konferencyjna','Sala do konferencji'),(_binary '',10,52,'30','Sala 1002','Warsztatowa','Sala do warsztatów'),(_binary '\0',10,53,'20','Sala 1003','Spotkań','Sala do spotkań'),(_binary '',10,54,'100','Sala 1004','Wykładowa','Sala do wykładów'),(_binary '\0',10,55,'200','Sala 1005','Audytoryjna','Duża sala audytoryjna'),(_binary '',11,56,'50','Sala 1101','Konferencyjna','Sala do konferencji'),(_binary '',11,57,'30','Sala 1102','Warsztatowa','Sala do warsztatów'),(_binary '\0',11,58,'20','Sala 1103','Spotkań','Sala do spotkań'),(_binary '',11,59,'100','Sala 1104','Wykładowa','Sala do wykładów'),(_binary '\0',11,60,'200','Sala 1105','Audytoryjna','Duża sala audytoryjna'),(_binary '',12,61,'50','Sala 1201','Konferencyjna','Sala do konferencji'),(_binary '',12,62,'30','Sala 1202','Warsztatowa','Sala do warsztatów'),(_binary '\0',12,63,'20','Sala 1203','Spotkań','Sala do spotkań'),(_binary '',12,64,'100','Sala 1204','Wykładowa','Sala do wykładów'),(_binary '\0',12,65,'200','Sala 1205','Audytoryjna','Duża sala audytoryjna'),(_binary '',13,66,'50','Sala 1301','Konferencyjna','Sala do konferencji'),(_binary '',13,67,'30','Sala 1302','Warsztatowa','Sala do warsztatów'),(_binary '\0',13,68,'20','Sala 1303','Spotkań','Sala do spotkań'),(_binary '',13,69,'100','Sala 1304','Wykładowa','Sala do wykładów'),(_binary '\0',13,70,'200','Sala 1305','Audytoryjna','Duża sala audytoryjna'),(_binary '',14,71,'50','Sala 1401','Konferencyjna','Sala do konferencji'),(_binary '',14,72,'30','Sala 1402','Warsztatowa','Sala do warsztatów'),(_binary '\0',14,73,'20','Sala 1403','Spotkań','Sala do spotkań'),(_binary '',14,74,'100','Sala 1404','Wykładowa','Sala do wykładów'),(_binary '\0',14,75,'200','Sala 1405','Audytoryjna','Duża sala audytoryjna'),(_binary '',15,76,'50','Sala 1501','Konferencyjna','Sala do konferencji'),(_binary '',15,77,'30','Sala 1502','Warsztatowa','Sala do warsztatów'),(_binary '\0',15,78,'20','Sala 1503','Spotkań','Sala do spotkań'),(_binary '',15,79,'100','Sala 1504','Wykładowa','Sala do wykładów'),(_binary '\0',15,80,'200','Sala 1505','Audytoryjna','Duża sala audytoryjna'),(_binary '',16,81,'50','Sala 1601','Sala do wykładów','Sala do konferencji'),(_binary '\0',16,82,'15','Sala 1602','Nowoczesne laboratorium komputerowe','Sala do warsztatów'),(_binary '',16,83,'30','Sala 1603','Duża sala audytoryjna','Sala do konferencji'),(_binary '\0',16,84,'20','Sala 1604','Sala do szkoleń i warsztatów','Sala do wykładów'),(_binary '',16,85,'10','Sala 1605','Sala do seminariów','Sala do warsztatów'),(_binary '\0',17,86,'30','Sala 1','Audytoryjna','Sala do spotkań'),(_binary '',17,87,'200','Sala 2','Warsztatowa','Sala do konferencji'),(_binary '\0',17,88,'30','Sala 3','Audytoryjna','Sala do spotkań'),(_binary '',17,89,'30','Sala 4','Warsztatowa','Sala do konferencji'),(_binary '',17,90,'10','Sala 5','Audytoryjna','Sala do spotkań'),(_binary '',18,91,'10','Sala 6','Nowoczesne laboratorium komputerowe','Sala do konferencji'),(_binary '\0',18,92,'200','Sala 7','Audytoryjna','Duża sala audytoryjna'),(_binary '\0',18,93,'30','Sala 8','Nowoczesne laboratorium komputerowe','Sala do konferencji'),(_binary '',18,94,'30','Sala 9','Audytoryjna','Duża sala audytoryjna'),(_binary '',18,95,'200','Sala 10','Nowoczesne laboratorium komputerowe','Sala do konferencji'),(_binary '\0',19,96,'30','Sala 15','Audytoryjna','Duża sala audytoryjna'),(_binary '',19,97,'200','Sala 16','Nowoczesne laboratorium komputerowe','Sala do konferencji'),(_binary '',20,98,'30','Sala 1','Wykładowa','Sala do wykładów'),(_binary '\0',20,99,'20','Sala 2','Konferencyjna','Sala do konferencji'),(_binary '',20,100,'200','Sala 3','Spotkań','Sala do spotkań'),(_binary '',20,101,'20','Sala 4','Wykładowa','Sala do wykładów'),(_binary '\0',20,102,'30','Sala 5','Konferencyjna','Sala do konferencji'),(_binary '',21,103,'10','Sala 6','Konferencyjna','Sala do konferencji'),(_binary '',21,104,'10','Sala 7','Wykładowa','Sala do wykładów'),(_binary '',21,105,'30','Sala 8','Spotkań','Sala do spotkań'),(_binary '\0',21,106,'200','Sala 9','Spotkań','Sala do spotkań'),(_binary '',21,107,'20','Sala 10','Konferencyjna','Sala do konferencji'),(_binary '',22,108,'10','Sala 15','Spotkań','Sala do spotkań'),(_binary '',22,109,'10','Sala 16','Wykładowa','Sala do wykładów'),(_binary '\0',22,110,'30','Sala 1','Spotkań','Sala do spotkań'),(_binary '',22,111,'10','Sala 2','Konferencyjna','Sala do konferencji'),(_binary '',23,112,'10','Sala 3','Spotkań','Sala do spotkań'),(_binary '\0',23,113,'30','Sala 4','Wykładowa','Sala do wykładów'),(_binary '',23,114,'20','Sala 5','Spotkań','Sala do spotkań'),(_binary '\0',23,115,'10','Sala 6','Konferencyjna','Sala do konferencji'),(_binary '',23,116,'200','Sala 7','Spotkań','Sala do spotkań'),(_binary '\0',23,117,'30','Sala 8','Wykładowa','Sala do wykładów'),(_binary '',24,118,'10','Sala 9','Spotkań','Sala do spotkań'),(_binary '\0',24,119,'20','Sala 10','Konferencyjna','Sala do konferencji'),(_binary '',24,120,'10','Sala 15','Spotkań','Sala do spotkań'),(_binary '',24,121,'30','Sala 16','Wykładowa','Sala do wykładów'),(_binary '\0',24,122,'10','Sala 1','Spotkań','Sala do spotkań'),(_binary '',24,123,'10','Sala 2','Duża sala audytoryjna','Sala do konferencji'),(_binary '\0',24,124,'30','Sala 3','Wykładowa','Sala do wykładów'),(_binary '',25,125,'20','Sala 4','Duża sala audytoryjna','Sala do konferencji'),(_binary '',25,126,'200','Sala 5','Konferencyjna','Sala do konferencji'),(_binary '',25,127,'10','Sala 6','Duża sala audytoryjna','Sala do konferencji'),(_binary '\0',25,128,'30','Sala 7','Wykładowa','Sala do wykładów'),(_binary '',25,129,'10','Sala 8','Duża sala audytoryjna','Sala do konferencji'),(_binary '\0',25,130,'10','Sala 9','Konferencyjna','Sala do konferencji'),(_binary '',25,131,'200','Sala 10','Wykładowa','Sala do wykładów'),(_binary '',25,132,'50','Sala 15','Wykładowa','Sala do wykładów'),(_binary '\0',25,133,'56','Sala 16','Duża sala audytoryjna','Sala do konferencji');
/*!40000 ALTER TABLE `halls` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `localizations`
--

DROP TABLE IF EXISTS `localizations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `localizations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `postal_code` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `localizations`
--

LOCK TABLES `localizations` WRITE;
/*!40000 ALTER TABLE `localizations` DISABLE KEYS */;
INSERT INTO `localizations` VALUES (1,'ul. Marszałkowska 1','Warszawa','Główna siedziba','00-001'),(2,'ul. Floriańska 5','Kraków','Oddział Krakowski','30-001'),(3,'ul. Długa 10','Gdańsk','Filia w Gdańsku','80-001'),(4,'ul. Święty Marcin 20','Poznań','Oddział Poznański','61-001'),(5,'ul. Piłsudskiego 15','Wrocław','Wrocławski punkt','50-001');
/*!40000 ALTER TABLE `localizations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservations`
--

DROP TABLE IF EXISTS `reservations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservations` (
  `date` datetime(6) DEFAULT NULL,
  `end_meeting` datetime(6) DEFAULT NULL,
  `hall_id` bigint NOT NULL,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `start_meeting` datetime(6) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9ef1eaarhc15ntbkr95q5w7xs` (`hall_id`),
  KEY `FKb5g9io5h54iwl2inkno50ppln` (`user_id`),
  CONSTRAINT `FK9ef1eaarhc15ntbkr95q5w7xs` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`id`),
  CONSTRAINT `FKb5g9io5h54iwl2inkno50ppln` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES ('2025-03-30 15:59:26.000000','2025-04-01 12:00:00.000000',1,1,'2025-04-01 10:00:00.000000',1),('2025-03-30 15:59:26.000000','2025-04-02 16:00:00.000000',2,2,'2025-04-02 14:00:00.000000',2),('2025-03-30 15:59:26.000000','2025-04-03 11:00:00.000000',3,3,'2025-04-03 09:00:00.000000',3),('2025-03-30 15:59:26.000000','2025-04-04 15:00:00.000000',4,4,'2025-04-04 13:00:00.000000',4),('2025-03-30 15:59:26.000000','2025-04-05 10:00:00.000000',5,5,'2025-04-05 08:00:00.000000',5);
/*!40000 ALTER TABLE `reservations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'jan.kowalski@example.com','Jan','Kowalski','ADMIN'),(2,'anna.nowak@example.com','Anna','Nowak','USER'),(3,'piotr.wisniewski@example.com','Piotr','Wiśniewski','USER'),(4,'maria.dabrowska@example.com','Maria','Dąbrowska','USER'),(5,'tomasz.lewandowski@example.com','Tomasz','Lewandowski','MODERATOR');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-31 15:23:00
