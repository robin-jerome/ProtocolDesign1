#!/bin/bash
# iot-server.sh: Shell script to start iot server
clear
echo "##################################################################################"
echo "                           Clean UP                                               "
echo "##################################################################################"

rm -rf ./logs/server_*
rm -rf ./logs/client_*
rm -rf ./null

cd db
sqlite3 iotps 'delete from client_table'

echo "Cleanup Complete"