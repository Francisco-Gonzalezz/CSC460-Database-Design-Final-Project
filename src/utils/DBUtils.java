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

    public static void addNewGymMemberToDB(GymMember member, Connection dbConnection) {
        String firstName = member.getFirstName();
        String lastName = member.getLastName();
        String phoneNumber = member.getPhoneNumber();
        String email = member.getEmail();
        MembershipLevelEnum membershipLevel = MembershipLevelEnum.BASIC;
        try {
            Statement stmt = dbConnection.createStatement();
            ResultSet result = stmt.executeQuery("");

            stmt.close(); // Fun fact this will close the result sets too!
        } catch (SQLException e) {

        }
    }

}
