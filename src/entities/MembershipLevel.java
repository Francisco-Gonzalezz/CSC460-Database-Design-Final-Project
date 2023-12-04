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
