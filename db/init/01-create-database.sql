-- Da eseguire UNA volta come root nel container MySQL esistente.
-- Crea il database, l'utente applicativo e un utente di sola lettura per Power BI.

CREATE DATABASE IF NOT EXISTS retail_analytics CHARACTER SET utf8mb4;

CREATE USER IF NOT EXISTS 'retail'@'%' IDENTIFIED BY 'retail';
GRANT ALL PRIVILEGES ON retail_analytics.* TO 'retail'@'%';

CREATE USER IF NOT EXISTS 'retail_bi'@'%' IDENTIFIED BY 'retail_bi';
GRANT SELECT ON retail_analytics.* TO 'retail_bi'@'%';

FLUSH PRIVILEGES;
