package utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Class;
import entities.GymMember;
import entities.Transaction;
import enums.MembershipLevelEnum;

public class DBUtils {

    private static final String BODE1 = "BODE1";
    private static final String FRANCISCOG852 = "FRANCISCOG852";
    private static final String PERIOD = ".";
    private static final String ALL_SEQ = "ALL_SEQ";
    private static final String SEQUENCE = FRANCISCOG852 + PERIOD + ALL_SEQ;

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
                    .executeQuery( "SELECT " + SEQUENCE + PERIOD + "NEXTVAL FROM " + BODE1 + PERIOD + MEMBER_TABLE );
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
            Statement updateStatement = dbConnection.createStatement();
            while ( result.next() ) {
                int classnum = result.getInt( "CLASSNUM" );
                // Decrement the enrollment numbers
                updateStatement
                    .executeUpdate(
                        "UPDATE " + BODE1 + PERIOD + CLASS_TABLE + " SET ENROLLMENT = ENROLLMENT - 1 WHERE CLASSNUM = "
                            + classnum );
                // Remove from member class table
                updateStatement
                    .executeUpdate(
                        "DELETE FROM " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " WHERE MEMBERID = " + memberID );
            }
            updateStatement.close();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to remove member from their classes" );
            System.out.println( e.getMessage() );
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
        StringBuilder sqlBuilder = new StringBuilder( "UPDATE " + BODE1 + PERIOD + MEMBER_TABLE + " SET\n" );
        sqlBuilder.append( "MEMBERID = " + member.getMemberID() + ",\n" );
        sqlBuilder.append( "FNAME = '" + member.getFirstName() + "',\n" );
        sqlBuilder.append( "LNAME = '" + member.getLastName() + "',\n" );
        sqlBuilder.append( "PHONENUM = '" + member.getPhoneNumber() + "',\n" );
        sqlBuilder.append( "EMAIL = '" + member.getEmail() + "',\n" );
        sqlBuilder.append( "MEMBERSHIPLEVEL = '" + member.getMembershipLevel() + "',\n" );
        sqlBuilder.append( "ACCOUNTBALANCE = " + member.getBalance() + "\n" );
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

