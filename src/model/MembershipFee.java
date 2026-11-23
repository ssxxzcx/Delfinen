
package model;

import java.time.LocalDate;

public class MembershipFee {

    private final Member member;

    public MembershipFee(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null");
        }

        this.member = member;
    }

    public int calculateFee(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        if (member.getMembershipType() == MembershipType.PASSIVE) {
            return 500;
        }

        int age = member.getAge(date);

        if (age < 18) {
            return 1000;
        } else if (age >= 60) {
            return 1200;
        } else {
            return 1600;
        }
    }
}
