
package ui;

import controller.Controller;
import model.Member;
import model.MembershipType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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
            System.out.println("0. Afslut");
            System.out.print("Vælg: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> createMember();
                    case "2" -> showMembers();
                    case "3" -> showExpectedRevenue();
                    case "4" -> registerPayment();
                    case "5" -> showArrears();
                    case "6" -> showTeam(controller.getJuniorMembers(),
                            "JUNIORHOLD");
                    case "7" -> showTeam(controller.getSeniorMembers(),
                            "SENIORHOLD");
                    case "0" -> running = false;
                    default -> System.out.println("Ugyldigt valg.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Fejl: " + e.getMessage());
            }
        }

        System.out.println("Programmet afsluttes.");
    }

    private void createMember() {
        System.out.print("Navn: ");
        String name = scanner.nextLine().trim();

        LocalDate birthDate = readDate("Fødselsdato (dd-MM-yyyy): ");

        boolean active = readYesNo("Aktivt medlem? (ja/nej): ");

        MembershipType type = active
                ? MembershipType.ACTIVE
                : MembershipType.PASSIVE;

        boolean competitive = false;

        if (active) {
            competitive = readYesNo(
                    "Konkurrencesvømmer? (ja/nej): ");
        }

        Member member = controller.createMember(
                name, birthDate, type, competitive);

        System.out.println(
                "Medlem oprettet! Medlemsnummer: "
                        + member.getMemberId());
    }

    private void showMembers() {
        List<Member> members = controller.getMembers();

        if (members.isEmpty()) {
            System.out.println("Ingen medlemmer registreret.");
            return;
        }

        System.out.println("\n=== MEDLEMSOVERSIGT ===");

        for (Member member : members) {
            int age = member.getAge(LocalDate.now());

            String category = age < 18 ? "Junior" : "Senior";

            String activity;

            if (member.getMembershipType() == MembershipType.PASSIVE) {
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
                            + " | Aktivitetsform: " + activity);
        }
    }

    private void showExpectedRevenue() {
        double revenue = controller.getExpectedRevenue();

        System.out.println("\n=== FORVENTET KONTINGENTINDTÆGT ===");
        System.out.printf("Samlet forventet indtægt: %.2f kr.%n",
                revenue);
    }

    private void registerPayment() {
        System.out.println("\n=== REGISTRER BETALING ===");

        int memberId = readInt("Medlemsnummer: ");
        double amount = readDouble("Beløb: ");
        LocalDate paymentDate = readDate("Betalingsdato (dd-MM-yyyy): ");

        controller.registerPayment(memberId, amount, paymentDate);

        System.out.println("Betaling registreret!");
    }

    private void showArrears() {
        List<Member> members = controller.getMembersInArrears();

        System.out.println("\n=== RESTANCELISTE ===");

        if (members.isEmpty()) {
            System.out.println("Ingen medlemmer i restance.");
            return;
        }

        for (Member member : members) {
            System.out.println(
                    "Medlemsnummer: " + member.getMemberId()
                            + " | Navn: " + member.getName());
        }
    }

    private void showTeam(List<Member> members, String title) {
        System.out.println("\n=== " + title + " ===");

        if (members.isEmpty()) {
            System.out.println("Ingen medlemmer på holdet.");
            return;
        }

        for (Member member : members) {
            System.out.println(
                    "Medlemsnummer: " + member.getMemberId()
                            + " | Navn: " + member.getName()
                            + " | Alder: "
                            + member.getAge(LocalDate.now()));
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ugyldigt tal. Prøv igen.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().replace(',', '.');

            try {
                double amount = Double.parseDouble(input);

                if (!Double.isFinite(amount) || amount <= 0) {
                    System.out.println("Beløbet skal være positivt.");
                    continue;
                }

                return amount;
            } catch (NumberFormatException e) {
                System.out.println("Ugyldigt beløb. Prøv igen.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return LocalDate.parse(
                        scanner.nextLine().trim(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println(
                        "Ugyldig dato. Brug formatet dd-MM-yyyy.");
            }
        }
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("ja")) {
                return true;
            }

            if (answer.equalsIgnoreCase("nej")) {
                return false;
            }

            System.out.println("Skriv ja eller nej.");
        }
    }
}
