/**
 * @author Francisco Gonzalez
 * Class: ExitThread.java
 * Purpose: Thread to run cleanup tasks when JVM is signalled to stop. Cleans up resources such as the connection to the database and closes the scanner that was used throughout the application
 * 
 * Extends the Thread class
 * Utilizes:
 *  - java.sql.Connection
 *  - java.sql.SQLException
 *  - java.util.Scanner
 * 
 * Constructor( Connection, Scanner ):
 *          Give the two resources that need to be closed at the end of the application
 * 
 * Methods:
 *  - run()
 *      The code to be run when the thread is told to start ( which is at exit )
 * 
 */
package exit_thread;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import utils.CommonPrints;

public class ExitThread extends Thread {

    private Connection dbConnection; // Connection to close

    private Scanner scanner; // Object for user input

    public ExitThread( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

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
