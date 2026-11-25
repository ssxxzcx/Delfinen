
import model.FileHandler;
import model.Member;
import model.MembershipType;
import model.Payment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    @TempDir
    Path tempDir;

    private FileHandler createFileHandler() {
        return new FileHandler(
                tempDir.resolve("members-test.txt").toString(),
                tempDir.resolve("payments-test.txt").toString()
        );
    }

    @Test
    void saveAndLoadMembers() throws IOException {
        FileHandler fileHandler = createFileHandler();

        Member member = new Member(
                1,
                "Test Medlem",
                LocalDate.of(2005, 5, 10),
                MembershipType.ACTIVE,
                true
        );

        fileHandler.saveData(List.of(member));

        List<Member> loaded = fileHandler.loadData();

        assertEquals(1, loaded.size());
        assertEquals(1, loaded.get(0).getMemberId());
        assertEquals("Test Medlem", loaded.get(0).getName());
        assertEquals(LocalDate.of(2005, 5, 10),
                loaded.get(0).getBirthDate());
        assertEquals(MembershipType.ACTIVE,
                loaded.get(0).getMembershipType());
        assertTrue(loaded.get(0).isCompetitive());
    }

    @Test
    void saveAndLoadPayments() throws IOException {
        FileHandler fileHandler = createFileHandler();

        Payment payment = new Payment(
                1,
                1600,
                LocalDate.of(2026, 11, 25)
        );

        fileHandler.savePayments(List.of(payment));

        List<Payment> loaded = fileHandler.loadPayments();

        assertEquals(1, loaded.size());
        assertEquals(1, loaded.get(0).getMemberId());
        assertEquals(1600, loaded.get(0).getAmount());
        assertEquals(LocalDate.of(2026, 11, 25),
                loaded.get(0).getPaymentDate());
    }

    @Test
    void missingFilesReturnEmptyLists() throws IOException {
        FileHandler fileHandler = createFileHandler();

        assertTrue(fileHandler.loadData().isEmpty());
        assertTrue(fileHandler.loadPayments().isEmpty());
    }
}
