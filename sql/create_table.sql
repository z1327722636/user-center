create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                        null comment '用户昵称',
    userAccount  varchar(256)                        null comment '登录账户',
    userPassword varchar(512)                        not null comment '登录密码',
    avatarUrl    varchar(1024)                       null comment '头像',
    gender       tinyint                             null comment '性别',
    phone        varchar(128)                        null comment '电话',
    email        varchar(512)                        null comment '邮箱',
    userStatus   int       default 0                 not null comment '状态0 - 正常',
    createTime   timestamp default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint   default 0                 null comment '逻辑删除',
    userRole     int       default 0                 not null comment '用户角色-普通用户0/管理员1'
)
    comment '用户';