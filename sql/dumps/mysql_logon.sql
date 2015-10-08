/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : blaze

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2015-04-30 19:43:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `profiles`
-- ----------------------------
DROP TABLE IF EXISTS `profiles`;
CREATE TABLE `profiles` (
  `user` varchar(20) NOT NULL,
  `user_clean` varchar(20) NOT NULL,
  `pass` varchar(150) NOT NULL,
  `lastIp` varchar(15) NOT NULL,
  `lastSeen` bigint(11) NOT NULL DEFAULT '0',
  `rights` smallint(3) NOT NULL default '0',
  PRIMARY KEY (`user_clean`),
  UNIQUE KEY `user_clean` (`user_clean`) USING HASH,
  KEY `user` (`user`) USING BTREE,
  KEY `lastSeen` (`lastSeen`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of profiles
-- ----------------------------
