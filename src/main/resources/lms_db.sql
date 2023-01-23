CREATE TABLE IF NOT EXISTS Member (
    id VARCHAR(36) PRIMARY KEY ,
    name VARCHAR(200) NOT NULL ,
    address VARCHAR(250) NOT NULL ,
    contact VARCHAR(11) NOT NULL
);

INSERT INTO Member
VALUES ('104ccff3-c584-4782-a582-8a06479b4600','Pubudu Janith','Horana','071-7845123'),
       ('2714641a-301e-43d5-9d31-ad916d075700','Supun Sudeera','Kaluthara','077-8525693'),
       ('2714641a-301e-43d5-9d31-ad916d075800','Kasun Subasinghe','Panadura','072-4512369');

CREATE TABLE IF NOT EXISTS Book (
    isbn VARCHAR(25) PRIMARY KEY ,
    title VARCHAR(250) NOT NULL ,
    author VARCHAR(250) NOT NULL ,
    copies INT NOT NULL DEFAULT 1
);

INSERT INTO Book
VALUES ('978-3-16-148410-0','Patterns of Enterprise Application Architecture','Martin Fowler',1),
       ('978-3-16-148410-1','Application Architecture','Microsoft',2),
       ('978-3-16-148410-2','Clean Code','Robert Martin',3),
       ('978-3-16-148410-3','MongoDB Specification','MongoDB Inc',1),
       ('978-3-16-148410-4','Introduction to Cloud Computing ','Martin Robert',4),
       ('978-3-16-148410-5','ECMAScript Specification 2022','ECMA Body',1),
       ('978-3-16-148410-6','Java Language Specification','James Gosling',2),
       ('978-3-16-148410-7','Effective Java 3','Oracle Team',1);

CREATE TABLE IF NOT EXISTS IssueNote (
    id INT PRIMARY KEY AUTO_INCREMENT ,
    date DATE NOT NULL ,
    member_id VARCHAR(36) NOT NULL ,
    CONSTRAINT FOREIGN KEY (member_id) REFERENCES Member(id)
);

INSERT INTO IssueNote
VALUES (1,'2023-01-10','104ccff3-c584-4782-a582-8a06479b4600'),
       (2,'2023-01-11','2714641a-301e-43d5-9d31-ad916d075700');

CREATE TABLE IF NOT EXISTS IssueItem (
    issue_id INT NOT NULL ,
    isbn VARCHAR(25) NOT NULL ,
    CONSTRAINT PRIMARY KEY (issue_id, isbn) ,
    CONSTRAINT FOREIGN KEY (issue_id) REFERENCES IssueNote(id) ,
    CONSTRAINT FOREIGN KEY (isbn) REFERENCES Book(isbn)
);

INSERT INTO IssueItem
VALUES (1,'978-3-16-148410-0'),
       (1,'978-3-16-148410-1'),
       (2,'978-3-16-148410-2'),
       (2,'978-3-16-148410-3');

CREATE TABLE IF NOT EXISTS `Return` (
    date DATE NOT NULL ,
    issue_id INT NOT NULL ,
    isbn VARCHAR(25) NOT NULL ,
    CONSTRAINT PRIMARY KEY (issue_id, isbn),
    CONSTRAINT FOREIGN KEY (issue_id, isbn) REFERENCES IssueItem(issue_id,isbn)
);

INSERT INTO `Return`
VALUES ('2023-01-14',1,'978-3-16-148410-0'),
       ('2023-01-15',2,'978-3-16-148410-2');

# Available copies of the relevant book
SELECT (B.copies - COUNT(II.isbn) + COUNT(R.isbn)) AS `available_copies`
FROM IssueItem II
LEFT JOIN `Return` R ON II.issue_id = R.issue_id AND II.isbn = R.isbn
RIGHT JOIN Book B ON II.isbn = B.isbn
WHERE B.isbn = '978-3-16-148410-1'
GROUP BY B.isbn;

# Is this book previously issued to this member ?
SELECT II.isbn
FROM IssueItem II
INNER JOIN `Return` R ON NOT (II.issue_id = R.issue_id AND II.isbn = R.isbn)
INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id
INNER JOIN Book B ON II.isbn = B.isbn
WHERE `IN`.member_id = '2714641a-301e-43d5-9d31-ad916d075700' AND B.isbn = '978-3-16-148410-3';

# Available book limit
SELECT M.id, M.name, 3 - COUNT(`IN`.id) as available
FROM Member M
LEFT JOIN IssueNote `IN` ON M.id = `IN`.member_id
LEFT JOIN IssueItem II ON `IN`.id = II.issue_id
LEFT JOIN `Return` R ON II.issue_id = R.issue_id AND II.isbn = R.isbn
WHERE R.date IS NULL AND M.id = '104ccff3-c584-4782-a582-8a06479b4600' GROUP BY M.id;

# Is valid issue item
SELECT *
FROM IssueItem II
INNER JOIN IssueNote `IN` ON II.issue_id = `IN`.id
WHERE `IN`.member_id = '2714641a-301e-43d5-9d31-ad916d075700' AND II.issue_id = 4 AND II.isbn = '978-3-16-148410-3';

