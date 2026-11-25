
package ui;

import controller.Controller;
import model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private final Controller controller;
    private final Scanner scanner = new Scanner(System.in);

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd-MM-uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    public UserInterface(Controller controller) {
        this.controller = controller;
    }

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println("\n=== SVØMMEKLUBBEN DELFINEN ===");
            System.out.println("1. Opret medlem");
            System.out.println("2. Vis medlemmer");
            System.out.println("3. Vis forventet kontingentindtægt");
            System.out.println("4. Registrer betaling");
            System.out.println("5. Vis restanceliste");
            System.out.println("6. Vis juniorhold");
            System.out.println("7. Vis seniorhold");
            System.out.println("8. Registrer træningsresultat");
            System.out.println("9. Registrer konkurrenceresultat");
            System.out.println("10. Vis svømmeresultater");
            System.out.println("11. Vis top 5");
            System.out.println("12. Vis medlemskontingent");
            System.out.println("0. Afslut");
            System.out.print("Vælg: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1" -> createMember();
                    case "2" -> showMembers();
                    case "3" -> showExpectedRevenue();
                    case "4" -> registerPayment();
                    case "5" -> showArrears();

                    case "6" -> showTeam(
                            controller.getJuniorMembers(),
                            "JUNIORHOLD"
                    );

                    case "7" -> showTeam(
                            controller.getSeniorMembers(),
                            "SENIORHOLD"
                    );

                    case "8" -> registerTrainingResult();
                    case "9" -> registerCompetitionResult();
                    case "10" -> showResultsForMember();
                    case "11" -> showTopFive();
                    case "12" -> showMemberFees();

                    case "0" -> running = false;

                    default -> System.out.println("Ugyldigt valg.");
                }

            } catch (IllegalArgumentException |
                     IllegalStateException e) {

                System.out.println("Fejl: " + e.getMessage());
            }
        }

        System.out.println("Programmet afsluttes.");
    }

    // OPRET MEDLEM

    private void createMember() {

        System.out.println("\n=== OPRET MEDLEM ===");

        System.out.print("Navn: ");
        String name = scanner.nextLine().trim();

        LocalDate birthDate =
                readDate("Fødselsdato (dd-MM-yyyy): ");

        boolean active =
                readYesNo("Aktivt medlem? (ja/nej): ");

        MembershipType type = active
                ? MembershipType.ACTIVE
                : MembershipType.PASSIVE;

        boolean competitive = false;

        if (active) {
            competitive = readYesNo(
                    "Konkurrencesvømmer? (ja/nej): "
            );
        }

        List<SwimmingDiscipline> disciplines =
                new ArrayList<>();

        if (competitive) {

            boolean addMore = true;

            while (addMore) {

                SwimmingDiscipline discipline =
                        readDiscipline();

                if (!disciplines.contains(discipline)) {

                    disciplines.add(discipline);

                    System.out.println(
                            "Tilføjet: " + discipline
                    );

                } else {

                    System.out.println(
                            "Disciplinen er allerede valgt."
                    );
                }

                if (disciplines.size()
                        == SwimmingDiscipline.values().length) {
                    break;
                }

                addMore = readYesNo(
                        "Tilføj flere discipliner? (ja/nej): "
                );
            }
        }

        Member member = controller.createMember(
                name,
                birthDate,
                type,
                competitive,
                disciplines
        );

        System.out.println(
                "Medlem oprettet! Medlemsnummer: "
                        + member.getMemberId()
        );
    }

    // VIS MEDLEMMER

    private void showMembers() {

        List<Member> members = controller.getMembers();

        System.out.println("\n=== MEDLEMSOVERSIGT ===");

        if (members.isEmpty()) {
            System.out.println("Ingen medlemmer registreret.");
            return;
        }

        for (Member member : members) {

            int age = member.getAge(LocalDate.now());

            String category = age < 18
                    ? "Junior"
                    : "Senior";

            String activity;

            if (member.getMembershipType()
                    == MembershipType.PASSIVE) {

                activity = "Passiv";

            } else if (member.isCompetitive()) {

                activity = "Konkurrencesvømmer";

            } else {

                activity = "Motionist";
            }

            System.out.println(
                    "Medlemsnummer: " + member.getMemberId()
                            + " | Navn: " + member.getName()
                            + " | Alder: " + age
                            + " | " + category
                            + " | Aktivitetsform: " + activity
            );

            if (member.isCompetitive()) {

                System.out.println(
                        "Discipliner: " + member.getDisciplines()
                );
            }
        }
    }

    // F5: VIS ÅRLIGT KONTINGENT FOR HVERT MEDLEM

    private void showMemberFees() {

        System.out.println("\n=== MEDLEMSKONTINGENTER ===");

        List<Member> members = controller.getMembers();

        if (members.isEmpty()) {
            System.out.println("Ingen medlemmer registreret.");
            return;
        }

        double total = 0;

        for (Member member : members) {

            double annualFee =
                    controller.getAnnualFee(
                            member.getMemberId()
                    );

            System.out.printf(
                    "Medlemsnummer: %d | Navn: %s | Årligt kontingent: %.2f kr.%n",
                    member.getMemberId(),
                    member.getName(),
                    annualFee
            );

            total += annualFee;
        }

        System.out.printf(
                "Samlet årligt kontingent: %.2f kr.%n",
                total
        );
    }

    // F6: FORVENTET KONTINGENTINDTÆGT

    private void showExpectedRevenue() {

        System.out.println(
                "\n=== FORVENTET KONTINGENTINDTÆGT ==="
        );

        System.out.printf(
                "Samlet forventet indtægt: %.2f kr.%n",
                controller.getExpectedRevenue()
        );
    }

    // F7: REGISTRER BETALING

    private void registerPayment() {

        System.out.println("\n=== REGISTRER BETALING ===");

        int memberId = readInt("Medlemsnummer: ");

        double amount = readDouble("Beløb: ");

        LocalDate date =
                readDate("Betalingsdato (dd-MM-yyyy): ");

        controller.registerPayment(
                memberId,
                amount,
                date
        );

        System.out.println("Betaling registreret!");
    }

    // F8: RESTANCELISTE

    private void showArrears() {

        System.out.println("\n=== RESTANCELISTE ===");

        List<Member> members =
                controller.getMembersInArrears();

        if (members.isEmpty()) {

            System.out.println(
                    "Ingen medlemmer i restance."
            );

            return;
        }

        double totalDue = 0;

        for (Member member : members) {

            double amountDue =
                    controller.getAmountDue(
                            member.getMemberId()
                    );

            System.out.printf(
                    "Medlemsnummer: %d | Navn: %s | Skylder: %.2f kr.%n",
                    member.getMemberId(),
                    member.getName(),
                    amountDue
            );

            totalDue += amountDue;
        }

        System.out.printf(
                "Samlet restance: %.2f kr.%n",
                totalDue
        );
    }

    // F9: JUNIOR- OG SENIORHOLD

    private void showTeam(
            List<Member> members,
            String title) {

        System.out.println("\n=== " + title + " ===");

        if (members.isEmpty()) {

            System.out.println(
                    "Ingen medlemmer på holdet."
            );

            return;
        }

        for (Member member : members) {

            System.out.println(
                    "Medlemsnummer: " + member.getMemberId()
                            + " | Navn: " + member.getName()
                            + " | Alder: "
                            + member.getAge(LocalDate.now())
                            + " | Discipliner: "
                            + member.getDisciplines()
            );
        }
    }

    // F10: BEDSTE TRÆNINGSTID

    private void registerTrainingResult() {

        System.out.println(
                "\n=== REGISTRER TRÆNINGSRESULTAT ==="
        );

        int memberId = readInt("Medlemsnummer: ");

        Member member = controller.getMember(memberId);

        System.out.println(
                "Svømmer: " + member.getName()
        );

        SwimmingDiscipline discipline =
                readDiscipline();

        LocalDate date =
                readDate("Træningsdato (dd-MM-yyyy): ");

        int time = readSwimTime();

        boolean improved =
                controller.registerTrainingResult(
                        memberId,
                        discipline,
                        date,
                        time
                );

        if (improved) {

            System.out.println(
                    "Ny bedste træningstid registreret!"
            );

        } else {

            System.out.println(
                    "Tiden var ikke bedre. "
                            + "Den gamle bedste tid er bevaret."
            );
        }
    }

    // F11: STÆVNERESULTAT

    private void registerCompetitionResult() {

        System.out.println(
                "\n=== REGISTRER KONKURRENCERESULTAT ==="
        );

        int memberId = readInt("Medlemsnummer: ");

        Member member = controller.getMember(memberId);

        System.out.println(
                "Svømmer: " + member.getName()
        );

        String competitionName =
                readCompetitionName();

        SwimmingDiscipline discipline =
                readDiscipline();

        LocalDate date =
                readDate("Stævnedato (dd-MM-yyyy): ");

        int placement = readPositiveInt(
                "Placering (1 = førsteplads): "
        );

        int time = readSwimTime();

        controller.registerCompetitionResult(
                memberId,
                discipline,
                date,
                time,
                competitionName,
                placement
        );

        System.out.println(
                "Konkurrenceresultat registreret!"
        );
    }

    private String readCompetitionName() {

        while (true) {

            System.out.print("Stævnenavn: ");

            String name = scanner.nextLine().trim();

            if (!name.isBlank()
                    && !name.contains(";")
                    && !name.contains("\n")
                    && !name.contains("\r")) {

                return name;
            }

            System.out.println(
                    "Ugyldigt stævnenavn. Prøv igen."
            );
        }
    }

    // F12: RESULTATER FOR ET MEDLEM

    private void showResultsForMember() {

        System.out.println(
                "\n=== SVØMMERRESULTATER ==="
        );

        int memberId = readInt("Medlemsnummer: ");

        Member member = controller.getMember(memberId);

        System.out.println(
                "\nResultater for: " + member.getName()
        );

        List<MemberTrainingResult> training =
                controller.getBestTrainingResultsForMember(
                        memberId
                );

        System.out.println(
                "\n--- BEDSTE TRÆNINGSTIDER ---"
        );

        if (training.isEmpty()) {

            System.out.println(
                    "Ingen træningsresultater."
            );

        } else {

            for (MemberTrainingResult entry : training) {
                printResult(entry);
            }
        }

        List<MemberTrainingResult> competitions =
                controller.getCompetitionResultsForMember(
                        memberId
                );

        System.out.println(
                "\n--- STÆVNERESULTATER ---"
        );

        if (competitions.isEmpty()) {

            System.out.println(
                    "Ingen stævneresultater."
            );

        } else {

            for (MemberTrainingResult entry : competitions) {
                printResult(entry);
            }
        }
    }

    private void printResult(MemberTrainingResult entry) {

        TrainingResult result = entry.getResult();

        String output =
                "Disciplin: " + result.getDiscipline()
                        + " | Tid: "
                        + formatSwimTime(result.getTime())
                        + " | Dato: "
                        + result.getDate()
                        .format(dateFormatter);

        if (entry.getResultType()
                == ResultType.COMPETITION) {

            output += " | Stævne: "
                    + result.getCompetitionName()
                    + " | Placering: "
                    + result.getPlacement();

        } else {

            output += " | Type: Træning";
        }

        System.out.println(output);
    }

    // F13: TOP 5

    private void showTopFive() {

        System.out.println("\n=== TOP 5 SVØMMERE ===");

        SwimmingDiscipline discipline =
                readDiscipline();

        List<MemberTrainingResult> juniors =
                controller.getTopFive(
                        discipline,
                        true
                );

        List<MemberTrainingResult> seniors =
                controller.getTopFive(
                        discipline,
                        false
                );

        printTopFive(juniors, "JUNIOR TOP 5");
        printTopFive(seniors, "SENIOR TOP 5");
    }

    private void printTopFive(
            List<MemberTrainingResult> results,
            String title) {

        System.out.println("\n--- " + title + " ---");

        if (results.isEmpty()) {

            System.out.println(
                    "Ingen registrerede resultater."
            );

            return;
        }

        int position = 1;

        for (MemberTrainingResult entry : results) {

            TrainingResult result = entry.getResult();

            System.out.println(
                    position + ". "
                            + entry.getMember().getName()
                            + " | Tid: "
                            + formatSwimTime(result.getTime())
                            + " | Dato: "
                            + result.getDate()
                            .format(dateFormatter)
            );

            position++;
        }
    }

    // VÆLG DISCIPLIN

    private SwimmingDiscipline readDiscipline() {

        while (true) {

            System.out.println("\nVælg disciplin:");
            System.out.println("1. Crawl");
            System.out.println("2. Brystsvømning");
            System.out.println("3. Butterfly");
            System.out.println("4. Rygcrawl");

            int choice = readInt("Vælg 1-4: ");

            switch (choice) {

                case 1:
                    return SwimmingDiscipline.FREESTYLE;

                case 2:
                    return SwimmingDiscipline.BREASTSTROKE;

                case 3:
                    return SwimmingDiscipline.BUTTERFLY;

                case 4:
                    return SwimmingDiscipline.BACKSTROKE;

                default:
                    System.out.println(
                            "Ugyldigt valg. Prøv igen."
                    );
            }
        }
    }

    // INDTAST SVØMMETID

    private int readSwimTime() {

        while (true) {

            System.out.print(
                    "Svømmetid (fx 45.32 eller 1:05.32): "
            );

            String input = scanner.nextLine().trim();

            try {

                String[] parts = input.split(":", -1);

                long minutes = 0;
                String secondsPart;

                if (parts.length == 2) {

                    if (!parts[0].matches("\\d+")) {
                        throw new IllegalArgumentException();
                    }

                    minutes = Long.parseLong(parts[0]);
                    secondsPart = parts[1];

                } else if (parts.length == 1) {

                    secondsPart = parts[0];

                } else {

                    throw new IllegalArgumentException();
                }

                if (!secondsPart.matches(
                        "\\d{1,2}\\.\\d{2}")) {

                    throw new IllegalArgumentException();
                }

                String[] numbers =
                        secondsPart.split("\\.");

                int seconds =
                        Integer.parseInt(numbers[0]);

                int hundredths =
                        Integer.parseInt(numbers[1]);

                if (seconds >= 60) {
                    throw new IllegalArgumentException();
                }

                if (minutes > Integer.MAX_VALUE / 6000L) {
                    throw new IllegalArgumentException();
                }

                long total =
                        minutes * 6000L
                                + seconds * 100L
                                + hundredths;

                if (total <= 0
                        || total > Integer.MAX_VALUE) {

                    throw new IllegalArgumentException();
                }

                return (int) total;

            } catch (IllegalArgumentException e) {

                System.out.println(
                        "Ugyldig tid. Brug fx 45.32 eller 1:05.32."
                );
            }
        }
    }

    // FORMATÉR SVØMMETID

    private String formatSwimTime(int hundredths) {

        int minutes = hundredths / 6000;
        int seconds = (hundredths / 100) % 60;
        int rest = hundredths % 100;

        return String.format(
                "%d:%02d.%02d",
                minutes,
                seconds,
                rest
        );
    }

    // LÆS HELTAL

    private int readInt(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Ugyldigt tal. Prøv igen."
                );
            }
        }
    }

    private int readPositiveInt(String prompt) {

        while (true) {

            int value = readInt(prompt);

            if (value > 0) {
                return value;
            }

            System.out.println(
                    "Tallet skal være større end 0."
            );
        }
    }

    // LÆS BELØB

    private double readDouble(String prompt) {

        while (true) {

            System.out.print(prompt);

            String input = scanner.nextLine()
                    .trim()
                    .replace(',', '.');

            try {

                double amount =
                        Double.parseDouble(input);

                if (!Double.isFinite(amount)
                        || amount <= 0) {

                    System.out.println(
                            "Beløbet skal være positivt."
                    );

                    continue;
                }

                return amount;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Ugyldigt beløb. Prøv igen."
                );
            }
        }
    }

    // LÆS DATO

    private LocalDate readDate(String prompt) {

        while (true) {

            System.out.print(prompt);

            try {

                return LocalDate.parse(
                        scanner.nextLine().trim(),
                        dateFormatter
                );

            } catch (DateTimeParseException e) {

                System.out.println(
                        "Ugyldig dato. Brug dd-MM-yyyy."
                );
            }
        }
    }

    // JA / NEJ

    private boolean readYesNo(String prompt) {

        while (true) {

            System.out.print(prompt);

            String answer =
                    scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("ja")) {
                return true;
            }

            if (answer.equalsIgnoreCase("nej")) {
                return false;
            }

            System.out.println(
                    "Skriv ja eller nej."
            );
        }
    }
}
