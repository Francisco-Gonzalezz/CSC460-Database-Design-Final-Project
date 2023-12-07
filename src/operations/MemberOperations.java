/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * 
 * Class: MemberOperations.java
 * Implements: OperationsInterface
 * Purpose: Encapsulate operations that are specific to individual member operations. Such as member creation, member deletion, checking
 *          member schedules, and purchasing new packages.
 * 
 * Utilizes:
 *  - java.sql.Connection
 *  - java.sql.Date
 *  - java.sql.Timestamp
 *  - java.text.DateFormat
 *  - java.text.SimpleDateFormat
 *  - java.util.Map
 *  - java.util.Scanner
 *  - java.util.Timezone
 *  - entities.GymMember
 *  - entities.Transaction
 *  - utils.DBUtils
 *  - utils.CommonPrints
 *  - utils.ValidationUtils
 * 
 * Constructor: MemberOperations( Connection, Scanner ):
 *  - Connection to DB
 *  - Scanner to read input from stdin
 * 
 * Methods:
 *  openMenu():
 *      - Opens the member operations memu and prints the available options within the displayed menu. Reads input from user
 *        and validates that it was one of the options the user could currently choose from. Then directs control of program
 *        over to coresponding functions of the option chosen.
 * rechargeFunds():
 *      - Starts the proccess of a member paying off any bills they may have or just adding money in anticipation of buying another
 *        package.
 * makePurchaseOrRecharge(float amount):
 *      - Determines if transaction was a purchase or a recharge based on the sign of the amount passed in. Will
 *        then call function to create a Transaction entity within the DB and save it.
 * createTransaction( GymMember member, float amount, Connection dbConnection ):
 *      - This function will create a new Transaction entity for the DB to store with relevant information like member id, amount, etc.
 *        Will call database function to save the object into the DB
 * openAddMemberWizard():
 *      - Takes user through the creation of a new member where various information is asked such as name, phone number, email. This is then
 *         placed into a GymMember object where it is sent to DBUtils to be saved into the database.
 * memberPackagePurchase():
 *      - Goes through the process of a member purchasing a new package where they are asked for their member id
 *        then are prompted with the available packages. This function then calls makePurchaseOrRecharge where it creates
 *        a transaction and subtracts the amount of the package from the member current balance.
 * promptUserForPackagePurchase():
 *      - Will prompt user to which package they would like to purchase after listing them all out and their prices. The prices will be
 *        displayed with the member current discount level prices
 * openRemoveMemberWizard():
 *      - Takes the user through the process of deleting a member, will then unenroll member from all current class if they are at a positive balance
 *        for their account. The class enrollment will be updated.
 * openMemberClassScheduleSearch():
 *      - Will prompt user for a member id and what month to check the schedule for. Will then display the days of the week within that month they are
 *        in a class and from their start time to their end time.
 * getEmailFromUser():
 *      - Will continually prompt user for an email, then it will validate that the email is in a valid form and return the string
 *        back if it is valid
 * getPhoneNumberFromUser():
 *      - Asks user for phone number and validates that it could be a valid phone number. Returns the number as a string
 * getNameFromUser():
 *      - Asks user for first and last name, makes sure that an empty string is not entered.
 * getMemberIDFromUser():
 *      - Asks user for the member id and verifies that it could potentially be a valid id 
 * 
 * 
 * Constants:
 *  - MAX_MEMBER_OPERATIONS: Max valid integer option that the user can select
 *  - MIN_OPERATIONS: Min valid integer that a user can input
 *  - SMALLEST_MEMBER_ID: Smallest possible member id that can be created
 *  - ADD_MEMBER_OPTION: Integer option to start creating a member
 *  - REMOVE_MEMBER_OPTION: Integer option to start removing a member
 *  - PURCHASE_PACKAGE_OPTION: Integer option to start purchasing a package
 *  - ADD_FUNDS_OPTION: Integer option to add funds to member account
 *  - CHECK_MEMBER_SCHEDULE_OPTION: Integer option to search member schedule
 *  - RETURN_TO_MAIN_MENU_OPTION: Integer option to return to main menu
 *  - EXIT: String that will indicate to cancel the current operation
 */

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

    // Constructor, uses connection and scanner objects
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

    /**
     * This private method handles the user recharging funds to their account
     */
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

    /**
     * This private method calls the util methods to set the account balance for a member
     *  and to create a transaction to store in the database
     * Arguments: gym member object and amount to add to account
     */
    private void makePurchaseOrRecharge( GymMember member, float amount ) {
        // Update member balance and save change
        member.setBalance( member.getBalance() + amount );
        DBUtils.saveChangesToMember( member, dbConnection );
        createTransaction( member, amount, dbConnection );
    }

    /**
     * This private method creates the transaction tuple in the transaction relation to
     *  describe either the purchase of a package by a member or the recharge of the
     *  account funds.
     * Arguments: gym member object, amount to add to account, connection to database
     */
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
     * Goes through the process of adding a new member to DB with the UI
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

    /**
     * Handles the user interaction with members purchasing a package
     */
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

    /**
     * Handles the user interaction with members purchasing a package, either at account creation or upon selection
     *  in the member operations menu
     * @param member - member that will be purchasing the course package
     */
    private void promptUserForPackagePurchase( GymMember member ) {
        System.out.println( "\nSelect a package for user to purchase ( Type name of package or 'cancel' for none )" );
        System.out.println( "-------------------------------------------------------------------------------------" );
        Map<String, Float> packages = DBUtils.getPackagesAndPrices( dbConnection );
        if ( packages.isEmpty() ) {
            System.err.println( "Unable to find any packages. Cancelling purchase." );
            return;
        }

        // Print out the options
        for ( String packageName : packages.keySet() ) {
            float cost = packages.get( packageName );
            System.out.println( packageName + "  $" + Math.round( ( cost - ( cost * member.getDiscount() ) ) ) );
        }
        String userInput = null;
        System.out.println();
        while ( true ) {
            userInput = readInputFromUser();
            if ( exitSignal ) {
                System.out.println( "Cancelling package purchase for member." );
                return;
            }
            if ( !packages.containsKey( userInput ) ) {
                System.out.println( "Please enter a choice from above." );
                continue;
            }
            break;
        }
        float cost = Math.round( ( packages.get( userInput ) - ( packages.get( userInput ) * member.getDiscount() ) ) );
        cost = -cost;
        makePurchaseOrRecharge( member, (float) cost );

        DBUtils.addMemberToPackageCourses( member, userInput, dbConnection );
    }

    /**
     * Handles user interaction with removing a member from the database
     */
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

    /**
     * Handles user interaction with member schedule search, printing out a formatted schedule of
     *  the user's classes during a given month
     */
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

    /**
     * Gathers user input with the possibility of cancelling an operation, which
     *  is handled in methods that call this one
     */
    private String readInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

    /**
     * Handles getting a valid email address from the user in member creation or
     *  cancel member creation
     * @return string of email to add to the database
     */
    private String getEmailFromUser() {
        System.out.println( "Enter new member's email address" );
        String email = "";
        email = readInputFromUser();
        if ( exitSignal ) {
            return null;
        }
        while ( !ValidationUtils.validateEmail( email ) ) {
            System.out.println( "Please enter a valid email" );
            email = readInputFromUser();
            if ( exitSignal ) {
                return null;
            }
        }

        return email;
    }

    /**
     * Handles getting a valid phone number from the user in member account creation
     *  or cancel member creation
     * @return string of phone number to add to the database
     */
    private String getPhoneNumberFromUser() {
        System.out.println( "Enter new member's phone number" );
        String phoneNumber = "";
        phoneNumber = readInputFromUser();
        if ( exitSignal ) {
            return null;
        }
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
