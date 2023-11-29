package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;
import utils.ValidationUtils;

public class MemberOperations implements OperationsInterface {

    private static final int MAX_MEMBER_OPERATIONS = 3; // Max valid integer option for member menu
    private static final int MIN_OPERATIONS = 1; // Min valid integer option for member menu
    private static final int SMALLEST_MEMBER_ID = 1; // Smallest member id should be 1

    // Values of each of the member operations
    private static final int ADD_MEMBER_OPTION = 1;
    private static final int REMOVE_MEMBER_OPTION = 2;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 3;
    private static final String EXIT = "CANCEL";

    private Scanner scanner;

    private Connection dbConnection;

    private boolean exitSignal;

    public MemberOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /**
     * Open menu for member operations and sends program control to given operation
     */
    public void openMenu() {
        exitSignal = false;
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
        System.out.println( "New Member Wizard ( Type 'Cancel' at anytime to cancel member creation )" );
        System.out.println( "----------------------------------------------------------------------" );
        String firstName = getNameFromUser( true );
        if ( exitSignal ) {
            CommonPrints.printMemberCreationCancelled();
            return;
        }
        String lastName = getNameFromUser( false );
        if ( exitSignal ) {
            CommonPrints.printMemberCreationCancelled();
            return;
        }
        String phoneNumber = getPhoneNumberFromUser();
        if ( exitSignal ) {
            CommonPrints.printMemberCreationCancelled();
            return;
        }
        String email = getEmailFromUser();
        if ( exitSignal ) {
            CommonPrints.printMemberCreationCancelled();
            return;
        }
    }

    private void openRemoveMemberWizard() {
        long memberID = getMemberIDFromUser();
    }

    // Functions to grab input from user

    private String readInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

    private String getEmailFromUser() {
        System.out.println( "Enter new member's email address" );
        String email = "";
        email = readInputFromUser();
        while ( !ValidationUtils.validateEmail( email ) ) {
            System.out.println( "Please enter a valid email" );
            email = readInputFromUser();
            if ( exitSignal ) {
                return null;
            }
        }

        return email;
    }

    private String getPhoneNumberFromUser() {
        System.out.println( "Enter new member's phone number" );
        String phoneNumber = "";
        phoneNumber = readInputFromUser();
        while ( !ValidationUtils.validatePhoneNumber( phoneNumber ) ) {
            System.out.println( "Please enter a valid phone nubmer" );
            phoneNumber = readInputFromUser();
            if ( exitSignal ) {
                return null;
            }
        }
        return phoneNumber;
    }

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
        name = readInputFromUser();
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
