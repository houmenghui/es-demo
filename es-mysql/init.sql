INSERT INTO sys_config(param_key, param_value, remark) VALUE
('agentApi2_login_token_ttl', '2592000', '代理商api2登陆有效时间,单位秒'),
('agentApi2_merchant_quality_search_cur_month_trans_money', '50000', '优质商户 本月交易金额>=x元'),
('agentApi2_merchant_active_search_trans_day', '30', '活跃商户 近x天交易笔数>=x笔,且交易金额>=x元--天数'),
('agentApi2_merchant_active_search_trans_order_num', '2', '活跃商户 近x天交易笔数>=x笔,且交易金额>=x元--笔数'),
('agentApi2_merchant_active_search_trans_money', '10', '活跃商户 近x天交易笔数>=x笔,且交易金额>=x元--元'),
('agentApi2_merchant_max_trans_slide', '1000', '商户交易下滑汇总商户的最大数量'),
('agentApi2_merchant_sleep_search_merchant_create', '60', '休眠商户 入网≥X天,连续无交易大于X天--入网天数'),
('agentApi2_merchant_sleep_search_trans_time', '60', '休眠商户 入网≥X天,连续无交易大于X天--无交易天数');

ALTER TABLE agent_info ADD COLUMN safe_password VARCHAR(32) DEFAULT NULL COMMENT '安全密码';

ALTER TABLE `nposp`.`agent_info` ADD COLUMN `regist_type` VARCHAR(10) NULL COMMENT '代理商注册类型:拓展代理为1,其他为空';

CREATE TABLE `profit_update_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `share_id` bigint(20) NOT NULL COMMENT '分润规则id',
  `cost_history` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '修改前代理商成本',
  `cost` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '修改后代理商成本',
  `share_profit_percent_history` decimal(10,6) DEFAULT NULL COMMENT '修改前分润比例',
  `share_profit_percent` decimal(10,6) DEFAULT NULL COMMENT '修改后分润比例',
  `efficient_date` datetime DEFAULT NULL COMMENT '生效日期',
  `effective_status` varchar(20) CHARACTER SET utf8 DEFAULT NULL COMMENT '是否生效:0-未生效,1-已生效',
  `update_date` datetime DEFAULT NULL COMMENT '修改日期',
  `auther` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '修改人',
  `share_task_id` int(20) DEFAULT NULL COMMENT '对应agent_share_rule_task表id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC COMMENT='修改分润记录表';

