package model;

import java.util.ArrayList;

public class ResultService {
    private ArrayList<TrainingResult> trainingResults = new ArrayList<>();
    private ArrayList<TrainingResult> competitionResults = new ArrayList<>();

    public void registerTrainingResult(TrainingResult result) {
        trainingResults.add(result);
    }

    public void registerCompetitionResult(TrainingResult result) {
        competitionResults.add(result);
    }
    public ArrayList<TrainingResult> getTrainingResults() {
        return trainingResults;
    }
    public ArrayList<TrainingResult> getCompetitionResults() {
        return competitionResults;
    }
        public ArrayList<TrainingResult> getAllResults() {
            ArrayList<TrainingResult> allResults = new ArrayList<>();
            allResults.addAll(trainingResults);
            allResults.addAll(competitionResults);
            return allResults;
        }
}
