
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final List<Payment> payments = new ArrayList<>();

    public void registerPayment(int memberId, double amount, LocalDate paymentDate) {
        if (memberId <= 0) {
            throw new IllegalArgumentException("Medlemsnummer skal være positivt.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Betalingsbeløb skal være positivt.");
        }

        if (paymentDate == null) {
            throw new IllegalArgumentException("Betalingsdato må ikke være tom.");
        }

        payments.add(new Payment(memberId, amount, paymentDate));
    }

    public List<Payment> getPayments() {
        return new ArrayList<>(payments);
    }

    public List<Member> getMembersInArrears(List<Member> members) {
        List<Member> membersInArrears = new ArrayList<>();

        for (Member member : members) {
            MembershipFee fee = new MembershipFee(member);
            double amountDue = fee.calculateFee(LocalDate.now());

            double amountPaid = 0;

            for (Payment payment : payments) {
                if (payment.getMemberId() == member.getMemberId()) {
                    amountPaid += payment.getAmount();
                }
            }

            if (amountPaid < amountDue) {
                membersInArrears.add(member);
            }
        }

        return membersInArrears;
    }
}
