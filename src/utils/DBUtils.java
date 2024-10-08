/**
 * @author Francisco Gonzalez
 * @author Jake Bode
 * @version 1.0
 * Class: DBUtils.java
 * Purpose: Contains a variety of methods that let the program interface interact with the
 *  Oracle database connection (see method comments for details).
 * 
 * Utilizes:
 *  - java.sql.*
 *  - java.time.*
 *  - java.util.*
 * 
 * No constructor
 * 
 * Methods/constants: see method comments below
 */

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
import entities.Course;
import entities.CoursePackage;
import entities.GymMember;
import entities.RentalItem;
import entities.RentalLogEntry;
import entities.Trainer;
import entities.Transaction;
import enums.MembershipLevelEnum;

public class DBUtils {

    // SQL/database constants
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
    public static Map<String, Integer> getCheckoutRentalsForMember( GymMember member, Connection dbConnection ) {
        Map<String, Integer> rentals = new HashMap<>();
        try {
            PreparedStatement getLogInfo = dbConnection
                .prepareStatement(
                    "SELECT ITEMNUM, QUANTITY, RETURNED FROM " + BODE1 + PERIOD + RENTAL_LOG_TABLE
                        + " WHERE MEMBERID = ? AND RETURNED = 0" );
            getLogInfo.setInt( 1, member.getMemberID() );
            ResultSet logInfo = getLogInfo.executeQuery();
            while ( logInfo.next() ) {
                int itemNum = logInfo.getInt( "ITEMNUM" );
                int quantityBorrowed = logInfo.getInt( "QUANTITY" );
                String itemName = getItemName( itemNum, dbConnection );
                if ( rentals.containsKey( itemName ) ) {
                    int stored = rentals.get( itemName );
                    stored += quantityBorrowed;
                    rentals.put( itemName, stored );
                } else {
                    rentals.put( itemName, quantityBorrowed );
                }

            }
            getLogInfo.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve member's rental items" );
            System.out.println( e.getMessage() );
        }
        return rentals;
    }

    /**
     * Get item name from the item num
     * @param itemNum
     * @param dbConnection
     * @return
     */
    private static String getItemName( int itemNum, Connection dbConnection ) {
        String name = "";
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT ITEMNAME FROM " + BODE1 + PERIOD + RENTAL_ITEM_TABLE + " WHERE ITEMNUM = ?" );
            stmt.setInt( 1, itemNum );
            ResultSet result = stmt.executeQuery();
            result.next();
            name = result.getString( "ITEMNAME" );
        } catch ( SQLException e ) {
            System.out.println( "Unable to find item name" );
        }

        return name;
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

