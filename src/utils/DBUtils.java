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

    private static final String BODE1 = "BODE1";
    private static final String FRANCISCOG852 = "FRANCISCOG852";
    private static final String PERIOD = ".";

    // Table names
    private static final String MEMBER_TABLE = "MEMBER";
    private static final String MEMBERSHIPLEVEL_TABLE = "MEMBERSHIPLEVEL";
    private static final String MEMBER_CLASS_TABLE = "MEMBERCLASS";
    private static final String CLASS_TABLE = "CLASS";
    private static final String TRAINER_TABLE = "TRAINER";
    private static final String COURSE_TABLE = "COURSE";
    private static final String PACKAGE_TABLE = "PACKAGE";
    private static final String COURSE_PACKAGE_TABLE = "COURSEPACKAGE";
    private static final String MEMBER_PACKAGE_TABLE = "MEMBERPACKAGE";
    private static final String TRANSACTION_TABLE = "TRANSACTION";
    private static final String RENTAL_ITEM_TABLE = "RENTALITEM";
    private static final String RENTAL_LOG_TABLE = "RENTALLOG";

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
            ResultSet numberGen = null;
            int memberID;
            if ( !isTableEmpty( BODE1 + PERIOD + MEMBER_TABLE, dbConnection ) ) {
                memberID = 1;
            } else {
                numberGen = stmt
                    .executeQuery(
                        "SELECT " + FRANCISCOG852 + PERIOD + "MEMBER_SEQ.NEXTVAL FROM " + BODE1 + PERIOD
                            + MEMBER_TABLE );
                numberGen.next();
                memberID = numberGen.getInt( "NEXTVAL" );
            }
            member.setMemberID( memberID );
            int result = stmt.executeUpdate( createInsertMemberQuery( member ) );
            stmt.close(); // Fun fact this will close the result sets too!
            return true;
        } catch ( SQLException e ) {
            return false;
        }
    }

    /**
     * 
     * @param tableName
     * @param dbConnection
     * @return True if table is not empty and false if it is
     */
    private static boolean isTableEmpty( String tableName, Connection dbConnection ) {
        boolean bool = false;
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery( "SELECT * FROM " + tableName );
            bool = result.next();
        } catch ( SQLException e ) {
            System.out.println( "Something went wrong!" );
        }
        return bool;
    }

    /**
     * Creates a query to insert a member into the db using attributes from the GymMember object
     * @param member GymMember object to insert into the DB
     * @return The dynamic query to insert into a db
     */
    private static String createInsertMemberQuery( GymMember member ) {
        StringBuilder sqlBuilder = new StringBuilder( "INSERT INTO " + BODE1 + PERIOD + MEMBER_TABLE + " VALUES(\n" );
        sqlBuilder.append( member.getMemberID() + ",\n" );
        sqlBuilder.append( "'" + member.getFirstName() + "',\n" );
        sqlBuilder.append( "'" + member.getLastName() + "',\n" );
        sqlBuilder.append( "'" + member.getPhoneNumber() + "',\n" );
        sqlBuilder.append( "'" + member.getEmail() + "',\n" );
        sqlBuilder.append( "'" + member.getMembershipLevel() + "',\n" );
        sqlBuilder.append( member.getBalance() + "\n" );
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
            ResultSet result = stmt
                .executeQuery( "SELECT * FROM " + BODE1 + PERIOD + MEMBER_TABLE + " WHERE MEMBERID = " + memberId );
            if ( result.next() ) {
                String firstName = result.getString( "FNAME" );
                String lastname = result.getString( "LNAME" );
                String phoneNumber = result.getString( "PHONENUM" );
                String email = result.getString( "EMAIL" );
                MembershipLevelEnum membershipLevel = MembershipLevelEnum
                    .valueOf( result.getString( "MEMBERSHIPLEVEL" ) );
                float accountBalance = result.getFloat( "ACCOUNTBALANCE" );
                member = new GymMember(
                    memberId,
                    firstName,
                    lastname,
                    phoneNumber,
                    email,
                    membershipLevel,
                    accountBalance );
            }
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
            ResultSet result = stmt.executeQuery( "SELECT * FROM " + BODE1 + PERIOD + PACKAGE_TABLE );
            while ( result.next() ) {
                String packageName = result.getString( "PACKAGENAME" );
                Float cost = result.getFloat( "COST" );
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
                String itemName = result.getString( RENTAL_ITEM_TABLE + PERIOD + "ITEMNAME" );
                int quantityBorrowed = result.getInt( RENTAL_LOG_TABLE + PERIOD + "QUANTITY" );
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
            "SELECT RENTALLOG.ITEMNUM, RENTALLOG.MEMBERID, RENTALITEM.ITEMNUM, RENTALITEM.ITEMNAME, RENTALLOG.QUANTITY\n" );
        sqlBuilder.append( "FROM " + BODE1 + PERIOD + RENTAL_LOG_TABLE + " RentalLog\n" );
        sqlBuilder
            .append(
                "INNER JOIN " + BODE1 + PERIOD + RENTAL_ITEM_TABLE + " ON RENTALLOG.ITEMNUM = RENTALITEM.ITEMNUM\n" );
        sqlBuilder.append( "WHERE RENTALLOG.RETURNTIME is null AND RENTALLOG.MEMBERID = " + memberID );

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
                    "UPDATE " + BODE1 + PERIOD + RENTAL_ITEM_TABLE + " SET QUANTITY = QUANTITY - " + quantityToRemove
                        + " WHERE ITEMNAME = '" + itemName.toUpperCase() + "'" );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to update rental item quantity" );
        }
    }

    /**
     * Searches DB for all classes that the member is currently enrolled in and decrements the enrollment number by one.
     * Also removes their entries in MEMBERCLASS table after
     * @param memberID
     * @param dbConnection
     */
    public static void removeMemberFromAllTheirClasses( int memberID, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt
                .executeQuery(
                    "SELECT CLASSNUM FROM " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " WHERE MEMBERID = " + memberID );
            // Grab all the class numbers and update the enrollment numbers of them
            while ( result.next() ) {
                int classnum = result.getInt( "CLASSNUM" );
                // Decrement the enrollment numbers
                int returnCode = stmt
                    .executeUpdate(
                        "UPDATE " + BODE1 + PERIOD + CLASS_TABLE + " SET ENROLLMENT = ENROLLMENT - 1 WHERE CLASSNUM = "
                            + classnum );
                stmt
                    .executeUpdate(
                        "DELETE FROM " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " WHERE MEMBERID = " + memberID
                            + " AND CLASSNUM = " + classnum );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to remove member from their classes" );
        }
    }

    public static void saveChangesToMember( GymMember member, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            int returnCode = stmt.executeUpdate( generateMemberUpdate( member ) );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to update record for member" );
        }
    }

    private static String generateMemberUpdate( GymMember member ) {
        StringBuilder sqlBuilder = new StringBuilder( "UPDATE " + BODE1 + PERIOD + MEMBER_TABLE + ",\n" );
        sqlBuilder.append( "SET FNAME = '" + member.getFirstName() + "',\n" );
        sqlBuilder.append( "SET LNAME = '" + member.getLastName() + "',\n" );
        sqlBuilder.append( "SET PHONENUM = '" + member.getPhoneNumber() + "',\n" );
        sqlBuilder.append( "SET EMAIL = '" + member.getEmail() + "',\n" );
        sqlBuilder.append( "SET MEMBERSHIPLEVEL = '" + member.getMembershipLevel() + "',\n" );
        sqlBuilder.append( "SET ACCOUNTBALANCE = " + member.getBalance() + "\n" );
        sqlBuilder.append( "WHERE MEMBERID = " + member.getMemberID() );

        return sqlBuilder.toString();
    }

    public static void removeMemberFromDB( GymMember member, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            int returnCode = stmt
                .executeUpdate(
                    "DELETE FROM " + BODE1 + PERIOD + MEMBER_TABLE + " WHERE MEMBERID = " + member.getMemberID() );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to remove member from db" );
        }
    }

}
