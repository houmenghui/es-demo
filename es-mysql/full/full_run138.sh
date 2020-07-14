#/bin/bash

startAndLookLog() {
    export LOGSTASH_MYSQL_HOST=192.168.1.138
    export LOGSTASH_MYSQL_PORT=3306
    export LOGSTASH_MYSQL_DB=nposp
    export LOGSTASH_MYSQL_USERNAME=pcx
    export LOGSTASH_MYSQL_PASSWORD=pcx@123.com><CN
    export LOGSTASH_HOME=/opt/logstash-6.2.2
    export LOGSTASH_CONFIG_HOME=${LOGSTASH_HOME}/kq_nposp_es/full
    export LOGSTASH_ES_HOST=192.168.3.172
    export LOGSTASH_ES_PORT=9200
    export LOGSTASH_ES_INDEX=kq_nposp_es
    mkdir -p ${LOGSTASH_CONFIG_HOME}/lastRun
    pid=`ps -ef | grep 'logstash' | grep -v grep | awk  '{print $2}'`
    echo "kill -9 $pid"
    kill -9 $pid
    nohup ${LOGSTASH_HOME}/bin/logstash -f ${LOGSTASH_CONFIG_HOME}/conf/$1.conf > /dev/null 2>&1 &
    tail -f ${LOGSTASH_HOME}/logs/logstash-plain.log
}

case "$1" in
    agent)
        startAndLookLog agent
        ;;
    order)
        startAndLookLog order
       ;;
    merchant)
        startAndLookLog merchant
        ;;
    mbp)
        startAndLookLog mbp
        ;;
    all)
        startAndLookLog all
        ;;
    *)
        echo "Usage: $0 (all|agent|order|merchant|mbp)"
esac


