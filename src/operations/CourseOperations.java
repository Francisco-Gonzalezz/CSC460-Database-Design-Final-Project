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
 *  - java.sql.Date
 *  - java.sql.Timestamp
 *  - java.time.LocalDate
 *  - java.util.Calendar
 *  - java.util.HashSet
 *  - java.util.List
 *  - java.util.Scanner
 *  - java.util.Set
 *  - java.util.stream.Collectors
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
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import entities.Course;
import entities.Trainer;
import entities.Class;
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

    /**
     * Opens the menu for the possible course operations, reads input from user to decide
     * what they want to do and hand program control to the function that will perform that task
     */
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
                if ( exitSignal ) {
                    return;
                }
            } catch ( NumberFormatException e ) {
                System.out.println( "Please enter an integer value" );
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
     * Opens the wizard to take user through the process of creating a new wizard.
     * Will grab information such as class names and dates and trainers who teach.
     */
    private void openAddClassWizard() {
        System.out.println( "New Class Wizard ( Type 'Cancel' at anytime to cancel class creation )" );
        System.out.println( "----------------------------------------------------------------------" );

        int classId = DBUtils.generateIDNumberFromSequence( dbConnection );
        
        int courseId = getCourseFromUser();
        if ( exitSignal ) {
            return;
        }
        
        System.out.println( "\nWhat is the start date for this class? (Enter as 'YYYY-MM-DD')" );
        System.out.println( "--------------------------------------------------------------" );
        Date startDate = getDateFromUser();
        if ( exitSignal ) {
            return;
        }

        System.out.println( "\nWhat is the end date for this class? (Enter as 'YYYY-MM-DD')" );
        System.out.println( "--------------------------------------------------------------" );
        Date endDate = getDateFromUser();
        if ( exitSignal ) {
            return;
        }
        if ( startDate.after( endDate ) ) {
            System.out.println( "End date is before start date. Cancelling operation" );
            return;
        }

        Timestamp startTime = getStartTimeFromUser( startDate );
        if ( exitSignal ) {
            return;
        }

        int duration = getDurationFromUser();
        if ( exitSignal ) {
            return;
        }

        int trainerId = getTrainerFromUser( startTime, duration );
        if ( exitSignal ) {
            return;
        }

        int capacity = getCapacityFromUser();

        Class newClass = new Class( classId, courseId, trainerId, startTime, duration, 
                startDate, endDate, 0, capacity );
        DBUtils.saveNewClass( newClass, dbConnection );
    }

    /**
     * Asks user for the course to be offered as a class (to be created),
     *  dealing with error handling and managing UI
     */
    private int getCourseFromUser() {
        System.out.println( "Available courses to select from:" );
        System.out.println( "---------------------------------" );
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
                CommonPrints.printClassCreationCancelled();
                return 0;
            }

            // validates input, restricted to course list
            if ( !courseNames.contains( userInput ) ) {
                System.out.println( "Please select a course from the list above" );
                continue;
            }

            String[] split = userInput.split( " " );
            category = split[0];
            catalogNum = Integer.parseInt( split[1] );
            break;
        }
        return DBUtils.getCourseId( category, catalogNum, dbConnection );
    }

    /**
     * Asks user for the trainer that will teach the class to be created,
     *  dealing with error handling and managing UI
     */
    private int getTrainerFromUser( Timestamp startTime, int duration ) {
        System.out.println( "\nSelect a trainer to teach this class:" );
        System.out.println( "-------------------------------------" );
        List<Trainer> allTrainers = DBUtils.listAllTrainers( dbConnection );
        for ( Trainer trainer : allTrainers ) {
            System.out.println( trainer.getFullName() );
        }
        Set<String> trainerNames = new HashSet<>(
            allTrainers.stream().map( Trainer::getFullName ).collect( Collectors.toSet() ) );
        System.out.println();
        String trainerFname, trainerLname, userInput;
        int trainerId;
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                CommonPrints.printClassCreationCancelled();
                return 0;
            }

            // validates input, restricted to course list
            if ( !trainerNames.contains( userInput ) ) {
                System.out.println( "Please select a trainer from the list above" );
                continue;
            }

            String[] split = userInput.split( " " );
            trainerFname = split[0];
            trainerLname = split[1];
            trainerId = DBUtils.getTrainerId( trainerFname, trainerLname, dbConnection );
            if ( DBUtils.trainerScheduleConflict( trainerId, startTime, duration, dbConnection ) ) {
                System.out.println( "There is a scheduling conflict with this trainer." + 
                    " Select another trainer from the list." );
                continue;
            }
            break;
        }
        return trainerId;
    }

    /**
     * Prompts user for the start/end date for the class to be created,
     *  dealing with error handling and managing UI
     */
    private Date getDateFromUser() {
        String userInput;
        int year, month, day; 
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                CommonPrints.printClassCreationCancelled();
                return null;
            }

            // validates input as date
            String[] split = userInput.split("-");
            if ( split.length != 3  || split[0].length() != 4 || split[1].length() != 2
                || split[2].length() != 2 ) {
                System.out.println( "Please enter the date in the formatting specified above" );
                continue;
            }
            try {
                year = Integer.parseInt( split[0] );
                month = Integer.parseInt( split[1] );
                day = Integer.parseInt( split[2] );
            } catch ( NumberFormatException e ) {
                System.out.println( "Please enter the date in the formatting specified above" );
                continue;
            }
            if ( month > 12 || month < 1 || day < 1 || day > 31 ) {
                System.out.println( "Please enter a valid date" );
                continue;
            }
            break;
        }
        LocalDate localDate = LocalDate.of( year, month, day );
        Date date = Date.valueOf( localDate );
        return date;
    }

    /**
     * Prompts user for the weekly start time for the class to be created,
     *  dealing with error handling and managing UI; class will be held
     *  weekly on the specified start date at this given start time
     */
    private Timestamp getStartTimeFromUser( Date startDate ) {
        System.out.println( "\nWhat is the start time for this class? (Enter as 'HH:MM', using 24-hr time)" );
        System.out.println( "---------------------------------------------------------------------------" );
        String userInput;
        int hour, minute;
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                CommonPrints.printClassCreationCancelled();
                return null;
            }

            // validates user input as specified format and numerical values
            String[] split = userInput.split(":");
            if ( split.length != 2  || split[0].length() != 2 || split[1].length() != 2 ) {
                System.out.println( "Please enter the time in the formatting specified above" );
                continue;
            }
            try {
                hour = Integer.parseInt( split[0] );
                minute = Integer.parseInt( split[1] );
            } catch ( NumberFormatException e ) {
                System.out.println( "Please enter the time in the formatting specified above" );
                continue;
            }
            if ( hour > 23 || hour < 0 || minute > 59 || minute < 0 ) {
                System.out.println( "Please enter a valid timestamp" );
                continue;
            }
            break;
        }

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTime( startDate );
        cal.set( Calendar.HOUR_OF_DAY, hour );
        cal.set( Calendar.MINUTE, minute );
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Prompts user for the duration of the class to be created,
     *  dealing with error handling and managing UI; class will be held
     *  from start time and will last this many minutes, specifed by user
     */
    private int getDurationFromUser() {
        System.out.println( "\nWhat is the duration of this class? (Enter as an integer in number of minutes)" );
        System.out.println( "-----------------------------------------------------------------------------" );
        String userInput;
        int duration;
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                CommonPrints.printClassCreationCancelled();
                return 0;
            }

            // Validates and formats user input
            try {
                duration = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                System.out.println( "Duration must be a numeric value" );
                continue;
            }
            if ( duration > 300 ) {
                System.out.println( "Duration should be less than 5 hrs" );
                continue;
            }
            break;
        }
        return duration;
    }

    /**
     * Prompts user for the total capacity of members that can be enrolled in
     *  a class; deals with error handling and managing UI
     */
    private int getCapacityFromUser() {
        System.out.println( "\nWhat is the capacity this class? (Enter an integer)" );
        System.out.println( "-----------------------------------------------------------------------------" );
        String userInput;
        int capacity;
        while ( true ) {
            userInput = getInputFromUser();
            if ( exitSignal ) {
                CommonPrints.printClassCreationCancelled();
                return 0;
            }

            // Validates and formats user input
            try {
                capacity = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                System.out.println( "Duration must be a numeric value" );
                continue;
            }
            if ( capacity < 0 ) {
                System.out.println( "Duration should be non-negative" );
                continue;
            }
            break;
        }
        return capacity;
    }

    /**
     * Starts the process of removing a course
     */
    private void openRemoveClassWizard() {
        System.out.println( "Remove class wizard ( Type 'Cancel' to exit menu )" );
        System.out.println( "--------------------------------------------------" );
        System.out.println( "We did not get to complete this functionality :(" );
    }

    /**
     * Gets input from user stdin
     * @return String value of what user entered
     */
    private String getInputFromUser() {
        String input = scanner.nextLine();
        if ( input.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return input;
    }

}
