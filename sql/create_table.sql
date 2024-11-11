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