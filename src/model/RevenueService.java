
package model;

import java.time.LocalDate;
import java.util.List;

public class RevenueService {

    public double calculateExpectedRevenue(List<Member> members) {
        double totalRevenue = 0;

        for (Member member : members) {
            MembershipFee fee = new MembershipFee(member);
            totalRevenue += fee.calculateFee(LocalDate.now());
        }

        return totalRevenue;
    }
}
