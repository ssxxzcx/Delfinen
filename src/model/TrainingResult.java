
package model;

import java.time.LocalDate;

public class TrainingResult {

    private SwimmingDiscipline discipline;
    private LocalDate date;
    private int time;
    private int memberId;

    // Salmas version med 4 argumenter
    public TrainingResult(SwimmingDiscipline discipline,
                          LocalDate date,
                          int time,
                          int memberId) {
        this.discipline = discipline;
        this.date = date;
        this.time = time;
        this.memberId = memberId;
    }

    // Sofies version med 3 argumenter
    public TrainingResult(SwimmingDiscipline discipline,
                          LocalDate date,
                          int time) {
        this(discipline, date, time, -1);
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
}
