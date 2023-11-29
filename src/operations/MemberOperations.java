package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.PrintUtils;

public class MemberOperations implements OperationsInterface {

    private static final int MAX_MEMBER_OPERATIONS = 3; // Max valid integer option for member menu
    private static final int MIN_OPERATIONS = 1; // Min valid integer option for member menu
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
        PrintUtils.printGymMemberOperations();
        String userInput = null;
        int option;
        while ( true ) {
            System.out.println();
            userInput = scanner.nextLine();
            try {
                option = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                PrintUtils.printInvalidOptionMessage();
                continue;
            }

            if ( option < MIN_OPERATIONS || option > MAX_MEMBER_OPERATIONS ) {
                PrintUtils.printInvalidOptionMessage();
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

    private void openAddMemberWizard() {

    }

    private void openRemoveMemberWizard() {

    }

}
