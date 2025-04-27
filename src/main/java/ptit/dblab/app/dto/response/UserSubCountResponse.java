package ptit.dblab.app.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserSubCountResponse {
    private String userId;
    private String userCode;
    private String fullName;
    List<SubmitCountResponse> submits;

    public int getTodaySubmissionCount() {
        LocalDate today = LocalDate.now();
        return submits != null
                ? submits.stream()
                .filter(submit -> submit.getSubmitDate().equals(today))
                .map(SubmitCountResponse::getTotalSub)
                .findFirst()
                .orElse(0)  // Default to 0 if there's no submission for today
                : 0;
    }
}
