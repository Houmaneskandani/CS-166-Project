/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.util.Scanner;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random; 


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject{
	//reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	
	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try{
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println ("Connection URL: " + url + "\n");
			
			// obtain a physical connection
	        this._connection = DriverManager.getConnection(url, user, passwd);
	        System.out.println("Done");
		}catch(Exception e){
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
	        System.out.println("Make sure you started postgres on this machine");
	        System.exit(-1);
		}
	}
	
	/**
	 * Method to execute an update SQL statement.  Update SQL instructions
	 * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 * */
	public void executeUpdate (String sql) throws SQLException { 
		// creates a statement object
		Statement stmt = this._connection.createStatement ();

		// issues the update instruction
		stmt.executeUpdate (sql);

		// close the instruction
	    stmt.close ();
	}//end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and outputs the results to
	 * standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		/*
		 *  obtains the metadata object for the returned result set.  The metadata
		 *  contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData ();
		int numCol = rsmd.getColumnCount ();
		int rowCount = 0;
		
		//iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()){
			if(outputHeader){
				for(int i = 1; i <= numCol; i++){
					System.out.print(rsmd.getColumnName(i) + "\t");
			    }
			    System.out.println();
			    outputHeader = false;
			}
			for (int i=1; i<=numCol; ++i)
				System.out.print (rs.getString (i) + "\t");
			System.out.println ();
			++rowCount;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the results as
	 * a list of records. Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException { 
		//creates a statement object 
		Statement stmt = this._connection.createStatement (); 
		
		//issues the query instruction 
		ResultSet rs = stmt.executeQuery (query); 
	 
		/*
		 * obtains the metadata object for the returned result set.  The metadata 
		 * contains row and column info. 
		*/ 
		ResultSetMetaData rsmd = rs.getMetaData (); 
		int numCol = rsmd.getColumnCount (); 
		int rowCount = 0; 
	 
		//iterates through the result set and saves the data returned by the query. 
		boolean outputHeader = false;
		List<List<String>> result  = new ArrayList<List<String>>(); 
		while (rs.next()){
			List<String> record = new ArrayList<String>(); 
			for (int i=1; i<=numCol; ++i) 
				record.add(rs.getString (i)); 
			result.add(record); 
		}//end while 
		stmt.close (); 
		return result; 
	}//end executeQueryAndReturnResult
	
	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT).  This
	 * method issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery (String query) throws SQLException {
		//creates a statement object
		Statement stmt = this._connection.createStatement ();

		//issues the query instruction
		ResultSet rs = stmt.executeQuery (query);

		int rowCount = 0;

		//iterates through the result set and count nuber of results.
		if(rs.next()){
			rowCount++;
		}//end while
		stmt.close ();
		return rowCount;
	}
	
	/**
	 * Method to fetch the last value from sequence. This
	 * method issues the query to the DBMS and returns the current 
	 * value of sequence used for autogenerated keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	
	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement ();
		
		ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
		if (rs.next()) return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup(){
		try{
			if (this._connection != null){
				this._connection.close ();
			}//end if
		}catch (SQLException e){
	         // ignored.
		}//end try
	}//end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
	 */
	public static void main (String[] args) {
		if (args.length != 3) {
			System.err.println (
				"Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName () +
		            " <dbname> <port> <user>");
			return;
		}//end if
		
		DBproject esql = null;
		
		try{
			System.out.println("(1)");
			
			try {
				Class.forName("org.postgresql.Driver");
			}catch(Exception e){

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}
			
			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];
			
			esql = new DBproject (dbname, dbport, user, "");
			
			boolean keepon = true;
			while(keepon){
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");
				
				switch (readChoice()){
					case 1: AddPlane(esql); break;
					case 2: AddPilot(esql); break;
					case 3: AddFlight(esql); break;
					case 4: AddTechnician(esql); break;
					case 5: BookFlight(esql); break;
					case 6: ListNumberOfAvailableSeats(esql); break;
					case 7: ListsTotalNumberOfRepairsPerPlane(esql); break;
					case 8: ListTotalNumberOfRepairsPerYear(esql); break;
					case 9: FindPassengersCountWithStatus(esql); break;
					case 10: keepon = false; break;
				}
			}
		}catch(Exception e){
			System.err.println (e.getMessage ());
		}finally{
			try{
				if(esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup ();
					System.out.println("Done\n\nBye !");
				}//end if				
			}catch(Exception e){
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}//end try
		}while (true);
		return input;
	}//end readChoice

