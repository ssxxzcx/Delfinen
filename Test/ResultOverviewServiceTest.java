
import model.Member;
import model.MemberTrainingResult;
import model.MembershipType;
import model.ResultOverviewService;
import model.ResultType;
import model.SwimmingDiscipline;
import model.TrainingResult;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResultOverviewServiceTest {

    @Test
    void getTopFiveReturnsFastestJuniorResults() {

        Member junior1 = new Member(1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        Member junior2 = new Member(2, "Peter",
                LocalDate.now().minusYears(15),
                MembershipType.ACTIVE, true);

        Member senior = new Member(3, "Lars",
                LocalDate.now().minusYears(20),
                MembershipType.ACTIVE, true);

        junior1.addDiscipline(SwimmingDiscipline.FREESTYLE);
        junior2.addDiscipline(SwimmingDiscipline.FREESTYLE);
        senior.addDiscipline(SwimmingDiscipline.FREESTYLE);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(junior1,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(junior2,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 40),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(senior,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 30),
                ResultType.TRAINING));

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> topFive =
                service.getTopFive(
                        SwimmingDiscipline.FREESTYLE,
                        results, true);

        assertEquals(2, topFive.size());
        assertEquals(40, topFive.get(0).getResult().getTime());
        assertEquals(50, topFive.get(1).getResult().getTime());
    }

    @Test
    void getTopFiveReturnsMaximumFiveResults() {

        List<MemberTrainingResult> results = new ArrayList<>();

        for (int i = 1; i <= 7; i++) {

            Member member = new Member(
                    i,
                    "Junior " + i,
                    LocalDate.now().minusYears(15),
                    MembershipType.ACTIVE,
                    true
            );

            member.addDiscipline(SwimmingDiscipline.FREESTYLE);

            TrainingResult result = new TrainingResult(
                    SwimmingDiscipline.FREESTYLE,
                    LocalDate.now(),
                    30 + i
            );

            results.add(new MemberTrainingResult(
                    member, result, ResultType.TRAINING));
        }

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> topFive =
                service.getTopFive(
                        SwimmingDiscipline.FREESTYLE,
                        results, true);

        assertEquals(5, topFive.size());
    }

    @Test
    void getTopFiveOnlyIncludesMemberOnce() {

        Member junior = new Member(
                1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        junior.addDiscipline(SwimmingDiscipline.FREESTYLE);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(junior,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(junior,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 40),
                ResultType.TRAINING));

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> topFive =
                service.getTopFive(
                        SwimmingDiscipline.FREESTYLE,
                        results, true);

        assertEquals(1, topFive.size());
        assertEquals(40, topFive.get(0).getResult().getTime());
    }

    @Test
    void getTopFiveReturnsFastestSeniorResults() {

        Member senior1 = new Member(
                1, "Lars",
                LocalDate.now().minusYears(25),
                MembershipType.ACTIVE, true);

        Member senior2 = new Member(
                2, "Peter",
                LocalDate.now().minusYears(30),
                MembershipType.ACTIVE, true);

        Member junior = new Member(
                3, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        senior1.addDiscipline(SwimmingDiscipline.FREESTYLE);
        senior2.addDiscipline(SwimmingDiscipline.FREESTYLE);
        junior.addDiscipline(SwimmingDiscipline.FREESTYLE);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(senior1,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(senior2,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 40),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(junior,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 30),
                ResultType.TRAINING));

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> topFive =
                service.getTopFive(
                        SwimmingDiscipline.FREESTYLE,
                        results, false);

        assertEquals(2, topFive.size());
        assertEquals(40, topFive.get(0).getResult().getTime());
        assertEquals(50, topFive.get(1).getResult().getTime());
    }

    @Test
    void getResultsForMemberReturnsOnlySelectedMembersResults() {

        Member anna = new Member(
                1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        Member peter = new Member(
                2, "Peter",
                LocalDate.now().minusYears(17),
                MembershipType.ACTIVE, true);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(peter,
                new TrainingResult(
                        SwimmingDiscipline.BACKSTROKE,
                        LocalDate.now(), 60),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.BUTTERFLY,
                        LocalDate.now(), 45),
                ResultType.TRAINING));

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> annaResults =
                service.getResultsForMember(1, results);

        assertEquals(2, annaResults.size());
        assertEquals(1, annaResults.get(0).getMember().getMemberId());
        assertEquals(1, annaResults.get(1).getMember().getMemberId());
    }

    @Test
    void getCompetitionResultsForMemberReturnsOnlyCompetitionResults() {

        Member anna = new Member(
                1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        Member peter = new Member(
                2, "Peter",
                LocalDate.now().minusYears(17),
                MembershipType.ACTIVE, true);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.COMPETITION));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.BACKSTROKE,
                        LocalDate.now(), 60),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(peter,
                new TrainingResult(
                        SwimmingDiscipline.BUTTERFLY,
                        LocalDate.now(), 45),
                ResultType.COMPETITION));

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> competitionResults =
                service.getCompetitionResultsForMember(1, results);

        assertEquals(1, competitionResults.size());
        assertEquals(ResultType.COMPETITION,
                competitionResults.get(0).getResultType());
        assertEquals(1,
                competitionResults.get(0).getMember().getMemberId());
    }

    @Test
    void getBestTrainingResultReturnsFastestTrainingTime() {

        Member anna = new Member(
                1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 40),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 30),
                ResultType.COMPETITION));

        ResultOverviewService service = new ResultOverviewService();

        MemberTrainingResult bestResult =
                service.getBestTrainingResult(
                        1, SwimmingDiscipline.FREESTYLE, results);

        assertEquals(40, bestResult.getResult().getTime());
        assertEquals(ResultType.TRAINING, bestResult.getResultType());
    }

    @Test
    void getTopFiveWorksForAllSwimmingDisciplines() {

        Member anna = new Member(
                1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        List<MemberTrainingResult> results = new ArrayList<>();

        for (SwimmingDiscipline discipline : SwimmingDiscipline.values()) {

            anna.addDiscipline(discipline);

            results.add(new MemberTrainingResult(
                    anna,
                    new TrainingResult(
                            discipline, LocalDate.now(), 50),
                    ResultType.TRAINING));
        }

        ResultOverviewService service = new ResultOverviewService();

        for (SwimmingDiscipline discipline : SwimmingDiscipline.values()) {

            List<MemberTrainingResult> topFive =
                    service.getTopFive(discipline, results, true);

            assertEquals(1, topFive.size());
            assertEquals(discipline,
                    topFive.get(0).getResult().getDiscipline());
        }
    }

    @Test
    void constructorRejectsNullMember() {

        TrainingResult result = new TrainingResult(
                SwimmingDiscipline.FREESTYLE,
                LocalDate.now(),
                50
        );

        assertThrows(IllegalArgumentException.class, () ->
                new MemberTrainingResult(
                        null, result, ResultType.TRAINING));
    }

    @Test
    void getBestTrainingResultsForMemberReturnsBestTimeInEachDiscipline() {

        Member anna = new Member(
                1, "Anna",
                LocalDate.now().minusYears(16),
                MembershipType.ACTIVE, true);

        List<MemberTrainingResult> results = new ArrayList<>();

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 50),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.FREESTYLE,
                        LocalDate.now(), 40),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.BREASTSTROKE,
                        LocalDate.now(), 60),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.BUTTERFLY,
                        LocalDate.now(), 45),
                ResultType.TRAINING));

        results.add(new MemberTrainingResult(anna,
                new TrainingResult(
                        SwimmingDiscipline.BACKSTROKE,
                        LocalDate.now(), 30),
                ResultType.COMPETITION));

        ResultOverviewService service = new ResultOverviewService();

        List<MemberTrainingResult> bestResults =
                service.getBestTrainingResultsForMember(1, results);

        assertEquals(3, bestResults.size());
        assertEquals(40, bestResults.get(0).getResult().getTime());
        assertEquals(60, bestResults.get(1).getResult().getTime());
        assertEquals(45, bestResults.get(2).getResult().getTime());
    }
}
