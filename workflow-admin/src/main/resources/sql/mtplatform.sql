/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 5.7.18-log : Database - mtplatform
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`mtplatform` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `mtplatform`;

/*Table structure for table `act_evt_log` */

DROP TABLE IF EXISTS `act_evt_log`;

/*Data for the table `act_ru_variable` */



/*Table structure for table `sys_department` */

DROP TABLE IF EXISTS `sys_department`;

CREATE TABLE `sys_department` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `department_code` varchar(20) NOT NULL COMMENT '部门编码',
  `department_name` varchar(40) NOT NULL COMMENT '部门名称',
  `department_icon` varchar(20) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `parent_id` varchar(20) DEFAULT NULL COMMENT '上级部门编码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `sequence` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='部门表';

/*Data for the table `sys_department` */

insert  into `sys_department`(`id`,`department_code`,`department_name`,`department_icon`,`description`,`parent_id`,`create_time`,`sequence`) values ('1','001','管理部门','fi-social-apple','管理部门','','2014-02-19 01:00:00',1),('5b5493a951674e568a6c501525eb779a','003','人事部门','fi-folder','人事部门','','2017-08-16 19:30:14',3),('9da171a27291411eaf1c16e2a09b9bd4','004','软件部门','fi-folder','软件部门','','2017-08-16 19:31:24',4),('a69ffd17a000465894b41530a3db149c','002','测试部门','fi-folder','测试部门','','2017-08-13 14:03:31',2);

/*Table structure for table `sys_oper_log` */

DROP TABLE IF EXISTS `sys_oper_log`;

CREATE TABLE `sys_oper_log` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `oper_user_id` varchar(32) DEFAULT NULL COMMENT '操作用户ID',
  `oper_user_name` varchar(40) DEFAULT NULL COMMENT '操作用户名',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `oper_client_ip` varchar(20) DEFAULT NULL COMMENT '客户端IP地址',
  `request_url` varchar(80) DEFAULT NULL COMMENT '请求地址',
  `request_method` varchar(500) DEFAULT NULL COMMENT '请求方法',
  `oper_event` varchar(500) DEFAULT NULL COMMENT '操作事件（删除，新增，修改，查询，登录，退出）',
  `oper_status` int(5) DEFAULT NULL COMMENT '操作状态（1：成功，2：失败）',
  `log_description` varchar(500) DEFAULT NULL COMMENT '描述信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='系统操作日志';

/*Data for the table `sys_oper_log` */



/*Table structure for table `sys_resource` */

DROP TABLE IF EXISTS `sys_resource`;

CREATE TABLE `sys_resource` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `resource_url` varchar(100) DEFAULT NULL COMMENT '资源路径',
  `resource_code` varchar(50) DEFAULT NULL COMMENT '资源编码',
  `resource_name` varchar(50) DEFAULT NULL COMMENT '资源名称',
  `resource_type` varchar(2) DEFAULT NULL COMMENT '资源类型',
  `resource_icon` varchar(30) DEFAULT NULL COMMENT '资源图标',
  `open_mode` varchar(10) DEFAULT NULL COMMENT '打开方式(ajax/iframe)',
  `parent_id` varchar(50) DEFAULT NULL COMMENT '上级资源编码',
  `sequence` int(20) DEFAULT '0' COMMENT '排序',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='菜单表';

/*Data for the table `sys_resource` */

