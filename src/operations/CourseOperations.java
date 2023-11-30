package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;

public class CourseOperations implements OperationsInterface {

    private static int MINIMUM_INTEGER_OPTION = 1;
    private static int MAX_INTEGER_OPTION = 3;

    // Valid integer options
    private static final int ADD_COURSE_OPTION = 1;
    private static final int DELETE_COURSE_OPTION = 2;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 3;

    private Connection dbConnection;

    private Scanner scanner;

    private boolean exitSignal;

    public CourseOperations( Connection dbConnection, Scanner scanner ) {
        this.dbConnection = dbConnection;
        this.scanner = scanner;
    }

    @Override
    public void openMenu() {
        exitSignal = false;
        System.out.println();
        CommonPrints.printCourseOperations();
        String userInput = null;
        int option = 0;
        while ( true ) {
            System.out.println();
            userInput = scanner.nextLine();
            // Validate that input is an integer
            try {
                option = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }

            // Validate that it is a valid option
            if ( option < MINIMUM_INTEGER_OPTION || option > MAX_INTEGER_OPTION ) {
                CommonPrints.printInvalidOptionMessage();
                continue;
            }
            break;
        }

        // Now go through whatever operation was selected
        switch ( option ) {
            case ADD_COURSE_OPTION:
                openAddCourseWizard();
                break;
            case DELETE_COURSE_OPTION:
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    private void openAddCourseWizard() {
        System.out.println( "New Course Wizard ( Type 'Cancel' at anytime to cancel course creation )" );
        System.out.println( "------------------------------------------------------------------------" );
    }

    private void openRemoveCourseWizard() {
        System.out.println( "Remove Course Wizard ( Type 'Cancel' at anytime to cancel course removal )" );
        System.out.println( "--------------------------------------------------------------------------" );

    }

}
