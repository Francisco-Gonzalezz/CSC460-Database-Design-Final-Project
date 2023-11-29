package utils;

/**
 * The purpose of this class is to encapsulate all the "cleanup code" for when the program exits
 * @author Francisco Gonzalez
 * @version 1.0
 */

import java.sql.Connection;
import java.sql.SQLException;

public class ExitThread extends Thread {

    private Connection dbConnection; // Connection to close

    public ExitThread( Connection dbConnection ) {
        this.dbConnection = dbConnection;
    }

    @Override
    public void run() {
        PrintUtils.printExitMessage();
        try {
            dbConnection.close();
        } catch ( SQLException e ) {
            System.err.println( "Unable to close the connection to the DB" );
        }
    }

}
