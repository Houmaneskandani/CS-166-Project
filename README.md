 # CS-166-Project 
## Description:
 > In this project, we create model and build an airline management system. We use this
system to track information about different airlines, the planes they own, the maintenance of
those planes, the pilots they employ and the trips that the pilots make, as well as information
about the customers that use the airline services.



## Requirement Analysis

### Airline Management:
 > 1. Given a flight number, get the flight’s schedule for the week
	(A flight may be scheduled on multiple days in a week)


 > 2. Given a flight and a date, get (1) the number of seats still available and (2) number of
seats sold


 > 3. Given a flight and date, find whether (1) the flight departed on time, and (2) arrived on
time


 > 4. Given a date, get all flights scheduled on that day


 > 5. Given a flight and date, get a list of passengers who (1) made reservations, (2) are on the
waiting list, (3) actually flew on the flight (for flights already completed)


 > 6. Given a reservation number, retrieve information about the travelers under that number
	( First & Last Name, Gender, Date of birth, Address, Phone number, Zip Code)


 > 7. Given a plane number, get its make, model, age, and last repair date


 > 8. Given a maintenance technician ID, list all repairs made by that person


 > 9. Given a plane ID and a date range, list all the dates and the codes for repairs performed

### Customers:

 > 1. Given a destination and departure city, find all flights on a given date.
	( Must return departure and arrival time, number of stops scheduled, and on-time
record as a percentage)


 > 2. Given a flight number, find the ticket cost.


 > 3. Given a flight number, find the airplane type (make and model)


 > 4. Make a reservation for a flight
	( Get on the waitlist for a flight if the flight is full)

### Maintenance Staff:

 > 1. Given a plane ID and a date range, list all the dates and the codes for repairs performed


 > 2. Given a pilot ID, list all maintenance requests made by the pilot


 > 3. After each repair, make an entry showing plane ID, repair code, and date of repair

### Pilots:

 > 1. Make maintenance request listing plane ID, repair code requested, and date of request



# Phase 1: ER Design

**Our Task :**

● Design a logical model of our database using the ER-model with provided requirement.

<img src="Phase1/IMAGE/image.png " width ="900" >



#### Assumptions:
 > - A customer can reserve, be waitlisted, and have flown in many flights

 > - Gender attribute of a customer can be stored as a string

 > - A maintenance technician can repair many airplanes and an airplane can be
  repaired by many maintenance technicians

 > - An airplane can only be controlled by one pilot and a pilot can only control one
  airplane

 > - We assume each customer has a unique identification as ​ssn

 > - We assume each plane has two unique attributes which are the plane number
  and plane id.

 > - We assume each customer who gets into the waiting list for a flight will be given
  a unique waiting number.

 > - We assume that each flight’s cost could be changed on different days or special
  days.

 > - We assume each flight can have many tickets (economy-class, first-class, etc)



# Phase 2: Relational Schema Design

**Our Task :**

● Translate the provided ER design to a PostgreSQL relational database schema.


<img src="Phase2/image3.png " width ="600" >


#### Assumptions:

 > - The ID field of Customers is a string of 11 characters
 > - The ID field of the Pilot entity is its primary key
 > - The ID field of the Plane entity is its primary key
 > - The ID field of the Technician entity is its primary key
 > - We assume that the attribute ID for the following entities is unique:

     -  Plane (Plane_ID) 
     -  Technician(Tech_ID)
     -  Pilot(Pilot_ID)
     -  Flight(flight_num)
     -  Customer(Customer_ID)

 > - Changed the name of the ID attribute of repair_request entity to repair_request_ID
 > - In the repairs entity, we assume that the type of the ‘code’ attribute is CHAR(40) in the repairs entity.
 > - For entities, Waitlisted, Confirmed, Reserved we added boolean attribute W_status, C_status, Re_status which shows the status of each customer who is going to reserve a  ticket.
 > - We store all number and ID fields(i.e. flight_num, Rnum, Plane ID, etc) as CHAR(11). We assume these fields will not be used for any mathematical operation; therefore, there is no point in storing them as integers. If needed, these fields could store more characters, but we assume eleven is enough.




# Phase 3: Implementation/Requirement
```
• Add Plane: Ask the user for details of a plane and add it to the DB
```
```
• Add Pilot: Ask the user for details of a pilot and add it to the DB
```
```
• Add Flight: Ask the user for details of a flight and add it to the DB
```
```
• Add Technician: Ask user for details of a technician and add it to the DB
```
```
• Book Flight: Given a customer and flight that he/she wants to book, determine the status
of the reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database
with appropriate status.
```
```
• List number of available seats for a given flight: Given a flight number and a departure date,
find the number of available seats in the flight.
```
```
• List total number of repairs per plane in descending order: Return the list of planes in de-
creasing order of number of repairs that have been made on the planes.
```
```
• List total number of repairs per year in ascending order: Return the years with the number of
repairs made in those years in ascending order of number of repairs per year.
```
```
• Find total number of passengers with a given status: For a given flight and passenger status,
return the number of passengers with the given status.
```
