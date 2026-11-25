
package model;

import java.time.LocalDate;

public class TrainingResult {

    private SwimmingDiscipline discipline;
    private LocalDate date;
    private int time;
    private int memberId;

    private String competitionName;
    private int placement;

    // Beholder Sofies constructor
    public TrainingResult(
            SwimmingDiscipline discipline,
            LocalDate date,
            int time) {

        this(discipline, date, time, -1);
    }

    // Træningsresultat med medlemsnummer
    public TrainingResult(
            SwimmingDiscipline discipline,
            LocalDate date,
            int time,
            int memberId) {

        this.discipline = discipline;
        this.date = date;
        this.time = time;
        this.memberId = memberId;
        this.competitionName = null;
        this.placement = 0;
    }

    // Stævneresultat
    public TrainingResult(
            SwimmingDiscipline discipline,
            LocalDate date,
            int time,
            int memberId,
            String competitionName,
            int placement) {

        this(discipline, date, time, memberId);

        if (competitionName == null
                || competitionName.isBlank()
                || competitionName.contains(";")
                || competitionName.contains("\n")
                || competitionName.contains("\r")) {
            throw new IllegalArgumentException(
                    "Ugyldigt stævnenavn");
        }

        if (placement <= 0) {
            throw new IllegalArgumentException(
                    "Placering skal være mindst 1");
        }

        this.competitionName = competitionName.trim();
        this.placement = placement;
    }

    public SwimmingDiscipline getDiscipline() {
        return discipline;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getTime() {
        return time;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public int getPlacement() {
        return placement;
    }
}
