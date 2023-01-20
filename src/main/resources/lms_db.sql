CREATE TABLE IF NOT EXISTS Member (
    id VARCHAR(36) PRIMARY KEY ,
    name VARCHAR(200) NOT NULL ,
    address VARCHAR(250) NOT NULL ,
    contact VARCHAR(11) NOT NULL
);

INSERT INTO Member
VALUES ('104ccff3-c584-4782-a582-8a06479b46f6','Nuwan Ramindu','Galle','078-1234567'),
       ('2714641a-301e-43d5-9d31-ad916d075ba6','Kasun Sampath','Galle','077-1234567'),
       ('2714641a-301e-43d5-9d31-ad916d075ba7','Tharindu','Panadura','011-1234567');

CREATE TABLE IF NOT EXISTS Book (
    isbn VARCHAR(25) PRIMARY KEY ,
    title VARCHAR(250) NOT NULL ,
    author VARCHAR(250) NOT NULL ,
    copies INT NOT NULL DEFAULT 1
);

INSERT INTO Book
VALUES ('1234-1234','Patterns of Enterprise Application Architecture','Martin Fowler',2),
       ('1234-4567','Application Architecture','Microsoft',3),
       ('1234-7891','Clean Code','Robert Cecil Martin',4),
       ('1234-9874','Test','Test',1),
       ('4567-1234','UML Distilled','Martin Fowler',1),
       ('4567-4567','SQL Specification 2011','Ansi',1),
       ('4567-7891','ECMAScript Specification 2022','ECMA Body',1),
       ('7891-1234','Java Language Specification','James Gosling',1),
       ('9874-1234','Effective Java 3','Prasad Sir',1);

CREATE TABLE IF NOT EXISTS IssueNote (
    id INT PRIMARY KEY AUTO_INCREMENT ,
    date DATE NOT NULL ,
    member_id VARCHAR(36) NOT NULL ,
    CONSTRAINT FOREIGN KEY (member_id) REFERENCES Member(id)
);

INSERT INTO IssueNote
VALUES (1,'2022-11-14','2714641a-301e-43d5-9d31-ad916d075ba7'),
       (3,'2022-11-14','2714641a-301e-43d5-9d31-ad916d075ba7'),
       (8,'2022-11-15','2714641a-301e-43d5-9d31-ad916d075ba7'),
       (9,'2022-11-15','104ccff3-c584-4782-a582-8a06479b46f6'),
       (10,'2022-11-15','104ccff3-c584-4782-a582-8a06479b46f6');

CREATE TABLE IF NOT EXISTS IssueItem (
    issue_id INT NOT NULL ,
    isbn VARCHAR(25) NOT NULL ,
    CONSTRAINT PRIMARY KEY (issue_id, isbn) ,
    CONSTRAINT FOREIGN KEY (issue_id) REFERENCES IssueNote(id) ,
    CONSTRAINT FOREIGN KEY (isbn) REFERENCES Book(isbn)
);

INSERT INTO IssueItem
VALUES (3,'1234-1234'),
       (8,'1234-4567'),
       (9,'1234-4567'),
       (1,'1234-7891'),
       (3,'1234-7891'),
       (10,'1234-7891'),
       (8,'1234-9874'),
       (1,'4567-4567'),
       (3,'7891-1234');

CREATE TABLE IF NOT EXISTS `Return` (
    date DATE NOT NULL ,
    issue_id INT NOT NULL ,
    isbn VARCHAR(25) NOT NULL ,
    CONSTRAINT PRIMARY KEY (issue_id, isbn),
    CONSTRAINT FOREIGN KEY (issue_id, isbn) REFERENCES IssueItem(issue_id,isbn)
);

INSERT INTO `Return`
VALUES ('2022-11-14',1,'4567-4567'),
       ('2022-11-15',10,'1234-7891');

# Available copies of the relevant book
SELECT (B.copies - COUNT(II.isbn) + COUNT(R.isbn)) AS `available_copies`
FROM IssueItem II
LEFT JOIN `Return` R ON II.issue_id = R.issue_id AND II.isbn = R.isbn
RIGHT JOIN Book B ON II.isbn = B.isbn
WHERE B.isbn = '1234-1234'
GROUP BY B.isbn;

# Is this book previously issued to this member ?
SELECT II.isbn
FROM IssueItem II
INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id AND II.isbn = R.isbn)
INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id
INNER JOIN Book B ON II.isbn = B.isbn
WHERE `IN`.member_id = '104ccff3-c584-4782-a582-8a06479b46f6' AND B.isbn = '1234-1234';

# Available book limit
SELECT M.name, 3 - COUNT(R.issue_id) AS available
FROM IssueNote `IN`
INNER JOIN IssueItem II ON `IN`.id = II.issue_id
INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id and II.isbn = R.isbn)
RIGHT JOIN Member M ON `IN`.member_id = M.id
WHERE M.id = '2714641a-301e-43d5-9d31-ad916d075ba6' GROUP BY M.id;

# Is valid issue item
SELECT *
FROM IssueItem II
INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id
WHERE `IN`.member_id = ? AND II.issue_id = ? AND II.isbn = ?;

