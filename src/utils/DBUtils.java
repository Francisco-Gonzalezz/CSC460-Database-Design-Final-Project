package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
            int result = stmt.executeUpdate( createInsertMemberQuery( member ) );
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
            float accountBalance = result.getFloat( "accountBalance" );
            MembershipLevelEnum membershipLevel = MembershipLevelEnum.valueOf( result.getString( "membershipLevel" ) );
            member = new GymMember(
                memberId,
                firstName,
                lastname,
                phoneNumber,
                email,
                membershipLevel,
                accountBalance );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve member details" );
            return null;
        }
        return member;
    }

    /**
     * Queries db for all packages and their cost
     * @param dbConnection Connection to db
     * @return Map of package name and it's associated cost
     */
    public static Map<String, Float> getPackagesAndPrices( Connection dbConnection ) {
        Map<String, Float> packages = new HashMap<>();
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery( "SELECT * FROM Package" );
            while ( result.next() ) {
                String packageName = result.getString( "packagename" );
                Float cost = result.getFloat( "Cost" );
                packages.put( packageName, cost );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve all packages" );
            return null;
        }

        return packages;
    }

    /**
     * Retrieves all the names of the rentals that the member has made and sums all like rentals and places them into
     * a map.
     * @param memberID ID of member
     * @param dbConnection Connection to DB
     * @return Map<String,Integer> Containing rental names and the quantity borrowed from member
     */
    public static Map<String, Integer> getCheckoutRentalsForMember( int memberID, Connection dbConnection ) {
        Map<String, Integer> rentals = new HashMap<>();
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery( generateCheckoutRentalSQL( memberID ) );
            while ( result.next() ) {
                String itemName = result.getString( "RentalItem.ItemName" );
                int quantityBorrowed = result.getInt( "RentalLog.Quantity" );
                // Add up all quantities of the same type of item
                if ( rentals.containsKey( itemName ) ) {
                    rentals.put( itemName, rentals.get( itemName ) + quantityBorrowed );
                } else {
                    rentals.put( itemName, quantityBorrowed );
                }
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to find all rentals" );
            return null;
        }
        return rentals;
    }

    /**
     * Generate a SQL Query returning information about rentals that a member has
     * @param memberID
     * @return SQL Query as descibed above
     */
    private static String generateCheckoutRentalSQL( int memberID ) {
        StringBuilder sqlBuilder = new StringBuilder(
            "SELECT RentalLog.ItemNumber, RentalLog.MemberID RentalItem.ItemNumber, RentalItem.ItemName, RentalLog.Quantity\n" );
        sqlBuilder.append( "FROM RentalLog\n" );
        sqlBuilder.append( "INNER JOIN RentalItem ON RentalLog.ItemNumber = RentalItem.ItemNumber\n" );
        sqlBuilder.append( "WHERE RentalLog.CheckIn is null AND RentalLog.MemberID = " + memberID );

        return sqlBuilder.toString();
    }

    /**
     * Removes a quantity of an item 
     * @param itemName Name of item
     * @param quantityToRemove Quantity to remove
     * @param dbConnection Connection to DB
     */
    public static void removeQuantityFromRentalItems( String itemName, int quantityToRemove, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            int result = stmt
                .executeUpdate(
                    "UPDATE RentalItem SET Quantity = Quantity - " + quantityToRemove + " WHERE ItemName = '" + itemName
                        + "'" );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to update rental item quantity" );
        }
    }

}
