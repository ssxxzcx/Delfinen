
package model;

import java.time.LocalDate;
import java.time.Period;

public class Member {

    private int memberId;
    private String name;
    private LocalDate birthDate;
    private MembershipType membershipType;
    private boolean competitive;

    public Member(int memberId, String name, LocalDate birthDate,
                  MembershipType membershipType, boolean competitive) {
        this.memberId = memberId;
        this.name = name;
        this.birthDate = birthDate;
        this.membershipType = membershipType;
        this.competitive = competitive;
    }

    public int getAge(LocalDate date) {
        return Period.between(birthDate, date).getYears();
    }

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public boolean isCompetitive() {
        return competitive;
    }
}
