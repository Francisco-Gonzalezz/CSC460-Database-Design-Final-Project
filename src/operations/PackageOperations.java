/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * 
 * Class: PackageOperations.java
 * Implements: OperationsInterface
 * 
 * Utilizes:
 *  - java.sql.Connection
 *  - java.util.Scanner
 *  - utils.CommonPrints
 * 
 * Constructor: PackageOperations( Connection, Scanner ):
 *      - Connection to DB
 *      - Scanner to read input from stdin
 * 
 * Methods:
 *   openMenu():
 *       - Opens the menu for the operations that deal with packages, such as creating and deleting a package
 * 
 * Constants:
 *  - MINIMUM_INTEGER_OPTION: Min valid integer option
 *  - MAX_INTEGER_OPTIONL Max valid integer option
 *  - EXIT: String that will cancel the current operation
 *  - ADD_PACKAGE_OPTION: Integer option to add packages
 *  - UPDATE_PACKAGE_OPTION: Integer option to update a current package
 *  - REMOVE_PACKAGE_OPTION: Integer option to remove an existing package
 *  - RETURN_TO_MAIN_MENU_OPTION: Integer option to return to main menu
 * 
 * Global Variables
 *  - dbConnection: Connection to the DB
 *  - exitSignal: Boolean value that will signal to stop the current operation
 *  - scanner: Scanner object used to read from stdin
 */
package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;

public class PackageOperations implements OperationsInterface {

    private static final int MINIMUM_INTEGER_OPTION = 1;
    private static final int MAX_INTEGER_OPTION = 4;
    private static final String EXIT = "CANCEL";

    // Options
    private static final int ADD_PACKAGE_OPTION = 1;
    private static final int UPDATE_PACKAGE_OPTION = 2;
    private static final int REMOVE_PACKAGE_OPTION = 3;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 4;

    private Scanner scanner;

    private Connection dbConnection;

    private boolean exitSignal;

    public PackageOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /**
     * Opens the menu for package operations. Will print the available operations that can be performed from the menu
     * and hand program control to functions who complete the task the user wants
     */
    @Override
    public void openMenu() {
        System.out.println();
        exitSignal = false;
        CommonPrints.printPackageOperations();
        String userInput = null;
        int option;
        while ( true ) {
            System.out.println();
            userInput = scanner.nextLine();
            try {
                option = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }

            if ( option < MINIMUM_INTEGER_OPTION || option > MAX_INTEGER_OPTION ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            break;
        }

        System.out.println();

        switch ( option ) {
            case ADD_PACKAGE_OPTION:
                openNewPackageWizard();
                break;
            case UPDATE_PACKAGE_OPTION:
                openUpdatePackageWizard();
                break;
            case REMOVE_PACKAGE_OPTION:
                openRemovePackageWizard();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    /**
     * Takes user through the process of creating a new package. Will read a package name from the user,
     * the cost of the package, and the courses that will be in that package.
     */
    private void openNewPackageWizard() {
        System.out.println( "New Course Package Wizard ( Type 'Cancel' at anytime to cancel package creation )" );
        System.out.println( "---------------------------------------------------------------------------------" );
    }

    /**
     * Takes user through the process of updating a package
     */
    private void openUpdatePackageWizard() {
        System.out.println( "Update Course Package Wizard ( Type 'Cancel' at anytime to cancel package update )" );
        System.out.println( "----------------------------------------------------------------------------------" );
    }

    /**
     * Takes user through the process of removing an existing package
     */
    private void openRemovePackageWizard() {
        System.out.println( "Remove Course Package Wizard ( Type 'Cancel' at anytime to cancel package removal )" );
        System.out.println( "-----------------------------------------------------------------------------------" );
    }

    /**
     * Reads input from the user and sets the exit flag to true if the 
     * user types cancel
     * @return What the user typed with a string
     */
    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }
}
