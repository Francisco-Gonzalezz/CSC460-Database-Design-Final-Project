/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * Class: CommonPrints.java
 * Purpose: This class will print out the different prints that are used pretty frequently throughout the program
 * 
 * Methods:
 *  - printWelcomeMessage()
 *  - promptUserToSelectTypeOfOperation()
 *  - printStandardOptions()
 *  - printGymMemberOperations()
 *  - printCourseOperations()
 *  - printPackageOperations()
 *  - printRentalOperations()
 *  - printAdminOperations()
 *  - printInvalidOptionMessage()
 *  - printMemberCreationCancelled()
 *  - printExitMessage()
 */
package utils;

public class CommonPrints {

    // Keeps from making a new instance of the class
    private CommonPrints() {
    }

    /**
     * Prints the welcome message for when the program starts up
     */
    public static void printWelcomeMessage() {
        System.out.println( "\nWelcome to the 460 Gym Interface" );
    }

    /**
     * Prints the prompt to user for which type of operation they would like to perform
     */
    public static void promptUserToSelectTypeOfOperation() {
        System.out.println( "Select what type of operation you would like to perform" );
        System.out.println( "-------------------------------------------------------" );
    }

    /**
     * Prints the options for which operations can be done
     */
    public static void printStandardOptions() {
        System.out.println( "1) Gym member operations" );
        System.out.println( "2) Course/Class operations" );
        System.out.println( "3) Package operations" );
        System.out.println( "4) Rental operations" );
        System.out.println( "5) Admin operations" );
        System.out.println( "6) Exit" );
    }

    /**
     * Prints the available gym member options
     */
    public static void printGymMemberOperations() {
        System.out.println( "Gym Member Operations" );
        System.out.println( "---------------------" );
        System.out.println( "1) Add a new member" );
        System.out.println( "2) Delete a member" );
        System.out.println( "3) Purchase a package" );
        System.out.println( "4) Add funds to account" );
        System.out.println( "5) Check member's class schedule" );
        System.out.println( "6) Return to previous menu" );
    }

    /**
     * Prints the available course operations
     */
    public static void printCourseOperations() {
        System.out.println( "Course Operations" );
        System.out.println( "-----------------" );
        System.out.println( "1) Add a course" );
        System.out.println( "2) Add class" );
        System.out.println( "3) Remove class" );
        System.out.println( "4) Return to previous menu" );
    }

    /**
     * Prints the avaiable Course Package Operations
     */
    public static void printPackageOperations() {
        System.out.println( "Package Operations" );
        System.out.println( "------------------" );
        System.out.println( "1) Add Package" );
        System.out.println( "2) Update Package" );
        System.out.println( "3) Remove Package" );
        System.out.println( "4) Return to previous menu" );
    }

    /**
     * Prints out the possible rental operations
     */
    public static void printRentalOperations() {
        System.out.println( "Rental Operations" );
        System.out.println( "-----------------" );
        System.out.println( "1) Rent out an item" );
        System.out.println( "2) Return an item" );
        System.out.println( "3) Check quantity of items" );
        System.out.println( "4) List items that member hasn't returned" );
        System.out.println( "5) Return to previous menu" );
    }

    /**
     * Prints the available admin operations
     */
    public static void printAdminOperations() {
        System.out.println( "Admin Operations" );
        System.out.println( "----------------" );
        System.out.println( "1) List members who have a negative balance" );
        System.out.println( "2) List all trainer's working hours" );
        System.out.println( "3) Return to previous menu" );
    }

    /**
     * Prints that the option user selected was invalid
     */
    public static void printInvalidOptionMessage() {
        System.out.println( "Please enter a valid option from above" );
    }

    /**
     * Prints when the gym member creation was cancelled
     */
    public static void printMemberCreationCancelled() {
        System.out.println( "\nCancelling member creation" );
    }

    /**
     * Message to print on exit
     */
    public static void printExitMessage() {
        System.out.println( "\nThanks for using the 460 Gym Interface!" );
    }

}
