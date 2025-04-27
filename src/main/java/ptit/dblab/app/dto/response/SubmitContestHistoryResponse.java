package ptit.dblab.app.dto.response;

import ptit.dblab.app.enumerate.AnswerStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SubmitContestHistoryResponse {
    private String id;
    private AnswerStatus status;

    private LocalDateTime timeSubmit;

    private float timeExec;

    private UserBaseResponse user;

    private String querySub;

    private int testPass;

    private int totalTest;

    private float point;

    private String ip;

    private QuestionContestBaseResponse questionContest;

    private TypeDatabaseResponse database;
}
