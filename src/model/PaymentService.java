
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final List<Payment> payments = new ArrayList<>();

    // Registrer betaling
    public void registerPayment(
            int memberId,
            double amount,
            LocalDate paymentDate) {

        if (memberId <= 0) {
            throw new IllegalArgumentException(
                    "Medlemsnummer skal være positivt.");
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Betalingsbeløb skal være positivt.");
        }

        if (paymentDate == null) {
            throw new IllegalArgumentException(
                    "Betalingsdato må ikke være tom.");
        }

        payments.add(new Payment(
                memberId,
                amount,
                paymentDate
        ));
    }

    // Hent alle betalinger
    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }

    // Beregn skyldig beløb på dags dato
    public double getAmountDue(Member member) {
        return getAmountDue(member, LocalDate.now());
    }

    // Beregn skyldig beløb på en bestemt dato
    public double getAmountDue(
            Member member,
            LocalDate date) {

        if (member == null || date == null) {
            throw new IllegalArgumentException(
                    "Medlem og dato skal være angivet.");
        }

        MembershipFee membershipFee =
                new MembershipFee(member);

        double annualFee =
                membershipFee.calculateFee(date);

        double amountPaid = 0;

        for (Payment payment : payments) {

            boolean correctMember =
                    payment.getMemberId()
                            == member.getMemberId();

            boolean correctYear =
                    payment.getPaymentDate().getYear()
                            == date.getYear();

            boolean paymentAlreadyMade =
                    !payment.getPaymentDate().isAfter(date);

            if (correctMember
                    && correctYear
                    && paymentAlreadyMade) {

                amountPaid += payment.getAmount();
            }
        }

        return Math.max(0, annualFee - amountPaid);
    }

    // Find alle medlemmer, der mangler at betale
    public List<Member> getMembersInArrears(
            List<Member> members) {

        return getMembersInArrears(
                members,
                LocalDate.now()
        );
    }

    // Restanceliste på en bestemt dato
    public List<Member> getMembersInArrears(
            List<Member> members,
            LocalDate date) {

        if (members == null || date == null) {
            throw new IllegalArgumentException(
                    "Medlemsliste og dato skal være angivet.");
        }

        List<Member> membersInArrears =
                new ArrayList<>();

        for (Member member : members) {

            double amountDue =
                    getAmountDue(member, date);

            if (amountDue > 0) {
                membersInArrears.add(member);
            }
        }

        return membersInArrears;
    }
}
