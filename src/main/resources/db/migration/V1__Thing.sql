CREATE SEQUENCE THING_ID_SEQ;

CREATE TABLE THING (
  ID BIGINT PRIMARY KEY,
  NAME VARCHAR(1024),
  DELETED BOOLEAN NOT NULL DEFAULT FALSE
);