
package model;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private final String fileName = "members.txt";

    public void saveData(List<Member> members) throws IOException {

        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

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

    public List<Member> loadData() throws IOException {

        List<Member> members = new ArrayList<>();
        File file = new File(fileName);

        if (!file.exists()) {
            return members;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] data = line.split(";", -1);

                if (data.length != 5) {
                    throw new IOException("Ugyldige medlemsdata: " + line);
                }

                try {
                    int memberId = Integer.parseInt(data[0]);
                    String name = data[1];
                    LocalDate birthDate = LocalDate.parse(data[2]);
                    MembershipType membershipType =
                            MembershipType.valueOf(data[3]);
                    boolean competitive;

                    if (data[4].equals("true")) {
                        competitive = true;
                    } else if (data[4].equals("false")) {
                        competitive = false;
                    } else {
                        throw new IllegalArgumentException("Ugyldig boolean");
                    }

                    Member member = new Member(
                            memberId,
                            name,
                            birthDate,
                            membershipType,
                            competitive
                    );

                    members.add(member);

                } catch (IllegalArgumentException exception) {
                    throw new IOException("Kunne ikke læse medlem: " + line, exception);
                }
            }
        }

        return members;
    }
}
