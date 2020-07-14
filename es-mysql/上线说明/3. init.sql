CREATE TABLE `agent_dayhpb_share_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(25) DEFAULT NULL COMMENT '代理商编号',
  `parent_id` varchar(25) DEFAULT NULL COMMENT '父级代理商编号',
  `agent_node` varchar(255) DEFAULT NULL COMMENT '代理商节点',
  `total_count` int(10) DEFAULT '0' COMMENT '条数',
  `total_money` decimal(20,2) DEFAULT '0.00' COMMENT '金额总数',
  `acc_money` decimal(20,2) DEFAULT '0.00' COMMENT '入账总数',
  `collec_time` date DEFAULT NULL COMMENT '汇总时间',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='返现数据汇总';


CREATE TABLE `agent_daysettle_share_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(25) DEFAULT NULL COMMENT '代理商编号',
  `parent_id` varchar(25) DEFAULT NULL COMMENT '父代理商级别',
  `agent_node` varchar(255) DEFAULT NULL COMMENT '代理商节点',
  `total_count` int(10) DEFAULT '0' COMMENT '条数',
  `total_money` decimal(20,2) DEFAULT '0.00' COMMENT '金额总数',
  `acc_money` decimal(20,2) DEFAULT '0.00' COMMENT '入账总数',
  `collec_time` date DEFAULT NULL COMMENT '汇总时间',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='提现分润数据汇总';



CREATE TABLE `agent_daytrans_share_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(25) DEFAULT NULL COMMENT '代理商编号',
  `parent_id` varchar(25) DEFAULT NULL COMMENT '父代理商级别',
  `agent_node` varchar(255) DEFAULT NULL COMMENT '代理商节点',
  `total_count` int(10) DEFAULT '0' COMMENT '条数',
  `total_trans_amount` decimal(20,2) DEFAULT '0.00' COMMENT '总交易金额',
  `total_money` decimal(20,2) DEFAULT '0.00' COMMENT '金额总数',
  `acc_money` decimal(20,2) DEFAULT '0.00' COMMENT '入账总数',
  `collec_time` date DEFAULT NULL COMMENT '汇总时间',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `agent_time_index` (`agent_no`,`collec_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商分润数据汇总';


CREATE TABLE `agent_monthhpb_share_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(25) DEFAULT NULL COMMENT '代理商编号',
  `parent_id` varchar(25) DEFAULT NULL COMMENT '父级代理商编号',
  `agent_node` varchar(255) DEFAULT NULL COMMENT '代理商节点',
  `total_count` int(10) DEFAULT '0' COMMENT '条数',
  `total_money` decimal(20,2) DEFAULT '0.00' COMMENT '金额总数',
  `acc_money` decimal(20,2) DEFAULT '0.00' COMMENT '入账总数',
  `collec_time` date DEFAULT NULL COMMENT '汇总时间',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `ind_agent_no` (`agent_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='返现月数据汇总';



CREATE TABLE `agent_monthsettle_share_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(25) DEFAULT NULL COMMENT '代理商编号',
  `parent_id` varchar(25) DEFAULT NULL COMMENT '父代理商级别',
  `agent_node` varchar(255) DEFAULT NULL COMMENT '代理商节点',
  `total_count` int(10) DEFAULT '0' COMMENT '条数',
  `total_money` decimal(20,2) DEFAULT '0.00' COMMENT '金额总数',
  `acc_money` decimal(20,2) DEFAULT '0.00' COMMENT '入账总数',
  `collec_time` date DEFAULT NULL COMMENT '汇总时间',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `ind_agent_no` (`agent_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='提现分润月数据汇总';


CREATE TABLE `agent_monthtrans_share_collect` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `agent_no` varchar(25) DEFAULT NULL COMMENT '代理商编号',
  `parent_id` varchar(25) DEFAULT NULL COMMENT '父代理商级别',
  `agent_node` varchar(255) DEFAULT NULL COMMENT '代理商节点',
  `total_trans_amount` decimal(20,2) DEFAULT '0.00' COMMENT '总交易金额',
  `total_count` int(10) DEFAULT '0' COMMENT '条数',
  `total_money` decimal(20,2) DEFAULT '0.00' COMMENT '金额总数',
  `acc_money` decimal(20,2) DEFAULT '0.00' COMMENT '入账总数',
  `collec_time` date DEFAULT NULL COMMENT '汇总时间',
  `create_time` datetime DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `ind_agent_no` (`agent_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='代理商分润月数据汇总';

