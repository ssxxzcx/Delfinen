
package model;

public class MemberTrainingResult {

    private Member member;
    private TrainingResult result;
    private ResultType resultType;

    public MemberTrainingResult(Member member, TrainingResult result, ResultType resultType) {

        if (member == null || result == null || resultType == null) {
            throw new IllegalArgumentException("Member, result and result type cannot be null");
        }

        this.member = member;
        this.result = result;
        this.resultType = resultType;
    }

    public Member getMember() {
        return member;
    }

    public TrainingResult getResult() {
        return result;
    }

    public ResultType getResultType() {
        return resultType;
    }
}
