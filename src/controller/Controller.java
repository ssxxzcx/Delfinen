
package controller;

import model.Member;
import model.MemberService;
import model.MembershipType;

import java.time.LocalDate;
import java.util.List;

public class Controller {

    private final MemberService memberService = new MemberService();

    public Member createMember(String name, LocalDate birthDate,
                               MembershipType membershipType, boolean competitive) {
        return memberService.createMember(name, birthDate, membershipType, competitive);
    }

    public List<Member> getMembers() {
        return memberService.getMembers();
    }
}
