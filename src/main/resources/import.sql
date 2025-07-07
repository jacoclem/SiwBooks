DROP SEQUENCE IF EXISTS utente_seq CASCADE;
DROP SEQUENCE IF EXISTS credentials_seq CASCADE;

CREATE SEQUENCE utente_seq START 1 INCREMENT 1;
CREATE SEQUENCE credentials_seq START 1 INCREMENT 1;

insert into utente (cognome, data_di_nascita, email, nome, id) values ('Clementucci', '2002-09-18', 'jacopoclementucci@gmail.com', 'Jacopo', nextval('utente_seq'));

insert into credentials (password, ruolo, username, utente_id, id) values ('$2a$10$PB.d8Tb1diMVo6F49ZrZ.Om6OLhcHoXRB3Sr0kf9Kd0YsCoreNTnm', 'ADMIN', 'jacoclem', currval('utente_seq'), nextval('credentials_seq'));