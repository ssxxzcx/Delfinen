package model;

import java.time.LocalDate;

public class TrainingResult {
    private SwimmingDiscipline discipline;
    private LocalDate date;
    private int time;
    private int memberId;

    public TrainingResult(SwimmingDiscipline discipline, LocalDate date, int time,int memberId) {
        this.discipline = discipline;
        this.date = date;
        this.time = time;
        this.memberId = memberId;
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