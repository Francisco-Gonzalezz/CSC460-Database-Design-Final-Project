package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;

public class PackageOperations implements OperationsInterface {

    private static final int MINIMUM_INTEGER_OPTION = 1;
    private static final int MAX_INTEGER_OPTION = 4;
    private static final String EXIT = "CANCEL";

    // Options
    private static final int ADD_PACKAGE_OPTION = 1;
    private static final int UPDATE_PACKAGE_OPTION = 2;
    private static final int REMOVE_PACKAGE_OPTION = 3;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 4;

    private Scanner scanner;

    private Connection dbConnection;

    private boolean exitSignal;

    public PackageOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    @Override
    public void openMenu() {
        System.out.println();
        exitSignal = false;
        CommonPrints.printPackageOperations();
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

            if ( option < MINIMUM_INTEGER_OPTION || option > MAX_INTEGER_OPTION ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            break;
        }

        System.out.println();

        switch ( option ) {
            case ADD_PACKAGE_OPTION:
                openNewPackageWizard();
                break;
            case UPDATE_PACKAGE_OPTION:
                openUpdatePackageWizard();
                break;
            case REMOVE_PACKAGE_OPTION:
                openRemovePackageWizard();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    private void openNewPackageWizard() {
        System.out.println( "New Course Package Wizard ( Type 'Cancel' at anytime to cancel package creation )" );
        System.out.println( "---------------------------------------------------------------------------------" );
    }

    private void openUpdatePackageWizard() {
        System.out.println( "Update Course Package Wizard ( Type 'Cancel' at anytime to cancel package update )" );
        System.out.println( "----------------------------------------------------------------------------------" );
    }

    private void openRemovePackageWizard() {
        System.out.println( "Remove Course Package Wizard ( Type 'Cancel' at anytime to cancel package removal )" );
        System.out.println( "-----------------------------------------------------------------------------------" );
    }

    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }
}
