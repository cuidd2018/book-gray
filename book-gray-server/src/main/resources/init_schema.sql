
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gray_instance
-- ----------------------------
DROP TABLE IF EXISTS `gray_instance`;
CREATE TABLE `gray_instance`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `instance_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `service_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `policy_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '略策组唯一标记',
  `open_gray` smallint(1) DEFAULT 1 COMMENT '是否开启灰度0：否 1：是',
  `status` smallint(1) DEFAULT 0 COMMENT '是否在线 0：否 1：是',
  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `env` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务资源环境',
  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `instance_id`(`instance_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务实例信息表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gray_policy
-- ----------------------------
DROP TABLE IF EXISTS `gray_policy`;
CREATE TABLE `gray_policy`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `policy_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `policy_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '灰度策略类型',
  `policy_key` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略参数名',
  `policy_value` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略参数值',
  `policy_match_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '匹配方式',
  `is_delete` smallint(1) DEFAULT 0,
  `create_time` timestamp(0) DEFAULT NULL,
  `update_time` timestamp(0) DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `policy_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建者',
  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `policy_id`(`policy_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '灰度策略表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gray_policy_group
-- ----------------------------
DROP TABLE IF EXISTS `gray_policy_group`;
CREATE TABLE `gray_policy_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `policy_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `alias` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `enable` smallint(1) DEFAULT 0 COMMENT '否是可用0：否 1：是',
  `group_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '策略组类型（AND OR）',
  `is_delete` smallint(1) DEFAULT 0 COMMENT '否是删除0：否 1：是',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建者',
  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `policy_group_id`(`policy_group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '灰度策略组表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gray_policy_group_policy
-- ----------------------------
DROP TABLE IF EXISTS `gray_policy_group_policy`;
CREATE TABLE `gray_policy_group_policy`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `policy_group_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '略策组唯一标记',
  `policy_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '略策标记',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `policy_group_id__policy_id`(`policy_group_id`, `policy_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '灰度策略组和灰度策略M:N映射关系表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gray_rbac_department
-- ----------------------------
DROP TABLE IF EXISTS `gray_rbac_department`;
CREATE TABLE `gray_rbac_department`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '部门唯一编号',
  `department_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '部门名称',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建者udid',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `department_id`(`department_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of gray_rbac_department
-- ----------------------------
INSERT INTO `gray_rbac_department` VALUES (4, 'DEPARTMENT_000', '鼎阅集团', NULL);

-- ----------------------------
-- Table structure for gray_rbac_resources
-- ----------------------------
DROP TABLE IF EXISTS `gray_rbac_resources`;
CREATE TABLE `gray_rbac_resources`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资源唯一编号',
  `resource` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资源',
  `resource_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资源名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `resource_id`(`resource_id`) USING BTREE,
  UNIQUE INDEX `resource`(`resource`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '资源表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gray_rbac_role
-- ----------------------------
DROP TABLE IF EXISTS `gray_rbac_role`;
CREATE TABLE `gray_rbac_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '角色唯一编号',
  `role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `role_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '角色编号',
  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `is_admin` smallint(1) DEFAULT 0 COMMENT '是否总管理员（0：不是，1：是）',
  `is_department_admin` smallint(1) DEFAULT 0 COMMENT '是否部门管理员（0：不是，1：是）',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建者udid',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_id`(`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of gray_rbac_role
-- ----------------------------
INSERT INTO `gray_rbac_role` VALUES (9, 'ROLE_000', 'admin', '总管理员', 'DEPARTMENT_000', 1, 0, NULL);

-- ----------------------------
-- Table structure for gray_rbac_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `gray_rbac_role_resource`;
CREATE TABLE `gray_rbac_role_resource`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resource_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '资源唯一编号',
  `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '角色唯一编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `resource_id__role_id`(`resource_id`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 54 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色资源关联表' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for gray_rbac_user
-- ----------------------------
DROP TABLE IF EXISTS `gray_rbac_user`;
CREATE TABLE `gray_rbac_user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `udid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作人员唯一编号',
  `account` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '账户',
  `nickname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '操作人员名字',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备注',
  `department_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '所属部门唯一编号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户密码',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '创建者udid',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `account`(`account`) USING BTREE,
  UNIQUE INDEX `udid`(`udid`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of gray_rbac_user
-- ----------------------------
INSERT INTO `gray_rbac_user` VALUES (8, 'USER_000', 'admin', '总管理员', '总管理员', 'DEPARTMENT_000', '$2a$10$FhdUxNSv8uw5t4YHa7.gXOsasrcbjdG8.JDrEU9BSLSOEoymvjg.G', NULL);

-- ----------------------------
-- Table structure for gray_rbac_user_role
-- ----------------------------
DROP TABLE IF EXISTS `gray_rbac_user_role`;
CREATE TABLE `gray_rbac_user_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `udid` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '用户唯一编号',
  `role_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '权限唯一编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `udid__role_id`(`udid`, `role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色表' ROW_FORMAT = Compact;

-- ----------------------------
-- Records of gray_rbac_user_role
-- ----------------------------
INSERT INTO `gray_rbac_user_role` VALUES (12, 'USER_000', 'ROLE_000');

-- ----------------------------
-- Table structure for gray_service
-- ----------------------------
DROP TABLE IF EXISTS `gray_service`;
CREATE TABLE `gray_service`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '服务名称',
  `service_id` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `sort` int(3) DEFAULT NULL COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '服务说明',
  `create_time` timestamp(0) DEFAULT NULL,
  `update_time` timestamp(0) DEFAULT NULL,
  `is_delete` smallint(1) DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `service_id`(`service_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务表' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
