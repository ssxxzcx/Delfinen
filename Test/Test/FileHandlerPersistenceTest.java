
package Test;

import model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerPersistenceTest {

    @TempDir
    Path tempDir;

    private FileHandler fileHandler;

    private Path memberFile;
    private Path paymentFile;
    private Path trainingFile;
    private Path competitionFile;

    @BeforeEach
    void setUp() {

        memberFile = tempDir.resolve("members.txt");
        paymentFile = tempDir.resolve("payments.txt");
        trainingFile = tempDir.resolve("training-results.txt");
        competitionFile = tempDir.resolve("competition-results.txt");

        fileHandler = new FileHandler(
                memberFile.toString(),
                paymentFile.toString(),
                trainingFile.toString(),
                competitionFile.toString()
        );
    }

    @Test
    void membersAndDisciplinesShouldBeSavedAndLoaded()
            throws IOException {

        Member member = createCompetitiveMember(
                1, "Peter"
        );

        member.addDiscipline(
                SwimmingDiscipline.BREASTSTROKE
        );

        fileHandler.saveData(List.of(member));

        List<Member> loaded = fileHandler.loadData();

        assertEquals(1, loaded.size());
        assertEquals(1, loaded.get(0).getMemberId());
        assertEquals("Peter", loaded.get(0).getName());
        assertTrue(loaded.get(0).isCompetitive());

        assertTrue(
                loaded.get(0).getDisciplines().contains(
                        SwimmingDiscipline.FREESTYLE)
        );

        assertTrue(
                loaded.get(0).getDisciplines().contains(
                        SwimmingDiscipline.BREASTSTROKE)
        );

        assertEquals(
                2,
                loaded.get(0).getDisciplines().size()
        );
    }

    @Test
    void paymentsShouldBeSavedAndLoaded()
            throws IOException {

        Payment payment = new Payment(
                1,
                500,
                LocalDate.of(2026, 11, 20)
        );

        fileHandler.savePayments(List.of(payment));

        List<Payment> loaded =
                fileHandler.loadPayments();

        assertEquals(1, loaded.size());
        assertEquals(1, loaded.get(0).getMemberId());
        assertEquals(500, loaded.get(0).getAmount());
        assertEquals(
                LocalDate.of(2026, 11, 20),
                loaded.get(0).getPaymentDate()
        );
    }

    @Test
    void trainingResultsShouldBeSavedAndLoaded()
            throws IOException {

        TrainingResult result = new TrainingResult(
                SwimmingDiscipline.FREESTYLE,
                LocalDate.of(2026, 11, 20),
                4532,
                1
        );

        fileHandler.saveTrainingResults(
                List.of(result)
        );

        List<TrainingResult> loaded =
                fileHandler.loadTrainingResults();

        assertEquals(1, loaded.size());

        TrainingResult saved = loaded.get(0);

        assertEquals(1, saved.getMemberId());
        assertEquals(
                SwimmingDiscipline.FREESTYLE,
                saved.getDiscipline()
        );
        assertEquals(4532, saved.getTime());
        assertEquals(
                LocalDate.of(2026, 11, 20),
                saved.getDate()
        );
    }

    @Test
    void competitionResultsShouldPreserveNameAndPlacement()
            throws IOException {

        TrainingResult result = new TrainingResult(
                SwimmingDiscipline.BUTTERFLY,
                LocalDate.of(2026, 11, 20),
                6025,
                1,
                "DM 2026",
                2
        );

        fileHandler.saveCompetitionResults(
                List.of(result)
        );

        List<TrainingResult> loaded =
                fileHandler.loadCompetitionResults();

        assertEquals(1, loaded.size());

        TrainingResult saved = loaded.get(0);

        assertEquals(1, saved.getMemberId());
        assertEquals(
                SwimmingDiscipline.BUTTERFLY,
                saved.getDiscipline()
        );
        assertEquals(6025, saved.getTime());
        assertEquals("DM 2026", saved.getCompetitionName());
        assertEquals(2, saved.getPlacement());
    }

    @Test
    void duplicateMemberIdsShouldBeSkipped()
            throws IOException {

        Files.writeString(
                memberFile,
                "1;Peter;2000-05-10;ACTIVE;false;\n"
                        + "1;Anna;2001-03-15;ACTIVE;false;\n"
        );

        List<Member> loaded = fileHandler.loadData();

        assertEquals(1, loaded.size());
        assertEquals("Peter", loaded.get(0).getName());
    }

    @Test
    void invalidMemberLinesShouldNotDestroyValidMembers()
            throws IOException {

        Files.writeString(
                memberFile,
                "1;Peter;2000-05-10;ACTIVE;false;\n"
                        + "ugyldig tekst\n"
                        + "2;Anna;2001-03-15;ACTIVE;false;\n"
        );

        List<Member> loaded = fileHandler.loadData();

        assertEquals(2, loaded.size());
        assertEquals("Peter", loaded.get(0).getName());
        assertEquals("Anna", loaded.get(1).getName());
    }

    @Test
    void invalidPaymentLinesShouldBeSkipped()
            throws IOException {

        Files.writeString(
                paymentFile,
                "1;500.0;2026-11-20\n"
                        + "1;-200.0;2026-11-20\n"
                        + "1;300.0;2026-11-21\n"
        );

        List<Payment> loaded =
                fileHandler.loadPayments();

        assertEquals(2, loaded.size());
        assertEquals(500, loaded.get(0).getAmount());
        assertEquals(300, loaded.get(1).getAmount());
    }

    @Test
    void invalidTrainingResultsShouldBeSkipped()
            throws IOException {

        Files.writeString(
                trainingFile,
                "1;FREESTYLE;2026-11-20;4532\n"
                        + "1;FREESTYLE;2026-11-20;-100\n"
                        + "2;BACKSTROKE;2026-11-21;5000\n"
        );

        List<TrainingResult> loaded =
                fileHandler.loadTrainingResults();

        assertEquals(2, loaded.size());
        assertEquals(4532, loaded.get(0).getTime());
        assertEquals(5000, loaded.get(1).getTime());
    }

    @Test
    void missingFilesShouldReturnEmptyLists()
            throws IOException {

        assertTrue(fileHandler.loadData().isEmpty());

        assertTrue(
                fileHandler.loadPayments().isEmpty()
        );

        assertTrue(
                fileHandler.loadTrainingResults().isEmpty()
        );

        assertTrue(
                fileHandler.loadCompetitionResults().isEmpty()
        );
    }

    @Test
    void allFourFilesShouldPreserveData()
            throws IOException {

        Member member = createCompetitiveMember(
                1, "Peter"
        );

        Payment payment = new Payment(
                1,
                1000,
                LocalDate.of(2026, 11, 20)
        );

        TrainingResult training = new TrainingResult(
                SwimmingDiscipline.FREESTYLE,
                LocalDate.of(2026, 11, 20),
                4532,
                1
        );

        TrainingResult competition = new TrainingResult(
                SwimmingDiscipline.FREESTYLE,
                LocalDate.of(2026, 11, 21),
                4400,
                1,
                "Klubmesterskab",
                1
        );

        fileHandler.saveData(List.of(member));
        fileHandler.savePayments(List.of(payment));
        fileHandler.saveTrainingResults(List.of(training));
        fileHandler.saveCompetitionResults(
                List.of(competition)
        );

        FileHandler newFileHandler = new FileHandler(
                memberFile.toString(),
                paymentFile.toString(),
                trainingFile.toString(),
                competitionFile.toString()
        );

        assertEquals(
                1,
                newFileHandler.loadData().size()
        );

        assertEquals(
                1,
                newFileHandler.loadPayments().size()
        );

        assertEquals(
                1,
                newFileHandler.loadTrainingResults().size()
        );

        assertEquals(
                1,
                newFileHandler.loadCompetitionResults().size()
        );

        assertEquals(
                "Klubmesterskab",
                newFileHandler.loadCompetitionResults()
                        .get(0).getCompetitionName()
        );
    }

    private Member createCompetitiveMember(
            int id,
            String name) {

        Member member = new Member(
                id,
                name,
                LocalDate.of(2008, 5, 10),
                MembershipType.ACTIVE,
                true
        );

        member.addDiscipline(
                SwimmingDiscipline.FREESTYLE
        );

        return member;
    }
}
