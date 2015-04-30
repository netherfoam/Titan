/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50621
Source Host           : localhost:3306
Source Database       : blaze

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2015-02-21 22:53:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `profiles`
-- ----------------------------
DROP TABLE IF EXISTS `profiles`;
CREATE TABLE `profiles` (
  `user` varchar(20) NOT NULL,
  `user_clean` text NOT NULL,
  `pass` varchar(150) NOT NULL,
  `lastIp` varchar(15) NOT NULL,
  `lastSeen` bigint(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_clean`(20))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of profiles
-- ----------------------------
