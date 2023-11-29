public class PrintUtils {

    // Keeps from making a new instance of the class
    private PrintUtils() {
    }

    /**
     * Prints the welcome message for when the program starts up
     */
    public static void printWelcomeMessage() {
        System.out.println( "\nWelcome to the 460 Gym Interface" );
        System.out.println( "--------------------------------" );
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

    /**
     * Message to print on exit
     */
    public static void printExitMessage() {
        System.out.println( "\nThanks for using the 460 Gym Interface!" );
    }

}
