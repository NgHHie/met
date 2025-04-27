package ptit.dblab.app.dto.response;

import ptit.dblab.shared.common.BaseResponse;
import ptit.dblab.app.enumerate.AnswerStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SubmitHistoryResponse extends BaseResponse {
    private LocalDateTime timeSubmit;

    private double timeout;

    @Enumerated(EnumType.STRING)
    private AnswerStatus status;

    private UserHisResponse user;

    private int testPass;

    private int totalTest;

    private QuestionHisResponse question;

    private TypeDatabaseResponse database;
}
