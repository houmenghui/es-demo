#/bin/bash
export LOGSTASH_MYSQL_HOST=192.168.1.183
export LOGSTASH_MYSQL_PORT=5567
export LOGSTASH_MYSQL_DB=kq_nposp
export LOGSTASH_MYSQL_USERNAME=kf_wr
export LOGSTASH_MYSQL_PASSWORD=kf@#!123
export LOGSTASH_HOME=/opt/logstash-6.2.2
export LOGSTASH_CONFIG_HOME=${LOGSTASH_HOME}/kq_nposp_es/increment
export LOGSTASH_ES_HOST=192.168.1.147
export LOGSTASH_ES_PORT=9200
export LOGSTASH_ES_INDEX=kq_nposp_es
mkdir -p ${LOGSTASH_CONFIG_HOME}/lastRun
echo "--- `date -d -2hour '+%Y-%m-%d %k:%M:%S'`.000000000 +08:00" > ${LOGSTASH_CONFIG_HOME}/lastRun/agent_last_run.txt
echo "--- `date -d -2hour '+%Y-%m-%d %k:%M:%S'`.000000000 +08:00" > ${LOGSTASH_CONFIG_HOME}/lastRun/order_last_run.txt
echo "--- `date -d -2hour '+%Y-%m-%d %k:%M:%S'`.000000000 +08:00" > ${LOGSTASH_CONFIG_HOME}/lastRun/merchant_last_run.txt
echo "--- `date -d -2hour '+%Y-%m-%d %k:%M:%S'`.000000000 +08:00" > ${LOGSTASH_CONFIG_HOME}/lastRun/mbp_last_run.txt

nohup ${LOGSTASH_HOME}/bin/logstash -f ${LOGSTASH_CONFIG_HOME}/conf/increment.conf > /dev/null 2>&1 &
tail -f ${LOGSTASH_HOME}/logs/logstash-plain.log
