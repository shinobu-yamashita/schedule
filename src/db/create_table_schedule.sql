create table schedule (
  id serial NOT NULL
 ,userid int NOT NULL
 ,scheduledate date NOT NULL
 ,starttime time 
 ,endtime time 
 ,schedule varchar(67)
 ,schedulememo varchar(67)
);
 