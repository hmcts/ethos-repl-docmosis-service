create type emp_status as enum('salaried', 'fee paid');

-- auto-generated definition

create table judge_detail

(

    id uuid default nextval('judgedetail_id_seq'::regclass) not null

        constraint judgedetail_pk

            primary key,

    code              varchar(100),

    name              varchar(50),

    tribunal_office   varchar(70),

    employment_status emp_status

);




alter table judge_detail

    owner to ethos;

create unique index judgedetail_id_uindex
    on judge_detail (id);

insert into judge_detail (code, name, tribunal_office, employment_status)

values ('345_CP_Rostant', 'CP Rostant', 'MukeraCity', 'fee paid');

insert into judge_detail (code, name, tribunal_office, employment_status)

values ('3118_E_Anderson', 'CE Anderson', 'MukeraCity', 'salaried');

insert into judge_detail (code, name, tribunal_office, employment_status)

values ('33106_C_McAvoy-Newns', 'C McAvoy-Newns', 'leeds', 'fee paid');

insert into judge_detail (code, name, tribunal_office, employment_status)

values ('338_GR_Little', 'GR Little', 'leeds', 'salaried');

insert into judge_detail (code, name, tribunal_office, employment_status)

values ('jdg-code-1', 'Mark evans', 'leeds', 'salaried');

insert into judge_detail (code, name, tribunal_office, employment_status)

values ('2880_CJ O''Neill', 'CJ O''Neill', 'MukeraCity', 'salaried');

select * from judge_detail;