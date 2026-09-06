CREATE DATABASE IF NOT EXISTS `nexusbank_customer`;
CREATE DATABASE IF NOT EXISTS `nexusbank_admin`;

CREATE USER IF NOT EXISTS 'nexususer'@'%' IDENTIFIED BY 'nexuspass';
GRANT ALL PRIVILEGES ON `nexusbank_customer`.* TO 'nexususer'@'%';
GRANT ALL PRIVILEGES ON `nexusbank_admin`.* TO 'nexususer'@'%';
FLUSH PRIVILEGES;
