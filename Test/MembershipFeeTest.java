import model.Member;
import model.MembershipFee;
import model.MembershipType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MembershipFeeTest {

    private final LocalDate calculationDate =
            LocalDate.of(2026, 11, 23);


    @Test
    void juniorUnder18Pays1000() {
        Member member = new Member(
                1, "Test Junior",
                LocalDate.of(2009, 11, 24),
                MembershipType.ACTIVE, false
        );

        MembershipFee fee = new MembershipFee(member);

        assertEquals(1000, fee.calculateFee(calculationDate));
    }

    @Test
    void seniorAt18Pays1600() {
        Member member = new Member(
                2, "Test Senior",
                LocalDate.of(2008, 11, 23),
                MembershipType.ACTIVE, false
        );

        MembershipFee fee = new MembershipFee(member);

        assertEquals(1600, fee.calculateFee(calculationDate));
    }

    @Test
    void seniorAt59Pays1600() {
        Member member = new Member(
                3, "Test Senior",
                LocalDate.of(1966, 11, 24),
                MembershipType.ACTIVE, false
        );

        MembershipFee fee = new MembershipFee(member);

        assertEquals(1600, fee.calculateFee(calculationDate));
    }

    @Test
    void seniorAt60Gets25PercentDiscount() {
        Member member = new Member(
                4, "Test Senior",
                LocalDate.of(1966, 11, 23),
                MembershipType.ACTIVE, false
        );

        MembershipFee fee = new MembershipFee(member);

        assertEquals(1200, fee.calculateFee(calculationDate));
    }

    @Test
    void passiveMemberPays500() {
        Member member = new Member(
                5, "Test Passive",
                LocalDate.of(2000, 1, 1),
                MembershipType.PASSIVE, false
        );

        MembershipFee fee = new MembershipFee(member);

        assertEquals(500, fee.calculateFee(calculationDate));
    }
}