//=========================================================================================================================================================================

	public static void AddPlane(DBproject esql) {//1
		startingMessage();
		int planeId = 0;
		String make = "";
		String model = "";
		int year = 0;
		int seats = 0;
		String query = "";
		String sucessMessage = "";

		// Validate Plane Id
		while (true){
			planeId = readIntegerHelper("Plane id");
			int rowCount = executeSelectQuery(String.format("SELECT * FROM Plane P WHERE P.id = %d", planeId), esql);	
			if (rowCount > 0){
				System.out.println("Plane id already exists. Please enter a valid plane id");
			}
			else {
				break;
			}
		}

		// Validate Make
		while (true){
			make = readStringHelper("Make");
			if (make.length() == 0){
				System.out.println("Please enter a make for the plane");
			}
			else if (make.length() > 32){
				System.out.println("The make entered is too long. Plase enter a shorter make");
			}
			else {
				break;
			}
		}

		// Validate Model
		while (true){
			model = readStringHelper("Model");
			if (make.length() == 0){
				System.out.println("Please enter a make for the plane.");
			}
			else if (make.length() > 32){
				System.out.println("The make entered is too long. Plase enter a shorter make.");
			}
			else {
				break;
			}
		}

		// Validate year
		while (true){
			// Read year as an integer
			year = readIntegerHelper("Year");
			// if year is less 1970 or greater than 2020 then it is invalid so keep looping otherwise convert the year to string and break
			if (year < 1970 || year > 2020){
				System.out.println("Invalid year. Year of the plane must be between 1970 and 2020. Try Again.");
			}
		 	else {
				break;
			}
		}

		// Validate number of seats
		while (true){
			// Read number of seats as an integer
			seats = readIntegerHelper("Seats");
			// if number of seats is less than 0 or greater than 500 then it is invalid so keep looping otherwise break
			if (seats <= 0 || seats >= 500){
				System.out.println("Invalid input. Number of seats must be between 0 and 500");
			}
			else {
				break;
			}
		}
		// Create Plane
		query = String.format("INSERT INTO Plane VALUES (%d, '%s', '%s', %d, %d)", planeId, make, model, year, seats);
		sucessMessage = String.format("Plane (%d) successfully created", planeId);
		executeUpdateInsertQuery(query, sucessMessage, esql);
	}

//=========================================================================================================================================================================


	public static void AddPilot(DBproject esql) {//2
        startingMessage();
		int rowCount = 0;
		int pilotNumber = 0;
                String sucessMessage = "";
                String query = "";
                String fullname = "";
                String nationality = "";
		// Validate pilot number. If it already exists then keep looping otherwise break
		while(true){
			pilotNumber = readIntegerHelper("Pilot number");
			rowCount = executeSelectQuery(String.format("SELECT * FROM Pilot P WHERE P.id = %d;", pilotNumber), esql); 
			if (rowCount > 0){
				System.out.println("Pilot number already exists. Please enter a valid Pilot number");
			}
			else {
				break;
			}
		}

        //validate full name of pilot.
		while (true){
			fullname = readStringHelper("fullname");
			if (fullname.length() == 0){
				System.out.println("Invalid name. Please enter correct fullname");
			}
			else {
				break;
			}
		}

        //validate nationality.
		while(true){
			nationality = readStringHelper("nationality");
			if(nationality.length() == 0){
				System.out.println("Invalid nationality. Please enter correct nationality");
			}
			else {
				break;
			}
		}

                // create pilot
                query = String.format("INSERT INTO Pilot VALUES (%d, '%s', '%s')", pilotNumber, fullname, nationality);
		sucessMessage = "The Pilot successfully created";
                executeUpdateInsertQuery(query, sucessMessage, esql);
	
/*		// update Repairs relation
		query = String.format("INSERT INTO Repairs(pilot_id) VALUES (%d)", pilotNumber);
                sucessMessage = "The Repairs entity successfully updated";
                executeUpdateInsertQuery(query, sucessMessage, esql);
*/
	}
