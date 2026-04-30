-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: automatedtraineeprogress
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `module`
--

DROP TABLE IF EXISTS `module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `module` (
  `module_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `module_name` varchar(255) NOT NULL,
  `sequence_order` int NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `template_id` bigint NOT NULL,
  PRIMARY KEY (`module_id`),
  KEY `FKh4hnbh5w5f9ffgrcuwish51h8` (`template_id`),
  CONSTRAINT `FKh4hnbh5w5f9ffgrcuwish51h8` FOREIGN KEY (`template_id`) REFERENCES `template` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `module`
--

LOCK TABLES `module` WRITE;
/*!40000 ALTER TABLE `module` DISABLE KEYS */;
INSERT INTO `module` VALUES (7,'2026-04-14 14:48:35.969222','Module for Object Oriented Programming','OOP',1,'2026-04-14 14:48:35.969222',1),(8,'2026-04-14 14:56:30.598314','Module for Java','Java',2,'2026-04-14 14:56:30.598314',1),(9,'2026-04-14 15:02:31.286852','Module for Database','Database',3,'2026-04-14 15:02:31.286852',1),(10,'2026-04-30 16:09:06.436106','Foundamentals of Spring framework','SPRING',4,'2026-04-30 16:09:06.436106',1);
/*!40000 ALTER TABLE `module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` enum('MANAGER','TRAINEE') NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'MANAGER'),(2,'TRAINEE');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_line`
--

DROP TABLE IF EXISTS `service_line`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_line` (
  `service_lineid` bigint NOT NULL AUTO_INCREMENT,
  `department` enum('DEVELOPMENT','FINANCE','QA') NOT NULL,
  PRIMARY KEY (`service_lineid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_line`
--

LOCK TABLES `service_line` WRITE;
/*!40000 ALTER TABLE `service_line` DISABLE KEYS */;
INSERT INTO `service_line` VALUES (1,'FINANCE'),(3,'QA'),(4,'DEVELOPMENT');
/*!40000 ALTER TABLE `service_line` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text,
  `title` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `service_line_id` bigint NOT NULL,
  PRIMARY KEY (`template_id`),
  KEY `FKb9l5iu7uf0pk5y0arvy771q1m` (`created_by`),
  KEY `FKls6fwde2uyewhjte6ysa64c83` (`service_line_id`),
  CONSTRAINT `FKb9l5iu7uf0pk5y0arvy771q1m` FOREIGN KEY (`created_by`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKls6fwde2uyewhjte6ysa64c83` FOREIGN KEY (`service_line_id`) REFERENCES `service_line` (`service_lineid`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (1,'2026-03-31 13:01:16.015915','Backend Java template','JAVA','2026-04-13 11:24:38.249999',11,1),(2,'2026-03-31 13:01:38.075431','Updated frontend template','REACT','2026-04-07 13:16:17.826774',1,4),(4,'2026-04-07 13:16:01.167244','Dot Net training template','DOTNET','2026-04-07 13:16:21.414970',1,4),(5,'2026-04-09 17:00:13.540537','Python backend template','PYTHON','2026-04-09 17:00:13.540537',8,1),(6,'2026-04-13 15:56:03.396008','Template for Node','Node','2026-04-13 15:56:03.396008',8,4),(7,'2026-04-30 16:07:19.324881','Backend template for C#','C#','2026-04-30 16:07:19.324881',8,4);
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `topic`
--

DROP TABLE IF EXISTS `topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `topic` (
  `topic_id` bigint NOT NULL AUTO_INCREMENT,
  `assignment` text,
  `created_at` datetime(6) DEFAULT NULL,
  `learning_objective` text,
  `reading_material` text,
  `sequence_order` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `module_id` bigint NOT NULL,
  PRIMARY KEY (`topic_id`),
  KEY `FK61tp1ugtn26t353ss9nwaj41k` (`module_id`),
  CONSTRAINT `FK61tp1ugtn26t353ss9nwaj41k` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `topic`
--

LOCK TABLES `topic` WRITE;
/*!40000 ALTER TABLE `topic` DISABLE KEYS */;
INSERT INTO `topic` VALUES (11,'Create a simple program (in Java, Python, or any OOP language) that demonstrates the four main OOP principles: Encapsulation, Abstraction, Inheritance, and Polymorphism.','2026-04-14 14:50:54.743846','To understand and apply the four fundamental Object-Oriented Programming (OOP) principles—Encapsulation, Abstraction, Polymorphism, and Inheritance—to design modular, reusable, and maintainable software systems.','https://www.geeksforgeeks.org/java/object-oriented-programming-oops-concept-in-java/',1,'Encapsulation, Abstraction, Polymorphism, Inheritance','2026-04-14 14:50:54.743846',7),(12,'Create a small program to demonstrate Static Classes and Sealed Classes.','2026-04-14 14:52:23.657026','To understand the purpose and usage of static nested classes and sealed classes in object-oriented programming for better structure, control, and restricted class hierarchy design.','https://www.geeksforgeeks.org/java/classes-objects-java/',2,'Static Classes, Sealed Classes','2026-04-14 14:52:23.657026',7),(13,'Create a Java program that demonstrates methods, method parameters, method overloading, and sealed methods.','2026-04-14 14:53:37.767305','To understand how methods work in Java, including parameter passing, method overloading, and sealed methods, to build flexible, reusable, and controlled behavior in programs.','https://www.geeksforgeeks.org/java/methods-in-java/',3,'Methods, Methods Parameters, Methods Overloading, Sealed Methods','2026-04-14 14:53:37.767305',7),(14,'Create a simple program that demonstrates different variables and data types in Java.','2026-04-14 14:57:36.394070','To understand variables and data types in programming and how they are used to store and manage different kinds of data in a program.','https://www.geeksforgeeks.org/java/variables-in-java/',1,'Variables, Data Types','2026-04-14 14:57:36.394070',8),(15,'Create a simple program that demonstrates Enums and Constants using a basic system (e.g., order status or student grade system).','2026-04-14 14:58:32.679318','To understand how Enums and Constants are used in Java to define fixed values and improve code safety, readability, and maintainability.','https://www.w3schools.com/java/java_enums.asp',2,' Enums, Constants','2026-04-14 14:58:32.679318',8),(16,'Create a simple program that demonstrates arrays and different types of operators in Java.','2026-04-14 14:59:23.709745','To understand arrays and operators in Java and how they are used to store multiple values and perform different types of calculations and logical operations in programs.','https://www.w3schools.com/java/java_arrays.asp',3,' Arrays, Operators','2026-04-14 14:59:23.709745',8),(17,'Create a simple program that demonstrates if-else and switch statements using a basic grading or menu system.','2026-04-14 15:00:03.660704','To understand how to control program flow using if-else and switch statements for making decisions based on different conditions in Java.','https://www.geeksforgeeks.org/java/decision-making-javaif-else-switch-break-continue-jump/',4,' If else / switch','2026-04-14 15:00:03.660704',8),(18,'Create a simple program that demonstrates Interfaces, Abstract Classes, and Generics using a basic system.','2026-04-14 15:01:06.476621','To understand how interfaces, abstract classes, and generics are used in Java to achieve abstraction, code reusability, and type safety in software development.','https://www.geeksforgeeks.org/java/generic-constructors-and-interfaces-in-java/',5,' Interfaces / Abstract classes, Generics','2026-04-14 15:01:06.476621',8),(19,'Create a simple program that demonstrates Lambda Expressions and Anonymous Functions using basic functional interfaces.','2026-04-14 15:01:58.874208','To understand how lambda expressions and anonymous functions simplify code by enabling concise implementation of functional interfaces in Java.','https://www.geeksforgeeks.org/java/lambda-expressions-java-8/',4,'Lambda Expression, Anonymous Functions','2026-04-14 15:01:58.874208',7),(20,'Design a simple database for a Student Management System.','2026-04-14 15:03:24.044836','To understand the fundamentals of basic database design, including tables, relationships, primary keys, and normalization for organizing data efficiently and avoiding redundancy.','https://www.geeksforgeeks.org/dbms/database-design-in-dbms/',1,'  Basic database design','2026-04-14 15:03:24.044836',9),(21,'Create and manage a simple database table and perform CRUD operations using SQL queries.','2026-04-14 15:04:30.167789','To understand how to perform CRUD operations (Create, Read, Update, Delete) and write basic SQL queries for managing and retrieving data from a database.','https://www.geeksforgeeks.org/dbms/sql-server-crud-operations/',2,'  CRUD operations, queries','2026-04-14 15:04:30.167789',9),(22,'Database design of a system like Application tracking system or Multi tenancy role based authorization','2026-04-14 15:05:32.522320','To understand how to create and use stored procedures in SQL to encapsulate reusable database logic for efficient, secure, and organized data operations.','https://www.geeksforgeeks.org/dbms/sql-server-crud-operations/',3,'Stored Procedures','2026-04-14 15:05:32.522320',9);
/*!40000 ALTER TABLE `topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainee_module`
--

DROP TABLE IF EXISTS `trainee_module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainee_module` (
  `trainee_module_id` bigint NOT NULL AUTO_INCREMENT,
  `completed_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `module_id` bigint NOT NULL,
  `trainee_template_id` bigint NOT NULL,
  PRIMARY KEY (`trainee_module_id`),
  KEY `FKk19xo33lu2cqhm0n1q692pltw` (`module_id`),
  KEY `FK3jios9t6tc1hdqd5xf7vje7go` (`trainee_template_id`),
  CONSTRAINT `FK3jios9t6tc1hdqd5xf7vje7go` FOREIGN KEY (`trainee_template_id`) REFERENCES `trainee_template` (`trainee_template_id`),
  CONSTRAINT `FKk19xo33lu2cqhm0n1q692pltw` FOREIGN KEY (`module_id`) REFERENCES `module` (`module_id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainee_module`
--

LOCK TABLES `trainee_module` WRITE;
/*!40000 ALTER TABLE `trainee_module` DISABLE KEYS */;
INSERT INTO `trainee_module` VALUES (11,NULL,'NOT_STARTED','2026-04-14 14:48:36.047204',7,2),(12,NULL,'NOT_STARTED','2026-04-14 14:48:36.051030',7,3),(13,NULL,'NOT_STARTED','2026-04-14 14:48:36.054039',7,5),(14,NULL,'NOT_STARTED','2026-04-14 14:56:30.625072',8,2),(15,NULL,'NOT_STARTED','2026-04-14 14:56:30.627517',8,3),(16,NULL,'NOT_STARTED','2026-04-14 14:56:30.629478',8,5),(17,NULL,'NOT_STARTED','2026-04-14 15:02:31.302332',9,2),(18,NULL,'NOT_STARTED','2026-04-14 15:02:31.304331',9,3),(19,NULL,'NOT_STARTED','2026-04-14 15:02:31.306332',9,5),(20,NULL,'NOT_STARTED','2026-04-16 10:00:19.576029',7,6),(21,NULL,'NOT_STARTED','2026-04-16 10:00:19.593388',8,6),(22,NULL,'NOT_STARTED','2026-04-16 10:00:19.593388',9,6),(23,NULL,'NOT_STARTED','2026-04-30 16:09:06.510457',10,2),(24,NULL,'NOT_STARTED','2026-04-30 16:09:06.523028',10,3),(25,NULL,'NOT_STARTED','2026-04-30 16:09:06.527054',10,5),(26,NULL,'NOT_STARTED','2026-04-30 16:09:06.531078',10,6);
/*!40000 ALTER TABLE `trainee_module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainee_template`
--

DROP TABLE IF EXISTS `trainee_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainee_template` (
  `trainee_template_id` bigint NOT NULL AUTO_INCREMENT,
  `started_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `template_id` bigint NOT NULL,
  `trainee_id` bigint NOT NULL,
  PRIMARY KEY (`trainee_template_id`),
  KEY `FKcb2jh0ld0vl2cij22hok8nhr6` (`template_id`),
  KEY `FK32a0j2ufgelaft89vij4xj2nk` (`trainee_id`),
  CONSTRAINT `FK32a0j2ufgelaft89vij4xj2nk` FOREIGN KEY (`trainee_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKcb2jh0ld0vl2cij22hok8nhr6` FOREIGN KEY (`template_id`) REFERENCES `template` (`template_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainee_template`
--

LOCK TABLES `trainee_template` WRITE;
/*!40000 ALTER TABLE `trainee_template` DISABLE KEYS */;
INSERT INTO `trainee_template` VALUES (2,'2026-03-31 16:21:54.327278','NOT_STARTED','2026-03-31 16:21:54.327278',1,1),(3,'2026-04-13 16:10:21.171192','NOT_STARTED','2026-04-13 16:10:21.171192',1,13),(4,'2026-04-13 16:12:19.967354','NOT_STARTED','2026-04-13 16:12:19.967354',4,10),(5,'2026-04-13 16:19:25.291853','NOT_STARTED','2026-04-13 16:19:25.291853',1,14),(6,'2026-04-16 10:00:19.559894','NOT_STARTED','2026-04-16 10:00:19.559894',1,15);
/*!40000 ALTER TABLE `trainee_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trainee_topic`
--

DROP TABLE IF EXISTS `trainee_topic`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trainee_topic` (
  `trainee_topic_id` bigint NOT NULL AUTO_INCREMENT,
  `completed_at` datetime(6) DEFAULT NULL,
  `status` enum('COMPLETED','IN_PROGRESS','NOT_STARTED') NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `topic_id` bigint NOT NULL,
  `trainee_module_id` bigint NOT NULL,
  PRIMARY KEY (`trainee_topic_id`),
  KEY `FKkwfk6998j43hrbda8e5xh7h1v` (`topic_id`),
  KEY `FKioegs2f9pm7tm12nva4t6qkco` (`trainee_module_id`),
  CONSTRAINT `FKioegs2f9pm7tm12nva4t6qkco` FOREIGN KEY (`trainee_module_id`) REFERENCES `trainee_module` (`trainee_module_id`),
  CONSTRAINT `FKkwfk6998j43hrbda8e5xh7h1v` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`topic_id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trainee_topic`
--

LOCK TABLES `trainee_topic` WRITE;
/*!40000 ALTER TABLE `trainee_topic` DISABLE KEYS */;
INSERT INTO `trainee_topic` VALUES (15,NULL,'NOT_STARTED','2026-04-14 14:50:54.773783',11,11),(16,NULL,'NOT_STARTED','2026-04-14 14:50:54.776787',11,12),(17,'2026-04-14 15:07:53.090700','IN_PROGRESS','2026-04-14 15:08:00.840424',11,13),(18,NULL,'NOT_STARTED','2026-04-14 14:52:23.687507',12,11),(19,NULL,'NOT_STARTED','2026-04-14 14:52:23.690908',12,12),(20,'2026-04-14 15:07:54.511290','IN_PROGRESS','2026-04-14 15:08:01.710843',12,13),(21,NULL,'NOT_STARTED','2026-04-14 14:53:37.795201',13,11),(22,NULL,'NOT_STARTED','2026-04-14 14:53:37.797193',13,12),(23,'2026-04-14 15:07:56.845995','IN_PROGRESS','2026-04-14 15:08:03.912938',13,13),(24,NULL,'NOT_STARTED','2026-04-14 14:57:36.421159',14,14),(25,NULL,'NOT_STARTED','2026-04-14 14:57:36.423705',14,15),(26,NULL,'NOT_STARTED','2026-04-14 14:57:36.427502',14,16),(27,NULL,'NOT_STARTED','2026-04-14 14:58:32.704807',15,14),(28,NULL,'NOT_STARTED','2026-04-14 14:58:32.707351',15,15),(29,NULL,'NOT_STARTED','2026-04-14 14:58:32.710413',15,16),(30,NULL,'NOT_STARTED','2026-04-14 14:59:23.726807',16,14),(31,NULL,'NOT_STARTED','2026-04-14 14:59:23.729803',16,15),(32,NULL,'NOT_STARTED','2026-04-14 14:59:23.731795',16,16),(33,NULL,'NOT_STARTED','2026-04-14 15:00:03.706382',17,14),(34,NULL,'NOT_STARTED','2026-04-14 15:00:03.709922',17,15),(35,NULL,'NOT_STARTED','2026-04-14 15:00:03.720664',17,16),(36,NULL,'NOT_STARTED','2026-04-14 15:01:06.513655',18,14),(37,NULL,'NOT_STARTED','2026-04-14 15:01:06.518170',18,15),(38,NULL,'NOT_STARTED','2026-04-14 15:01:06.521201',18,16),(39,NULL,'NOT_STARTED','2026-04-14 15:01:58.893769',19,11),(40,NULL,'NOT_STARTED','2026-04-14 15:01:58.895785',19,12),(41,'2026-04-14 15:07:58.324628','IN_PROGRESS','2026-04-14 15:08:05.055139',19,13),(42,NULL,'NOT_STARTED','2026-04-14 15:03:24.104501',20,17),(43,NULL,'NOT_STARTED','2026-04-14 15:03:24.108014',20,18),(44,NULL,'NOT_STARTED','2026-04-14 15:03:24.113066',20,19),(45,NULL,'NOT_STARTED','2026-04-14 15:04:30.199126',21,17),(46,NULL,'NOT_STARTED','2026-04-14 15:04:30.202139',21,18),(47,NULL,'NOT_STARTED','2026-04-14 15:04:30.206142',21,19),(48,NULL,'NOT_STARTED','2026-04-14 15:05:32.540390',22,17),(49,NULL,'NOT_STARTED','2026-04-14 15:05:32.543533',22,18),(50,NULL,'NOT_STARTED','2026-04-14 15:05:32.545532',22,19),(51,'2026-04-16 11:08:09.142858','IN_PROGRESS','2026-04-21 11:19:37.017144',11,20),(52,'2026-04-16 11:08:11.501058','IN_PROGRESS','2026-04-21 11:19:44.009738',12,20),(53,'2026-04-16 11:08:13.789136','IN_PROGRESS','2026-04-21 11:19:45.362892',13,20),(54,'2026-04-21 11:19:42.031854','IN_PROGRESS','2026-04-21 11:19:46.169599',19,20),(55,NULL,'NOT_STARTED','2026-04-16 10:00:19.626655',14,21),(56,NULL,'NOT_STARTED','2026-04-16 10:00:19.626655',15,21),(57,NULL,'NOT_STARTED','2026-04-16 10:00:19.626655',16,21),(58,NULL,'NOT_STARTED','2026-04-16 10:00:19.626655',17,21),(59,NULL,'NOT_STARTED','2026-04-16 10:00:19.640031',18,21),(60,'2026-04-16 10:10:31.472506','IN_PROGRESS','2026-04-16 10:10:39.645586',20,22),(61,'2026-04-16 10:10:33.265446','IN_PROGRESS','2026-04-16 10:10:40.951155',21,22),(62,'2026-04-16 10:10:35.866358','IN_PROGRESS','2026-04-16 10:10:42.799109',22,22);
/*!40000 ALTER TABLE `trainee_topic` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uploaded_content`
--

DROP TABLE IF EXISTS `uploaded_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `uploaded_content` (
  `content_id` bigint NOT NULL AUTO_INCREMENT,
  `additional_note` text,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(255) NOT NULL,
  `file_type` varchar(255) NOT NULL,
  `uploaded_at` datetime(6) DEFAULT NULL,
  `trainee_topicid` bigint NOT NULL,
  PRIMARY KEY (`content_id`),
  KEY `FK7t8sljufjur09p41a8rjm63ro` (`trainee_topicid`),
  CONSTRAINT `FK7t8sljufjur09p41a8rjm63ro` FOREIGN KEY (`trainee_topicid`) REFERENCES `trainee_topic` (`trainee_topic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uploaded_content`
--

LOCK TABLES `uploaded_content` WRITE;
/*!40000 ALTER TABLE `uploaded_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `uploaded_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FK4qu1gr772nnf6ve5af002rwya` (`role_id`),
  CONSTRAINT `FK4qu1gr772nnf6ve5af002rwya` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-03-31 11:55:07.487360','abcd@example.com','Aiman Mustafa','$2a$10$RUzKow6dKpl1rR9rjr/sv.F83s0Y6gP1mQjyMqGwT8K.I8IIuR0/y','2026-03-31 11:58:02.218812',1),(2,'2026-03-31 11:57:38.808198','sarah.trainee@example.com','Sarah Trainee','$2a$10$WWPj9OJVP5ss8MzuRd2GpOl4gR6plGb3sva5vnrMvVSCZOyNSp0Ge','2026-03-31 11:57:38.808198',2),(3,'2026-03-31 13:00:26.411089','abc@example.com','Aiman','$2a$10$SBshW2Q9Vhn1pmg2UdxaXu/EjtP.RdDsXfCGxGMGyGUQ4.GUXMPQS','2026-03-31 13:00:26.411089',1),(5,'2026-03-31 19:06:48.834516','admin@example.com','Admin User','$2a$10$2t3focQJvVHWYsUn.B.vhuWDbYVN3Zc5NKdFoxs6shD2ieeCyiDsS','2026-03-31 19:06:48.834516',1),(7,'2026-03-31 19:09:59.859850','david@example.com','David Aloe','$2a$10$Tu71eumpjZ5VWgPLYum6WevncImDg/hXcOZtHihZHXtB5g7jGhmYi','2026-03-31 19:09:59.859850',1),(8,'2026-03-31 19:27:27.411646','aiman@example.com','Aiman Mustafa','$2a$10$rQk3A1q9gWFyg/BlAKCTlOvFvVM324r98gsV2tBnugH0l3xmKn9iy','2026-03-31 19:27:27.411646',1),(9,'2026-04-07 12:43:03.828410','aiman@gmail.com','aiman mustafa','$2a$10$EcFrNREoUc24XcCBy8A8wO7qoIn7Fun6Lxbv.G2fMtYbWC2vHS4va','2026-04-07 12:43:03.828410',2),(10,'2026-04-09 14:48:19.967154','iqra@gmail.com','Iqra Asghar','$2a$10$tnUUnZaWaZQXoxW3i9muVOvtVKPDHcAZYVDJvherefCQ0fEyTe/hu','2026-04-09 14:48:19.967154',2),(11,'2026-04-09 17:01:13.122791','aiman1@example.com','Aiman Mustafa','$2a$10$C0cjG/XB5Uw5JWboyEBBU.fCOtD7UBt29kwuwH/EfVXPbBGbUU5Uy','2026-04-09 17:01:13.122791',1),(12,'2026-04-13 16:00:29.300313','maham@example.com','maham noor','$2a$10$NaU72ekcfdPmtwWO4XfVUe539mIII8Zs/3UkiYjX1EyYLJWcp60OK','2026-04-13 16:00:29.300313',2),(13,'2026-04-13 16:06:41.669515','anamana@example.com','ana mana','$2a$10$50GusQMufTZ7QnLCcELvKevyC.kE6eIzRx0H0dhcnCt7A7rsjgzTq','2026-04-13 16:06:41.669515',2),(14,'2026-04-13 16:19:22.008537','naina@example.com','naina aiman','$2a$10$mnxnQyfF0f4C0h/EylqwEeB9RVFfEHTSPy5SPqEVUlRQva5GJxQsy','2026-04-13 16:19:22.008537',2),(15,'2026-04-16 10:00:10.982771','aimantrainee@gmail.com','Aiman Mustafa','$2a$10$2SqY.3kEEixyB7vA9l6usOe1Yi11eOd/fBhz9ZDT1Pz/Yk54k/59K','2026-04-16 10:00:10.982771',2),(16,'2026-04-16 10:00:59.726529','abc@gmail.com','abc xyz','$2a$10$veJSV7cAg412FCpoSZ2cWuEm2j5OdE5fO4JZ7vVSeUMlgiISSKXJG','2026-04-16 10:00:59.726529',1);
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

-- Dump completed on 2026-04-30 16:18:13
