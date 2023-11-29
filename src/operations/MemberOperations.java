package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;

public class MemberOperations implements OperationsInterface {

    private static final int MAX_MEMBER_OPERATIONS = 3; // Max valid integer option for member menu
    private static final int MIN_OPERATIONS = 1; // Min valid integer option for member menu
    private static final int SMALLEST_MEMBER_ID = 1; // Smallest member id should be 1

    // Values of each of the member operations
    private static final int ADD_MEMBER_OPTION = 1;
    private static final int REMOVE_MEMBER_OPTION = 2;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 3;

    private Scanner scanner;

    private Connection dbConnection;

    public MemberOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /**
     * Open menu for member operations and sends program control to given operation
     */
    public void openMenu() {
        System.out.println();
        CommonPrints.printGymMemberOperations();
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

            if ( option < MIN_OPERATIONS || option > MAX_MEMBER_OPERATIONS ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            break;
        }

        // Select which operation to do
        switch ( option ) {
            case ADD_MEMBER_OPTION:
                openAddMemberWizard();
                break;
            case REMOVE_MEMBER_OPTION:
                openRemoveMemberWizard();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    /**
     * Goes through the process of adding a new member to DB
     */
    private void openAddMemberWizard() {
        // TODO: Need to add way to exit from the wizard without finishing registartion
        System.out.println( "New Member Wizard" );
        System.out.println( "-----------------" );
        String firstName = getNameFromUser( true );
        String lastName = getNameFromUser( false );
    }

    private void openRemoveMemberWizard() {
        long memberID = getMemberIDFromUser();
    }

    // Functions to grab input from user

    /**
     * Prompts the user to get the member's first or last name
     * @param firstName true to prompt for first name false for last name
     * @return First or Last name as a String
     */
    private String getNameFromUser( boolean firstName ) {
        String name = "";
        if ( firstName ) {
            System.out.println( "Enter new member's first name" );
        } else {
            System.out.println( "Enter the new member's last name" );
        }
        name = scanner.nextLine();
        return name;
    }

    /**
    * Prompts user to enter a member id from stdin and validates that it could be a valid
    * member id
    * @return Member ID as an integer
    */
    private long getMemberIDFromUser() {
        System.out.println( "Enter the member id" );
        String userInputMemberID = null;
        long memberID = 0;
        while ( true ) {
            System.out.println();
            userInputMemberID = scanner.nextLine();

            // Check to see input can be turned into an integer
            try {
                memberID = Long.valueOf( userInputMemberID );
            } catch ( NumberFormatException e ) {
                System.out.println( "Member ID should only contain numeric values" );
                continue;
            }

            // Check validity of integer
            if ( memberID < SMALLEST_MEMBER_ID || memberID > Integer.MAX_VALUE ) {
                System.out.println( "Member ID must be a positive number and smaller than Java's max integer value" );
                continue;
            }
            break;
        }

        return memberID;
    }

}
