create table user(
                     user_num bigint auto_increment,
                     user_id varchar(256),
                     signflag int default 0,
                     password varchar(256),
                     user_name varchar(20),
                     birth_year varchar(5),
                     birth_month varchar(5),
                     birth_day varchar(5),
                     score double default 0,
                     is_del varchar(5) default 'N',
                     address varchar(256),
                     primary key(user_num)
);

create table sellbuy_board(
                              board_id bigint auto_increment,
                              seller_id bigint not null,
                              category varchar(256) not null,
                              board_name varchar(256) not null,
                              trade_flag int default 0,
                              trade_price int not null,
                              board_content text,
                              like_count int default 0,
                              read_count int default 0,
                              reg_at datetime default now(),
                              is_del varchar(5) default 'N',
                              primary key (board_id));

create table product_file(
                             uuid varchar(256),
                             board_id bigint not null,
                             save_dir varchar(256),
                             file_name varchar(256),
                             file_type int default 0,
                             file_size bigint,
                             reg_at datetime default now(),
                             primary key(uuid));

create table qa_board(
                         qa_id bigint auto_increment,
                         qa_name varchar(256),
                         qa_content varchar(256),
                         reg_at datetime default now(),
                         is_del varchar(5) default 'N',
                         user_num bigint,
                         comment_count int,
                         primary key(qa_id)
);

create table qa_comment(
                           cno bigint auto_increment,
                           user_num bigint,
                           qa_id bigint,
                           qa_com_content varchar(256),
                           reg_at datetime default now(),
                           is_del varchar(5) default 'N',
                           recomment_count int,
                           primary key(cno)
);

create table qa_recomment(
                             re_cno bigint auto_increment,
                             cno bigint,
                             user_num bigint,
                             re_content varchar(256),
                             reg_at datetime default now(),
                             primary key(re_cno)
);

create table auth(
                     auth_id bigint auto_increment,
                     user_num bigint,
                     auth varchar(256),
                     primary key(auth_id)
);

create table locate(
                       locate_id bigint auto_increment,
                       user_num bigint,
                       primary key(locate_id)
);

create table chat_room(
                         room_id bigint auto_increment,
						 user_num int not null,
                         room_name varchar(256) not null,
                         room_status varchar(5) default 'Y',
                         primary key(room_id)
                         -- foreign key(user_num) references user(user_num)
);

create table chat_join(
                          room_id bigint not null,
                          user_num bigint not null
						  -- foreign key(room_id) references chat_room(room_id),
                          -- foreign key(user_num) references user(user_num)
 );

create table chat_comment(
						comment_id int auto_increment,
                        room_id int not null,
                        comment_user_num int not null,
                        comment_content varchar(512),
                        comment_write_date datetime default now(),
                        primary key(comment_id)
                        -- foreign key(room_id) references chat_room(room_id)
                        -- foreign key(comment_user_num) references user(user_num)
);

create table reviewlist(
                           review_id bigint auto_increment,
                           user_num bigint,
                           primary key(review_id)
);


create table resonlist(
                          comp_id bigint auto_increment,
                          comp_content varchar(50),
                          primary key(comp_id)
);

create table reportlist(
                           report_num bigint auto_increment,
                           report_comp_id int,
                           report_date datetime default now(),
                           primary key(report_num)
);

create table notifications(
                              alarm_id bigint auto_increment,
                              user_num bigint,
                              msg varchar(256),
                              type enum('general', 'warning', 'notification'),
                              reg_at datetime default now(),
                              read_or_not boolean,
                              primary key(alarm_id)
);

create table coupon(
                       coupon_id bigint auto_increment,
                       coupon_name varchar(256),
                       discount int,
                       primary key(coupon_id)
);

create table couponlist(
                           coupon_id bigint,
                           user_num bigint
);


create table trade(
                      trade_id bigint auto_increment,
                      user_num bigint,
                      trade_category int,
                      trade_price int,
                      trade_date timestamp,
                      primary key(trade_id)
);

create table payment (
                         payment_id bigint auto_increment,
                         merchant_uid varchar(255) not null unique,
                         board_id bigint not null,
                         amount int not null,                     -- 결제 금액
                         product_name varchar(255) not null,      -- 상품 이름
                         payment_status varchar(50) default 'SUCCESS', -- 결제 상태 (예: SUCCESS, FAILED, CANCELLED)
                         paid_at datetime default now(),         -- 결제 일시
                         refunded_amount int default 0,           -- 환불 금액 (환불 시 업데이트)
                         cancel_flag boolean default false,       -- 결제 취소 여부
                         primary key (payment_id));
-----------------------------------------------------
--241205
alter table user add column social_id varchar(256);

--241206
alter table user add column phone varchar(50);

--241209
ALTER TABLE user
    CHANGE COLUMN address address_1 VARCHAR(256);
ALTER TABLE user
    ADD COLUMN address_2 VARCHAR(256) AFTER address_1,
ADD COLUMN post_code VARCHAR(10) AFTER address_2;
alter table user add column address_3 varchar(256) after address_2;

--241210
ALTER TABLE payment ADD COLUMN user_num bigint UNIQUE;
ALTER TABLE sellbuy_board modify seller_id bigint;
ALTER TABLE sellbuy_board MODIFY COLUMN board_content TEXT;
ALTER TABLE product_file ADD COLUMN file_url VARCHAR(512);
ALTER TABLE payment DROP INDEX user_num;