DROP DATABASE IF EXISTS meetingDB;
CREATE DATABASE meetingDB;

USE meetingDB;

CREATE TABLE room (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100)
);

CREATE TABLE meeting (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    time DATETIME NOT NULL,
    roomID INT NOT NULL,
    FOREIGN KEY (roomID) REFERENCES room(id)
);

CREATE TABLE employee (
	id INT PRIMARY KEY AUTO_INCREMENT,
    firstName VARCHAR(50) NOT NULL,
    lastName VARCHAR(50) NOT NULL
);

CREATE TABLE meeting_employee (
	meetingID INT NOT NULL,
    employeeID INT NOT NULL,
    FOREIGN KEY (meetingID) REFERENCES meeting(id),
    FOREIGN KEY (employeeID) REFERENCES employee(id)
);

INSERT INTO room(id, name, description) VALUES(1, "North Room", "Large conference room");
INSERT INTO room(id, name, description) VALUES(2, "South Room", "Medium conference room");
INSERT INTO room(id, name, description) VALUES(3, "West Room", "Small conference room");

INSERT INTO meeting(id, name, time, roomID) VALUES(1, "All Team Meeting", '2018-01-01 14:00:00', 1);
INSERT INTO meeting(id, name, time, roomID) VALUES(2, "Lunch and Learn", '2018-01-02 12:00:00', 1);
INSERT INTO meeting(id, name, time, roomID) VALUES(3, "Birthday", '2018-01-03 10:00:00', 2);

INSERT INTO employee(id, firstName, lastName) VALUES(1, "Bob", "Johnson");
INSERT INTO employee(id, firstName, lastName) VALUES(2, "John", "Smith");
INSERT INTO employee(id, firstName, lastName) VALUES(3, "Karen", "Jones");
INSERT INTO employee(id, firstName, lastName) VALUES(4, "Connie", "Samson");

INSERT INTO meeting_employee(meetingID, employeeID) VALUES(1, 1);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(1, 2);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(1, 3);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(1, 4);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(2, 3);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(2, 4);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(3, 1);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(3, 2);
INSERT INTO meeting_employee(meetingID, employeeID) VALUES(3, 4);

SELECT meeting.id, room.name, employee.firstName, employee.lastName  FROM meeting
JOIN meeting_employee ON meeting.id = meeting_employee.meetingID
JOIN employee ON employee.id = meeting_employee.employeeID
JOIN room ON room.id = meeting.roomID;