    /**
     * Updates a member information in the DB
     * @param member Member to update
     * @param dbConnection Connection to DB
     */
    public static void saveChangesToMember( GymMember member, Connection dbConnection ) {
        float amountSpent = getAmountSpentByMember( member, dbConnection );
        member.setMembershipLevel( GymMember.determineLevel( amountSpent ) );
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate( generateMemberUpdate( member ) );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to update record for member" );
        }
    }

    /**
     * Updates a member information Query
     * @param member Member to update
     * @return
     */
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

    /**
     * Deletes a member from the DB
     * @param member
     * @param dbConnection
     */
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

    /**
     * Generates 'Unique' id from db using the sequence
     * @param dbConnection
     * @return
     */
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

    /**
     * Saves a new transaction log entry into the DB
     * @param transaction
     * @param dbConnection
     */
    public static void saveNewTransaction( Transaction transaction, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate( generateInsertTransaction( transaction ) );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to persist transaction" );
        }
    }

    /**
     * Creates query to add transaction to the DB
     * @param transaction
     * @return
     */
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

    /**
     * Fetches the course id associated with a category and catalog number
     *  from the Course table within the database.
     */
    public static int getCourseId( String category, int catalogNum, Connection dbConnection ) {
        int courseId = 0;
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet courseIdResult = stmt
                .executeQuery(
                    "SELECT COURSEID FROM " + BODE1 + PERIOD + COURSE_TABLE + " WHERE CATEGORY='" + category
                        + "' AND CATALOGNUM=" + catalogNum );
            courseIdResult.next();
            courseId = courseIdResult.getInt( "COURSEID" );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to add new class with this course" );
        }
        return courseId;
    }

    /**
     * Saves a new class to Db
     * @param newClass
     * @param dbConnection
     */
    public static void saveNewClass( Class newClass, Connection dbConnection ) {
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.executeUpdate( generateInsertClassQuery( newClass ) );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to add new class" );
        }
    }

    /**
     * Generate a query to save a new class to the DB
     * @param newClass
     * @return
     */
    private static String generateInsertClassQuery( Class newClass ) {
        StringBuilder sqlBuilder = new StringBuilder( "INSERT INTO " + BODE1 + PERIOD + CLASS_TABLE + " VALUES (" );
        sqlBuilder.append( newClass.getClassNum() + ",\n" );
        sqlBuilder.append( newClass.getCourseID() + ",\n" );
        sqlBuilder.append( newClass.getTrainerID() + ",\n" );
        sqlBuilder.append( "TO_TIMESTAMP('" + newClass.getStartTime() );
        sqlBuilder.deleteCharAt( sqlBuilder.length() - 1 );
        sqlBuilder.deleteCharAt( sqlBuilder.length() - 1 );
        sqlBuilder.append( "', 'YYYY-MM-DD HH24:MI:SS'),\n" );
        sqlBuilder.append( newClass.getClassDuration() + ",\n" );
        sqlBuilder.append( "TO_DATE('" + newClass.getStartDate() + "', 'YYYY-MM-DD'),\n" );
        sqlBuilder.append( "TO_DATE('" + newClass.getEndDate() + "', 'YYYY-MM-DD'),\n" );
        sqlBuilder.append( newClass.getCurrentEnrollment() + ",\n" );
        sqlBuilder.append( newClass.getCapacity() );
        sqlBuilder.append( ")" );
        return sqlBuilder.toString();
    }

    /**
     * Adds a member to every course in a given package
     * @param member
     * @param packageName
     * @param dbConnection
     */
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

    /**
     * Add a tuple in member class table
     * @param member
     * @param gymClass
     * @param dbConnection
     */
    public static void addToMemberClassTable( GymMember member, Class gymClass, Connection dbConnection ) {
        try {
            PreparedStatement testStmt = dbConnection
                .prepareStatement(
                    "SELECT CLASSNUM FROM " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " WHERE MEMBERID = ?" );
            testStmt.setInt( 1, member.getMemberID() );
            ResultSet classNumSet = testStmt.executeQuery();
            while ( classNumSet.next() ) {
                if ( classNumSet.getInt( 1 ) == gymClass.getClassNum() ) {
                    return;
                }
            }

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

    /**
    * Updates an existing class information
    * @param gymClass Class to update
    * @param dbConnection Connection to DB
    */
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

    /**
     * Creates query to save an existing class
     * @return String query with place holders
     */
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

    /**
     * Gets the trainer Id associated with the trainer selected by the user by first
     *  and last name.
     */
    public static int getTrainerId( String fName, String lName, Connection dbConnection ) {
        return 0;
    }

    /**
     * Create a list of classes from result set
     * @param classes
     * @return
     */
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

    /**
     * Get the negative balance account users
     * @param dbConnection Connection to DB
     * @return Users and their phone numbers who are negative
     */
    public static Map<String, String> getNegativeAccountUsers( Connection dbConnection ) {
        Map<String, String> namesAndNums = new HashMap<>();
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT FNAME, LNAME, PHONENUM FROM " + BODE1 + PERIOD + MEMBER_TABLE
                        + " WHERE ACCOUNTBALANCE < 0" );
            ResultSet result = stmt.executeQuery();
            int i = 1;
            while ( result.next() ) {
                String number = i + ")";
                String firstName = result.getString( "FNAME" );
                String lastName = result.getString( "LNAME" );
                String fullName = firstName + " " + lastName;
                String phoneNum = result.getString( "PHONENUM" );
                namesAndNums.put( number + fullName, phoneNum );
                i++;
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve negative balance accounts" );
        }
        return namesAndNums;
    }

    /**
     * Get a member's schedule for a given month
     * @param member Member to check schedule for
     * @param month Month for schedule check
     * @param dbConnection Connection to DB
     * @return Member's class times for that month
     */
    public static
        Map<Timestamp, Float>
        getMemberScheduleForMonth( GymMember member, int month, Connection dbConnection ) {
        Map<Timestamp, Float> startTimeAndDuration = new HashMap<>();
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.clear();
        maxCalendar.set( Calendar.MONTH, month - 1 );
        maxCalendar.set( Calendar.YEAR, Year.now().getValue() );
        maxCalendar.set( Calendar.DAY_OF_MONTH, maxCalendar.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.clear();
        minCalendar.set( Calendar.MONTH, month - 1 );
        minCalendar.set( Calendar.YEAR, Year.now().getValue() );
        minCalendar.set( Calendar.DAY_OF_MONTH, 1 );

        Date maxDate = new Date( maxCalendar.getTime().getTime() );
        Date minDate = new Date( minCalendar.getTime().getTime() );
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT CLASSNUM FROM " + BODE1 + PERIOD + MEMBER_CLASS_TABLE + " WHERE MEMBERID = ?" );
            PreparedStatement getClassInfo = dbConnection
                .prepareStatement( "SELECT * FROM " + BODE1 + PERIOD + CLASS_TABLE + " WHERE CLASSNUM = ?" );
            stmt.setInt( 1, member.getMemberID() );
            ResultSet classNums = stmt.executeQuery();
            while ( classNums.next() ) {
                int classNum = classNums.getInt( "CLASSNUM" );
                getClassInfo.setInt( 1, classNum );
                ResultSet classInfo = getClassInfo.executeQuery();
                while ( classInfo.next() ) {
                    Date startDate = classInfo.getDate( "STARTDATE" );
                    Date endDate = classInfo.getDate( "ENDDATE" );
                    if ( ( startDate.after( minDate ) && startDate.before( maxDate ) )
                        || ( endDate.after( minDate ) && endDate.before( maxDate ) ) ) {
                        startTimeAndDuration
                            .put( classInfo.getTimestamp( "STARTTIME" ), classInfo.getFloat( "DURATION" ) );
                    }
                }
            }
            getClassInfo.close();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve schedule" );
            System.out.println( e.getMessage() );
        }

        return startTimeAndDuration;
    }

    /**
     * Get the amount of hours a trainer works in a given month
     * @param month month to look for
     * @param dbcConnection Connection to db
     * @return Trainers and their working hours
     */
    public static Map<String, Float> getAllTrainersWorkinghours( int month, Connection dbcConnection ) {
        Map<String, Float> trainerHours = new HashMap<>();
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
            List<Trainer> allTrainers = listAllTrainers( dbcConnection );
            PreparedStatement stmt = dbcConnection
                .prepareStatement(
                    "SELECT DURATION, ENDDATE FROM " + BODE1 + PERIOD + CLASS_TABLE + " WHERE TRAINERID = ?" );
            for ( Trainer trainer : allTrainers ) {
                float hours = 0;
                stmt.setInt( 1, trainer.getTrainerID() );
                ResultSet classDurationInfo = stmt.executeQuery();
                while ( classDurationInfo.next() ) {
                    Date endDate = classDurationInfo.getDate( "ENDDATE" );
                    if ( endDate.before( maxDate ) && endDate.after( minDate ) ) {
                        hours += classDurationInfo.getFloat( "DURATION" );
                    }
                }
                trainerHours.put( trainer.getFullName(), hours );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve all trainers working hours" );
        }
        return trainerHours;
    }

    /**
     * This method determines whether the trainer given has a scheduling conflict with the class
     *  they are proposed to teach.
     * @param trainerId trainer in question, checking for scheduling conflict
     * @param startTime time and date when proposed class starts
     * @param duration length, in minutes, of the proposed class
     * @param dbConnection connection to the Oracle database
     * @return boolean, whether there is a scheduling overlap with the trainer and the proposed
     *  new class
     */
    public static
        boolean
        trainerScheduleConflict( int trainerId, Timestamp startTime, int duration, Connection dbConnection ) {
        //Timestamp proposedStart =
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet classes = stmt
                .executeQuery(
                    "SELECT STARTTIME, DURATION FROM " + BODE1 + PERIOD + CLASS_TABLE + " WHERE TRAINERID="
                        + trainerId );
            while ( classes.next() ) {
                Timestamp classTime = classes.getTimestamp( "STARTTIME" );
                int classDur = classes.getInt( "DURATION" );
                int millis = classDur * 60000;
                if ( false ) {
                    return true;
                }
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to find trainer's current schedule" );
            return true;
        }
        return false;
    }

    /**
     * Get a list of all trainers
     * @param dbConnection
     * @return List of trainer
     */
    public static List<Trainer> listAllTrainers( Connection dbConnection ) {
        List<Trainer> trainers = new ArrayList<>();

        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet trainerInfo = stmt.executeQuery( "SELECT * FROM " + BODE1 + PERIOD + TRAINER_TABLE );
            while ( trainerInfo.next() ) {
                int trainerID = trainerInfo.getInt( "TRAINERID" );
                String firstName = trainerInfo.getString( "FNAME" );
                String lastName = trainerInfo.getString( "LNAME" );
                String phoneNum = trainerInfo.getString( "PHONENUM" );
                Trainer trainer = new Trainer( trainerID, firstName, lastName, phoneNum );
                trainers.add( trainer );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to create trainer list" );
        }

        return trainers;
    }

    /**
     * Get the rental items the amount in stock from DB
     * @param dbConnection Connection to DB
     * @return Map containing items and their qunatites
     */
    public static Map<String, Integer> getRentalItemsAndQuantities( Connection dbConnection ) {
        Map<String, Integer> itemAndQunatity = new HashMap<>();

        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet rentalInfo = stmt.executeQuery( "SELECT * FROM " + BODE1 + PERIOD + RENTAL_ITEM_TABLE );
            while ( rentalInfo.next() ) {
                itemAndQunatity.put( rentalInfo.getString( "ITEMNAME" ), rentalInfo.getInt( "QTYINSTOCK" ) );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to retrieve all rental items and their qunatites" );
        }

        return itemAndQunatity;
    }

    /**
     * Get a list of all rental items in the db
     * @param dbConnection
     * @return List<RentalItem>
     */
    public static List<RentalItem> getRentalItems( Connection dbConnection ) {
        List<RentalItem> items = new ArrayList<>();
        try {
            ResultSet itemInfo = dbConnection
                .prepareStatement( "SELECT * FROM " + BODE1 + PERIOD + RENTAL_ITEM_TABLE )
                .executeQuery();
            while ( itemInfo.next() ) {
                int itemNum = itemInfo.getInt( "ITEMNUM" );
                String itemName = itemInfo.getString( "ITEMNAME" );
                int qtyInStock = itemInfo.getInt( "QTYINSTOCK" );
                RentalItem item = new RentalItem( itemNum, itemName, qtyInStock );
                items.add( item );
            }
            itemInfo.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to get all items" );
        }
        return items;
    }

    /**
     * Saves a new rental log entry into the DB
     * @param entry
     * @param dbConnection
     */
    public static void saveNewRentalLogEntry( RentalLogEntry entry, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement( "INSERT INTO " + BODE1 + PERIOD + RENTAL_LOG_TABLE + " VALUES (?, ?, ?, ?, ?, ?)" );
            stmt.setInt( 1, entry.getRentalID() );
            stmt.setInt( 2, entry.getMemberID() );
            stmt.setInt( 3, entry.getItemNum() );
            stmt.setDate( 4, entry.getOutTime() );
            stmt.setInt( 5, entry.getQuantityBorrowed() );
            stmt.setBoolean( 6, entry.isReturned() );
            stmt.executeUpdate();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to save new rental log entry" );
        }
    }

    /**
     * Updates a rental item in the db
     * @param item Item to update
     * @param dbConnection Connection to DB
     */
    public static void saveChangesToRentalItem( RentalItem item, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection.prepareStatement( generateSaveRentalItemQuery() );
            stmt.setInt( 1, item.getQuantityInStock() );
            stmt.setInt( 2, item.getItemNum() );
            stmt.executeUpdate();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to update item: " + item.getItemName() );
        }
    }

    /**
     * Generates a query to save a new rental item
     * @return String with a query to save a rental item
     */
    private static String generateSaveRentalItemQuery() {
        StringBuilder sqlBuilder = new StringBuilder( "UPDATE " + BODE1 + PERIOD + RENTAL_ITEM_TABLE + " SET \n" );
        sqlBuilder.append( "QTYINSTOCK = ?\n" );
        sqlBuilder.append( "WHERE ITEMNUM = ?" );
        return sqlBuilder.toString();
    }

    /**
     * Updates the RentalItem table to up quantity after something has been returned
     * @param itemName rental item that is being returned
     * @param dbConnection Connection to db
     */
    public static void returnItem( String itemName, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "UPDATE " + BODE1 + PERIOD + RENTAL_ITEM_TABLE
                        + " SET QTYINSTOCK = QTYINSTOCK + 1 WHERE ITEMNAME = ?" );
            stmt.setString( 1, itemName );
            stmt.executeUpdate();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to return item" );
            System.out.println( e.getMessage() );
        }
    }

    /**
     * Updates the oldest rental log for the item the member is returning
     * @param member Member who is returning item
     * @param itemName Name of item being returned
     * @param dbConnection Connection to DB
     */
    public static void updateRentalLog( GymMember member, String itemName, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT * FROM " + BODE1 + PERIOD + RENTAL_LOG_TABLE
                        + " WHERE MEMBERID = ? AND RETURNED = 0  AND ITEMNUM = ? ORDER BY OUTTIME ASC" );
            stmt.setInt( 1, member.getMemberID() );
            stmt.setInt( 2, getItemIDFromName( itemName, dbConnection ) );
            ResultSet result = stmt.executeQuery();
            result.next();
            int rentalID = result.getInt( "RENTALID" ); // Rental ID to update...oldest first
            Statement saveReturn = dbConnection.createStatement();
            saveReturn
                .executeUpdate(
                    "UPDATE " + BODE1 + PERIOD + RENTAL_LOG_TABLE + " SET RETURNED = 1 WHERE RENTALID = " + rentalID );
            stmt.close();
            saveReturn.close();
        } catch ( SQLException e ) {
            System.out.println( "Issue with updating rental log" );
        }
    }

    /**
     * Get Item ID from the DB based on the name
     * @param item Item name to search DB for
     * @param dbConnection Connection to DB
     * @return Item ID of the item
     */
    private static int getItemIDFromName( String item, Connection dbConnection ) {
        int id = -1;
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT ITEMNUM FROM " + BODE1 + PERIOD + RENTAL_ITEM_TABLE + " WHERE ITEMNAME = ?" );
            stmt.setString( 1, item );
            ResultSet result = stmt.executeQuery();
            result.next();
            id = result.getInt( "ITEMNUM" );
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to determine itemnum" );
        }
        return id;
    }

    /**
     * Saves a new course to the DB
     * @param course Course to save
     * @param dbConnection Connection to DB
     */
    public static void saveNewCourse( Course course, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement( "INSERT INTO " + BODE1 + PERIOD + COURSE_TABLE + " VALUES ( ?, ?, ? )" );
            stmt.setInt( 1, course.getCourseID() );
            stmt.setString( 2, course.getCategory() );
            stmt.setInt( 3, course.getCatalogNum() );
            stmt.executeUpdate();
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unable to save the new course" );
        }
    }

    /**
     * Get a list of all courses in the DB
     * @param dbConnection
     * @return List<Course>
     */
    public static List<Course> getAllCourses( Connection dbConnection ) {
        List<Course> courses = new ArrayList<>();
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery( "SELECT * FROM " + BODE1 + PERIOD + COURSE_TABLE );
            while ( result.next() ) {
                int id = result.getInt( "COURSEID" );
                String category = result.getString( "CATEGORY" );
                int catNum = result.getInt( "CATALOGNUM" );
                Course course = new Course( id, category, catNum );
                courses.add( course );
            }
            stmt.close();
        } catch ( SQLException e ) {
            System.out.println( "Unabale to " );
        }
        return courses;
    }

    /**
     * Gets the amount that user has spent throughout their purchase transactions
     * @param member Me
        * @param dbConnection Connection to DB
     * @return amount member has spent up to now
     */
    private static float getAmountSpentByMember( GymMember member, Connection dbConnection ) {
        float amount = 0;
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT SUM(AMOUNT) FROM " + BODE1 + PERIOD + TRANSACTION_TABLE
                        + " WHERE MEMBERID = ? AND XACTTYPE = 'PURCHASE'" );
            stmt.setInt( 1, member.getMemberID() );
            ResultSet result = stmt.executeQuery();
            result.next();
            amount = result.getFloat( "SUM(AMOUNT)" );
            amount = Math.abs( amount );
        } catch ( SQLException e ) {
            System.out.println( "Unable to get money member spent" );
        }
        return amount;
    }

    /**
     * Searches DB for the course id of a course given it's name
     * @param name Name of course
     * @param dbConnection Connection to DB
     * @return Course ID for name passed into the function
     */
    public static int getCourseIDFromName( String name, Connection dbConnection ) {
        int id = 0;
        String[] categoryNum = name.split( " " );
        String category = categoryNum[0];
        int catNum = Integer.parseInt( categoryNum[1] );
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement(
                    "SELECT COURSEID FROM " + BODE1 + PERIOD + COURSE_TABLE
                        + " WHERE CATEGORY = ? AND CATALOGNUM = ?" );
            stmt.setString( 1, category );
            stmt.setInt( 2, catNum );
            ResultSet result = stmt.executeQuery();
            result.next();
            id = result.getInt( "COURSEID" );
        } catch ( SQLException e ) {
            System.out.println( "Unable to find ID from package name" );
        }

        return id;
    }

    /**
     * Saves the course package object into the DB
     * @param coursePackage Course package to save 
     * @param dbConnection Connection to DB
     */
    public static void saveNewCoursePackage( CoursePackage coursePackage, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement( "INSERT INTO " + BODE1 + PERIOD + COURSE_PACKAGE_TABLE + " VALUES ( ?, ? )" );
            stmt.setInt( 1, coursePackage.getCourseID() );
            stmt.setString( 2, coursePackage.getPackageName() );
            stmt.executeUpdate();
        } catch ( SQLException e ) {
            System.out.println( "Unable to save course package" );
        }
    }

    /**
     * Saves a new package to the DB
     * @param packageToAdd Package to add
     * @param dbConnection Connection to DB
     * @return True if successful and false otherwise
     */
    public static boolean saveNewPackage( entities.Package packageToAdd, Connection dbConnection ) {
        try {
            PreparedStatement stmt = dbConnection
                .prepareStatement( "INSERT INTO " + BODE1 + PERIOD + PACKAGE_TABLE + " VALUES ( ?, ? )" );
            stmt.setString( 1, packageToAdd.getPackageName() );
            stmt.setFloat( 2, packageToAdd.getCost() );
            stmt.executeUpdate();
            return true;
        } catch ( SQLException e ) {
            return false;
        }
    }

}
