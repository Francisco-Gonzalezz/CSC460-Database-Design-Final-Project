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

    private void listNegativeBalanceMembers() {
        System.out.println( "Members with negative balances" );
        System.out.println( "------------------------------" );
        Map<String, String> namesAndNums = DBUtils.getNegativeAccountUsers( dbConnection );
        if ( namesAndNums.isEmpty() ) {
            System.out.println( "There are zero negative balance accounts" );
            return;
        }
        List<String> memberName = new ArrayList<>( namesAndNums.keySet() );
        Collections.sort( memberName );
        for ( String name : memberName ) {
            System.out.println( name + " " + namesAndNums.get( name ) );
        }
    }

    private void showTrainerWorkingHours() {
        System.out.println( "Trainer's working hours" );
        System.out.println( "-----------------------" );
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
            float hours = trainerHours.get( trainerName ) / 60;
            System.out.println( trainerName + " is working " + hours + " hours" );
        }
    }

    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }

}
