# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table user (
  id                        bigint auto_increment not null,
  vorname                   varchar(255),
  nachname                  varchar(255),
  geschlecht                integer,
  user_name                 varchar(255),
  passwort                  varchar(255),
  constraint ck_user_geschlecht check (geschlecht in (0,1)),
  constraint pk_user primary key (id))
;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

