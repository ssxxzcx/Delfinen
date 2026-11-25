
package model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Member {

    private int memberId;
    private String name;
    private LocalDate birthDate;
    private MembershipType membershipType;
    private boolean competitive;

    // Svømmediscipliner medlemmet er aktiv i
    private List<SwimmingDiscipline> disciplines;

    // Eksisterende constructor
    public Member(int memberId,
                  String name,
                  LocalDate birthDate,
                  MembershipType membershipType,
                  boolean competitive) {

        this.memberId = memberId;
        this.name = name;
        this.birthDate = birthDate;
        this.membershipType = membershipType;
        this.competitive = competitive;
        this.disciplines = new ArrayList<>();
    }

    // Tilføj en svømmedisciplin
    public void addDiscipline(SwimmingDiscipline discipline) {

        if (!competitive) {
            throw new IllegalArgumentException(
                    "Kun konkurrencesvømmere kan have discipliner"
            );
        }

        if (discipline == null) {
            throw new IllegalArgumentException(
                    "Disciplin må ikke være null"
            );
        }

        if (!disciplines.contains(discipline)) {
            disciplines.add(discipline);
        }
    }

    // Kontrollér om medlemmet er aktivt i disciplinen
    public boolean isActiveInDiscipline(
            SwimmingDiscipline discipline) {

        return disciplines.contains(discipline);
    }

    // Hent medlemmets discipliner
    public List<SwimmingDiscipline> getDisciplines() {
        return new ArrayList<>(disciplines);
    }

    public int getAge(LocalDate date) {
        return Period.between(birthDate, date).getYears();
    }

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public boolean isCompetitive() {
        return competitive;
    }
}

