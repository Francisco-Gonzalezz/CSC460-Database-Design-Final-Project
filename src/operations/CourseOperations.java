/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * @version 1.0
 * Class: CourseOperations.java
 * Implements: OperationsInterface
 * Purpose: Encapsulate operations that are speicific to courses and classes. Such as course creation, course deletion, and course updates.
 * 
 * Utilizes:
 *  - java.sql.Connection
 *  - java.util.HashSet
 *  - java.util.List
 *  - java.util.Scanner
 *  - java.util.Set
 *  - java.util.stream.Collectors
 *  - entities.Course
 *  - utils.CommonPrints
 *  - utils.DBUtils
 * 
 * Constructor: CourseOperations(Connection, Scanner):
 *      - Connection to DB
 *      - Scanner to read input from stdin
 * 
 * Methods:
 *  openMenu():
 *      - Opens the course operations menu and prints the available options within that menu. Will read input from stdin
 *        to determine what action to take next after validating that it is one of of the valid options listed below. Will
 *        send control to function coresponding to that option
 * openAddClassWizard():
 *      - Takes user through the process of creating a class such as setting dates, durations, trainers, etc.
 *        will save the class to the DB after the course creation process is finished.
 * openAddCourseWizard():
 *      - Will take user through the process of creating a course getting information such as category, and catalog number.
 *        After the process is done the new course will be saved to the DB. 
 * openRemoveClassWizard():
 *      - Goes through the process of deleting a class from the DB including getting input from user for what class they want to delete.
 *        will then print the names of the people the admin should contact whose class is being cancelled
 * 
 * Constants:
 *  - MINIMUM_INTEGER_OPTION: Minimum valid integer option
 *  - MAX_INTEGER_OPTION: Maximum valid integer option
 *  - EXIT: String literal to store the value if entered should end the current operations
 *  - ADD_COURSE_OPTION: Integer value to enter the add course wizard
 *  - ADD_CLASS_OPTION: Integer value to enter the add class wizard
 *  - DELETE_CLASS_OPTION: Integer value to enter the delete class wizard
 *  - RETURN_TO_MAIN_MENU_OPTION: Integer value to return program to the main menu
 * 
 * Global Variables:
 *  scanner: Scanner object to read input from user through stdin
 *  dbConnection: Connection to the DB
 *  exitSignal: Boolean value that tells the program if it should return to main menu or not
 */
package operations;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import entities.Course;
import utils.CommonPrints;
import utils.DBUtils;

public class CourseOperations implements OperationsInterface {

    private static final int MINIMUM_INTEGER_OPTION = 1;
    private static final int MAX_INTEGER_OPTION = 4;
    private static final String EXIT = "CANCEL";

    // Valid integer options
    private static final int ADD_COURSE_OPTION = 1;
    private static final int ADD_CLASS_OPTION = 2;
    private static final int DELETE_CLASS_OPTION = 3;
    private static final int RETURN_TO_MAIN_MENU_OPTION = 4;

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
            case ADD_CLASS_OPTION:
                openAddClassWizard();
                break;
            case DELETE_CLASS_OPTION:
                openRemoveClassWizard();
                break;
            case RETURN_TO_MAIN_MENU_OPTION:
                break;
        }

        System.out.println();
    }

    private void openAddClassWizard() {
        System.out.println( "New Class Wizard ( Type 'Cancel' at anytime to cancel class creation )" );
        System.out.println( "----------------------------------------------------------------------" );
        System.out.println( "\nAvailable course to select" );
        System.out.println( "------------------------------" );
        List<Course> coursesAvailable = DBUtils.getAllCourses( dbConnection );
        for ( Course course : coursesAvailable ) {
            System.out.println( course );
        }
        Set<String> courseNames = new HashSet<>(
            coursesAvailable.stream().map( Course::toString ).collect( Collectors.toSet() ) );
        System.out.println();
        String category;
        int catalogNum;
        String userInput;
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                return;
            }

            if ( !courseNames.contains( userInput ) ) {
                System.out.println( "Please select a course from above" );
                continue;
            }

            String[] split = userInput.split( " " );
            category = split[0];
            catalogNum = Integer.parseInt( split[1] );
        }

        // TODO: Finsh this

    }

    /**
     * Starts the process of creating a new course
     */
    private void openAddCourseWizard() {
        System.out.println( "New Course Wizard ( Type 'Cancel' at anytime to cancel course creation )" );
        System.out.println( "------------------------------------------------------------------------" );
        System.out.println( "Enter category for course ( i.e Strength etc. )" );
        String category = getInputFromUser();
        if ( exitSignal ) {
            return;
        }

        System.out.println( "Enter a catalog number for new course" );
        int catalogNum;
        while ( true ) {
            try {
                catalogNum = Integer.valueOf( getInputFromUser() );
            } catch ( NumberFormatException e ) {
                System.out.println( "Please enter a numeric value" );
                continue;
            }
            if ( exitSignal ) {
                return;
            }
            break;
        }

        // Wait to generate id until we know creation will actually be done
        int courseID = DBUtils.generateIDNumberFromSequence( dbConnection );
        Course newCourse = new Course( courseID, category, catalogNum );
        DBUtils.saveNewCourse( newCourse, dbConnection );
    }

    /**
     * Starts the process of removing a course
     */
    private void openRemoveClassWizard() {
        System.out.println( "Remove class wizard ( Type 'Cancel' to exit menu )" );
        System.out.println( "--------------------------------------------------" );

    }

    private String getInputFromUser() {
        String input = scanner.nextLine();
        if ( input.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return input;
    }

}
