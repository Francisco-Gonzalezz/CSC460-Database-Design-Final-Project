
/**
 * TODO: Complete the header for this file
 * @author Francisco Gonzalez
 * @author Jake Bode
 */

import java.sql.*;

public class Program4 {

    private static final String jdbcURL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

    public static void main( String[] args ) {
        // Get the username and password and verify that were set with a command line argument
        String username = CommandLineArgumentsUtils.getUsername( args );
        String password = CommandLineArgumentsUtils.getPassword( args );

        if ( username == null || password == null ) {
            System.out.println( "\nUsage: Program4 <-u username> <-p password>\n" );
            System.out.println( "Options:" );
            System.out.println( "\t-u Username to login with to oracle database" );
            System.out.println( "\t-p Password to login with to oracle database" );
            System.out.println();
            System.exit( 1 );
        }

        // Load the Oracle JDBC Driver
        try {
            Class.forName( "oracle.jdbc.OracleDriver" );
        } catch ( ClassNotFoundException e ) {
            System.err.println( "Unable to load the JDBC Driver. Ensure that the JDBC driver is on the classpath" );
            System.exit( 1 );
        }

        Connection dbConnection = null;
        try {
            dbConnection = DriverManager.getConnection(jdbcURL, username, password );
        } catch ( SQLException e ) {
            System.out.println("Unable to connect to DB.");
            System.out.println("Check username/password");
            System.exit(1);
        }

    	try {	
	dbConnection.close();
	} catch ( SQLException e ) {
		System.out.println("Unable to close connection");
	}
    }

}
