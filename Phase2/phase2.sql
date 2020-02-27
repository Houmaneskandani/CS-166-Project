DROP TABLE IF EXISTS Reserved, Confirmed, Waitlisted, has, Reservation, Customer, Schedule, Flight, repair_request, Pilot, repairs, Technician, Plane;



CREATE TABLE Plane (
    Plane_ID CHAR(11) NOT NULL,
    model CHAR(40) NOT NULL,
    make CHAR(40)  NOT NULL,
    age INTEGER,
    num_seats INTEGER,
    PRIMARY KEY(Plane_ID)
);


CREATE TABLE Technician (
    Tech_ID CHAR(11) NOT NULL,
    PRIMARY KEY(Tech_ID)
);


CREATE TABLE repairs (
    Tech_ID CHAR(11) NOT NULL,
    Plane_ID CHAR(11) NOT NULL,
    date  DATE,
    code  CHAR(40)  NOT NULL,
    PRIMARY KEY(Tech_ID,Plane_ID),
    FOREIGN KEY(Tech_ID)          REFERENCES Technician(Tech_ID),
    FOREIGN KEY(Plane_ID)         REFERENCES Plane(Plane_ID)
);


CREATE TABLE Pilot (
    Pilot_ID CHAR(11) NOT NULL,
    name CHAR(40) NOT NULL,
    nationality CHAR(20),
    PRIMARY KEY(Pilot_ID)
);


CREATE TABLE repair_request (
    Pilot_ID CHAR(11) NOT NULL,
    Tech_ID CHAR(11) NOT NULL,
    Plane_ID CHAR(11) NOT NULL,
    repair_request_ID  CHAR(11) NOT NULL,     
    PRIMARY KEY(Pilot_ID, Tech_ID, Plane_ID),
    FOREIGN KEY(Tech_ID)          REFERENCES Technician(Tech_ID),
    FOREIGN KEY(Plane_ID)         REFERENCES Plane(Plane_ID),
    FOREIGN KEY(Pilot_ID)         REFERENCES Pilot(Pilot_ID)
);



CREATE TABLE Flight (
    flight_num CHAR(11) NOT NULL,
    Plane_ID CHAR(11) NOT NULL,
    Pilot_ID CHAR(11) NOT NULL,
    cost DECIMAL,
    num_sold INTEGER,
    num_stops INTEGER,
    actual_arrive_date DATE,
    actual_arrive_time TIME,         
    actual_depart_date DATE,
    actual_depart_time TIME, 
    source CHAR(40),
    destination CHAR(40),
    PRIMARY KEY (flight_num),
    FOREIGN KEY(Pilot_ID)         REFERENCES Pilot(Pilot_ID),
    FOREIGN KEY(Plane_ID)         REFERENCES Pilot(Plane_ID)
);


CREATE TABLE Schedule (
    flight_num CHAR(11) NOT NULL,
    day CHAR(11),        
    depart_time TIME,
    arrive_time TIME,
    PRIMARY KEY (flight_num),     
    FOREIGN KEY (flight_num) REFERENCES Flight(flight_num)
);



CREATE TABLE Customer (
    Customer_ID CHAR(11) NOT NULL,
    first_name CHAR(20)  NOT NULL,
    last_name CHAR(20),
    gender CHAR(15),
    date_of_birth DATE   NOT NULL,
    address CHAR(40),
    contact_num CHAR(11),
    ZIP_CODE CHAR(5),
    PRIMARY KEY(Customer_ID)
);


CREATE TABLE Reservation (
    Rnum CHAR(11) NOT NULL,
    PRIMARY KEY(Rnum)
);


CREATE TABLE has (
    flight_num CHAR(11) NOT NULL, 
    Rnum CHAR(11) NOT NULL,
    Customer_ID CHAR(11) NOT NULL,
    PRIMARY KEY(flight_num, Rnum, Customer_ID),  
    FOREIGN KEY(flight_num)       REFERENCES Flight(flight_num),
    FOREIGN KEY(Rnum)             REFERENCES Reservation(Rnum),
    FOREIGN KEY(Customer_ID)      REFERENCES Customer(Customer_ID)
);


CREATE TABLE Waitlisted (
    Rnum CHAR(11) NOT NULL,
    W_status BOOLEAN,
    PRIMARY KEY(Rnum),
    FOREIGN KEY(Rnum)      REFERENCES Reservation(Rnum)
);


CREATE TABLE Confirmed (
    Rnum CHAR(11) NOT NULL,
    C_status BOOLEAN,
    PRIMARY KEY(Rnum),
    FOREIGN KEY(Rnum)      REFERENCES Reservation(Rnum)
);


CREATE TABLE Reserved(
    Rnum CHAR(11) NOT NULL,
    Re_status BOOLEAN,
    PRIMARY KEY(Rnum),
    FOREIGN KEY(Rnum)      REFERENCES Reservation(Rnum)
);




-- TO DO: CHECK HOW TO STORE TIME FIELDS IN POSTGRES 
