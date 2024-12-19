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

create table reasonlist(
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

CREATE TABLE wish_list(
                         wish_id BIGINT AUTO_INCREMENT,   -- 찜리스트 아이디
                         user_num BIGINT NOT NULL,        -- 사용자 아이디 (user_num)
                         board_id BIGINT NOT NULL,        -- 상품 아이디 (board_id)
                         reg_at datetime default now(),   -- 추가된 시간
                         primary key(wish_id));
CREATE TABLE notifications (
                        notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- PK 이름을 notification_id로 변경
                        user_id BIGINT NOT NULL,
                        message VARCHAR(255) NOT NULL,
                        status VARCHAR(20) DEFAULT 'UNREAD',  -- 읽음 상태 (읽음, 안읽음)
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                          );
CREATE TABLE qna_board (
                            qna_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT DEFAULT NULL,
                            category VARCHAR(256) NOT NULL,
                            qna_name VARCHAR(256) NOT NULL,
                            qna_content TEXT,
                            reg_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                            is_del VARCHAR(5) DEFAULT 'N'
                        );
CREATE TABLE qna_file (
                        uuid VARCHAR(256) NOT NULL PRIMARY KEY,
                        qna_id BIGINT NOT NULL,
                        save_dir VARCHAR(256) DEFAULT NULL,
                        file_name VARCHAR(256) DEFAULT NULL,
                        file_type INT DEFAULT 0,
                        file_size BIGINT DEFAULT NULL,
                        reg_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                        file_url VARCHAR(512) DEFAULT NULL
                    );
 CREATE TABLE answer (
                         ano BIGINT AUTO_INCREMENT PRIMARY KEY,
                         qna_id BIGINT, -- 질문 번호 (qna 테이블의 ID)
                         user_num BIGINT, -- 사용자 번호 (user 테이블의 ID)
                         answer TEXT,         -- 답변 내용
                         reg_at DATETIME DEFAULT NOW()     -- 답변 등록 시간
                     );
 CREATE TABLE reply (
                         reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         writer_name varchar(255),
                         ano BIGINT, -- 부모 답변 번호 (answer 테이블의 ano)
                         user_num BIGINT, -- 대댓글 작성자 번호 (user 테이블의 user_num)
                         reply TEXT,           -- 대댓글 내용
                         reg_at DATETIME DEFAULT NOW()     -- 대댓글 등록 시간
                     );

-----------------------------------------------------
-- 241205
alter table user add column social_id varchar(256);

-- 241206
alter table user add column phone varchar(50);

-- 241209
ALTER TABLE user
    CHANGE COLUMN address address_1 VARCHAR(256);
ALTER TABLE user
    ADD COLUMN address_2 VARCHAR(256) AFTER address_1,
ADD COLUMN post_code VARCHAR(10) AFTER address_2;
alter table user add column address_3 varchar(256) after address_2;

-- 241210
ALTER TABLE payment ADD COLUMN user_num bigint UNIQUE;
ALTER TABLE sellbuy_board modify seller_id bigint;
ALTER TABLE sellbuy_board MODIFY COLUMN board_content TEXT;
ALTER TABLE product_file ADD COLUMN file_url VARCHAR(512);
ALTER TABLE payment DROP INDEX user_num;

-- 241211
CREATE TABLE wish_list(
                          wish_id BIGINT AUTO_INCREMENT,       -- 찜리스트 아이디
                          user_num BIGINT NOT NULL,                        -- 사용자 아이디 (user_num)
                          board_id BIGINT NOT NULL,                        -- 상품 아이디 (board_id)
                          reg_at datetime default now(),   -- 추가된 시간
                          primary key(wish_id));

-- 241212
alter table user add column reg_date datetime default now();

-- 241213
ALTER TABLE user
    MODIFY score DOUBLE DEFAULT 50;
INSERT INTO reasonlist (comp_content) VALUES
                                         ('허위 매물 의심'),
                                         ('가격 사기'),
                                         ('불법/금지 품목 거래'),
                                         ('욕설/비방 및 부적절한 내용'),
                                         ('사진/정보 도용 의심'),
                                         ('거래 불이행'),
                                         ('중복 게시물/스팸'),
                                         ('거래와 무관한 게시글');
alter table reportlist add column user_num bigint;
alter table reportlist add column status enum('pending', 'confirmed', 'canceled') default 'pending';
alter table reportlist add column board_id bigint;

-- 관리자 권한 주기 INSERT INTO auth (user_num, auth) VALUES (50, 'ROLE_ADMIN');


-- 241227
ALTER TABLE notifications
ADD COLUMN move_id bigint;

ALTER TABLE notifications
ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'DEFAULT';

-- 241219
create table event(
                      event_id bigint auto_increment,
                      user_num bigint,
                      type varchar(20),
                      reg_at datetime default now(),
                      primary key(event_id)
);
create table giftcon(
    giftcon_id bigint auto_increment,
    giftcon_name varchar(256),
    user_num bigint,
    reg_at datetime default now(),
    end_date datetime,
    primary key(giftcon_id)
);

ALTER TABLE qna_board
ADD COLUMN answer_count INT DEFAULT 0;
