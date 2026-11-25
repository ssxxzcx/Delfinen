
package Test;

import model.Member;
import model.MembershipType;
import model.SwimmingDiscipline;
import model.TeamService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TeamServiceTest {

    private TeamService teamService;

    private final LocalDate testDate =
            LocalDate.of(2026, 11, 25);

    private Member createMember(
            int id,
            String name,
            LocalDate birthDate,
            MembershipType type,
            boolean competitive) {

        Member member = new Member(
                id,
                name,
                birthDate,
                type,
                competitive
        );

        if (competitive) {
            member.addDiscipline(
                    SwimmingDiscipline.FREESTYLE
            );
        }

        return member;
    }

    @BeforeEach
    void setUp() {
        teamService = new TeamService();
    }

    @Test
    void competitiveJuniorShouldBeOnJuniorTeam() {

        Member junior = createMember(
                1, "Anna",
                LocalDate.of(2010, 5, 10),
                MembershipType.ACTIVE, true
        );

        List<Member> juniors =
                teamService.getJuniorMembers(
                        List.of(junior), testDate
                );

        assertEquals(1, juniors.size());
        assertEquals(1, juniors.get(0).getMemberId());
    }

    @Test
    void competitiveSeniorShouldBeOnSeniorTeam() {

        Member senior = createMember(
                2, "Peter",
                LocalDate.of(2000, 5, 10),
                MembershipType.ACTIVE, true
        );

        List<Member> seniors =
                teamService.getSeniorMembers(
                        List.of(senior), testDate
                );

        assertEquals(1, seniors.size());
        assertEquals(2, seniors.get(0).getMemberId());
    }

    @Test
    void nonCompetitiveMembersShouldNotBeOnTeams() {

        Member junior = createMember(
                1, "Anna",
                LocalDate.of(2010, 5, 10),
                MembershipType.ACTIVE, false
        );

        Member senior = createMember(
                2, "Peter",
                LocalDate.of(2000, 5, 10),
                MembershipType.ACTIVE, false
        );

        List<Member> members =
                List.of(junior, senior);

        assertTrue(
                teamService.getJuniorMembers(
                        members, testDate).isEmpty()
        );

        assertTrue(
                teamService.getSeniorMembers(
                        members, testDate).isEmpty()
        );
    }

    @Test
    void juniorBecomesSeniorOnEighteenthBirthday() {

        Member swimmer = createMember(
                1, "Sara",
                LocalDate.of(2008, 11, 25),
                MembershipType.ACTIVE, true
        );

        assertTrue(
                teamService.getJuniorMembers(
                        List.of(swimmer),
                        LocalDate.of(2026, 11, 24)
                ).contains(swimmer)
        );

        assertTrue(
                teamService.getSeniorMembers(
                        List.of(swimmer),
                        LocalDate.of(2026, 11, 25)
                ).contains(swimmer)
        );
    }

    @Test
    void juniorsAndSeniorsShouldBeSeparated() {

        Member junior = createMember(
                1, "Anna",
                LocalDate.of(2010, 1, 1),
                MembershipType.ACTIVE, true
        );

        Member senior = createMember(
                2, "Peter",
                LocalDate.of(2000, 1, 1),
                MembershipType.ACTIVE, true
        );

        List<Member> members =
                List.of(junior, senior);

        assertEquals(
                1,
                teamService.getJuniorMembers(
                        members, testDate).size()
        );

        assertEquals(
                1,
                teamService.getSeniorMembers(
                        members, testDate).size()
        );
    }

    @Test
    void emptyMemberListShouldReturnEmptyTeams() {

        assertTrue(
                teamService.getJuniorMembers(
                        List.of(), testDate).isEmpty()
        );

        assertTrue(
                teamService.getSeniorMembers(
                        List.of(), testDate).isEmpty()
        );
    }
}
