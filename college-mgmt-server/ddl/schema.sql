create schema college;
use college;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` int NOT NULL AUTO_INCREMENT,
  `course_id` varchar(40) DEFAULT NULL,
  `title` varchar(40) DEFAULT NULL,
  `credits` int DEFAULT NULL,
  `teacher_id` varchar(40) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `course_id` (`course_id`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `resource`
--

DROP TABLE IF EXISTS `resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resource` (
  `id` int NOT NULL AUTO_INCREMENT,
  `resource_key` varchar(40) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `method` varchar(40) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `resource_key` (`resource_key`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
/*!40000 ALTER TABLE `resource` DISABLE KEYS */;
INSERT INTO `resource` VALUES (1,'2e770778-ac15-11eb-8c16-c85b7653eeb9','/ping','API',1,'GET',NULL,'2021-05-03 19:10:53'),(2,'2e7c869c-ac15-11eb-8c16-c85b7653eeb9','/api/v1/auth/signup','API',1,'POST',NULL,'2021-05-22 23:26:15'),(3,'a97150ac-bb0c-11eb-8008-c85b7653eeb9','/api/v1/auth/signin','API',1,'POST','2021-05-22 20:17:04','2021-05-22 23:26:27'),(4,'65fa9464-bb21-11eb-8a3d-c85b7653eeb9','/api/v1/college/ping','API',1,'GET','2021-05-22 22:45:41','2021-05-22 23:26:41'),(5,'7f228c20-bf19-11eb-8a3d-c85b7653eeb9','/api/v1/auth/resetPassword','API',1,'POST','2021-05-27 23:59:14','2021-05-27 23:59:39'),(6,'994f72ae-bf19-11eb-8a3d-c85b7653eeb9','/api/v1/college/updateUser','API',1,'POST','2021-05-28 00:00:11','2021-05-28 00:00:23'),(7,'aea74276-bf19-11eb-8a3d-c85b7653eeb9','/api/v1/college/get','API',1,'GET','2021-05-28 00:00:42','2021-05-28 10:45:24'),(8,'bfd7be97-bf19-11eb-8a3d-c85b7653eeb9','/api/v1/college/registerCourses','API',1,'POST','2021-05-28 00:01:17','2021-05-28 00:01:27'),(9,'efce42a8-bf19-11eb-8a3d-c85b7653eeb9','/api/v1/college/addUser','API',1,'POST','2021-05-28 00:02:38','2021-05-28 23:26:41'),(11,'1487f4cb-bf1a-11eb-8a3d-c85b7653eeb9','/api/v1/college/addCourse','API',1,'POST','2021-05-28 00:03:47','2021-05-28 00:03:49');
/*!40000 ALTER TABLE `resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_id` int DEFAULT NULL,
  `resources` json DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `fk_role_permission` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
INSERT INTO `role_permissions` VALUES (1,1,'[\"2e770778-ac15-11eb-8c16-c85b7653eeb9\", \"2e7c869c-ac15-11eb-8c16-c85b7653eeb9\", \"a97150ac-bb0c-11eb-8008-c85b7653eeb9\", \"65fa9464-bb21-11eb-8a3d-c85b7653eeb9\", \"7f228c20-bf19-11eb-8a3d-c85b7653eeb9\", \"994f72ae-bf19-11eb-8a3d-c85b7653eeb9\", \"aea74276-bf19-11eb-8a3d-c85b7653eeb9\", \"efce42a8-bf19-11eb-8a3d-c85b7653eeb9\", \"1487f4cb-bf1a-11eb-8a3d-c85b7653eeb9\"]','API',NULL,'2021-05-28 23:45:53'),(2,2,'[\"2e770778-ac15-11eb-8c16-c85b7653eeb9\", \"a97150ac-bb0c-11eb-8008-c85b7653eeb9\", \"65fa9464-bb21-11eb-8a3d-c85b7653eeb9\", \"7f228c20-bf19-11eb-8a3d-c85b7653eeb9\", \"994f72ae-bf19-11eb-8a3d-c85b7653eeb9\", \"aea74276-bf19-11eb-8a3d-c85b7653eeb9\"]','API',NULL,'2021-05-28 00:10:00'),(3,3,'[\"2e770778-ac15-11eb-8c16-c85b7653eeb9\", \"a97150ac-bb0c-11eb-8008-c85b7653eeb9\", \"65fa9464-bb21-11eb-8a3d-c85b7653eeb9\", \"7f228c20-bf19-11eb-8a3d-c85b7653eeb9\", \"994f72ae-bf19-11eb-8a3d-c85b7653eeb9\", \"aea74276-bf19-11eb-8a3d-c85b7653eeb9\", \"bfd7be97-bf19-11eb-8a3d-c85b7653eeb9\"]','API',NULL,'2021-05-28 00:10:24');
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `role` varchar(40) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `status` tinyint(1) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ADMIN','Admin User',1,NULL,NULL),(2,'TEACHER','Teacher',1,NULL,NULL),(3,'STUDENT','Student',1,NULL,NULL);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_courses`
--

DROP TABLE IF EXISTS `student_courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_courses` (
  `course_id` varchar(40) NOT NULL,
  `student_id` varchar(40) NOT NULL,
  PRIMARY KEY (`course_id`,`student_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `student_courses_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`),
  CONSTRAINT `student_courses_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` varchar(40) NOT NULL,
  `role_id` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `role_id` (`role_id`),
  CONSTRAINT `user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` varchar(40) DEFAULT NULL,
  `first_name` varchar(40) DEFAULT NULL,
  `last_name` varchar(40) DEFAULT NULL,
  `email` varchar(40) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


