-- MySQL dump 10.13  Distrib 9.2.0, for Linux (x86_64)
--
-- Host: localhost    Database: conference
-- ------------------------------------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `buildings`
--

LOCK TABLES `buildings` WRITE;
/*!40000 ALTER TABLE `buildings` DISABLE KEYS */;
INSERT INTO `buildings` VALUES (101,6,'Na przeciwko nauki jazdy','Oddział Łabiszyńska'),(102,7,'Obok szkoly','Oddział Jagielońska');
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
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKowc82hok5hwtuhrhaxw1tok55` (`building_id`),
  CONSTRAINT `FKowc82hok5hwtuhrhaxw1tok55` FOREIGN KEY (`building_id`) REFERENCES `buildings` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=139 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `halls`
--

LOCK TABLES `halls` WRITE;
/*!40000 ALTER TABLE `halls` DISABLE KEYS */;
INSERT INTO `halls` VALUES (_binary '',101,134,'10','Sala 1','Komputerowa','I pietro','sala1@bucikbialekgmail.onmicrosoft.com'),(_binary '',101,135,'20','Sala 2','Wykładowa','I pietro','sala2@bucikbialekgmail.onmicrosoft.com'),(_binary '',101,136,'30','Sala 3','Aula','II pietro','sala3@bucikbialekgmail.onmicrosoft.com'),(_binary '',101,137,'40','Sala 4','Biznesowa','II pietro','sala4@bucikbialekgmail.onmicrosoft.com'),(_binary '',102,138,'50','Sala 5','Komputerowa','I pietro','sala5@bucikbialekgmail.onmicrosoft.com');
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
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `localizations`
--

LOCK TABLES `localizations` WRITE;
/*!40000 ALTER TABLE `localizations` DISABLE KEYS */;
INSERT INTO `localizations` VALUES (6,'Łabiszyńska 25','Warszawa','','03-204',NULL),(7,'Jagielońska 88','Warszawa','','00-992',NULL);
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
  `organizer_email` varchar(255) DEFAULT NULL,
  `outlook_event_id` varchar(255) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9ef1eaarhc15ntbkr95q5w7xs` (`hall_id`),
  KEY `FKb5g9io5h54iwl2inkno50ppln` (`user_id`),
  CONSTRAINT `FK9ef1eaarhc15ntbkr95q5w7xs` FOREIGN KEY (`hall_id`) REFERENCES `halls` (`id`),
  CONSTRAINT `FKb5g9io5h54iwl2inkno50ppln` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=683 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservations`
--

LOCK TABLES `reservations` WRITE;
/*!40000 ALTER TABLE `reservations` DISABLE KEYS */;
INSERT INTO `reservations` VALUES ('2025-06-04 10:31:39.556479','2025-06-05 04:00:00.000000',135,667,'2025-06-05 03:00:00.000000','dominika@bucikbialekgmail.onmicrosoft.com','AAMkADExODU2NWNhLWM1MTctNGRlNS1iN2Y5LWM4OTEyMGNiMDExNQBGAAAAAAAftTFzsCKtQqRfJ4NWen4RBwDakGvKVrG9TqBk_oC1NATgAAAAAAENAADakGvKVrG9TqBk_oC1NATgAAARAPyWAAA=',7,'Dominika Brzozka: 47'),('2025-06-04 10:32:09.795718','2025-06-05 07:00:00.000000',135,668,'2025-06-05 06:00:00.000000','dominika@bucikbialekgmail.onmicrosoft.com','AAMkADExODU2NWNhLWM1MTctNGRlNS1iN2Y5LWM4OTEyMGNiMDExNQBGAAAAAAAftTFzsCKtQqRfJ4NWen4RBwDakGvKVrG9TqBk_oC1NATgAAAAAAENAADakGvKVrG9TqBk_oC1NATgAAARAPyXAAA=',7,'Dominika Brzozka: 98'),('2025-06-04 10:33:21.466403','2025-06-05 03:00:00.000000',134,669,'2025-06-05 02:00:00.000000','dominika@bucikbialekgmail.onmicrosoft.com','AAMkADExODU2NWNhLWM1MTctNGRlNS1iN2Y5LWM4OTEyMGNiMDExNQBGAAAAAAAftTFzsCKtQqRfJ4NWen4RBwDakGvKVrG9TqBk_oC1NATgAAAAAAENAADakGvKVrG9TqBk_oC1NATgAAARAPyYAAA=',7,'Dominika Brzozka: Opis projektu'),('2025-06-04 10:42:20.745267','2025-06-05 02:00:00.000000',135,672,'2025-06-05 01:00:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: Programowanie'),('2025-06-04 10:43:50.352509','2025-06-05 02:30:00.000000',136,673,'2025-06-05 01:30:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: Aplikacje Webowe'),('2025-06-04 11:05:30.540367','2025-06-05 06:00:00.000000',134,674,'2025-06-05 05:00:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: cdsfs'),('2025-06-04 11:09:34.454311','2025-06-05 03:00:00.000000',138,675,'2025-06-05 02:00:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: Arka'),('2025-06-04 11:11:04.020320','2025-06-05 07:30:00.000000',136,676,'2025-06-05 06:30:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: Programowanie zawansowane'),('2025-06-04 11:11:40.979968','2025-06-05 05:30:00.000000',135,677,'2025-06-05 04:30:00.000000','dominika@bucikbialekgmail.onmicrosoft.com',NULL,7,'Dominika Brzozka: Legia'),('2025-06-04 11:17:03.677233','2025-06-07 09:00:00.000000',134,678,'2025-06-07 08:00:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: hhh'),('2025-06-04 11:17:33.392438','2025-06-06 15:30:00.000000',134,679,'2025-06-06 14:30:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: hh'),('2025-06-04 11:17:48.077719','2025-06-06 18:00:00.000000',134,680,'2025-06-06 17:00:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: kjj'),('2025-06-04 11:19:27.385434','2025-06-05 10:30:00.000000',134,681,'2025-06-05 09:30:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: jjj'),('2025-06-04 11:23:27.243750','2025-06-05 12:30:00.000000',134,682,'2025-06-05 11:30:00.000000','jakub@bucikbialekgmail.onmicrosoft.com',NULL,2,'Jakub Bialek: jjj');
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'sala3@bucikbialekgmail.onmicrosoft.com','Outlook','User',NULL),(2,'jakub@bucikbialekgmail.onmicrosoft.com','Jakub','Bialek',NULL),(3,'sala1@bucikbialekgmail.onmicrosoft.com','Outlook','User',NULL),(4,'sala2@bucikbialekgmail.onmicrosoft.com','Outlook','User',NULL),(5,'sala4@bucikbialekgmail.onmicrosoft.com','Outlook','User',NULL),(6,'sala5@bucikbialekgmail.onmicrosoft.com','Outlook','User',NULL),(7,'dominika@bucikbialekgmail.onmicrosoft.com','Dominika','Brzozka',NULL);
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

-- Dump completed on 2025-06-04 11:32:43
