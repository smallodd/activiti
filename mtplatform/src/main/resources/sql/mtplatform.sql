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

insert  into `sys_resource`(`id`,`resource_url`,`resource_code`,`resource_name`,`resource_type`,`resource_icon`,`open_mode`,`parent_id`,`sequence`,`create_time`) values ('013ce0d0b44c4380b7ecd8af65756f71','','023','申请管理','0','fi-page-multiple','ajax','',3,'2017-08-18 16:16:55'),('1','','001','系统管理','0','fi-folder','ajax','',0,NULL),('1bc000c4567c4ec6a9d304a0725d1cf6','/sysDepartment/delete','016','删除','1','','ajax','670bb8d8a8bf4e4f9046b8da2bc7d124',2,'2017-08-12 17:25:18'),('1be8588cf60a4c0a95211b5dbfab5bc7','','021','流程管理','0','fi-thumbnails','ajax','',2,'2017-08-17 10:37:21'),('2','/sysResource/manager','002','资源管理','0','fi-database','ajax','1',1,NULL),('202edbee609e48fc97069c5cb91d8b36','/tVacation/getComments','035','查看审批进度','1','','ajax','effb8eb1d75a46f1a31f1a435bf4577c',2,'2017-08-26 17:11:03'),('2275d7959c0b40d5897981f059c75200','/sysUser/edit','009','编辑','1','','ajax','eeea90fb6bf346888592bd5ccbe5e475',3,'2017-08-12 11:19:23'),('294f8355c701400996c0e86db6815e85','/activiti/claimTask','029','签收','1','','ajax','7595d0149dc049d68c2b89ab999ad882',1,'2017-08-20 22:25:30'),('3','/sysResource/add','003','添加','1','','ajax','2',1,NULL),('3323b161301747fe8892f6338924a53f','/sysOperLog/manager','036','操作日志','0','fi-database',NULL,'8a1a2f49ad8b431e8155ca5fbcc65ed5',1,'2017-08-28 00:03:36'),('397c47b362f84ddcb5c7679201366333','/sysUser/delete','008','删除','1','','ajax','eeea90fb6bf346888592bd5ccbe5e475',2,'2017-08-12 11:17:01'),('3a0e79e02205402a956a78ea95f9c752','/activiti/transferTask','032','转办','1','','ajax','7595d0149dc049d68c2b89ab999ad882',4,'2017-08-20 22:27:43'),('3d6f03d570c5411284c57d60216ad76e','/activiti/active','037','激活','1','',NULL,'98f0c0b5e51248b18f2f1ebd07cbd25c',3,'2017-09-08 16:33:29'),('4','/sysRsource/delete','004','删除','1','','ajax','2',2,NULL),('41077feebcfd47caab1bc51d838735a0','/tVacation/add','025','发起申请','1','','ajax','effb8eb1d75a46f1a31f1a435bf4577c',1,'2017-08-18 18:16:29'),('4896a4e8a90d478390bd0b4bdbb8fe0d','/sysRole/add','011','添加','1','','ajax','8cd65d6021e5425ab55a7e22e66b27a1',1,'2017-08-12 12:59:38'),('4c32091fad9548f6b737b574090c4c5d','/tUserTask/manager','022','流程配置','0','fi-widget','ajax','1be8588cf60a4c0a95211b5dbfab5bc7',2,'2017-08-18 15:54:18'),('5','/sysRsource/edit','005','编辑','1','','ajax','2',3,NULL),('670bb8d8a8bf4e4f9046b8da2bc7d124','/sysDepartment/manager','014','部门管理','0','fi-thumbnails','ajax','1',4,'2017-08-12 13:05:13'),('7595d0149dc049d68c2b89ab999ad882','/activiti/taskManager','028','我的任务','0','fi-list-thumbnails','ajax','1be8588cf60a4c0a95211b5dbfab5bc7',3,'2017-08-20 17:22:53'),('7929d9b9a356491693c90f2068daeb70','/sysRole/delete','012','删除','1','','ajax','8cd65d6021e5425ab55a7e22e66b27a1',2,'2017-08-12 13:01:42'),('8a1a2f49ad8b431e8155ca5fbcc65ed5','','018','系统维护','0','fi-torso-business','ajax','',1,'2017-08-12 18:35:12'),('8cd65d6021e5425ab55a7e22e66b27a1','/sysRole/manager','010','角色管理','0','fi-torso-business','ajax','1',3,'2017-08-12 12:58:38'),('98f0c0b5e51248b18f2f1ebd07cbd25c','/activiti/processdefManager','027','流程定义','0','fi-shuffle','ajax','1be8588cf60a4c0a95211b5dbfab5bc7',1,'2017-08-19 14:26:28'),('a5c29bad8b804849892787546f70d010','/activiti/hisTaskManager','034','我的已办','0','fi-checkbox',NULL,'1be8588cf60a4c0a95211b5dbfab5bc7',4,'2017-08-31 21:01:47'),('a6c1d1c19147411fbddc534f85f82b91','/icons','019','系统图标','0','fi-photo','ajax','8a1a2f49ad8b431e8155ca5fbcc65ed5',2,'2017-08-12 18:42:12'),('a8bebd9c95d0484d92cbe368cb5081cf','/activiti/deploy','028','流程部署','1','','ajax','98f0c0b5e51248b18f2f1ebd07cbd25c',1,'2017-08-19 14:37:21'),('af6a5e6f09124fa4aa010c3010165d0b','/sysRole/grant','020','授权','1','','ajax','8cd65d6021e5425ab55a7e22e66b27a1',4,'2017-08-12 22:19:36'),('b13e0b774e6445c28b13536dc716bac5','/sysRole/edit','013','编辑','1','','ajax','8cd65d6021e5425ab55a7e22e66b27a1',3,'2017-08-12 13:02:39'),('b7a1a2cf219d48178a450c71a45f3c45','/sysUser/add','007','添加','1','','ajax','eeea90fb6bf346888592bd5ccbe5e475',1,'2017-08-12 11:12:41'),('c2ac8926811a41908f74bf8dcef385ab','/activiti/delegateTask','031','委派','1','','ajax','7595d0149dc049d68c2b89ab999ad882',3,'2017-08-20 22:27:03'),('c6bcb99c20e7403787442e5f7aa68956','/tVacation/getProcessImage','038','查看流程图','1','',NULL,'effb8eb1d75a46f1a31f1a435bf4577c',3,'2017-09-13 19:00:56'),('cea8161aba674721b3812078e1d611da','/activiti/jumpTask','033','跳转','1','','ajax','7595d0149dc049d68c2b89ab999ad882',5,'2017-08-20 22:28:25'),('da9ce84c01a840ca8a71086dc64f2436','/activiti/complateTask','030','办理','1','','ajax','7595d0149dc049d68c2b89ab999ad882',2,'2017-08-20 22:26:19'),('db0660daedcd48b0bfd7bfd724137e31','/activiti/sleep','036','挂起','1','',NULL,'98f0c0b5e51248b18f2f1ebd07cbd25c',2,'2017-09-08 16:32:28'),('eb6fc407610842219f7e94dcf1fa82ea','/sysDepartment/edit','017','编辑','1','','ajax','670bb8d8a8bf4e4f9046b8da2bc7d124',3,'2017-08-12 17:27:42'),('eeea90fb6bf346888592bd5ccbe5e475','/sysUser/manager','006','用户管理','0','fi-torsos-all','iframe','1',2,'2017-08-12 10:52:23'),('effb8eb1d75a46f1a31f1a435bf4577c','/tVacation/manager','024','请假申请','0','fi-universal-access','ajax','013ce0d0b44c4380b7ecd8af65756f71',1,'2017-08-18 16:25:55'),('f4de27ddadf340539ced1f9c0c0e307b','/tUserTask/configUser','034','设定人员','1','','ajax','4c32091fad9548f6b737b574090c4c5d',1,'2017-08-23 12:16:28'),('fa5459a821b34a30b07a676faaa806ae','/sysDepartment/add','015','添加','1','','ajax','670bb8d8a8bf4e4f9046b8da2bc7d124',1,'2017-08-12 13:55:46');

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

