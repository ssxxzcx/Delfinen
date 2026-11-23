
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberService {

    private final List<Member> members = new ArrayList<>();
    private int nextMemberId = 1;

    public Member createMember(String name, LocalDate birthDate,
                               MembershipType membershipType, boolean competitive) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Navn må ikke være tomt");
        }

        if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ugyldig fødselsdato");
        }

        if (membershipType == null) {
            throw new IllegalArgumentException("Medlemskabstype mangler");
        }

        if (membershipType == MembershipType.PASSIVE && competitive) {
            throw new IllegalArgumentException("Passive medlemmer kan ikke være konkurrencesvømmere");
        }

        Member member = new Member(nextMemberId, name, birthDate,
                membershipType, competitive);

        members.add(member);
        nextMemberId++;

        return member;
    }

    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }
}
