/*
 * Enum name: MembershipLevelEnum
 * Jake Bode, Frankie Gonzalez
 * Purpose: This enum defines the different membership levels that are 
 *  possible within the current gym membership structure.
 * Public getter for getLevel(), the membership level as a string, is
 *  the only public method
 */

package enums;

public enum MembershipLevelEnum {

    // only membership levels in the current schema
    BASIC( "BASIC" ), GOLD( "GOLD" ), DIAMOND( "DIAMOND" );

    private String level;

    private MembershipLevelEnum( String level ) {
        this.level = level;
    }

    // Getter for the enum var value
    public String getLevel() {
        return level;
    }
}
