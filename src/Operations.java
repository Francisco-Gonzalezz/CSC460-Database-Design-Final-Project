import java.sql.Connection;
import java.util.Scanner;

public class Operations {

    private Connection dbConnection;

    private Scanner scanner;

    public Operations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /**
     * Open menu for member operations and sends program control to given operation
     */
    public void openMemberOperations() {
        System.out.println();
        PrintUtils.printGymMemberOperations();
        String userInput = null;
        int option;
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

        // Select which operation to do
        switch ( option ) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
        }

        System.out.println();
    }

}
