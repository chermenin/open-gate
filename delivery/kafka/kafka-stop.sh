#!/bin/bash
set KAFKA_HOME=...
$KAFKA_HOME/bin/zookeeper-server-stop.sh &
$KAFKA_HOME/bin/kafka-server-stop.sh &
