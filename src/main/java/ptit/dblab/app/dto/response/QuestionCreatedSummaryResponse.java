package ptit.dblab.app.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCreatedSummaryResponse {
    private int totalQuestionRequest;
    private int totalQuestionImported;
    private List<QuestionSummaryItem> success;
    private List<QuestionSummaryItem> failed;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionSummaryItem {
        private String id;
        private String title;
    }
}

