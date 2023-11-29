import java.sql.Connection;
import java.util.Scanner;

public class CommandLineGui {

    private Connection dbConnection; // Connection to the oracle db

    private Scanner scanner; // Scanner to read from stdin

    private Operations operations;

    /**
     * Creates a new instance of a CommandLineGui and access to a SQL DB Connection
     * @param dbConnection
     * @return a new instance of CommandLineGui
     */
    public CommandLineGui( Connection dbConnection ) {
        this.dbConnection = dbConnection;
        scanner = new Scanner( System.in );
        operations = new Operations( this.dbConnection, this.scanner );
    }

    /**
     * Starts the command line interface to interact with the gym database
     */
    public void startGui() {
        PrintUtils.printWelcomeMessage();
        System.out.println( "\n" );

        while ( true ) {
            PrintUtils.promptUserToSelectTypeOfOperation();
            PrintUtils.printStandardOptions();
            getOperationSelection();
        }
    }

    /**
     * Prints the options that the user can select and take input from stdin for the selection
     */
    private void getOperationSelection() {
        String userInput = null; // String form of the userinput
        int option; // Will convert the string input to be an int
        while ( true ) {
            System.out.println();
            userInput = scanner.nextLine();
            try {
                option = Integer.valueOf( userInput );
                break;
            } catch ( NumberFormatException e ) {
                System.out.println( "\nPlease select one of the available options from above.\n" );
            }
        }
        openOperationsMenu( option );
    }

    /**
     * Directs control flow to whatever operations menu should be open
     * @param option
     */
    private void openOperationsMenu( int option ) {
        switch ( option ) {
            case 1:
                operations.openMemberOperations();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                scanner.close();
                System.exit( 0 );
        }
    }

}
