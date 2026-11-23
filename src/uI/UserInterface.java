
package ui;

import controller.Controller;
import model.Member;
import model.MembershipType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
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
            System.out.println("0. Afslut");
            System.out.print("Vælg: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> createMember();
                case "2" -> showMembers();
                case "0" -> running = false;
                default -> System.out.println("Ugyldigt valg.");
            }
        }
    }

    private void createMember() {
        try {
            System.out.print("Navn: ");
            String name = scanner.nextLine();

            System.out.print("Fødselsdato (dd-MM-yyyy): ");
            LocalDate birthDate =
                    LocalDate.parse(scanner.nextLine(), dateFormatter);

            System.out.print("Aktivt medlem? (ja/nej): ");
            boolean active =
                    scanner.nextLine().equalsIgnoreCase("ja");

            MembershipType type = active
                    ? MembershipType.ACTIVE
                    : MembershipType.PASSIVE;

            boolean competitive = false;

            if (active) {
                System.out.print("Konkurrencesvømmer? (ja/nej): ");
                competitive =
                        scanner.nextLine().equalsIgnoreCase("ja");
            }

            Member member = controller.createMember(
                    name, birthDate, type, competitive
            );

            System.out.println(
                    "Medlem oprettet! Medlemsnummer: "
                            + member.getMemberId()
            );

        } catch (DateTimeParseException e) {
            System.out.println(
                    "Ugyldig dato. Brug formatet dd-MM-yyyy."
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Fejl: " + e.getMessage());
        }
    }

    private void showMembers() {
        if (controller.getMembers().isEmpty()) {
            System.out.println("Ingen medlemmer registreret.");
            return;
        }

        System.out.println("\n=== MEDLEMSOVERSIGT ===");

        for (Member member : controller.getMembers()) {

            int age = member.getAge(LocalDate.now());

            String category = age < 18
                    ? "Junior"
                    : "Senior";

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
                            + " | Aktivitetsform: " + activity
            );
        }
    }
}
