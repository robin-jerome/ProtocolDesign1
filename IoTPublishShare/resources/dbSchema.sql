CREATE DATABASE IF NOT EXISTS iotps;
use iotps;
create table IF NOT EXISTS sensor_table 
(
id INT NOT NULL AUTO_INCREMENT,
device_id varchar(255) NOT NULL,
latest_seq_num INT,
latest_json_data TEXT DEFAULT NULL,
PRIMARY KEY (id)
);
create table IF NOT EXISTS client_table 
(
id INT NOT NULL AUTO_INCREMENT,
ip varchar(255) NOT NULL,
port INT NOT NULL,
sub_seq_no INT NOT NULL,
seq_no INT NOT NULL,
device_id varchar(255),
ack_support INT DEFAULT 0,
version INT DEFAULT 1,
PRIMARY KEY (id)
);

