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

//====================================

	public static void AddPlane(DBproject esql) {//1
		startingMessage();
		// Read plane information
		
		// Validates Plane Id
		int planeId = 0;
		while (true){
			planeId = readIntegerHelper("Plane id");
			int rowCount = executeSelectQuery(String.format("SELECT * FROM Plane P WHERE P.id = %d", planeId), esql);	
			if (rowCount != 0){
				System.out.println("Plane id already exists. Please enter a valid plane id");
			}
			else {
				break;
			}
		}
		String make = readStringHelper("Make");
		String model = readStringHelper("Model");
		int year = 0;
		int seats = 0;

		// Validate year
		while (true){
			// Read year as an integer
			year = readIntegerHelper("Year");
			// if year is less 1970 or greater than 2020 then it is invalid so keep looping otherwise convert the year to string and break
			if (year < 1970 || year > 2020){
				System.out.println("Invalid input. Year of the plane must be between 1970 and 2020");
			}
		 	else {
				break;
			}
		}

		// Validate number of seats
		while (true){
			// Read number of seats as an integer
			seats = readIntegerHelper("Seats");
			// if number of seats is less than 0 or greater than 500 then it is invalid so keep looping otherwise convert the number of seats to a string and break
			if (seats <= 0 || seats >= 500){
				System.out.println("Invalid input. Number of seats must be between 0 and 500");
			}
			else {
				break;
			}
		}

		try {
			String query = String.format("INSERT INTO Plane VALUES (%d, '%s', '%s', %d, %d)", planeId, make, model, year, seats);
			System.out.println(); 
			esql.executeUpdate(query);
			System.out.println(String.format("Plane (%s) successfully created", planeId));
			System.out.println(); 
		 }
		 catch (Exception e){
			System.err.println (e.getMessage());         
		 }
	}

//===================================

	public static void AddPilot(DBproject esql) {//2
                 startingMessage();
		 int rowCount = 0;
		 int pilotNumber = 0;
	// Validate pilot number. If it already exists then keep looping otherwise convert it to string and continue reading pilot information
                while(true){
                        int pilotNumber = readIntegerHelper("Pilot number");
                        int rowCount = executeSelectQuery(String.format("SELECT * FROM Pilot P WHERE P.id = %d;", pilotNumber), esql); 
                        if (rowCount > 0){
                                System.out.println("Pilot number already exists. Please enter a valid Pilot number");
                        }
                        else {
                                break;
                        }
                }

         //validate full name of pilot.
                String fullname = "";
                while (true){
                       fullname = readStringHelper("fullname");
	           //    int rowCount =  executeSelectQuery(String.format("SELECT * FROM Pilot  P WHERE P.fullname = %s;", fullname), esql);       
                       if (fullname.length() = 0){
                                System.out.println("Invalid name. Please enter correct fullname");
                        }
                        else {
                                break;
                        }
                }

        //validate nationality.
                String nationality = "";
                while(true){
                        nationality = readStringHelper("nationality");
                        if(nationality.length() = 0){
                                System.out.println("Invalid nationality. Please enter correct nationality");
                        }
                        else {
                                break;
                        }
                }

                try {
                        String query = String.format("INSERT INTO Pilot VALUES (%d, '%s', '%s')", pilotNumber, fullname, nationality);
                        System.out.println();
                        esql.executeUpdate(query);
                        System.out.println(String.format("Pilot (%d) successfully created", pilotNumber));
                        System.out.println();
                 }
                 catch (Exception e){
                        System.err.println (e.getMessage());
                 }
        }

