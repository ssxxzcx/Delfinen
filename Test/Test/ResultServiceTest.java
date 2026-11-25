
package Test;

import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ResultServiceTest {

    private ResultService resultService;
    private Member swimmer;

    @BeforeEach
    void setUp() {

        resultService = new ResultService();

        swimmer = new Member(
                1,
                "Peter",
                LocalDate.of(2008, 5, 10),
                MembershipType.ACTIVE,
                true
        );

        swimmer.addDiscipline(
                SwimmingDiscipline.FREESTYLE
        );
    }

    @Test
    void firstTrainingTimeShouldBeSaved() {

        TrainingResult result = createResult(
                1,
                SwimmingDiscipline.FREESTYLE,
                4532
        );

        boolean improved =
                resultService.registerBestTrainingResult(
                        swimmer,
                        result
                );

        assertTrue(improved);

        assertEquals(
                1,
                resultService.getTrainingResults().size()
        );

        assertEquals(
                4532,
                resultService.getTrainingResults()
                        .get(0).getTime()
        );
    }

    @Test
    void fasterTimeShouldReplacePreviousRecord() {

        resultService.registerBestTrainingResult(
                swimmer,
                createResult(
                        1,
                        SwimmingDiscipline.FREESTYLE,
                        4532
                )
        );

        boolean improved =
                resultService.registerBestTrainingResult(
                        swimmer,
                        createResult(
                                1,
                                SwimmingDiscipline.FREESTYLE,
                                4310
                        )
                );

        assertTrue(improved);

        assertEquals(
                1,
                resultService.getTrainingResults().size()
        );

        assertEquals(
                4310,
                resultService.getTrainingResults()
                        .get(0).getTime()
        );
    }

    @Test
    void slowerTimeShouldNotReplaceRecord() {

        resultService.registerBestTrainingResult(
                swimmer,
                createResult(
                        1,
                        SwimmingDiscipline.FREESTYLE,
                        4310
                )
        );

        boolean improved =
                resultService.registerBestTrainingResult(
                        swimmer,
                        createResult(
                                1,
                                SwimmingDiscipline.FREESTYLE,
                                4750
                        )
                );

        assertFalse(improved);

        assertEquals(
                4310,
                resultService.getTrainingResults()
                        .get(0).getTime()
        );

        assertEquals(
                1,
                resultService.getTrainingResults().size()
        );
    }

    @Test
    void identicalTimeShouldNotReplaceRecord() {

        resultService.registerBestTrainingResult(
                swimmer,
                createResult(
                        1,
                        SwimmingDiscipline.FREESTYLE,
                        4310
                )
        );

        boolean improved =
                resultService.registerBestTrainingResult(
                        swimmer,
                        createResult(
                                1,
                                SwimmingDiscipline.FREESTYLE,
                                4310
                        )
                );

        assertFalse(improved);

        assertEquals(
                1,
                resultService.getTrainingResults().size()
        );

        assertEquals(
                4310,
                resultService.getTrainingResults()
                        .get(0).getTime()
        );
    }

    @Test
    void differentDisciplinesShouldHaveSeparateRecords() {

        swimmer.addDiscipline(
                SwimmingDiscipline.BREASTSTROKE
        );

        resultService.registerBestTrainingResult(
                swimmer,
                createResult(
                        1,
                        SwimmingDiscipline.FREESTYLE,
                        4310
                )
        );

        resultService.registerBestTrainingResult(
                swimmer,
                createResult(
                        1,
                        SwimmingDiscipline.BREASTSTROKE,
                        5500
                )
        );

        assertEquals(
                2,
                resultService.getTrainingResults().size()
        );
    }

    @Test
    void differentSwimmersShouldHaveSeparateRecords() {

        Member secondSwimmer = new Member(
                2,
                "Anna",
                LocalDate.of(2009, 3, 15),
                MembershipType.ACTIVE,
                true
        );

        secondSwimmer.addDiscipline(
                SwimmingDiscipline.FREESTYLE
        );

        resultService.registerBestTrainingResult(
                swimmer,
                createResult(
                        1,
                        SwimmingDiscipline.FREESTYLE,
                        4310
                )
        );

        resultService.registerBestTrainingResult(
                secondSwimmer,
                createResult(
                        2,
                        SwimmingDiscipline.FREESTYLE,
                        4500
                )
        );

        assertEquals(
                2,
                resultService.getTrainingResults().size()
        );
    }

    @Test
    void nonCompetitiveMemberShouldBeRejected() {

        Member nonCompetitive = new Member(
                3,
                "Lars",
                LocalDate.of(2000, 1, 1),
                MembershipType.ACTIVE,
                false
        );

        TrainingResult result = createResult(
                3,
                SwimmingDiscipline.FREESTYLE,
                4500
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> resultService.registerBestTrainingResult(
                        nonCompetitive,
                        result
                )
        );

        assertTrue(
                resultService.getTrainingResults().isEmpty()
        );
    }

    private TrainingResult createResult(
            int memberId,
            SwimmingDiscipline discipline,
            int time) {

        return new TrainingResult(
                discipline,
                LocalDate.of(2026, 11, 20),
                time,
                memberId
        );
    }
}
