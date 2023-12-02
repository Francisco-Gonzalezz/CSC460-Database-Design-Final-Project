package enums;

public enum MembershipLevelEnum {
    BASIC( "BASIC" ), GOLD( "GOLD" ), DIAMOND( "DIAMOND" );

    private String text;

    private MembershipLevelEnum( String level ) {
        this.text = level;
    }

    public String getLevel() {
        return text;
    }
}
