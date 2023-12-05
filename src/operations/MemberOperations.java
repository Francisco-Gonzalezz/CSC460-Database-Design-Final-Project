package operations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;

import entities.GymMember;
import entities.Transaction;
import utils.CommonPrints;
import utils.DBUtils;
import utils.ValidationUtils;

public class MemberOperations implements OperationsInterface {

    private static final int MAX_MEMBER_OPERATIONS = 6; // Max valid integer option for member menu
    private static final int MIN_OPERATIONS = 1; // Min valid integer option for member menu
    private static final int SMALLEST_MEMBER_ID = 1; // Smallest member id should be 1

    // Values of each of the member operations
    private static final int ADD_MEMBER_OPTION = 1;
    private static final int REMOVE_MEMBER_OPTION = 2;
    private static final int PURCHASE_PACKAGE_OPTION = 3;
    private static final int ADD_FUNDS_OPTION = 4;
    private static final int CHECK_MEMBER_SCHEDULE_OPTION = 5;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 6;
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
            case PURCHASE_PACKAGE_OPTION:
                memberPackagePurchase();
                break;
            case ADD_FUNDS_OPTION:
                rechargeFunds();
                break;
            case CHECK_MEMBER_SCHEDULE_OPTION:
                openMemberClassScheduleSearch();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    private void rechargeFunds() {
        // Get gym member information
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

        System.out.println( "\nCurrent account balance: $" + member.getBalance() );
        System.out.println( "How much money would you like to recharge with?" );
        // Enter amount of funds that they want to put into their account
        float rechargeAmount = 0;
        while ( true ) {
            String input = scanner.nextLine();
            try {
                rechargeAmount = Float.valueOf( input );
            } catch ( NumberFormatException e ) {
                System.out.println( "Please enter a numeric value" );
                continue;
            }

            if ( rechargeAmount < 0 ) {
                System.out.println( "Recharge number must be positive" );
                continue;
            }
            break;
        }
        makePurchaseOrRecharge( member, rechargeAmount );
    }

    private void makePurchaseOrRecharge( GymMember member, float amount ) {
        // Update member balance and save change
        member.setBalance( member.getBalance() + amount );
        DBUtils.saveChangesToMember( member, dbConnection );
        createTransaction( member, amount, dbConnection );
    }

    private void createTransaction( GymMember member, float amount, Connection dbConnection ) {
        int generatedID = DBUtils.generateIDNumberFromSequence( dbConnection );
        String transactionType = null;
        if ( amount < 0 ) {
            transactionType = "PURCHASE";
        } else {
            transactionType = "RECHARGE";
        }

        // Create the transaction entity
        Transaction transaction = new Transaction(
            generatedID,
            member.getMemberID(),
            transactionType,
            new Date( System.currentTimeMillis() ),
            amount );
        DBUtils.saveNewTransaction( transaction, dbConnection );
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

    private void memberPackagePurchase() {
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

        promptUserForPackagePurchase( member );
    }

    private void promptUserForPackagePurchase( GymMember member ) {
        System.out.println( "\nSelect a package for user to purchase ( Type name of package )" );
        System.out.println( "---------------------------------------------------------------" );
        Map<String, Float> packages = DBUtils.getPackagesAndPrices( dbConnection );
        if ( packages.isEmpty() ) {
            System.err.println( "Unable to find any packages. Cancelling purchase." );
            return;
        }
        // Print out the options
        for ( String packageName : packages.keySet() ) {
            float cost = packages.get( packageName );
            System.out.println( packageName + "  $" + cost );
        }
        String userInput = null;
        System.out.println();
        while ( true ) {
            userInput = scanner.nextLine();
            if ( !packages.containsKey( userInput ) ) {
                System.out.println( "Please enter a choice from above." );
                continue;
            }
            break;
        }
        float cost = packages.get( userInput );
        cost = -cost;
        makePurchaseOrRecharge( member, (float) cost );
        DBUtils.saveChangesToMember( member, dbConnection ); // Save changes made to the member object

        DBUtils.addMemberToPackageCourses( member, userInput, dbConnection );
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

        // Check user for negative balance
        if ( member.getBalance() < 0 ) {
            System.out
                .println( "Member must pay $" + member.getBalance() + " before they can cancel their membership" );
            return;
        }

        // Update rental items ( No need since qty is going to be perma gone and already accounted for)

        // Update class tables where this member was a part of
        DBUtils.removeMemberFromAllTheirClasses( member.getMemberID(), dbConnection );

        // Remove member from member table
        DBUtils.removeMemberFromDB( member, dbConnection );
    }

    private void openMemberClassScheduleSearch() {
        System.out.println( "Member Schedule Search Wizard ( Type 'Cancel' to exit wizard )" );
        System.out.println( "--------------------------------------------------------------" );
        int memberID;
        String userInput;

        // Get member object from db
        GymMember member = null;
        while ( member == null ) {
            System.out.println( "Enter member ID" );
            userInput = readInputFromUser();
            if ( exitSignal ) {
                return;
            }
            try {
                memberID = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                System.out.println( "ID must be a numeric value" );
                continue;
            }

            member = DBUtils.retrieveMemberFromID( memberID, dbConnection );
            if ( member == null ) {
                System.out.println( "Invalid ID please enter again" );
                continue;
            }
            break;
        }

        System.out.println( "Enter month to search for ( 1 for January and 12 for December etc. )" );
        userInput = null;
        int month;
        while ( true ) {
            userInput = readInputFromUser();
            if ( exitSignal ) {
                return;
            }

            try {
                month = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                System.out.println( "Enter 1-12 please" );
                continue;
            }

            if ( month < 1 || month > 12 ) {
                System.out.println( "Enter 1-12 please" );
                continue;
            }
            break;
        }
        DateFormat formatter = new SimpleDateFormat( "EEEE" );
        System.out.println( "Schedule for " + member.getFullName() + "\n" );
        Map<Timestamp, Float> schedule = DBUtils.getMemberScheduleForMonth( member, month, dbConnection );
        for ( Timestamp startTime : schedule.keySet() ) {
            String startAMPM = "AM";
            String endAMPM = "AM";
            int startHour = startTime.toInstant().atZone( TimeZone.getDefault().toZoneId() ).getHour();
            if ( startHour > 12 ) {
                startHour -= 11;
                startAMPM = "PM";
            }
            float duration = schedule.get( startTime ) / 60;
            int endHour = (int) ( startHour + duration );
            if ( endHour > 12 ) {
                endHour -= 11;
                endAMPM = "PM";
            }
            String dayOfWeek = formatter.format( startTime );
            System.out.println( dayOfWeek + " " + startHour + startAMPM + " - " + endHour + endAMPM );
        }

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
