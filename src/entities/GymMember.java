package entities;

import enums.MembershipLevelEnum;

public class GymMember {

    private int memberID;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private MembershipLevelEnum membershipLevel;

    private float accountBalance;

    public GymMember( String firstName, String lastName, String phoneNumber, String email ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.membershipLevel = MembershipLevelEnum.BASIC;
        this.accountBalance = 0;
    }

    public GymMember(
        int memberId,
        String firstName,
        String lastName,
        String phoneNumber,
        String email,
        MembershipLevelEnum membershipLevel,
        float accountBalance ) {
        this.memberID = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.membershipLevel = membershipLevel;
        this.accountBalance = accountBalance;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public double getDiscount() {
        if ( membershipLevel.equals( MembershipLevelEnum.DIAMOND ) ) {
            return 0.3;
        } else if ( membershipLevel.equals( MembershipLevelEnum.GOLD ) ) {
            return .2;
        } else {
            return 0;
        }
    }

    public static MembershipLevelEnum determineLevel( float amountSpent ) {
        if ( amountSpent >= 1500 ) {
            return MembershipLevelEnum.DIAMOND;
        } else if ( amountSpent >= 1000 ) {
            return MembershipLevelEnum.GOLD;
        } else {
            return MembershipLevelEnum.BASIC;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "Member Name: " + firstName + " " + lastName + "\n" );
        sb.append( "Member Phone#: " + phoneNumber + "\n" );
        sb.append( "Member Email: " + email + "\n" );
        sb.append( "Member ID: " + getMemberID() );
        return sb.toString();
    }

    // Getters and Setters

    public float getBalance() {
        return accountBalance;
    }

    public void setBalance( float balance ) {
        this.accountBalance = balance;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID( int memberID ) {
        this.memberID = memberID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber( String phoneNumber ) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public void setMembershipLevel( MembershipLevelEnum level ) {
        this.membershipLevel = level;
    }

    public String getMembershipLevel() {
        return membershipLevel.getLevel();
    }

}