insert  into `t_mail_log`(`id`,`mail_to`,`mail_from`,`mail_subject`,`maill_cc`,`mail_bcc`,`mail_text`,`send_time`) values ('4609698d09e240cb9b16eada7f4dfac1','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'人事部门的龙鑫鑫于2017-10-19 13:28:56已经办理了您的请假申请，办理的结果为:办理通过，办理人的批注信息为:同意了。','2017-10-19 13:28:58'),('5fd8e241c42b41bfbeb15ab3f42b6b3a','管理员','龙鑫鑫','请假业务办理进度',NULL,NULL,'人事部门的龙鑫鑫于2017-10-19 15:46:39已经办理了您的单号为SN201710170001请假申请，办理的结果为:办理通过，办理人的批注信息为:同意啦。','2017-10-19 15:46:40'),('8a49c34f7e064deb99693905701097b6','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'软件部门的史路遥于2017-10-19 13:27:55已经办理了您的请假申请，办理的结果为:办理通过，办理人的批注信息为:同意啦。','2017-10-19 13:27:57'),('9438ea042b594b508d1b5b92be241817','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'软件部门的史路遥于2017-10-19 12:19:33已经办理了您的请假申请，办理的结果为:办理通过，办理人的批注信息为:。','2017-10-19 12:19:35'),('9c1ddfcf35094054b7abac14e5bf74aa','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'软件部门的史路遥于2017-10-19 12:25:31已经办理了您的请假申请，办理的结果为:办理通过，办理人的批注信息为:。','2017-10-19 12:25:33'),('aa9e11649dcb4c278589c11599d40206','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'软件部门的史路遥于2017-10-19 13:55:03已经办理了您的单号为SN201710170001请假申请，办理的结果为:办理通过，办理人的批注信息为:同意啦。','2017-10-19 13:55:05'),('c0ca2085db0a4a8b8a5629fbec9dfff9','管理员','史路遥','请假业务办理进度',NULL,NULL,'软件部门的史路遥于2017-10-19 15:40:15已经办理了您的单号为SN201710170001请假申请，办理的结果为:办理通过，办理人的批注信息为:同意。','2017-10-19 15:40:18'),('c4f50b2275c34b8aa6cbdbaca56eb81c','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'史路遥于2017-10-19 12:09:09已经办理了您的请假申请，办理的结果为:办理不通过办理人的批注信息为:是否会发送成功','2017-10-19 12:09:11'),('c63609c633cf4f4983e22bcbe546cc73','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'人事部门的龙鑫鑫于2017-10-19 12:36:16已经办理了您的请假申请，办理的结果为:办理通过，办理人的批注信息为:通过。','2017-10-19 12:36:18'),('e7d1c1bd2f554c958ba565a956ead3a5','2205511679@qq.com','2205511679@qq.com','请假业务办理进度',NULL,NULL,'人事部门的龙鑫鑫于2017-10-19 13:29:30已经办理了您的请假申请，办理的结果为:办理通过，办理人的批注信息为:同意啦。','2017-10-19 13:29:32');

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

insert  into `t_vacation`(`id`,`vacation_code`,`apply_date`,`begin_date`,`work_days`,`end_date`,`proc_inst_id`,`user_id`,`vacation_reason`,`vacation_status`,`vacation_type`) values ('001ba4c620144e5d937fea9cbddf3656','SN201710170004','2017-10-17 17:14:46','2017-10-18 07:14:33',3,'2017-10-19 07:14:35','317501','1','测试',2,1),('01b2b6818a894cbcb7245e0f8d598a25',NULL,'2017-09-22 11:34:26','2017-09-23 01:34:10',3,'2017-09-29 01:34:12','310058','1','测试请假',2,1),('089aec9ddcc94722abb929d92789ea06','SN201710170002','2017-10-17 17:09:34','2017-10-18 07:09:22',3,'2017-10-20 07:09:25','315012','1','测试',2,1),('09878b1814534a4ab7e80cb2511e4112','SN201710170001','2017-10-17 17:08:51','2017-10-18 07:08:40',2,'2017-10-26 07:08:42','315001','1','测试',2,1),('22437cc4ce914d94852a162e68aa0550','SN201710250001','2017-10-25 09:23:56','2017-10-25 09:23:15',3,'2017-10-27 09:23:17','342501','1','测试邮件',1,1),('260559884a7b4b33b0ea56275f91bc0b','SN201710190001','2017-10-19 15:54:05','2017-10-19 15:53:16',3,'2017-10-26 15:53:19','340001','1','测试流水号',1,1),('34c06be1a20a45b9aa38d84ec1440ccc',NULL,'2017-09-22 09:38:56','2017-09-21 19:38:37',4,'2017-09-27 19:38:39','310001','1','请假测试四天',3,1),('3f89647cac9e4f10b110c427f4d7f968',NULL,'2017-10-17 10:27:14','2017-10-19 00:26:47',8,'2017-10-27 00:26:52','312512','1','测试时间',2,1),('42010a83c1034c9e9fb6c6f1da0905a6','SN201710170001','2017-10-19 15:39:33','2017-10-19 15:39:05',2,'2017-10-26 15:39:08','330001','1','测试邮件发送',1,1),('4995f5db876542c99b1d259bbba185ce',NULL,'2017-10-17 10:24:28','2017-10-18 00:24:07',3,'2017-10-25 00:24:10','312501','1','测试时间',2,1),('524d07b4588240728518a468bd400958','SN201710170001','2017-10-19 14:56:29','2017-10-19 14:56:13',4,'2017-10-26 14:56:15','327501','1','测试发邮件',1,1),('68fb5e6b937b41dba82c43a492cce263','SN201710170001','2017-10-19 13:31:56','2017-10-19 13:31:38',3,'2017-10-27 13:31:40','322604','1','测试发邮件',1,1),('6aa7b47053af43c8946865405718ec0c','SN201710170001','2017-10-19 13:27:21','2017-10-20 03:27:09',3,'2017-10-27 03:27:10','322547','1','测试邮件',2,1),('7ccd1e5f08714c409f3e4a10f83b3e1d','SN201710170003','2017-10-17 17:09:55','2017-10-18 07:09:37',2,'2017-10-26 07:09:39','315023','1','测试',2,1),('a39da5199b22407cb1ce41d019bcee7b',NULL,'2017-09-22 09:44:21','2017-09-21 19:44:10',2,'2017-09-28 19:44:12','310034','1','请假啦',2,1),('ad28f04b54a74e0187eee51b7a3deab9','SN201710190003','2017-10-19 15:54:54','2017-10-19 15:54:36',2,'2017-10-26 15:54:38','340023','1','测试流水号',1,1),('b47c9ba3298840d784345c6134997de2',NULL,'2017-09-20 17:37:01','2017-09-20 03:36:49',3,'2017-09-27 03:36:51','300001','1','请假几天，重新申请',2,1),('b7c48f95373749189fd9e44ca1cc0846',NULL,'2017-09-22 09:15:56','2017-09-21 19:15:37',4,'2017-09-27 19:15:39','307501','1','测试请假',3,1),('c771165cd9f24ae29c06057c92181fd4',NULL,'2017-10-17 10:30:00','2017-10-26 00:29:37',1,'2017-10-27 00:29:41','312523','1','测试时间',2,1),('d49f3f7403fe4dd4a589de9a60c991b1','SN201710170001','2017-10-19 12:08:19','2017-10-20 16:08:03',5,'2017-10-27 16:08:05','320005','1','测试邮件发送',2,1),('eb90f0447ca64d7d993f0c97e2ad1a42','SN201710190002','2017-10-19 15:54:27','2017-10-19 15:54:14',2,'2017-10-26 15:54:16','340012','1','测试流水号',1,1);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
