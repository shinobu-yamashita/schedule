create table usertable (
  id serial NOT NULL
 ,name varchar(33) NOT NULL
 ,pass varchar(33) NOT NULL
 ,roll int
);


insert into usertable(name, pass, roll) values
('root','root',1);
