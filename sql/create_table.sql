-- auto-generated definition
create table user
(
    id           bigint auto_increment primary key,
    username     varchar(256)                        null comment '使用者名稱',
    userAccount  varchar(256)                        null comment '帳號',
    avatarUrl    varchar(1024)                       null comment '大頭照',
    gender       tinyint                             null comment '性別',
    userPassword varchar(512)                        not null comment '密碼',
    phone        varchar(128)                        null comment '電話',
    email        varchar(512)                        null comment '電子郵件',
    userStatus   int       default 0                 not null comment '狀態 0正常',
    createTime   datetime  default CURRENT_TIMESTAMP null comment '創建時間',
    updateTime   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint   default 0                 null comment '是否刪除',
    tags         varchar(1024)                       null comment '標籤 Json 列表'
)
    comment '使用者';