//=========================================================================================================================================================================

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		startingMessage();
		String query = "";
		String sucessMessage = "";
		int flightNumber = 0;
		int planeId = 0;
		int pilotId = 0;
		int rowCount = 0;
		int cost = 0;
		int numSold = 0;
		int numStops= 0;
		String departureDate = "";
		String arrivalDate = "";
		String departureAirport = "";
		String arrivalAirport = "";

		// Validate flight number. If it already exists then keep looping otherwise convert it to string and continue reading flight information
		while (true){
			flightNumber = readIntegerHelper("Flight number");
			rowCount = executeSelectQuery(String.format("SELECT * FROM FLIGHT F WHERE F.fnum = %d;", flightNumber), esql);
			// If rowCount is greater than 0 then flight with the inputted flight number already exists
			if (rowCount > 0){
				System.out.println("Flight number already exists. Please enter a valid flight number");
			}
			else {
				break;
			}
		}

		// Validate cost
		while (true){
			cost = readIntegerHelper("Cost (Dollars)");
			if (cost <= 0){
				System.out.println("Invalid cost. Please enter a value greater then 0");
			}
			else {
				break;
			}
		}	
		
		// Validate number of tickets sold
		while (true){
			numSold = readIntegerHelper("Number of tickets sold");
			if (numSold < 0){
				System.out.println("Invalid number of tickets sold. Please enter a value greater or equal to 0");
			}
			else {
				break;	
			}
		}

		// Validate number of stops
		while (true){
			numStops = readIntegerHelper("Number of stops");
			if (numStops < 0){
				System.out.println("Invalid number of stops. Please enter a value greater or equal to 0");
			}
			else {
				break;
			}
		}

		departureDate = constructDate("Departure Date");
		arrivalDate = constructDate("Arrival Date");
		

		//Note we assume a flight can depart and arrive at the same airport, so we do not validate the case when the airport codes are the same
		// Validate departure airport code
		while (true){
			departureAirport = readStringHelper("Departure airport code");
			if (departureAirport.length() > 5){
				System.out.println("Invalid departure airport code. Please enter a code of at most 5 letters/digits");
			}
			else {
				break;
			}
		}

		// Validate arrival airport code
		while (true){
			arrivalAirport = readStringHelper("Arrival airport code");
			if (arrivalAirport.length() > 5){
				System.out.println("Invalid arrival airport code. Please enter a code of at most 5 letters/digits");
			}
			else {
				break;
			}
		}

		// Validate Plane Id
		while (true){
			planeId = readIntegerHelper("Plane Id");
			rowCount = executeSelectQuery(String.format("SELECT * FROM Plane P WHERE P.id = %d;", planeId), esql);
			// If rowCount is 0 then plane does not exist
			if (rowCount == 0){
				System.out.println("Plane does not exist. Please enter a valid plane id");
			}
			else {
				break;
			}
		}

		// Validate Pilot Id
		while (true){
			pilotId = readIntegerHelper("Pilot Id");
			rowCount = executeSelectQuery(String.format("SELECT * FROM Pilot P WHERE P.id = %d;", pilotId), esql);
			// If rowCount is 0 then pilot does not exist
			if (rowCount == 0){
				System.out.println("Pilot does not exist. Please enter a valid pilot id");
			}
			else {
				break;
			}
		}

		// Creates flight
		query = String.format("INSERT INTO Flight VALUES (%d, %d, %d, %d, '%s', '%s', '%s', '%s')", flightNumber, cost, numSold, numStops, departureDate, arrivalDate, arrivalAirport, departureAirport);
		sucessMessage = String.format("Flight (%d) successfully created", flightNumber);
		executeUpdateInsertQuery(query, sucessMessage, esql);

		// Creates flight Info
		int flightInfoId = generateValidId();
		query = String.format("INSERT INTO FlightInfo VALUES(%d, %d, %d, %d)", flightInfoId, flightNumber, pilotId, planeId);
		sucessMessage = "Flight Information sucessfully saved";
		executeUpdateInsertQuery(query, sucessMessage, esql);

		int scheduleId = generateValidId();
		// Creates Schedule
		query = String.format("INSERT INTO Schedule VALUES(%d, %d, '%s', '%s')", scheduleId, flightNumber, departureDate, arrivalDate);
		sucessMessage = "Flight sucessfully scheduled";
		executeUpdateInsertQuery(query, sucessMessage, esql);
	}

