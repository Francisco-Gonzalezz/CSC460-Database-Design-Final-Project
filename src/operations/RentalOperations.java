/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * 
 * Class: RentalOperations.java
 * Implements: OperationsInterface
 * Purpose: Encapsulate operations that are specific to item rental at the gym such as renting and returning
 *  items and determining which items a user has checked out but not returned.
 * 
 * Utilizes:
 *  - java.sql.*
 *  - java.util.*
 * 
 * Constructor: RentalOperations( Connection, Scanner ):
 *  - Connection to DB
 *  - Create scanner to read input from stdin
 * 
 * Public Methods:
 *  - openMenu():
 *      - Opens the member operations memu and prints the available options within the displayed menu. Reads input from user
 *        and validates that it was one of the options the user could currently choose from. Then directs control of program
 *        over to coresponding functions of the option chosen.
 */

package operations;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import entities.GymMember;
import entities.RentalItem;
import entities.RentalLogEntry;
import utils.CommonPrints;
import utils.DBUtils;

public class RentalOperations implements OperationsInterface {

    private static final int MIN_INTEGER_OPTION = 1;
    private static final int MAX_INTEGER_OPTION = 5;
    private static final int SMALLEST_MEMBER_ID = 1;
    private static final String EXIT = "CANCEL";

    private static final int RENT_OUT_ITEM_OPTION = 1;
    private static final int RETURN_RENTAL_OPTION = 2;
    private static final int CHECK_QUANTIY_OPTION = 3;
    private static final int CHECK_UNRETURNED_ITEMS_OPTION = 4;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 5;

    private Connection dbConnection;

    private Scanner scanner;

    private boolean exitSignal;

    // Constructor for rental operations menu
    public RentalOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    /**
     * Opens the member operations memu and prints the available options within the displayed menu. Reads input from user
     *  and validates that it was one of the options the user could currently choose from. Then directs control of program
     *  over to coresponding functions of the option chosen.
     */
    @Override
    public void openMenu() {
        System.out.println();
        exitSignal = false;
        CommonPrints.printRentalOperations();
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

            // checks that the option selected is one of the ones in the list
            if ( option < MIN_INTEGER_OPTION || option > MAX_INTEGER_OPTION ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            break;
        }

        // opens menu corresponding to selected option
        switch ( option ) {
            case RENT_OUT_ITEM_OPTION:
                openRentOutItemMenu();
                break;
            case RETURN_RENTAL_OPTION:
                openReturnItemMenu();
                break;
            case CHECK_QUANTIY_OPTION:
                listRentalItemsAndQuantities();
                break;
            case CHECK_UNRETURNED_ITEMS_OPTION:
                listUnreturnedItems();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }
        System.out.println();
    }

    /**
     * This private method implements our custom query, which prompts the user for a member id
     *  then lists all of the rental items that that member has not yet returned; handles user
     *  input and printing the query results to the console.
     */
    private void listUnreturnedItems() {
        System.out.println( "Figure out what items user has not returned yet" );
        System.out.println( "------------------------------------------------" );
        System.out.println( "Enter memberID" );
        GymMember member = null;
        while ( member == null ) {
            int memberId;
            try {
                memberId = Integer.valueOf( getInputFromUser() );
                if ( exitSignal ) {
                    return;
                }
            } catch ( NumberFormatException e ) {
                System.out.println( "Enter a numeric value" );
                continue;
            }

            // builds the member object from the database
            member = DBUtils.retrieveMemberFromID( memberId, dbConnection );
            if ( member == null ) {
                System.out.println( "Invalid ID please enter again" );
                continue;
            }
            break;
        }
        // gets the result set from the query
        Map<String, Integer> checkoutItems = DBUtils.getCheckoutRentalsForMember( member, dbConnection );
        if ( checkoutItems.isEmpty() ) {
            System.out.println( "\n" + member.getFullName() + " has no unreturned items" );
            return;
        }

        System.out.println( "\nList of items rented by " + member.getFullName() + " and quantity borrowed" );
        System.out
            .println(
                "-------------------------------------------------------------------------------------------------" );
        for ( String item : checkoutItems.keySet() ) {
            int checkout = checkoutItems.get( item );
            System.out.println( item + " " + checkout );
        }
    }

