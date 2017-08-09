##############
#数据库创建脚本
##############

CREATE TABLE user_basic
(
    user_id           BIGINT      NOT NULL,
    user_name         CHAR(64)    NOT NULL,
    user_sex          TINYINT     NOT NULL,
    user_create_time  DATE        NULL,
    PRIMARY KEY(user_id)
) ENGINE=InnoDB;


CREATE TABLE user_tieba
(
    user_id     BIGINT    NOT NULL,
    tieba_name  CHAR(64)  NOT NULL,
    update_time DATETIME  NOT NULL,
    is_like     TINYINT   NULL,
    level_id    TINYINT   NULL,
    level_name  CHAR(64)  NULL,
    cur_score   INT       NULL,
    bawu        TINYINT   NULL,
    PRIMARY KEY (user_id, tieba_name)
) ENGINE=InnoDB;


CREATE TABLE post
(
    tieba_name CHAR(64)    NOT NULL,
    thread_id  BIGINT      NOT NULL,
    post_id    BIGINT      NOT NULL,
    user_id    BIGINT      NOT NULL,
    content    TEXT        NOT NULL,
    post_no    INT         NOT NULL,
    post_index INT         NOT NULL,
    data       DATETIME    NOT NULL,
    is_anonym  BOOLEAN     NULL,
    open_id    CHAR(64)    NULL,
    open_type  CHAR(64)    NULL,
    vote_crypt CHAR(255)   NULL,
    type       CHAR(64)    NULL,
    ptype      CHAR(64)    NULL,
    is_saveface BOOLEAN    NULL,
    props      CHAR(255)   NULL,
    pb_tpoint  CHAR(255)   NULL,
    PRIMARY KEY(post_id)
) ENGINE=InnoDB;


CREATE TABLE comment
(
    tieba_name     CHAR(64)    NOT NULL,
    post_id        BIGINT      NOT NULL,
    comment_id     BIGINT      NOT NULL,
    user_id        BIGINT      NOT NULL,
    now_time       DATETIME    NOT NULL,
    content        MEDIUMTEXT  NOT NULL,
    ptype          CHAR(255)   NULL,
    during_time    CHAR(255)   NULL,
    direct_user_id BIGINT      NULL,
    PRIMARY KEY(comment_id)
) ENGINE=InnoDB;


CREATE TABLE thread
(
    tieba_name     CHAR(64)  NOT NULL,
    thread_id      BIGINT    NOT NULL,
    title          CHAR(255)  NOT NULL,
    first_post_id  BIGINT    NOT NULL,
    user_id        BIGINT    NOT NULL,
    data           DATETIME  NULL,
    update_time    DATETIME  NOT NULL,
    is_bakan       CHAR(255) NULL,
    vid            CHAR(255) NULL,
    is_good        BOOLEAN   NULL,
    is_top         BOOLEAN   NULL,
    is_protal      CHAR(255) NULL,
    is_membertop   CHAR(255) NULL,
    is_multi_forum CHAR(255) NULL,
    frs_tpiont     CHAR(255) NULL,
    PRIMARY KEY (thread_id)
) ENGINE=InnoDB;
