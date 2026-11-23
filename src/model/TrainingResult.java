package model;

import java.time.LocalDate;

public class TrainingResult {
    private SwimmingDiscipline discipline;
    private LocalDate date;
    private int time;

    public TrainingResult(SwimmingDiscipline discipline, LocalDate date, int time) {
        this.discipline = discipline;
        this.date = date;
        this.time = time;
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
}