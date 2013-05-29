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
	echo "Example: ./iot-server 5060 5061 1                            "
}
curr_time=`date +%s`
# dbUrl=jdbc:sqlite:/u/92/babujer1/unix/IoTPSAssignment/db/iotps
dbUrl=jdbc:sqlite:D:\\Software\\sqlite-shell-win32-x86-3071700\\iotps
mkdir ./logs 1>./null 2>./null

# You need exactly 3 arguments to the shell script
if [ $# -ne 3 ]
then
  usage
  exit 1
fi

#java -cp iotps.jar sqlite-jdbc-3.7.2.jar com.aalto.protocol.design.iotps.start.IoTPSServerStarter $1 $2 5062 $3 $dbUrl 1>./logs/server_info_$curr_time.log 2>./logs/server_error_$curr_time.log &
java -cp iotps.jar:sqlite-jdbc-3.7.2.jar com.aalto.protocol.design.iotps.start.IoTPSServerStarter $1 $2 5062 $3 $dbUrl 
