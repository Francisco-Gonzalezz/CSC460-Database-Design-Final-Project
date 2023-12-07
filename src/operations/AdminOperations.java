/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * @version 1.0
 * Class: AdminOperations.java
 * Implements: OperationsInteface
 * Purpose: Encapsulate operations that are specific to admin operations including seeing negative balances, and trainer's working
 *          hours.
 * 
 * Utilizes:
 *  - java,sql.Connection
 *  - java.time.Month
 *  - java.util.ArrayList
 *  - java.util.Collections
 *  - java.util.List
 *  - java.util.Map
 *  - java.util.Scanner
 *  - utils.CommonPrints
 *  - utils.DBUtils
 * 
 * Constructor: AdminOperations( Connection, Scanner ):
 *      - Connection to DB
 *      - Scanner to read user input
 * 
 * Methods:
 *  openMenu():
 *      - Opens the admin operation menu and prints the available options within that menu. Reads input from user
 *        and validates that it was displayed on screen. Then sends over to the functions necessary for the chosen operation.
 *  listNegativeBalanceMembers():
 *      - Will query database on all accounts whose balance is below zero and print them out to the console
 * showTrainerWorkingHours():
 *      - Queries the DB for the total amount of hours that a trainer is working for a month specified by the user
 * getInputFromUser():
 *      - Reads input from user through stdin and if they type anyforn of cancel the exit flag is set and thus when returned
 *        back to an operation the operation will terminate itself
 * 
 * Constants:
 *  - MAX_INTEGER_OPTION: Maximum valid integer option for user to select
 *  - MIN_INTEGER_OPTION: Minimum valid integer option for the user to select
 *  - EXIT: String that will input will be compared to, in order to set exitSignal flag
 *  - NEGATIVE_BALANCE_OPTION: Integer selection that will list negative balance accounts
 *  - TRAINER_HOURS_OPTION: Integer selection that will list all trainers working hours for month specified by user
 *  - RETURN_TO_MAIN_MENU_OPTION: Integer selection that will return control back to the main menu of the program
 * 
 * Global Variables:
 *  scanner: Scanner to read input from stdin from user
 *  dbConnection: Connection object that is currently connected to Oracle DB
 *  exitSignal: boolean that will signal a function to stop what it is doing and return to main menu
 */
package operations;

import java.sql.Connection;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import utils.CommonPrints;
import utils.DBUtils;

public class AdminOperations implements OperationsInterface {

    private static final int MAX_INTEGER_OPTION = 3;
    private static final int MIN_INTEGER_OPTION = 1;
    private static final String EXIT = "CANCEL";

    // Options 
    private static final int NEGATIVE_BALANCE_OPTION = 1;
    private static final int TRAINER_HOURS_OPTION = 2;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 3;

    private Scanner scanner;

    private Connection dbConnection;

    private boolean exitSignal;

    public AdminOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /**
     * Opens the menu for admin operations allowing user to select from the available actions.
     * Once user inputs a value it will be validated that it is a valid option and control will
     * be handed to whatever function corresponds to that operations
     */
    @Override
    public void openMenu() {
        System.out.println();
        exitSignal = false;
        CommonPrints.printAdminOperations();
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

            if ( option < MIN_INTEGER_OPTION || option > MAX_INTEGER_OPTION ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }

            break;
        }

        System.out.println();
        // Open menu that user selected
        switch ( option ) {
            case NEGATIVE_BALANCE_OPTION:
                listNegativeBalanceMembers();
                break;
            case TRAINER_HOURS_OPTION:
                showTrainerWorkingHours();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    /**
     * Function that will query the DB for accounts whose balance falls below zero. Once that is 
     * returned it will print out each member and their phone number to the console
     */
    private void listNegativeBalanceMembers() {
        System.out.println( "Members with negative balances (and their phone number)" );
        System.out.println( "-------------------------------------------------------" );
        Map<String, String> namesAndNums = DBUtils.getNegativeAccountUsers( dbConnection );
        if ( namesAndNums.isEmpty() ) {
            System.out.println( "There are zero negative balance accounts" );
            return;
        }
        List<String> memberName = new ArrayList<>( namesAndNums.keySet() );
        Collections.sort( memberName );
        for ( String name : memberName ) {
            System.out.println( name + " [" + namesAndNums.get( name ) + "]" );
        }
    }

    /**
     * Function that will query DB for the amount of hours that each trainer works in a given month specified
     * through user input. Will validate that the month is 1-12. Also handles case where there are no trainers working
     * the month given.
     */
    private void showTrainerWorkingHours() {
        System.out.println( "Trainer's working hours" );
        System.out.println( "----------------------------------------" );
        System.out.println( "Enter month to search for ( 1 for January and 12 for December etc. )" );
        System.out.println();
        String userInput = null;
        int month;
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                return;
            }

            try {
                month = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                System.out.println( "Enter 1-12 please" );
                continue;
            }

            if ( month < 0 || month > 12 ) {
                System.out.println( "Enter 1-12 please" );
                continue;
            }
            break;
        }
        System.out.println();
        Map<String, Float> trainerHours = DBUtils.getAllTrainersWorkinghours( month, dbConnection );
        String monthString = Month.of( month ).toString();
        if ( trainerHours.isEmpty() ) {
            System.out.println( "There are no trainers working in " + monthString );
            return;
        }
        System.out.println( "\nTrainer hours for " + monthString );
        System.out.println( "-----------------" );
        for ( String trainerName : trainerHours.keySet() ) {
            float hours = ( trainerHours.get( trainerName ) / 60 ) * 4;
            System.out.println( trainerName + " is working " + hours + " hours" );
        }
    }

    /**
     * Reads input in from stdin and sees if the user wants to cancel the current operation.
     * If so it sets the exitSignal flag so the function can exit on return
     * @return String value that the user entered in through stdin
     */
    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

}
