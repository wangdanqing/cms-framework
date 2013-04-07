create database cms_framework if exists default utf8;

CREATE TABLE cms_framework.channel
(
    id INT PRIMARY KEY auto_increment NOT NULL,
    name varchar(255),
    dir varchar(20)  NOT NULL
);

