create table sensor_table
(
id INTEGER PRIMARY KEY,
device_id varchar(255) NOT NULL,
latest_seq_num INTEGER,
latest_json_data TEXT
);

create table client_table
(
id INTEGER PRIMARY KEY,
ip varchar(255) NOT NULL,
port INTEGER,
sub_seq_no INTEGER,
seq_no INTEGER,
device_id varchar(255),
ack_support INTEGER DEFAULT 1,
version INTEGER DEFAULT 1
);