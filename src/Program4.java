
/**
 * @author Francisco Gonzalez, Jake Bode
 * CSC 460, Program 4
 * Instructor: Dr. McCann
 * TAs: Daniel Bazmandeh, Zhenyu Qi
 * Due: December 5, 2023
 * Description: This file is the main file for Program 4. This program uses the
 *  Oracle DB connection, with our database schema outlined in the design.pdf
 *  document within this submission, in order to execute queries on the GYM
 *  460 schema outlined in the project handout. In brief, the program deals
 *  with gym membership for gym participants, rental of items at the gym,
 *  special courses offered by the gym, as well as membership deals and
 *  discounts. The program allows administrative tasks including adding and
 *  deleting members from the system, adding and deleting courses from the
 *  schedule, and adding/updating/deleting course packages that can be
 *  purchased by members. Special queries:
 *   - Listing all member with negative account balance.
 *   - Checking member class schedule for a given month.
 *   - Checking a trainer's working hours for December.
 *   - Listing all unreturned items that a user has checked out from the rental
 *     center.
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
