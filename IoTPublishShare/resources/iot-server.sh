#!/bin/bash
# iot-server.sh: Shell script to start iot server
clear
echo "##################################################################################"
echo "                           Starting IoT Server                                    "
echo "##################################################################################"

# define usage function
usage(){

	echo "Incorrect Usage"
	echo "Usage: ./iot-server <publish port> <subscribe port> <version>"
}
curr_time=`date +%s`
mkdir ./logs 1>/dev/null 2>/dev/null

# You need exactly 3 arguments to the shell script
if [ $# -ne 3 ]
then
  usage()
  exit 1
fi

java -cp iotps.jar com.aalto.protocol.design.iotps.start.IoTPSServerStarter $@ 1>./logs/server_info_$curr_time.log 2>./logs/server_error_$curr_time.log &
