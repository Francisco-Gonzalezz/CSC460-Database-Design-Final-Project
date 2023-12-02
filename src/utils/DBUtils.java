package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import entities.GymMember;
import enums.MembershipLevelEnum;

public class DBUtils {

    private DBUtils() {
    }

    /**
     * Inserts a new gym member into the DB 
     * @param member Member to add
     * @param dbConnection Connection to the DB
     */
    public static boolean addNewGymMemberToDB( GymMember member, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery( createInsertMemberQuery( member ) ); // Unsure if result set is needed by why not have this just in case!
            stmt.close(); // Fun fact this will close the result sets too!
            return true;
        } catch ( SQLException e ) {
            return false;
        }
    }

    /**
     * Creates a query to insert a member into the db using attributes from the GymMember object
     * @param member GymMember object to insert into the DB
     * @return The dynamic query to insert into a db
     */
    private static String createInsertMemberQuery( GymMember member ) {
        StringBuilder sqlBuilder = new StringBuilder( "INSERT INTO Member VALUES(\n" );
        sqlBuilder.append( "member_sequence.nextval,\n" );
        sqlBuilder.append( "'" + member.getFirstName() + "',\n" );
        sqlBuilder.append( "'" + member.getLastName() + "',\n" );
        sqlBuilder.append( "'" + member.getPhoneNumber() + "',\n" );
        sqlBuilder.append( "'" + member.getEmail() + "',\n" );
        sqlBuilder.append( "'" + member.getMembershipLevel() + "'" );
        sqlBuilder.append( ")" );
        return sqlBuilder.toString();
    }

    /**
     * Constructs a new Gym member object from the information returned by SELECT statement to DB
     * @param memberId Id of member to search for
     * @param dbConnection Connection to the DB
     * @return GymMember object that is associated with the given memberID
     */
    public static GymMember retrieveMemberFromID( int memberId, Connection dbConnection ) {
        GymMember member = null;
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery( "SELECT * FROM Member WHERE memberID = " + memberId ); // This should only return a single result
            result.next(); // Move the cursor 
            String firstName = result.getString( "firstname" );
            String lastname = result.getString( "lastname" );
            String phoneNumber = result.getString( "phonenumber" );
            String email = result.getString( "email" );
            MembershipLevelEnum membershipLevel = MembershipLevelEnum.valueOf( result.getString( "membershipLevel" ) );
            member = new GymMember( memberId, firstName, lastname, phoneNumber, email, membershipLevel );
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve member details" );
            return null;
        }
        return member;
    }

}
