
package model;

import java.time.LocalDate;

public class Payment {

    private int memberId;
    private double amount;
    private LocalDate paymentDate;

    public Payment(int memberId, double amount, LocalDate paymentDate) {
        this.memberId = memberId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getMemberId() {
        return memberId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }
}
