package ptit.dblab.app.dto.response;

import ptit.dblab.app.enumerate.AnswerStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubmitHistoryDetailResponse {
    private LocalDateTime timeSubmit;

    private double timeout;

    @Enumerated(EnumType.STRING)
    private AnswerStatus status;

    private UserHisResponse user;

    private int testPass;

    private int totalTest;
    private String querySub;
    private float point;

    private QuestionHisResponse question;

    private TypeDatabaseResponse database;

    private String evaluate;
}