//===================================

	// To do: 
	// 	Check for date validation. format (yyyy-mm-dd)
	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		startingMessage();
		int flightNumber = 0;
		
		// Validate flight number. If it already exists then keep looping otherwise convert it to string and continue reading flight information
		while (true){
			flightNumber = readIntegerHelper("Flight number");
			int rowCount = executeSelectQuery(String.format("SELECT * FROM FLIGHT F WHERE F.fnum = %d;", flightNumber), esql);
			// If rowCount is greater than 0 then flight with the inputted flight number already exists
			if (rowCount > 0){
				System.out.println("Flight number already exists. Please enter a valid flight number");
			}
			else {
				break;
			}
		}

		// Validate cost
		int cost = 0;
		while (true){
			cost = readIntegerHelper("Cost");
			if (cost <= 0){
				System.out.println("Invalid cost. Please enter a value greater then 0");
			}
			else {
				break;
			}
		}	
		
		// Validate number of tickets sold
		int numSold = 0;
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
		int numStops= 0;
                while (true){
                        numStops = readIntegerHelper("Number of stops");
                        if (numStops < 0){
                                System.out.println("Invalid number of stops. Please enter a value greater or equal to 0");
                        }
                        else {
                                break;
                        }
                }

		String departureDate = readStringHelper("Actual departure date"); 
		String arrivalDate = readStringHelper("Actual arrival date"); 
		
		// Validate departure airport code
		String departureAirport = "";
		while (true){
			departureAirport = readStringHelper("Departure airport code");
			if (departureAirport.length() > 5){
				System.out.println("Invalid departure airport code. Please enter a code of at most 5 letters/digits");
			}
			else {
				break;
			}
		}

		//Validate arrival airport code
		String arrivalAirport = "";
		while (true){
			arrivalAirport = readStringHelper("Arrival airport code");
			if (arrivalAirport.length() > 5){
				System.out.println("Invalid arrival airport code. Please enter a code of at most 5 letters/digits");
			}
			else {
				break;
			}
		}

		try {
			String query = String.format("INSERT INTO Flight VALUES (%d, %d, %d, %d, '%s', '%s', '%s', '%s')", flightNumber, cost, numSold, numStops, departureDate, arrivalDate, arrivalAirport, departureAirport);
			System.out.println(query);
			System.out.println(); 
			esql.executeUpdate(query);
			System.out.println(String.format("Flight (%d) successfully created", flightNumber));
			System.out.println(); 
		 }
		 catch (Exception e){
			System.err.println (e.getMessage());         
		 }
	}

//==================================


	public static void AddTechnician(DBproject esql) {//4
                 startingMessage();
		 int rowCount = 0;
		 int technicianId = 0;
        // Validate Technician id. If it already exists then keep looping otherwise convert it to string and continue reading Technician information
                while(true){
                        int technicianId = readIntegerHelper("Technician id");
                        int rowCount = executeSelectQuery(String.format("SELECT * FROM Technician T WHERE T.id = %d;", pilotNumber), esql);
                        if (rowCount > 0){
                                System.out.println("Technician number already exists. Please enter a valid technician id");
                        }
                        else {
                                break;
                        }
               }

        //validate full name of technician
                String fullname = "";
                while (true){
                       fullname = readStringHelper("fullname"); 
                       if (fullname.length() = 0){
                                System.out.println("Invalid name. Please enter correct fullname");
                        }
                        else {
                                break;
                        }
                }

                try {
                        String query = String.format("INSERT INTO Technician VALUES (%d, '%s')", technicianId, fullname);
                        System.out.println();
                        esql.executeUpdate(query);
                        System.out.println(String.format("Technician id (%d) successfully created", technicianId));
                        System.out.println();
                 }
                 catch (Exception e){
                        System.err.println (e.getMessage());
                 }
	}

