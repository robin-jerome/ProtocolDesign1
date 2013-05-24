#!/bin/bash
# iot-client.sh: Shell script to start iot clients
clear
echo "##################################################################################"
echo "                           Starting IoT Clients                                   "
echo "##################################################################################"

# define usage function
usage(){
	echo "Incorrect Usage"
	echo "Usage: ./iot-client <server ip> <server port> <version> [list of sensor ids]"
}

curr_time=`date +%s`
mkdir ./logs 1>/dev/null 2>/dev/null

# You need atleast arguments to the shell script
if [ $# -lt 4 ]
then
  usage()
  exit 1
fi

java -cp iotps.jar com.aalto.protocol.design.iotps.start.IoTPSClientStarter $@ 1>./logs/client_info_$curr_time.log 2>./logs/client_error_$curr_time.log &