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
INSERT INTO `sys_resource` VALUES ('1', '', '001', '系统管理', '0', 'fi-folder', 'ajax', '', '0', null);
INSERT INTO `sys_resource` VALUES ('1bc000c4567c4ec6a9d304a0725d1cf6', '/sysDepartment/delete', '016', '删除', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', '2', '2017-08-12 17:25:18');
INSERT INTO `sys_resource` VALUES ('1be8588cf60a4c0a95211b5dbfab5bc7', '', '021', '流程管理', '0', 'fi-thumbnails', 'ajax', '', '2', '2017-08-17 10:37:21');
INSERT INTO `sys_resource` VALUES ('2', '/sysResource/manager', '002', '资源管理', '0', 'fi-database', 'ajax', '1', '1', null);
INSERT INTO `sys_resource` VALUES ('2275d7959c0b40d5897981f059c75200', '/sysUser/edit', '009', '编辑', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', '3', '2017-08-12 11:19:23');
INSERT INTO `sys_resource` VALUES ('287c36805ee34a5e965919198c547509', '/activiti/model/create', 'NO201711070002', '添加', '1', '', null, 'f84a8562694e4b8ebd67f5ec39f2e4b3', '0', '2017-11-07 11:18:33');
-- INSERT INTO `sys_resource` VALUES ('294f8355c701400996c0e86db6815e85', '/activiti/claimTask', '029', '签收', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '1', '2017-08-20 22:25:30');
INSERT INTO `sys_resource` VALUES ('2edce6a6c8444a9fba4384dee34fee7f', '/activiti/jumpTask', 'NO201711210001', '跳转', '1', '', null, 'deee316b106e43668da06e3e11756b1a', '0', '2017-11-21 09:43:42');
INSERT INTO `sys_resource` VALUES ('3', '/sysResource/add', '003', '添加', '1', '', 'ajax', '2', '1', null);
INSERT INTO `sys_resource` VALUES ('30159bb15c28446788d46e7f808f730d', '/activiti/delegateTask', 'NO201711060003', '委派', '1', '', null, 'deee316b106e43668da06e3e11756b1a', '0', '2017-11-06 09:35:37');
INSERT INTO `sys_resource` VALUES ('3323b161301747fe8892f6338924a53f', '/sysOperLog/manager', '036', '操作日志', '0', 'fi-database', null, '8a1a2f49ad8b431e8155ca5fbcc65ed5', '1', '2017-08-28 00:03:36');
INSERT INTO `sys_resource` VALUES ('397c47b362f84ddcb5c7679201366333', '/sysUser/delete', '008', '删除', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', '2', '2017-08-12 11:17:01');
INSERT INTO `sys_resource` VALUES ('3a0e79e02205402a956a78ea95f9c752', '/activiti/transferTask', '032', '转办', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '4', '2017-08-20 22:27:43');
INSERT INTO `sys_resource` VALUES ('3d6f03d570c5411284c57d60216ad76e', '/activiti/active', '037', '激活', '1', '', null, '98f0c0b5e51248b18f2f1ebd07cbd25c', '3', '2017-09-08 16:33:29');
INSERT INTO `sys_resource` VALUES ('4', '/sysRsource/delete', '004', '删除', '1', '', 'ajax', '2', '2', null);
INSERT INTO `sys_resource` VALUES ('4896a4e8a90d478390bd0b4bdbb8fe0d', '/sysRole/add', '011', '添加', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '1', '2017-08-12 12:59:38');
INSERT INTO `sys_resource` VALUES ('4c32091fad9548f6b737b574090c4c5d', '/tUserTask/manager', '022', '流程配置', '0', 'fi-widget', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', '2', '2017-08-18 15:54:18');
INSERT INTO `sys_resource` VALUES ('5', '/sysRsource/edit', '005', '编辑', '1', '', 'ajax', '2', '3', null);
INSERT INTO `sys_resource` VALUES ('5015bf9b1a31430e85126e90d2e4ee5b', '/app/add', 'NO201711060002', '添加', '1', '', null, '8bd73fb8c092459dbf9285b69799c9ef', '0', '2017-11-06 15:41:29');
INSERT INTO `sys_resource` VALUES ('57eb059e400c4d55ad0e6a472ba2d79c', '/app/edit', 'NO201711060003', '编辑', '1', '', null, '8bd73fb8c092459dbf9285b69799c9ef', '1', '2017-11-06 15:42:39');
INSERT INTO `sys_resource` VALUES ('5be30dbf587a4d37a8a4628c2a875821', '', 'NO201711060001', '全部任务', '0', 'fi-list', null, '1be8588cf60a4c0a95211b5dbfab5bc7', '3', '2017-11-06 09:21:35');
INSERT INTO `sys_resource` VALUES ('670bb8d8a8bf4e4f9046b8da2bc7d124', '/sysDepartment/manager', '014', '部门管理', '0', 'fi-thumbnails', 'ajax', '1', '4', '2017-08-12 13:05:13');
INSERT INTO `sys_resource` VALUES ('7595d0149dc049d68c2b89ab999ad882', '/activiti/taskManager', '028', '我的任务', '0', 'fi-list-thumbnails', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', '4', '2017-08-20 17:22:53');
INSERT INTO `sys_resource` VALUES ('7929d9b9a356491693c90f2068daeb70', '/sysRole/delete', '012', '删除', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '2', '2017-08-12 13:01:42');
INSERT INTO `sys_resource` VALUES ('85bbb83c680f4684b102c8db1127217c', '/activiti/model/edit', 'NO201711070003', '编辑', '1', '', null, 'f84a8562694e4b8ebd67f5ec39f2e4b3', '1', '2017-11-07 11:19:16');
INSERT INTO `sys_resource` VALUES ('8a1a2f49ad8b431e8155ca5fbcc65ed5', '', '018', '系统维护', '0', 'fi-torso-business', 'ajax', '', '1', '2017-08-12 18:35:12');
INSERT INTO `sys_resource` VALUES ('8bd73fb8c092459dbf9285b69799c9ef', '/app/manage', 'NO201711060001', '应用管理', '0', 'fi-social-windows', null, '1', '5', '2017-11-06 09:38:00');
INSERT INTO `sys_resource` VALUES ('8cd65d6021e5425ab55a7e22e66b27a1', '/sysRole/manager', '010', '角色管理', '0', 'fi-torso-business', 'ajax', '1', '3', '2017-08-12 12:58:38');
INSERT INTO `sys_resource` VALUES ('964535f00cc54421965a559a4b2691ca', '/activiti/model/deploy', 'NO201711070004', '部署', '1', '', null, 'f84a8562694e4b8ebd67f5ec39f2e4b3', '2', '2017-11-07 11:19:48');
INSERT INTO `sys_resource` VALUES ('98f0c0b5e51248b18f2f1ebd07cbd25c', '/activiti/processdefManager', '027', '流程定义', '0', 'fi-shuffle', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', '1', '2017-08-19 14:26:28');
INSERT INTO `sys_resource` VALUES ('a5b2352b339f4eed8a70e5dbd5af4326', '/activiti/allHisTaskManager', 'NO201711160001', '历史任务', '0', 'fi-clipboard-notes on', null, '5be30dbf587a4d37a8a4628c2a875821', '1', '2017-11-16 17:52:43');
INSERT INTO `sys_resource` VALUES ('a5c29bad8b804849892787546f70d010', '/activiti/hisTaskManager', '034', '我的已办', '0', 'fi-checkbox', null, '1be8588cf60a4c0a95211b5dbfab5bc7', '5', '2017-08-31 21:01:47');
INSERT INTO `sys_resource` VALUES ('a6c1d1c19147411fbddc534f85f82b91', '/icons', '019', '系统图标', '0', 'fi-photo', 'ajax', '8a1a2f49ad8b431e8155ca5fbcc65ed5', '2', '2017-08-12 18:42:12');
INSERT INTO `sys_resource` VALUES ('a8bebd9c95d0484d92cbe368cb5081cf', '/activiti/deploy', '028', '流程部署', '1', '', 'ajax', '98f0c0b5e51248b18f2f1ebd07cbd25c', '1', '2017-08-19 14:37:21');
INSERT INTO `sys_resource` VALUES ('ab854a1516204b6fa62102c1b94e6ebf', '/app/delete', 'NO201711060004', '删除', '1', '', null, '8bd73fb8c092459dbf9285b69799c9ef', '2', '2017-11-06 15:42:55');
INSERT INTO `sys_resource` VALUES ('af6a5e6f09124fa4aa010c3010165d0b', '/sysRole/grant', '020', '授权', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '4', '2017-08-12 22:19:36');
INSERT INTO `sys_resource` VALUES ('b13e0b774e6445c28b13536dc716bac5', '/sysRole/edit', '013', '编辑', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', '3', '2017-08-12 13:02:39');
INSERT INTO `sys_resource` VALUES ('b7a1a2cf219d48178a450c71a45f3c45', '/sysUser/add', '007', '添加', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', '1', '2017-08-12 11:12:41');
INSERT INTO `sys_resource` VALUES ('c2ac8926811a41908f74bf8dcef385ab', '/activiti/delegateTask', '031', '委派', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '3', '2017-08-20 22:27:03');
INSERT INTO `sys_resource` VALUES ('c4b8b3c724344c70b6f9ac263adcf766', '/activiti/model/detail', 'NO201711070005', '详情', '1', '', null, 'f84a8562694e4b8ebd67f5ec39f2e4b3', '3', '2017-11-07 11:20:12');
INSERT INTO `sys_resource` VALUES ('cea8161aba674721b3812078e1d611da', '/activiti/jumpTask', '033', '跳转', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '5', '2017-08-20 22:28:25');
INSERT INTO `sys_resource` VALUES ('da9ce84c01a840ca8a71086dc64f2436', '/activiti/complateTask', '030', '办理', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', '2', '2017-08-20 22:26:19');
INSERT INTO `sys_resource` VALUES ('db0660daedcd48b0bfd7bfd724137e31', '/activiti/sleep', '036', '挂起', '1', '', null, '98f0c0b5e51248b18f2f1ebd07cbd25c', '2', '2017-09-08 16:32:28');
INSERT INTO `sys_resource` VALUES ('deee316b106e43668da06e3e11756b1a', '/activiti/allTaskManager', 'NO201711170001', '待审批任务', '0', 'fi-clipboard-pencil on', null, '5be30dbf587a4d37a8a4628c2a875821', '0', '2017-11-17 09:45:29');
INSERT INTO `sys_resource` VALUES ('eb6fc407610842219f7e94dcf1fa82ea', '/sysDepartment/edit', '017', '编辑', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', '3', '2017-08-12 17:27:42');
INSERT INTO `sys_resource` VALUES ('eeea90fb6bf346888592bd5ccbe5e475', '/sysUser/manager', '006', '用户管理', '0', 'fi-torsos-all', 'iframe', '1', '2', '2017-08-12 10:52:23');
INSERT INTO `sys_resource` VALUES ('f4de27ddadf340539ced1f9c0c0e307b', '/tUserTask/configUser', '034', '设定人员', '1', '', 'ajax', '4c32091fad9548f6b737b574090c4c5d', '1', '2017-08-23 12:16:28');
INSERT INTO `sys_resource` VALUES ('f84a8562694e4b8ebd67f5ec39f2e4b3', '/activiti/model/modelManager', 'NO201711030001', '模型管理', '0', 'fi-paint-bucket', null, '1be8588cf60a4c0a95211b5dbfab5bc7', '0', '2017-11-03 17:42:16');
INSERT INTO `sys_resource` VALUES ('fa5459a821b34a30b07a676faaa806ae', '/sysDepartment/add', '015', '添加', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', '1', '2017-08-12 13:55:46');
INSERT INTO `sys_resource` VALUES ('fdf48b6775184da1999e49ceebfa7494', '/app/modelManage', 'NO201711070001', '模型管理', '1', '', null, '8bd73fb8c092459dbf9285b69799c9ef', '4', '2017-11-07 10:30:48');
INSERT INTO `sys_resource` VALUES ('fe10cc8d40914ca387f2c00afd75c452', '/activiti/transferTask', 'NO201711060002', '转办', '1', '', null, 'deee316b106e43668da06e3e11756b1a', '0', '2017-11-06 09:35:11');


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

INSERT INTO `sys_role_resource` VALUES ('030016220c564d13b987db2a48242020', '8a1a2f49ad8b431e8155ca5fbcc65ed5', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('03e44b21f12747fba699ff23a10a697e', '7929d9b9a356491693c90f2068daeb70', '1');
INSERT INTO `sys_role_resource` VALUES ('04c4fb8af8a844a7afa5abbd24758a18', '670bb8d8a8bf4e4f9046b8da2bc7d124', '1');
INSERT INTO `sys_role_resource` VALUES ('04f03362cfa54c2ca4ccc8985e3a5b0d', 'c2ac8926811a41908f74bf8dcef385ab', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('06208abbd6e24a2f9ecff6ff36ad35e4', '3', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('07853c5e9c014d828e7d269c97a46bd7', 'ab854a1516204b6fa62102c1b94e6ebf', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('08c0253b0d4b47aaae932f1f1e642def', 'eeea90fb6bf346888592bd5ccbe5e475', '1');
INSERT INTO `sys_role_resource` VALUES ('08f442d6d3514e3cb14970e26b4486e0', '1bc000c4567c4ec6a9d304a0725d1cf6', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('0e4d4325de7e40b59a5bd6e347b5a3cd', '57eb059e400c4d55ad0e6a472ba2d79c', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('104d144f40d5404d901941c14211f9e0', 'c2ac8926811a41908f74bf8dcef385ab', '1');
INSERT INTO `sys_role_resource` VALUES ('111a028630e640688ebca8fffb3012e0', '57eb059e400c4d55ad0e6a472ba2d79c', '1');
INSERT INTO `sys_role_resource` VALUES ('12aeae3ae585479fb1d3ff4952c2bb30', '294f8355c701400996c0e86db6815e85', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('12f1ac919b59402884ca4bc9f0108535', '287c36805ee34a5e965919198c547509', '1');
INSERT INTO `sys_role_resource` VALUES ('15ebd81e43814e7fa2b2c5f631c24adf', 'a6c1d1c19147411fbddc534f85f82b91', '1');
INSERT INTO `sys_role_resource` VALUES ('19c90623aba648299999deffd80d5ae5', 'db0660daedcd48b0bfd7bfd724137e31', '1');
INSERT INTO `sys_role_resource` VALUES ('1ac6b6f413fd40a38ddf2c640127a42b', '5', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('1caae8be3ad540bfb434338523af1003', 'db0660daedcd48b0bfd7bfd724137e31', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('20dcc8b98bdd4216806a22c494af15e7', '2275d7959c0b40d5897981f059c75200', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('253a78fca0af4d52b9c29af06b49cc85', 'f84a8562694e4b8ebd67f5ec39f2e4b3', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('287ab08fcae74cae9facbfe081154621', 'b13e0b774e6445c28b13536dc716bac5', '1');
INSERT INTO `sys_role_resource` VALUES ('2e0a8270ca08410ba9d2b84d7029ddb2', '294f8355c701400996c0e86db6815e85', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('2e88f6428da14ce4a3aa44fd63364f30', 'c4b8b3c724344c70b6f9ac263adcf766', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('2f018184c86542368e4b00fbbbbd5508', '4896a4e8a90d478390bd0b4bdbb8fe0d', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('3548cd408d874d1eaa3afeba6e7d8172', '7595d0149dc049d68c2b89ab999ad882', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('3b5ace03b045427b93cf21eb79d962da', '85bbb83c680f4684b102c8db1127217c', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('3cf8c2d142ae4a5eb4217cf82c1803f4', '4c32091fad9548f6b737b574090c4c5d', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('3d18a15692394d1dba6a7e0bac44488b', '5be30dbf587a4d37a8a4628c2a875821', '1');
INSERT INTO `sys_role_resource` VALUES ('3f41495619d14b5492b6d502ce74c0a6', '1be8588cf60a4c0a95211b5dbfab5bc7', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('3f7c13cc617549919c17aaf3c6e7249d', 'a8bebd9c95d0484d92cbe368cb5081cf', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('401db62fe51848bb9f75f0363b3a409d', '8bd73fb8c092459dbf9285b69799c9ef', '1');
INSERT INTO `sys_role_resource` VALUES ('402050908be4414daec92031037f07db', '5015bf9b1a31430e85126e90d2e4ee5b', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('40d69e8b93874914b6e6f337d28656a1', '5015bf9b1a31430e85126e90d2e4ee5b', '1');
INSERT INTO `sys_role_resource` VALUES ('45f0ada300cd4a47981c44657ddafe1b', '294f8355c701400996c0e86db6815e85', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('47aec5df8f8e44ac8b4cd530d43d8883', '2', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('49b741a9e8bf4e6ebec92b781841d3f5', '7929d9b9a356491693c90f2068daeb70', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('4b960a5b33724cf6ad972db977864909', '5', '1');
INSERT INTO `sys_role_resource` VALUES ('5327580ae48d4f28b573c1fbb06ef99a', 'fa5459a821b34a30b07a676faaa806ae', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('58d3395f3f134cc7ae56d52f5ca7eb6d', 'a5b2352b339f4eed8a70e5dbd5af4326', '1');
INSERT INTO `sys_role_resource` VALUES ('5d9661c314ca41438f8c3b56ae46d7c7', 'c4b8b3c724344c70b6f9ac263adcf766', '1');
INSERT INTO `sys_role_resource` VALUES ('5dce939802a24009922e0b20cb1f1f92', '3a0e79e02205402a956a78ea95f9c752', '1');
INSERT INTO `sys_role_resource` VALUES ('60bb331a2bf54b00a462a7e948950270', 'af6a5e6f09124fa4aa010c3010165d0b', '1');
INSERT INTO `sys_role_resource` VALUES ('6661046104714495903332f0410214fa', 'da9ce84c01a840ca8a71086dc64f2436', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('66d5173916b44acfb98598479bdc43c5', '4896a4e8a90d478390bd0b4bdbb8fe0d', '1');
INSERT INTO `sys_role_resource` VALUES ('6a6e49b0824a469bbb5185946cdd58e5', 'a5c29bad8b804849892787546f70d010', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('6b178a7a40ce4916ac7b4b1f3e7b808a', '3a0e79e02205402a956a78ea95f9c752', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('6b9bd6e008704b45b4307ee9c0a40129', '8a1a2f49ad8b431e8155ca5fbcc65ed5', '1');
INSERT INTO `sys_role_resource` VALUES ('70ee4f070e1543c3908b6f5eb99791b1', '7595d0149dc049d68c2b89ab999ad882', '1');
INSERT INTO `sys_role_resource` VALUES ('71e637135b354f67b9c62e6408270347', 'da9ce84c01a840ca8a71086dc64f2436', '492cdebdbbe74ad98d047e627b696c66');
INSERT INTO `sys_role_resource` VALUES ('739c222c1a9247dea6f7803ae97d98f7', 'cea8161aba674721b3812078e1d611da', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('756519dacf274bdcae2459046900f5a5', '397c47b362f84ddcb5c7679201366333', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('7649ee93281f4e90af71a2cfc0ed28fa', '7595d0149dc049d68c2b89ab999ad882', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('767d5ef26f2d429da3707a7cf02626b1', 'a5c29bad8b804849892787546f70d010', '1');
INSERT INTO `sys_role_resource` VALUES ('76ecd68e63fe4353acce1332c560a750', 'eeea90fb6bf346888592bd5ccbe5e475', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('8203380378c041c4b23b2561f5679e5b', 'eb6fc407610842219f7e94dcf1fa82ea', '1');
INSERT INTO `sys_role_resource` VALUES ('8368cc3344f74e9086b53e781963ef21', 'fe10cc8d40914ca387f2c00afd75c452', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('8add10dc45c34df9bbd62af155b68c50', '294f8355c701400996c0e86db6815e85', '492cdebdbbe74ad98d047e627b696c66');
INSERT INTO `sys_role_resource` VALUES ('8b67ee4ab6844c998c81727970998a53', '1be8588cf60a4c0a95211b5dbfab5bc7', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('8e4b5ba6196243d2ac9e0ff88eb58057', '7595d0149dc049d68c2b89ab999ad882', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('8ed22fa90ff141b7a149857ca6a75a92', 'a5c29bad8b804849892787546f70d010', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('90b1449662fa4d919338ea02d17eb81e', '397c47b362f84ddcb5c7679201366333', '1');
INSERT INTO `sys_role_resource` VALUES ('90ed2958331941149ae679da7de76b24', '1bc000c4567c4ec6a9d304a0725d1cf6', '1');
INSERT INTO `sys_role_resource` VALUES ('92336b11392e443881b3ccf53330f767', '2', '1');
INSERT INTO `sys_role_resource` VALUES ('93a83011ac4d4433991bc0574ab0c854', 'af6a5e6f09124fa4aa010c3010165d0b', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('96640b8ed274436a9fd1e61529850de5', '3323b161301747fe8892f6338924a53f', '1');
INSERT INTO `sys_role_resource` VALUES ('9722d99fb3804371b6ecd73bbdff5545', '98f0c0b5e51248b18f2f1ebd07cbd25c', '1');
INSERT INTO `sys_role_resource` VALUES ('9b288723a6b143cf9b45354913040a67', 'da9ce84c01a840ca8a71086dc64f2436', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('9d085c03e8d247368a00a23b818acba7', 'fdf48b6775184da1999e49ceebfa7494', '1');
INSERT INTO `sys_role_resource` VALUES ('9dff0d19e30c45cfa3c5c8f24c904d51', '287c36805ee34a5e965919198c547509', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('a0d5ee3d27e343cc931fe82bcd35133b', 'fa5459a821b34a30b07a676faaa806ae', '1');
INSERT INTO `sys_role_resource` VALUES ('a2f2cd624ba5446dbe58a67e42bd0102', '4', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('a528e5c644dd4bb494abd560da4e434b', '98f0c0b5e51248b18f2f1ebd07cbd25c', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('a97fd1dd3cc24fb0984d2671a7102e12', 'f4de27ddadf340539ced1f9c0c0e307b', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('aa7d3b59d893410b96d28789817edc86', 'a8bebd9c95d0484d92cbe368cb5081cf', '1');
INSERT INTO `sys_role_resource` VALUES ('ab3ea61dd467496397e463ee4f339bdd', '30159bb15c28446788d46e7f808f730d', '1');
INSERT INTO `sys_role_resource` VALUES ('ab63f790923a4cedb41ffb531164bb2f', 'da9ce84c01a840ca8a71086dc64f2436', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('ade2cd6364ed4ac1ba391e69afcf7585', '8cd65d6021e5425ab55a7e22e66b27a1', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('b08163873f9144dc990d0751460522cf', 'b13e0b774e6445c28b13536dc716bac5', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('b3ac7cdf17fb42818760cbb1d65be788', 'fdf48b6775184da1999e49ceebfa7494', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('b435ba5804ea4ac5a42ffbb77de053ec', '1', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('b766ac3acb294cf4b5a92188e0e94fd6', 'f4de27ddadf340539ced1f9c0c0e307b', '1');
INSERT INTO `sys_role_resource` VALUES ('b89fad6086d846a6b740178f3da53e36', '4c32091fad9548f6b737b574090c4c5d', '1');
INSERT INTO `sys_role_resource` VALUES ('b9e976fe631a4df9a869784a72bbd17b', '7595d0149dc049d68c2b89ab999ad882', '492cdebdbbe74ad98d047e627b696c66');
INSERT INTO `sys_role_resource` VALUES ('ba9d287a05d545099c0da66f81dcdf0d', 'a5c29bad8b804849892787546f70d010', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('bb69c9ad4dfa485980f3258e40e4ab4e', 'cea8161aba674721b3812078e1d611da', '1');
INSERT INTO `sys_role_resource` VALUES ('bf8fa0fcce9b401fa112a0bed33bc105', '3d6f03d570c5411284c57d60216ad76e', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('bff4262107ee4151b49ad3d58524a817', '3d6f03d570c5411284c57d60216ad76e', '1');
INSERT INTO `sys_role_resource` VALUES ('c5d6dc5fd8774beaa760508659abc3e7', '1be8588cf60a4c0a95211b5dbfab5bc7', '1');
INSERT INTO `sys_role_resource` VALUES ('c75bf4aa39bf47c8b703631fb829d7d0', '964535f00cc54421965a559a4b2691ca', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('c793848fe9cb49fdaf2c285a0f429d65', '30159bb15c28446788d46e7f808f730d', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('cb4aa83d4a10482a8451871b6a585c0b', 'b7a1a2cf219d48178a450c71a45f3c45', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('cc09a183d7814754a6396e07b5df8d67', '8cd65d6021e5425ab55a7e22e66b27a1', '1');
INSERT INTO `sys_role_resource` VALUES ('cc7231ca961b4c88ac6f30dd45cbe8ec', 'a5b2352b339f4eed8a70e5dbd5af4326', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('ced3f3caf0944edcb0bbb082de8de850', '670bb8d8a8bf4e4f9046b8da2bc7d124', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('cee55f7de10941b8982660295443c64c', '1', '1');
INSERT INTO `sys_role_resource` VALUES ('cf0145360019499b8aab887d292b4c67', 'a5c29bad8b804849892787546f70d010', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('d0a130789f6448ba9245ec692c67bb47', 'b7a1a2cf219d48178a450c71a45f3c45', '1');
INSERT INTO `sys_role_resource` VALUES ('d59508d7fa4c4fc692af299d33e026b7', '3323b161301747fe8892f6338924a53f', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('d5e2f1b00e214cac99dc381b4efa9cc1', '5be30dbf587a4d37a8a4628c2a875821', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('daa1e35ea7ba424e8b7156b83cb211f5', '294f8355c701400996c0e86db6815e85', '1');
INSERT INTO `sys_role_resource` VALUES ('dc1afcf0bcc64a86902d2bb1acef6f7e', '8bd73fb8c092459dbf9285b69799c9ef', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('dd73cc0926e144bd90a057becab7a52b', '2275d7959c0b40d5897981f059c75200', '1');
INSERT INTO `sys_role_resource` VALUES ('ddb6bbfd15694a388ef5e7b990982078', 'da9ce84c01a840ca8a71086dc64f2436', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('e2c0dd8b6f1244e3b53ffdd0a573663b', '964535f00cc54421965a559a4b2691ca', '1');
INSERT INTO `sys_role_resource` VALUES ('e4c8d84694274b408c33d083d1093edd', 'eb6fc407610842219f7e94dcf1fa82ea', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('e5d52ab34b1d4953a7b9d1d469c3c74f', '85bbb83c680f4684b102c8db1127217c', '1');
INSERT INTO `sys_role_resource` VALUES ('ea28a69b893f45fba894443ae5009885', '294f8355c701400996c0e86db6815e85', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('eaec0535990242a38e37df2c7472e740', 'deee316b106e43668da06e3e11756b1a', '1');
INSERT INTO `sys_role_resource` VALUES ('ed5f4a8976004ac79bd4564793bd031f', 'ab854a1516204b6fa62102c1b94e6ebf', '1');
INSERT INTO `sys_role_resource` VALUES ('f140bb7feec042a6a28f601d0eb1b65c', 'a6c1d1c19147411fbddc534f85f82b91', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('f67545d02d9c46d5a2b7d70426a5a8ec', '3', '1');
INSERT INTO `sys_role_resource` VALUES ('f738264c1e88477693f01625cce71804', 'da9ce84c01a840ca8a71086dc64f2436', '1');
INSERT INTO `sys_role_resource` VALUES ('f8ae717991df44aab54835b26d08eec8', '7595d0149dc049d68c2b89ab999ad882', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('f9f656c457cf420fab678a9043eeb9bb', 'deee316b106e43668da06e3e11756b1a', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('fb9fca26de104cf788a8a4e6c71f8d65', 'f84a8562694e4b8ebd67f5ec39f2e4b3', '1');
INSERT INTO `sys_role_resource` VALUES ('fec25aea111c4003b06baffa9c449c41', '4', '1');
INSERT INTO `sys_role_resource` VALUES ('ff2eff9b40f3415ab98c7108a4cc6a39', 'fe10cc8d40914ca387f2c00afd75c452', '1');

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

insert  into `sys_user`(`id`,`login_name`,`login_pwd`,`user_name`,`user_phone`,`user_email`,`user_type`,`user_sex`,`register_time`,`department_id`) values ('1','admin','E10ADC3949BA59ABBE56E057F20F883E','管理员','123456','liujunyang@moutum.com','0','0','2014-02-19 01:00:00','1');

/*Table structure for table `sys_user_role` */

DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户外键',
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户角色关联表';

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`id`,`user_id`,`role_id`) values ('b0aa7180c2f1400dba95ab43b5d4d59e','1','1');

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
  `order_num` tinyint(4) NOT NULL COMMENT '任务节点顺序号',
  `version_` tinyint(4) NOT NULL COMMENT '流程定义版本',
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

DROP TABLE IF EXISTS `t_app`;
CREATE TABLE `t_app` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '应用名称',
  `key` varchar(64) NOT NULL COMMENT '应用KEY',
  `creator` varchar(64) NOT NULL COMMENT '应用创建者ID',
  `updater` varchar(64) NOT NULL COMMENT '应用更新者ID',
  `status` tinyint(2) DEFAULT '1' COMMENT '状态：1-启用；0-禁用',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='应用表';

DROP TABLE IF EXISTS `t_app_model`;
CREATE TABLE `t_app_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_key` varchar(64) DEFAULT NULL COMMENT '关联t_app key',
  `model_key` varchar(64) DEFAULT NULL COMMENT '关联act_re_model key_',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='应用模型关联表';

/*Data for the table `t_vacation` */

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
