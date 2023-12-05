/*
 * Class name: MembershipLevel
 * Jake Bode, Frankie Gonzalez
 * Purpose: This class holds the data collected from the MembershipLevel table
 *  within the database. It represents a membership level that a member of the
 *  gym can attain with a certain given amount spent on course packages, as an
 *  entity.
 * Constructor: MembershipLevel(...) takes in all of the attributes from a single
 *  tuple in the MembershipLevel relation and sets all of the values to the
 *  corresponding variables in this class.
 * Public getters and setters for every attribute from the tuples are
 *  defined in this class.
 */

package entities;

import enums.MembershipLevelEnum;

public class MembershipLevel {

    private MembershipLevelEnum level;

    private float minSpendingRequirement;

    private float discountRate;

    public MembershipLevel( MembershipLevelEnum level, float minSpendingRequirement, float discountRate ) {
        this.level = level;
        this.minSpendingRequirement = minSpendingRequirement;
        this.discountRate = discountRate;
    }

}