//=================================


	// To do: start it
	// Book Flight: Given a customer and flight that he/she wants to book, determine the status
	// of the reservation (Waitlisted/Confirmed/Reserved) and add the reservation to the database
	// with appropriate status.
	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
		startingMessage();
		
		int customerId = 0;
		// Validate customer Id.
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

		int flightNumber = 0;

		// Validate flight number.
		while (true){
			flightNumber = readIntegerHelper("Flight number");
			List<List<String>> FlightRecord  = executeSelectQueryGetResults(String.format("SELECT * FROM FLIGHT F WHERE F.fnum = %d;", flightNumber), esql);
			// If rowCount is greater than 0 then flight with the inputted flight number already exists
			if (FlightRecord.isEmpty()){
				System.out.println("Flight does not exist. Please enter a valid flight number");
			}
			else {
				break;
			}
		}

		List<List<String>> ReservationRecord  = executeSelectQueryGetResults(String.format("SELECT * FROM Reservation R WHERE R.cid = %d AND R.fid = %d;", customerId, flightNumber), esql);
		if (!ReservationRecord.isEmpty()){
			// If resevation's status is W:
				// Display: "You are currently waitlised for Flight <inser flight info>."" 
				// If flight still full:
					// Display: "Fligh is still full. No actions available"
					// Break

			for (String S : ReservationRecord.get(0)){
				System.out.println(S);
			}

			// Else if Reservation' status is R:
				// Display: "You have reserved  Flight <inser flight info>."" 
				// Display: "Woud you like to confirm your fligh? (Y/N)"
				// If Y 
					// Update reservation record status to Confirmed and break
					// Display: "You have confirmed Flight <insert flight info>."
					// break
				// Else: 
					// Display: "You are still reserved for Flight <insert fligh info>"
					// break

			//  Else
				// Display: "You have confirmed Flight <insert flight info>.""
				// Break 
		}
		// Else
			// If Flight is full
				// Display: "Fligh <insert flight info> is full. Would you like to be added to the waitlist? (Y/N)"
				// if Y : Waitlist user
				// else : break
			// Else
				// Display "Wold you like to confirm or reserve the flight <insert flight info>? (C/R)"
				// if C : Create a reservation record with Confirmed status
				// else : Create a reservation record with Reserved status


	}

//=====================================

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
		// check the flight number	  
                 startingMessage();
                 int rowCount = 0;
                 int flightNum = 0;
                 while(true){
                        int flightNum = readIntegerHelper("flight number");
                        int rowCount = executeSelectQuery(String.format("SELECT * FROM Flight F WHERE F.fnum = %d;", flightNum), esql);
                        if (rowCount = 0){
                                System.out.println("There is no flight available Please enter a valid flight number");
                        }
                        else {
                                break;
                        }
                }
	
		// check the depart date
		String departDate ="";
		String rowCount =""; 
                 while(true){
                        string departDate = readIntegerHelper("depart date");
                        String rowCount = executeSelectQuery(String.format("SELECT * FROM Flight F WHERE F.actual_departure_date = %d;", departDate), esql);
                        if (rowCount.length() = 0){
                                System.out.println(" Please enter a valid departure date");
                        }
                        else {
                                break;
                        }
                }
/*
                try {
                        String query = String.format("SELECT P.num_seats From Plane P Where);
                        System.out.println();
                        esql.executeUpdate(query);
                        System.out.println(String.format("Technician id (%d) successfully created", technicianId));
                        System.out.println();
                 }
                 catch (Exception e){
                        System.err.println (e.getMessage());
                 }

*/






	}
//====================================

	// TO DO: handle errors??	
	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
		int rowCount = executeSelectQuery("SELECT P*, COUNT(R.rid) as NumOfRepairs FROM Plane P, Repairs R WHERE P.id = R.plane_id GROUP BY P.id ORDER BY NumOfRepairs DESC;", esql);
	}
//=====================================

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
	}


//=====================================
	
	// TO DO: Check if flight number is valid
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
			rowCount = executeSelectQuery(String.format("SELECT * FROM FLIGHT F WHERE F.fnum = %d", flightNumber), esql);
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
				if (passengerStatus != "W" && passengerStatus != "C" && passengerStatus != "R"){
					throw new Exception();
				}
				else {
					break;
				}
			}catch(Exception e){
				System.out.println("Your input is invalid! Please select a valid option");
				continue;
			}			
		}
		// Executes query
		 rowCount = executeSelectQuery(String.format("SELECT COUNT(*) as NumberOfPassengers FROM Customer C, Reservation R WHERE R.cid = C.id and R.status = '%s' and R.fid = %d", passengerStatus, flightNumber), esql);
	}

/*********************************  Helper Functions ******************************** */
	
	public static void startingMessage(){
		System.out.println("Please enter the following information:");
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
			System.err.println (e.getMessage());         
		 }	
		 return rowCount;
	}

	public static  List<List<String>> executeSelectQueryGetResults(String query, DBproject esql){
		//list< list< string>> records error not initialized Not Fixed! 
		List<List<String>> records;
		try {
			records = esql.executeQueryAndReturnResult(query);
		 }
		 catch (Exception e){
			System.err.println (e.getMessage());         
		 }	
		 return records;
	}
}
