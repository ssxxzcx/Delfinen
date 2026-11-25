
package Test;

import model.Member;
import model.MembershipType;
import model.PaymentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private Member member;

    private final LocalDate testDate =
            LocalDate.of(2026, 11, 25);

    @BeforeEach
    void setUp() {

        paymentService = new PaymentService();

        member = new Member(
                1,
                "Peter",
                LocalDate.of(2000, 5, 10),
                MembershipType.ACTIVE,
                false
        );
    }

    @Test
    void memberWithoutPaymentOwesFullFee() {

        assertEquals(
                1600.0,
                paymentService.getAmountDue(member, testDate)
        );
    }

    @Test
    void partialPaymentReducesDebt() {

        paymentService.registerPayment(
                1,
                500,
                LocalDate.of(2026, 3, 10)
        );

        assertEquals(
                1100.0,
                paymentService.getAmountDue(member, testDate)
        );
    }

    @Test
    void fullPaymentRemovesDebt() {

        paymentService.registerPayment(
                1,
                1600,
                LocalDate.of(2026, 3, 10)
        );

        assertEquals(
                0.0,
                paymentService.getAmountDue(member, testDate)
        );
    }

    @Test
    void overpaymentCannotCreateNegativeDebt() {

        paymentService.registerPayment(
                1,
                2000,
                LocalDate.of(2026, 3, 10)
        );

        assertEquals(
                0.0,
                paymentService.getAmountDue(member, testDate)
        );
    }

    @Test
    void previousYearPaymentDoesNotCount() {

        paymentService.registerPayment(
                1,
                1600,
                LocalDate.of(2025, 12, 10)
        );

        assertEquals(
                1600.0,
                paymentService.getAmountDue(member, testDate)
        );
    }

    @Test
    void futurePaymentDoesNotCountYet() {

        paymentService.registerPayment(
                1,
                1600,
                LocalDate.of(2026, 12, 10)
        );

        assertEquals(
                1600.0,
                paymentService.getAmountDue(member, testDate)
        );
    }

    @Test
    void arrearsListOnlyIncludesMembersWhoOweMoney() {

        Member secondMember = new Member(
                2,
                "Anna",
                LocalDate.of(2001, 4, 10),
                MembershipType.ACTIVE,
                false
        );

        paymentService.registerPayment(
                2,
                1600,
                LocalDate.of(2026, 5, 10)
        );

        List<Member> arrears =
                paymentService.getMembersInArrears(
                        List.of(member, secondMember),
                        testDate
                );

        assertEquals(1, arrears.size());
        assertEquals(
                1,
                arrears.get(0).getMemberId()
        );
    }
}

