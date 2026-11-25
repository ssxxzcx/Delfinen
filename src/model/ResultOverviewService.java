package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResultOverviewService {

    // Hent alle resultater for et medlem
    public List<MemberTrainingResult> getResultsForMember(
            int memberId,
            List<MemberTrainingResult> results) {

        List<MemberTrainingResult> memberResults = new ArrayList<>();

        for (MemberTrainingResult entry : results) {
            if (entry.getMember().getMemberId() == memberId) {
                memberResults.add(entry);
            }
        }

        return memberResults;
    }

    // Hent konkurrenceresultater for et medlem
    public List<MemberTrainingResult> getCompetitionResultsForMember(
            int memberId,
            List<MemberTrainingResult> results) {

        List<MemberTrainingResult> competitionResults = new ArrayList<>();

        for (MemberTrainingResult entry : results) {
            if (entry.getMember().getMemberId() == memberId
                    && entry.getResultType() == ResultType.COMPETITION) {
                competitionResults.add(entry);
            }
        }

        return competitionResults;
    }

    // Find bedste træningstid i en bestemt disciplin
    public MemberTrainingResult getBestTrainingResult(
            int memberId,
            SwimmingDiscipline discipline,
            List<MemberTrainingResult> results) {

        MemberTrainingResult bestResult = null;

        for (MemberTrainingResult entry : results) {

            if (entry.getMember().getMemberId() == memberId
                    && entry.getResult().getDiscipline() == discipline
                    && entry.getResultType() == ResultType.TRAINING) {

                if (bestResult == null
                        || entry.getResult().getTime()
                        < bestResult.getResult().getTime()) {
                    bestResult = entry;
                }
            }
        }

        return bestResult;
    }

    // Find bedste træningstid i hver disciplin
    public List<MemberTrainingResult> getBestTrainingResultsForMember(
            int memberId,
            List<MemberTrainingResult> results) {

        List<MemberTrainingResult> bestResults = new ArrayList<>();

        for (SwimmingDiscipline discipline : SwimmingDiscipline.values()) {

            MemberTrainingResult bestResult = getBestTrainingResult(
                    memberId, discipline, results
            );

            if (bestResult != null) {
                bestResults.add(bestResult);
            }
        }

        return bestResults;
    }

    // Top 5 for juniorer eller seniorer
    public List<MemberTrainingResult> getTopFive(
            SwimmingDiscipline discipline,
            List<MemberTrainingResult> results,
            boolean juniors) {

        List<MemberTrainingResult> bestResults = new ArrayList<>();

        for (MemberTrainingResult entry : results) {

            Member member = entry.getMember();
            TrainingResult result = entry.getResult();

            boolean isJunior = member.getAge(LocalDate.now()) < 18;

            // Kun konkurrencesvømmere med gyldig disciplin
            // og registrerede træningstider
            if (!member.isCompetitive()
                    || !member.isActiveInDiscipline(discipline)
                    || entry.getResultType() != ResultType.TRAINING
                    || result.getDiscipline() != discipline
                    || isJunior != juniors) {
                continue;
            }

            boolean memberAlreadyAdded = false;

            // Hver svømmer må kun optræde én gang
            for (int i = 0; i < bestResults.size(); i++) {

                MemberTrainingResult existing = bestResults.get(i);

                if (existing.getMember().getMemberId()
                        == member.getMemberId()) {

                    memberAlreadyAdded = true;

                    // Behold svømmerens hurtigste træningstid
                    if (result.getTime()
                            < existing.getResult().getTime()) {
                        bestResults.set(i, entry);
                    }

                    break;
                }
            }

            if (!memberAlreadyAdded) {
                bestResults.add(entry);
            }
        }

        // Sorter efter hurtigste tid
        bestResults.sort(
                Comparator.comparingInt(
                        entry -> entry.getResult().getTime()
                )
        );

        // Returner højst fem svømmere
        if (bestResults.size() > 5) {
            return new ArrayList<>(bestResults.subList(0, 5));
        }

        return bestResults;
    }
}