    public static int generateIDNumberFromSequence( Connection dbConnection ) {
        int generatedID = 0;
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt
                .executeQuery( "SELECT " + SEQUENCE + PERIOD + "NEXTVAL FROM " + BODE1 + PERIOD + MEMBER_TABLE );
            result.next();
            generatedID = result.getInt( "NEXTVAL" );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to generate an ID from sequence" );
        }
        return generatedID;
    }

    public static void saveNewTransaction( Transaction transaction, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate( generateInsertTransaction( transaction ) );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to persist transaction" );
        }
    }

    private static String generateInsertTransaction( Transaction transaction ) {
        StringBuilder sqlBuilder = new StringBuilder(
            "INSERT INTO " + BODE1 + PERIOD + TRANSACTION_TABLE + " VALUES (\n" );
        sqlBuilder.append( transaction.getTransactionID() + ",\n" );
        sqlBuilder.append( transaction.getMemberID() + ",\n" );
        sqlBuilder.append( "'" + transaction.getXactType() + "',\n" );
        sqlBuilder.append( "SYSDATE,\n" );
        sqlBuilder.append( transaction.getAmount() );
        sqlBuilder.append( ")" );
        return sqlBuilder.toString();
    }

    public static void saveNewClass( Class newClass, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate( generateInsertClassQuery( newClass ) );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to save new class" );
        }
    }

    private static String generateInsertClassQuery( Class newClass ) {
        StringBuilder sqlBuilder = new StringBuilder( "INSERT INTO " + BODE1 + PERIOD + CLASS_TABLE + " VALUES (" );
        sqlBuilder.append( newClass.getClassNum() + ",\n" );
        sqlBuilder.append( newClass.getCourseID() + ",\n" );
        sqlBuilder.append( newClass.getTrainerID() + ",\n" );
        sqlBuilder.append( new Date( newClass.getStartTime().getTime() ) + ",\n" );
        sqlBuilder.append( newClass.getClassDuration() + ",\n" );
        sqlBuilder.append( newClass.getStartDate() + ",\n" );
        sqlBuilder.append( newClass.getEndDate() + ",\n" );
        sqlBuilder.append( newClass.getCurrentEnrollment() + ",\n" );
        sqlBuilder.append( newClass.getCapacity() );
        sqlBuilder.append( ")" );
        return sqlBuilder.toString();
    }

    public static void addMemberToPackageCourses( GymMember member, String packageName, Connection dbConnection ) {
        try {
            // Grab all the courses that are in the package the member bought
            PreparedStatement preparedStatement = dbConnection
                .prepareStatement(
                    "SELECT COURSEID FROM " + BODE1 + PERIOD + COURSE_PACKAGE_TABLE + " WHERE PACKAGENAME = ?" );
            preparedStatement.setString( 1, packageName );
            ResultSet courseIDs = preparedStatement.executeQuery();
            while ( courseIDs.next() ) {
                int courseID = courseIDs.getInt( "COURSEID" );
                PreparedStatement getClasses = dbConnection
                    .prepareStatement( "SELECT * FROM " + BODE1 + PERIOD + CLASS_TABLE + " WHERE COURSEID = ?" );
                getClasses.setInt( 1, courseID );
                ResultSet classSet = getClasses.executeQuery(); // This contains the classes that the member should be enrolled in
                List<Class> classes = formClassList( classSet );
                for ( Class gymClass : classes ) {
                    gymClass.addStudent(); // Update enrollment
                    saveClassInfo( gymClass, dbConnection ); // save changes
                    addToMemberClassTable( member, gymClass, dbConnection ); // Add record to memberclass table
                }
                getClasses.close();
            }
            preparedStatement.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to add member to all courses necessary" );
            System.out.println( e.getMessage() );
        }
    }

    public static void addToMemberClassTable( GymMember member, Class gymClass, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement( "INSERT INTO " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " VALUES (?, ?)" );
            stmt.setInt( 1, member.getMemberID() );
            stmt.setInt( 2, gymClass.getClassNum() );
            stmt.executeUpdate();
        } catch ( SQLException e ) {
            System.out.println( "Unable to update memberclass table" );
            System.out.println( e.getMessage() );
        }
    }

    public static void saveClassInfo( Class gymClass, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection.prepareStatement( generateSaveClassQuery() );
            stmt.setInt( 1, gymClass.getClassNum() );
            stmt.setInt( 2, gymClass.getCourseID() );
            stmt.setInt( 3, gymClass.getTrainerID() );
            stmt.setTimestamp( 4, gymClass.getStartTime() );
            stmt.setFloat( 5, gymClass.getClassDuration() );
            stmt.setDate( 6, gymClass.getStartDate() );
            stmt.setDate( 7, gymClass.getEndDate() );
            stmt.setInt( 8, gymClass.getCurrentEnrollment() );
            stmt.setInt( 9, gymClass.getCapacity() );
            stmt.setInt( 10, gymClass.getClassNum() );
            stmt.executeUpdate();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to save class info" );
        }
    }

    private static String generateSaveClassQuery() {
        StringBuilder sqlBuilder = new StringBuilder( "UPDATE " + BODE1 + PERIOD + CLASS_TABLE + " SET \n" );
        sqlBuilder.append( "CLASSNUM = ?, " );
        sqlBuilder.append( "COURSEID = ?, " );
        sqlBuilder.append( "TRAINERID = ?, " );
        sqlBuilder.append( "STARTTIME = ?, " );
        sqlBuilder.append( "DURATION = ?, " );
        sqlBuilder.append( "STARTDATE = ?, " );
        sqlBuilder.append( "ENDDATE = ?, " );
        sqlBuilder.append( "ENROLLMENT = ?, " );
        sqlBuilder.append( "CAPACITY = ? " );
        sqlBuilder.append( "WHERE CLASSNUM = ?" );
        return sqlBuilder.toString();
    }

    private static List<Class> formClassList( ResultSet classes ) {
        List<Class> classList = new ArrayList<>();

        try {
            int classNum;
            int courseID;
            int trainerID;
            Timestamp startTime;
            float classDuration;
            Date startDate;
            Date endDate;
            int currentEnrollment;
            int capacity;
            while ( classes.next() ) {
                classNum = classes.getInt( "CLASSNUM" );
                courseID = classes.getInt( "COURSEID" );
                trainerID = classes.getInt( "TRAINERID" );
                startTime = classes.getTimestamp( "STARTTIME" );
                classDuration = classes.getFloat( "DURATION" );
                startDate = classes.getDate( "STARTDATE" );
                endDate = classes.getDate( "ENDDATE" );
                currentEnrollment = classes.getInt( "ENROLLMENT" );
                capacity = classes.getInt( "CAPACITY" );
                Class toAdd = new Class(
                    classNum,
                    courseID,
                    trainerID,
                    startTime,
                    classDuration,
                    startDate,
                    endDate,
                    currentEnrollment,
                    capacity );
                classList.add( toAdd );
            }
        } catch ( SQLException e ) {
            System.out.println( "Unable to create list of classes" );
        }

        return classList;
    }

    public static float getAmountMemberSpent( GymMember member, Connection dbConnection ) {
        Float amount = null;
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT SUM(AMOUNT) FROM " + BODE1 + PERIOD + TRANSACTION_TABLE
                        + " WHERE MEMBERID = ? AND XACTTYPE = ?" );
            stmt.setInt( 1, member.getMemberID() );
            stmt.setString( 2, "PURCHASE" );
            ResultSet result = stmt.executeQuery();
            if ( result.next() ) {
                amount = result.getFloat( "SUM(AMOUNT)" );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to determine how much member has spent" );
        }
        return amount;
    }

    public static Map<String, String> getNegativeAccountUsers( Connection dbConnection ) {
        Map<String, String> namesAndNums = new HashMap<>();
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT FNAME, LNAME, PHONENUM FROM " + BODE1 + PERIOD + MEMBER_TABLE
                        + " WHERE ACCOUNTBALANCE < 0" );
            ResultSet result = stmt.executeQuery();
            while ( result.next() ) {
                String firstName = result.getString( "FNAME" );
                String lastName = result.getString( "LNAME" );
                String fullName = firstName + " " + lastName;
                String phoneNum = result.getString( "PHONENUM" );
                namesAndNums.put( fullName, phoneNum );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve negative balance accounts" );
        }
        return namesAndNums;
    }

    public static
        Map<Timestamp, Float>
        getMemberScheduleForMonth( GymMember member, int month, Connection dbConnection ) {

        // TODO: Throwing an exception here need to figure out why

        Map<Timestamp, Float> startToEnd = new HashMap<>();
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.clear();
        maxCalendar.set( Calendar.MONTH, month );
        maxCalendar.set( Calendar.YEAR, Year.now().getValue() );
        maxCalendar.set( Calendar.DAY_OF_MONTH, maxCalendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.clear();
        minCalendar.set( Calendar.MONTH, month );
        minCalendar.set( Calendar.YEAR, Year.now().getValue() );
        minCalendar.set( Calendar.DAY_OF_MONTH, minCalendar.getActualMinimum( Calendar.DAY_OF_MONTH ) );
        Date maxDate = new Date( maxCalendar.getTime().getTime() );
        Date minDate = new Date( minCalendar.getTime().getTime() );
        try {
            PreparedStatement memberClassStmt = dbConnection
                .prepareStatement(
                    "SELECT CLASSNUM FROM " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " WHERE MEMBERID = ?" );
            memberClassStmt.setInt( 1, member.getMemberID() );
            ResultSet classNumsForMember = memberClassStmt.executeQuery();
            while ( classNumsForMember.next() ) {
                int classNum = classNumsForMember.getInt( "CLASSNUM" );
                PreparedStatement classInfo = dbConnection
                    .prepareStatement(
                        "SELECT STARTTIME, DURATION  FROM " + BODE1 + PERIOD + CLASS_TABLE + " WHERE CLASSNUM = ?" );
                classInfo.setInt( 1, classNum );
                ResultSet infoResult = classInfo.executeQuery();
                Timestamp startTime;
                float duration;
                while ( infoResult.next() ) {
                    Date endDate = infoResult.getDate( "ENDDATE" );
                    if ( endDate.after( minDate ) && endDate.before( maxDate ) ) {
                        startTime = infoResult.getTimestamp( "STARTTIME" );
                        duration = infoResult.getFloat( "DURATION" );
                        startToEnd.put( startTime, duration );
                    }
                }
                classInfo.close();
            }
            memberClassStmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve member schedule" );
            System.out.println( e.getMessage() );
        }

        return startToEnd;
    }

}
