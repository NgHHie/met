package ptit.dblab.app.interfaceProjection;

public interface UserTrackerProjection {
    String getUserId();
    String getUserCode();
    String getFirstName();
    String getLastName();
    String getActionType();
    int getActionCount();
}
