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

    public static void promptUserToSelectTypeOfOperation() {
        System.out.println( "Select what type of operation you would like to perform." );
        System.out.println( "--------------------------------------------------------" );
    }

    /**
     * Prints the options for which operations can be done
     */
    public static void printStandardOptions() {
        System.out.println( "1) Gym member operations" );
        System.out.println( "2) Course operations" );
        System.out.println( "3) Admin Operations" );
        System.out.println( "4) Exit" );
    }

    public static void printGymMemberOperations() {
        System.out.println( "Gym Member Operations" );
        System.out.println( "---------------------" );
        System.out.println( "1) Add a new member" );
        System.out.println( "2) Delete a member" );
        System.out.println( "3) Return to previous menu" );
    }

    public static void printInvalidOptionMessage() {
        System.out.println( "Please enter a valid option from above" );
    }

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
