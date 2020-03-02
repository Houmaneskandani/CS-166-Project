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

	// TO DO: Check if plane id already exists
	public static void AddPlane(DBproject esql) {//1
		startingMessage();
		// Read plane information
		
		int planeId = readIntegerHelper("Plane id");
		String make = readStringHelper("Make");
		String model = readStringHelper("Model");
		int year = 0;
		int seats = 0;

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

		while (true){
			// Read number of seats as an integer
			seats = readIntegerHelper("Seats");
			// if number of seats is less than 0 or greater than 500 then it is invalid so keep looping otherwise convert the number of seats to a string and break
			if (seats < 0 || seats > 500){
				System.out.println("Invalid input. Number of seats must be between 0 and 500");
			}
			else {
				break;
			}
		}

		try {
			String query = String.format("INSERT INTO Plane VALUES (%d, '%s', '%s', %d, %d)", planeId, make, model, year, seats);
			System.out.println(query);
			System.out.println(); 
			esql.executeUpdate(query);
			System.out.println(String.format("Plane (%s) successfully created", planeId));
			System.out.println(); 
		 }
		 catch (Exception e){
			System.err.println (e.getMessage());         
		 }
	}

	public static void AddPilot(DBproject esql) {//2
	}

	// To do: 
	// 	Check for date validation. format (yyyy-mm-dd)
	//	Check flight number does not already exist
	// Check cost is > 0
	// Check numof stops and num stold >=0

	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB
		startingMessage();
		int flightNumber = 0;
		
		// Read flight as an integer. If it already exists then keep looping otherwise convert it to string and continue reading flight information
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

		int cost =  readIntegerHelper("Cost");
		int numSold = readIntegerHelper("Number of tickets sold");
		int numStops = readIntegerHelper("Number of stops"); 
		String departureDate = readStringHelper("Actual departure date"); 
		String arrivalDate = readStringHelper("Actual arrival date"); 
		String departureAirport = readStringHelper("Departure airport code");
		String arrivalAirport = readStringHelper("Arrival airport code");
		try {
			String query = String.format("INSERT INTO Plane VALUES (%d, %d, %d, %d, '%s', '%s', '%s', '%s')", flightNumber, cost, numSold, numStops, departureDate, arrivalDate, departureAirport, arrivalAirport);
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

	public static void AddTechnician(DBproject esql) {//4
	}

	// To do: start it
	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB
	}

	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
	}

	// TO DO: handle errors??	
	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order
		int rowCount = executeSelectQuery("SELECT P*, COUNT(R.rid) as NumOfRepairs FROM Plane P, Repairs R WHERE P.id = R.plane_id GROUP BY P.id ORDER BY NumOfRepairs DESC;", esql);
	}

	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order
	}
	
	// TO DO: Check if flight number is valid
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.	
		startingMessage();
		int flightNumber = 0;
		String passengerStatus = "";
		int rowCount = 0;

		// Loops until a valid flight number is inputted
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

		// Loops until a valid passenger status is inputted
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
			System.out.println(); 
			rowCount = esql.executeQuery(query);
			System.out.println(); 
		 }
		 catch (Exception e){
			System.err.println (e.getMessage());         
		 }	
		 return rowCount;
	}
}