INSERT INTO `sys_resource` VALUES ('013ce0d0b44c4380b7ecd8af65756f71', '', '023', '申请管理', '0', 'fi-page-multiple', 'ajax', '', '3', '2017-08-18 16:16:55');
INSERT INTO `sys_resource` VALUES ('1', '', '001', '系统管理', '0', 'fi-folder', 'ajax', '', '0', null);
INSERT INTO `sys_resource` VALUES ('1bc000c4567c4ec6a9d304a0725d1cf6', '/sysDepartment/delete', '016', '删除', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', '2', '2017-08-12 17:25:18');
INSERT INTO `sys_resource` VALUES ('1be8588cf60a4c0a95211b5dbfab5bc7', '', '021', '流程管理', '0', 'fi-thumbnails', 'ajax', '', '2', '2017-08-17 10:37:21');
INSERT INTO `sys_resource` VALUES ('2', '/sysResource/manager', '002', '资源管理', '0', 'fi-database', 'ajax', '1', '1', null);
INSERT INTO `sys_resource` VALUES ('202edbee609e48fc97069c5cb91d8b36', '/tVacation/getComments', '035', '查看审批进度', '1', '', 'ajax', 'effb8eb1d75a46f1a31f1a435bf4577c', '2', '2017-08-26 17:11:03');
INSERT INTO `sys_resource` VALUES ('2275d7959c0b40d5897981f059c75200', '/sysUser/edit', '009', '编辑', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', '3', '2017-08-12 11:19:23');
INSERT INTO `sys_resource` VALUES ('294f8355c701400996c0e86db6815e85', '/activiti/claimTask', '029', '签收', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '1', '2017-08-20 22:25:30');
INSERT INTO `sys_resource` VALUES ('3', '/sysResource/add', '003', '添加', '1', '', 'ajax', '2', '1', null);
INSERT INTO `sys_resource` VALUES ('3323b161301747fe8892f6338924a53f', '/sysOperLog/manager', '036', '操作日志', '0', 'fi-database', null, '8a1a2f49ad8b431e8155ca5fbcc65ed5', '1', '2017-08-28 00:03:36');
INSERT INTO `sys_resource` VALUES ('397c47b362f84ddcb5c7679201366333', '/sysUser/delete', '008', '删除', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', '2', '2017-08-12 11:17:01');
INSERT INTO `sys_resource` VALUES ('3a0e79e02205402a956a78ea95f9c752', '/activiti/transferTask', '032', '转办', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '4', '2017-08-20 22:27:43');
INSERT INTO `sys_resource` VALUES ('3d6f03d570c5411284c57d60216ad76e', '/activiti/active', '037', '激活', '1', '', null, '98f0c0b5e51248b18f2f1ebd07cbd25c', '3', '2017-09-08 16:33:29');
INSERT INTO `sys_resource` VALUES ('4', '/sysRsource/delete', '004', '删除', '1', '', 'ajax', '2', '2', null);
INSERT INTO `sys_resource` VALUES ('41077feebcfd47caab1bc51d838735a0', '/tVacation/add', '025', '发起申请', '1', '', 'ajax', 'effb8eb1d75a46f1a31f1a435bf4577c', '1', '2017-08-18 18:16:29');
INSERT INTO `sys_resource` VALUES ('4896a4e8a90d478390bd0b4bdbb8fe0d', '/sysRole/add', '011', '添加', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '1', '2017-08-12 12:59:38');
INSERT INTO `sys_resource` VALUES ('4c32091fad9548f6b737b574090c4c5d', '/tUserTask/manager', '022', '流程配置', '0', 'fi-widget', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', '2', '2017-08-18 15:54:18');
INSERT INTO `sys_resource` VALUES ('5', '/sysRsource/edit', '005', '编辑', '1', '', 'ajax', '2', '3', null);
INSERT INTO `sys_resource` VALUES ('670bb8d8a8bf4e4f9046b8da2bc7d124', '/sysDepartment/manager', '014', '部门管理', '0', 'fi-thumbnails', 'ajax', '1', '4', '2017-08-12 13:05:13');
INSERT INTO `sys_resource` VALUES ('7595d0149dc049d68c2b89ab999ad882', '/activiti/taskManager', '028', '我的任务', '0', 'fi-list-thumbnails', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', '3', '2017-08-20 17:22:53');
INSERT INTO `sys_resource` VALUES ('7929d9b9a356491693c90f2068daeb70', '/sysRole/delete', '012', '删除', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '2', '2017-08-12 13:01:42');
INSERT INTO `sys_resource` VALUES ('8a1a2f49ad8b431e8155ca5fbcc65ed5', '', '018', '系统维护', '0', 'fi-torso-business', 'ajax', '', '1', '2017-08-12 18:35:12');
INSERT INTO `sys_resource` VALUES ('8cd65d6021e5425ab55a7e22e66b27a1', '/sysRole/manager', '010', '角色管理', '0', 'fi-torso-business', 'ajax', '1', '3', '2017-08-12 12:58:38');
INSERT INTO `sys_resource` VALUES ('98f0c0b5e51248b18f2f1ebd07cbd25c', '/activiti/processdefManager', '027', '流程定义', '0', 'fi-shuffle', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', '1', '2017-08-19 14:26:28');
INSERT INTO `sys_resource` VALUES ('a5c29bad8b804849892787546f70d010', '/activiti/hisTaskManager', '034', '我的已办', '0', 'fi-checkbox', null, '1be8588cf60a4c0a95211b5dbfab5bc7', '4', '2017-08-31 21:01:47');
INSERT INTO `sys_resource` VALUES ('a6c1d1c19147411fbddc534f85f82b91', '/icons', '019', '系统图标', '0', 'fi-photo', 'ajax', '8a1a2f49ad8b431e8155ca5fbcc65ed5', '2', '2017-08-12 18:42:12');
INSERT INTO `sys_resource` VALUES ('a8bebd9c95d0484d92cbe368cb5081cf', '/activiti/deploy', '028', '流程部署', '1', '', 'ajax', '98f0c0b5e51248b18f2f1ebd07cbd25c', '1', '2017-08-19 14:37:21');
INSERT INTO `sys_resource` VALUES ('af6a5e6f09124fa4aa010c3010165d0b', '/sysRole/grant', '020', '授权', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '4', '2017-08-12 22:19:36');
INSERT INTO `sys_resource` VALUES ('b13e0b774e6445c28b13536dc716bac5', '/sysRole/edit', '013', '编辑', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '3', '2017-08-12 13:02:39');
INSERT INTO `sys_resource` VALUES ('b7a1a2cf219d48178a450c71a45f3c45', '/sysUser/add', '007', '添加', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', '1', '2017-08-12 11:12:41');
INSERT INTO `sys_resource` VALUES ('c2ac8926811a41908f74bf8dcef385ab', '/activiti/delegateTask', '031', '委派', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '3', '2017-08-20 22:27:03');
INSERT INTO `sys_resource` VALUES ('c6bcb99c20e7403787442e5f7aa68956', '/tVacation/getProcessImage', '038', '查看流程图', '1', '', null, 'effb8eb1d75a46f1a31f1a435bf4577c', '3', '2017-09-13 19:00:56');
INSERT INTO `sys_resource` VALUES ('cea8161aba674721b3812078e1d611da', '/activiti/jumpTask', '033', '跳转', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '5', '2017-08-20 22:28:25');
INSERT INTO `sys_resource` VALUES ('da9ce84c01a840ca8a71086dc64f2436', '/activiti/complateTask', '030', '办理', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '2', '2017-08-20 22:26:19');
INSERT INTO `sys_resource` VALUES ('db0660daedcd48b0bfd7bfd724137e31', '/activiti/sleep', '036', '挂起', '1', '', null, '98f0c0b5e51248b18f2f1ebd07cbd25c', '2', '2017-09-08 16:32:28');
INSERT INTO `sys_resource` VALUES ('eb6fc407610842219f7e94dcf1fa82ea', '/sysDepartment/edit', '017', '编辑', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', '3', '2017-08-12 17:27:42');
INSERT INTO `sys_resource` VALUES ('eeea90fb6bf346888592bd5ccbe5e475', '/sysUser/manager', '006', '用户管理', '0', 'fi-torsos-all', 'iframe', '1', '2', '2017-08-12 10:52:23');
INSERT INTO `sys_resource` VALUES ('effb8eb1d75a46f1a31f1a435bf4577c', '/tVacation/manager', '024', '请假申请', '0', 'fi-universal-access', 'ajax', '013ce0d0b44c4380b7ecd8af65756f71', '1', '2017-08-18 16:25:55');
INSERT INTO `sys_resource` VALUES ('f4de27ddadf340539ced1f9c0c0e307b', '/tUserTask/configUser', '034', '设定人员', '1', '', 'ajax', '4c32091fad9548f6b737b574090c4c5d', '1', '2017-08-23 12:16:28');
INSERT INTO `sys_resource` VALUES ('f84a8562694e4b8ebd67f5ec39f2e4b3', '/activiti/model/modelManager', 'NO201711030001', '模型管理', '0', 'fi-paint-bucket', null, '1be8588cf60a4c0a95211b5dbfab5bc7', '0', '2017-11-03 17:42:16');
INSERT INTO `sys_resource` VALUES ('fa5459a821b34a30b07a676faaa806ae', '/sysDepartment/add', '015', '添加', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', '1', '2017-08-12 13:55:46');


/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `role_code` varchar(30) DEFAULT NULL COMMENT '角色编码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `role_name` varchar(40) NOT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

/*Data for the table `sys_role` */

insert  into `sys_role`(`id`,`role_code`,`create_time`,`description`,`role_name`) values ('1','001','2014-02-19 01:00:00','管理员','系统管理员'),('492cdebdbbe74ad98d047e627b696c66','005',NULL,'总经理','总经理'),('543c56799ed14d8d93294f1d2e93626d','006',NULL,'普通员工','普通员工'),('979e0715942e4cc09fc4a78e6c3544e7','003',NULL,'部门经理','部门经理'),('e1fa3ea4aff84a8ebf3aebcfc5c6d4fa','004',NULL,'项目经理','项目经理'),('eb5cb38d0102457f8cfe4764198dbeae','002',NULL,'测试','软件测试');

/*Table structure for table `sys_role_resource` */

DROP TABLE IF EXISTS `sys_role_resource`;

CREATE TABLE `sys_role_resource` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `resource_id` varchar(32) NOT NULL COMMENT '菜单外键',
  `role_id` varchar(32) NOT NULL COMMENT '角色外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色菜单表';

/*Data for the table `sys_role_resource` */

insert  into `sys_role_resource`(`id`,`resource_id`,`role_id`) values ('032f4e084bc74c839a68efb7bb6438eb','a6c1d1c19147411fbddc534f85f82b91','1'),('0473de61dc104c94bdbc26385132e132','98f0c0b5e51248b18f2f1ebd07cbd25c','1'),('12aeae3ae585479fb1d3ff4952c2bb30','294f8355c701400996c0e86db6815e85','e1fa3ea4aff84a8ebf3aebcfc5c6d4fa'),('1fc54bf2a7ca43cbb896d8bc397ecb1d','3','1'),('2faaf0136a964938b8130b827ececbd5','c6bcb99c20e7403787442e5f7aa68956','1'),('3477076ea8f249a5be8e7e2294fd9ce3','3d6f03d570c5411284c57d60216ad76e','1'),('3548cd408d874d1eaa3afeba6e7d8172','7595d0149dc049d68c2b89ab999ad882','543c56799ed14d8d93294f1d2e93626d'),('3b129bcdb17a4e05ae6a2f022262d4c9','eb6fc407610842219f7e94dcf1fa82ea','1'),('3c3bc295fd7c4f82add81dfc49c78ed4','eeea90fb6bf346888592bd5ccbe5e475','1'),('3f41495619d14b5492b6d502ce74c0a6','1be8588cf60a4c0a95211b5dbfab5bc7','979e0715942e4cc09fc4a78e6c3544e7'),('45f0ada300cd4a47981c44657ddafe1b','294f8355c701400996c0e86db6815e85','543c56799ed14d8d93294f1d2e93626d'),('56ea84336468494d93af91479f6c540a','397c47b362f84ddcb5c7679201366333','1'),('57281ca02fd343c48b6bd14f96bec254','1bc000c4567c4ec6a9d304a0725d1cf6','1'),('58da1b713d5947deae8543978f8826b0','2','1'),('5a8ebf8e150f4e6985e149398180cbfe','','eb5cb38d0102457f8cfe4764198dbeae'),('621fc861f0184d5e9e87ad37a5d5e78d','1','1'),('6661046104714495903332f0410214fa','da9ce84c01a840ca8a71086dc64f2436','543c56799ed14d8d93294f1d2e93626d'),('6a6e49b0824a469bbb5185946cdd58e5','a5c29bad8b804849892787546f70d010','e1fa3ea4aff84a8ebf3aebcfc5c6d4fa'),('71e637135b354f67b9c62e6408270347','da9ce84c01a840ca8a71086dc64f2436','492cdebdbbe74ad98d047e627b696c66'),('742cc7bc79a74c1e8f21bba5293317fb','4896a4e8a90d478390bd0b4bdbb8fe0d','1'),('7649ee93281f4e90af71a2cfc0ed28fa','7595d0149dc049d68c2b89ab999ad882','e1fa3ea4aff84a8ebf3aebcfc5c6d4fa'),('79da2432940f4629a5e7c8047828a625','b7a1a2cf219d48178a450c71a45f3c45','1'),('7bf6fa9debd442ddaa095894026575bc','013ce0d0b44c4380b7ecd8af65756f71','1'),('7cffe53b6a0e463384608867da2fc9e4','a8bebd9c95d0484d92cbe368cb5081cf','1'),('80eef4785e6f426b9ca5b0af0f9d4e7d','8a1a2f49ad8b431e8155ca5fbcc65ed5','1'),('88707bfe301e434590cb48de335acde6','da9ce84c01a840ca8a71086dc64f2436','1'),('8add10dc45c34df9bbd62af155b68c50','294f8355c701400996c0e86db6815e85','492cdebdbbe74ad98d047e627b696c66'),('8e3993df070740f6ab56f0e3407c2466','7595d0149dc049d68c2b89ab999ad882','1'),('8e4b5ba6196243d2ac9e0ff88eb58057','7595d0149dc049d68c2b89ab999ad882','979e0715942e4cc09fc4a78e6c3544e7'),('8ed22fa90ff141b7a149857ca6a75a92','a5c29bad8b804849892787546f70d010','979e0715942e4cc09fc4a78e6c3544e7'),('91d3ca36c8804ec88e434d87480d22de','af6a5e6f09124fa4aa010c3010165d0b','1'),('9a65ed774ed34877b2674596b16750d5','b13e0b774e6445c28b13536dc716bac5','1'),('9b9611d585484bea98cda71284fe9f8c','670bb8d8a8bf4e4f9046b8da2bc7d124','1'),('ab63f790923a4cedb41ffb531164bb2f','da9ce84c01a840ca8a71086dc64f2436','979e0715942e4cc09fc4a78e6c3544e7'),('b23acaf15db64c2a927ab9298aa4ab6a','2275d7959c0b40d5897981f059c75200','1'),('b389ae3ad0fc4e3f9a01fef1b7afc758','8cd65d6021e5425ab55a7e22e66b27a1','1'),('b9e976fe631a4df9a869784a72bbd17b','7595d0149dc049d68c2b89ab999ad882','492cdebdbbe74ad98d047e627b696c66'),('cf0145360019499b8aab887d292b4c67','a5c29bad8b804849892787546f70d010','543c56799ed14d8d93294f1d2e93626d'),('d072a5950a034cffbbf0a296f5a60b11','4','1'),('d07874850cfa44dfa7c2c5a8ac89531e','5','1'),('d0d02303ae76450ba4843161348ac166','effb8eb1d75a46f1a31f1a435bf4577c','1'),('d1cb98b1183e4f3bb5aed67deb91392a','f4de27ddadf340539ced1f9c0c0e307b','1'),('d76d0658342548098bcda62c4ff0b63b','294f8355c701400996c0e86db6815e85','1'),('da277df902c14443921497af25091c4a','3323b161301747fe8892f6338924a53f','1'),('dbca3be41cfd4e74aae8545b647c28ce','fa5459a821b34a30b07a676faaa806ae','1'),('ddb6bbfd15694a388ef5e7b990982078','da9ce84c01a840ca8a71086dc64f2436','e1fa3ea4aff84a8ebf3aebcfc5c6d4fa'),('e269b3a186e94b73b841adc9760a9271','1be8588cf60a4c0a95211b5dbfab5bc7','1'),('e99de6fd20254ce086ba5571bcbace46','4c32091fad9548f6b737b574090c4c5d','1'),('e9edd710fdf84080bf3aab21dbc929cc','a5c29bad8b804849892787546f70d010','1'),('ea28a69b893f45fba894443ae5009885','294f8355c701400996c0e86db6815e85','979e0715942e4cc09fc4a78e6c3544e7'),('ecdc990352d84aeca7c57b28140c137e','7929d9b9a356491693c90f2068daeb70','1'),('ed7af00b4b484c339e428548cb06fa64','202edbee609e48fc97069c5cb91d8b36','1'),('f635616287394f5aab099e6f0af9a122','db0660daedcd48b0bfd7bfd724137e31','1'),('f6ec9f9ec5234e8594c7deb0abb87dca','41077feebcfd47caab1bc51d838735a0','1');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `login_name` varchar(30) NOT NULL COMMENT '登录名',
  `login_pwd` varchar(65) NOT NULL COMMENT '登录密码',
  `user_name` varchar(20) DEFAULT NULL COMMENT '昵称',
  `user_phone` varchar(20) DEFAULT NULL COMMENT '手机',
  `user_email` varchar(30) DEFAULT NULL COMMENT '邮箱',
  `user_type` varchar(2) DEFAULT NULL COMMENT '用户类型(0:管理员 1:用户)',
  `user_sex` varchar(2) DEFAULT NULL COMMENT '性别',
  `register_time` datetime DEFAULT NULL COMMENT '注册时间',
  `department_id` varchar(32) DEFAULT NULL COMMENT '部门外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

/*Data for the table `sys_user` */

insert  into `sys_user`(`id`,`login_name`,`login_pwd`,`user_name`,`user_phone`,`user_email`,`user_type`,`user_sex`,`register_time`,`department_id`) values ('1','admin','E10ADC3949BA59ABBE56E057F20F883E','管理员','123456','liujunyang@moutum.com','0','0','2014-02-19 01:00:00','1'),('474693d0b9a345feb6a07479019c9ff5','litao','E10ADC3949BA59ABBE56E057F20F883E','李涛','434343123','litao@moutum.com','1','0','2017-08-16 19:53:21','1'),('6414f0ca9eaf4ba596736eb7db0ad157','shiluyao','E10ADC3949BA59ABBE56E057F20F883E','史路遥','4343833434343','shiluyao@moutum.com','1','0','2017-08-16 19:45:05','9da171a27291411eaf1c16e2a09b9bd4'),('9baf237adfb6407ba529382fc8f7aa7b','wangzhao','E10ADC3949BA59ABBE56E057F20F883E','王钊','1590000111123','wangzhao@moutum.com','1','0','2017-09-13 09:14:49','1'),('a29de2c7d5eb4f269c2fc14363810f9c','longxinxin','E10ADC3949BA59ABBE56E057F20F883E','龙鑫鑫','3434355','longxinxin@moutum.com','1','1','2017-08-16 19:47:57','5b5493a951674e568a6c501525eb779a'),('c28fb2ff582d484ea77692279ae56fff','tongliang','E10ADC3949BA59ABBE56E057F20F883E','同亮','43434335','tongliang@moutum.com','1','0','2017-08-16 19:43:25','1'),('cf42f07adc69455b94e82f8ce06de09e','test','E10ADC3949BA59ABBE56E057F20F883E','测试员','123123','liujunyang@moutum.com','1','0','2017-08-13 14:05:04','a69ffd17a000465894b41530a3db149c'),('d7dadc0fd4fa40e9be886ca966522614','liujunyang','E10ADC3949BA59ABBE56E057F20F883E','刘俊阳','43434365','liujunyang@moutum.com','1','0','2017-08-16 19:52:31','9da171a27291411eaf1c16e2a09b9bd4');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户外键',
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户角色关联表';

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`id`,`user_id`,`role_id`) values ('8dfd4328d9054004a5a98325fcb391af','6414f0ca9eaf4ba596736eb7db0ad157','979e0715942e4cc09fc4a78e6c3544e7'),('955dc5a9f4d945e18a93a7c8c165ee9e','9baf237adfb6407ba529382fc8f7aa7b','e1fa3ea4aff84a8ebf3aebcfc5c6d4fa'),('9c5636622c8e4b609e0ac4bd9d848a03','474693d0b9a345feb6a07479019c9ff5','492cdebdbbe74ad98d047e627b696c66'),('9f0e1b06582f48f3847d07aa94f5a2af','c28fb2ff582d484ea77692279ae56fff','e1fa3ea4aff84a8ebf3aebcfc5c6d4fa'),('b0aa7180c2f1400dba95ab43b5d4d59e','1','1'),('b658259dfadb4a498f650b6cbb708276','cf42f07adc69455b94e82f8ce06de09e','543c56799ed14d8d93294f1d2e93626d'),('cd2f4398338d4be187f094d33f67b263','a29de2c7d5eb4f269c2fc14363810f9c','979e0715942e4cc09fc4a78e6c3544e7'),('fc78e9c596fe4b5e9de87aaac880d0ab','d7dadc0fd4fa40e9be886ca966522614','543c56799ed14d8d93294f1d2e93626d');

/*Table structure for table `t_mail_log` */

DROP TABLE IF EXISTS `t_mail_log`;

CREATE TABLE `t_mail_log` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `mail_to` varchar(44) DEFAULT NULL COMMENT '邮件的接收者。可使用逗号分隔多个接收者',
  `mail_from` varchar(44) DEFAULT NULL COMMENT '邮件发送者的地址。如果不提供，会使用默认配置的地址。',
  `mail_subject` varchar(255) DEFAULT NULL COMMENT '邮件的主题',
  `maill_cc` varchar(255) DEFAULT NULL COMMENT '邮件抄送人。可使用逗号分隔多个接收者。',
  `mail_bcc` varchar(255) DEFAULT NULL COMMENT '邮件暗送人。可使用逗号分隔多个接收者。',
  `mail_text` text COMMENT '邮件的文本内容。',
  `send_time` datetime DEFAULT NULL COMMENT '邮件发送的时间。',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='邮件发送日志';

/*Data for the table `t_mail_log` */



/*Table structure for table `t_user_task` */

DROP TABLE IF EXISTS `t_user_task`;

CREATE TABLE `t_user_task` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `proc_def_key` varchar(255) DEFAULT NULL COMMENT '流程定义KEY',
  `proc_def_name` varchar(255) DEFAULT NULL COMMENT '流程定义名称',
  `task_def_key` varchar(255) DEFAULT NULL COMMENT '任务定义KEY',
  `task_name` varchar(255) DEFAULT NULL COMMENT '任务名称',
  `task_type` varchar(255) DEFAULT NULL COMMENT '任务类型',
  `candidate_name` varchar(255) DEFAULT NULL COMMENT '[受理人,候选人,候选组]名称',
  `candidate_ids` varchar(255) DEFAULT NULL COMMENT '[受理人,候选人,候选组]ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户任务表';

/*Data for the table `t_user_task` */


/*Table structure for table `t_vacation` */

DROP TABLE IF EXISTS `t_vacation`;

CREATE TABLE `t_vacation` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `vacation_code` varchar(40) DEFAULT NULL COMMENT '请假单号',
  `apply_date` datetime DEFAULT NULL COMMENT '申请日期',
  `begin_date` datetime DEFAULT NULL COMMENT '开始日期',
  `work_days` int(11) DEFAULT NULL COMMENT '请假天数',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `proc_inst_id` varchar(255) DEFAULT NULL COMMENT '流程实例ID',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `vacation_reason` varchar(255) DEFAULT NULL COMMENT '请假原因',
  `vacation_status` int(10) DEFAULT NULL COMMENT '请假状态',
  `vacation_type` int(10) DEFAULT NULL COMMENT '请假类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='请假表';

/*Data for the table `t_vacation` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
