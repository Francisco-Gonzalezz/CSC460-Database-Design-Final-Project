/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * 
 * Class: PackageOperations.java
 * Implements: OperationsInterface
 * 
 * Utilizes:
 *  - java.sql.Connection
 *  - java.util.Scanner
 *  - utils.CommonPrints
 * 
 * Constructor: PackageOperations( Connection, Scanner ):
 *      - Connection to DB
 *      - Scanner to read input from stdin
 * 
 * Methods:
 *   openMenu():
 *       - Opens the menu for the operations that deal with packages, such as creating and deleting a package
 * 
 * Constants:
 *  - MINIMUM_INTEGER_OPTION: Min valid integer option
 *  - MAX_INTEGER_OPTIONL Max valid integer option
 *  - EXIT: String that will cancel the current operation
 *  - ADD_PACKAGE_OPTION: Integer option to add packages
 *  - UPDATE_PACKAGE_OPTION: Integer option to update a current package
 *  - REMOVE_PACKAGE_OPTION: Integer option to remove an existing package
 *  - RETURN_TO_MAIN_MENU_OPTION: Integer option to return to main menu
 * 
 * Global Variables
 *  - dbConnection: Connection to the DB
 *  - exitSignal: Boolean value that will signal to stop the current operation
 *  - scanner: Scanner object used to read from stdin
 */
package operations;

import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import entities.Course;
import entities.CoursePackage;
import entities.Package;
import utils.CommonPrints;
import utils.DBUtils;

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

    /**
     * Opens the menu for package operations. Will print the available operations that can be performed from the menu
     * and hand program control to functions who complete the task the user wants
     */
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

    /**
     * Takes user through the process of creating a new package. Will read a package name from the user,
     * the cost of the package, and the courses that will be in that package.
     */
    private void openNewPackageWizard() {
        System.out.println( "New Course Package Wizard ( Type 'Cancel' at anytime to cancel package creation )" );
        System.out.println( "---------------------------------------------------------------------------------" );
        System.out.println();

        // Get package name
        String packageName = getPackageName();
        if ( exitSignal ) {
            return;
        }

        // Get cost of new package
        float cost = getCostOfPackage();
        if ( exitSignal ) {
            return;
        }

        entities.Package packageToAdd = new Package( packageName, cost );
        if ( !DBUtils.saveNewPackage( packageToAdd, dbConnection ) ) {
            System.out.println( "Unable to save package to DB, possibly a duplicate package" );
            return;
        }
        // Get how many courses to add to package
        System.out.println( "\nHow many courses would you like to add to the package" );
        System.out.println( "------------------------------------------------------" );
        int amountOfCourses = 0;
        while ( true ) {
            try {
                amountOfCourses = Integer.valueOf( getInputFromUser() );
            } catch ( NumberFormatException e ) {
                if ( exitSignal ) {
                    break;
                }
                System.out.println( "Must enter a numeric value" );
                continue;
            }

            if ( amountOfCourses <= 0 ) {
                System.out.println( "Must add at least one course" );
                continue;
            }

            break;
        }

        if ( exitSignal ) {
            return;
        }
        // Get courses that user wants to end
        Set<String> courseSelections = getCourseSelections( amountOfCourses );
        if ( exitSignal ) {
            return;
        }

        // Save the selections to DB
        for ( String courseSelection : courseSelections ) {
            int courseID = DBUtils.getCourseIDFromName( courseSelection, dbConnection );
            CoursePackage coursePackage = new CoursePackage( courseID, courseSelection );
            DBUtils.saveNewCoursePackage( coursePackage, dbConnection );
        }

    }

    /**
     * Continually asks for user to select how many courses they initially chose to add to this package.
     * Will make sure that the same course is not selected more than once.
     * Will return a set of string of courses selected
     * @param numberOfCoursesToSelect
     * @return Set<String> containing string of courses
     */
    private Set<String> getCourseSelections( int numberOfCoursesToSelect ) {
        Set<String> coursesSelected = new HashSet<>();
        List<Course> allCourses = DBUtils.getAllCourses( dbConnection );
        if ( allCourses.size() < numberOfCoursesToSelect ) {
            System.out.println( "Not enough courses to make a package with " + numberOfCoursesToSelect + " courses." );
            exitSignal = true;
            return null;
        }

        System.out.println( "\nSelect a course from below" );
        System.out.println( "--------------------------" );
        Map<String, String> courseMap = new HashMap<>();
        int i = 1;
        for ( Course course : allCourses ) {
            String option = i + ")" + course.toString();
            System.out.println( option );
            String[] split = option.split( Pattern.quote( ")" ) );
            courseMap.put( split[0], split[1] );
            i++;
        }

        System.out.println();
        System.out.println( "Select first class" );
        while ( coursesSelected.size() < numberOfCoursesToSelect ) {
            String userSelection = null;
            while ( userSelection == null ) {
                userSelection = getInputFromUser();
                if ( exitSignal ) {
                    return null;
                }
                if ( !courseMap.containsKey( userSelection ) ) {
                    System.out.println( "Not a valid option" );
                    continue;
                }
                String courseSelected = courseMap.get( userSelection );
                if ( !coursesSelected.add( courseSelected ) ) {
                    System.out.println( "Course already selected please pick a different option" );
                } else if ( coursesSelected.size() < numberOfCoursesToSelect ) {
                    System.out.println( "Select another class to add" );
                }
            }
        }

        return coursesSelected;
    }

    private float getCostOfPackage() {
        float cost = 0;

        System.out.println( "\nEnter cost of the package" );
        System.out.println( "--------------------------" );
        while ( true ) {
            try {
                cost = Float.valueOf( getInputFromUser() );
                if ( exitSignal ) {
                    return cost;
                }
            } catch ( NumberFormatException e ) {
                System.out.println( "Enter a numberic value please" );
                continue;
            }

            if ( cost <= 0 ) {
                System.out.println( "Class cannot be free" );
                continue;
            }

            break;
        }

        return cost;
    }

    /**
     * Get package name from user
     * @return User supplied package name
     */
    private String getPackageName() {
        System.out.println( "Name of new package" );
        System.out.println( "-------------------" );
        String packageName = null;
        while ( true ) {
            packageName = getInputFromUser();
            if ( exitSignal ) {
                return null;
            }

            if ( packageName.isEmpty() ) {
                System.out.println( "Name cannot be empty" );
                continue;
            }
            break;
        }
        return packageName;
    }

    /**
     * Takes user through the process of updating a package
     */
    private void openUpdatePackageWizard() {
        System.out.println( "Update Course Package Wizard ( Type 'Cancel' at anytime to cancel package update )" );
        System.out.println( "----------------------------------------------------------------------------------" );
    }

    /**
     * Takes user through the process of removing an existing package
     */
    private void openRemovePackageWizard() {
        System.out.println( "Remove Course Package Wizard ( Type 'Cancel' at anytime to cancel package removal )" );
        System.out.println( "-----------------------------------------------------------------------------------" );
    }

    /**
     * Reads input from the user and sets the exit flag to true if the 
     * user types cancel
     * @return What the user typed with a string
     */
    private String getInputFromUser() {
        String userInput = scanner.nextLine();
        if ( userInput.equalsIgnoreCase( EXIT ) ) {
            exitSignal = true;
        }
        return userInput;
    }
}
