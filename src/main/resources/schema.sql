create table Author
(
    id   int         NOT NULL AUTO_INCREMENT,
    name varchar(20) NOT NULL,
    PRIMARY KEY (id)
);

create table Book
(
    id        int         NOT NULL AUTO_INCREMENT,
    name      varchar(20) NOT NULL,
    price     int         NOT NULL,
    author_id int         NOT NULL,
    PRIMARY KEY (id)
);
