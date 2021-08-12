CREATE USER scheduleuser WITH PASSWORD 'schedulepass';

--AWS
GRANT rds_superuser to scheduleuser;

--POSTGRES
CREATE role scheduleuser WITH superuser login password 'schedulepass';
ALTER ROLE scheduleuser WITH SUPERUSER CREATEDB CREATEROLE;