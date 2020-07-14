DROP TABLE IF EXISTS `terminal_operate`;
CREATE TABLE `terminal_operate` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(50) DEFAULT NULL COMMENT '代理商编号',
  `for_operater` varchar(25) DEFAULT NULL COMMENT '出库指从谁出库 ，入库指从入库给谁',
  `oper_num` int(11) DEFAULT NULL COMMENT '操作数量',
  `sn_array` text COMMENT '操作的sn数组集合',
  `oper_detail_type` varchar(10) DEFAULT NULL COMMENT '具体操作类型 1-出/入库，2-回收/被回收',
  `oper_type` varchar(10) DEFAULT NULL COMMENT '筛选栏类型 1-入库  2-出库',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='机具操作记录表';