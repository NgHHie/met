package ptit.dblab.app.dto.response;

import ptit.dblab.app.dto.request.CpSubmitRequest;
import ptit.dblab.app.enumerate.AnswerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class CpSubmitResponse {
    private int status;
    private String message;
    private CpSubmitRequest request;
    private String typeQuery;
    private double timeExec;
    private AnswerStatus statusSubmit;
    private int testPass;
    private int totalTest;
    private LocalDateTime timeResponse;
}
