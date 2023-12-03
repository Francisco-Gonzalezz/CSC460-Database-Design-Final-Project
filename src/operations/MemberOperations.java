package operations;

import java.sql.Connection;
import java.util.Map;
import java.util.Scanner;

import entities.GymMember;
import utils.CommonPrints;
import utils.DBUtils;
import utils.ValidationUtils;

public class MemberOperations implements OperationsInterface {

    private static final int MAX_MEMBER_OPERATIONS = 4; // Max valid integer option for member menu
    private static final int MIN_OPERATIONS = 1; // Min valid integer option for member menu
    private static final int SMALLEST_MEMBER_ID = 1; // Smallest member id should be 1

    // Values of each of the member operations
    private static final int ADD_MEMBER_OPTION = 1;
    private static final int REMOVE_MEMBER_OPTION = 2;
    private static final int CHECK_MEMBER_SCHEDULE_OPTION = 3;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 4;
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
        System.out.println();
        exitSignal = false;
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

        System.out.println();
        // Select which operation to do
        switch ( option ) {
            case ADD_MEMBER_OPTION:
                openAddMemberWizard();
                break;
            case REMOVE_MEMBER_OPTION:
                openRemoveMemberWizard();
                break;
            case CHECK_MEMBER_SCHEDULE_OPTION:
                openMemberClassScheduleSearch();
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
        // Store as object to keep all info in one place for DB insertion
        GymMember newMember = new GymMember( firstName, lastName, phoneNumber, email );
        boolean returnCode = DBUtils.addNewGymMemberToDB( newMember, dbConnection ); // Add the member to the DB
        if ( !returnCode ) {
            System.out.println( "\nERROR: member creation FAILED" );
            return;
        }

        promptUserForPackagePurchase( newMember );

        System.out.println( "\nThe new member's ID is: " + newMember.getMemberID() );
    }

    private void promptUserForPackagePurchase( GymMember member ) {
        System.out.println( "\nSelect a package for user to purchase ( Type name of package )" );
        System.out.println( "---------------------------------------------------------------" );
        Map<String, Float> packages = DBUtils.getPackagesAndPrices( dbConnection );
        if ( packages.isEmpty() ) {
            System.err.println( "Unable to find any packages. Cancelling purchase." );
            return;
        }
        for ( String packageName : packages.keySet() ) {
            float cost = packages.get( packageName );
            System.out.println( packageName + "\t$" + cost );
        }
        String userInput = null;
        while ( true ) {
            userInput = scanner.nextLine();
            if ( !packages.containsKey( userInput ) ) {
                System.out.println( "Please enter a choice from above." );
                continue;
            }
            break;
        }

        // TODO: Update tables showing that the member purchased this package

    }

    private void openRemoveMemberWizard() {
        System.out.println( "Remove Member Wizard ( Type 'Cancel' at anytime to cancel member deletion )" );
        System.out.println( "---------------------------------------------------------------------------" );
        GymMember member = null;
        while ( member == null ) {
            int memberID = getMemberIDFromUser();
            if ( exitSignal ) {
                System.out.println( "Cancelling member deletion" );
                return;
            }
            member = DBUtils.retrieveMemberFromID( memberID, dbConnection );
            if ( member == null ) {
                System.out.println( "Invalid member id. Verify that id was typed in correctly" );
            }
        }
        System.out.println( member.toString() );

        // Check user for negative balance
        if ( member.getBalance() < 0 ) {
            System.out
                .println( "Member must pay $" + member.getBalance() + " before they can cancel their membership" );
            return;
        }

        // Update rental items
        Map<String, Integer> unreturnedRentalsOfMember = DBUtils
            .getCheckoutRentalsForMember( member.getMemberID(), dbConnection );
        for ( String rental : unreturnedRentalsOfMember.keySet() ) {
            int quantityToSubtract = unreturnedRentalsOfMember.get( rental );
            DBUtils.removeQuantityFromRentalItems( rental, quantityToSubtract, dbConnection );
        }

        // Update class tables where this member was a part of
        DBUtils.removeMemberFromAllTheirClasses( member.getMemberID(), dbConnection );

        // Remove member from member table
        DBUtils.removeMemberFromDB( member, dbConnection );
    }

    private void openMemberClassScheduleSearch() {
        long memberID = getMemberIDFromUser();

        // TODO: implement 
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
     * Prompts the user to get the member's first or last name.
     * Will allow for any name except an empty one
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

        // Loop until name is valid
        while ( name.isEmpty() ) {
            String userInput = readInputFromUser();
            if ( !userInput.isEmpty() ) {
                name = userInput;
            } else {
                System.out.println( "Please enter a non-empty name" );
            }
        }

        return name;
    }

    /**
    * Prompts user to enter a member id from stdin and validates that it could be a valid
    * member id
    * @return Member ID as an integer
    */
    private int getMemberIDFromUser() {
        System.out.println( "Enter the member id" );
        String userInputMemberID = null;
        int memberID;
        while ( true ) {
            System.out.println();
            userInputMemberID = readInputFromUser();

            // Check to see input can be turned into an integer
            try {
                memberID = Integer.valueOf( userInputMemberID );
            } catch ( NumberFormatException e ) {
                if ( exitSignal ) {
                    return -1;
                }
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