//=========================================================================================================================================================================


	public static void AddTechnician(DBproject esql) {//4
        startingMessage();
		int rowCount = 0;
		int technicianId = 0;
                String fullname = "";
		String query ="";
                String sucessMessage = "";
	 // Validate Technician id. If it already exists then keep looping otherwise convert it to string and continue reading Technician information
		while(true){
			technicianId = readIntegerHelper("Technician id");
			rowCount = executeSelectQuery(String.format("SELECT * FROM Technician T WHERE T.id = %d;", technicianId), esql);
	                if (rowCount > 0){
            	              System.out.println("Technician id already exists. Please enter a valid technician id");
           		}
            		else {
              		 	break;
           		}
       		}

        //validate full name of technician
		while (true){
			fullname = readStringHelper("fullname"); 
			if (fullname.length() == 0){
				System.out.println("Invalid name. Please enter correct fullname");
			}
			else {
				break;
			}
		}

		//create technician
		query = String.format("INSERT INTO Technician VALUES (%d, '%s')", technicianId, fullname);
		sucessMessage = "The technician successfully created";
                executeUpdateInsertQuery(query, sucessMessage, esql);

/*		// update Repairs entity
                query = String.format("INSERT INTO Repairs(technician_id) VALUES (%d)", technicianId);
                sucessMessage = "The Repairs entity successfully updated";
                executeUpdateInsertQuery(query, sucessMessage, esql);
*/
	}

