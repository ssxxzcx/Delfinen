
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResultService {

    private final List<TrainingResult> trainingResults =
            new ArrayList<>();

    private final List<TrainingResult> competitionResults =
            new ArrayList<>();

    // Registrer træningsresultat
    public void registerTrainingResult(TrainingResult result) {
        validateResult(result);
        saveBestTrainingResult(result);
    }

    // Registrer træningsresultat med medlemskontrol
    public void registerTrainingResult(
            Member member,
            TrainingResult result) {

        validateMemberAndResult(member, result);
        saveBestTrainingResult(result);
    }

    // Returnerer true, hvis svømmeren fik ny bedste tid
    public boolean registerBestTrainingResult(
            Member member,
            TrainingResult result) {

        validateMemberAndResult(member, result);
        return saveBestTrainingResult(result);
    }

    // Behold kun den bedste træningstid pr. medlem og disciplin
    private boolean saveBestTrainingResult(TrainingResult result) {

        for (int i = 0; i < trainingResults.size(); i++) {

            TrainingResult existing = trainingResults.get(i);

            if (existing.getMemberId() == result.getMemberId()
                    && existing.getDiscipline()
                    == result.getDiscipline()) {

                if (result.getTime() < existing.getTime()) {
                    trainingResults.set(i, result);
                    return true;
                }

                return false;
            }
        }

        trainingResults.add(result);
        return true;
    }

    // Registrer konkurrenceresultat
    public void registerCompetitionResult(TrainingResult result) {
        validateResult(result);
        competitionResults.add(result);
    }

    // Registrer konkurrenceresultat med medlemskontrol
    public void registerCompetitionResult(
            Member member,
            TrainingResult result) {

        validateMemberAndResult(member, result);
        competitionResults.add(result);
    }

    private void validateResult(TrainingResult result) {

        if (result == null) {
            throw new IllegalArgumentException(
                    "Resultatet må ikke være null");
        }

        if (result.getMemberId() <= 0) {
            throw new IllegalArgumentException(
                    "Ugyldigt medlemsnummer");
        }

        if (result.getDiscipline() == null) {
            throw new IllegalArgumentException(
                    "Disciplin mangler");
        }

        if (result.getDate() == null
                || result.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Ugyldig resultatdato");
        }

        if (result.getTime() <= 0) {
            throw new IllegalArgumentException(
                    "Svømmetiden skal være positiv");
        }
    }

    private void validateMemberAndResult(
            Member member,
            TrainingResult result) {

        validateResult(result);

        if (member == null) {
            throw new IllegalArgumentException(
                    "Medlemmet findes ikke");
        }

        if (member.getMemberId() != result.getMemberId()) {
            throw new IllegalArgumentException(
                    "Resultatet tilhører et andet medlem");
        }

        if (!member.isCompetitive()) {
            throw new IllegalArgumentException(
                    "Kun konkurrencesvømmere kan registrere resultater");
        }

        if (!member.isActiveInDiscipline(
                result.getDiscipline())) {
            throw new IllegalArgumentException(
                    "Svømmeren er ikke aktiv i disciplinen");
        }
    }

    public ArrayList<TrainingResult> getTrainingResults() {
        return new ArrayList<>(trainingResults);
    }

    public ArrayList<TrainingResult> getCompetitionResults() {
        return new ArrayList<>(competitionResults);
    }

    public ArrayList<TrainingResult> getAllResults() {

        ArrayList<TrainingResult> allResults = new ArrayList<>();

        allResults.addAll(trainingResults);
        allResults.addAll(competitionResults);

        return allResults;
    }

    // Konverter til Sofies resultatoversigt
    public List<MemberTrainingResult> getOverviewResults(
            List<Member> members) {

        if (members == null) {
            throw new IllegalArgumentException(
                    "Medlemslisten må ikke være null");
        }

        List<MemberTrainingResult> overview =
                new ArrayList<>();

        for (TrainingResult result : trainingResults) {

            Member member = findMember(
                    result.getMemberId(), members);

            if (member != null) {
                overview.add(new MemberTrainingResult(
                        member, result, ResultType.TRAINING));
            }
        }

        for (TrainingResult result : competitionResults) {

            Member member = findMember(
                    result.getMemberId(), members);

            if (member != null) {
                overview.add(new MemberTrainingResult(
                        member, result, ResultType.COMPETITION));
            }
        }

        return overview;
    }

    private Member findMember(
            int memberId,
            List<Member> members) {

        for (Member member : members) {

            if (member.getMemberId() == memberId) {
                return member;
            }
        }

        return null;
    }
}
