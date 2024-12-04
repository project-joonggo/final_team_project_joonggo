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
                              board_name varchar(256),
                              trade_flag int default 0,
                              trade_price int,
                              board_content varchar(512),
                              read_count int default 0,
                              trade_category varchar(256),
                              like_count int default 0,
                              create_at datetime default now(),
                              is_del varchar(5) default 'N',
                              user_num bigint,
                              primary key(board_id)
);

create table product_file(
                             uuid varchar(256),
                             board_id bigint,
                             save_dir varchar(256),
                             file_name varchar(256),
                             file_type int default 0,
                             file_size bigint,
                             reg_at datetime default now(),
                             primary key(uuid)
);

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

create table chat_join(
                          user_num bigint,
                          room_id bigint
);

create table chatroom(
                         room_id bigint auto_increment,
                         comment_id int,
                         comment_user_id int,
                         chat_comment varchar(512),
                         comment_write_date datetime default now(),
                         primary key(room_id)
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
