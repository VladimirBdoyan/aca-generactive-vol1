CREATE SEQUENCE country_id_sequence_Group INCREMENT 1 MINVALUE 0;
CREATE SEQUENCE country_id_sequence_Item INCREMENT 1 MINVALUE 0;

CREATE TABLE Groups
(
    id              bigint PRIMARY KEY DEFAULT nextval('country_id_sequence_Group'),
    name            varchar NOT NULL,
    parent_group_id bigint,
    items_id        bigint
);

CREATE TABLE Item
(
    id         bigint PRIMARY KEY DEFAULT nextval('country_id_sequence_Item'),
    name       varchar                       NOT NULL,
    base_price bigint                        NOT NULL,
    group_id   bigint REFERENCES Groups (id) NULL -- one two many relation
);

SELECT *
FROM Groups
         RIGHT JOIN Item I on Groups.id = I.group_id;

SELECT group_id, COUNT(group_id) AS COUNT_ITEM
FROM Item
         LEFT JOIN Groups G on Item.group_id = G.id
GROUP BY group_id;



SELECT *
FROM Groups;
SELECT *
FROM Item;

DROP TABLE Item;
DROP SEQUENCE country_id_sequence_Item;
DROP TABLE Groups;
DROP SEQUENCE country_id_sequence_Group;
