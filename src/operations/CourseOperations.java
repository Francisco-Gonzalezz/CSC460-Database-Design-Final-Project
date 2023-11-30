package operations;

import java.sql.Connection;
import java.util.Scanner;

import utils.CommonPrints;

public class CourseOperations implements OperationsInterface {

    private static final int MINIMUM_INTEGER_OPTION = 1;
    private static final int MAX_INTEGER_OPTION = 3;
    private static final String EXIT = "CANCEL";

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
        System.out.println();
        exitSignal = false;
        CommonPrints.printCourseOperations();
        String userInput = null;
        int option = 0;
        while ( !exitSignal ) {
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

        System.out.println();
        // Now go through whatever operation was selected
        switch ( option ) {
            case ADD_COURSE_OPTION:
                openAddCourseWizard();
                break;
            case DELETE_COURSE_OPTION:
                openRemoveCourseWizard();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    /**
     * Starts the process of creating a new course
     */
    private void openAddCourseWizard() {
        System.out.println( "New Course Wizard ( Type 'Cancel' at anytime to cancel course creation )" );
        System.out.println( "------------------------------------------------------------------------" );
    }

    /**
     * Starts the process of removing a course
     */
    private void openRemoveCourseWizard() {
        System.out.println( "Remove Course Wizard ( Type 'Cancel' at anytime to cancel course removal )" );
        System.out.println( "--------------------------------------------------------------------------" );

    }

    private String getInputFromUser() {
        String input = scanner.nextLine();
        if ( input.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return input;
    }

}
