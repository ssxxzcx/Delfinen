
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    public List<Member> getJuniorMembers(List<Member> members) {
        List<Member> juniors = new ArrayList<>();

        for (Member member : members) {
            if (member.getAge(LocalDate.now()) < 18) {
                juniors.add(member);
            }
        }

        return juniors;
    }

    public List<Member> getSeniorMembers(List<Member> members) {
        List<Member> seniors = new ArrayList<>();

        for (Member member : members) {
            if (member.getAge(LocalDate.now()) >= 18) {
                seniors.add(member);
            }
        }

        return seniors;
    }
}
