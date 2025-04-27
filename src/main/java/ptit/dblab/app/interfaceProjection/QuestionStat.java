package ptit.dblab.app.interfaceProjection;

public interface QuestionStat {
    String getId();
    String getQuestionCode();
    String getTitle();
    int getTotalUser();
    int getTotalSubmitCorrect();
    int getTotalSubmitAc();
    int getTotalSubmissions();
}