//=========================================================================================================================================================================

	// Book Flight: Given a customer and flight that he/she wants to book, determine the status
	// of the reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database
	// with appropriate status.
	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		startingMessage();
		int customerId = 0;
		int flightNumber = 0;
		List<List<String>> FlightRecord = new ArrayList<List<String>>();
		boolean procceed = false;
		String reservationNum = "";
		//String flightNum = "";
		String query = "";
		String sucessMessage = "";

		// Validate customer Id
		while (true){
			customerId = readIntegerHelper("Customer ID");
			int rowCountCustomer = executeSelectQuery(String.format("SELECT * FROM CUSTOMER C WHERE C.id = %d;", customerId), esql);
			// If rowCount is greater than 0 then flight with the inputted flight number already exists
			if (rowCountCustomer == 0){
				System.out.println("Customer does not exist. Please enter a valid customer ID");
			}
			else {
				break;
			}
		}

		// Validate flight number
		while (true){
			flightNumber = readIntegerHelper("Flight number");
			FlightRecord  = executeSelectQueryGetResults(String.format("SELECT * FROM FLIGHT F WHERE F.fnum = %d;", flightNumber), esql);
			// If rowCount is greater than 0 then flight with the inputted flight number already exists
			if (FlightRecord.isEmpty()){
				System.out.println("Flight does not exist. Please enter a valid flight number");
			}
			else {
				break;
			}
		}

		List<List<String>> ReservationRecord = executeSelectQueryGetResults(String.format("SELECT * FROM Reservation R WHERE R.cid = %d AND R.fid = %d;", customerId, flightNumber), esql);

		// If a reservation exists
		if (!ReservationRecord.isEmpty()){
			reservationNum = ReservationRecord.get(0).get(0);
			//flightNum = FlightRecord.get(0).get(0);

			// If resevation status is W:
			if (ReservationRecord.get(0).get(3).equals("W")){
				System.out.println("Customer is currently WAITLISTED for Flight:\n" + DisplayFlightInfo(FlightRecord.get(0)));
			}// If resereation status is R
			else if (ReservationRecord.get(0).get(3).equals("R")){

				System.out.println("Customer is RESERVED for flight:\n" + DisplayFlightInfo(FlightRecord.get(0)));
				System.out.println("Woud you like to change customer's reservation status to Confirmed? (Y/N)");
				procceed = getYesNoAnswer();

				// Update reservation status to confirm
				if (procceed){
					query = String.format("UPDATE Reservation R SET R.status = 'C' WHERE R.rnum = %s", reservationNum);
					sucessMessage = String.format("Successfully CONFIRMED resevation (%s) for flight %d ", reservationNum, flightNumber);
					executeUpdateInsertQuery(query, sucessMessage, esql);
				}
				else {
					// User still reserved
					System.out.println("Customer is still RESERVED for flight:\n" + DisplayFlightInfo(FlightRecord.get(0)));
				}
			} // If reservation status is C
			else {
				// Display confirmation
				System.out.println("Customer is CONFIRMED for flight:\n" + DisplayFlightInfo(FlightRecord.get(0)));
				System.out.println("No further action necessary");
			}
		} // If there is no reservation
		else{
			// If flight is full
			if (isFlightFull(flightNumber, esql)){
				System.out.println("Flight is currently full. Would you like to add the customer to the waitlist? (Y/N)");
				procceed = getYesNoAnswer();
				if (procceed){
					// Crates a reservation with W status
					query = String.format("INSERT INTO Reservation (%d, %d, %d, 'W')", generateValidId(), customerId, flightNumber); 
					sucessMessage = String.format("Sucessfully WAITLISTED customer for flight (%d)", flightNumber);
					executeUpdateInsertQuery(query, sucessMessage, esql);
				}
				else {
					System.out.println(String.format("Customer was not WAITLISTED for flight (%d)", flightNumber));
				}
			}
			else {
				// TO DO CREATE RESEVATION NUMBERS
				System.out.println("Flight has open seats. Wold you like to CONFIRM or RESERVE the flight for the customer?");
				// Check if user wants to confirm or reseve a flight
				String answer = "";
				Scanner scanner = new Scanner(System.in);
				boolean confirmed = false;

				while(true){
					System.out.println("Pleaser enter C/c to CONFIRM the flight or R/r to RESERVE the flight");
					answer = scanner.nextLine();
					if (answer.equals("C") || answer.equals("c")){
						confirmed = true;
						break;
					} 
					else if (answer.equals("R") || answer.equals("r")){
						confirmed = false;
						break;
					}
					else {
						System.out.print("Invalid choice. ");
					}
				}
				scanner.close();
				if (confirmed){
					query = String.format("INSERT INTO Reservation (%d, %d, %d, 'C')", generateValidId() ,customerId, flightNumber); 
					sucessMessage = String.format("Sucessfully CONFIRMED customer for flight (%d)", flightNumber);
					executeUpdateInsertQuery(query, sucessMessage, esql);					
				}
				else {
					query = String.format("INSERT INTO Reservation (%d, %d, %d, 'R')",  generateValidId(), customerId, flightNumber); 
					sucessMessage = String.format("Sucessfully RESERVED customer for flight (%d)", flightNumber);
					executeUpdateInsertQuery(query, sucessMessage, esql);	
				}
			}
		}
	}

//=========================================================================================================================================================================

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
		// check the flight number	  
		startingMessage();
		int rowCount = 0;
		int flightNum = 0;
		String query ="";
		String query2 ="";
                int totalAvailableSeats = 0;
                int totalNumBooked = 0;
                int totalNumSeats = 0;
		while(true){
			flightNum = readIntegerHelper("flight number");
			rowCount = executeSelectQuery(String.format("SELECT * FROM Flight F WHERE F.fnum = %d;", flightNum), esql);
			if (rowCount == 0){
				System.out.println("There is no flight available. Please enter a valid flight number");
			}
			else {
				break;
			}
	    }
	
		  // check the depart date
		String departDate ="";
		while(true){
			departDate = readStringHelper("Depart date");
			rowCount = executeSelectQuery(String.format("SELECT * FROM Flight F WHERE F.actual_departure_date = %d;", departDate), esql);
			if (rowCount == 0){
				System.out.println("Please enter a valid departure date");
			}
			else {
				break;
			}
		}
          	try{
			// total  number of seats 
			query = String.format("SELECT P.seats FROM Plane P, FlightInfo FI, Flight F  WHERE FI.flight_id = F.fnum AND F.fnum = %d AND F.actual_departure_date = %s AND FI.plane_id = P.id;",flightNum, departDate );
			System.out.println();
			totalNumSeats = esql.executeQuery(query);
			// number of reserved seats/ booked seats
			query2 = String.format("SELECT COUNT(*) FROM Reservation R, Flight F  WHERE R.fid = F.fnum AND R.status = R;");
			System.out.println();
			totalNumBooked = esql.executeQuery(query2);
			// number of availble seats
			totalAvailableSeats = totalNumSeats - totalNumBooked;
                	if (totalAvailableSeats == 0){
                        System.out.println(" No seats available");
                	}
			else{   
			System.out.println(String.format("The flight has (%d) available seats", totalAvailableSeats));
			System.out.println();
			}
		}
		catch(Exception e){
                        System.out.println("Something went wrong. Please try again!");
                        System.err.println(e.getMessage());
		}
	}

	// TO DO: IMPROVE READABILITY?
