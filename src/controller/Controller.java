
package controller;

import model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private final MemberService memberService = new MemberService();
    private final FileHandler fileHandler = new FileHandler();
    private final RevenueService revenueService = new RevenueService();
    private final PaymentService paymentService = new PaymentService();
    private final TeamService teamService = new TeamService();

    public Controller() {
        try {
            memberService.loadMembers(fileHandler.loadData());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke indlæse medlemmer", e);
        }
    }

    public Member createMember(String name, LocalDate birthDate,
                               MembershipType membershipType,
                               boolean competitive) {

        if (name.contains(";")) {
            throw new IllegalArgumentException(
                    "Navnet må ikke indeholde semikolon");
        }

        Member member = memberService.createMember(
                name, birthDate, membershipType, competitive);

        saveMembers();
        return member;
    }

    public List<Member> getMembers() {
        return memberService.getMembers();
    }

    public double getExpectedRevenue() {
        return revenueService.calculateExpectedRevenue(
                memberService.getMembers());
    }

    public void registerPayment(int memberId, double amount,
                                LocalDate paymentDate) {

        boolean memberExists = false;

        for (Member member : memberService.getMembers()) {
            if (member.getMemberId() == memberId) {
                memberExists = true;
                break;
            }
        }

        if (!memberExists) {
            throw new IllegalArgumentException(
                    "Medlemsnummeret findes ikke");
        }

        paymentService.registerPayment(memberId, amount, paymentDate);
    }

    public List<Member> getMembersInArrears() {
        return paymentService.getMembersInArrears(
                memberService.getMembers());
    }

    public List<Member> getJuniorMembers() {
        return teamService.getJuniorMembers(memberService.getMembers());
    }

    public List<Member> getSeniorMembers() {
        return teamService.getSeniorMembers(memberService.getMembers());
    }

    private void saveMembers() {
        try {
            fileHandler.saveData(memberService.getMembers());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke gemme medlemmer", e);
        }
    }
}
