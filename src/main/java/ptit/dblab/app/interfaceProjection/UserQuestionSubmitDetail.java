package ptit.dblab.app.interfaceProjection;

public interface UserQuestionSubmitDetail {

    String getUserId();

    String getQuestionContestId();

    String getQuestionCode();

    int getNumTries();

    int getMaxTestPass();

    int getTotalTest();

    String getFinalStatus();
}
