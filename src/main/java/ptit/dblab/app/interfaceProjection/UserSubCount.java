package ptit.dblab.app.interfaceProjection;

import java.time.LocalDate;

public interface UserSubCount {
    LocalDate getSubmitDate();
    String getUserId();
    int getTotalSubmit();
}
