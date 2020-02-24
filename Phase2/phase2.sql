-- Add tables to drop in this statement (As a comma separated list)
DROP TABLE IF EXISTS Customer, Pilot, Plane, Technician, Flight, Reservation, Waitlisted, Reserved, Confirmed;

CREATE TABLE Customer(
    first_name CHAR(20) NOT NULL,
    last_name CHAR(20) NOT NULL,
    gender CHAR(15),
    date_of_birth DATE NOT NULL,
    address CHAR(40),
    contact_num INTEGER,
    ID CHAR(11),
    ZIP_CODE INTEGER,
    PRIMARY KEY(ID)
);

CREATE TABLE Pilot (
    name CHAR(40) NOT NULL,
    ID CHAR(11) NOT NULL,
    nationality CHAR(20) NOT NULL,
    PRIMARY KEY(ID)
);

CREATE TABLE Plane(
    model CHAR(40) NOT NULL,
    ID CHAR(11) NOT NULL,
    make CHAR(40)NOT NULL,
    age INTEGER,
    num_seats INTEGER,
    PRIMARY KEY(ID)
);

CREATE TABLE Technician(
    ID CHAR(11) NOT NULL,
    PRIMARY KEY(ID)
);

-- TO DO: CHECK HOW TO STORE TIME FIELDS IN POSTGRES SQL
CREATE TABLE Flight (
    flight_num INTEGER NOT NULL,
    cost DECIMAL,
    num_sold INTEGER,
    num_stops INTEGER,
    actual_arrive_date DATE,
    actual_arrive_time TIME,         
    actual_depart_date DATE,
    actual_depart_time TIME, 
    source CHAR(40),
    destination CHAR(40),
    PRIMARY KEY (flight_num)
);

CREATE TABLE Reservation (
    Rnum INTEGER NOT NULL,
    PRIMARY KEY(Rnum)
);

CREATE TABLE Waitlisted (
    W_num INTEGER NOT NULL,
    FOREIGN KEY (Wnum) REFERENCES Reservation(Rnum)
);

CREATE TABLE Confirmed (
    C_num INTEGER NOT NULL,
    FOREIGN KEY (Cnum) REFERENCES Reservation(Rnum)
);

CREATE TABLE Reserved(
    Re_num INTEGER NOT NULL,
    FOREIGN KEY (Re_num) REFERENCES Reservation(Rnum)
);

CREATE TABLE Schedule (
    day Date NOT NULL,
    depart_time TIME,
    arrive_time TIME,
    flight_num INTEGER NOT NULL,
    FOREIGN KEY (flight_num) REFERENCES Flight(flight_num),
    PRIMARY KEY (flight_num)
);