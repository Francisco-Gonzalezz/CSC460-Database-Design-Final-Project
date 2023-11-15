
/**
 * Utility functions to help verify commandline arguments
 * @author Francisco Gonzalez
 * @version 1.0
 */
public class CommandLineArgumentsUtils {

    private static final String USER_FLAG = "-u";

    private static final String PASSWORD_FLAG = "-p";

    /**
     * Searches for the username flag and returns the argument directly after that for the username
     * @param commandLineArguments String array of cli arguments
     * @return username that was given as a commandline argument null if unable to be found
     */
    public static String getUsername( String[] commandLineArguments ) {
        for ( int i = 0 ; i < commandLineArguments.length - 1 ; i++ ) {
            String argument = commandLineArguments[i];
            if ( argument.equals( USER_FLAG ) ) {
                String username = commandLineArguments[i + 1];
                return username;
            }
        }
        return null;
    }

    /**
     * Searches for the password flag and returns the arguments directly after that for the password
     * @param commandLineArguments
     * @return password that was given as a commandline argument and null if unable to be found
     */
    public static String getPassword( String[] commandLineArguments ) {
        for ( int i = 0 ; i < commandLineArguments.length - 1 ; i++ ) {
            String argument = commandLineArguments[i];
            if ( argument.equals( PASSWORD_FLAG ) ) {
                String password = commandLineArguments[i + 1];
                return password;
            }
        }
        return null;
    }
}
