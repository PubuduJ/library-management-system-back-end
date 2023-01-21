DROP TABLE IF EXISTS `Member`;
CREATE TABLE `Member` (
                          `id` varchar(36) NOT NULL,
                          `name` varchar(200) NOT NULL,
                          `address` varchar(250) NOT NULL,
                          `contact` varchar(11) NOT NULL,
                          PRIMARY KEY (`id`)
);

INSERT INTO `Member`
VALUES ('104ccff3-c584-4782-a582-8a06479b4600','Pubudu Janith','Horana','071-7845123'),
       ('2714641a-301e-43d5-9d31-ad916d075700','Supun Sudeera','Kaluthara','077-8525693'),
       ('2714641a-301e-43d5-9d31-ad916d075800','Kasun Subasinghe','Panadura','072-4512369');

DROP TABLE IF EXISTS `Book`;
CREATE TABLE `Book` (
                        `isbn` varchar(25) NOT NULL,
                        `title` varchar(250) NOT NULL,
                        `author` varchar(250) NOT NULL,
                        `copies` int NOT NULL DEFAULT '1',
                        PRIMARY KEY (`isbn`)
);

INSERT INTO `Book`
VALUES ('978-3-16-148410-0','Patterns of Enterprise Application Architecture','Martin Fowler',1),
       ('978-3-16-148410-1','Application Architecture','Microsoft',2),
       ('978-3-16-148410-2','Clean Code','Robert Martin',3),
       ('978-3-16-148410-3','MongoDB Specification','MongoDB Inc',1),
       ('978-3-16-148410-4','Introduction to Cloud Computing ','Martin Robert',4),
       ('978-3-16-148410-5','ECMAScript Specification 2022','ECMA Body',1),
       ('978-3-16-148410-6','Java Language Specification','James Gosling',2),
       ('978-3-16-148410-7','Effective Java 3','Oracle Team',1);

DROP TABLE IF EXISTS `IssueNote`;
CREATE TABLE `IssueNote` (
                             `id` int NOT NULL AUTO_INCREMENT,
                             `date` date NOT NULL,
                             `member_id` varchar(36) NOT NULL,
                             PRIMARY KEY (`id`),
                             CONSTRAINT `IssueNote_ibfk_1` FOREIGN KEY (`member_id`) REFERENCES `Member` (`id`)
);

INSERT INTO `IssueNote`
VALUES (3,'2023-01-10','104ccff3-c584-4782-a582-8a06479b4600'),
       (4,'2023-01-11','2714641a-301e-43d5-9d31-ad916d075700');

DROP TABLE IF EXISTS `IssueItem`;
CREATE TABLE `IssueItem` (
                             `issue_id` int NOT NULL,
                             `isbn` varchar(25) NOT NULL,
                             PRIMARY KEY (`issue_id`,`isbn`),
                             CONSTRAINT `IssueItem_ibfk_1` FOREIGN KEY (`issue_id`) REFERENCES `IssueNote` (`id`),
                             CONSTRAINT `IssueItem_ibfk_2` FOREIGN KEY (`isbn`) REFERENCES `Book` (`isbn`)
);

INSERT INTO `IssueItem`
VALUES (3,'978-3-16-148410-0'),
       (3,'978-3-16-148410-1'),
       (4,'978-3-16-148410-2'),
       (4,'978-3-16-148410-3');

DROP TABLE IF EXISTS `Return`;
CREATE TABLE `Return` (
                          `date` date NOT NULL,
                          `issue_id` int NOT NULL,
                          `isbn` varchar(25) NOT NULL,
                          PRIMARY KEY (`issue_id`,`isbn`),
                          CONSTRAINT `Return_ibfk_1` FOREIGN KEY (`issue_id`, `isbn`) REFERENCES `IssueItem` (`issue_id`, `isbn`)
);

INSERT INTO `Return`
VALUES ('2023-01-14',3,'978-3-16-148410-0'),
       ('2023-01-15',4,'978-3-16-148410-2');