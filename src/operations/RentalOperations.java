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
    private static final int MAX_INTEGER_OPTION = 4;
    private static final int SMALLEST_MEMBER_ID = 1;
    private static final String EXIT = "CANCEL";

    private static final int RENT_OUT_ITEM_OPTION = 1;
    private static final int RETURN_RENTAL_OPTION = 2;
    private static final int CHECK_QUANTIY_OPTION = 3;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 4;

    private Connection dbConnection;

    private Scanner scanner;

    private boolean exitSignal;

    public RentalOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

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

            if ( option < MIN_INTEGER_OPTION || option > MAX_INTEGER_OPTION ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            break;
        }

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
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }
        System.out.println();
    }

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
        DBUtils.returnItem( itemBeingReturned, dbConnection );
        DBUtils.updateRentalLog( member, itemBeingReturned, dbConnection );
    }

    private void openRentOutItemMenu() {
        System.out.println( "Rent out item ( Type 'Cancel' to exit )" );
        System.out.println( "---------------------------------------" );

        // Get member who is wanting to rent
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
            toRentOut );
        DBUtils.saveNewRentalLogEntry( entry, dbConnection );

        // Update the rental item table to account for quantity being taken
        itemSelected.setQuantityInStock( itemSelected.getQuantityInStock() - toRentOut );
        DBUtils.saveChangesToRentalItem( itemSelected, dbConnection );
    }

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

    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

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
