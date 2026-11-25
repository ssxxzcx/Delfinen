
package controller;

import model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private final MemberService memberService =
            new MemberService();

    private final FileHandler fileHandler =
            new FileHandler();

    private final RevenueService revenueService =
            new RevenueService();

    private final PaymentService paymentService =
            new PaymentService();

    private final TeamService teamService =
            new TeamService();

    private final ResultService resultService =
            new ResultService();

    private final ResultOverviewService resultOverviewService =
            new ResultOverviewService();

    public Controller() {

        try {

            memberService.loadMembers(
                    fileHandler.loadData());

            for (Payment payment : fileHandler.loadPayments()) {

                if (findMember(payment.getMemberId()) != null) {
                    paymentService.registerPayment(
                            payment.getMemberId(),
                            payment.getAmount(),
                            payment.getPaymentDate()
                    );
                }
            }

            for (TrainingResult result :
                    fileHandler.loadTrainingResults()) {

                try {

                    Member member =
                            getMember(result.getMemberId());

                    resultService.registerTrainingResult(
                            member, result);

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Springer træningsresultat over: "
                                    + e.getMessage());
                }
            }

            for (TrainingResult result :
                    fileHandler.loadCompetitionResults()) {

                try {

                    Member member =
                            getMember(result.getMemberId());

                    resultService.registerCompetitionResult(
                            member, result);

                } catch (IllegalArgumentException e) {

                    System.out.println(
                            "Springer stævneresultat over: "
                                    + e.getMessage());
                }
            }

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Kunne ikke indlæse data", e);
        }
    }

    // OPRET MEDLEM - EKSISTERENDE METODE

    public Member createMember(
            String name,
            LocalDate birthDate,
            MembershipType type,
            boolean competitive) {

        validateName(name);

        Member member = memberService.createMember(
                name, birthDate, type, competitive);

        saveMembers();

        return member;
    }

    // OPRET MEDLEM MED DISCIPLINER

    public Member createMember(
            String name,
            LocalDate birthDate,
            MembershipType type,
            boolean competitive,
            List<SwimmingDiscipline> disciplines) {

        validateName(name);

        Member member = memberService.createMember(
                name,
                birthDate,
                type,
                competitive,
                disciplines
        );

        saveMembers();

        return member;
    }

    private void validateName(String name) {

        if (name == null
                || name.contains(";")
                || name.contains("\n")
                || name.contains("\r")) {

            throw new IllegalArgumentException(
                    "Ugyldigt navn");
        }
    }

    public List<Member> getMembers() {
        return memberService.getMembers();
    }

    public Member getMember(int memberId) {

        Member member = findMember(memberId);

        if (member == null) {
            throw new IllegalArgumentException(
                    "Medlemsnummeret findes ikke");
        }

        return member;
    }

    // F5: ÅRLIGT KONTINGENT FOR ET MEDLEM

    public double getAnnualFee(int memberId) {

        Member member = getMember(memberId);

        MembershipFee fee = new MembershipFee(member);

        return fee.calculateFee(LocalDate.now());
    }

    // F6: FORVENTET INDTÆGT

    public double getExpectedRevenue() {

        return revenueService.calculateExpectedRevenue(
                memberService.getMembers());
    }

    // F7: REGISTRER BETALING

    public void registerPayment(
            int memberId,
            double amount,
            LocalDate paymentDate) {

        getMember(memberId);

        paymentService.registerPayment(
                memberId,
                amount,
                paymentDate
        );

        savePayments();
    }

    // F8: RESTANCER

    public List<Member> getMembersInArrears() {

        return paymentService.getMembersInArrears(
                memberService.getMembers());
    }

    public double getAmountDue(int memberId) {

        Member member = getMember(memberId);

        return paymentService.getAmountDue(member);
    }

    // F9: JUNIOR- OG SENIORHOLD

    public List<Member> getJuniorMembers() {

        return teamService.getJuniorMembers(
                memberService.getMembers());
    }

    public List<Member> getSeniorMembers() {

        return teamService.getSeniorMembers(
                memberService.getMembers());
    }

    // F10: REGISTRER BEDSTE TRÆNINGSTID

    public boolean registerTrainingResult(
            int memberId,
            SwimmingDiscipline discipline,
            LocalDate date,
            int time) {

        Member member = getMember(memberId);

        TrainingResult result = new TrainingResult(
                discipline,
                date,
                time,
                memberId
        );

        boolean improved =
                resultService.registerBestTrainingResult(
                        member, result);

        if (improved) {
            saveTrainingResults();
        }

        return improved;
    }

    // F11: REGISTRER STÆVNERESULTAT

    public void registerCompetitionResult(
            int memberId,
            SwimmingDiscipline discipline,
            LocalDate date,
            int time,
            String competitionName,
            int placement) {

        Member member = getMember(memberId);

        TrainingResult result = new TrainingResult(
                discipline,
                date,
                time,
                memberId,
                competitionName,
                placement
        );

        resultService.registerCompetitionResult(
                member, result);

        saveCompetitionResults();
    }

    // F12: ALLE RESULTATER

    public List<MemberTrainingResult> getAllResults() {

        return resultService.getOverviewResults(
                memberService.getMembers());
    }

    public List<MemberTrainingResult> getResultsForMember(
            int memberId) {

        getMember(memberId);

        return resultOverviewService.getResultsForMember(
                memberId,
                getAllResults()
        );
    }

    public List<MemberTrainingResult>
    getCompetitionResultsForMember(int memberId) {

        getMember(memberId);

        return resultOverviewService
                .getCompetitionResultsForMember(
                        memberId,
                        getAllResults()
                );
    }

    public List<MemberTrainingResult>
    getBestTrainingResultsForMember(int memberId) {

        getMember(memberId);

        return resultOverviewService
                .getBestTrainingResultsForMember(
                        memberId,
                        getAllResults()
                );
    }

    // F13: TOP 5

    public List<MemberTrainingResult> getTopFive(
            SwimmingDiscipline discipline,
            boolean juniors) {

        if (discipline == null) {
            throw new IllegalArgumentException(
                    "Svømmedisciplin mangler");
        }

        return resultOverviewService.getTopFive(
                discipline,
                getAllResults(),
                juniors
        );
    }

    private Member findMember(int memberId) {

        for (Member member : memberService.getMembers()) {

            if (member.getMemberId() == memberId) {
                return member;
            }
        }

        return null;
    }

    // F14: GEM MEDLEMMER

    private void saveMembers() {

        try {

            fileHandler.saveData(
                    memberService.getMembers());

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Kunne ikke gemme medlemmer", e);
        }
    }

    // GEM BETALINGER

    private void savePayments() {

        try {

            fileHandler.savePayments(
                    paymentService.getPayments());

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Kunne ikke gemme betalinger", e);
        }
    }

    // GEM TRÆNINGSRESULTATER

    private void saveTrainingResults() {

        try {

            fileHandler.saveTrainingResults(
                    resultService.getTrainingResults());

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Kunne ikke gemme træningsresultater", e);
        }
    }

    // GEM STÆVNERESULTATER

    private void saveCompetitionResults() {

        try {

            fileHandler.saveCompetitionResults(
                    resultService.getCompetitionResults());

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Kunne ikke gemme stævneresultater", e);
        }
    }
}
