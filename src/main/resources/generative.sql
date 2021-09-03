CREATE SEQUENCE country_id_sequence_Group INCREMENT 1 MINVALUE 0;
CREATE SEQUENCE country_id_sequence_Item INCREMENT 1 MINVALUE 0;

CREATE TABLE Groups
(
    id              bigint PRIMARY KEY DEFAULT nextval('country_id_sequence_Group'),
    name            varchar                       NOT NULL,
    parent_group_id bigint REFERENCES Groups (id) null
);

CREATE TABLE Item
(
    id         bigint PRIMARY KEY DEFAULT nextval('country_id_sequence_Item'),
    name       varchar NOT NULL,
    base_price bigint  NOT NULL,
    group_id   bigint REFERENCES Groups (id) -- one two many relation
);

ALTER TABLE groups
    ADD COLUMN item_id varchar;

ALTER TABLE Groups
DROP COLUMN item_id;

ALTER TABLE Item
    ALTER COLUMN group_id SET NOT NULL;

ALTER TABLE Item RENAME TO Items;
ALTER TABLE Items RENAME COLUMN base_price TO price_base;

TRUNCATE TABLE item;

DROP TABLE Item;
DROP SEQUENCE country_id_sequence_Item;
DROP TABLE Groups;
DROP SEQUENCE country_id_sequence_Group;