//=====================================================================================================================================================================
	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order. Excute query and print
		try {	
			int rowCount = esql.executeQueryAndPrintResult("SELECT P.id as ID, P.make as Make, P.model as Model, COUNT(R.rid) as NumOfRepairs FROM Plane P, Repairs R WHERE P.id = R.plane_id GROUP BY P.id ORDER BY NumOfRepairs DESC;");		
		}
		 catch (Exception e){
			System.out.println("Something went wrong. Please try again!");
			System.err.println(e.getMessage());         
		 }	
	}
//=========================================================================================================================================================================
	
	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
	//List total number of repairs per year in ascending order: Return the years with the number of
        //repairs made in those years in ascending order of number of repairs per year.		
		try{
			int rowCount = esql.executeQueryAndPrintResult("SELECT DISTINCT repair_date, COUNT(rid) AS NumRepairsPerYear FROM Repairs GROUP BY repair_date ORDER BY COUNT(rid) ASC;");
                        if ( rowCount == 0){
                	        System.out.println(" No records ");     
              	        }        
		}
		catch (Exception e){
                        System.out.println("Something went wrong. Please try again!");
                        System.err.println(e.getMessage());
                 }
	}

//=========================================================================================================================================================================
	//TO DO: TEST
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.	
		startingMessage();
		int flightNumber = 0;
		String passengerStatus = "";
		int rowCount = 0;

		// Validate flight number
		while (true){
			// Reads flight number
			flightNumber = readIntegerHelper("Flight number");
			// Check if flight number actually exists
			rowCount = executeSelectQuery(String.format("SELECT * FROM FLIGHT F WHERE F.fnum = %d;", flightNumber), esql);
			// If it does not then keep looping otherwise break
			if (rowCount == 0){
				System.out.println("Flight number does not exist. Please enter a valid flight number");
			}
			else {
				break;
			}
		}

		// Validate passenger status
		while (true){
			try{
				System.out.println("Select a passenger status");
				System.out.println("W - Waitlisted");
				System.out.println("C - Confirmed");
				System.out.println("R - Reserved");
				passengerStatus = in.readLine();
				if (!passengerStatus.equals("W") && !passengerStatus.equals("C") && !passengerStatus.equals("R")){
					System.out.println(passengerStatus);
					throw new Exception();
				}
				else {
					break;
				}
			}catch(Exception e){
				System.out.println("Your input is invalid! Please select a valid option");
			}			
		} 
		String query = String.format("SELECT COUNT(*) as NumberOfPassengers FROM Customer C, Reservation R WHERE R.cid = C.id and R.status = '%s' and R.fid = %d;", passengerStatus, flightNumber);
		String numbeOfPassengers = executeSelectQueryGetResults(query, esql).get(0).get(0);
		System.out.print("Number of passengers ");
		if (passengerStatus.equals("W")) System.out.print("waitlisted: ");
		else if (passengerStatus.equals("C")) System.out.print("confirmed: ");
		else System.out.print("reserved: ");
		System.out.println(numbeOfPassengers);
	}

