CREATE DATABASE IF NOT EXISTS iotps;
use iotps;
create table IF NOT EXISTS sensor_table 
(
id INT NOT NULL AUTO_INCREMENT,
device_id varchar(255) NOT NULL,
latest_seq_num INT,
PRIMARY KEY (id)
);