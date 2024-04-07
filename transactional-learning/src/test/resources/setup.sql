TRUNCATE TABLE person CASCADE;
TRUNCATE TABLE room CASCADE;
TRUNCATE TABLE booking CASCADE;


insert into person (id, name) values (1, 'Frodo');
insert into person (id, name) values (2, 'Sam');
insert into room (id, name) values (1, 'Rivendell')