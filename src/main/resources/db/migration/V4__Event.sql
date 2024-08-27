CREATE SEQUENCE EVENT_ID_SEQ;

CREATE TABLE EVENT (
    ID BIGINT PRIMARY KEY,
    PREVIOUS_ID BIGINT NOT NULL,
    NEXT_ID BIGINT NOT NULL,
    WORLD_ITEM_TYPE VARCHAR(255) NOT NULL,
    EVENT_TYPE VARCHAR(255) NOT NULL,
    THING_ID BIGINT,
    THING_NAME VARCHAR(255),
    LINK_ID BIGINT,
    TARGET_THING_ID BIGINT,
    RESOURCE_ID BIGINT,
    RESOURCE_NAME VARCHAR(255),
    RESOURCE_MODIFIER INT,
    EVENT_POSITION VARCHAR(64) NOT NULL,
    DELETED BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (PREVIOUS_ID) REFERENCES EVENT(ID),
    FOREIGN KEY (NEXT_ID) REFERENCES EVENT(ID)
);

INSERT INTO EVENT (ID, PREVIOUS_ID, NEXT_ID, WORLD_ITEM_TYPE, EVENT_TYPE, THING_NAME) VALUES (-1, -1, -1, 'THING', 'CREATE', 'NULL_EVENT');

CREATE TABLE EVENT_POINTERS (
    POINTER_NAME VARCHAR(128) PRIMARY KEY,
    EVENT_ID BIGINT,
    FOREIGN KEY (EVENT_ID) REFERENCES EVENT(ID)
);

INSERT INTO EVENT_POINTERS (POINTER_NAME, EVENT_ID) VALUES ('CURRENT', -1);
INSERT INTO EVENT_POINTERS (POINTER_NAME, EVENT_ID) VALUES ('QUEUE_HEAD', -1);
INSERT INTO EVENT_POINTERS (POINTER_NAME, EVENT_ID) VALUES ('QUEUE_TAIL', -1);