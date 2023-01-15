CREATE TABLE IF NOT EXISTS Member (
    id VARCHAR(36) PRIMARY KEY ,
    name VARCHAR(200) NOT NULL ,
    address VARCHAR(250) NOT NULL ,
    contact VARCHAR(11) NOT NULL
);

CREATE TABLE IF NOT EXISTS Book (
    isbn VARCHAR(25) PRIMARY KEY ,
    title VARCHAR(250) NOT NULL ,
    author VARCHAR(250) NOT NULL ,
    copies INT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS IssueNote (
    id INT PRIMARY KEY AUTO_INCREMENT ,
    date DATE NOT NULL ,
    member_id VARCHAR(36) NOT NULL ,
    CONSTRAINT FOREIGN KEY (member_id) REFERENCES Member(id)
);

CREATE TABLE IF NOT EXISTS IssueItem (
    issue_id INT NOT NULL ,
    isbn VARCHAR(25) NOT NULL ,
    CONSTRAINT PRIMARY KEY (issue_id, isbn) ,
    CONSTRAINT FOREIGN KEY (issue_id) REFERENCES IssueNote(id) ,
    CONSTRAINT FOREIGN KEY (isbn) REFERENCES Book(isbn)
);

CREATE TABLE IF NOT EXISTS `Return` (
    date DATE NOT NULL ,
    issue_id INT NOT NULL ,
    isbn VARCHAR(25) NOT NULL ,
    CONSTRAINT PRIMARY KEY (issue_id, isbn),
    CONSTRAINT FOREIGN KEY (issue_id, isbn) REFERENCES IssueItem(issue_id,isbn)
);
