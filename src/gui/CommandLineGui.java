package gui;

import java.sql.Connection;
import java.util.Scanner;

import exit_thread.ExitThread;
import operations.MemberOperations;
import utils.PrintUtils;

public class CommandLineGui {

    // Static integers for main menu options
    private static final int MIN_OPTIONS = 1; // Miniumn integer option for every menu
    private static final int MAX_MAIN_MENU_OPTIONS = 4; // Maximum integer option for the main menu
    private static final int MEMBER_MENU_OPTION = 1;
    private static final int COURSE_MENU_OPTION = 2;
    private static final int ADMIN_MENU_OPTION = 3;
    private static final int EXIT_MENU_OPTION = 4;

    private Connection dbConnection; // Connection to the oracle db

    private Scanner scanner; // Scanner to read from stdin

    private MemberOperations memberOperations;

    /**
     * Creates a new instance of a CommandLineGui and access to a SQL DB Connection
     * @param dbConnection
     * @return a new instance of CommandLineGui
     */
    public CommandLineGui( Connection dbConnection ) {
        this.dbConnection = dbConnection;
        scanner = new Scanner( System.in );
        memberOperations = new MemberOperations( dbConnection, scanner );
        // Give objects that need to be closed to the ExitThread to free resources at the end
        Runtime.getRuntime().addShutdownHook( new ExitThread( dbConnection, scanner ) );
    }

    /**
     * Starts the command line interface to interact with the gym database
     */
    public void startGui() {
        PrintUtils.printWelcomeMessage();
        System.out.println( "\n" );

        while ( true ) {
            PrintUtils.promptUserToSelectTypeOfOperation();
            PrintUtils.printStandardOptions();
            getOperationSelection();
        }
    }

    /**
     * Prints the options that the user can select and take input from stdin for the selection
     */
    private void getOperationSelection() {
        String userInput = null; // String form of the userinput
        int option = 0; // Will convert the string input to be an int
        while ( true ) {
            System.out.println();
            userInput = scanner.nextLine();
            try {
                option = Integer.valueOf( userInput );
            } catch ( NumberFormatException e ) {
                PrintUtils.printInvalidOptionMessage();
            }

            if ( option < MIN_OPTIONS || option > MAX_MAIN_MENU_OPTIONS ) {
                PrintUtils.printInvalidOptionMessage();
                continue;
            }
            openOperationsMenu( option );
            break;
        }
    }

    /**
     * Directs control flow to whatever operations menu should be open
     * @param option
     */
    private void openOperationsMenu( int option ) {
        switch ( option ) {
            case MEMBER_MENU_OPTION:
                memberOperations.openMenu();
                break;
            case COURSE_MENU_OPTION:
                break;
            case ADMIN_MENU_OPTION:
                break;
            case EXIT_MENU_OPTION:
                System.exit( 0 );
        }
    }

}
