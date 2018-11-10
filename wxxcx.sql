/*
Navicat MySQL Data Transfer

Source Server         : root
Source Server Version : 50721
Source Host           : localhost:3306
Source Database       : wxxcx

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-11-10 23:13:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `feedback`
-- ----------------------------
DROP TABLE IF EXISTS `feedback`;
CREATE TABLE `feedback` (
  `token` varchar(255) NOT NULL,
  `word` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of feedback
-- ----------------------------

-- ----------------------------
-- Table structure for `formid`
-- ----------------------------
DROP TABLE IF EXISTS `formid`;
CREATE TABLE `formid` (
  `formid` varchar(255) NOT NULL,
  `datetime` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of formid
-- ----------------------------

-- ----------------------------
-- Table structure for `likes`
-- ----------------------------
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
  `signinID` varchar(255) NOT NULL,
  `userid` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of likes
-- ----------------------------

-- ----------------------------
-- Table structure for `signin_record`
-- ----------------------------
DROP TABLE IF EXISTS `signin_record`;
CREATE TABLE `signin_record` (
  `signinID` varchar(255) NOT NULL,
  `userid` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `word` longtext NOT NULL,
  `likes_num` int(11) NOT NULL,
  `isPublic` char(2) NOT NULL,
  PRIMARY KEY (`signinID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of signin_record
-- ----------------------------

-- ----------------------------
-- Table structure for `users`
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `userid` int(255) NOT NULL,
  `openid` varchar(255) NOT NULL,
  `nickName` longtext NOT NULL,
  `avatarUrl` varchar(255) NOT NULL,
  `session_key` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `first_register` date NOT NULL COMMENT '首次授权日期',
  `beginTime` time DEFAULT NULL,
  `endTime` time DEFAULT NULL,
  `last_update` date DEFAULT NULL COMMENT '上次修改时间的日期',
  `is_robot` char(2) DEFAULT NULL COMMENT '是否为机器人',
  `last_signDate` date DEFAULT NULL COMMENT '上次签到时间',
  `sign_days` int(11) DEFAULT NULL COMMENT '累计签到天数',
  `running_days` int(11) DEFAULT NULL COMMENT '持续签到天数',
  `long_running_days` int(11) DEFAULT NULL COMMENT '最长持续签到天数',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of users
-- ----------------------------
