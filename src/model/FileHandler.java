
package model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileHandler {

    private final String memberFile;
    private final String paymentFile;
    private final String trainingFile;
    private final String competitionFile;

    public FileHandler() {
        this(
                "members.txt",
                "payments.txt",
                "training-results.txt",
                "competition-results.txt"
        );
    }

    // Bevarer constructoren fra FileHandlerTest
    public FileHandler(String memberFile, String paymentFile) {
        this(
                memberFile,
                paymentFile,
                "training-results.txt",
                "competition-results.txt"
        );
    }

    // Fire separate filer
    public FileHandler(
            String memberFile,
            String paymentFile,
            String trainingFile,
            String competitionFile) {

        this.memberFile = memberFile;
        this.paymentFile = paymentFile;
        this.trainingFile = trainingFile;
        this.competitionFile = competitionFile;
    }

    // GEM MEDLEMMER OG DERES DISCIPLINER

    public void saveData(List<Member> members) throws IOException {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(memberFile))) {

            for (Member member : members) {

                StringBuilder disciplines = new StringBuilder();

                for (SwimmingDiscipline discipline
                        : member.getDisciplines()) {

                    if (disciplines.length() > 0) {
                        disciplines.append(",");
                    }

                    disciplines.append(discipline.name());
                }

                writer.println(
                        member.getMemberId() + ";" +
                                member.getName() + ";" +
                                member.getBirthDate() + ";" +
                                member.getMembershipType() + ";" +
                                member.isCompetitive() + ";" +
                                disciplines
                );
            }

            if (writer.checkError()) {
                throw new IOException("Kunne ikke gemme medlemmer");
            }
        }
    }

    // INDLÆS MEDLEMMER OG DISCIPLINER

    public List<Member> loadData() throws IOException {

        List<Member> members = new ArrayList<>();
        Set<Integer> usedIds = new HashSet<>();

        File file = new File(memberFile);

        if (!file.exists()) {
            return members;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                try {

                    String[] data = line.split(";", -1);

                    // 5 felter er gammelt format,
                    // 6 felter er nyt format.
                    if (data.length != 5 && data.length != 6) {
                        throw new IllegalArgumentException(
                                "Forkert antal felter");
                    }

                    int id = Integer.parseInt(data[0]);

                    String name = data[1];

                    LocalDate birthDate =
                            LocalDate.parse(data[2]);

                    MembershipType type =
                            MembershipType.valueOf(data[3]);

                    if (!data[4].equals("true")
                            && !data[4].equals("false")) {
                        throw new IllegalArgumentException(
                                "Ugyldig medlemsstatus");
                    }

                    boolean competitive =
                            Boolean.parseBoolean(data[4]);

                    if (id <= 0 || usedIds.contains(id)) {
                        throw new IllegalArgumentException(
                                "Ugyldigt eller dubleret medlemsnummer");
                    }

                    if (name.isBlank()
                            || name.contains("\n")
                            || name.contains("\r")) {
                        throw new IllegalArgumentException(
                                "Ugyldigt navn");
                    }

                    if (birthDate.isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException(
                                "Fødselsdato er i fremtiden");
                    }

                    if (type == MembershipType.PASSIVE
                            && competitive) {
                        throw new IllegalArgumentException(
                                "Passivt konkurrencemedlem");
                    }

                    Member member = new Member(
                            id,
                            name,
                            birthDate,
                            type,
                            competitive
                    );

                    if (data.length == 6 && !data[5].isBlank()) {

                        for (String value : data[5].split(",")) {

                            SwimmingDiscipline discipline =
                                    SwimmingDiscipline.valueOf(
                                            value.trim());

                            member.addDiscipline(discipline);
                        }
                    }

                    members.add(member);
                    usedIds.add(id);

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Advarsel: Springer medlemslinje "
                                    + lineNumber + " over: "
                                    + e.getMessage());
                }
            }
        }

        return members;
    }

    // GEM BETALINGER

    public void savePayments(List<Payment> payments)
            throws IOException {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(paymentFile))) {

            for (Payment payment : payments) {

                writer.println(
                        payment.getMemberId() + ";" +
                                payment.getAmount() + ";" +
                                payment.getPaymentDate()
                );
            }

            if (writer.checkError()) {
                throw new IOException("Kunne ikke gemme betalinger");
            }
        }
    }

    // INDLÆS BETALINGER

    public List<Payment> loadPayments() throws IOException {

        List<Payment> payments = new ArrayList<>();

        File file = new File(paymentFile);

        if (!file.exists()) {
            return payments;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                try {

                    String[] data = line.split(";", -1);

                    if (data.length != 3) {
                        throw new IllegalArgumentException(
                                "Forkert antal felter");
                    }

                    int memberId = Integer.parseInt(data[0]);

                    double amount = Double.parseDouble(data[1]);

                    LocalDate date = LocalDate.parse(data[2]);

                    if (memberId <= 0
                            || !Double.isFinite(amount)
                            || amount <= 0) {
                        throw new IllegalArgumentException(
                                "Ugyldig betaling");
                    }

                    payments.add(new Payment(
                            memberId,
                            amount,
                            date
                    ));

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Advarsel: Springer betalingslinje "
                                    + lineNumber + " over: "
                                    + e.getMessage());
                }
            }
        }

        return payments;
    }

    // GEM TRÆNINGSRESULTATER

    public void saveTrainingResults(
            List<TrainingResult> results) throws IOException {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(trainingFile))) {

            for (TrainingResult result : results) {

                writer.println(
                        result.getMemberId() + ";" +
                                result.getDiscipline() + ";" +
                                result.getDate() + ";" +
                                result.getTime()
                );
            }

            if (writer.checkError()) {
                throw new IOException(
                        "Kunne ikke gemme træningsresultater");
            }
        }
    }

    // GEM STÆVNERESULTATER

    public void saveCompetitionResults(
            List<TrainingResult> results) throws IOException {

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter(competitionFile))) {

            for (TrainingResult result : results) {

                if (result.getCompetitionName() == null
                        || result.getPlacement() <= 0) {
                    throw new IOException(
                            "Stævneresultat mangler navn eller placering");
                }

                writer.println(
                        result.getMemberId() + ";" +
                                result.getDiscipline() + ";" +
                                result.getDate() + ";" +
                                result.getTime() + ";" +
                                result.getCompetitionName() + ";" +
                                result.getPlacement()
                );
            }

            if (writer.checkError()) {
                throw new IOException(
                        "Kunne ikke gemme stævneresultater");
            }
        }
    }

    // INDLÆS TRÆNINGSRESULTATER

    public List<TrainingResult> loadTrainingResults()
            throws IOException {

        return loadResults(trainingFile, false);
    }

    // INDLÆS STÆVNERESULTATER

    public List<TrainingResult> loadCompetitionResults()
            throws IOException {

        return loadResults(competitionFile, true);
    }

    // FÆLLES INDLÆSNING AF RESULTATER

    private List<TrainingResult> loadResults(
            String filename,
            boolean competition) throws IOException {

        List<TrainingResult> results = new ArrayList<>();

        File file = new File(filename);

        if (!file.exists()) {
            return results;
        }

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(file))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {

                lineNumber++;

                try {

                    String[] data = line.split(";", -1);

                    int expectedFields = competition ? 6 : 4;

                    if (data.length != expectedFields) {
                        throw new IllegalArgumentException(
                                "Forkert antal felter");
                    }

                    int memberId = Integer.parseInt(data[0]);

                    SwimmingDiscipline discipline =
                            SwimmingDiscipline.valueOf(data[1]);

                    LocalDate date = LocalDate.parse(data[2]);

                    int time = Integer.parseInt(data[3]);

                    if (memberId <= 0
                            || time <= 0
                            || date.isAfter(LocalDate.now())) {
                        throw new IllegalArgumentException(
                                "Ugyldige resultatoplysninger");
                    }

                    TrainingResult result;

                    if (competition) {

                        String competitionName = data[4];

                        int placement =
                                Integer.parseInt(data[5]);

                        result = new TrainingResult(
                                discipline,
                                date,
                                time,
                                memberId,
                                competitionName,
                                placement
                        );

                    } else {

                        result = new TrainingResult(
                                discipline,
                                date,
                                time,
                                memberId
                        );
                    }

                    results.add(result);

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Advarsel: Springer resultatlinje "
                                    + lineNumber + " i "
                                    + filename + " over: "
                                    + e.getMessage());
                }
            }
        }

        return results;
    }
}
