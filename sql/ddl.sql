CREATE USER 'cms'@'localhost' IDENTIFIED BY '111111';
GRANT ALL ON *.* TO 'cms'@'localhost';

CREATE DATABASE IF NOT EXISTS cms DEFAULT CHARSET utf8;

USE cms;

CREATE TABLE media (
  id      INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  `desc`  VARCHAR(56)                    NOT NULL,
  siteurl VARCHAR(256),
  logourl VARCHAR(256)
);

CREATE TABLE entity_item (
  id         BIGINT PRIMARY KEY  NOT NULL,
  pid        INT                 NOT NULL,
  title      VARCHAR(128)        NOT NULL,
  subhead    VARCHAR(128),
  content    BLOB,
  ctime      TIMESTAMP           NOT NULL,
  uptime     TIMESTAMP           NOT NULL,
  priority   TINYINT DEFAULT 60,
  status     TINYINT             NOT NULL,
  channelId  INT                 NOT NULL,
  mediaId    INT,
  author     VARCHAR(56),
  editor     INT                 NOT NULL,
  dutyEditor INT,
  url        VARCHAR(256),
  category   VARCHAR(512)        NOT NULL,
  shortName  VARCHAR(56),
  keyword    VARCHAR(56),
  pictures   VARCHAR(512),
  reurl      VARCHAR(256),
  tags       VARCHAR(56)
);

CREATE TABLE id_seq (
  id      BIGINT PRIMARY KEY NOT NULL,
  `group` VARCHAR(56)        NOT NULL
);

CREATE TABLE channel
(
  id   INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  name VARCHAR(255),
  dir  VARCHAR(20)                    NOT NULL,
  UNIQUE (dir)
);

CREATE TABLE subject
(
  id              INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
  pid             INT          NOT NULL,
  category        VARCHAR(256) NOT NULL,
  shortName       VARCHAR(125) NOT NULL,
  name            VARCHAR(125) NOT NULL,
  ctime           TIMESTAMP    NOT NULL,
  uptime          TIMESTAMP    NOT NULL,
  channelId       INT          NOT NULL,
  priority        INT DEFAULT 60,
  status          INT DEFAULT 0,
  `desc`          VARCHAR(256),
  tags            VARCHAR(125),
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

# 网站页面模版
CREATE TABLE template (
  id         INT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name       VARCHAR(50) NOT NULL,
  type       TINYINT     NOT NULL,
  content    BLOB,
  createTime TIMESTAMP   NOT NULL,
  uptime     TIMESTAMP   NOT NULL,
  status     TINYINT DEFAULT 1,
  creator    INT         NOT NULL #创建者
);


