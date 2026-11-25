
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

            for (Payment payment : fileHandler.loadPayments()) {
                if (findMember(payment.getMemberId()) != null) {
                    paymentService.registerPayment(
                            payment.getMemberId(),
                            payment.getAmount(),
                            payment.getPaymentDate()
                    );
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke indlæse data", e);
        }
    }

    public Member createMember(String name, LocalDate birthDate,
                               MembershipType membershipType,
                               boolean competitive) {

        if (name == null || name.contains(";") ||
                name.contains("\n") || name.contains("\r")) {
            throw new IllegalArgumentException("Ugyldigt navn");
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

        if (findMember(memberId) == null) {
            throw new IllegalArgumentException(
                    "Medlemsnummeret findes ikke");
        }

        paymentService.registerPayment(
                memberId, amount, paymentDate);

        savePayments();
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

    private Member findMember(int memberId) {
        for (Member member : memberService.getMembers()) {
            if (member.getMemberId() == memberId) {
                return member;
            }
        }
        return null;
    }

    private void saveMembers() {
        try {
            fileHandler.saveData(memberService.getMembers());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke gemme medlemmer", e);
        }
    }

    private void savePayments() {
        try {
            fileHandler.savePayments(paymentService.getPayments());
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Kunne ikke gemme betalinger", e);
        }
    }
}
