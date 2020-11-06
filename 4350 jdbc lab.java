/*  CS 4350 LAB 4: Using SQL Server with JDBC for Pomona Transit System
 *  Author: Ryan Atienza
 *  Due: 12/9/19
 *  This program connects to a local database for a hypothetical Pomona Transit System. It allows users to alter or pull records from the database, which is
 *	established using Microsoft's SQL server.
 */

package cs4350_lab4;
import java.sql.*;
import java.util.Scanner;

public class lab_4 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			// registering the driver
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			
			// connection URL
			String connectionUrl = "jdbc:sqlserver://localhost:1433;" + "databaseName=pomona_transit_system;integratedSecurity=true;";
			
			// user interface
			
			int sentinel = 1;
			
			while(sentinel != 0) {
				Scanner input = new Scanner(System.in);
				System.out.println("Pomona Transit System: Main Menu");
				System.out.println("Please select from the following options:");
				System.out.println("\t1: Print a Schedule\n"
						+ "\t2: Edit Trip Offerings\n"
						+ "\t3: Display Stops for a Trip\n"
						+ "\t4: Display Weekly Schedule for a Driver\n"
						+ "\t5: Add a new driver\n"
						+ "\t6: Add a new bus\n"
						+ "\t7: Delete a bus\n"
						+ "\t8: Record Actual Data (time, arrival, passenger information) for a stop\n"
						+ "\t0: Quit\n");
				// get user input
				System.out.print("> ");
				sentinel = input.nextInt(); input.nextLine();
				if(sentinel == 1)
					printSchedule(connectionUrl, input);
				else if(sentinel == 2) {
					System.out.println("Edit Trip Offerings: Submenu");
					System.out.println("Please select from the following options:");
					System.out.println("\t1: Delete a Trip Offering\n"
							+ "\t2: Add a set of Trip Offerings\n"
							+ "\t3: Change the driver for a trip offering\n"
							+ "\t4: Change the bus for a trip offering\n"
							+ "\t5: Return to Main Menu\n");
					// get user input
					System.out.print("> ");
					sentinel = input.nextInt(); input.nextLine();
					if(sentinel == 1)
						deleteTripOffering(connectionUrl, input);
					else if(sentinel == 2)
						addTripOfferings(connectionUrl, input);
					else if(sentinel == 3)
						changeDriver(connectionUrl, input);
					else if(sentinel == 4) 
						changeBus(connectionUrl, input);
				}
				else if(sentinel == 3)
					displayStops(connectionUrl, input);
				else if(sentinel == 4)
					displayDriverSched(connectionUrl, input);
				else if(sentinel == 5)
					addDriver(connectionUrl, input);
				else if(sentinel == 6)
					addBus(connectionUrl, input);
				else if(sentinel == 7)
					deleteBus(connectionUrl, input);
				else if(sentinel == 8)
					recordActualTripStop(connectionUrl, input);
				else if(sentinel == 0)
					input.close();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void printSchedule(String connectionUrl, Scanner input) {
		System.out.println("Printing a schedule...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			String startLocation, destinationName, date; // for user input
			
			// grabbing user input
			System.out.println("Enter Start Location: ");
			startLocation = input.nextLine();
			System.out.println("Enter Destination: ");
			destinationName = input.nextLine();
			System.out.println("Enter Date (YYYY-MM-DD): ");
			date = input.nextLine();
			// executing the query
			
			Statement stmt = con.createStatement();
			String sqlStmt = "SELECT T.TripNumber, T.StartLocationName, T.DestinationName, TOff.Date, TOff.ScheduledStartTime, "
					+ "TOff.ScheduledArrivalTime, TOff.DriverName, TOff.BusID ";
			String fromWhere = "FROM Trip T JOIN TripOffering TOff ON "
					+ "T.TripNumber = TOff.TripNumber "
					+ "WHERE T.StartLocationName = '" + startLocation + "' AND "
					+ "T.DestinationName = '" +  destinationName + "' AND TOff.Date = '" + date + "'" ;
			ResultSet rs = stmt.executeQuery(sqlStmt + fromWhere);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			// printing
			printSQL(rs, rsMeta);
			
			//cleanup
			rs.close();
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	public static void deleteTripOffering(String connectionUrl, Scanner input) {
		System.out.println("Deleting a trip...\n");
		try {
			Connection con;
			con = DriverManager.getConnection(connectionUrl);
			// for user input
			int tripNum;
			String Date, schedStart;
			
			// grabbing user input
			System.out.println("Enter a trip number: ");
			tripNum = input.nextInt(); input.nextLine();
			System.out.println("Enter a date (YYYY-MM-DD): ");
			Date = input.nextLine();
			System.out.println("Enter a Start Time (ex. 8:00 AM): ");
			schedStart = input.nextLine();
			
			Statement stmt = con.createStatement();
			
			String deleteStmt = "DELETE FROM [dbo].[TripOffering] "
					+ "WHERE TripOffering.TripNumber = " + tripNum + " AND "
							+ "TripOffering.Date = '" + Date + "' AND "
									+ "TripOffering.ScheduledStartTime = '" + schedStart + "'";
			
			int rs = stmt.executeUpdate(deleteStmt);
			
			if(rs == 0)
				System.out.println("Problem with deleting.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addTripOfferings(String connectionUrl, Scanner input) {
		System.out.println("Inserting additional trip(s)...\n");
		try {
			Connection con;
			con = DriverManager.getConnection(connectionUrl);
			// for user input
			int tripNum, sentinel, busID;
			sentinel = 1;
			String Date, schedStart, schedArrival, DriverName;
			while(sentinel != 0)
			{
				// grabbing user input
				System.out.println("Enter trip number: ");
				tripNum = input.nextInt(); input.nextLine();
				System.out.println("Enter date (YYYY-MM-DD): ");
				Date = input.nextLine();
				System.out.println("Enter Start Time (ex. 8:00 AM): ");
				schedStart = input.nextLine();
				System.out.println("Enter Secheduled Arrival Time (ex. 12:00 PM): ");
				schedArrival = input.nextLine();
				System.out.println("Enter Driver Name: ");
				DriverName = input.nextLine();
				System.out.println("Enter Bus ID: ");
				busID = input.nextInt();
				
				Statement stmt = con.createStatement();
				
				String insertStmt = "INSERT INTO [dbo].[TripOffering]\r\n" + 
						"           ([TripNumber]" + 
						"           ,[Date]" + 
						"           ,[ScheduledStartTime]" + 
						"           ,[ScheduledArrivalTime]" + 
						"           ,[DriverName]" + 
						"           ,[BusID])" + 
						"     VALUES " + 
						"           (" + tripNum +  
						"           , '" + Date + "'" + 
						"           , '" + schedStart + "'" + 
						"           , '" + schedArrival + "'" +
						"           , '" + DriverName + "'" + 
						"           , " + busID + ")";
				
				int rs = stmt.executeUpdate(insertStmt);
				if(rs == 0)
					System.out.println("Problem with inserting.\n");
				
				System.out.println("Continue inserting? (1 = yes, 0 = no): ");
				sentinel = input.nextInt();
				if(sentinel == 0) {
					//cleanup
					stmt.close();
					con.close();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void changeDriver(String connectionUrl, Scanner input) {
		System.out.println("Changing Driver for a Trip Offering...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			
			int tripNum;
			String Date, schedStart, DriverName;
			
			System.out.println("Enter Trip Number: ");
			tripNum = input.nextInt(); input.nextLine();
			System.out.println("Enter Date (YYYY-MM-DD): ");
			Date = input.nextLine();
			System.out.println("Enter Scheduled Start Time (ex. 8:00 AM): ");
			schedStart = input.nextLine();
			System.out.println("Enter New Driver's Name: ");
			DriverName = input.nextLine();
			
			Statement stmt = con.createStatement();
			String updateStmt = "UPDATE [dbo].[TripOffering]\r\n" + 
					"   SET [DriverName] = '" + DriverName + "' " +  
					" WHERE TripOffering.TripNumber = " + tripNum + " AND "
							+ "TripOffering.Date = '" + Date + "' AND "
									+ "TripOffering.ScheduledStartTime = '" + schedStart + "'";
			int rs = stmt.executeUpdate(updateStmt);
			
			if(rs == 0)
				System.out.println("Problem with updating.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void changeBus(String connectionUrl, Scanner input) {
		System.out.println("Changing bus for a trip offering...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			
			int tripNum, busID;
			String Date, schedStart;
			
			System.out.println("Enter Trip Number: ");
			tripNum = input.nextInt(); input.nextLine();
			System.out.println("Enter Date (YYYY-MM-DD): ");
			Date = input.nextLine();
			System.out.println("Enter Scheduled Start Time (ex. 8:00 AM): ");
			schedStart = input.nextLine();
			System.out.println("Enter New Bus ID: ");
			busID = input.nextInt();
			
			Statement stmt = con.createStatement();
			String updateStmt = "UPDATE [dbo].[TripOffering]\r\n" + 
					"   SET [BusID] = " + busID +  
					" WHERE TripOffering.TripNumber = " + tripNum + " AND "
							+ "TripOffering.Date = '" + Date + "' AND "
									+ "TripOffering.ScheduledStartTime = '" + schedStart + "'";
			int rs = stmt.executeUpdate(updateStmt);
			
			if(rs == 0)
				System.out.println("Problem with updating.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void displayStops(String connectionUrl, Scanner input) {
		System.out.println("Retrieving stop information for a trip...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			int tripNum;
			System.out.println("Enter Trip Number: ");
			tripNum = input.nextInt();
			
			Statement stmt = con.createStatement();
			String query = "SELECT	S.TripNumber, S.StopNumber, S.SequenceNumber, S.DrivingTime " + 
					"FROM	TripStopInfo S " + 
					"WHERE	S.TripNumber = " + tripNum;
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			// print
			printSQL(rs, rsMeta);
			
			//cleanup
			rs.close();
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void displayDriverSched(String connectionUrl, Scanner input) {
		System.out.println("Retrieving Driver Schedule...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			String DriverName, Date;
			System.out.println("Enter Driver Name: ");
			DriverName = input.nextLine();
			System.out.println("Enter Date (YYYY-MM-DD): ");
			Date = input.nextLine();
			
			Statement stmt = con.createStatement();
			String query = "SELECT	* " + 
					"FROM	TripOffering S " + 
					"WHERE	S.DriverName = '" + DriverName + "' AND S.Date = '" + Date + "'";
			ResultSet rs = stmt.executeQuery(query);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			// print
			printSQL(rs, rsMeta);
			
			//cleanup
			rs.close();
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addDriver(String connectionUrl, Scanner input) {
		System.out.println("Adding new driver...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			String DriverName, tel;
			
			System.out.println("Enter Driver's name: ");
			DriverName = input.nextLine();
			System.out.println("Enter Driver's telephone number (xxx-xxx-xxxx, enter 'NULL' if not applicable): ");
			tel = input.nextLine();
			
			Statement stmt = con.createStatement();
			String add = "INSERT INTO [dbo].[Driver] " + 
					"           ([DriverName] " + 
					"           ,[DriverTelephoneNumber]) " + 
					"     VALUES " + 
					"           ('" + DriverName + "'" + 
					"           ,'" + tel + "')";
			
			int rs = stmt.executeUpdate(add);
			
			if(rs == 0)
				System.out.println("Problem with insertion.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addBus(String connectionUrl, Scanner input) {
		System.out.println("Adding new bus...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			int busID;
			String model, yr;
			
			System.out.println("Enter ID number of new bus: ");
			busID = input.nextInt(); input.nextLine();
			System.out.println("Enter bus model: ");
			model = input.nextLine();
			System.out.println("Enter year: ");
			yr = input.nextLine();
			
			Statement stmt = con.createStatement();
			String add = "INSERT INTO [dbo].[Bus]\r\n" + 
					"           ([BusID]" + 
					"           ,[Model]" + 
					"           ,[Year]) " + 
					"     VALUES " + 
					"           (" + busID + 
					"           ,'" + model + "'" + 
					"           ,'" + yr + "')";
			int rs = stmt.executeUpdate(add);
			
			if(rs == 0)
				System.out.println("Problem with insertion.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deleteBus(String connectionUrl, Scanner input) {
		System.out.println("Deleting a bus...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			int busID;
			
			System.out.println("Enter ID of the bus: ");
			busID = input.nextInt();
			
			Statement stmt = con.createStatement();
			String deleteStmt = "DELETE FROM [dbo].[Bus]" + 
					"      WHERE Bus.BusID = " + busID;
			
			int rs = stmt.executeUpdate(deleteStmt);
			
			if(rs == 0)
				System.out.println("Problem with deletion.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void recordActualTripStop(String connectionUrl, Scanner input) {
		System.out.println("Recording Actual Trip Stop details...\n");
		try {
			Connection con = DriverManager.getConnection(connectionUrl);
			String schedStart, Date, actualStart, actualArrival;
			int tripNum, stopNum, numIn, numOut;
			
			// gathering key details
			System.out.println("Enter Trip Number: ");
			tripNum = input.nextInt(); input.nextLine();
			System.out.println("Enter Scheduled Start Time (ex. 8:00 AM): ");
			schedStart = input.nextLine();
			System.out.println("Enter Date (YYYY-MM-DD): ");
			Date = input.nextLine();
			System.out.println("Enter Stop Number: ");
			stopNum = input.nextInt(); input.nextLine();
			
			// recording the data
			System.out.println("Enter the Actual Start Time (ex. 8:00 AM): ");
			actualStart = input.nextLine();
			System.out.println("Enter the Actual Arrival Time (ex. 9:30 AM): ");
			actualArrival = input.nextLine();
			System.out.println("Enter the number of passengers that boarded at the stop: ");
			numIn = input.nextInt();
			System.out.println("Enter the number of passengers that got off at the stop: ");
			numOut = input.nextInt();
			
			Statement stmt = con.createStatement();
			String updateStmt = "UPDATE [dbo].[ActualTripStopInfo]" + 
					"   SET [ActualStartTime] = '" + actualStart + "'" + 
					"      ,[ActualArrivalTime] = '" + actualArrival + "'" + 
					"      ,[NumPassengerIn] = " + numIn + 
					"      ,[NumPassengerOut] = " + numOut +
					" WHERE ActualTripStopInfo.TripNumber = " + tripNum + " AND "
							+ "ActualTripStopInfo.ScheduledStartTime = '" + schedStart + "' AND "
									+ "ActualTripStopInfo.Date = '" + Date + "' AND "
											+ "ActualTripStopInfo.StopNumber = " + stopNum;
			
			int rs = stmt.executeUpdate(updateStmt);
			
			if(rs == 0)
				System.out.println("Problem with update.\n");
			
			//cleanup
			stmt.close();
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void printSQL(ResultSet rs, ResultSetMetaData rsMeta) {
		String varColNames = "";
		int varColCount;
		try {
			varColCount = rsMeta.getColumnCount();
			for (int col = 1; col <= varColCount; col++) {
				System.out.printf("%-21s", rsMeta.getColumnName(col));
			}
			System.out.println(varColNames);
			
			// display column values
			while (rs.next()) {
				for (int col = 1; col <= varColCount; col++) {
					System.out.printf("%-21s", rs.getString(col));
				}
				System.out.println();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}