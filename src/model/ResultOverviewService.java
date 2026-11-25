
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ResultOverviewService {

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

    public List<MemberTrainingResult> getTopFive(
            SwimmingDiscipline discipline,
            List<MemberTrainingResult> results,
            boolean juniors) {

        List<MemberTrainingResult> bestResults = new ArrayList<>();

        for (MemberTrainingResult entry : results) {

            Member member = entry.getMember();
            TrainingResult result = entry.getResult();

            boolean isJunior = member.getAge(LocalDate.now()) < 18;

            if (result.getDiscipline() == discipline && isJunior == juniors) {
                boolean memberAlreadyAdded = false;

                for (int i = 0; i < bestResults.size(); i++) {
                    if (bestResults.get(i).getMember().getMemberId()
                            == member.getMemberId()) {

                        memberAlreadyAdded = true;

                        if (result.getTime()
                                < bestResults.get(i).getResult().getTime()) {
                            bestResults.set(i, entry);
                        }
                    }
                }

                if (!memberAlreadyAdded) {
                    bestResults.add(entry);
                }
            }
        }

        bestResults.sort(
                Comparator.comparingInt(entry -> entry.getResult().getTime())
        );

        if (bestResults.size() > 5) {
            return new ArrayList<>(bestResults.subList(0, 5));
        }

        return bestResults;
    }
}
