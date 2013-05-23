CREATE DATABASE  IF not EXISTS cms_framework DEFAULT charset utf8;

use cms_framework;

CREATE TABLE cms_framework.channel
(
  id   INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  name VARCHAR(255),
  dir  VARCHAR(20)                    NOT NULL
);

CREATE TABLE subject
(
  id              INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  pid             INT          NOT NULL,
  fullpath        VARCHAR(150) NOT NULL,
  name            VARCHAR(50)  NOT NULL,
  `desc`          VARCHAR(200),
  ctime           INT          NOT NULL,
  priority        INT DEFAULT 60,
  status          INT DEFAULT 0,
  channelId       INT          NOT NULL,
  editorId        INT,
  templateId      INT,
  bakTemplateList VARCHAR(200),
  `type`          INT DEFAULT 0
);

CREATE TABLE user (
  id      INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name    VARCHAR(50)  NOT NULL,
  `desc`  VARCHAR(50)  NOT NULL,
  email   VARCHAR(150) NOT NULL,
  passwd  VARCHAR(50)  NOT NULL,
  phone   VARCHAR(50)  NOT NULL,
  mobile  VARCHAR(50)  NOT NULL,
  address VARCHAR(50)  NOT NULL,
  `group` INT
);


