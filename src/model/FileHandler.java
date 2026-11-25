
package model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private final String memberFile;
    private final String paymentFile;

    // Bruges af det almindelige program
    public FileHandler() {
        this("members.txt", "payments.txt");
    }

    // Bruges af JUnit-tests med midlertidige filer
    public FileHandler(String memberFile, String paymentFile) {
        this.memberFile = memberFile;
        this.paymentFile = paymentFile;
    }

    // Gem medlemmer i tekstfil
    public void saveData(List<Member> members) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(memberFile))) {

            for (Member member : members) {
                writer.println(
                        member.getMemberId() + ";" +
                                member.getName() + ";" +
                                member.getBirthDate() + ";" +
                                member.getMembershipType() + ";" +
                                member.isCompetitive()
                );
            }
        }
    }

    // Indlæs medlemmer fra tekstfil
    public List<Member> loadData() throws IOException {

        List<Member> members = new ArrayList<>();
        File file = new File(memberFile);

        if (!file.exists()) {
            return members;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(";", -1);

                if (data.length != 5) {
                    System.out.println("Springer ugyldig medlemslinje over: " + line);
                    continue;
                }

                try {
                    int memberId = Integer.parseInt(data[0]);
                    String name = data[1];
                    LocalDate birthDate = LocalDate.parse(data[2]);
                    MembershipType type = MembershipType.valueOf(data[3]);

                    if (memberId <= 0 || name.isBlank()
                            || name.contains(";")
                            || name.contains("\n")
                            || name.contains("\r")) {
                        throw new IllegalArgumentException("Ugyldigt medlem");
                    }

                    if (!data[4].equals("true")
                            && !data[4].equals("false")) {
                        throw new IllegalArgumentException("Ugyldig boolean");
                    }

                    boolean competitive = Boolean.parseBoolean(data[4]);

                    if (type == MembershipType.PASSIVE && competitive) {
                        throw new IllegalArgumentException(
                                "Passive medlemmer kan ikke være konkurrencesvømmere");
                    }

                    members.add(new Member(
                            memberId,
                            name,
                            birthDate,
                            type,
                            competitive
                    ));

                } catch (IllegalArgumentException e) {
                    System.out.println("Springer ugyldigt medlem over: " + line);
                }
            }
        }

        return members;
    }

    // Gem betalinger i tekstfil
    public void savePayments(List<Payment> payments) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(paymentFile))) {

            for (Payment payment : payments) {
                writer.println(
                        payment.getMemberId() + ";" +
                                payment.getAmount() + ";" +
                                payment.getPaymentDate()
                );
            }
        }
    }

    // Indlæs betalinger fra tekstfil
    public List<Payment> loadPayments() throws IOException {

        List<Payment> payments = new ArrayList<>();
        File file = new File(paymentFile);

        if (!file.exists()) {
            return payments;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(";", -1);

                if (data.length != 3) {
                    System.out.println("Springer ugyldig betalingslinje over: " + line);
                    continue;
                }

                try {
                    int memberId = Integer.parseInt(data[0]);
                    double amount = Double.parseDouble(data[1]);
                    LocalDate paymentDate = LocalDate.parse(data[2]);

                    if (memberId <= 0 || !Double.isFinite(amount) || amount <= 0) {
                        throw new IllegalArgumentException("Ugyldig betaling");
                    }

                    payments.add(new Payment(
                            memberId,
                            amount,
                            paymentDate
                    ));

                } catch (IllegalArgumentException e) {
                    System.out.println("Springer ugyldig betaling over: " + line);
                }
            }
        }

        return payments;
    }
}
