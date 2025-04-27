package ptit.dblab.app.interfaceProjection;

public interface TopUserProjection {
    String getId();
    String getUserCode();

    String getFullName();

    String getAvatar();

    int getNumQuestionDone();

    float getTotalPoints();
}
