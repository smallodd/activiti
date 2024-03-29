/*
SQLyog Ultimate v11.25 (64 bit)
MySQL - 5.7.18-log : Database - workflow
*********************************************************************
*/

CREATE DATABASE /*!32312 IF NOT EXISTS*/`workflow` /*!40100 DEFAULT CHARACTER SET utf8 */;



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
INSERT INTO `sys_resource` VALUES ('0c05bb3e89544699b4c6ef1ce34edc5e', '/activiti/showTask', 'NO201711210003', '进度', '1', '', NULL, '7595d0149dc049d68c2b89ab999ad882', 0, '2017-11-21 13:29:18');
INSERT INTO `sys_resource` VALUES ('1', '', '001', '系统管理', '0', 'fi-folder', 'ajax', '', 0, NULL);
INSERT INTO `sys_resource` VALUES ('132343cd4b814af8b28337c02c8e987a', '/activiti/model/copy', 'NO201711270001', '复制', '1', '', NULL, 'f84a8562694e4b8ebd67f5ec39f2e4b3', 0, '2017-11-27 10:22:27');
INSERT INTO `sys_resource` VALUES ('1bc000c4567c4ec6a9d304a0725d1cf6', '/sysDepartment/delete', '016', '删除', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', 2, '2017-8-12 17:25:18');
INSERT INTO `sys_resource` VALUES ('1be8588cf60a4c0a95211b5dbfab5bc7', '', '021', '流程管理', '0', 'fi-thumbnails', 'ajax', '', 2, '2017-8-17 10:37:21');
INSERT INTO `sys_resource` VALUES ('1dbbd11615f848b5b80a5409629a026f', '/activiti/adminShowTask', 'NO201711210004', '进度', '1', '', NULL, 'deee316b106e43668da06e3e11756b1a', 0, '2017-11-21 14:13:01');
INSERT INTO `sys_resource` VALUES ('2', '/sysResource/manager', '002', '资源管理', '0', 'fi-database', 'ajax', '1', 1, NULL);
INSERT INTO `sys_resource` VALUES ('216feb54e7334de087097b900dc23fdf', '/activiti/adminComplateTask', 'NO201711210001', '办理', '1', '', NULL, 'deee316b106e43668da06e3e11756b1a', 0, '2017-11-21 11:53:38');
INSERT INTO `sys_resource` VALUES ('2275d7959c0b40d5897981f059c75200', '/sysUser/edit', '009', '编辑', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', 3, '2017-8-12 11:19:23');
INSERT INTO `sys_resource` VALUES ('287c36805ee34a5e965919198c547509', '/activiti/model/create', 'NO201711070002', '添加', '1', '', NULL, 'f84a8562694e4b8ebd67f5ec39f2e4b3', 0, '2017-11-7 11:18:33');
INSERT INTO `sys_resource` VALUES ('3', '/sysResource/add', '003', '添加', '1', '', 'ajax', '2', 1, NULL);
/*INSERT INTO `sys_resource` VALUES ('30159bb15c28446788d46e7f808f730d', '/activiti/adminDelegateTask', 'NO201711060003', '委派', '1', '', NULL, 'deee316b106e43668da06e3e11756b1a', 0, '2017-11-6 09:35:37');*/
INSERT INTO `sys_resource` VALUES ('3323b161301747fe8892f6338924a53f', '/sysOperLog/manager', '036', '操作日志', '0', 'fi-database', NULL, '8a1a2f49ad8b431e8155ca5fbcc65ed5', 1, '2017-8-28 00:03:36');
INSERT INTO `sys_resource` VALUES ('397c47b362f84ddcb5c7679201366333', '/sysUser/delete', '008', '删除', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', 2, '2017-8-12 11:17:01');
INSERT INTO `sys_resource` VALUES ('3a0e79e02205402a956a78ea95f9c752', '/activiti/transferTask', '032', '转办', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', 4, '2017-8-20 22:27:43');
INSERT INTO `sys_resource` VALUES ('3d6f03d570c5411284c57d60216ad76e', '/activiti/active', '037', '激活', '1', '', NULL, '98f0c0b5e51248b18f2f1ebd07cbd25c', 3, '2017-9-8 16:33:29');
INSERT INTO `sys_resource` VALUES ('4', '/sysRsource/delete', '004', '删除', '1', '', 'ajax', '2', 2, NULL);
INSERT INTO `sys_resource` VALUES ('4896a4e8a90d478390bd0b4bdbb8fe0d', '/sysRole/add', '011', '添加', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', 1, '2017-8-12 12:59:38');
INSERT INTO `sys_resource` VALUES ('4c32091fad9548f6b737b574090c4c5d', '/tUserTask/manager', '022', '流程配置', '0', 'fi-widget', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', 2, '2017-8-18 15:54:18');
INSERT INTO `sys_resource` VALUES ('5', '/sysRsource/edit', '005', '编辑', '1', '', 'ajax', '2', 3, NULL);
INSERT INTO `sys_resource` VALUES ('5015bf9b1a31430e85126e90d2e4ee5b', '/app/add', 'NO201711060002', '添加', '1', '', NULL, '8bd73fb8c092459dbf9285b69799c9ef', 0, '2017-11-6 15:41:29');
INSERT INTO `sys_resource` VALUES ('57eb059e400c4d55ad0e6a472ba2d79c', '/app/edit', 'NO201711060003', '编辑', '1', '', NULL, '8bd73fb8c092459dbf9285b69799c9ef', 1, '2017-11-6 15:42:39');
INSERT INTO `sys_resource` VALUES ('5b1dfcfdb34441888b549d103b02f1f0', '/activiti/adminJumpTask', 'NO201711210002', '跳转', '1', '', NULL, 'deee316b106e43668da06e3e11756b1a', 0, '2017-11-21 13:15:59');
INSERT INTO `sys_resource` VALUES ('5be30dbf587a4d37a8a4628c2a875821', '', 'NO201711060001', '全部任务', '0', 'fi-list', NULL, '1be8588cf60a4c0a95211b5dbfab5bc7', 3, '2017-11-6 09:21:35');
INSERT INTO `sys_resource` VALUES ('670bb8d8a8bf4e4f9046b8da2bc7d124', '/sysDepartment/manager', '014', '部门管理', '0', 'fi-thumbnails', 'ajax', '1', 4, '2017-8-12 13:05:13');
INSERT INTO `sys_resource` VALUES ('7595d0149dc049d68c2b89ab999ad882', '/activiti/taskManager', '028', '我的任务', '0', 'fi-list-thumbnails', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', 4, '2017-8-20 17:22:53');
INSERT INTO `sys_resource` VALUES ('7929d9b9a356491693c90f2068daeb70', '/sysRole/delete', '012', '删除', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', 2, '2017-8-12 13:01:42');
INSERT INTO `sys_resource` VALUES ('85bbb83c680f4684b102c8db1127217c', '/activiti/model/edit', 'NO201711070003', '编辑', '1', '', NULL, 'f84a8562694e4b8ebd67f5ec39f2e4b3', 1, '2017-11-7 11:19:16');
INSERT INTO `sys_resource` VALUES ('8a1a2f49ad8b431e8155ca5fbcc65ed5', '', '018', '系统维护', '0', 'fi-torso-business', 'ajax', '', 1, '2017-8-12 18:35:12');
INSERT INTO `sys_resource` VALUES ('8bd73fb8c092459dbf9285b69799c9ef', '/app/manage', 'NO201711060001', '应用管理', '0', 'fi-social-windows', NULL, '1', 5, '2017-11-6 09:38:00');
INSERT INTO `sys_resource` VALUES ('8cd65d6021e5425ab55a7e22e66b27a1', '/sysRole/manager', '010', '角色管理', '0', 'fi-torso-business', 'ajax', '1', 3, '2017-8-12 12:58:38');
INSERT INTO `sys_resource` VALUES ('964535f00cc54421965a559a4b2691ca', '/activiti/model/deploy', 'NO201711070004', '部署', '1', '', NULL, 'f84a8562694e4b8ebd67f5ec39f2e4b3', 2, '2017-11-7 11:19:48');
INSERT INTO `sys_resource` VALUES ('98f0c0b5e51248b18f2f1ebd07cbd25c', '/activiti/processdefManager', '027', '流程定义', '0', 'fi-shuffle', 'ajax', '1be8588cf60a4c0a95211b5dbfab5bc7', 1, '2017-8-19 14:26:28');
INSERT INTO `sys_resource` VALUES ('a5b2352b339f4eed8a70e5dbd5af4326', '/activiti/allHisTaskManager', 'NO201711160001', '历史任务', '0', 'fi-clipboard-notes', NULL, '5be30dbf587a4d37a8a4628c2a875821', 0, '2017-11-16 17:52:43');
INSERT INTO `sys_resource` VALUES ('a5c29bad8b804849892787546f70d010', '/activiti/hisTaskManager', '034', '我的已办', '0', 'fi-checkbox', NULL, '1be8588cf60a4c0a95211b5dbfab5bc7', 5, '2017-8-31 21:01:47');
INSERT INTO `sys_resource` VALUES ('a6c1d1c19147411fbddc534f85f82b91', '/icons', '019', '系统图标', '0', 'fi-photo', 'ajax', '8a1a2f49ad8b431e8155ca5fbcc65ed5', 2, '2017-8-12 18:42:12');
INSERT INTO `sys_resource` VALUES ('a8bebd9c95d0484d92cbe368cb5081cf', '/activiti/deploy', '028', '流程部署', '1', '', 'ajax', '98f0c0b5e51248b18f2f1ebd07cbd25c', 1, '2017-8-19 14:37:21');
INSERT INTO `sys_resource` VALUES ('ab854a1516204b6fa62102c1b94e6ebf', '/app/delete', 'NO201711060004', '删除', '1', '', NULL, '8bd73fb8c092459dbf9285b69799c9ef', 2, '2017-11-6 15:42:55');
INSERT INTO `sys_resource` VALUES ('af6a5e6f09124fa4aa010c3010165d0b', '/sysRole/grant', '020', '授权', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', 4, '2017-8-12 22:19:36');
INSERT INTO `sys_resource` VALUES ('b13e0b774e6445c28b13536dc716bac5', '/sysRole/edit', '013', '编辑', '1', '', 'ajax', '8cd65d6021e5425ab55a7e22e66b27a1', 3, '2017-8-12 13:02:39');
INSERT INTO `sys_resource` VALUES ('b7a1a2cf219d48178a450c71a45f3c45', '/sysUser/add', '007', '添加', '1', '', 'ajax', 'eeea90fb6bf346888592bd5ccbe5e475', 1, '2017-8-12 11:12:41');
INSERT INTO `sys_resource` VALUES ('c2ac8926811a41908f74bf8dcef385ab', '/activiti/delegateTask', '031', '委派', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', 3, '2017-8-20 22:27:03');
INSERT INTO `sys_resource` VALUES ('c4b8b3c724344c70b6f9ac263adcf766', '/activiti/model/detail', 'NO201711070005', '详情', '1', '', NULL, 'f84a8562694e4b8ebd67f5ec39f2e4b3', 3, '2017-11-7 11:20:12');
INSERT INTO `sys_resource` VALUES ('cea8161aba674721b3812078e1d611da', '/activiti/jumpTask', '033', '跳转', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', 5, '2017-8-20 22:28:25');
INSERT INTO `sys_resource` VALUES ('da9ce84c01a840ca8a71086dc64f2436', '/activiti/complateTask', '030', '办理', '1', '', 'ajax', '7595d0149dc049d68c2b89ab999ad882', 2, '2017-8-20 22:26:19');
INSERT INTO `sys_resource` VALUES ('db0660daedcd48b0bfd7bfd724137e31', '/activiti/sleep', '036', '挂起', '1', '', NULL, '98f0c0b5e51248b18f2f1ebd07cbd25c', 2, '2017-9-8 16:32:28');
INSERT INTO `sys_resource` VALUES ('deee316b106e43668da06e3e11756b1a', '/activiti/allTaskManager', 'NO201711170001', '待审批任务', '0', 'fi-clipboard-pencil', NULL, '5be30dbf587a4d37a8a4628c2a875821', 0, '2017-11-17 09:45:29');
INSERT INTO `sys_resource` VALUES ('e372a4a8ec0941ddbf9a9658ba24a471', '/sysUser/password', 'NO201711210001', '密码管理', '0', 'fi-key', NULL, '', 3, '2017-11-21 10:37:37');
INSERT INTO `sys_resource` VALUES ('eb6fc407610842219f7e94dcf1fa82ea', '/sysDepartment/edit', '017', '编辑', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', 3, '2017-8-12 17:27:42');
INSERT INTO `sys_resource` VALUES ('eeea90fb6bf346888592bd5ccbe5e475', '/sysUser/manager', '006', '用户管理', '0', 'fi-torsos-all', 'iframe', '1', 2, '2017-8-12 10:52:23');
INSERT INTO `sys_resource` VALUES ('f4de27ddadf340539ced1f9c0c0e307b', '/tUserTask/configUser', '034', '设定人员', '1', '', 'ajax', '4c32091fad9548f6b737b574090c4c5d', 1, '2017-8-23 12:16:28');
INSERT INTO `sys_resource` VALUES ('f84a8562694e4b8ebd67f5ec39f2e4b3', '/activiti/model/modelManager', 'NO201711030001', '模型管理', '0', 'fi-paint-bucket', NULL, '1be8588cf60a4c0a95211b5dbfab5bc7', 0, '2017-11-3 17:42:16');
INSERT INTO `sys_resource` VALUES ('fa5459a821b34a30b07a676faaa806ae', '/sysDepartment/add', '015', '添加', '1', '', 'ajax', '670bb8d8a8bf4e4f9046b8da2bc7d124', 1, '2017-8-12 13:55:46');
INSERT INTO `sys_resource` VALUES ('fdf48b6775184da1999e49ceebfa7494', '/app/modelManage', 'NO201711070001', '模型管理', '1', '', NULL, '8bd73fb8c092459dbf9285b69799c9ef', 4, '2017-11-7 10:30:48');
INSERT INTO `sys_resource` VALUES ('fe10cc8d40914ca387f2c00afd75c452', '/activiti/adminTransferTask', 'NO201711060002', '转办', '1', '', NULL, 'deee316b106e43668da06e3e11756b1a', 0, '2017-11-6 09:35:11');
INSERT INTO `sys_resource` VALUES ('fbb959e2f8e04e92bf60305994ec6e48', '/activiti/model/resetKey', 'NO201711270002', '重置key', '1', '', NULL, 'f84a8562694e4b8ebd67f5ec39f2e4b3', 0, '2017-11-27 17:39:15');

/*Table structure for table `sys_role` */



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



CREATE TABLE `sys_role_resource` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `resource_id` varchar(32) NOT NULL COMMENT '菜单外键',
  `role_id` varchar(32) NOT NULL COMMENT '角色外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色菜单表';

/*Data for the table `sys_role_resource` */
INSERT INTO `sys_role_resource` VALUES ('0006e74e2a054ba290105d1a882fd8f0', '2275d7959c0b40d5897981f059c75200', '1');
INSERT INTO `sys_role_resource` VALUES ('05971ef0b3de4b65a59bc71fa0385f2b', '4896a4e8a90d478390bd0b4bdbb8fe0d', '1');
INSERT INTO `sys_role_resource` VALUES ('085f07b3a1ea4b7aa433384c6d5d624e', '216feb54e7334de087097b900dc23fdf', '1');
INSERT INTO `sys_role_resource` VALUES ('08f9b18d7e5941b8ab75de077b584681', '8cd65d6021e5425ab55a7e22e66b27a1', '1');
INSERT INTO `sys_role_resource` VALUES ('0a6c24b7f3eb4c58abad94c90e01c96e', 'c2ac8926811a41908f74bf8dcef385ab', '1');
INSERT INTO `sys_role_resource` VALUES ('0ae4463401fa4f0cb1a6523f34c56d8a', 'b13e0b774e6445c28b13536dc716bac5', '1');
INSERT INTO `sys_role_resource` VALUES ('0f8be852c8c1417284febdae481c630b', '5be30dbf587a4d37a8a4628c2a875821', '1');
INSERT INTO `sys_role_resource` VALUES ('12aeae3ae585479fb1d3ff4952c2bb30', '294f8355c701400996c0e86db6815e85', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('1306f4691f624272a2adb44cf1117d43', 'ab854a1516204b6fa62102c1b94e6ebf', '1');
INSERT INTO `sys_role_resource` VALUES ('192fbb598abd4169b05817494a28d65c', '4', '1');
INSERT INTO `sys_role_resource` VALUES ('242bfbec038d4a58adf3160217f786ec', '670bb8d8a8bf4e4f9046b8da2bc7d124', '1');
INSERT INTO `sys_role_resource` VALUES ('297c2dc5f19146f3bbd5623c101a9cf5', 'fdf48b6775184da1999e49ceebfa7494', '1');
INSERT INTO `sys_role_resource` VALUES ('307d53c68c934222adc834731c8c7ff4', '30159bb15c28446788d46e7f808f730d', '1');
INSERT INTO `sys_role_resource` VALUES ('32d1c3c1d9f2467b9d27a233c7fe1766', 'cea8161aba674721b3812078e1d611da', '1');
INSERT INTO `sys_role_resource` VALUES ('3548cd408d874d1eaa3afeba6e7d8172', '7595d0149dc049d68c2b89ab999ad882', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('38db361be2df4232b11c30e01edb7753', 'f4de27ddadf340539ced1f9c0c0e307b', '1');
INSERT INTO `sys_role_resource` VALUES ('3f41495619d14b5492b6d502ce74c0a6', '1be8588cf60a4c0a95211b5dbfab5bc7', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('41498db077a84752a3dd99743258031a', '98f0c0b5e51248b18f2f1ebd07cbd25c', '1');
INSERT INTO `sys_role_resource` VALUES ('45f0ada300cd4a47981c44657ddafe1b', '294f8355c701400996c0e86db6815e85', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('4a8ee38732444ffcb46e6a052643e3cb', '57eb059e400c4d55ad0e6a472ba2d79c', '1');
INSERT INTO `sys_role_resource` VALUES ('4b30da503cea4a0b86d806aa107e59fe', '1be8588cf60a4c0a95211b5dbfab5bc7', '1');
INSERT INTO `sys_role_resource` VALUES ('4f83cbb5d9234e06aaaee1b42927ab42', '3323b161301747fe8892f6338924a53f', '1');
INSERT INTO `sys_role_resource` VALUES ('5052876088df47188bb5d18cca53ba90', 'af6a5e6f09124fa4aa010c3010165d0b', '1');
INSERT INTO `sys_role_resource` VALUES ('5286d329621b47da8e02c97d7dc4d357', 'a5c29bad8b804849892787546f70d010', '1');
INSERT INTO `sys_role_resource` VALUES ('5a8ebf8e150f4e6985e149398180cbfe', '', 'eb5cb38d0102457f8cfe4764198dbeae');
INSERT INTO `sys_role_resource` VALUES ('5cdb1d49f62e47eeb83a4931ebd1ceb3', 'eb6fc407610842219f7e94dcf1fa82ea', '1');
INSERT INTO `sys_role_resource` VALUES ('5f85aee02aa549ac92939333a01aef26', 'eeea90fb6bf346888592bd5ccbe5e475', '1');
INSERT INTO `sys_role_resource` VALUES ('6661046104714495903332f0410214fa', 'da9ce84c01a840ca8a71086dc64f2436', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('6a08be862bc54b33a23536b6cc716b7e', '5', '1');
INSERT INTO `sys_role_resource` VALUES ('6a6e49b0824a469bbb5185946cdd58e5', 'a5c29bad8b804849892787546f70d010', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('6a78942682434edcb42cc04875c6d0d7', 'c4b8b3c724344c70b6f9ac263adcf766', '1');
INSERT INTO `sys_role_resource` VALUES ('6cec7cc7592f4fb79d7cef5e63988bf0', 'a5b2352b339f4eed8a70e5dbd5af4326', '1');
INSERT INTO `sys_role_resource` VALUES ('6ea5e034ee2f4269bd0d3c689a2df9b3', '5b1dfcfdb34441888b549d103b02f1f0', '1');
INSERT INTO `sys_role_resource` VALUES ('6ebf1da733364d0f9cee645b6744409e', 'e372a4a8ec0941ddbf9a9658ba24a471', '1');
INSERT INTO `sys_role_resource` VALUES ('70051f0f7dda4053ae0d398bdeba89f7', '3', '1');
INSERT INTO `sys_role_resource` VALUES ('7174777b32a342989c0571612959c9a8', '287c36805ee34a5e965919198c547509', '1');
INSERT INTO `sys_role_resource` VALUES ('71e637135b354f67b9c62e6408270347', 'da9ce84c01a840ca8a71086dc64f2436', '492cdebdbbe74ad98d047e627b696c66');
INSERT INTO `sys_role_resource` VALUES ('75a105db04324bb487a7f4f7ad701d4e', 'a8bebd9c95d0484d92cbe368cb5081cf', '1');
INSERT INTO `sys_role_resource` VALUES ('7649ee93281f4e90af71a2cfc0ed28fa', '7595d0149dc049d68c2b89ab999ad882', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('801b7a9175f145239935bf1e788eb797', '8a1a2f49ad8b431e8155ca5fbcc65ed5', '1');
INSERT INTO `sys_role_resource` VALUES ('8619ab9c6dee416fa307b29dc17f4563', 'deee316b106e43668da06e3e11756b1a', '1');
INSERT INTO `sys_role_resource` VALUES ('87e876b52f4c4a248187604bf8991310', 'fe10cc8d40914ca387f2c00afd75c452', '1');
INSERT INTO `sys_role_resource` VALUES ('8add10dc45c34df9bbd62af155b68c50', '294f8355c701400996c0e86db6815e85', '492cdebdbbe74ad98d047e627b696c66');
INSERT INTO `sys_role_resource` VALUES ('8e4b5ba6196243d2ac9e0ff88eb58057', '7595d0149dc049d68c2b89ab999ad882', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('8ed22fa90ff141b7a149857ca6a75a92', 'a5c29bad8b804849892787546f70d010', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('95185e45e6bb4385a45a7901a6eaba60', '2', '1');
INSERT INTO `sys_role_resource` VALUES ('9569a73a1591471eadab0a26cda912a2', '0c05bb3e89544699b4c6ef1ce34edc5e', '1');
INSERT INTO `sys_role_resource` VALUES ('99697637b6b64de0939006d8baedfd78', '1bc000c4567c4ec6a9d304a0725d1cf6', '1');
INSERT INTO `sys_role_resource` VALUES ('9a23c01d9b2a4a74af50ea586d60219f', 'f84a8562694e4b8ebd67f5ec39f2e4b3', '1');
INSERT INTO `sys_role_resource` VALUES ('9d5761e163e54a3eaf97897f6f7760b9', 'db0660daedcd48b0bfd7bfd724137e31', '1');
INSERT INTO `sys_role_resource` VALUES ('9e2abe2ec239458a8f516ac86942c259', '964535f00cc54421965a559a4b2691ca', '1');
INSERT INTO `sys_role_resource` VALUES ('a1db318f556b4db1a17fc1bf03032b8c', '1', '1');
INSERT INTO `sys_role_resource` VALUES ('a40ee59159e64b12a2fe03990aca8e4f', '7929d9b9a356491693c90f2068daeb70', '1');
INSERT INTO `sys_role_resource` VALUES ('a608a0a8658b4643a7fdefd85bc18ff4', 'b7a1a2cf219d48178a450c71a45f3c45', '1');
INSERT INTO `sys_role_resource` VALUES ('ab63f790923a4cedb41ffb531164bb2f', 'da9ce84c01a840ca8a71086dc64f2436', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('ae509c13682e4f539ea8f94d993f7ff0', 'da9ce84c01a840ca8a71086dc64f2436', '1');
INSERT INTO `sys_role_resource` VALUES ('afc255456e7044dda0a79a7322a5ed2d', '8bd73fb8c092459dbf9285b69799c9ef', '1');
INSERT INTO `sys_role_resource` VALUES ('b8927ee07d094b68accef0655350fcff', 'fa5459a821b34a30b07a676faaa806ae', '1');
INSERT INTO `sys_role_resource` VALUES ('b9e976fe631a4df9a869784a72bbd17b', '7595d0149dc049d68c2b89ab999ad882', '492cdebdbbe74ad98d047e627b696c66');
INSERT INTO `sys_role_resource` VALUES ('bc0b8f019f6244b895e913178978184d', '1dbbd11615f848b5b80a5409629a026f', '1');
INSERT INTO `sys_role_resource` VALUES ('c2e283e07b634c62822faca64de52b8d', '132343cd4b814af8b28337c02c8e987a', '1');
INSERT INTO `sys_role_resource` VALUES ('c3178cdfe66a4371b1f330e3e9ce486b', '397c47b362f84ddcb5c7679201366333', '1');
INSERT INTO `sys_role_resource` VALUES ('cf0145360019499b8aab887d292b4c67', 'a5c29bad8b804849892787546f70d010', '543c56799ed14d8d93294f1d2e93626d');
INSERT INTO `sys_role_resource` VALUES ('cfde93bdafd043f69a0bd7ed00123362', '4c32091fad9548f6b737b574090c4c5d', '1');
INSERT INTO `sys_role_resource` VALUES ('d553443e0130417cbd2e875898723570', '3d6f03d570c5411284c57d60216ad76e', '1');
INSERT INTO `sys_role_resource` VALUES ('ddb6bbfd15694a388ef5e7b990982078', 'da9ce84c01a840ca8a71086dc64f2436', 'e1fa3ea4aff84a8ebf3aebcfc5c6d4fa');
INSERT INTO `sys_role_resource` VALUES ('de7935fd53d9407a8115acb3ab1188da', '3a0e79e02205402a956a78ea95f9c752', '1');
INSERT INTO `sys_role_resource` VALUES ('ea28a69b893f45fba894443ae5009885', '294f8355c701400996c0e86db6815e85', '979e0715942e4cc09fc4a78e6c3544e7');
INSERT INTO `sys_role_resource` VALUES ('ee3a2585cba5406ca83682c63eae07f3', '85bbb83c680f4684b102c8db1127217c', '1');
INSERT INTO `sys_role_resource` VALUES ('f368eeeadcf14d12a834d18d7bf2a21f', '7595d0149dc049d68c2b89ab999ad882', '1');
INSERT INTO `sys_role_resource` VALUES ('fbb4c859338149e29584d22ee7d8f1e6', '5015bf9b1a31430e85126e90d2e4ee5b', '1');
INSERT INTO `sys_role_resource` VALUES ('fc2966a0d5b34901815fc62f5bef5c6a', 'a6c1d1c19147411fbddc534f85f82b91', '1');

/*Table structure for table `sys_user` */



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

insert  into `sys_user`(`id`,`login_name`,`login_pwd`,`user_name`,`user_phone`,`user_email`,`user_type`,`user_sex`,`register_time`,`department_id`) values ('admin','admin','E10ADC3949BA59ABBE56E057F20F883E','管理员','123456','13601094934@163.com','0','0','2014-02-19 01:00:00','1');

/*Table structure for table `sys_user_role` */



CREATE TABLE `sys_user_role` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `user_id` varchar(32) DEFAULT NULL COMMENT '用户外键',
  `role_id` varchar(32) DEFAULT NULL COMMENT '角色外键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户角色关联表';

/*Data for the table `sys_user_role` */

insert  into `sys_user_role`(`id`,`user_id`,`role_id`) values ('b0aa7180c2f1400dba95ab43b5d4d59e','admin','1');

/*Table structure for table `t_mail_log` */


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
  `user_count_total` tinyint(4) DEFAULT NULL COMMENT '任务节点总的审批人数',
  `user_count_need` tinyint(4) DEFAULT NULL COMMENT '任务节点需要的审批人数',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='用户任务表';




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


CREATE TABLE `t_app_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_key` varchar(64) DEFAULT NULL COMMENT '关联t_app key',
  `model_key` varchar(64) DEFAULT NULL COMMENT '关联act_re_model key_',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='应用模型关联表';

