package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;

public class RentalOperations implements OperationsInterface {

    private static final int MIN_INTEGER_OPTION = 1;
    private static final int MAX_INTEGER_OPTION = 5;
    private static final String EXIT = "CANCEL";

    private static final int RENT_OUT_ITEM_OPTION = 1;
    private static final int RETURN_RENTAL_OPTION = 2;
    private static final int CHECK_RENTAL_STATUS_OPTION = 3;
    private static final int CHECK_QUANTIY_OPTION = 4;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 5;

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
                break;
            case RETURN_RENTAL_OPTION:
                break;
            case CHECK_RENTAL_STATUS_OPTION:
                break;
            case CHECK_QUANTIY_OPTION:
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

}