/*************************************************************************  Helper Functions ********************************************************************* */
	
	public static void startingMessage(){
		System.out.println("***************************************");
		System.out.println("Please enter the following information:");
		System.out.println("***************************************");
	}

	// Read String values and handle exceptions
	public static int readIntegerHelper(String nameOfField) {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print(nameOfField + ": ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! Please try again");
				continue;
			}//end try
		}while (true);
		return input;
	} 

	// Read String values and handle exceptions
	public static String readStringHelper(String nameOfField) {
		String input;
		// returns only if a correct value is given.
		do {
			System.out.print(nameOfField + ": ");
			try { // read the string and break.
				input = in.readLine();
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid! Please try again");
				continue;
			}//end try
		}while (true);
		return input;
	}

	// Executes a Select Query and handles database exceptions
	// Returns the row count
	public static int executeSelectQuery (String query, DBproject esql){
		int rowCount = 0;
		try {
			rowCount = esql.executeQuery(query);
		 }
		 catch (Exception e){
			System.out.println("Something went wrong, please try again!");
			//System.err.println(e.getMessage());           
		 }	
		 return rowCount;
	}

	public static void executeUpdateInsertQuery(String query, String sucessMessage, DBproject esql){
		try {
			//System.out.println(query);
			System.out.println(); 
			esql.executeUpdate(query);
			System.out.println(sucessMessage);
			System.out.println(); 
		}
		catch (Exception e){
			System.out.println("Something went wrong, please try again!");
			//System.err.println(e.getMessage());         
		}
	}

	public static List<List<String>> executeSelectQueryGetResults(String query, DBproject esql){
		List<List<String>> records = new ArrayList<List<String>>();
		try {
			records = esql.executeQueryAndReturnResult(query);
		 }
		 catch (Exception e){
			System.out.println("Something went wrong. Please try again!");
			//System.err.println(e.getMessage());         
		 }	
		 return records;
	}

	public static String DisplayFlightInfo(List<String> FlightRecord){
		return "\nNumber: " + FlightRecord.get(0) + "\nCost: $" + FlightRecord.get(1) + "\nDeparting on: " + FlightRecord.get(4) + "\n";
	}

	// Asks user for year, month, and date information and constructs a date string in the format yyyy-mm-dd
	public static String constructDate (String DateType){
		int year = 0;
		int month = 0;
		int day = 0;
		System.out.println("Enter the following information for the " + DateType + ":");
		// Get current year
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		
		while (true){
			year = readIntegerHelper("Year");
			if (year < currentYear || year > currentYear + 2){
				System.out.println("Invalid year. Please enter a valid year");
			}
			else {
				break;
			}
		}

		while (true){
			month = readIntegerHelper("Month");
			if (month < 1 || month > 12){
				System.out.println("Invalid month. Please enter a valid month");
			}
			else{
				break;
			}
		}

		while (true){
			day = readIntegerHelper("Day");
			if (day < 1 || day > 31){
				System.out.println("Invalid day. Please enter a valid day");
			}
			else{
				break;
			}
		}

		return Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day); 
	}

	public static boolean getYesNoAnswer(){
		String answer = "";
		Scanner scanner = new Scanner(System.in);
		while(true){
			System.out.println("Pleaser enter Y/y to continue or N/n to stop");
			answer = scanner.nextLine();
			if (answer.equals("Y") || answer.equals("y")){
				scanner.close();
				return true;
			} 
			else if (answer.equals("N") || answer.equals("n")){
				scanner.close();
				return false;
			}
			else {
				System.out.print("Invalid choice. ");
			}
		}
	}

	public static boolean isFlightFull(int flightNumber, DBproject esql){
		String query = "";
		String numOfTicketsSold = "";
		String numOfSeatsInPlane = "";

		// Get the number of tickets sold
		query = String.format("Select F.num_sold FROM Flight F WHERE F.fnum = %d;", flightNumber);
		numOfTicketsSold = executeSelectQueryGetResults(query, esql).get(0).get(0);

		// Get the number of seats in the plane
		query = String.format("SELECT P.seats FROM Plane P WHERE P.Id = (SELECT FI.plane_id FROM FlightInfo FI WHERE FI.flight_id = %d);", flightNumber);
		numOfSeatsInPlane = executeSelectQueryGetResults(query, esql).get(0).get(0);

		return Integer.parseInt(numOfSeatsInPlane) <= Integer.parseInt(numOfTicketsSold);
	}

	public static int generateValidId(){
		// create instance of Random class 
        Random rand = new Random(); 
        // Generate random integers in range 10000 to 100000
        return rand.nextInt(90000) + 10000;
	}
}
