/*
 Navicat Premium Data Transfer

 Source Server         : 本机
 Source Server Type    : MySQL
 Source Server Version : 50722
 Source Host           : localhost:3306
 Source Schema         : archerplatform

 Target Server Type    : MySQL
 Target Server Version : 50722
 File Encoding         : 65001

 Date: 18/10/2018 10:35:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for apiinfo
-- ----------------------------
DROP TABLE IF EXISTS `apiinfo`;
CREATE TABLE `apiinfo`  (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `apiName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `protocol` int(1) NULL DEFAULT NULL,
  `url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `method` int(1) NULL DEFAULT NULL,
  `header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `isMock` int(1) NULL DEFAULT NULL,
  `serviceId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 121 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for caseapiinfo
-- ----------------------------
DROP TABLE IF EXISTS `caseapiinfo`;
CREATE TABLE `caseapiinfo`  (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `apiId` int(5) NULL DEFAULT NULL,
  `apiName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `protocol` int(1) NULL DEFAULT NULL,
  `url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `method` int(1) NULL DEFAULT NULL,
  `header` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `body` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `waitMillis` int(10) NULL DEFAULT NULL,
  `isMock` int(1) NULL DEFAULT NULL,
  `serviceId` int(5) NULL DEFAULT NULL,
  `caseId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for caseinfo
-- ----------------------------
DROP TABLE IF EXISTS `caseinfo`;
CREATE TABLE `caseinfo`  (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `caseName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `apiSequence` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `runBatTimes` int(10) UNSIGNED ZEROFILL NULL DEFAULT 0000000000,
  `projectId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for caseverifyinfo
-- ----------------------------
DROP TABLE IF EXISTS `caseverifyinfo`;
CREATE TABLE `caseverifyinfo`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `verifyName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `verifyType` int(1) NULL DEFAULT NULL,
  `expression` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expectValue` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `actualValue` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `isSuccess` int(1) NULL DEFAULT NULL,
  `caseApiId` int(12) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for correlateinfo
-- ----------------------------
DROP TABLE IF EXISTS `correlateinfo`;
CREATE TABLE `correlateinfo`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `corrField` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `corrPattern` int(1) NULL DEFAULT NULL,
  `corrExpression` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `corrValue` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `caseApiId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for hostsinfo
-- ----------------------------
DROP TABLE IF EXISTS `hostsinfo`;
CREATE TABLE `hostsinfo`  (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `hostsItem` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `description` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `status` int(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for projectinfo
-- ----------------------------
DROP TABLE IF EXISTS `projectinfo`;
CREATE TABLE `projectinfo`  (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `projectName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for respinfo
-- ----------------------------
DROP TABLE IF EXISTS `respinfo`;
CREATE TABLE `respinfo`  (
  `id` int(12) NOT NULL AUTO_INCREMENT,
  `apiName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `url` varchar(2048) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `statusCode` int(3) NULL DEFAULT NULL,
  `respHeader` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `respContent` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `respTime` int(12) UNSIGNED NULL DEFAULT NULL,
  `runBatTimes` int(10) NULL DEFAULT NULL,
  `apiId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 781 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for serviceinfo
-- ----------------------------
DROP TABLE IF EXISTS `serviceinfo`;
CREATE TABLE `serviceinfo`  (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `serviceName` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `baseUrl` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `version` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `type` int(1) NULL DEFAULT NULL,
  `createTime` datetime(0) NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `projectId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 76 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for verifyinfo
-- ----------------------------
DROP TABLE IF EXISTS `verifyinfo`;
CREATE TABLE `verifyinfo`  (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `verifyName` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `verifyType` int(1) NULL DEFAULT NULL,
  `expression` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `expectValue` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `actualValue` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `isSuccess` int(1) NULL DEFAULT NULL,
  `apiId` int(5) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 179 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
