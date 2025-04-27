package ptit.dblab.app.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import ptit.dblab.app.enumerate.AnswerStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SubmitResponse {
	private int status;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Object result;
	private String typeQuery;
	private double timeExec;
	private String tableEffect;
	private AnswerStatus statusSubmit;
	private int testPass;
	private int totalTest;
	private LocalDateTime timeSubmit;
	private String submitId;
	private String queryInput;
	private String description;

	public SubmitResponse(int status, Object result) {
		super();
		this.status = status;
		this.result = result;
	}
	
	
}
