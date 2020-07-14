SELECT
    mbp.id auto_id,
    mi.agent_no,
    mi.parent_node agent_node,
    mbp.id `mid`,
    mbp.merchant_no,
    mbp.bp_id,
    mbp.`status`,
    IFNULL(mi.team_id, '') team_id,
    mi.hlf_active,
    DATE_FORMAT(mbp.create_time,'%Y-%m-%d %H:%i:%S') create_time,
    mbp.last_update_time
FROM merchant_business_product mbp
JOIN business_product_define bpd ON bpd.bp_id = mbp.bp_id
JOIN merchant_info mi ON mi.merchant_no = mbp.merchant_no
WHERE mbp.id >= :sql_last_value
order by mbp.id asc
limit 50000