    /**
     * This private method deals with a member returning an item through the database, handling user
     *  interaction with the UI as well as running the queries.
     */
    private void openReturnItemMenu() {
        System.out.println( "Return an item ( Type 'Cancel' to exit menu )" );
        System.out.println( "---------------------------------------------" );
        System.out.println( "\nEnter member id" );
        GymMember member = null;
        while ( member == null ) {
            int memberId;
            try {
                memberId = Integer.valueOf( getInputFromUser() );
                if ( exitSignal ) {
                    return;
                }
            } catch ( NumberFormatException e ) {
                System.out.println( "Enter a numeric value" );
                continue;
            }

            member = DBUtils.retrieveMemberFromID( memberId, dbConnection );
            if ( member == null ) {
                System.out.println( "Invalid ID please enter again" );
                continue;
            }
            break;
        }

        // Get rental items from db that is in possesion of member
        Map<String, Integer> checkoutItems = DBUtils.getCheckoutRentalsForMember( member, dbConnection );
        if ( checkoutItems.isEmpty() ) {
            System.out.println( "\n" + member.getFullName() + " has no unreturned items" );
            return;
        }

        System.out.println( "\nList of items rented by " + member.getFullName() + " and quantity borrowed" );
        System.out
            .println(
                "-------------------------------------------------------------------------------------------------" );
        for ( String item : checkoutItems.keySet() ) {
            int checkout = checkoutItems.get( item );
            System.out.println( item + " " + checkout );
        }
        // Ask user to select an item from above
        System.out.println( "\nSelect an item to return" );
        String itemBeingReturned = "";
        while ( true ) {
            itemBeingReturned = getInputFromUser();
            if ( exitSignal ) {
                return;
            }
            if ( !checkoutItems.containsKey( itemBeingReturned ) ) {
                System.out.println( "Please enter a value from above" );
                continue;
            }
            break;
        }
        // runs the queries to update the log
        DBUtils.returnItem( itemBeingReturned, dbConnection );
        DBUtils.updateRentalLog( member, itemBeingReturned, dbConnection );
    }

    /**
     * This private method deals with a member renting an item through the database, handling user
     *  interaction with the UI as well as running the queries.
     */
    private void openRentOutItemMenu() {
        System.out.println( "Rent out item ( Type 'Cancel' to exit )" );
        System.out.println( "---------------------------------------" );

        // Get member who is wanting to rent
        GymMember member = null;
        while ( member == null ) {
            int memberID = getMemberIDFromUser();
            if ( exitSignal ) {
                System.out.println( "Cancelling item rental" );
                return;
            }
            member = DBUtils.retrieveMemberFromID( memberID, dbConnection );
            if ( member == null ) {
                System.out.println( "Invalid member id. Verify that id was typed in correctly" );
            }
        }

        // Get which item they want to rent
        System.out.println( "\nWhich item would you like to rent out?" );
        System.out.println( "------------------------------------------" );
        List<RentalItem> rentalItems = DBUtils.getRentalItems( dbConnection );
        Map<String, Integer> rentalMap = DBUtils.getRentalItemsAndQuantities( dbConnection );
        for ( RentalItem item : rentalItems ) {
            System.out.println( item );
        }
        System.out.println();
        String item = null;
        while ( true ) {
            item = getInputFromUser();
            if ( exitSignal ) {
                return;
            }
            if ( !rentalMap.containsKey( item ) ) {
                System.out.println( "Please choose from the above options" );
                continue;
            }
            break;
        }

        // selects the rental item from the possible list that matches the user input
        int toRentOut = 1;
        RentalItem itemSelected = null;
        for ( RentalItem rentalItem : rentalItems ) {
            if ( rentalItem.getItemName().equalsIgnoreCase( item ) ) {
                itemSelected = rentalItem;
                break;
            }
        }

        // Create the entry for rental log
        RentalLogEntry entry = new RentalLogEntry(
            DBUtils.generateIDNumberFromSequence( dbConnection ),
            member.getMemberID(),
            itemSelected.getItemNum(),
            new Date( System.currentTimeMillis() ),
            false,
            toRentOut 
        );
        DBUtils.saveNewRentalLogEntry( entry, dbConnection );

        // Update the rental item table to account for quantity being taken
        itemSelected.setQuantityInStock( itemSelected.getQuantityInStock() - toRentOut );
        DBUtils.saveChangesToRentalItem( itemSelected, dbConnection );
    }

    /**
     * This private method lists all of the rental items in stock for the user to see
     */
    private void listRentalItemsAndQuantities() {
        System.out.println();
        System.out.println( "Rental items and their quantites in stock" );
        System.out.println( "-----------------------------------------" );
        Map<String, Integer> rentalItems = DBUtils.getRentalItemsAndQuantities( dbConnection );
        for ( String item : rentalItems.keySet() ) {
            int qty = rentalItems.get( item );
            System.out.println( item + " " + qty );
        }
    }

    /**
     * Gathers user input with the possibility of cancelling an operation, which
     *  is handled in methods that call this one
     */
    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

    /**
     * This method gets the member id from the user for various commands within the rental
     *  operations menu.
     */
    private int getMemberIDFromUser() {
        System.out.println( "Enter the member id" );
        String userInputMemberID = null;
        int memberID;
        while ( true ) {
            System.out.println();
            userInputMemberID = getInputFromUser();

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
