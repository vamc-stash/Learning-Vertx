create schema college;
use college;

DROP TABLE IF EXISTS `resource`;
CREATE TABLE `resource` (
    `id` int NOT NULL AUTO_INCREMENT,
    `resource_key` varchar(40) DEFAULT NULL,
    `description` varchar(100) DEFAULT NULL,
    `type` varchar(10) DEFAULT NULL, -- type can be API, WEB
    `status` tinyint(1) DEFAULT NULL,
    `method` varchar(40) DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`),
    UNIQUE KEY `resource_key` (`resource_key`)
);

LOCK TABLES `resource` WRITE;
INSERT INTO `resource` VALUES ('', '/ping', 'API', 1, 'GET', NULL, NULL);
INSERT INTO `resource` VALUES ('', '/api/college/v1/signUp', 'API', 1, 'POST', NULL, NULL);
UNLOCK TABLES;

DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
    `id` int NOT NULL AUTO_INCREMENT,
    `role` varchar(40) DEFAULT NULL,
    `description` varchar(100) DEFAULT NULL,
    `status` tinyint(1) DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `role` (`role`)
);

LOCK TABLES `roles` WRITE;
INSERT INTO `roles` VALUES (1, "ADMIN", "Admin User", 1, NULL, NULL);
INSERT INTO `roles` VALUES (2, "TEACHER", "Teacher", 1, NULL, NULL);
INSERT INTO `roles` VALUES (3, "STUDENT", "Student", 1, NULL, NULL);
UNLOCK TABLES;

DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
    `id` int NOT NULL AUTO_INCREMENT,
    `role_id` int DEFAULT NULL,
    `resources` json DEFAULT NULL,
    `type` varchar(10) DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `role_id` (`role_id`),
    CONSTRAINT fk_role_permission FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
);

LOCK TABLES `role_permissions` WRITE;
INSERT INTO `role_permissions` VALUES (1, 1, JSON_ARRAY("2e770778-ac15-11eb-8c16-c85b7653eeb9", "2e7c869c-ac15-11eb-8c16-c85b7653eeb9"), "API");
UNLOCK TABLES;

DROP TABLE IF EXISTS `users`;
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
);

DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
    `user_id` varchar(40) NOT NULL,
    `role_id` int NOT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (`user_id`),
    FOREIGN KEY (role_id) REFERENCES roles (`id`),
    PRIMARY KEY (`user_id`, `role_id`)
);

DROP TABLE IF EXISTS `courses`;
CREATE TABLE `courses` (
    `id` int NOT NULL AUTO_INCREMENT,
    `course_id` varchar(40) DEFAULT NULL,
    `title` varchar(40) DEFAULT NULL,
    `credits` int DEFAULT NULL,
    `teacher_id` varchar(40) DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY(`id`),
    UNIQUE KEY `course_id` (`course_id`),
    FOREIGN KEY (`teacher_id`) REFERENCES users (`user_id`)
);

DROP TABLE IF EXISTS `student_courses`;
CREATE TABLE `student_courses` (
    `course_id` varchar(40) NOT NULL,
    `student_id` varchar(40) NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses (`course_id`),
    FOREIGN KEY (student_id) REFERENCES users (`user_id`),
    PRIMARY KEY (`course_id`, `student_id`)
);
