/*
Navicat MySQL Data Transfer

Source Server         : 商城2.0（A开发）
Source Server Version : 50637
Source Host           : 192.168.10.64:3333
Source Database       : ymkj-order

Target Server Type    : MYSQL
Target Server Version : 50637
File Encoding         : 65001

Date: 2019-05-23 14:26:18
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for income_subject
-- ----------------------------
DROP TABLE IF EXISTS `income_subject`;
CREATE TABLE `income_subject` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `income_subject_name` varchar(255) NOT NULL COMMENT '收益主体名称',
  `income_subject_code` varchar(255) NOT NULL COMMENT '收益主体类型code',
  `settlement_subject_name` varchar(255) NOT NULL COMMENT '结算主体名称',
  `settlement_subject_code` varchar(255) NOT NULL COMMENT '结算主体类型code',
  `creator` varchar(50) NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `updater` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COMMENT='收益主体表';

-- ----------------------------
-- Records of income_subject
-- ----------------------------
INSERT INTO `income_subject` VALUES ('1', '翼猫总部', 'MAIN_COMPANY', '翼猫科技发展（上海）有限公司', 'MAIN_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('2', '省级公司', 'PROVINCE_COMPANY', '省级公司', 'PROVINCE_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('3', '市级公司', 'CITY_COMPANY', '市级公司', 'CITY_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('4', '产品公司', 'PRODUCT_COMPANY', '产品公司', 'PRODUCT_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('5', '市级合伙人', 'CITY_PARTNER', '市级合伙人', 'CITY_PARTNER', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('6', '市级发起人', 'CITY_SPONSOR', '市级发起人', 'CITY_SPONSOR', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('7', '区县级公司（推荐人）', 'RECOMMEND_STATION_COMPANY', '区县级公司（推荐人）', 'RECOMMEND_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('8', '智慧助理', 'ASSISTANT', '区县级公司（推荐人）', 'RECOMMEND_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('9', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('10', '区县级发起人', 'REGION_SPONSOR', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('11', '服务站承包人（产品收益）', 'STATION_CONTRACTOR_PRODUCT', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('12', '服务站承包人（服务收益）', 'STATION_CONTRACTOR_SERVICE', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('13', '服务站站长', 'STATION_MASTER', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('14', '区县级股东（推荐人）', 'REGION_SHAREHOLDER', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('15', '经销商', 'DISTRIBUTOR', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('16', '分销用户', 'DISTRIBUTOR_USER', '区县级公司（经销商）', 'DISTRIBUTOR_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('17', '区县级公司（安装工）', 'ENGINEER_STATION_COMPANY', '区县级公司（安装工）', 'ENGINEER_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
INSERT INTO `income_subject` VALUES ('18', '安装服务人员', 'ENGINEER', '区县级公司（安装工）', 'ENGINEER_STATION_COMPANY', '系统', '2019-01-09 16:49:22', null, null);
