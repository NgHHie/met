package ptit.dblab.app.interfaceProjection;

public interface UserSubmitStat {
    String getId();
    String getUserCode();

    String getFullName();

    int getNumQuestionDone();

    int getTotalSubmitAc();

    float getTotalPoints();

    int getTotalSubmit();
}
