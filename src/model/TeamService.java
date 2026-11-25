
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    // Juniorhold: konkurrencesvømmere under 18 år
    public List<Member> getJuniorMembers(List<Member> members) {

        return getJuniorMembers(members, LocalDate.now());
    }

    public List<Member> getJuniorMembers(
            List<Member> members,
            LocalDate date) {

        if (members == null || date == null) {
            throw new IllegalArgumentException(
                    "Medlemslisten og datoen må ikke være null");
        }

        List<Member> juniors = new ArrayList<>();

        for (Member member : members) {

            if (member.isCompetitive()
                    && member.getAge(date) < 18) {

                juniors.add(member);
            }
        }

        return juniors;
    }

    // Seniorhold: konkurrencesvømmere fra 18 år
    public List<Member> getSeniorMembers(List<Member> members) {

        return getSeniorMembers(members, LocalDate.now());
    }

    public List<Member> getSeniorMembers(
            List<Member> members,
            LocalDate date) {

        if (members == null || date == null) {
            throw new IllegalArgumentException(
                    "Medlemslisten og datoen må ikke være null");
        }

        List<Member> seniors = new ArrayList<>();

        for (Member member : members) {

            if (member.isCompetitive()
                    && member.getAge(date) >= 18) {

                seniors.add(member);
            }
        }

        return seniors;
    }
}
