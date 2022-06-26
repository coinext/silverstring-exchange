Welcome to Coinext (Silverstring-Exchange)
===================

https://cryptosherlock.me/
블록체인 지갑 입출금 모니터링 서비스


오픈소스 가상화폐 거래소 Coinext (부제 : Silverstring-Exchange)

* 만든이 : Coinest2017@gmail.com (닉네임 : 나사못도 살포시)

* 언어 : Java 1.8

* 스킬 : spring boot, redis, rabbitmq, mysql, jpa, websocket, jquery, bootstrap, ipfs, clientjs

* 사용 솔류션 : rabbitmq, redis, mysql, google mail

* 목적 : 가상화폐 거래소를 만들고 싶어하고, 어떻게 구동되는지 알고싶은 많은 개발자들에게 기술을 오픈하고 싶어서..

* 개발 중점 : 기준 거래소들의 성능저하의 원인을 극복하고, 클라우드 환경에서의 scale out하게 서버를 확장가능하도록 구현하고, 비동기 방식과 메세지큐를 사용하여 퍼포먼스를 향상함.

* 최적화 : 리팩토링 작업 남음.

* 구동 방법 : rabbitmq, redis, mysql 설치하시고,application.properties쪽에 설정정보 채워서 구동하면됩니다. 아래쪽에 DB스키마를 참고해주세요. 

<br /> 
<br /> 

<br /> 

#### Docker compose를 이용한 구동
* https://docs.docker.com/
* https://docs.docker.com/compose/
```
$ gradle clean buildDocker
$ docker-compose -f silverstring-web/docker/docker-compose.yml up
```

> DB 스키마

