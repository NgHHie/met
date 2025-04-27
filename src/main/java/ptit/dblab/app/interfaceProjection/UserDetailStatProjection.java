package ptit.dblab.app.interfaceProjection;

public interface UserDetailStatProjection {
    String getUserCode();

    String getFullName();

    int getNumQuestionDone();

    int getTotalSubmit();

    int getTotalCorrectQuestions();

    int getTotalSubmitAc();

    int getTotalSubmitWa();

    int getTotalSubmitTle();

    int getTotalSubmitCe();
}
