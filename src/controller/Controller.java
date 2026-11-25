
package controller;

import model.FileHandler;
import model.Member;
import model.MemberService;
import model.MembershipType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private final MemberService memberService = new MemberService();
    private final FileHandler fileHandler = new FileHandler();

    public Controller() {
        try {
            memberService.loadMembers(fileHandler.loadData());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke indlæse medlemmer fra filen", e);
        }
    }

    public Member createMember(String name, LocalDate birthDate,
                               MembershipType membershipType, boolean competitive) {

        Member member = memberService.createMember(
                name, birthDate, membershipType, competitive);

        saveMembers();

        return member;
    }

    public List<Member> getMembers() {
        return memberService.getMembers();
    }

    private void saveMembers() {
        try {
            fileHandler.saveData(memberService.getMembers());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke gemme medlemmer i filen", e);
        }
    }
}
