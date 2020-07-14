SELECT
    id auto_id,
    agent_no,
    agent_node,
    agent_name,
    agent_level,
    parent_id,
    one_level_id,
    email,
    mobilephone,
    DATE_FORMAT(create_date,'%Y-%m-%d %H:%i:%S') create_time,
    last_update_time
FROM agent_info
WHERE id >= :sql_last_value
ORDER BY id ASC
limit 50000