```
CREATE TABLE action_log
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    tag VARCHAR(30),
    ip VARCHAR(100),
    user_agent VARCHAR(255),
    reg_dtm DATETIME,
    data VARCHAR(512)
);

CREATE TABLE admin_wallet
(
    coin_name VARCHAR(255) DEFAULT '' NOT NULL,
    type VARCHAR(10) DEFAULT '' NOT NULL,
    address VARCHAR(255),
    tag VARCHAR(100),
    recv_corp_nm VARCHAR(255),
    bank_name VARCHAR(255),
    bank_code VARCHAR(255),
    available_balance DECIMAL(32,8),
    using_balance DECIMAL(32,8),
    reg_dt DATETIME,
    CONSTRAINT `PRIMARY` PRIMARY KEY (coin_name, type)
);

CREATE TABLE bot_connect_setting
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20),
    service VARCHAR(100),
    connect_key VARCHAR(255),
    secret_key VARCHAR(255),
    reg_dtm DATETIME,
    status VARCHAR(20)
);

CREATE TABLE bot_report
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20),
    service VARCHAR(30),
    reg_dtm DATETIME,
    assets DECIMAL(32,8),
    profit DECIMAL(32,8),
    action VARCHAR(50)
);

CREATE TABLE bot_trade_setting
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20)
);

CREATE TABLE chart
(
    dt VARCHAR(255) NOT NULL,
    coin VARCHAR(255) NOT NULL,
    price DECIMAL(24,8),
    adj_close DECIMAL(24,8),
    close DECIMAL(24,8),
    high DECIMAL(24,8),
    low DECIMAL(24,8),
    open DECIMAL(24,8),
    volume DECIMAL(24,8),
    CONSTRAINT `PRIMARY` PRIMARY KEY (dt, coin)
);

CREATE TABLE coin
(
    name VARCHAR(30) PRIMARY KEY NOT NULL,
    han_name VARCHAR(30) NOT NULL,
    mark VARCHAR(4) NOT NULL,
    unit VARCHAR(11),
    display_priority INT(11) NOT NULL,
    rpc_url VARCHAR(255) NOT NULL,
    logo_url VARCHAR(255),
    reg_dtm DATETIME NOT NULL,
    active VARCHAR(1) NOT NULL,
    withdrawal_min_amount DECIMAL(32,8) NOT NULL,
    withdrawal_auto_allow_max_amount DECIMAL(32,8) NOT NULL,
    withdrawal_fee_amount DECIMAL(32,8) NOT NULL,
    auto_collect_min_amount DECIMAL(32,8) NOT NULL,
    trading_fee_percent DECIMAL(32,2) NOT NULL,
    trading_min_amount DECIMAL(32,8),
    margin_trading_fee_percent DECIMAL(32,8) NOT NULL,
    deposit_scan_page_offset INT(11),
    deposit_scan_page_size INT(11),
    deposit_allow_confirmation BIGINT(20)
);

CREATE TABLE coupon
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(255),
    content VARCHAR(255),
    img_url VARCHAR(255),
    reg_dtm DATETIME,
    del_dtm DATETIME,
    under_krw BIGINT(20),
    expire_hr INT(11)
);

CREATE TABLE email_confirm
(
    hash_email VARCHAR(120) NOT NULL,
    code VARCHAR(20) NOT NULL,
    email VARCHAR(30) NOT NULL,
    send_yn VARCHAR(1) NOT NULL,
    reg_dtm DATETIME NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (hash_email, code)
);
CREATE UNIQUE INDEX email_confirm_email_pk ON email_confirm (email);
CREATE UNIQUE INDEX email_confirm_hash_email_pk ON email_confirm (hash_email);

CREATE TABLE finger_print
(
    user_id INT(11) NOT NULL,
    hash_key VARCHAR(255) NOT NULL,
    reg_dtm DATETIME NOT NULL,
    del_dtm DATETIME,
    active VARCHAR(1) NOT NULL,
    CONSTRAINT `PRIMARY` PRIMARY KEY (user_id, hash_key)
);
CREATE UNIQUE INDEX finger_print_hash_key_uindex ON finger_print (hash_key);

CREATE TABLE ico_recommend
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    content VARCHAR(255),
    url VARCHAR(255),
    email VARCHAR(255),
    img_url VARCHAR(255)
);

CREATE TABLE level
(
    coin_name VARCHAR(8) DEFAULT '' NOT NULL,
    level VARCHAR(11) DEFAULT '' NOT NULL,
    once_amount DECIMAL(32,8),
    oneday_amount DECIMAL(32,8),
    CONSTRAINT `PRIMARY` PRIMARY KEY (coin_name, level)
);

CREATE TABLE manual_transaction
(
    id VARCHAR(150) DEFAULT '' NOT NULL,
    user_id INT(11) DEFAULT '0' NOT NULL,
    coin_name VARCHAR(30),
    category VARCHAR(10),
    address VARCHAR(255),
    tag VARCHAR(255),
    bank_nm VARCHAR(60),
    recv_nm VARCHAR(30),
    deposit_dvcd VARCHAR(10),
    amount DECIMAL(32,8),
    reg_dt DATETIME,
    status VARCHAR(30),
    reason VARCHAR(255),
    CONSTRAINT `PRIMARY` PRIMARY KEY (id, user_id)
);

CREATE TABLE market_history_order
(
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    order_id BIGINT(20) NOT NULL,
    amount DECIMAL(32,8),
    completed_dtm DATETIME,
    dt VARCHAR(255),
    order_type VARCHAR(255),
    price DECIMAL(32,8),
    reg_dtm DATETIME,
    status VARCHAR(255),
    from_coin_name VARCHAR(255),
    to_coin_name VARCHAR(255),
    CONSTRAINT `PRIMARY` PRIMARY KEY (id, user_id, order_id),
    CONSTRAINT FKficco4yldft62qi141fdk133r FOREIGN KEY (from_coin_name) REFERENCES coin (name),
    CONSTRAINT FKjkgrji3bs5tw60bvvqf3cufwh FOREIGN KEY (to_coin_name) REFERENCES coin (name)
);
CREATE INDEX FKficco4yldft62qi141fdk133r ON market_history_order (from_coin_name);
CREATE INDEX FKjkgrji3bs5tw60bvvqf3cufwh ON market_history_order (to_coin_name);

CREATE TABLE my_coupon
(
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) DEFAULT '0' NOT NULL,
    coupon_id BIGINT(20) DEFAULT '0' NOT NULL,
    reg_dtm DATETIME,
    used_begin_dtm DATETIME,
    expire_dtm DATETIME,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id, user_id),
    CONSTRAINT my_coupon_coupon_id_fk FOREIGN KEY (coupon_id) REFERENCES coupon (id)
);
CREATE INDEX my_coupon_coupon_id_fk ON my_coupon (coupon_id);

CREATE TABLE my_history_order
(
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    order_id BIGINT(20) NOT NULL,
    amount DECIMAL(32,8),
    completed_dtm DATETIME,
    dt VARCHAR(255),
    order_type VARCHAR(255),
    price DECIMAL(32,8),
    reg_dtm DATETIME,
    status VARCHAR(255),
    from_coin_name VARCHAR(255),
    to_coin_name VARCHAR(255),
    to_user_id BIGINT(20),
    to_order_id BIGINT(20),
    CONSTRAINT `PRIMARY` PRIMARY KEY (id, user_id, order_id),
    CONSTRAINT FKripxngjayhjot1c8not7v19a6 FOREIGN KEY (from_coin_name) REFERENCES coin (name),
    CONSTRAINT FK4twra4ndo9ef2l78wk779yj5s FOREIGN KEY (to_coin_name) REFERENCES coin (name)
);
CREATE INDEX FK4twra4ndo9ef2l78wk779yj5s ON my_history_order (to_coin_name);
CREATE INDEX FKripxngjayhjot1c8not7v19a6 ON my_history_order (from_coin_name);
CREATE UNIQUE INDEX my_history_orders_id_uindex ON my_history_order (id);

CREATE TABLE news
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    reg_dtm DATETIME NOT NULL,
    del_dtm DATETIME
);

CREATE TABLE notice
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content BLOB NOT NULL,
    url VARCHAR(255) NOT NULL,
    reg_dtm DATETIME NOT NULL,
    del_dtm DATETIME
);

CREATE TABLE `order`
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) NOT NULL,
    amount DECIMAL(32,8),
    amount_remaining DECIMAL(32,8),
    completed_dtm DATETIME,
    order_type VARCHAR(255),
    price DECIMAL(32,8),
    reg_dtm DATETIME,
    status VARCHAR(255),
    from_coin_name VARCHAR(255),
    to_coin_name VARCHAR(255),
    cancel_dtm DATETIME
);
CREATE INDEX FKj21c1p46tcvjbpaymef4xbjfe ON `order` (to_coin_name);
CREATE INDEX FKjao1n98lkxadyhny5dxf71qco ON `order` (from_coin_name);
CREATE INDEX orders_fk ON `order` (user_id);

CREATE TABLE submit_document
(
    user_id BIGINT(20) PRIMARY KEY NOT NULL,
    level_idcard_url_hash VARCHAR(255),
    level_doc_url_hash VARCHAR(255),
    reg_dtm DATETIME,
    complete_dtm DATETIME,
    status VARCHAR(30),
    reason VARCHAR(255)
);

CREATE TABLE support
(
    id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    parent_id BIGINT(20),
    user_id BIGINT(20),
    type VARCHAR(10),
    title VARCHAR(255),
    content VARCHAR(255),
    reg_dtm DATETIME,
    status VARCHAR(10),
    reason VARCHAR(255)
);

CREATE TABLE transaction
(
    id VARCHAR(150) DEFAULT '' NOT NULL,
    user_id INT(11) DEFAULT '0' NOT NULL,
    coin_name VARCHAR(30),
    category VARCHAR(10),
    tx_id VARCHAR(150),
    address VARCHAR(255),
    tag VARCHAR(255),
    bank_nm VARCHAR(60),
    recv_nm VARCHAR(30),
    amount DECIMAL(32,8),
    reg_dt DATETIME,
    complete_dtm DATETIME,
    confirmation INT(11),
    status VARCHAR(30),
    reason VARCHAR(255),
    CONSTRAINT `PRIMARY` PRIMARY KEY (id, user_id)
);
CREATE UNIQUE INDEX transaction_tx_id_coin_name_pk ON transaction (tx_id, coin_name);

CREATE TABLE user
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    email VARCHAR(60) NOT NULL,
    pwd VARCHAR(120) NOT NULL,
    level VARCHAR(11) NOT NULL,
    otp_hash VARCHAR(120) NOT NULL,
    active VARCHAR(1) NOT NULL,
    role VARCHAR(10),
    reg_dtm DATETIME NOT NULL,
    del_dtm DATETIME
);
CREATE UNIQUE INDEX user_email_uindex ON user (email);

CREATE TABLE virtual_account
(
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id INT(11),
    account VARCHAR(255) NOT NULL,
    bank_name VARCHAR(255),
    bank_code VARCHAR(255),
    recv_corp_nm VARCHAR(255),
    reg_dtm DATETIME,
    alloc_dtm DATETIME
);
CREATE UNIQUE INDEX virtual_account_account_pk ON virtual_account (account);
CREATE UNIQUE INDEX virtual_account_alloc_dtm_pk ON virtual_account (alloc_dtm);

CREATE TABLE wallet
(
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    user_id BIGINT(20) DEFAULT '0' NOT NULL,
    coin_name VARCHAR(255),
    address VARCHAR(255),
    bank_code VARCHAR(255),
    bank_name VARCHAR(255),
    recv_corp_nm VARCHAR(255),
    tag VARCHAR(100),
    deposit_dvcd VARCHAR(10),
    using_balance DECIMAL(32,8),
    available_balance DECIMAL(32,8),
    today_withdrawal_total_balance DECIMAL(32,8),
    reg_dt DATETIME,
    CONSTRAINT `PRIMARY` PRIMARY KEY (id, user_id),
    CONSTRAINT wallet_coin_name_fk FOREIGN KEY (coin_name) REFERENCES coin (name)
);
CREATE INDEX wallet_coin_name_fk ON wallet (coin_name);

```



