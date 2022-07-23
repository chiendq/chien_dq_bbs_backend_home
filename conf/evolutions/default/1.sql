-- posts schema

-- !Ups
create table post
(
    id          bigint auto_increment,
    title       text                               not null,
    content     text                               not null,
    thumbnail   text                               not null,
    author_name text                               not null,
    created_at  datetime default CURRENT_TIMESTAMP not null,
    updated_at  datetime default CURRENT_TIMESTAMP not null,
    constraint post_pk
        primary key (id)
);


-- !Downs

DROP TABLE post;