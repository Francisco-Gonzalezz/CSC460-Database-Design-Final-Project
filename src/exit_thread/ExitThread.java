package exit_thread;

/**
 * The purpose of this class is to encapsulate all the "cleanup code" for when the program exits
 * @author Francisco Gonzalez
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import utils.CommonPrints;

public class ExitThread extends Thread {

    private Connection dbConnection; // Connection to close

    private Scanner scanner;

    /* 
     * This constructor sets the values of the instance variables for
     *  the Connection object and the java.util Scanner
     */
    public ExitThread( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /*
     * This method is run when the program is exited, in order to close
     *  the user input Scanner and the Oracle database connection
     */
    @Override
    public void run() {
        CommonPrints.printExitMessage(); // Print exit message
        scanner.close(); // Close the scanner used for the program
        try {
            dbConnection.close(); // Close DB Connection
        } catch ( SQLException e ) {
            System.err.println( "Unable to close the connection to the DB" );
        }
    }

}
