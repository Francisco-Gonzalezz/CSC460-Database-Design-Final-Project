/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * 
 * Class: CommandLineGui.java
 * Purpose: This is the point interaction between the DB and the User. This will act as the "Main Menu" for the program.
 * This will direct flow of control to different operation objects so that different actions can be done. This is also the central
 * place to terminate the program cleanly.
 * 
 * Utilizes:
 *  - java.sql.Connection
 *  - java.util.Scanner
 * 
 * Constructor: CommandLineGui(Connection)
 *  - Will use the single connection to database throughout all the operations that are being called
 * 
 * Methods:
 *  startGui():
 *      - Starts the application loop for the program and prints the diffrent operations types
 *  getOperationSelection():
 *      - Reads input from the user and validates the the input is a valid input from the options presented to the user.
 *        Will then send the option over to the openOperationsMenu(int option) function.
 *  openOperationsMenu( int ):
 *      - This menu will use the numeric option given to it and open up that operations menu 
 * 
 * Constants:
 *  - MIN_OPTIONS: The minimum value that can be entered
 *  - MAX_MAIN_MENU_OPTIONS: The maximum valid integer value to be entered
 *  - MEMBER_MENU_OPTION: Integer value to enter the member opertaions menu
 *  - COURSE_MENU_OPTION: Integer value to enter the course/class operations menu
 *  - PACKAGE_MENU_OPTION: Integer value to enter the package operations menu
 *  - RENTAL_MENU_OPTION: Integer value to enter the rental operations menu
 *  - ADMIN_MENU_OPTION: Integer value to enter the admin operations menu
 *  - EXIT_MENU_OPTION: Integer value to exit the program
 * 
 * Global Variables:
 *  - memberOperations: Object containing possible member actions
 *  - courseOperations: Object containing possible course/class operations
 *  - adminOperations: Object containing possible admin operations
 *  - packageOperations: Object containg posssible package operations
 *  - rentalOperations: Object containing possible rental operations
 */
package gui;

/*
 * Class name: CommandLineGui
 * Author: Frankie Gonzalez
 * Utilizes java.sql, java.util
 * Purpose: This class describes the user interface model used in the program,
 *  including the menu selection and displaying the user interface upon running
 *  the program.
 * Constructor: CommandLineGui(Connection dbConnection) takes the connection to
 *  Oracle as input and constructs the Scanner object for user input, also
 *  setting up the Exit thread to clean up the program upon exit.
 * Public methods: startGui() - called when the program is run and the connection
 *  to the database is established, sets up the user interface
 */

import java.sql.Connection;
import java.util.Scanner;

import exit_thread.ExitThread;
import operations.AdminOperations;
import operations.CourseOperations;
import operations.MemberOperations;
import operations.PackageOperations;
import operations.RentalOperations;
import utils.CommonPrints;

public class CommandLineGui {

    // Static integers for main menu options
    private static final int MIN_OPTIONS = 1; // Miniumn integer option for every menu
    private static final int MAX_MAIN_MENU_OPTIONS = 6; // Maximum integer option for the main menu
    private static final int MEMBER_MENU_OPTION = 1;
    private static final int COURSE_MENU_OPTION = 2;
    private static final int PACKAGE_MENU_OPTION = 3;
    private static final int RENTAL_MENU_OPTION = 4;
    private static final int ADMIN_MENU_OPTION = 5;
    private static final int EXIT_MENU_OPTION = 6;

    private Scanner scanner; // Scanner to read from stdin

    // Operations that can be performed
    private MemberOperations memberOperations;
    private CourseOperations courseOperations;
    private AdminOperations adminOperations;
    private PackageOperations packageOperations;
    private RentalOperations rentalOperations;

    /**
     * Creates a new instance of a CommandLineGui and access to a SQL DB Connection
     * Also sets the runtime thread to close resources that need to be freed at the end of the program
     * @param dbConnection
     * @return a new instance of CommandLineGui
     */
    public CommandLineGui( Connection dbConnection ) {
        scanner = new Scanner( System.in );

        // Create the operations objects that will be needed throughout the front end program
        memberOperations = new MemberOperations( dbConnection, scanner );
        courseOperations = new CourseOperations( dbConnection, scanner );
        adminOperations = new AdminOperations( dbConnection, scanner );
        packageOperations = new PackageOperations( dbConnection, scanner );
        rentalOperations = new RentalOperations( dbConnection, scanner );

        // Give objects that need to be closed to the ExitThread to free resources at the end
        Runtime.getRuntime().addShutdownHook( new ExitThread( dbConnection, scanner ) );
    }

    /**
     * Starts the command line interface to interact with the gym database
     */
    public void startGui() {
        CommonPrints.printWelcomeMessage();
        System.out.println( "\n" );

	// continually prompts for user input until program is exited
        while ( true ) {
            CommonPrints.promptUserToSelectTypeOfOperation();
            CommonPrints.printStandardOptions();
            getOperationSelection();
        }
    }

    /**
     * Prints the options that the user can select and take input from stdin for the selection
     */
    private void getOperationSelection() {
        String userInput = null; // String form of the userinput
        int option = 0; // Will convert the string input to be an int
        while ( true ) {
            System.out.println();
            userInput = scanner.nextLine();
            try {
                option = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                CommonPrints.printInvalidOptionMessage();
            }

            if ( option < MIN_OPTIONS || option > MAX_MAIN_MENU_OPTIONS ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            openOperationsMenu( option );
            break;
        }
    }

    /**
     * Directs control flow to whatever operations menu should be open
     * @param option - integer value for which menu should be opened
     *  from the start menu
     */
    private void openOperationsMenu( int option ) {
        switch ( option ) {
            case MEMBER_MENU_OPTION:
                memberOperations.openMenu();
                break;
            case COURSE_MENU_OPTION:
                courseOperations.openMenu();
                break;
            case PACKAGE_MENU_OPTION:
                packageOperations.openMenu();
                break;
            case RENTAL_MENU_OPTION:
                rentalOperations.openMenu();
                break;
            case ADMIN_MENU_OPTION:
                adminOperations.openMenu();
                break;
            case EXIT_MENU_OPTION:
                System.exit( 0 );
        }
    }

}
