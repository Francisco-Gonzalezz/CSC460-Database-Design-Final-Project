package operations;

import java.sql.Connection;
import java.util.Map;
import java.util.Scanner;

import utils.CommonPrints;
import utils.DBUtils;

public class AdminOperations implements OperationsInterface {

    private static final int MAX_INTEGER_OPTION = 4;
    private static final int MIN_INTEGER_OPTION = 1;
    private static final String EXIT = "CANCEL";

    // Options 
    private static final int NEGATIVE_BALANCE_OPTION = 1;
    private static final int TRAINER_HOURS_OPTION = 2;
    private static final int REGISTER_TRAINER_OPTION = 3;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 4;

    private Scanner scanner;

    private Connection dbConnection;

    private boolean exitSignal;

    public AdminOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

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
            case REGISTER_TRAINER_OPTION:
                openRegisterNewTrainerWizard();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    private void listNegativeBalanceMembers() {
        System.out.println( "Members with negative balances" );
        System.out.println( "------------------------------" );
        Map<String, String> namesAndNums = DBUtils.getNegativeAccountUsers( dbConnection );
        if ( namesAndNums.isEmpty() ) {
            System.out.println( "There are zero negative balance accounts" );
            return;
        }

        for ( String name : namesAndNums.keySet() ) {
            String phoneNum = namesAndNums.get( name );
            System.out.println( name + " " + phoneNum );
        }
    }

    private void showTrainerWorkingHours() {
        System.out.println( "Trainer's working hours" );
        System.out.println( "-----------------------" );

    }

    private void openRegisterNewTrainerWizard() {
        System.out.println( "New Trainer Wizard ( Type 'Cancel' at anytime to cancel new trainer creation )" );
        System.out.println( "------------------------------------------------------------------------------" );
    }

    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

}
