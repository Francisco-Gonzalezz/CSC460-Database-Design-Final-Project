
/**
 * TODO: Complete the header for this file
 * @author Francisco Gonzalez
 * @author Jake Bode
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import gui.CommandLineGui;
import utils.CommandLineArgumentsUtils;

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

        // Grab connection to the DB
        Connection dbConnection = null;
        try {
            dbConnection = DriverManager.getConnection( jdbcURL, username, password );
            dbConnection.setAutoCommit( true );
        } catch ( SQLException e ) {
            System.out.println( "Unable to connect to DB." );
            System.out.println( "Check username/password" );
            System.exit( 1 );
        }

        // Start the cli
        CommandLineGui gui = new CommandLineGui( dbConnection );
        gui.startGui();
    }

}
