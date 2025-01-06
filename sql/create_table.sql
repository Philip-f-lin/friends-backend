-- auto-generated definition
create table user
(
    id           bigint auto_increment primary key,
    username     varchar(256)                        null comment '使用者名稱',
    user_account  varchar(256)                        null comment '帳號',
    avatar_url    varchar(1024)                       null comment '大頭照',
    gender       tinyint                             null comment '性別',
    user_password varchar(512)                        not null comment '密碼',
    phone        varchar(128)                        null comment '電話',
    email        varchar(512)                        null comment '電子郵件',
    user_status   int       default 0                 not null comment '狀態 0正常',
    create_time   datetime  default CURRENT_TIMESTAMP null comment '創建時間',
    update_time   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete     tinyint   default 0                 null comment '是否刪除',
    tags         varchar(1024)                       null comment '標籤 Json 列表'
)
    comment '使用者';

create table team
(
    id          bigint auto_increment comment 'id' primary key,
    name        varchar(256)                       not null comment '隊伍名稱',
    description varchar(1024)                      null comment '簡介',
    max_num      int      default 1                 not null comment '最大人數',
    user_id      bigint                             null comment '使用者 id ( 同時為隊伍隊長 id )',
    status      int      default 0                 not null comment '0 - 公開，1 - 加密(私人)',
    password    varchar(512)                       null comment '密碼',
    create_time  datetime default CURRENT_TIMESTAMP null comment '創建時間',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete    tinyint  default 0                 not null comment '是否刪除'
)
    comment '隊伍';

create table user_team
(
    id         bigint auto_increment comment 'id' primary key,
    user_id     bigint comment '使用者 id',
    team_id     bigint comment '隊伍 id',
    join_time   datetime                           null comment '加入時間',
    create_time datetime default CURRENT_TIMESTAMP null comment '創建時間',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    is_delete   tinyint  default 0                 not null comment '是否刪除'
)
    comment '使用者隊伍關係表';